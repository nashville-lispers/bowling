def flatten(iterable):
    """Recursively flatten an iterable

    e.g. [[1,2,[3,4,5] 6]] into [1, 2, 3, 4, 5, 6]
    """
    result = []
    for i in iterable:
        if isinstance(i, list):
            result.extend(flatten(i))
        else:
            result.append(i)
    return result
