package com.airtribe.ridewise;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.model.VehicleType;
import com.airtribe.ridewise.service.DriverService;
import com.airtribe.ridewise.service.RideService;
import com.airtribe.ridewise.service.RiderService;
import com.airtribe.ridewise.strategy.DefaultFareStrategy;
import com.airtribe.ridewise.strategy.FareStrategy;
import com.airtribe.ridewise.strategy.LeastActiveDriverStrategy;
import com.airtribe.ridewise.strategy.NearestDriverStrategy;
import com.airtribe.ridewise.strategy.PeakHourFareStrategy;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private RiderService riderService;
    private DriverService driverService;
    private RideService rideService;
    private Scanner scanner;

    public Main() {
        riderService = new RiderService();
        driverService = new DriverService();
        RideMatchingStrategy matchingStrategy = new NearestDriverStrategy();
        FareStrategy fareStrategy = new PeakHourFareStrategy();
        rideService = new RideService(driverService, riderService, matchingStrategy, fareStrategy);
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    private void run() {
        System.out.println("=========================================");
        System.out.println("   Welcome to RideWise - Ride Sharing   ");
        System.out.println("=========================================\n");

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            running = handleMenuChoice(choice);
        }

        System.out.println("\nThank you for using RideWise. Goodbye!");
        scanner.close();
    }

    // ToDo: View riders and drivers
    private void displayMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Add Rider");
        System.out.println("2. Add Driver");
        System.out.println("3. View All Riders");
        System.out.println("4. View All Drivers");
        System.out.println("5. View Available Drivers");
        System.out.println("6. Request Ride");
        System.out.println("7. Complete Ride");
        System.out.println("8. View Rides");
        System.out.println("9. Switch Ride Matching Strategy");
        System.out.println("10. Switch Fare Calculation Strategy");
        System.out.println("11. Exit");
        System.out.println("================================");
    }

    private boolean handleMenuChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    addRider();
                    break;
                case 2:
                    addDriver();
                    break;
                case 3:
                    viewAllRiders();
                    break;
                case 4:
                    viewAllDrivers();
                    break;
                case 5:
                    viewAvailableDrivers();
                    break;
                case 6:
                    requestRide();
                    break;
                case 7:
                    completeRide();
                    break;
                case 8:
                    viewRides();
                    break;
                case 9:
                    switchMatchingStrategy();
                    break;
                case 10:
                    switchFareStrategy();
                    break;
                case 11:
                    return false;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private void addRider() {
        scanner.nextLine();
        System.out.println("\n--- Add New Rider ---");
        System.out.print("Enter rider name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Rider name cannot be empty!");
            return;
        }

        System.out.print("Enter rider location: ");
        String location = scanner.nextLine().trim();

        if (location.isEmpty()) {
            System.out.println("Location cannot be empty!");
            return;
        }

        String riderId = IdGenerator.generateRiderId();
        Rider rider = new Rider(riderId, name, location);
        riderService.registerRider(rider);
        System.out.println("Rider registered successfully!");
        System.out.println("Rider ID: " + riderId);
        System.out.println(rider);
    }

    private void addDriver() {
        scanner.nextLine();
        System.out.println("\n--- Add New Driver ---");
        System.out.print("Enter driver name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Driver name cannot be empty!");
            return;
        }

        System.out.print("Enter driver location: ");
        String location = scanner.nextLine().trim();

        if (location.isEmpty()) {
            System.out.println("Location cannot be empty!");
            return;
        }

        System.out.println("Select vehicle type:");
        System.out.println("1. BIKE");
        System.out.println("2. AUTO");
        System.out.println("3. CAR");
        int vehicleChoice = getIntInput("Enter choice (1-3): ");

        VehicleType vehicleType;
        switch (vehicleChoice) {
            case 1:
                vehicleType = VehicleType.BIKE;
                break;
            case 2:
                vehicleType = VehicleType.AUTO;
                break;
            case 3:
                vehicleType = VehicleType.CAR;
                break;
            default:
                System.out.println("Invalid vehicle type. Defaulting to CAR.");
                vehicleType = VehicleType.CAR;
        }

        String driverId = IdGenerator.generateDriverId();
        Driver driver = new Driver(driverId, name, location, vehicleType);
        driverService.registerDriver(driver);
        System.out.println("Driver registered successfully!");
        System.out.println("Driver ID: " + driverId);
        System.out.println(driver);
    }

    private void viewAvailableDrivers() {
        System.out.println("\n--- Available Drivers ---");
        List<Driver> availableDrivers = driverService.getAvailableDrivers();

        if (availableDrivers.isEmpty()) {
            System.out.println("No drivers available at the moment.");
            return;
        }

        for (Driver driver : availableDrivers) {
            System.out.println(driver);
        }
        System.out.println("Total available drivers: " + availableDrivers.size());
    }

    private void viewAllRiders() {
        System.out.println("\n--- All Riders ---");
        java.util.Collection<Rider> allRiders = riderService.getAllRiders();

        if (allRiders.isEmpty()) {
            System.out.println("No riders registered yet.");
            return;
        }

        int count = 0;
        for (Rider rider : allRiders) {
            System.out.println(++count + ". " + rider);
        }
        System.out.println("\nTotal riders: " + allRiders.size());
    }

    private void viewAllDrivers() {
        System.out.println("\n--- All Drivers ---");
        java.util.Collection<Driver> allDrivers = driverService.getAllDrivers();

        if (allDrivers.isEmpty()) {
            System.out.println("No drivers registered yet.");
            return;
        }

        int count = 0;
        for (Driver driver : allDrivers) {
            System.out.println(++count + ". " + driver);
        }
        System.out.println("\nTotal drivers: " + allDrivers.size());
    }

    private void requestRide() {
        scanner.nextLine();
        System.out.println("\n--- Request a Ride ---");
        System.out.print("Enter rider ID: ");
        String riderId = scanner.nextLine().trim();

        if (!riderService.riderExists(riderId)) {
            System.out.println("Rider not found!");
            return;
        }

        Rider rider = riderService.getRiderById(riderId);

        System.out.print("Enter distance (in km): ");
        double distance = getDoubleInput();

        if (distance <= 0) {
            System.out.println("Distance must be greater than 0!");
            return;
        }

        try {
            String rideId = IdGenerator.generateRideId();
            Ride ride = new Ride(rideId, rider, distance);
            Ride assignedRide = rideService.requestRide(ride);

            System.out.println("Ride requested successfully!");
            System.out.println("Ride ID: " + rideId);
            System.out.println("Assigned Driver: " + assignedRide.getDriver().getName());
            System.out.println(assignedRide);
        } catch (NoDriverAvailableException e) {
            System.out.println("No driver available: " + e.getMessage());
        }
    }

    private void completeRide() {
        scanner.nextLine();
        System.out.println("\n--- Complete Ride ---");
        System.out.print("Enter ride ID: ");
        String rideId = scanner.nextLine().trim();

        Ride ride = rideService.getRideById(rideId);
        if (ride == null) {
            System.out.println("Ride not found!");
            return;
        }

        FareReceipt receipt = rideService.completeRide(rideId);
        if (receipt == null) {
            System.out.println("Cannot complete this ride!");
            return;
        }

        System.out.println("Ride completed successfully!");
        System.out.println(receipt);
        System.out.println("Updated Ride Status: " + ride.getStatus());
    }

    private void viewRides() {
        System.out.println("\n--- All Rides ---");
        Map<String, Ride> allRides = rideService.getAllRides();

        if (allRides.isEmpty()) {
            System.out.println("No rides found.");
            return;
        }

        for (Ride ride : allRides.values()) {
            System.out.println(ride);
        }
        System.out.println("Total rides: " + allRides.size());
    }

    private void switchMatchingStrategy() {
        System.out.println("\n--- Switch Ride Matching Strategy ---");
        System.out.println("1. Nearest Driver Strategy");
        System.out.println("2. Least Active Driver Strategy");
        int choice = getIntInput("Select strategy (1-2): ");

        RideMatchingStrategy newStrategy;
        switch (choice) {
            case 1:
                newStrategy = new NearestDriverStrategy();
                System.out.println("Switched to Nearest Driver Strategy");
                break;
            case 2:
                newStrategy = new LeastActiveDriverStrategy();
                System.out.println("Switched to Least Active Driver Strategy");
                break;
            default:
                System.out.println("Invalid choice!");
                return;
        }
        updateRideService(newStrategy, null);
    }

    private void switchFareStrategy() {
        System.out.println("\n--- Switch Fare Calculation Strategy ---");
        System.out.println("1. Default Fare Strategy");
        System.out.println("2. Peak Hour Fare Strategy");
        int choice = getIntInput("Select strategy (1-2): ");

        FareStrategy newStrategy;
        switch (choice) {
            case 1:
                newStrategy = new DefaultFareStrategy();
                System.out.println("Switched to Default Fare Strategy");
                break;
            case 2:
                newStrategy = new PeakHourFareStrategy();
                System.out.println("Switched to Peak Hour Fare Strategy");
                break;
            default:
                System.out.println("Invalid choice!");
                return;
        }
        updateRideService(null, newStrategy);
    }

    private void updateRideService(RideMatchingStrategy matchingStrategy, FareStrategy fareStrategy) {
        if (matchingStrategy != null || fareStrategy != null) {
            RideMatchingStrategy currentMatching = matchingStrategy != null ? matchingStrategy : 
                    new NearestDriverStrategy();
            FareStrategy currentFare = fareStrategy != null ? fareStrategy : 
                    new DefaultFareStrategy();
            rideService = new RideService(driverService, riderService, currentMatching, currentFare);
        }
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

    private double getDoubleInput() {
        try {
            return scanner.nextDouble();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Invalid input. Please enter a valid number.");
            return -1;
        }
    }
}
