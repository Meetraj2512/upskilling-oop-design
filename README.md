# Upskilling: OOP / Low-Level Design in Java

Practice solutions for classic **object-oriented / low-level design** interview problems,
implemented from scratch in Java — each with **phase-by-phase notes** explaining *why* every
design decision was made. Built as a learning resource, so the reasoning matters as much as the
code.

## Problems

| Problem | File | What it covers |
|---------|------|----------------|
| 1. Expense Tracker | [`ExpenseTrackerApp.java`](ExpenseTrackerApp.java) | entities vs attributes, encapsulation, one-to-many, picking collections |
| 2. Banking System | [`BankSystem.java`](BankSystem.java) | exact money math (`long` cents), custom exceptions, immutable transaction history, nested one-to-many |
| 3. Library Management | [`LibrarySystem.java`](LibrarySystem.java) | title vs physical copy, **many-to-many via a join entity** (`Loan`), due dates & borrowing rules |
| 4. Parking Lot | [`ParkingLotSystem.java`](ParkingLotSystem.java) | **inheritance + polymorphism** (`Vehicle` hierarchy), spot-fit logic, ticketing & fees |

## Run it

No build tool or dependencies — just a JDK. Compile and run any problem on its own:

```bash
javac ExpenseTrackerApp.java && java ExpenseTrackerApp
javac BankSystem.java        && java BankSystem
javac LibrarySystem.java     && java LibrarySystem
```

> Compile **one file at a time** (not `javac *.java`): every file is in the default package and
> some reuse generic class names, so compiling them together causes duplicate-class errors.

Each file's `main` runs a small demo scenario and prints the result — that demo doubles as the test.

## Design conventions used throughout

- **Manager + data classes** — one manager class owns the collections and routes operations;
  logic lives on the class that owns the data.
- **Money as `long` cents**, never `double` — exact arithmetic, with display formatting kept separate.
- **Enums** for fixed sets (categories, transaction/copy status).
- **`HashMap`** for key lookups, **`ArrayList`** for ordered iteration.
- **Defensive copies** when returning internal collections.
- **Throw on invalid operations** (including custom exceptions) instead of silent failure.
- **Join entity** for many-to-many relationships.

## Notes & study guide

The [`java/notes/`](java/notes/) folder is the heart of the learning material:

- **[`OOP_Design_Playbook.md`](java/notes/OOP_Design_Playbook.md)** — a reusable 6-step method for
  breaking *any* design problem into entities, actors, and relationships. Start here.
- **`ProblemN_Phase*.md`** — per-phase walkthroughs (problem analysis → class design → methods →
  data structures → complexity & interview Q&A).

## Roadmap

- [x] Expense Tracker
- [x] Banking System
- [x] Library Management System
- [x] Parking Lot System
