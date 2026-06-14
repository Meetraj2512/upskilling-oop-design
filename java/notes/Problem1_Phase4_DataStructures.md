# Problem: Expense Tracker — Phase 4: Data Structures

## Key Concepts Covered
- The big four Java collections: **ArrayList, LinkedList, HashMap, HashSet** — when each wins.
- "Lists" (ordered, by-position) vs "hash-based" (by-key, ~O(1), no order).
- Choosing a collection by **how you look things up**, not by habit.
- Big-O of common operations.
- The "simplest correct design + named scaling path" interview move.

## What We Built (decision)
- Main collection stays **`ArrayList<Expense>`** — matches our workload: append at the end (O(1)) + iterate all (totals/reports).
- Optimizations *named but not shipped* for this small problem:
  - `HashMap<Integer, Expense>` (id → expense) for O(1) lookup/remove at scale.
  - `HashMap<Category, Double>` running totals for O(1) category sums.
  - `HashSet<Category>` to track distinct categories used.

## Why We Made These Decisions
- **ArrayList over LinkedList:** we never insert in the middle while holding a node (LinkedList's only edge). ArrayList has O(1) append, O(1) index access, better cache locality, less memory (no next/prev pointers). For "grow at end + loop," ArrayList wins.
- **Skip the HashMap index for now:** a personal tracker has tens–hundreds of items, so the O(n) scan in `removeExpense`/`getTotalByCategory` is effectively instant. Adding a Map means keeping two structures in sync = more code + bug risk. Not worth it yet.
- **HashSet** isn't needed for core logic but is the right tool for "unique categories used" because it auto-dedupes in ~O(1).

## Big-O Cheat Sheet
| Op | ArrayList | LinkedList | HashMap | HashSet |
|----|-----------|------------|---------|---------|
| Access by index | O(1) | O(n) | — | — |
| Search by value | O(n) | O(n) | O(1) by key | O(1) |
| Insert at end | O(1)* | O(1) | O(1) | O(1) |
| Insert/del middle | O(n) | O(1)** | O(1) | O(1) |

\* amortized (array occasionally doubles). \** only if you already hold the node.

## Things to Remember for Interview
- Justify the collection by the **access pattern**: "we append and iterate, so ArrayList."
- Know LinkedList's middle-insert advantage requires *already holding the node* — otherwise it's still O(n) to find.
- Say the magic line: *"If this scaled to millions of id lookups, I'd add a `HashMap<Integer, Expense>` index for O(1)."*
- HashMap = key→value lookup; HashSet = uniqueness/membership. Both ~O(1), both unordered.
- Default to the **simplest correct structure**, then name the optimization — don't over-engineer up front.
