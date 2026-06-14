# Problem: Expense Tracker — Phase 5: Complexity & Q&A

## Key Concepts Covered
- Annotating **time & space complexity** per method.
- Reasoning about overall system space.
- Handling **follow-up questions**: mutability, id uniqueness, budgets, multi-user, money precision, date filtering, collection choice.
- The habit of stating the **simple design + named optimization**.

## What We Built
Final solution: `java/ExpenseTrackerApp.java` (single file: `ExpenseTrackerApp` with `main`, plus `Category`, `Expense`, `ExpenseTracker`). Compiles and runs; complexity is documented in each method's comment.

## Complexity (n = number of expenses)
| Method | Time | Space |
|--------|------|-------|
| addExpense | O(1) amortized | O(1) |
| removeExpense | O(n) | O(1) |
| getAllExpenses | O(n) | O(n) (defensive copy) |
| getTotal | O(n) | O(1) |
| getTotalByCategory | O(n) | O(1) |
| getExpensesByCategory | O(n) | O(k) matches |
| getters / toString | O(1) | O(1) |

System space: O(n) — n stored expenses, no duplication.

## Why We Made These Decisions
- Reports are simple O(n) single passes — correct and readable; optimize only if profiling/usage demands it.
- Defensive copy in `getAllExpenses` costs O(n) space but protects the internal list.
- O(1) amortized add comes from ArrayList's end-append (occasional resize averages out).

## Interview Q&A (memorize the angles)
1. **Mutable vs immutable Expense** — immutable is safer (history shouldn't change, thread-safe); mutable enables edits. Middle ground: keep `id`/`date` final, edit the rest via a single `editExpense` on the tracker.
2. **Duplicate ids** — impossible since `nextId` only increments; risk only if reusing ids or multithreading → use `AtomicInteger`/synchronize.
3. **Budgets/alerts** — `Map<Category,Double> budgets` + compare against `getTotalByCategory` on add.
4. **Multiple users** — promote `User` to an entity; `Map<Integer,ExpenseTracker>` keyed by userId.
5. **Money precision** — `double` drifts (`0.1+0.2!=0.3`); for banking use `long` cents or `BigDecimal`.
6. **Date-range/monthly report** — add `getExpensesBetween(start,end)`; O(n) scan with `LocalDate` comparison.
7. **Why ArrayList** — append+iterate workload; LinkedList's middle-insert edge needs the node in hand; HashMap index is the scale-up for O(1) id lookup.

## Things to Remember for Interview
- Lead with "adds O(1), reports O(n) single passes" then offer the HashMap caching upgrade.
- Always be ready to discuss mutability, concurrency (ids), and scaling to multi-user.
- Tie answers back to earlier design choices (enum, LocalDate, manager-owns-collection) to show coherence.
- The `double` money point is your bridge into Problem 2 (Banking System).
