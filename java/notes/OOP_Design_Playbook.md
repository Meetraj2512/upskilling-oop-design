# OOP Design Playbook — How to Break ANY Problem into Classes

A reusable, repeatable method. Works for Expense Tracker, Banking, Library, Parking Lot,
and anything they throw at you. Practice it until it's automatic.

---

## The 6-Step Process

### Step 1 — Read it and underline every NOUN and every VERB
- **Nouns** → candidate entities or attributes (the "things").
- **Verbs** → candidate methods (the "actions").
- Example (Library): nouns = book, copy, member, librarian, loan, due date, fine;
  verbs = borrow, return, register, search, reserve.

### Step 2 — Sort each noun into one of THREE buckets
For every noun ask: *actor, entity, or attribute?*

| Bucket | Test | Becomes |
|--------|------|---------|
| **Actor** | Does it USE the system from outside? (a person/external system) | NOT a class (usually) — it's who calls methods |
| **Entity** | Does it have its own identity, fields, and lifecycle? Do we store many of them? | A **class** |
| **Attribute** | Is it just a value describing an entity, with no behavior of its own? | A **field** inside a class |

> The attribute-vs-entity test (most common mistake): *"Does this thing need its own
> fields or behavior, or is it just one value?"* `color` = attribute. `Address` with
> street/city/zip = entity. When unsure, start as a field; promote to a class if it grows.

### Step 3 — Find the MANAGER and the hidden MIDDLE entities
- Almost every design has **one manager/service class** that owns the collections and is
  the entry point (`ExpenseTracker`, `BankManager`, `Library`, `ParkingLot`).
- Find **hidden entities** with the killer question:
  > **"Where does this DATA live?"**
  - Where does `balance` live? → forces `Account` into existence.
  - Where does `dueDate` live? → not on member, not on book → forces a `Loan` entity.
  - Where does the running history live? → on the owner, as a list.

### Step 4 — Connect them: name every RELATIONSHIP
Two shapes only:
- **has-a** (composition/aggregation): one class contains another.
  - **one-to-one**: a Person has-a Address.
  - **one-to-many**: a Library has-a many Books. (state it from the OWNER's side)
  - **many-to-many**: Members borrow many Books; a Book is borrowed by many Members.
- **is-a** (inheritance): a SavingsAccount is-a Account; a Car is-a Vehicle.

> 🔑 **Many-to-many ALWAYS needs a "join" / association entity.** You can't model it with a
> plain list on each side cleanly — you create a middle class that represents the
> relationship itself, and it's the natural home for relationship data:
>   - Member ↔ Book  →  **Loan** (holds member, copy, borrowDate, dueDate)
>   - Student ↔ Course → **Enrollment** (holds grade, semester)
>   - Driver ↔ Trip → **Booking**
> If you find yourself asking "where do I put the due date / grade / price?", that's the
> join entity knocking.

### Step 5 — Assign each VERB (method) to the class that owns the data
- Rule: **a method goes where the data it needs lives.**
- `debit()` needs `balance` → lives on `Account`.
- `getTotal()` needs all expenses → lives on the manager.
- If a method needs data from two classes, it usually lives on the manager (which can see both),
  or on the join entity.

### Step 6 — Pick types & collections, start minimal
- Fixed set of choices → **enum** (Category, TransactionType, VehicleType, AccountStatus).
- Money → **`long` cents** or `BigDecimal`, never `double`.
- Lookup by a key → **`Map<key, Entity>`** (O(1)); ordered list you iterate → **`ArrayList`**.
- Uniqueness/membership → **`Set`**.
- Build the **essential** fields/methods only; SAY the optional extensions out loud.

---

## Fast Heuristics (pattern-match these)

| You see / hear... | It usually means... |
|-------------------|---------------------|
| "history of", "log of", "list of" | a one-to-many: owner has-a `List<Record>` |
| "X can have many Y AND Y can have many X" | many-to-many → make a **join entity** |
| "different types of X" with different rules | **inheritance** (base X + subclasses) |
| a fixed set of options/states | an **enum** |
| "available / borrowed / reserved", "active / frozen" | a **status enum** on the entity |
| "at most N", "cannot exceed", "must be ≥ 0" | a **validation rule** in a method (throw/guard) |
| "physical copy" vs "the title" | split into **Item** (the concept) + **Copy** (the instance) |
| "who is using/managing it" | an **actor**, not a class |
| money, balance, price | `long` cents / `BigDecimal`, never `double` |

---

## A 60-Second Worked Example (Library)
1. **Nouns:** book, copy, member, librarian, loan, due date. **Verbs:** borrow, return, register.
2. **Buckets:** librarian/member = actors (member also becomes an entity since we store members);
   book, copy, member, loan = entities; title/author/ISBN/dueDate = attributes.
3. **Manager:** `Library`. **Hidden entity:** "where's the due date?" → `Loan`.
   "Title vs physical copy?" → `Book` (the title) + `BookCopy` (the physical instance you borrow).
4. **Relationships:** Library 1→many Book; Book 1→many BookCopy; Member ↔ BookCopy many-to-many → **Loan**.
5. **Methods:** `borrow()` needs member + copy + dueDate → on `Library` (or creates a `Loan`).
6. **Types:** `BookStatus { AVAILABLE, BORROWED }` enum; `Map<String, Book>` by ISBN; loan limit = guard.

---

## The One-Paragraph Script to Say in the Interview
> "First I'll pull out the nouns and verbs. Nouns become entities or attributes, verbs become
> methods. I'll find the manager class that owns the collections, then ask 'where does each
> piece of data live?' to surface hidden entities like a Loan or an Account. I'll name the
> relationships — especially any many-to-many, which needs a join entity — then place each
> method on the class that owns its data. I'll start with the essential fields and call out
> extensions. Let me begin..."

---

## Things to Remember for Interview
- Nouns→entities/attributes, Verbs→methods. Always start here.
- The killer question to find hidden classes: **"Where does this data live?"**
- Many-to-many ⇒ **join entity** (and it's where the relationship's data goes).
- "Different types with different rules" ⇒ **inheritance**.
- Method goes where its data lives. Start minimal, name extensions, never over-engineer.
