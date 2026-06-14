# Problem: Banking System — Full Summary & Interview Q&A

## Key Concepts Covered
- Nested one-to-many (Bank → Account → Transaction).
- Money as `long` cents (exact arithmetic) + separating storage from display.
- Logic lives where the data lives (credit/debit on Account, not the manager).
- Custom exceptions (`InsufficientFundsException`) and exceptions vs boolean returns.
- Immutable, snapshot-based transaction history.
- `HashMap` for O(1) account lookup by number.

## What We Built (the design)
```
BankSystem (demo/main)
BankManager  --owns many-->  Account  --owns many-->  Transaction
TransactionType (enum: CREDIT, DEBIT)
InsufficientFundsException (custom)
Money (display helper)
```
- **Transaction** (immutable): `type`, `amount`, `previousBalance`, `resultingBalance`, `timestamp`.
- **Account**: `accountNumber`, `ownerName`, `balance` (long cents), `List<Transaction>`;
  methods `credit`, `debit` (overdraft-guarded), `getBalance`, `getTransactions` (defensive copy).
- **BankManager**: `Map<Integer, Account>`, `nextAccountId`; methods `openAccount`,
  `credit`, `debit`, `getBalance`, `getHistory`, private `getAccount` (404 guard).

## Why We Made These Decisions
- **Account owns credit/debit** because the balance lives there ("method goes where the data is").
- **Overdraft guard throws** rather than returns false → a failed withdrawal can't be silently ignored.
- **Custom exception** gives the failure a name so callers can `catch` this specific case.
- **`long` cents** avoids `double` rounding; **Money.format** keeps display separate from storage.
- **Store before + after balances** → frozen history that can't re-derive wrongly; no setters = immutable.
- **`Map<Integer,Account>`** → O(1) lookup by account number (we always look up by number).
- **Defensive copy** on `getTransactions` protects the real history list.

## Complexity (a = accounts, t = transactions on one account)
| Operation | Time | Space |
|-----------|------|-------|
| openAccount | O(1) | O(1) |
| credit / debit | O(1) | O(1) (one transaction appended) |
| getBalance | O(1) | O(1) |
| getHistory | O(t) | O(t) (defensive copy) |
| getAccount (lookup) | O(1) avg (HashMap) | O(1) |
System space: O(a + total transactions).

## Interview Q&A

**Q1. Why store money as `long` cents instead of `double`?**
> `double` can't represent decimals exactly (`0.1+0.2 != 0.3`), so balances drift. Integer
> cents make arithmetic exact. I convert to a "$d.cc" string only for display.

**Q2. Why does credit/debit live on `Account` and not `BankManager`?**
> The balance is Account state, so the code that mutates it belongs there. BankManager is a
> thin router that finds the account and delegates — keeps responsibilities clean.

**Q3. Why throw `InsufficientFundsException` instead of returning false on overdraft?**
> A failed withdrawal is exceptional and must be handled; an exception can't be silently
> ignored like a boolean. A *custom* exception names the failure so callers can catch this
> case specifically (vs a bad account number).

**Q4. Checked vs unchecked exception — which did you use and why?**
> I extended `RuntimeException` (unchecked) to keep signatures clean. If I wanted to force
> callers to handle it at compile time, I'd extend `Exception` (checked). Trade-off:
> compile-time safety vs boilerplate.

**Q5. Why store both previousBalance and resultingBalance on a Transaction?**
> History should be a frozen snapshot. Storing the result means it can never be re-derived
> differently later (e.g., if fee logic changes). No setters → the record is immutable.

**Q6. Why a `Map<Integer, Account>` and not a `List<Account>`?**
> We look accounts up by account number. A HashMap gives O(1) lookup; a list would be O(n)
> scan. The key (account number) is exactly what a map is for.

**Q7. How do you guarantee unique account numbers?**
> `nextAccountId` only increments, so numbers never repeat. For concurrency I'd use
> `AtomicInteger` or synchronize `openAccount`.

**Q8. How would you implement a transfer between two accounts?**
> `transfer(from, to, amount)` = `debit(from, amount)` then `credit(to, amount)`. To keep
> it atomic (no money lost if step 2 fails), wrap in a try/rollback or a transaction lock,
> and add a `counterpartyAccount` field on Transaction for the audit trail.

**Q9. Is this thread-safe? What breaks under concurrency?**
> No. Two threads debiting the same account can both pass the balance check before either
> subtracts (a race → overdraft). Fix: synchronize per-account operations, or use atomic
> compare-and-set on the balance.

**Q10. How would you add savings vs checking accounts?**
> This is where inheritance fits: make `Account` a base class and `SavingsAccount` /
> `CheckingAccount` subclasses overriding rules (e.g., checking allows a small overdraft,
> savings adds interest). First real is-a in our problem set.

**Q11. What happens if someone debits/credits a non-existent account?**
> `getAccount` throws `IllegalArgumentException("No account with number ...")` — fail fast
> with a clear message instead of a NullPointerException.

**Q12. How would you add interest?**
> `applyInterest(rate)` on Account (or SavingsAccount): compute interest on balance, credit
> it, and record it as a CREDIT transaction so it appears in history.

**Q13. How would you persist accounts and history?**
> Introduce a `Repository` interface (save/load) with a DB or file implementation; the bank
> depends on the interface, not the storage (Repository pattern).

**Q14. How do you handle different currencies?**
> Add a `currency` field; never mix currencies in arithmetic; convert via an exchange-rate
> service at transfer time. Keep each account single-currency for simplicity.

## Things to Remember for Interview
- Lead with: money = `long` cents; logic on Account; manager routes.
- Name the patterns: custom exception, immutable snapshot history, defensive copy, Map for O(1) lookup.
- Be ready for: transfer (atomicity), concurrency (race on balance), inheritance (savings/checking).
- Tie back to Problem 1: Account→Transaction is the same owner+history pattern as Tracker→Expense.
