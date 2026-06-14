# Problem: Expense Tracker — Phase 3: Methods & Behaviors

## Key Concepts Covered
- **Where does a method belong?** → the class that owns the data it needs.
- Constructors and the `this.field = param` pattern.
- **Getters & encapsulation**; no setters → effectively immutable records.
- Overriding `toString()` for readable output.
- **Defensive copy** when returning an internal collection.
- Lambdas (`removeIf(e -> e.getId() == id)`) and why enums use `==`.

## What We Built
**`Expense`** (describes itself):
- Constructor setting all 5 fields.
- Getters: `getId`, `getAmount`, `getCategory`, `getDate`, `getDescription`.
- `toString()` for a one-line summary.

**`ExpenseTracker`** (operates on the whole collection):
- `ExpenseTracker()` — inits empty `ArrayList`, `nextId = 1`.
- `addExpense(...)` — creates `Expense` with current `nextId`, stores it, increments id, returns it.
- `removeExpense(id)` — `removeIf` matching id, returns boolean.
- `getAllExpenses()` — returns a **copy** of the list.
- `getTotal()` — loop-sum of all amounts.
- `getTotalByCategory(cat)` — loop-sum filtered by category.
- `getExpensesByCategory(cat)` — filtered list.

## Why We Made These Decisions
- Single-expense logic lives on `Expense`; "across all expenses" logic lives on `ExpenseTracker` because it owns the list. Putting `getTotal()` on `Expense` would be wrong — one expense can't see the others.
- Constructor avoids creating blank objects then mutating private fields.
- No setters → an expense is a record of something that already happened; immutability prevents accidental tampering.
- `getAllExpenses()` returns a copy so callers can't `clear()`/mutate our real list (defensive copy = protects encapsulation).
- `nextId` increments on every add → unique ids in O(1), no scanning for max.
- Enum comparison uses `==` because enum constants are single shared instances (correct + fast).

## Language Note (Java-specific)
- `@Override toString()` replaces Object's default ugly output (`Expense@1b6d35`).
- Forgetting `new ArrayList<>()` in the constructor leaves the list `null` → `NullPointerException` on first add.
- `removeIf(lambda)` is a clean Java 8+ way to delete by condition.

## Things to Remember for Interview
- Say the rule out loud: **"the method goes where the data lives."**
- Mention the **defensive copy** on getters that return collections — it impresses.
- Justify **no setters → immutable record**.
- Note the O(n) scan in `getTotalByCategory` and that a `Map` could optimize it — sets up the Phase 4 discussion.
- Always initialize collections in the constructor.
