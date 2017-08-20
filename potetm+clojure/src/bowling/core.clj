(ns bowling.core
  (:require
    [clojure.spec.alpha :as s]
    [clojure.spec.gen.alpha :as sgen]
    [clojure.spec.test.alpha :as stest]
    [expound.alpha :as ex]))

(defn shrunk-for-check-err
  ([]
   (shrunk-for-check-err *1))
  ([errs]
   (map (fn [{{{sm :smallest} :shrunk} :clojure.spec.test.check/ret}]
          sm)
        errs)))

(defmacro vcat
  "Takes key+pred pairs, e.g.

  (vcat :e even? :o odd?)

  Returns a regex op that matches vectors, returning a map containing
  the keys of each pred and the corresponding value. The attached
  generator produces vectors."
  [& key-pred-forms]
  `(s/with-gen (s/and vector? (s/cat ~@key-pred-forms))
               #(sgen/fmap vec (s/gen (s/cat ~@key-pred-forms)))))

(s/def ::roll (s/int-in 0 11))

(s/def ::strike-frame
  #{[10]})

(s/def ::spare-frame
  (into #{}
        (for [i (range 0 10)
              j (range 0 11)
              :when (= 10 (+ i j))]
          [i j])))

(s/def ::open-frame
  (into #{}
        (for [i (range 0 10)
              j (range 0 10)
              :when (< (+ i j) 10)]
          [i j])))

(s/def ::regular-frame
  (s/or ::open ::open-frame
        ::spare ::spare-frame
        ::strike ::strike-frame))

(s/def ::unfinished-frame
  (s/or :reg (s/tuple (s/int-in 0 10))))

(s/def ::final-frame
  (s/or ::open ::open-frame
        ::spare (s/with-gen
                  (s/and (s/tuple ::roll
                                  ::roll
                                  ::roll)
                         (fn [[r1 r2]]
                           (= (+ r1 r2)
                              10)))
                  #(sgen/fmap
                     (fn [[f l]]
                       (into f [l]))
                     (sgen/tuple (s/gen ::spare-frame)
                                 (sgen/choose 0 10))))
        ::strike (s/tuple #{10}
                          ::roll
                          ::roll)))

(s/def ::unfinished-final-frame
  (s/or :strike (s/or :1 (s/tuple #{10})
                      :2 (s/tuple #{10} ::roll))
        :spare ::spare-frame
        :reg (s/tuple (s/int-in 0 10))))

(s/def ::frame
  (s/or :reg ::regular-frame
        :fin ::final-frame))

(s/def ::game
  ;; Must split into two specs because ::unfinished-frame can only occur
  ;; from 0-9 and ::unfinished-final-frame can only occur at 10, but they have
  ;; identical :reg structures.
  (s/or :0-9 (s/with-gen
               (s/and (vcat :reg (s/& (s/* ::regular-frame)
                                      #(<= 0 (count %) 9))
                            :unfinished (s/? ::unfinished-frame))
                      (fn [{:keys [reg unfinished]}]
                        (or (empty? unfinished)
                            (<= (count reg) 8))))
               #(sgen/fmap (fn [[fms lf]]
                             (if lf
                               (into fms [lf])
                               fms))
                           (sgen/tuple (sgen/vector (s/gen ::regular-frame)
                                                    0
                                                    8)
                                       (sgen/one-of [(s/gen ::regular-frame)
                                                     (s/gen ::unfinished-frame)
                                                     (sgen/return nil)]))))
        :10 (s/with-gen
              (vcat :reg (s/& (s/* ::regular-frame)
                              #(= (count %) 9))
                    :last (s/alt :unfinished ::unfinished-final-frame
                                 :final ::final-frame))
              #(sgen/fmap (fn [[fms lf]]
                            (into fms [lf]))
                          (sgen/tuple (sgen/vector (s/gen ::regular-frame)
                                                   9)
                                      (sgen/one-of [(s/gen ::final-frame)
                                                    (s/gen ::unfinished-final-frame)]))))))

(s/def ::finished-game
  (s/with-gen
    (s/and ::game
           (fn [[gt {[lt] :last}]]
             (and (= gt :10)
                  (= lt :final))))
    #(sgen/fmap (fn [[fms lf]]
                  (into fms [lf]))
                (sgen/tuple (sgen/vector (s/gen ::regular-frame)
                                         9)
                            (s/gen ::final-frame)))))

(s/fdef frame-type
  :args (s/cat :frame ::frame)
  :ret #{::strike
         ::spare
         ::open})

(s/fdef score-frame
  :args (s/cat :frames (s/+ ::frame))
  :ret (s/and int?
              #(<= 0 % 30)))

(s/fdef score-game
  :args (s/cat :game ::game)
  :ret (s/and int?
              #(<= 0 % 300)))

(s/fdef add-roll
  :args (s/cat :game ::game
               :roll ::roll)
  :ret ::game)

(defn frame-type [f]
  (let [[_reg-or-fin [ft]] (s/conform ::frame
                                      f)]
    ft))

(defn score-frame [& [frame :as frames]]
  (if-not (s/valid? ::frame frame)
    0
    (let [[r1 r2 r3] (apply concat frames)]
      (case (frame-type frame)
        ::strike (if (and r2 r3)
                   (+ 10 r2 r3)
                   0)
        ::spare (if r3
                  (+ 10 r3)
                  0)
        ::open (+ r1 (or r2 0))))))

(defn score-game [game]
  (apply + (map (partial apply score-frame)
                (partition-all 3
                               1
                               game))))

(defn add-roll [game roll]
  (let [parse-unfinished-frame (fn [g]
                                 (let [[r c] (s/conform ::game g)]
                                   (case r
                                     :0-9
                                     (get-in c [:unfinished 1])
                                     :10
                                     (if (= :strike (get-in c [:last 1 0]))
                                       (get-in c [:last 1 1 1])
                                       (get-in c [:last 1 1]))
                                     nil)))
        add-to-frame           (fn [f r]
                                 (let [nf (conj f r)]
                                   (if-not (or (s/valid? ::frame nf)
                                               (s/valid? ::unfinished-final-frame nf))
                                     (do (println "Bad move bruh.")
                                         f)
                                     nf)))]
    (if (s/valid? ::finished-game game)
      (do (println "Game's over yo!")
          game)
      (let [uf (parse-unfinished-frame game)]
        (if uf
          (conj (vec (butlast game))
                (add-to-frame uf roll))
          (conj game
                [roll]))))))

(defn new-game []
  [])

(comment
  (def g (atom (new-game)))
  (swap! g add-roll 1)
  (swap! g add-roll 10) ;; invalid - no change made
  (swap! g add-roll 9)

  (swap! g add-roll 3)
  (swap! g add-roll 5)

  (swap! g add-roll 4)
  (swap! g add-roll 2)
  (score-game @g)

  (reset! g (new-game)))


;; bugs caught from gen tests:
;;   * mishandle last frame in `add-roll
;;   * incorrect ::frame detection
;;   * incorrect check for valid new frame
;;   * default to 0 for invalid frames in `score-frame
