# Problem: Banking System — Phase 1: Problem Analysis

## Key Concepts Covered
- Re-applying the actor-vs-entity + has-a/is-a framework to a harder problem.
- Finding the *hidden middle entity* (`Account`) by asking "where does the data live?"
- **Nested one-to-many** relationships (two levels deep) — the step-up from Problem 1.
- Recognizing a known pattern reused (`Account → Transaction` == `ExpenseTracker → Expense`).

## What We Built
No code — analysis only. (Worked out interactively by the learner, then corrected.)
- **System purpose:** manage many bank accounts — open accounts, credit/debit money,
  enforce a non-negative balance (no overdraft), check balance, view transaction history.
- **Actor:** `User` (outside the system).
- **Entities (→ classes):**
  - `BankManager` (a.k.a. `Bank`) — the **manager**; owns all accounts.
  - `Account` — **data class**; holds accountNumber, holderName, balance, and its own
    transaction history. (This was the easy-to-miss middle entity.)
  - `Transaction` — **data class**; one credit/debit event (type, amount, resulting
    balance, timestamp).

## Why We Made These Decisions
- The **balance lives on `Account`**, not the manager (the manager has many balances) and
  not on `Transaction` (a transaction is a finished event). "Where does the data live?"
  forces the `Account` entity to appear.
- **History attaches to an `Account`** → `Account` owns a list of `Transaction`.
- Relationships:
  - `BankManager → Account`: **one-to-many** (one bank, many accounts).
  - `Account → Transaction`: **one-to-many** (one account, many transactions).
  - No inheritance yet (could appear later with `SavingsAccount`/`CheckingAccount`).
- This is a **nested one-to-many** (Bank → Account → Transaction) — two levels, versus
  Problem 1's single level.

## Things to Remember for Interview
- Always hunt for the **middle entity** by asking where balance/state and history live.
- State one-to-many **from the owner's side**: "the Bank has-a many Accounts."
- `Account → Transaction` is the same "owner + chronological record list" pattern as
  `ExpenseTracker → Expense` — reuse what you know.
- Call out the **nested one-to-many** explicitly; it signals you see the structure.
