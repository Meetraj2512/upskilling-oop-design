/* ============================================================================
 *  YOUR ORIGINAL ATTEMPT (commented out for reference — good foundation!)
 *  Notes: needed (1) a transaction list ON the Account, (2) a full BankManager
 *  that owns accounts + nextId. You put the history on BankManager; it actually
 *  belongs on each Account (history is per-account). See the solution below.
 * ----------------------------------------------------------------------------
 *  import java.util.*;
 *
 *  public class BankSystem {
 *      void main(String[] args) {
 *      }
 *  }
 *
 *  enum TRANSACTION_TYPE { CREDIT, DEBIT }
 *
 *  class Account {
 *      private int id;
 *      private long amount;
 *      private String ownerName;
 *      public Account(int id, long amount, String ownerName) {
 *          this.id = id; this.amount = amount; this.ownerName = ownerName;
 *      }
 *      public int getId() { return id; }
 *      public long getAmount() { return amount; }
 *      public String getOwnerName() { return ownerName; }
 *  }
 *
 *  class Transaction {
 *      private long previousBalance;
 *      private TRANSACTION_TYPE type;
 *      private long transactionAmount;
 *      private Date timestamp;
 *      public Transaction(long previousBalance, TRANSACTION_TYPE type,
 *                         long transactionAmount, Date timestamp) {
 *          this.previousBalance = previousBalance; this.type = type;
 *          this.transactionAmount = transactionAmount; this.timestamp = timestamp;
 *      }
 *      public long getPreviousBalance() { return previousBalance; }
 *      public TRANSACTION_TYPE getTransactionType() { return type; }
 *      public long getTransactionAmount() { return transactionAmount; }
 *      public long getCurrentBalance() {
 *          if (type.equals(TRANSACTION_TYPE.CREDIT))
 *              return previousBalance + transactionAmount;
 *          else return previousBalance - transactionAmount;
 *      }
 *      public Date getTransactionTimeStamp() { return timestamp; }
 *  }
 *
 *  class BankManager {
 *      private List<Transaction> transactionHistory;   // <- belongs on Account
 *  }
 * ==========================================================================*/


/* ============================================================================
 *  COMPLETE SOLUTION
 *
 *  Design (from our phases):
 *    BankManager  -> owns many Accounts        (one-to-many)
 *    Account      -> owns many Transactions    (one-to-many)  == ExpenseTracker -> Expense
 *
 *  Money is stored as `long` cents (so $9.61 is 961) for EXACT arithmetic.
 *  Run:  javac BankSystem.java && java BankSystem
 * ==========================================================================*/
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankSystem {
    public static void main(String[] args) {
        BankManager bank = new BankManager();

        // Open two accounts (initial balances in CENTS: 50000 = $500.00)
        int alice = bank.openAccount("Alice", 50000);
        int bob   = bank.openAccount("Bob", 0);

        bank.credit(alice, 25000);   // +$250.00
        bank.debit(alice, 10000);    // -$100.00
        bank.credit(bob, 5000);      // +$50.00

        // Try to overdraw Bob — this should be blocked
        try {
            bank.debit(bob, 999999);
        } catch (InsufficientFundsException e) {
            System.out.println("Blocked: " + e.getMessage());
        }

        System.out.println("\nAlice balance: " + Money.format(bank.getBalance(alice)));
        System.out.println("Bob   balance: " + Money.format(bank.getBalance(bob)));

        System.out.println("\nAlice history:");
        for (Transaction t : bank.getHistory(alice)) {
            System.out.println("  " + t);
        }
    }
}

/** CREDIT = money in, DEBIT = money out. Type-safe fixed set. */
enum TransactionType {
    CREDIT, DEBIT
}

/** Custom exception thrown when a debit would push the balance below zero. */
class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

/** Tiny helper to turn cents (long) into a "$d.cc" string for display only. */
class Money {
    public static String format(long cents) {
        return String.format("$%d.%02d", cents / 100, Math.abs(cents % 100));
    }
}

/**
 * Transaction — one immutable credit/debit event (history never changes).
 * Stores both previousBalance and resultingBalance so history is a frozen snapshot.
 */
class Transaction {
    private TransactionType type;
    private long amount;            // cents
    private long previousBalance;   // cents, before this event
    private long resultingBalance;  // cents, after this event (snapshot)
    private LocalDateTime timestamp;

    public Transaction(TransactionType type, long amount, long previousBalance,
                       long resultingBalance, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.previousBalance = previousBalance;
        this.resultingBalance = resultingBalance;
        this.timestamp = timestamp;
    }

    public TransactionType getType()  { return type; }
    public long getAmount()           { return amount; }
    public long getPreviousBalance()  { return previousBalance; }
    public long getResultingBalance() { return resultingBalance; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return timestamp + " | " + type + " | " + Money.format(amount)
             + " | balance: " + Money.format(previousBalance)
             + " -> " + Money.format(resultingBalance);
    }
}

/**
 * Account — owns the balance and its own transaction history.
 * The credit/debit LOGIC lives here because the balance (the data) lives here.
 */
class Account {
    private int accountNumber;
    private String ownerName;
    private long balance;                  // cents
    private List<Transaction> transactions;

    public Account(int accountNumber, String ownerName, long initialBalance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }

    /** Money in. Records a CREDIT transaction. */
    public void credit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        long prev = balance;
        balance += amount;
        transactions.add(new Transaction(TransactionType.CREDIT, amount, prev, balance,
                                         LocalDateTime.now()));
    }

    /** Money out. Blocks overdraft. Records a DEBIT transaction. */
    public void debit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(
                "Insufficient funds: balance " + Money.format(balance)
                + ", tried to debit " + Money.format(amount));
        }
        long prev = balance;
        balance -= amount;
        transactions.add(new Transaction(TransactionType.DEBIT, amount, prev, balance,
                                         LocalDateTime.now()));
    }

    public int getAccountNumber() { return accountNumber; }
    public String getOwnerName()  { return ownerName; }
    public long getBalance()      { return balance; }

    /** Defensive copy so callers can't mutate our real history list. */
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
}

/**
 * BankManager — the manager class. Owns all accounts (keyed by account number)
 * and routes each operation to the right account.
 */
class BankManager {
    private Map<Integer, Account> accounts;   // accountNumber -> Account
    private int nextAccountId;

    public BankManager() {
        this.accounts = new HashMap<>();
        this.nextAccountId = 1;
    }

    /** Open a new account; returns its account number. */
    public int openAccount(String ownerName, long initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        int accNum = nextAccountId++;
        accounts.put(accNum, new Account(accNum, ownerName, initialBalance));
        return accNum;
    }

    public void credit(int accountNumber, long amount) {
        getAccount(accountNumber).credit(amount);
    }

    public void debit(int accountNumber, long amount) {
        getAccount(accountNumber).debit(amount);
    }

    public long getBalance(int accountNumber) {
        return getAccount(accountNumber).getBalance();
    }

    public List<Transaction> getHistory(int accountNumber) {
        return getAccount(accountNumber).getTransactions();
    }

    /** Look up an account or fail clearly. O(1) thanks to the HashMap. */
    private Account getAccount(int accountNumber) {
        Account acc = accounts.get(accountNumber);
        if (acc == null) {
            throw new IllegalArgumentException("No account with number " + accountNumber);
        }
        return acc;
    }
}
