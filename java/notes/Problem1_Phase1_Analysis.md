# Problem: Expense Tracker — Phase 1: Problem Analysis

## Key Concepts Covered
- The difference between an **actor** (who uses the system, lives outside) and an **entity** (a noun the system stores, becomes a class).
- The "circle the nouns" trick for finding entities.
- The two core relationship shapes: **has-a** (composition/aggregation) and **is-a** (inheritance).
- The common pattern of a **manager class** (owns the collection + operations) vs **data classes** (just hold info).

## What We Built
No code yet — this phase is analysis only. We identified:
- **System purpose:** record spending, organize it, answer summary questions (total, by category).
- **Entities:** `Expense` (data class), `Category` (enum — fixed set of choices), `ExpenseTracker` (manager class that owns all expenses).
- **Actor:** the `User` (human). Not modeled as a class yet because the app is single-user.

## Why We Made These Decisions
- `ExpenseTracker` owns the list of expenses because the collection logically belongs to the notebook, not to an individual record. If `Expense` held the list, every record would absurdly carry a copy of all others.
- `Category` is a fixed, known set → an **enum** is the right tool (type-safe, no typos like the string "Fod").
- No inheritance here because nothing is a "specialized version" of something else — the problem is flat. Keeping it simple avoids over-engineering.

## Java vs C++ Difference Here
- **Enum syntax:** Java uses `enum Category { FOOD, TRAVEL }`. (C++ uses `enum class`.)
- **Memory:** In Java, when an `Expense` is removed from the list and nothing else references it, the **Garbage Collector** reclaims it automatically at some later time — you never call `delete`.

## Things to Remember for Interview
- Say out loud: *"Let me first identify the entities and their relationships."* — interviewers reward this.
- Always name the **one-to-many** backbone relationship (Tracker → many Expenses).
- Distinguish actor vs entity explicitly; mention you'd add a `User` class if it became multi-user.
- Default to **no inheritance** unless you can point to a real "is-a".
