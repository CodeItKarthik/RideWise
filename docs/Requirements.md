# RideWise - Project Requirements & Implementation

## Project Overview
RideWise is a simplified, console-based ride-sharing system that demonstrates high-quality Low-Level Design (LLD) principles, showing correct application of design patterns, SOLID principles, and clean architecture principles.

## Key Features
1. **Rider Management** - Register riders with location tracking
2. **Driver Management** - Register drivers with vehicle types and availability
3. **Ride Matching** - Multiple strategies for driver-to-rider matching
4. **Fare Calculation** - Dynamic fare strategies (default and peak hour)
5. **Ride Lifecycle** - Track rides through REQUESTED → ASSIGNED → COMPLETED → CANCELLED
6. **Strategy Switching** - Change matching and fare strategies at runtime
7. **Exception Handling** - Proper error handling for edge cases

## Functional Requirements

### A. Core Functionalities
1. **Register Riders** - Create rider accounts with location
2. **Register Drivers** - Create driver accounts with vehicle type
3. **Show Available Drivers** - List all drivers currently available for rides
4. **Request a Ride** - Rider initiates a ride request
5. **Match Ride to Driver** - Automatic assignment using strategy pattern
6. **Calculate Fare** - Compute fare based on distance and strategy
7. **Track Ride Status** - Monitor ride through lifecycle

### B. Ride Status Lifecycle
- **REQUESTED** - Rider has submitted a ride request
- **ASSIGNED** - Driver has been matched to the ride
- **COMPLETED** - Ride has been completed successfully
- **CANCELLED** - Ride was cancelled before completion

## Domain Entities

### Model Classes

#### Rider
- `id` (String) - Unique identifier
- `name` (String) - Rider's name
- `location` (String) - Current location
- `rating` (double) - Rider rating (default: 5.0)

#### Driver
- `id` (String) - Unique identifier
- `name` (String) - Driver's name
- `currentLocation` (String) - Current location
- `vehicleType` (VehicleType) - Type of vehicle (BIKE, AUTO, CAR)
- `available` (boolean) - Availability status
- `rating` (double) - Driver rating (default: 5.0)

#### Ride
- `id` (String) - Unique identifier
- `rider` (Rider) - Associated rider
- `driver` (Driver) - Assigned driver (nullable)
- `distance` (double) - Distance in kilometers
- `fare` (double) - Calculated fare amount
- `status` (RideStatus) - Current ride status
- `createdAt` (long) - Ride creation timestamp

#### FareReceipt
- `rideId` (String) - Associated ride ID
- `amount` (double) - Fare amount
- `generatedAt` (long) - Receipt generation timestamp

#### VehicleType (Enum)
- BIKE
- AUTO
- CAR

## Design Patterns & Principles

### 1. Strategy Pattern
**Implementation:** RideMatchingStrategy & FareStrategy interfaces
- Allows switching between different algorithms at runtime
- Enables easy extension for new strategies
- Clients depend on abstractions, not concrete implementations

**Ride Matching Strategies:**
- `NearestDriverStrategy` - Selects first available driver
- `LeastActiveDriverStrategy` - Selects driver with fewer rides

**Fare Calculation Strategies:**
- `DefaultFareStrategy` - Base fare + distance-based charges
- `PeakHourFareStrategy` - Applies 1.2x multiplier for peak hours

### 2. SOLID Principles

**Single Responsibility Principle (SRP)**
- RiderService: Manages rider data
- DriverService: Manages driver data
- RideService: Manages ride lifecycle
- IdGenerator: Generates unique IDs
- Each service has ONE reason to change

**Open/Closed Principle (OCP)**
- New strategies can be added without modifying RideService
- New fare calculation methods extend FareStrategy interface
- Strategies are pluggable components

**Liskov Substitution Principle (LSP)**
- Any RideMatchingStrategy implementation is interchangeable
- Any FareStrategy implementation is interchangeable
- Behavior contracts are maintained

**Interface Segregation Principle (ISP)**
- RideMatchingStrategy has single method: `findDriver()`
- FareStrategy has single method: `calculateFare()`
- Clients only depend on methods they use

**Dependency Inversion Principle (DIP)**
- RideService depends on RideMatchingStrategy interface, not concrete classes
- RideService depends on FareStrategy interface, not concrete classes
- High-level modules don't depend on low-level modules
- Both depend on abstractions

### 3. Composition Over Inheritance
- RideService composes (not inherits from) strategies
- Driver and Rider are composed into Ride (Association)
- FareReceipt is composed from Ride data (Composition)
- Avoids deep inheritance hierarchies

### 4. Law of Demeter (Low Coupling)
- Services interact with direct collaborators only
- No deep method chains (e.g., `ride.getDriver().getLocation().getValue()`)
- Services encapsulate their internal state
- Main.java interacts only with services, not nested objects

### 5. Dependency Injection
- Strategies injected into RideService constructor
- No hardcoded dependencies
- Easy to test with mock implementations
- Enables runtime configuration

## Service Layer Architecture

### RiderService
```java
- registerRider(Rider): void
- getRiderById(String): Rider
- riderExists(String): boolean
```

### DriverService
```java
- registerDriver(Driver): void
- getDriverById(String): Driver
- getAvailableDrivers(): List<Driver>
- updateDriverAvailability(String, boolean): void
- updateDriverLocation(String, String): void
- driverExists(String): boolean
```

### RideService
```java
- requestRide(Ride): Ride (throws NoDriverAvailableException)
- completeRide(String): FareReceipt
- getRideById(String): Ride
- cancelRide(String): void
- getAllRides(): Map<String, Ride>
```

## Console Application Features

### Main Menu Options
1. **Add Rider** - Register new rider with name and location
2. **Add Driver** - Register new driver with name, location, and vehicle type
3. **View Available Drivers** - Display all drivers currently available
4. **Request Ride** - Submit a ride request as a rider
5. **Complete Ride** - Mark a ride as completed and generate fare receipt
6. **View Rides** - Display all rides with their current status
7. **Switch Ride Matching Strategy** - Change how drivers are matched to riders
8. **Switch Fare Calculation Strategy** - Change how fares are calculated
9. **Exit** - Close the application

### Input Validation
- Empty string checks
- Rider/Driver existence validation
- Distance validation (must be > 0)
- Choice range validation
- Exception handling for invalid input

## Relationship Model

```
Rider ──────────→ Ride (Association: 1-to-Many)
                    ↓
                 Driver (Association: 1-to-1)
                    ↓
               FareReceipt (Composition: 1-to-1)

RideService ──→ RideMatchingStrategy (Dependency: Interface)
             ├→ FareStrategy (Dependency: Interface)
             ├→ RiderService (Dependency: Composition)
             └→ DriverService (Dependency: Composition)
```

## MVP (Minimum Viable Product) Approach
- Core ride-sharing functionality implemented
- Two strategy implementations for each interface
- Essential services without unnecessary features
- Extensible design for future enhancements

## Running the Application

1. **Compile:**
   ```bash
   javac -d bin src/com/airtribe/ridewise/**/*.java
   ```

2. **Run:**
   ```bash
   java -cp bin com.airtribe.ridewise.Main
   ```

3. **Sample Flow:**
   - Add a rider
   - Add a driver
   - View available drivers
   - Request a ride
   - Complete the ride and view fare receipt
   - Check all rides

## Design Thinking & LLD Skills Demonstrated

1. **Entity Modeling** - Clear, focused domain entities
2. **Separation of Concerns** - Business logic in services, not in Main
3. **Scalable Extension Points** - Strategy interfaces for easy expansion
4. **Low Coupling** - Services interact through well-defined contracts
5. **High Cohesion** - Related functionality grouped in services
6. **Encapsulation** - Hidden implementation details, exposed contracts
7. **Error Handling** - Custom exceptions for domain-specific errors
8. **Dependency Management** - Injected, not hardcoded dependencies

