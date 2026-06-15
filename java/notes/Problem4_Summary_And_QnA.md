# Problem: Parking Lot System — Full Summary & Interview Q&A

## Key Concepts Covered
- **Inheritance + polymorphism** — the showcase of this problem.
- An **abstract base class** (`Vehicle`) with subclasses overriding behavior.
- Using `enum` ordinal for a natural ordering ("fits in" check).
- Manager assigns resources (spots) and a `Ticket` joins vehicle + spot + time.

## What We Built (the design)
```
ParkingLot (manager)
   --owns many--> ParkingSpot
   --tracks-->     Ticket  (vehicle + spot + entry/exit times)
Vehicle (abstract) <- Motorcycle | Car | Truck
SpotSize (enum: SMALL < MEDIUM < LARGE)
```
- **Vehicle** (abstract): `getRequiredSpotSize()` and `getHourlyRateCents()` — abstract,
  answered differently by each subclass (polymorphism).
- **ParkingSpot**: id, size, current vehicle; `canFit(vehicle)` = free AND big enough.
- **Ticket**: id, vehicle, spot, entryTime, exitTime; `calculateFee()` = ceil(hours) * rate.
- **ParkingLot**: `List<ParkingSpot>`, `Map<Integer,Ticket>` active tickets; `park`, `leave`,
  `countAvailable`, private `findSpot`.

## Why We Made These Decisions
- **Abstract `Vehicle` + subclasses:** this is a real "is-a" — Motorcycle/Car/Truck *are* vehicles
  with different sizes and rates. Putting `getRequiredSpotSize()`/`getHourlyRateCents()` as
  abstract methods lets the lot treat all vehicles uniformly while each type decides its own rule.
  Adding a new vehicle type = one new subclass, no changes to ParkingLot (Open/Closed Principle).
- **`SpotSize` ordinal for "fits in":** SMALL<MEDIUM<LARGE, so `spot.size.ordinal() >=
  vehicle.requiredSize.ordinal()` means a bigger spot can hold a smaller vehicle. Smallest-first
  spot list means we don't waste a large spot on a motorcycle.
- **Ticket joins vehicle+spot+times** and is the home for fee calculation (the data it needs is there).
- **Money as `long` cents**; fee = ceil(hours) * the vehicle's polymorphic rate, min 1 hour.

## Complexity (n = number of spots)
| Operation | Time |
|-----------|------|
| park | O(n) to find a fitting spot |
| leave | O(1) (ticket id lookup) |
| countAvailable | O(n) |
> park can be made O(1) by keeping separate free-spot queues per SpotSize and polling
> from the smallest that fits.

## Interview Q&A

**Q1. Why an abstract Vehicle class instead of a `type` enum field?**
> Each type has different *behavior* (required size, rate), not just a label. Abstract methods +
> subclasses let each type own its rule, and adding a new type needs no change to ParkingLot.
> An enum is fine if behavior is trivial; here behavior differs, so inheritance wins.

**Q2. What is polymorphism here, concretely?**
> `ParkingLot` calls `vehicle.getRequiredSpotSize()` / `getHourlyRateCents()` without knowing the
> concrete type. At runtime the correct subclass method runs. One call site, many behaviors.

**Q3. How does spot assignment work / why smallest-first?**
> The spot list is ordered small->large and `findSpot` returns the first that fits, so a car won't
> occupy a large spot while mediums are free. To make it O(1), keep per-size free lists.

**Q4. How is the fee computed?**
> `Duration` between entry and exit -> ceil to whole hours (min 1) -> times the vehicle's hourly
> rate (polymorphic). Different vehicles cost different amounts via their overridden rate.

**Q5. How would you support multiple floors/levels?**
> Add a `ParkingFloor` entity owning its spots; `ParkingLot` owns floors and delegates `findSpot`
> across them. Another level of one-to-many, same pattern.

**Q6. Handicapped / EV / reserved spots?**
> Extend `SpotSize` into a richer `SpotType`, or add flags on `ParkingSpot`; `canFit` checks both
> size and eligibility. Vehicles could expose `needsCharging()` etc.

**Q7. How to find a free spot in O(1) instead of scanning?**
> Maintain a free-spot structure per size (e.g. a stack/queue of free spots). park() pops from the
> smallest size that fits; leave() pushes the freed spot back. Trades a little bookkeeping for speed.

**Q8. Concurrency — two cars taking the last spot?**
> Synchronize the find-and-claim, or use an atomic transition on the spot's occupancy so only one
> thread can take a given spot. The claim must be atomic with the availability check.

**Q9. Lost ticket / pay by license plate?**
> Add a `Map<String, Ticket>` keyed by license plate alongside the ticket-id map, so exit can be
> looked up either way; charge a lost-ticket flat fee if needed.

## Things to Remember for Interview
- Lead with the **abstract Vehicle + polymorphic methods** — that's what this problem tests.
- Say "adding a vehicle type = one subclass, no ParkingLot change" (Open/Closed Principle).
- Mention the O(1) free-list optimization and concurrency on spot claiming.
- Ticket is the join entity (vehicle + spot + time) and owns fee calculation.
