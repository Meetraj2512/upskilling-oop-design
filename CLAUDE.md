# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A learning/upskilling repo for **OOP / low-level design interview practice in Java**. Each
problem is solved as a **single self-contained `.java` file** at the repo root, accompanied by
**phase-by-phase teaching notes** in `java/notes/`. There is no build tool, framework, or
dependency — just the JDK.

| File | Public class (entry point) | Problem |
|------|---------------------------|---------|
| `ExpenseTrackerApp.java` | `ExpenseTrackerApp` | Expense Tracker |
| `BankSystem.java` | `BankSystem` | Banking System |
| `LibrarySystem.java` | `LibrarySystem` | Library Management |
| `ParkingLotSystem.java` | `ParkingLotSystem` | Parking Lot |

## Build & run

There is no Maven/Gradle. Compile and run each problem **individually** with the JDK:

```bash
javac ExpenseTrackerApp.java && java ExpenseTrackerApp
javac BankSystem.java        && java BankSystem
javac LibrarySystem.java     && java LibrarySystem
```

Each file has a `main` that runs a small **demo scenario** and prints output — that demo *is*
the test. There is no unit-test framework; "verification" means running the file and checking
the printed output matches the expected behavior (balances, blocked operations, etc.).

**Do not `javac *.java` / compile the whole directory.** Every file lives in the default
package and several reuse generic top-level class names (`Account`, `Transaction`, etc.), so
compiling them together causes duplicate-class errors. Always compile one file at a time.
`.class` files are gitignored; clean them with `rm -f *.class`.

## Single-file structure convention

Each problem file packs all its classes into one file: exactly **one `public` class** (name must
match the filename, holds `main`) followed by several **package-private** classes and enums. This
is intentional — it keeps a whole design reviewable in one place. When adding a new problem,
follow the same shape and name the file after its public class.

## Shared design conventions (apply across all problems)

These patterns recur and should be preserved/reused when extending:

- **Manager + data classes.** One "manager" class (`ExpenseTracker`, `BankManager`, `Library`)
  owns the collections and is the entry point; it *routes* operations to data classes. Logic
  lives on the class that owns the data (e.g. `credit`/`debit` live on `Account`, not
  `BankManager`, because the balance lives on `Account`).
- **Money as `long` cents**, never `double` (exact arithmetic). Display-only formatting is
  separated out (see `Money.format` in `BankSystem.java`).
- **Fixed sets are enums** (`Category`, `TransactionType`, `CopyStatus`).
- **Lookup-by-key uses `HashMap`** (e.g. `Map<Integer, Account>`, `Map<String, Book>` by ISBN);
  ordered "iterate everything" collections use `ArrayList`.
- **Getters returning a collection return a defensive copy** (`new ArrayList<>(...)`) so callers
  can't mutate internal state.
- **Invalid operations throw** with clear messages (`IllegalArgumentException`,
  `IllegalStateException`, or a custom exception like `InsufficientFundsException`) rather than
  returning error codes.
- **Many-to-many relationships get a join entity** (e.g. `Loan` joins `Member` and `BookCopy`
  and is the home for `dueDate`).

## The notes system (`java/notes/`)

The markdown notes are first-class deliverables, not afterthoughts — this repo doubles as a
study guide. Two kinds:

- `ProblemN_PhaseX_*.md` — one file per teaching phase (Analysis → Class Design → Methods →
  Data Structures → Complexity/Q&A). Each follows a fixed header format:
  `Key Concepts Covered` / `What We Built` / `Why We Made These Decisions` /
  (`Java vs C++ Difference Here` — historical; the repo is now Java-only) /
  `Things to Remember for Interview`.
- `OOP_Design_Playbook.md` — the reusable 6-step method for decomposing any design problem
  into entities/actors/relationships. Read this first to understand the design vocabulary used
  throughout (manager class, "where does the data live?", join entity, etc.).

When adding a problem or extending one, keep the corresponding notes in sync — the notes
explain the *why* behind each design decision in beginner-friendly language.

## Note on file layout

Solution `.java` files currently sit at the repo root; only `notes/` lives under `java/`. If you
add solutions, match the existing root-level placement so the simple per-file `javac`/`java`
commands above keep working.
