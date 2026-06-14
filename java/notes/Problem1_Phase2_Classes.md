# Problem: Expense Tracker — Phase 2: Class Design

## Key Concepts Covered
- Turning entities into **class skeletons** (fields only, methods deferred).
- **Encapsulation:** fields are `private` so outside code can't corrupt them.
- Choosing **data types** deliberately and the reasons behind each.
- The **money/`double` precision gotcha** and when it matters.
- The **manager owns the collection** pattern (`ExpenseTracker` holds `List<Expense>`).

## What We Built (Java)
- `enum Category { FOOD, TRAVEL, RENT, SHOPPING, UTILITIES, OTHER }`
- `class Expense` with fields: `int id`, `double amount`, `Category category`, `LocalDate date`, `String description`.
- `class ExpenseTracker` with fields: `List<Expense> expenses`, `int nextId`.
- No methods yet — only constructors/getters/operations come in Phase 3.

## Why We Made These Decisions
- **`id`**: a stable handle to find/delete a specific expense unambiguously.
- **`amount` as `double`**: simple and readable *for this learning tracker only*. Flagged: for money-exact systems (Banking, Problem 2) we'll switch to integer cents (`long`) or `BigDecimal` because `double` can't represent decimals exactly (`0.1 + 0.2 != 0.3`).
- **`category` as enum**: closed, type-safe set — typos become compile errors.
- **`date` as `LocalDate`**: Java's built-in, clean date type (`import java.time.LocalDate`), enables month/range filtering.
- **`nextId`**: hand out unique ids in O(1) instead of scanning for the max each time.
- **Fields `private`**: encapsulation — the only way to change an expense is through methods we control.

## Java vs C++ Difference Here
- **Encapsulation syntax:** Java writes `private` on each field; C++ uses a single `private:` section label.
- **Enum:** Java `enum Category { ... }` (enums are full objects, can hold methods). C++ uses `enum class`.
- **Collection field:** Java `List<Expense>` is an *interface* (usually backed by `ArrayList`) and stores **references** to heap objects; the **Garbage Collector** frees them later.
- **Date:** Java has first-class `LocalDate`; C++ has no easy built-in (uses a string or C++20 `std::chrono`).
- **No trailing semicolon** needed after a Java class body (unlike C++).

## Things to Remember for Interview
- Always make fields `private` and justify it as **encapsulation**.
- Proactively mention the **`double` money problem** — it signals seniority.
- Keep a `nextId` counter instead of recomputing max ids.
- State clearly: "the manager class owns the one-to-many collection."
- Defer the *choice* of collection to a data-structures discussion (Phase 4) — but know `List`/`ArrayList` is the sensible default.
