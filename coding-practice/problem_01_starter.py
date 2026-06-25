# Problem 01: Minimum Window Substring
#
# Given two strings s and t, return the shortest substring of s that contains
# every character in t (including duplicates). If no such substring exists,
# return an empty string "".
#
# You may assume the answer is unique when it exists.
#
# Examples:
#   Input:  s = "ADOBECODEBANC", t = "ABC"
#   Output: "BANC"
#
#   Input:  s = "a", t = "a"
#   Output: "a"
#
#   Input:  s = "a", t = "aa"
#   Output: ""   (only one 'a' in s, but t requires two)
#
# Constraints:
#   - 1 <= len(s), len(t) <= 10^5
#   - s and t consist of uppercase and lowercase English letters
#   - Characters in t may repeat; the window must contain each with at least
#     the required frequency
#
# Complexity target:
#   Time:  O(|s| + |t|)
#   Space: O(|s| + |t|)  [for the character frequency maps]


def min_window(s: str, t: str) -> str:
    pass
