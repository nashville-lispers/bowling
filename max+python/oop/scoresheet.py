from .frame import Frame


class ScoreSheet:
    def __init__(self):
        self._frames = []

    def __repr__(self):
        roll_strings = '|'.join(map(str, self._frames))
        return "ScoreSheet(Score: {}, Rolls: |{}|)".format(self.score, roll_strings)

    def add_roll(self, pins_dropped):
        if not self._frames or self._frames[-1].is_complete:
            self._frames.append(Frame())

        self._frames[-1].add_roll(pins_dropped)

    def frame_score(self, frame_number):
        if frame_number > len(self._frames):
            return 0

        frame = self._frames[frame_number - 1]
        if frame.is_strike:
            next_two_frames = self._frames[frame_number:frame_number + 2]
            next_two_rolls = self._frames2rolls(next_two_frames)[:2]
            return frame.pins_count + sum(next_two_rolls)
        if frame.is_spare:
            next_frame = self._frames[frame_number:frame_number + 1]
            next_roll = self._frames2rolls(next_frame)[:1]
            return frame.pins_count + sum(next_roll)
        return frame.pins_count

    def _frames2rolls(self, frames):
        result = []
        for f in frames:
            result.extend(f.rolls)
        return result

    @property
    def frame_number(self):
        if not self._frames:
            return 1

        if self._frames[-1].is_complete:
            return len(self._frames) + 1

        return len(self._frames)

    @property
    def score(self):
        return sum(self.frame_score(ix + 1) for ix in range(len(self._frames)))
