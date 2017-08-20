class Frame:
    def __init__(self):
        self._rolls = []

    def __repr__(self):
        if self.is_strike:
            content = "X"
        elif self.is_spare:
            content = "/"
        else:
            content = '|'.join(map(str, self._rolls))
        return content

    @property
    def rolls(self):
        return list(self._rolls)

    @property
    def is_complete(self):
        return len(self._rolls) == 2 or self.is_strike

    @property
    def is_strike(self):
        return len(self._rolls) == 1 and self._rolls[0] == 10

    @property
    def is_spare(self):
        return len(self._rolls) == 2 and self.pins_count == 10

    @property
    def pins_count(self):
        return sum(self._rolls)

    def add_roll(self, pins):
        if self.is_complete:
            raise Exception("Frame is complete")

        if self.pins_count + pins > 10:
            raise Exception("Can't drop more than 10 pins")

        self._rolls.append(pins)
