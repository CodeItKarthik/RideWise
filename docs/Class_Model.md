# Class Model - RideWise

## Entity Classes (Model Package)

### Rider
**Purpose:** Represents a customer requesting rides

**Attributes:**
- `id: String` - Unique identifier (format: RIDER_XXXX)
- `name: String` - Rider's name
- `location: String` - Current location
- `rating: double` - Rider rating (initialized to 5.0)

**Methods:**
- `Rider(id, name, location)` - Constructor
- `getId(), getName(), getLocation(), getRating()` - Getters
- `setLocation(location), setRating(rating)` - Setters
- `toString()` - String representation

**Relationships:**
- Has-a: Ride (one rider can have multiple rides)

### Driver
**Purpose:** Represents a service provider offering rides

**Attributes:**
- `id: String` - Unique identifier (format: DRIVER_XXXX)
- `name: String` - Driver's name
- `currentLocation: String` - Current location
- `vehicleType: VehicleType` - Type of vehicle (BIKE, AUTO, CAR)
- `available: boolean` - Availability status (true/false)
- `rating: double` - Driver rating (initialized to 5.0)

**Methods:**
- `Driver(id, name, location, vehicleType)` - Constructor
- `getId(), getName(), getCurrentLocation(), getVehicleType()` - Getters
- `isAvailable(), getRating()` - State queries
- `setCurrentLocation(location), setAvailable(available), setRating(rating)` - Setters
- `toString()` - String representation

**Relationships:**
- Has-a: Ride (one driver can fulfill multiple rides)

### Ride
**Purpose:** Represents a single ride transaction between rider and driver

**Attributes:**
- `id: String` - Unique identifier (format: RIDE_XXXX)
- `rider: Rider` - Reference to the requesting rider
- `driver: Driver` - Reference to assigned driver (nullable before assignment)
- `distance: double` - Distance in kilometers
- `fare: double` - Calculated fare amount
- `status: RideStatus` - Current lifecycle status
- `createdAt: long` - Creation timestamp

**Methods:**
- `Ride(id, rider, distance)` - Constructor
- `getId(), getRider(), getDriver(), getDistance(), getFare()` - Getters
- `getStatus(), getCreatedAt()` - Status queries
- `setDriver(driver), setFare(fare), setStatus(status)` - Setters
- `toString()` - String representation

**State Transitions:**
```
REQUESTED → ASSIGNED → COMPLETED
   ↓              ↓
   └──→ CANCELLED ←──┘
```

**Relationships:**
- Composed-of: Rider (Association)
- Composed-of: Driver (Association)
- Has-a: FareReceipt (Composition - generated after completion)

### FareReceipt
**Purpose:** Receipt/record generated after ride completion

**Attributes:**
- `rideId: String` - Associated ride ID
- `amount: double` - Fare amount charged
- `generatedAt: long` - Generation timestamp

**Methods:**
- `FareReceipt(rideId, amount)` - Constructor
- `getRideId(), getAmount(), getGeneratedAt()` - Getters
- `toString()` - String representation

**Relationships:**
- References: Ride (one-to-one)

### RideStatus (Enum)
**Purpose:** Defines valid ride states

**Values:**
- `REQUESTED` - Initial state, rider has submitted request
- `ASSIGNED` - Driver matched and assigned to ride
- `COMPLETED` - Ride finished, fare calculated
- `CANCELLED` - Ride cancelled before completion

### VehicleType (Enum)
**Purpose:** Defines vehicle categories

**Values:**
- `BIKE` - Two-wheeler vehicle
- `AUTO` - Three-wheeler vehicle
- `CAR` - Four-wheeler vehicle

## Service Classes (Service Package)

### RiderService
**Purpose:** Manage all rider-related operations (SRP)

**Responsibilities:**
- Register new riders
- Retrieve riders by ID
- Query rider existence

**Internal Structure:**
- `riders: Map<String, Rider>` - In-memory storage

**Methods:**
```java
public void registerRider(Rider rider)
public Rider getRiderById(String riderId)
public boolean riderExists(String riderId)
```

**Design Notes:**
- No wallet management (simplified MVP)
- Encapsulates rider data storage
- Follows SRP - only rider operations

### DriverService
**Purpose:** Manage all driver-related operations (SRP)

**Responsibilities:**
- Register new drivers
- Retrieve drivers by ID
- Query driver availability
- Update driver status and location
- Filter available drivers for matching

**Internal Structure:**
- `drivers: Map<String, Driver>` - In-memory storage

**Methods:**
```java
public void registerDriver(Driver driver)
public Driver getDriverById(String driverId)
public List<Driver> getAvailableDrivers()
public void updateDriverAvailability(String driverId, boolean available)
public void updateDriverLocation(String driverId, String location)
public boolean driverExists(String driverId)
```

**Design Notes:**
- Encapsulates driver data and filtering logic
- Follows SRP - only driver operations
- `getAvailableDrivers()` enables strategy pattern

### RideService
**Purpose:** Orchestrate ride lifecycle and apply strategies (Composition)

**Responsibilities:**
- Create and manage rides
- Match drivers to riders using strategy
- Calculate fares using strategy
- Track ride status
- Handle exceptions

**Internal Structure:**
- `rides: Map<String, Ride>` - In-memory storage
- `driverService: DriverService` - Injected dependency
- `riderService: RiderService` - Injected dependency
- `matchingStrategy: RideMatchingStrategy` - Injected strategy
- `fareStrategy: FareStrategy` - Injected strategy

**Methods:**
```java
public Ride requestRide(Ride ride) throws NoDriverAvailableException
public FareReceipt completeRide(String rideId)
public Ride getRideById(String rideId)
public void cancelRide(String rideId)
public Map<String, Ride> getAllRides()
```

**Design Notes:**
- Demonstrates DIP - depends on interfaces, not concrete classes
- Demonstrates Composition - strategies injected, not inherited
- Demonstrates OCP - new strategies don't require changes to RideService
- Law of Demeter - only calls methods on direct dependencies

## Strategy Classes (Strategy Package)

### RideMatchingStrategy (Interface)
**Purpose:** Abstract contract for driver-to-rider matching algorithms

**Method:**
```java
Driver findDriver(Rider rider, List<Driver> drivers)
```

**Design Principles:**
- ISP - single, focused method
- DIP - RideService depends on this interface
- OCP - new implementations don't modify RideService

**Implementations:**

#### NearestDriverStrategy
- **Algorithm:** Returns first available driver in list
- **Use Case:** Quick matching, minimal complexity
- **Complexity:** O(1)

#### LeastActiveDriverStrategy
- **Algorithm:** Returns driver with fewer total rides
- **Use Case:** Load balancing among drivers
- **Complexity:** O(n)

### FareStrategy (Interface)
**Purpose:** Abstract contract for fare calculation algorithms

**Method:**
```java
double calculateFare(Ride ride)
```

**Design Principles:**
- ISP - single, focused method
- DIP - RideService depends on this interface
- OCP - new implementations don't modify RideService

**Implementations:**

#### DefaultFareStrategy
- **Formula:** BaseFare (50) + (distance × PerKmRate (10))
- **Use Case:** Standard rates during normal hours
- **Example:** 10 km ride = 50 + 100 = 150

#### PeakHourFareStrategy
- **Formula:** (BaseFare (50) + (distance × PerKmRate (15))) × PeakMultiplier (1.2)
- **Use Case:** Premium rates during peak hours
- **Example:** 10 km ride = (50 + 150) × 1.2 = 240

## Exception Classes (Exception Package)

### NoDriverAvailableException
**Purpose:** Thrown when no suitable driver found for ride request

**Extends:** Exception

**Constructors:**
```java
public NoDriverAvailableException(String message)
public NoDriverAvailableException(String message, Throwable cause)
```

**Usage:** RideService.requestRide() throws this when availability check fails

## Utility Classes (Util Package)

### IdGenerator
**Purpose:** Generate unique identifiers for domain entities

**Method:**
```java
public static String generateRiderId()      // RIDER_XXXX
public static String generateDriverId()     // DRIVER_XXXX
public static String generateRideId()       // RIDE_XXXX
```

**Implementation:** Uses `AtomicLong` for thread-safe, monotonic ID generation

## Class Dependency Diagram

```
┌─────────────────────────────────────────────────────┐
│                  Main (Console UI)                   │
│  Entry point, menu handling, input validation       │
└────┬─────────────────────────┬──────────────────────┘
     │                         │
     ▼                         ▼
┌──────────────┐          ┌──────────────────┐
│  RideService │◄─────────│  DriverService   │
│              │          │                  │
│ Uses:        │          │ Manages:         │
│ - Strategy   │          │ - Driver objects │
│ - Services   │          └──────────────────┘
└──────────────┘                   ▲
     │                             │
     │                             │ uses
     │                        ┌────┴─────┐
     │                        │  Driver   │
     │                        │ + Vehicle │
     │                        └───────────┘
     │
     ├──────────────┬─────────────────┬──────────────┐
     │              │                 │              │
     ▼              ▼                 ▼              ▼
┌─────────────────────────────────────────────────────┐
│        Strategies (Injected into RideService)       │
├─────────────────────────────────────────────────────┤
│  RideMatchingStrategy◄──┐                           │
│  ├─ NearestDriver      │                           │
│  └─ LeastActiveDriver  │ Composition               │
│                         │                           │
│  FareStrategy◄──────────┘                           │
│  ├─ DefaultFare                                     │
│  └─ PeakHourFare                                    │
└─────────────────────────────────────────────────────┘
```

## Package Organization

```
ridewise/
├── Main.java                    # Console application entry point
├── model/
│   ├── Rider.java              # Rider entity
│   ├── Driver.java             # Driver entity
│   ├── Ride.java               # Ride transaction
│   ├── FareReceipt.java        # Fare receipt
│   ├── RideStatus.java         # Enum: ride lifecycle states
│   └── VehicleType.java        # Enum: vehicle categories
├── service/
│   ├── RiderService.java       # Rider management
│   ├── DriverService.java      # Driver management
│   └── RideService.java        # Ride orchestration
├── strategy/
│   ├── RideMatchingStrategy.java    # Interface
│   ├── NearestDriverStrategy.java   # Implementation
│   ├── LeastActiveDriverStrategy.java # Implementation
│   ├── FareStrategy.java            # Interface
│   ├── DefaultFareStrategy.java     # Implementation
│   └── PeakHourFareStrategy.java    # Implementation
├── exception/
│   └── NoDriverAvailableException.java  # Custom exception
└── util/
    └── IdGenerator.java        # ID generation utility
```

