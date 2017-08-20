"""
Functions for interacting wit a game

A game is expected to be an array of frames, each frame being an iterable of rolls
"""
from .frame import (
    frame_to_string,
    is_complete,
    is_spare,
    is_strike,
    new_frame,
)

from .iters import flatten

def new_game():
    return []

def roll(game, pins):
    if not game or is_complete(game[-1]):
        game.append(new_frame())
    game[-1].append(pins)

def frame_number(game):
    if not game:
        return 1
    if is_complete(game[-1]):
        return len(game) + 1
    return len(game)

def frame_score(game, frame_number):
    if len(game) < frame_number:
        return 0
    frame = game[frame_number - 1]
    if is_strike(frame):
        # The next two rolls may take up to two frames (subsequent strikes)
        next_two_frames = game[frame_number:frame_number + 2]
        next_two_rolls = flatten(next_two_frames)[:2]
        return 10 + sum(next_two_rolls)
    if is_spare(frame):
        # Use list here to simplify sums.  When there isn't a next frame sum just returns 0.
        next_frame = game[frame_number:frame_number + 1]
        next_roll = flatten(next_frame)[:1]
        return 10 + sum(next_roll)
    else:
        return sum(frame)

def game_score(game):
    return sum(frame_score(game, ix + 1) for ix in range(len(game)))

def print_game(game):
    frame_strings = map(frame_to_string, game)
    print("Score: {}".format(game_score(game)))
    print("|{}|".format('|'.join(frame_strings)))

