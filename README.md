## Let's Go Bowling

Model a simplified game of 10-pin bowling using standard rules and fulfilling the specific requirements below.

### How Does Bowling Work, Anyways?

10-pin bowling is a sport where players roll a ball down a lane, in an attempt to knock down the 10 pins (!!!!)
at the end of the lane [0].  A game of bowling is divided into 10 rounds, called "frames".  At the end of each frame,
the pins are reset.  Frames one through nine are composed of two rolls. The tenth frame is composed of up to three rolls:
the bonus roll(s) following a strike or spare in the tenth (sometimes referred to as the eleventh and twelfth frames) are
fill ball(s) used only to calculate the score of the mark rolled in the tenth. If neither a strike nor a spare is
achieved in the tenth frame, no bonus roll is awarded.

Bowling has a unique scoring system which keeps track not only of the current pinfall in a frame, but also strikes and
spares which allow for the value of subsequent pinfall. Effectively, there are three kinds of marks given in a score;
a strike (all ten down in the first ball), a spare (all ten down by the second ball), and an open (one or more missed
pins still standing after the second ball). A strike earns ten points plus the points for the next two balls thrown.
(For example, if a player got a strike then followed with a 7 then 2, their value for the strike frame would be 10+7+2,
or 19.) A spare earns ten points plus the points for the next ball thrown. (Again, if a player gets a spare then follows
it with 7 pins down on the first ball of the next frame, their value for the spare frame would be 10+7, or 17.) A strike
after a spare would earn 20 points. The same score would be obtained if the reverse occurred. Open frames count the
value of the pinfall in that frame only. (Example: if a player knocks down 5 on their first ball and 3 on their second,
the open frame would be worth 8 points.) The maximum score in ten-pin bowling is 300. This consists of getting 12
strikes in a row in one game (one strike each in frames 1–9, all three possible strikes in the tenth frame), and is also
known as a perfect game.
[0]

### Requirements

* Add a roll to the game
* Get the frame of the next roll of the game.
* Access the score of any frame in the game.
* Get the total score for the game

You are not required to model the special handling for the tenth frame.

[0] https://media.giphy.com/media/1mNBTj3g4jRCg/giphy.gif
[1] https://en.wikipedia.org/wiki/Ten-pin_bowling

