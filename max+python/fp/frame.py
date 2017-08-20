"""
Functions for interacting with frame data

Frames are represented as an iterable of rolls. Some example frames:


[] - no rolls
[1] - one roll
[3, 4] - two rolls

A frame is expected to have no more than two rolls
"""
def new_frame():
    return []

def is_strike(frame):
    return frame == [10]

def is_spare(frame):
    return len(frame) == 2 and sum(frame) == 10

def is_complete(frame):
    return len(frame) == 2 or is_strike(frame)

def frame_to_string(frame):
    if is_strike(frame):
        return 'X'
    if is_spare(frame):
        return '/'
    return ','.join(map(str, frame))

