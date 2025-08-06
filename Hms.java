import java.util.*;
import java.io.*;

class Guest {
    String name;
    String contact;
    int roomNumber;
    String checkInDate;
    String checkOutDate;

    Guest(String name, String contact, int roomNumber, String checkInDate, String checkOutDate) {
        this.name = name;
        this.contact = contact;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    @Override
public String toString() {
    return "Booking Successful!\n\n" +
           "Booking Details:\n" +
           "------------------------\n" +
           "Guest Name: " + name + "\n" +
           "Contact: " + contact + "\n" +
           "Room Number: " + roomNumber + "\n" +
           "Check-In: " + checkInDate + "\n" +
           "Check-Out: " + checkOutDate + "\n" +
           "------------------------\n";
}

}

public class Hms {

    static ArrayList<Guest> bookings = new ArrayList<>();
    static final String FILE_NAME = "bookings.txt";
    static Set<Integer> availableRooms = new HashSet<>();

    public static void main(String[] args) {
        loadBookingsFromFile();
        initializeRooms();

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Hotel Management System ---");
            System.out.println("1. Book Room");
            System.out.println("2. View All Bookings");
            System.out.println("3. Check Room Availability");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    bookRoom(sc);
                    break;
                case 2:
                    viewBookings();
                    break;
                case 3:
                    checkAvailability();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 4);
    }

    static void initializeRooms() {
        for (int i = 101; i <= 110; i++) {
            availableRooms.add(i);
        }

        for (Guest g : bookings) {
            availableRooms.remove(g.roomNumber);
        }
    }

    static void bookRoom(Scanner sc) {
        System.out.print("Enter guest name: ");
        String name = sc.nextLine();
        System.out.print("Enter contact number: ");
        String contact = sc.nextLine();

        System.out.println("Available rooms: " + availableRooms);
        System.out.print("Select a room number from above: ");
        int roomNumber = sc.nextInt();
        sc.nextLine(); // consume newline

        if (!availableRooms.contains(roomNumber)) {
            System.out.println("Room " + roomNumber + " is already booked or invalid.");
            return;
        }

        // Date input and validation
        String checkIn, checkOut;

        while (true) {
            System.out.print("Enter check-in date (DD/MM/YYYY): ");
            checkIn = sc.nextLine();
            if (isValidDate(checkIn)) break;
            else System.out.println("Invalid check-in date. Please try again.");
        }

        while (true) {
            System.out.print("Enter check-out date (DD/MM/YYYY): ");
            checkOut = sc.nextLine();
            if (isValidDate(checkOut)) break;
            else System.out.println("Invalid check-out date. Please try again.");
        }

        // Prevent duplicate booking
        for (Guest g : bookings) {
            if (g.roomNumber == roomNumber) {
                System.out.println("Room " + roomNumber + " is already booked!");
                return;
            }
        }

        Guest g = new Guest(name, contact, roomNumber, checkIn, checkOut);
        bookings.add(g);
        availableRooms.remove(roomNumber);
        saveBookingToFile(g);

        System.out.println(g); // formatted confirmation
    }

    static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            System.out.println("\n--- All Bookings ---");
            for (Guest g : bookings) {
                System.out.println(g);
            }
        }
    }

    static void checkAvailability() {
        System.out.println("Available Rooms: " + availableRooms);
    }

    static void saveBookingToFile(Guest g) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(g.name + "," + g.contact + "," + g.roomNumber + "," + g.checkInDate + "," + g.checkOutDate);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving booking: " + e.getMessage());
        }
    }

    static void loadBookingsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    Guest g = new Guest(data[0], data[1], Integer.parseInt(data[2]), data[3], data[4]);
                    bookings.add(g);
                }
            }
        } catch (IOException e) {
            System.out.println("No previous bookings found. Starting fresh.");
        }
    }

    // Checks if date is in valid DD/MM/YYYY format and logical
    public static boolean isValidDate(String date) {
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12) return false;
        if (day < 1 || day > 31) return false;

        // Simple check for 30-day months
        if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) return false;
        // February check (ignoring leap year for simplicity)
        if (month == 2 && day > 28) return false;

        return true;
    }
}
