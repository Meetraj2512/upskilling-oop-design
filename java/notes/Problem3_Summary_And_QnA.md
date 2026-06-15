# Problem: Library Management System — Full Summary & Interview Q&A

## Key Concepts Covered
- The **title vs physical copy** split (`Book` vs `BookCopy`).
- **Many-to-many** (Member ↔ BookCopy) resolved with a **join entity** (`Loan`).
- Where relationship data (the due date) lives.
- Status enums, business-rule guards (max 3 books), and overdue detection.

## What We Built (the design)
```
Library (manager)
   --owns many--> Book --owns many--> BookCopy   (title vs physical instance)
   --owns many--> Member
   --owns many--> Loan   (join: which Member borrowed which BookCopy + dates)
CopyStatus (enum: AVAILABLE, BORROWED)
```
- **Book**: title, author, isbn, `List<BookCopy>`; `findAvailableCopy()`.
- **BookCopy**: copyId, back-reference to Book, `CopyStatus`; `markBorrowed/markAvailable`.
- **Member**: id, name, `List<Loan>` active loans.
- **Loan**: loanId, member, copy, borrowDate, dueDate, returnDate; `isOverdue(today)`.
- **Library**: maps by isbn / memberId / loanId; `addBook`, `registerMember`, `borrowBook`,
  `returnBook`, with `MAX_BOOKS_PER_MEMBER = 3` and a 14-day loan period.

## Why We Made These Decisions
- **Book vs BookCopy:** availability is per *physical copy*. A single `isAvailable` on Book
  couldn't let two members hold the same title. Each `BookCopy` has its own status.
- **Loan is the join entity:** Member ↔ BookCopy is many-to-many; the due date belongs to the
  *act of borrowing*, not to a member or a copy. Loan is its natural home.
- **borrow/return + the "max 3" rule live on Library** (the manager) because the policy spans
  members and copies. Copy status mutation lives on BookCopy; finding a free copy lives on Book.
- **Maps for O(1) lookup** by isbn / member id / loan id.

## Complexity
| Operation | Time |
|-----------|------|
| addBook (k copies) | O(k) |
| registerMember | O(1) |
| borrowBook | O(c) to scan a book's copies (c = copies of that title) |
| returnBook | O(1) (loan id -> loan, then O(L) to remove from member's small active list) |
| getActiveLoans | O(L) copy (L = member's active loans, ≤ 3) |

## Interview Q&A

**Q1. Why split Book and BookCopy?**
> "Harry Potter" is one title (one ISBN) but many physical copies. Availability/borrowing is
> per copy, so each needs its own status. Book holds shared metadata; BookCopy is what you borrow.

**Q2. Why does the due date live on Loan and not Member or Book?**
> Member ↔ BookCopy is many-to-many, and the due date describes a *specific borrowing event*.
> That's a join entity — Loan — which owns borrowDate/dueDate/returnDate.

**Q3. Where do you enforce "max 3 books"? Why there?**
> On the manager (`Library.borrowBook`), because it's a policy that needs to see the member's
> current loans. Data classes hold state; the manager enforces cross-entity rules.

**Q4. How do you detect overdue books?**
> `Loan.isOverdue(today)` compares the due date to the return date (or today if still out).
> A background job could scan active loans daily to flag/notify.

**Q5. How would you add fines for late returns?**
> On return, if overdue, compute `daysLate * finePerDay` and attach it to the member (or a Fine
> entity). Reuses `isOverdue` and the dates already on Loan.

**Q6. How would you add reservations / holds?**
> A `Reservation` entity (member, book, timestamp) queued per Book; when a copy is returned and
> reservations exist, allocate to the next member instead of marking AVAILABLE.

**Q7. Different member types (student vs staff) with different limits?**
> Inheritance: `Member` base with `getMaxBooks()`, overridden by `StudentMember` (3) and
> `StaffMember` (10). `borrowBook` calls `member.getMaxBooks()` polymorphically.

**Q8. Search the catalog by author/title?**
> Add secondary indexes (`Map<String, List<Book>>` by author) or scan for small catalogs.
> Trade memory for O(1) lookups if search is frequent.

**Q9. Make it concurrent (two members grabbing the last copy)?**
> Synchronize `borrowBook` per book (or use an atomic claim on the copy's status) so only one
> thread can transition a copy AVAILABLE -> BORROWED.

## Things to Remember for Interview
- Lead with the Book/BookCopy split and the Loan join entity — they're the whole insight.
- Put cross-entity policy on the manager; put state mutation on the entity that owns the state.
- Be ready to extend with fines, reservations, member-type inheritance, search indexes, concurrency.
