import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parking Lot System — single-file solution.
 *
 * Showcase problem for INHERITANCE + POLYMORPHISM:
 *   Vehicle (abstract) <- Motorcycle / Car / Truck
 *   Each subclass answers two questions polymorphically:
 *     - what spot size do I need?   (getRequiredSpotSize)
 *     - what is my hourly rate?     (getHourlyRateCents)
 *
 * Design:
 *   ParkingLot (manager) --owns many--> ParkingSpot
 *                        --tracks-->     Ticket  (join of Vehicle <-> ParkingSpot + times)
 *
 * Money is `long` cents. Run:  javac ParkingLotSystem.java && java ParkingLotSystem
 */
public class ParkingLotSystem {
    public static void main(String[] args) {
        // Build a lot: 1 small, 2 medium, 1 large
        List<ParkingSpot> spots = new ArrayList<>();
        spots.add(new ParkingSpot(1, SpotSize.SMALL));
        spots.add(new ParkingSpot(2, SpotSize.MEDIUM));
        spots.add(new ParkingSpot(3, SpotSize.MEDIUM));
        spots.add(new ParkingSpot(4, SpotSize.LARGE));
        ParkingLot lot = new ParkingLot(spots);

        Ticket t1 = lot.park(new Motorcycle("M-1"));   // -> a SMALL spot
        Ticket t2 = lot.park(new Car("C-1"));          // -> a MEDIUM spot
        Ticket t3 = lot.park(new Truck("T-1"));        // -> the LARGE spot
        System.out.println("Parked: " + t1 + " / " + t2 + " / " + t3);

        lot.park(new Car("C-2"));                      // -> the other MEDIUM spot

        // No spot left that fits a car (small taken, mediums taken, large taken) -> blocked
        try {
            lot.park(new Car("C-3"));
        } catch (IllegalStateException e) {
            System.out.println("Blocked: " + e.getMessage());
        }

        System.out.println("Available spots: " + lot.countAvailable());

        long fee = lot.leave(t1.getTicketId());        // motorcycle leaves
        System.out.println("Motorcycle fee: " + MoneyFmt.format(fee));
        System.out.println("Available spots after exit: " + lot.countAvailable());
    }
}

/** Spot sizes in increasing order. ordinal() is used to compare "fits in". */
enum SpotSize {
    SMALL, MEDIUM, LARGE
}

/** Display-only money helper (cents -> "$d.cc"). */
class MoneyFmt {
    public static String format(long cents) {
        return String.format("$%d.%02d", cents / 100, Math.abs(cents % 100));
    }
}

/**
 * Vehicle — abstract base. Subclasses decide (polymorphically) the spot size they
 * need and their hourly rate. This is the "different types with different rules" => inheritance.
 */
abstract class Vehicle {
    protected String licensePlate;

    protected Vehicle(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() { return licensePlate; }

    /** Smallest spot this vehicle can use. */
    public abstract SpotSize getRequiredSpotSize();

    /** Parking price per hour, in cents. */
    public abstract long getHourlyRateCents();
}

class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) { super(licensePlate); }
    public SpotSize getRequiredSpotSize() { return SpotSize.SMALL; }
    public long getHourlyRateCents()      { return 100; }   // $1.00/hr
}

class Car extends Vehicle {
    public Car(String licensePlate) { super(licensePlate); }
    public SpotSize getRequiredSpotSize() { return SpotSize.MEDIUM; }
    public long getHourlyRateCents()      { return 200; }   // $2.00/hr
}

class Truck extends Vehicle {
    public Truck(String licensePlate) { super(licensePlate); }
    public SpotSize getRequiredSpotSize() { return SpotSize.LARGE; }
    public long getHourlyRateCents()      { return 400; }   // $4.00/hr
}

/**
 * ParkingSpot — one space of a given size. Holds the vehicle currently parked (or null).
 */
class ParkingSpot {
    private int id;
    private SpotSize size;
    private Vehicle vehicle;   // null when free

    public ParkingSpot(int id, SpotSize size) {
        this.id = id;
        this.size = size;
    }

    public boolean isFree() { return vehicle == null; }

    /** A vehicle fits if the spot is free and at least as large as the vehicle needs. */
    public boolean canFit(Vehicle v) {
        return isFree() && size.ordinal() >= v.getRequiredSpotSize().ordinal();
    }

    public void park(Vehicle v) { this.vehicle = v; }
    public void free()          { this.vehicle = null; }

    public int getId()       { return id; }
    public SpotSize getSize(){ return size; }
    public Vehicle getVehicle() { return vehicle; }
}

/**
 * Ticket — issued at entry, settled at exit. Joins a Vehicle to a ParkingSpot and
 * records the timing used to compute the fee.
 */
class Ticket {
    private int ticketId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;   // null until the vehicle leaves

    public Ticket(int ticketId, Vehicle vehicle, ParkingSpot spot, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = entryTime;
    }

    public void markExit(LocalDateTime when) { this.exitTime = when; }

    /** Fee = ceil(hours parked) * the vehicle's hourly rate, minimum 1 hour. */
    public long calculateFee() {
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        long minutes = Duration.between(entryTime, end).toMinutes();
        long hours = Math.max(1, (long) Math.ceil(minutes / 60.0));
        return hours * vehicle.getHourlyRateCents();
    }

    public int getTicketId()     { return ticketId; }
    public Vehicle getVehicle()  { return vehicle; }
    public ParkingSpot getSpot() { return spot; }

    @Override
    public String toString() {
        return "Ticket#" + ticketId + "(" + vehicle.getLicensePlate()
             + " @ spot " + spot.getId() + ")";
    }
}

/**
 * ParkingLot — the manager. Owns all spots, assigns a fitting spot on entry,
 * tracks active tickets, and charges the fee on exit.
 */
class ParkingLot {
    private List<ParkingSpot> spots;
    private Map<Integer, Ticket> activeTickets;
    private int nextTicketId = 1;

    public ParkingLot(List<ParkingSpot> spots) {
        this.spots = new ArrayList<>(spots);
        this.activeTickets = new HashMap<>();
    }

    /** Park a vehicle in the first spot that fits; returns its ticket. */
    public Ticket park(Vehicle vehicle) {
        ParkingSpot spot = findSpot(vehicle);
        if (spot == null) {
            throw new IllegalStateException("No available spot for "
                + vehicle.getClass().getSimpleName() + " " + vehicle.getLicensePlate());
        }
        spot.park(vehicle);
        Ticket ticket = new Ticket(nextTicketId++, vehicle, spot, LocalDateTime.now());
        activeTickets.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    /** Settle a ticket: free the spot and return the fee owed. */
    public long leave(int ticketId) {
        Ticket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("No active ticket " + ticketId);
        }
        ticket.markExit(LocalDateTime.now());
        ticket.getSpot().free();
        return ticket.calculateFee();
    }

    public int countAvailable() {
        int count = 0;
        for (ParkingSpot s : spots) {
            if (s.isFree()) count++;
        }
        return count;
    }

    /** First spot (smallest-first since the list is ordered) that fits the vehicle. */
    private ParkingSpot findSpot(Vehicle vehicle) {
        for (ParkingSpot s : spots) {
            if (s.canFit(vehicle)) return s;
        }
        return null;
    }
}
