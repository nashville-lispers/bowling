"""
Tests for the fp package

This just runs some assertions and outputs some values
"""
from fp.game import (
    frame_number,
    game_score,
    new_game,
    print_game,
    roll,
)

game = new_game()

assert frame_number(game) == 1, frame_number(game)
print(game_score(game))

roll(game, 2)
roll(game, 7)
assert frame_number(game) == 2, frame_number(game)
print(game_score(game))

roll(game, 10)
assert frame_number(game) == 3, frame_number(game)
print(game_score(game))

roll(game, 8)
roll(game, 1)
print(game_score(game))

roll(game, 3)
roll(game, 7)
print(game_score(game))

print_game(game)
