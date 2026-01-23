import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "12345";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL Driver not found!");
            return;
        }

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {

            while (true) {
                System.out.println("""
                        ===== CONTACTS MENU =====
                        1 - Create contact
                        2 - Read all contacts
                        3 - Read contact by id
                        4 - Update contact
                        5 - Delete contact
                        6 - Exit
                        """);

                int choice;
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Enter number from 1 to 6!");
                    continue;
                }

                switch (choice) {
                    case 1 -> createContact(con, scanner);
                    case 2 -> readAllContacts(con);
                    case 3 -> readContactById(con, scanner);
                    case 4 -> updateContact(con, scanner);
                    case 5 -> deleteContact(con, scanner);
                    case 6 -> {
                        System.out.println("Bye!");
                        return;
                    }
                    default -> System.out.println("Choose 1-6.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void createContact(Connection con, Scanner scanner) {
        System.out.print("First name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Email (optional): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = null;

        System.out.print("Type (friend/family/work): ");
        String type = scanner.nextLine().trim();


        if (firstName.isEmpty() || lastName.isEmpty()) {
            System.out.println("Validation error: first name and last name cannot be empty!");
            return;
        }
        if (phone.isEmpty()) {
            System.out.println("Validation error: phone cannot be empty!");
            return;
        }
        if (type.isEmpty()) {
            System.out.println("Validation error: contact type cannot be empty!");
            return;
        }

        String sql = """
                INSERT INTO contacts (first_name, last_name, phone, email, contact_type)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, type);

            ps.executeUpdate();
            System.out.println("Contact created!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            System.out.println("Tip: phone must be UNIQUE (no duplicates).");
        }
    }

    private static void readAllContacts(Connection con) {
        String sql = "SELECT * FROM contacts ORDER BY id";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- CONTACTS ---");
            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                                rs.getString("first_name") + " " +
                                rs.getString("last_name") + " | " +
                                rs.getString("phone") + " | " +
                                rs.getString("email") + " | " +
                                rs.getString("contact_type") + " | " +
                                rs.getTimestamp("created_at")
                );
            }
            System.out.println("---------------\n");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    private static void readContactById(Connection con, Scanner scanner) {
        System.out.print("Enter id: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid id!");
            return;
        }

        String sql = "SELECT * FROM contacts WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println(
                            rs.getInt("id") + " | " +
                                    rs.getString("first_name") + " " +
                                    rs.getString("last_name") + " | " +
                                    rs.getString("phone") + " | " +
                                    rs.getString("email") + " | " +
                                    rs.getString("contact_type") + " | " +
                                    rs.getTimestamp("created_at")
                    );
                } else {
                    System.out.println("Contact not found!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void updateContact(Connection con, Scanner scanner) {
        System.out.print("Enter id to update: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid id!");
            return;
        }

        System.out.print("New first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("New last name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("New phone: ");
        String phone = scanner.nextLine().trim();

        System.out.print("New email (optional): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = null;

        System.out.print("New type (friend/family/work): ");
        String type = scanner.nextLine().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || type.isEmpty()) {
            System.out.println("Validation error: required fields cannot be empty!");
            return;
        }

        String sql = """
                UPDATE contacts
                SET first_name = ?, last_name = ?, phone = ?, email = ?, contact_type = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, type);
            ps.setInt(6, id);

            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("Contact not found!");
            else System.out.println("✅ Updated!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            System.out.println("Tip: phone must be UNIQUE (no duplicates).");
        }
    }

    private static void deleteContact(Connection con, Scanner scanner) {
        System.out.print("Enter id to delete: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid id!");
            return;
        }

        String sql = "DELETE FROM contacts WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("Contact not found!");
            else System.out.println("🗑 Deleted!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
