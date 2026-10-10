# Object Relationships & Design - RideWise

## Relationship Types

### Association
**Definition:** A "has-a" or "uses-a" relationship between objects
- Objects are independent and can exist without each other
- Lifetime management is independent

**In RideWise:**
- `Ride` --→ `Rider` (Association: Ride uses/has Rider)
- `Ride` --→ `Driver` (Association: Ride uses/has Driver)

### Composition
**Definition:** A "part-of" relationship where the part cannot exist without the whole
- Child objects cannot exist independently
- Lifetime is tied to parent

**In RideWise:**
- `RideService` ⟹ `RideMatchingStrategy` (Composition: RideService must have a strategy)
- `RideService` ⟹ `FareStrategy` (Composition: RideService must have a strategy)
- `Ride` ⟹ `FareReceipt` (Composition: Generated after ride completion)

### Dependency
**Definition:** One class depends on another for its functionality
- No ownership implied
- Usually shown as a dashed line

**In RideWise:**
- `RideService` ← `RideMatchingStrategy` (Dependency Injection)
- `RideService` ← `FareStrategy` (Dependency Injection)

---

## Entity Relationship Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                       RideService                             │
│                  (Orchestrates Lifecycle)                     │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│ Composition (injected at construction):                       │
│ ├─ matchingStrategy: RideMatchingStrategy                    │
│ ├─ fareStrategy: FareStrategy                                │
│ ├─ driverService: DriverService                              │
│ └─ riderService: RiderService                                │
│                                                                │
│ Storage:                                                       │
│ └─ rides: Map<String, Ride>                                  │
│                                                                │
│ Key Operations:                                                │
│ ├─ requestRide(Ride): Ride                                    │
│ ├─ completeRide(String): FareReceipt                          │
│ ├─ cancelRide(String): void                                   │
│ └─ getRideById(String): Ride                                  │
└──────────────────────────────────────────────────────────────┘
     │                    │                    │
     │ Uses               │ Uses               │ Uses
     ▼                    ▼                    ▼
┌──────────────────┐  ┌──────────────────┐  ┌─────────────────┐
│ DriverService    │  │ RiderService     │  │ Strategies      │
├──────────────────┤  ├──────────────────┤  ├─────────────────┤
│ Storage:         │  │ Storage:         │  │                 │
│ - drivers: Map   │  │ - riders: Map    │  │ Matching:       │
│                  │  │                  │  │ - Nearest       │
│ Methods:         │  │ Methods:         │  │ - LeastActive   │
│ - register       │  │ - register       │  │                 │
│ - getAvailable   │  │ - getRiderById   │  │ Fare:           │
│ - update         │  │ - exists         │  │ - Default       │
│   Availability   │  │                  │  │ - PeakHour      │
└──────────────────┘  └──────────────────┘  └─────────────────┘
     │                       │
     │ Has                    │ Has
     ▼                        ▼
┌──────────────────┐  ┌──────────────────┐
│ Driver           │  │ Rider            │
├──────────────────┤  ├──────────────────┤
│ - id             │  │ - id             │
│ - name           │  │ - name           │
│ - location       │  │ - location       │
│ - vehicleType    │  │ - rating         │
│ - available      │  │                  │
│ - rating         │  │                  │
└──────────────────┘  └──────────────────┘
           │                  │
           │                  │
           └──────────┬───────┘
                      │ Has
                      ▼
            ┌─────────────────────┐
            │ Ride                │
            ├─────────────────────┤
            │ - id                │
            │ - rider: Rider ──┐  │
            │ - driver: Driver ┘  │
            │ - distance          │
            │ - fare              │
            │ - status            │
            │ - createdAt         │
            └─────────────────────┘
                      │
                      │ Has (1-1)
                      ▼
            ┌─────────────────────┐
            │ FareReceipt         │
            ├─────────────────────┤
            │ - rideId            │
            │ - amount            │
            │ - generatedAt       │
            └─────────────────────┘
```

---

## Detailed Relationships

### 1. RideService ↔ RideMatchingStrategy

**Type:** Composition / Dependency Injection  
**Direction:** RideService → RideMatchingStrategy (interface)  
**Multiplicity:** 1 RideService : 1 RideMatchingStrategy  
**Lifetime:** Strategy lifetime managed with RideService  
**Implementation:**
```java
public RideService(DriverService ds, RiderService rs,
                   RideMatchingStrategy matchingStrategy,
                   FareStrategy fareStrategy) {
    this.matchingStrategy = matchingStrategy;  // Injected
}

// Usage in requestRide()
Driver driver = matchingStrategy.findDriver(ride.getRider(), availableDrivers);
```

**Design Notes:**
- **OCP:** Can inject NearestDriverStrategy or LeastActiveDriverStrategy
- **DIP:** RideService depends on interface, not concrete class
- **LSP:** Both implementations are interchangeable
- **ISP:** Strategy has only the method it needs

### 2. RideService ↔ FareStrategy

**Type:** Composition / Dependency Injection  
**Direction:** RideService → FareStrategy (interface)  
**Multiplicity:** 1 RideService : 1 FareStrategy  
**Lifetime:** Strategy lifetime managed with RideService  
**Implementation:**
```java
public RideService(DriverService ds, RiderService rs,
                   RideMatchingStrategy matchingStrategy,
                   FareStrategy fareStrategy) {
    this.fareStrategy = fareStrategy;  // Injected
}

// Usage in completeRide()
double totalFare = fareStrategy.calculateFare(ride);
```

**Design Notes:**
- **OCP:** Can inject DefaultFareStrategy or PeakHourFareStrategy
- **DIP:** RideService depends on interface, not concrete class
- **Runtime Switching:** Main.java can switch strategies during execution

### 3. RideService ↔ DriverService

**Type:** Dependency / Composition  
**Direction:** RideService → DriverService  
**Multiplicity:** 1 RideService : 1 DriverService  
**Purpose:** Query available drivers and update availability  
**Methods Used:**
```java
List<Driver> drivers = driverService.getAvailableDrivers();
driverService.updateDriverAvailability(driverId, available);
```

**Design Notes:**
- **Law of Demeter:** RideService calls methods directly on DriverService
- **No Deep Chains:** Correct: `driverService.updateDriverAvailability()`
- **No Deep Chains:** Wrong: `ride.getDriver().getService().updateAvailability()`

### 4. RideService ↔ RiderService

**Type:** Dependency / Composition  
**Direction:** RideService → RiderService  
**Multiplicity:** 1 RideService : 1 RiderService  
**Purpose:** Validate rider existence  
**Methods Used:**
```java
if (!riderService.riderExists(riderId)) {
    // Handle error
}
```

### 5. Ride ↔ Rider

**Type:** Association / Composition  
**Direction:** Ride ← Rider  
**Multiplicity:** Many Rides : One Rider  
**Lifetime:** Ride references Rider (Rider can exist without Ride)  
**Association:**
```java
public class Ride {
    private Rider rider;  // Association
    
    public Rider getRider() { return rider; }
}
```

**Design Notes:**
- **Not Ownership:** Rider exists independently
- **Valid Relationship:** Rider can have multiple rides
- **Law of Demeter:** 
  - Correct: `ride.getRider().getName()`
  - These are direct attributes, acceptable

### 6. Ride ↔ Driver

**Type:** Association / Composition (before assignment, it's nullable)  
**Direction:** Ride ← Driver  
**Multiplicity:** Many Rides : One Driver  
**Lifetime:** Ride references Driver (Driver can exist without Ride)  
**State Transitions:**
```java
// State 1: REQUESTED (driver is null)
Ride ride = new Ride(...);
ride.getDriver();  // Returns null

// State 2: ASSIGNED (driver is assigned)
ride.setDriver(driver);
ride.getDriver();  // Returns driver

// State 3: COMPLETED
ride.setStatus(RideStatus.COMPLETED);
```

**Design Notes:**
- **Nullable Reference:** Represents "not yet assigned" state
- **Encapsulation:** Driver reference managed by Ride
- **No Direct Modification:** Only through setDriver()

### 7. Ride ↔ FareReceipt

**Type:** Composition / Association  
**Direction:** Ride → FareReceipt  
**Multiplicity:** 1 Ride : 1 FareReceipt  
**Lifetime:** FareReceipt created at ride completion  
**Creation:**
```java
FareReceipt receipt = new FareReceipt(ride.getId(), totalFare);
// FareReceipt carries ride information, not the reverse
```

**Design Notes:**
- **Not Back Reference:** FareReceipt doesn't reference Ride object
- **Data Transfer:** Receipt is a summary, not composition
- **Separation of Concerns:** Ride and FareReceipt are independent

### 8. RideService ↔ Ride Collection

**Type:** Aggregation / Storage  
**Direction:** RideService → Ride  
**Multiplicity:** RideService : Many Rides  
**Storage:**
```java
private Map<String, Ride> rides = new HashMap<>();

public Ride getRideById(String rideId) {
    return rides.get(rideId);
}
```

**Design Notes:**
- **In-Memory Storage:** Rides stored in Map for quick lookup
- **Encapsulation:** Direct access prevented
- **Access Control:** Only through getter methods

### 9. DriverService ↔ Driver Collection

**Type:** Aggregation / Storage  
**Direction:** DriverService → Driver  
**Multiplicity:** DriverService : Many Drivers  
**Storage & Access:**
```java
private Map<String, Driver> drivers = new HashMap<>();

public List<Driver> getAvailableDrivers() {
    List<Driver> available = new ArrayList<>();
    for (Driver driver : drivers.values()) {
        if (driver.isAvailable()) {
            available.add(driver);
        }
    }
    return available;
}
```

**Design Notes:**
- **Filtering Logic:** Encapsulated in service
- **Not Exposing Internal Storage:** Returns List, not Map
- **Defensive Copy:** Prevents external modification

### 10. RiderService ↔ Rider Collection

**Type:** Aggregation / Storage  
**Direction:** RiderService → Rider  
**Multiplicity:** RiderService : Many Riders  
**Storage:**
```java
private Map<String, Rider> riders = new HashMap<>();

public Rider getRiderById(String riderId) {
    return riders.get(riderId);
}
```

---

## Data Flow Diagrams

### Flow 1: Request a Ride

```
User Input (Main)
    ↓
Validate Rider Exists (RiderService)
    ↓
Create Ride (new Ride())
    ↓
Request Ride (RideService.requestRide())
    ├─ Get Available Drivers (DriverService)
    ├─ Match Driver (RideMatchingStrategy)
    ├─ Assign Driver (ride.setDriver())
    ├─ Update Status (ride.setStatus(ASSIGNED))
    ├─ Update Driver Availability (DriverService)
    └─ Store Ride (rides Map)
    ↓
Return Ride with Assigned Driver
    ↓
Display to User (Main)
```

### Flow 2: Complete a Ride

```
User Input - Ride ID (Main)
    ↓
Get Ride (RideService.getRideById())
    ↓
Complete Ride (RideService.completeRide())
    ├─ Validate Ride Status
    ├─ Calculate Fare (FareStrategy)
    ├─ Update Ride (ride.setFare(), ride.setStatus(COMPLETED))
    ├─ Update Driver Availability (DriverService)
    └─ Generate Receipt (new FareReceipt())
    ↓
Return FareReceipt
    ↓
Display to User (Main)
```

### Flow 3: Strategy Switching

```
User selects different strategy (Main)
    ↓
Create New Strategy Instance
    ├─ new NearestDriverStrategy() or similar
    ├─ new DefaultFareStrategy() or similar
    └─ new RideService(..., newStrategy, ...) // New instance
    ↓
Future rides use new strategy
```

---

## Law of Demeter Compliance

**Definition:** A method should only call methods on objects that are:
1. Passed as parameters
2. Created locally
3. Field variables
4. Global variables

**Violations to Avoid:**
```java
// WRONG: Deep chaining (violates Law of Demeter)
ride.getDriver().getLocation().getValue().trim();

// WRONG: Accessing nested objects indirectly
service.getRide().getRider().getLocation();
```

**Correct Application in RideWise:**
```java
// CORRECT: Direct method calls
String driverLocation = driver.getCurrentLocation();
driverService.updateDriverLocation(driverId, newLocation);

// CORRECT: One level of indirection acceptable for entities
String riderName = ride.getRider().getName();  // ride.rider is composed

// CORRECT: Service calls other services' public methods
driverService.updateDriverAvailability(driverId, false);
```

---

## Summary of Design Principles

| Aspect | Implementation | Benefit |
|--------|-----------------|---------|
| **Composition** | Strategies injected, not inherited | OCP, DIP, LSP compliance |
| **Association** | Ride references Rider/Driver | Independent entity lifecycles |
| **Aggregation** | Services manage collections | Easy lookup, filtering, updates |
| **Encapsulation** | Private fields, public methods | Controlled access, maintainability |
| **Law of Demeter** | Direct method calls only | Low coupling, flexibility |
| **Dependency Injection** | Strategies passed to constructor | Testability, runtime flexibility |
| **Interfaces** | RideMatchingStrategy, FareStrategy | Extensibility, substitutability |
| **Storage** | Map-based repositories | O(1) lookup, scalability |

