# Aho-Corasick Pattern Matching

This project implements the Aho-Corasick algorithm in Java. It searches for multiple patterns in a text and compares the execution time with KMP, which searches for each pattern separately.

## How to run

1. Open the project folder in IntelliJ IDEA.
2. Set the Project SDK to JDK 17.
3. Open `Main.java`.
4. Run the `main()` method.

You can also run it using the terminal:

```
javac -d out src/*.java
java -cp out Main
```

The program first runs the tests, then measures the execution times and saves the results in `results.csv`.

## Project files

- `AhoCorasick.java` - Aho-Corasick implementation
- `KMP.java` - KMP implementation
- `TestCases.java` - Tests for checking the algorithms
- `Main.java` - Runs the tests and compares execution times
- `results.csv` - Benchmark results
- `explanation.txt` - Notes for the handwritten explanation

## How Aho-Corasick works

Aho-Corasick uses a trie to store multiple patterns. Each character becomes part of a path in the trie. When a pattern ends, its ID is stored in that node.

After building the trie, the algorithm creates failure links using BFS (breadth-first search). These links tell the algorithm where to continue if the current character does not match. Instead of starting the search over, it moves to another possible matching state.

Output links are used to find other patterns that end at the same position.

When searching through the text, the algorithm moves between trie nodes using the characters in the text. If it reaches the end of a pattern, it records the match and its starting position.

This allows Aho-Corasick to find multiple patterns while going through the text only once.

## Test cases

I used five tests to check that the algorithms work correctly.

1. `ushers` with `he`, `she`, `his`, `hers` - overlapping matches
2. `aaaa` with `a`, `aa`, `aaa` - repeated characters and overlapping patterns
3. `abcdef` with `xyz`, `123` - no matches
4. `aaa` with `a`, `a`, `aa` - duplicate patterns
5. `abcabc` with `abc`, `bc`, `c` - patterns ending at the same position

Both algorithms were checked against the expected results. The benchmark also checks whether both algorithms find the same number of matches.

## Performance testing

I tested both algorithms using:

- **Number of patterns:** 10, 100, and 1,000
- **Text lengths:** 1,000, 10,000, and 100,000 characters
- **Number of runs:** 5 for each combination

I used `System.nanoTime()` to measure execution time. Each test was also run twice before the measurements as a warm-up.

The test data contains randomly generated lowercase letters using fixed seeds. Patterns have lengths between 5 and 12 characters and are taken from the generated text.

The times include building the required data structures and searching the text, but not generating the test data.

## Results

These results were measured using OpenJDK 21 with Java 17 compatibility.

| Patterns | Text length | Aho-Corasick (ms) | KMP (ms) |
|---|---|---|---|
| 10 | 1,000 | 0.419 | 0.877 |
| 10 | 10,000 | 0.671 | 0.979 |
| 10 | 100,000 | 2.283 | 2.920 |
| 100 | 1,000 | 0.722 | 0.299 |
| 100 | 10,000 | 0.876 | 2.931 |
| 100 | 100,000 | 2.947 | 31.178 |
| 1,000 | 1,000 | 1.921 | 3.208 |
| 1,000 | 10,000 | 1.559 | 33.249 |
| 1,000 | 100,000 | 4.723 | 299.049 |

All times are averages of 5 runs.

## Time and space complexity

For the complexity analysis:

- `m` = total length of all patterns
- `n` = length of the text
- `k` = number of patterns
- `z` = total number of matches

### Aho-Corasick

Building the trie takes expected `O(m)` time. Creating the failure links and output links also takes expected `O(m)` time.

Searching takes expected `O(n + z)` time because the algorithm processes the text and reports the matches it finds.

The total expected time complexity is:

**O(m + n + z)**

The space complexity is:

**O(m + k)**

The implementation uses hash maps for transitions, so these time bounds assume average constant-time hash-map operations.

### KMP

KMP builds a prefix array for each pattern and searches the whole text separately for every pattern.

Building all prefix arrays takes `O(m)` time. Searching takes `O(kn)` time.

The total time complexity is:

**O(m + kn + z)**

Since this implementation processes one pattern at a time, the extra space is **O(L)**, where `L` is the length of the longest pattern.

## Comparison of Aho-Corasick and KMP

Looking at the results, Aho-Corasick was generally faster when the number of patterns increased.

For example, with 1,000 patterns and 100,000 characters, Aho-Corasick took around 4.7 ms, while KMP took around 299 ms.

This happens because Aho-Corasick searches for all patterns during one scan of the text. KMP has to search the same text again for every pattern.

However, KMP was faster for 100 patterns and 1,000 characters in this test. This is probably because Aho-Corasick needs extra time to build the trie and failure links.

KMP is easier to implement and uses less memory. It works well when there are only a few patterns.

Aho-Corasick is more complicated and needs additional memory for the trie and links, but it is usually better when searching for many patterns.

## Conclusion

From these tests, I found that Aho-Corasick is more efficient when searching for a large number of patterns, especially in longer texts.
KMP is still a good choice for searching one pattern or a small number of patterns because it is simpler and uses less memory.
Aho-Corasick performed better in most of my tests, especially when using 1,000 patterns.

## Submission

The Java files, tests, results, and README are included in this repository. The handwritten explanation is submitted separately.