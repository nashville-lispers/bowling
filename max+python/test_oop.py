"""
"Tests" for the `oop` package.

Go through and make some assertions, printing out intermediate
results
"""
from oop.scoresheet import ScoreSheet

sheet = ScoreSheet()
assert sheet.frame_number == 1, sheet.frame_number
print(sheet.score)

# for i in range(11):
#     sheet.add_roll(10)


assert sheet.frame_number == 1, sheet.frame_number
print(sheet.score)

sheet.add_roll(8)
sheet.add_roll(2)
assert sheet.frame_number == 2, sheet.frame_number
print(sheet.score)

sheet.add_roll(10)
assert sheet.frame_number == 3, sheet.frame_number
print(sheet.score)

sheet.add_roll(10)
print(sheet.score)

sheet.add_roll(10)
print(sheet.score)

sheet.add_roll(4)
print(sheet.score)
print(sheet)
