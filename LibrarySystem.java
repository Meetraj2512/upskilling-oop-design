import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Library Management System — single-file solution.
 *
 * Design (from the playbook):
 *   Library (manager) --owns--> Book --owns many--> BookCopy
 *                      --owns--> Member
 *                      --owns--> Loan   (the JOIN entity for Member <-> BookCopy)
 *
 *   You borrow a BookCopy (a physical thing), not a Book (the title).
 *   The due date lives on the Loan, because it belongs to the *relationship*.
 *
 * Run:  javac LibrarySystem.java && java LibrarySystem
 */
public class LibrarySystem {
    public static void main(String[] args) {
        Library library = new Library();

        // Catalog: 2 copies of Potter, 1 of DSA
        library.addBook("Harry Potter", "J.K. Rowling", "ISBN-HP", 2);
        library.addBook("Intro to Algorithms", "CLRS", "ISBN-DSA", 1);

        int alice = library.registerMember("Alice");
        int bob   = library.registerMember("Bob");

        Loan l1 = library.borrowBook(alice, "ISBN-HP");   // Alice gets a Potter copy
        Loan l2 = library.borrowBook(bob,   "ISBN-HP");   // Bob gets the other one
        System.out.println("Alice borrowed: " + l1);
        System.out.println("Bob   borrowed: " + l2);

        // No copies left -> should fail clearly
        try {
            library.borrowBook(alice, "ISBN-HP");
        } catch (IllegalStateException e) {
            System.out.println("Blocked: " + e.getMessage());
        }

        // Alice returns her copy, then Bob can... already has one; let Alice take DSA
        library.returnBook(l1.getLoanId());
        System.out.println("Alice returned loan #" + l1.getLoanId());

        Loan l3 = library.borrowBook(alice, "ISBN-DSA");
        System.out.println("Alice borrowed: " + l3);

        System.out.println("\nAlice active loans: " + library.getActiveLoans(alice).size());
    }
}

/** A physical copy's state. */
enum CopyStatus {
    AVAILABLE, BORROWED
}

/**
 * Book — the TITLE (one logical book), plus all its physical copies.
 * Multiple BookCopy objects, one Book.
 */
class Book {
    private String title;
    private String author;
    private String isbn;
    private List<BookCopy> copies;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.copies = new ArrayList<>();
    }

    public void addCopy(BookCopy copy) { copies.add(copy); }

    /** First copy that is free to borrow, or null if all are out. */
    public BookCopy findAvailableCopy() {
        for (BookCopy c : copies) {
            if (c.getStatus() == CopyStatus.AVAILABLE) {
                return c;
            }
        }
        return null;
    }

    public String getTitle()  { return title; }
    public String getAuthor() { return author; }
    public String getIsbn()   { return isbn; }
    public List<BookCopy> getCopies() { return new ArrayList<>(copies); }
}

/**
 * BookCopy — one physical instance you actually borrow. Knows its Book and its status.
 */
class BookCopy {
    private int copyId;
    private Book book;
    private CopyStatus status;

    public BookCopy(int copyId, Book book) {
        this.copyId = copyId;
        this.book = book;
        this.status = CopyStatus.AVAILABLE;
    }

    public void markBorrowed()  { this.status = CopyStatus.BORROWED; }
    public void markAvailable() { this.status = CopyStatus.AVAILABLE; }

    public int getCopyId()      { return copyId; }
    public Book getBook()       { return book; }
    public CopyStatus getStatus() { return status; }
}

/**
 * Member — a registered borrower. Tracks the loans they currently hold (for the limit).
 */
class Member {
    private int id;
    private String name;
    private List<Loan> activeLoans;

    public Member(int id, String name) {
        this.id = id;
        this.name = name;
        this.activeLoans = new ArrayList<>();
    }

    public void addLoan(Loan loan)    { activeLoans.add(loan); }
    public void removeLoan(Loan loan) { activeLoans.remove(loan); }

    public int getId()     { return id; }
    public String getName(){ return name; }
    public List<Loan> getActiveLoans() { return new ArrayList<>(activeLoans); }
}

/**
 * Loan — the JOIN entity. Represents "this member borrowed this copy" and is the
 * natural home for borrowDate / dueDate / returnDate.
 */
class Loan {
    private int loanId;
    private Member member;
    private BookCopy copy;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;   // null until returned

    public Loan(int loanId, Member member, BookCopy copy, LocalDate borrowDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.member = member;
        this.copy = copy;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public void markReturned(LocalDate when) { this.returnDate = when; }

    /** Overdue if it's past the due date and not yet returned (or returned late). */
    public boolean isOverdue(LocalDate today) {
        LocalDate effective = (returnDate != null) ? returnDate : today;
        return effective.isAfter(dueDate);
    }

    public int getLoanId()       { return loanId; }
    public Member getMember()    { return member; }
    public BookCopy getCopy()    { return copy; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate(){ return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    @Override
    public String toString() {
        return "Loan#" + loanId + " | " + member.getName() + " | "
             + copy.getBook().getTitle() + " (copy " + copy.getCopyId() + ")"
             + " | due " + dueDate;
    }
}

/**
 * Library — the manager. Owns the catalog, members, and loans, and enforces the rules.
 */
class Library {
    private static final int MAX_BOOKS_PER_MEMBER = 3;
    private static final int LOAN_PERIOD_DAYS = 14;

    private Map<String, Book> booksByIsbn;   // isbn -> Book
    private Map<Integer, Member> members;    // memberId -> Member
    private Map<Integer, Loan> loans;        // loanId -> Loan
    private int nextMemberId = 1;
    private int nextCopyId   = 1;
    private int nextLoanId   = 1;

    public Library() {
        this.booksByIsbn = new HashMap<>();
        this.members = new HashMap<>();
        this.loans = new HashMap<>();
    }

    /** Add a book with `numCopies` physical copies. */
    public void addBook(String title, String author, String isbn, int numCopies) {
        Book book = booksByIsbn.get(isbn);
        if (book == null) {
            book = new Book(title, author, isbn);
            booksByIsbn.put(isbn, book);
        }
        for (int i = 0; i < numCopies; i++) {
            book.addCopy(new BookCopy(nextCopyId++, book));
        }
    }

    public int registerMember(String name) {
        int id = nextMemberId++;
        members.put(id, new Member(id, name));
        return id;
    }

    /** Borrow an available copy of a book. Enforces the per-member limit. */
    public Loan borrowBook(int memberId, String isbn) {
        Member member = requireMember(memberId);
        Book book = booksByIsbn.get(isbn);
        if (book == null) {
            throw new IllegalArgumentException("No book with ISBN " + isbn);
        }
        if (member.getActiveLoans().size() >= MAX_BOOKS_PER_MEMBER) {
            throw new IllegalStateException(member.getName() + " already holds the max of "
                                            + MAX_BOOKS_PER_MEMBER + " books");
        }
        BookCopy copy = book.findAvailableCopy();
        if (copy == null) {
            throw new IllegalStateException("No available copy of '" + book.getTitle() + "'");
        }

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(nextLoanId++, member, copy, today, today.plusDays(LOAN_PERIOD_DAYS));
        copy.markBorrowed();
        member.addLoan(loan);
        loans.put(loan.getLoanId(), loan);
        return loan;
    }

    /** Return a borrowed copy via its loan id. */
    public void returnBook(int loanId) {
        Loan loan = loans.get(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("No loan with id " + loanId);
        }
        loan.getCopy().markAvailable();
        loan.markReturned(LocalDate.now());
        loan.getMember().removeLoan(loan);
    }

    public List<Loan> getActiveLoans(int memberId) {
        return requireMember(memberId).getActiveLoans();
    }

    private Member requireMember(int memberId) {
        Member m = members.get(memberId);
        if (m == null) {
            throw new IllegalArgumentException("No member with id " + memberId);
        }
        return m;
    }
}
