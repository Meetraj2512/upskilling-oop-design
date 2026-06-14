# Problem: Expense Tracker — Final Phase: Complete Interview Q&A Bank

A revise-the-night-before sheet. Questions are grouped by theme. Answers are written
the way you'd *say* them out loud — short, confident, with the reasoning.

---

## A. OOP Fundamentals (they test the concept using your code)

**Q1. What is encapsulation and where did you use it?**
> Bundling data + the methods that operate on it inside a class, and hiding the data
> behind `private`. In my design, `Expense`'s fields are `private`; the outside world
> reads them through getters and can only change state via the tracker's methods. This
> stops invalid states and lets me change the internals later without breaking callers.

**Q2. What is abstraction here?**
> The caller says `tracker.getTotal()` and doesn't know or care that it loops an
> `ArrayList`. The *what* is exposed; the *how* is hidden. I could swap the storage to a
> `HashMap` and no caller would change.

**Q3. Did you use inheritance or polymorphism? Why / why not?**
> No — nothing here is a *specialized version* of something else, so forcing an "is-a"
> hierarchy would be over-engineering. I keep it flat. Inheritance shows up naturally in
> problems like Parking Lot (`Car`/`Bike` are-a `Vehicle`).

**Q4. Why is `Category` an enum instead of a `String`?**
> The set of categories is fixed and known. An enum makes invalid values a *compile
> error*, gives fast `==` comparison, and is self-documenting. A `String` invites typos
> like "Fod" that silently break grouping.

**Q5. What's the difference between `==` and `.equals()`, and which did you use?**
> `==` compares references (same object?), `.equals()` compares logical value. For enums
> I use `==` because each enum constant is a single shared instance — it's correct and
> fast. For comparing `String` content I'd use `.equals()`.

---

## B. Class-design decisions

**Q6. Why does `ExpenseTracker` own the list and not `Expense`?**
> It's the one-to-many backbone: one tracker, many expenses. If each `Expense` held the
> list, every record would carry a copy of all the others — nonsense. The manager owns
> the collection; the data class describes one item.

**Q7. Is `Expense` mutable or immutable? Trade-off?**
> With setters it's mutable, which supports an "edit expense" feature. Immutable (no
> setters) is safer — history shouldn't silently change, and it's thread-safe. A clean
> middle ground: keep `id` and `date` final and route edits through one
> `editExpense(id, …)` method on the tracker so the manager stays in control.

**Q8. Why a `nextId` counter? What could go wrong?**
> It hands out unique ids in O(1) without scanning for the max. Risks: if I *reuse* ids
> after deletion, or two threads read the same `nextId` concurrently, I get duplicates.
> Fix: never recycle ids; for concurrency use `AtomicInteger` or synchronize `addExpense`.

**Q9. Why does `getAllExpenses()` return a copy?**
> Defensive copy. If I returned the real list, a caller could `clear()` it and wipe my
> data behind my back, breaking encapsulation. The copy keeps the manager in control of
> its own collection.

**Q10. Why `private` fields if you expose getters anyway?**
> Reading is fine; *uncontrolled writing* is the danger. Getters give read access while I
> still decide if/how writes happen. It also lets me add validation or change storage
> later without touching callers.

---

## C. Data structures & complexity

**Q11. Why `ArrayList` over `LinkedList`?**
> My workload is append + iterate. `ArrayList` gives O(1) append and cache-friendly
> iteration with less memory. `LinkedList`'s only edge is O(1) middle insert/delete *when
> you already hold the node* — I never do, since I find by id (still O(n)). So ArrayList wins.

**Q12. When WOULD a (doubly) linked list be right?**
> When I need O(1) insert/delete at a known position — e.g. an LRU cache pairing a DLL
> with a `HashMap<key, Node>`. The doubly-linked part enables O(1) unlink (you can reach
> `prev`). Not useful here because the cost is in the *search*, not the unlink.

**Q13. Walk me through the complexity of each method.**
> `addExpense` O(1) amortized; `removeExpense` O(n) (scan to find); `getAllExpenses` O(n)
> time + O(n) space (copy); `getTotal` / `getTotalByCategory` O(n); `getExpensesByCategory`
> O(n) time, O(k) output; getters/`toString` O(1). System space O(n) for n expenses.

**Q14. How would you make `getTotalByCategory` O(1)?**
> Keep a running `HashMap<Category, Double>` updated on every add/remove. Reads become
> O(1) at the cost of a little memory and keeping two structures in sync — worth it only
> if category totals are read very frequently.

**Q15. How would you make `removeExpense(id)` O(1)?**
> Add a `HashMap<Integer, Expense>` index (id → expense). Find is O(1); from a plain
> `ArrayList` the actual removal is still O(n) (shift), so for true O(1) removal I'd use
> the HashMap + doubly-linked-list pattern.

**Q16. HashMap vs HashSet — when each?**
> `HashMap` = key→value lookup (id → expense). `HashSet` = membership/uniqueness (which
> categories have been used). Both ~O(1), both unordered.

---

## D. Edge cases & validation

**Q17. What if someone adds a negative or zero amount?**
> Validate in `addExpense`: reject `amount <= 0` with an
> `IllegalArgumentException`. Better to fail fast than store garbage that corrupts totals.

**Q18. Removing an id that doesn't exist?**
> `removeIf` simply removes nothing — a safe no-op. If callers need to know, I'd return a
> `boolean` (true if something was removed) so they can react.

**Q19. Null category, date, or description?**
> Guard in the constructor/`addExpense` (e.g. `Objects.requireNonNull(category)`).
> Description could default to "" instead of null to avoid NullPointerExceptions downstream.

**Q20. Floating-point money error — is `0.1 + 0.2` exact?**
> No, `double` can't represent those exactly (`0.30000000000000004`), so totals can drift
> by fractions of a cent. Fine for a casual tracker; for anything money-critical I'd store
> `long` cents or use `BigDecimal`. (That's exactly the switch I make for the Banking System.)

---

## E. Scaling & extensions

**Q21. Support multiple users?**
> Promote `User` from actor to entity, then a top-level `ExpenseApp` holds
> `Map<Integer, ExpenseTracker>` keyed by userId — one isolated tracker per user. Existing
> `Expense`/`ExpenseTracker` logic is reused untouched. (Commented reference is in the .java file.)

**Q22. Monthly budgets and alerts?**
> Add `Map<Category, Double> budgets` + `setBudget(cat, limit)`. On add, compare the
> month's `getTotalByCategory` against the limit and flag overspend. Reuses existing totals.

**Q23. Filter by date range / monthly report?**
> Add `getExpensesBetween(start, end)` — an O(n) scan with `!d.isBefore(start) &&
> !d.isAfter(end)`. Choosing `LocalDate` over a raw string is what makes this comparison clean.

**Q24. Persist data across restarts?**
> Introduce a `Repository` interface (save/load) with implementations: file (JSON/CSV) or
> a DB (JDBC). The tracker depends on the interface, not the storage — that's the
> Repository pattern and keeps business logic storage-agnostic.

**Q25. Make it thread-safe for concurrent access?**
> Use `AtomicInteger` for `nextId`, and either synchronize mutating methods or use a
> `CopyOnWriteArrayList`/`ConcurrentHashMap` depending on read/write ratio. Define clearly
> which operations need atomicity (e.g. read-modify-write on a running total).

---

## F. Java-specific gotchas

**Q26. What does `@Override` on `toString()` do, and why?**
> It replaces `Object`'s default `toString()` (which prints `Expense@1b6d35`) with a
> readable summary. `@Override` makes the compiler verify I'm actually overriding, catching
> signature typos.

**Q27. `List<Expense>` vs `ArrayList<Expense>` for the field type — does it matter?**
> Program to the interface: declare the field as `List` and instantiate `ArrayList`. Then
> swapping the implementation later touches one line. (Coding to the interface, not the impl.)

**Q28. Why initialize the list in the constructor?**
> If I don't `new ArrayList<>()`, the field stays `null` and the first `.add()` throws a
> `NullPointerException`. Always initialize collections before use.

**Q29. Garbage collection — when is a removed `Expense` freed?**
> When nothing references it anymore, the GC reclaims it automatically at some later time.
> I never call anything like `delete`; I just drop the reference (remove it from the list).

---

## G. "Explain your design" / behavioral

**Q30. How did you approach the design from scratch?**
> I find the entities and relationships first (actors vs entities, has-a vs is-a), design
> classes with well-typed private fields, place each method where its data lives, justify
> the collection by the access pattern, then state complexity and edge cases. Simple correct
> design first, with a clear path to optimize.

**Q31. What would you do differently with more time?**
> Add input validation + custom exceptions, a `Repository` for persistence, a running
> `Map<Category,Double>` if reports are hot, and unit tests for totals/removal/edge cases.

**Q32. How would you test this?**
> Unit tests: add then assert `getTotal`; add mixed categories then assert
> `getTotalByCategory`; remove existing vs non-existing id; empty tracker returns 0;
> defensive-copy test (mutating the returned list doesn't change internal state).
```
