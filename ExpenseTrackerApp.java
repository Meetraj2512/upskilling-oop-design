import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Expense Tracker — all classes in one file.
 *
 * Java rule: a file may hold many top-level classes, but only ONE can be
 * `public`,
 * and its name must match the file name (here: ExpenseTrackerApp). The other
 * classes
 * (Category, Expense, ExpenseTracker) are package-private — fine for a
 * single-file demo.
 *
 * Run: javac ExpenseTrackerApp.java && java ExpenseTrackerApp
 */
public class ExpenseTrackerApp {
    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();

        // Add a few expenses
        tracker.addExpense(12.50, Category.FOOD, LocalDate.of(2026, 6, 10), "team lunch");
        tracker.addExpense(40.00, Category.TRAVEL, LocalDate.of(2026, 6, 11), "cab to airport");
        tracker.addExpense(8.75, Category.FOOD, LocalDate.of(2026, 6, 12), "coffee");
        Expense rent = tracker.addExpense(900.0, Category.RENT, LocalDate.of(2026, 6, 1), "June rent");

        System.out.println("All expenses:");
        for (Expense e : tracker.getAllExpenses()) {
            System.out.println("  " + e);
        }

        System.out.println("\nGrand total: $" + tracker.getTotal());
        System.out.println("Food total:  $" + tracker.getTotalByCategory(Category.FOOD));

        System.out.println("\nFood expenses only:");
        for (Expense e : tracker.getExpensesByCategory(Category.FOOD)) {
            System.out.println("  " + e);
        }

        // Remove the rent expense and show the new total
        tracker.removeExpense(rent.getId());
        System.out.println("\nAfter removing rent (#" + rent.getId() + ") — new total: $" + tracker.getTotal());
    }
}

/**
 * Category — a fixed, type-safe set of expense types.
 * Using an enum means only these values are possible (typos become compile
 * errors).
 */
enum Category {
    FOOD, TRAVEL, RENT, SHOPPING, UTILITIES, OTHER
}

/**
 * Expense — one purchase record. A "data class": it mostly describes itself.
 * Fields are private (encapsulation) and there are no setters, so an Expense
 * is effectively immutable once created — a faithful record of something that
 * happened.
 */
class Expense {
    private int id;
    private double amount;
    private Category category;
    private LocalDate date;
    private String description;

    public Expense(int id, double amount, Category category, LocalDate date, String description) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "#" + id + " | " + date + " | " + category + " | $" + amount + " | " + description;
    }
}

/**
 * ExpenseTracker — the "manager" class. It owns the whole collection of
 * expenses
 * (the one-to-many backbone) and exposes all operations that work across many
 * expenses.
 */
class ExpenseTracker {
    private List<Expense> expenses;
    private int nextId;

    public ExpenseTracker() {
        this.expenses = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Create an expense with a fresh unique id, store it, and return it.
     * Time: O(1) amortized (append). Space: O(1).
     */
    public Expense addExpense(double amount, Category category, LocalDate date, String description) {
        Expense e = new Expense(nextId, amount, category, date, description);
        expenses.add(e);
        nextId++;
        return e;
    }

    /**
     * Delete the expense with this id. Returns true if one was removed.
     * Time: O(n) (scan to find it). Space: O(1).
     */
    public void removeExpense(int id) {
        expenses.removeIf(e -> e.getId() == id);
    }

    /**
     * Return a COPY of the list so callers can't mutate our internal collection.
     * Time: O(n) (copy). Space: O(n) (the copy).
     */
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Sum of every expense amount.
     * Time: O(n). Space: O(1).
     */
    public double getTotal() {
        double sum = 0;
        for (Expense e : expenses) {
            sum += e.getAmount();
        }
        return sum;
    }

    /**
     * Sum of amounts for a single category.
     * Time: O(n). Space: O(1).
     */
    public double getTotalByCategory(Category category) {
        double sum = 0;
        for (Expense e : expenses) {
            if (e.getCategory() == category) {
                sum += e.getAmount();
            }
        }
        return sum;
    }

    /**
     * All expenses belonging to one category.
     * Time: O(n). Space: O(k) where k = matches.
     */
    public List<Expense> getExpensesByCategory(Category category) {
        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.getCategory() == category) {
                result.add(e);
            }
        }
        return result;
    }
}

/*
 * ============================================================================
 * REFERENCE ONLY — MULTI-USER EXTENSION (commented out on purpose)
 * ============================================================================
 * Idea (from Phase 5, Q4): today the "User" is just an actor. To support many
 * users, we PROMOTE User to an entity and give each user their own
 * ExpenseTracker. A top-level "ExpenseApp" manager then shards by userId.
 *
 * Nothing about ExpenseTracker/Expense changes — we just put a map in front of
 * it. This is the classic "one-to-many at a higher level" pattern.
 *
 * To actually use this: uncomment the two classes below, then drive it from
 * main() like:
 * ExpenseApp app = new ExpenseApp();
 * int aliceId = app.addUser("Alice");
 * app.trackerFor(aliceId).addExpense(12.5, Category.FOOD,
 * LocalDate.now(), "lunch");
 * System.out.println(app.trackerFor(aliceId).getTotal());
 *
 * ---------------------------------------------------------------------------
 * // A real entity now (was just an "actor" before).
 * class User {
 * private int id;
 * private String name;
 *
 * public User(int id, String name) {
 * this.id = id;
 * this.name = name;
 * }
 *
 * public int getId() { return id; }
 * public String getName() { return name; }
 * }
 *
 * // Top-level manager: owns all users and one ExpenseTracker per user.
 * class ExpenseApp {
 * private Map<Integer, User> users = new HashMap<>(); // userId -> User
 * private Map<Integer, ExpenseTracker> trackers = new HashMap<>(); // userId ->
 * their tracker
 * private int nextUserId = 1;
 *
 * // Register a user and create their personal tracker. O(1).
 * public int addUser(String name) {
 * int id = nextUserId++;
 * users.put(id, new User(id, name));
 * trackers.put(id, new ExpenseTracker()); // each user gets their own
 * return id;
 * }
 *
 * // Look up one user's tracker. O(1) thanks to the HashMap key.
 * public ExpenseTracker trackerFor(int userId) {
 * ExpenseTracker t = trackers.get(userId);
 * if (t == null) {
 * throw new IllegalArgumentException("No such user: " + userId);
 * }
 * return t;
 * }
 *
 * public User getUser(int userId) { return users.get(userId); }
 * }
 *
 * // NOTE: with this, you'd also add import java.util.HashMap; and
 * // import java.util.Map; at the top of the file.
 *
 * WHY a HashMap keyed by userId? -> O(1) "give me Alice's expenses" instead of
 * scanning. Users are looked up BY id, which is exactly what a Map is for.
 * WHY one tracker per user? -> total isolation; Alice can never see Bob's
 * expenses, and all the Problem-1 logic is reused untouched.
 * WHAT IF we skipped this? -> we'd have to tag every Expense with a userId
 * and filter by it on every operation (O(n) and easy to leak across users).
 * ============================================================================
 */
