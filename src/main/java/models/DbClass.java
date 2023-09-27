package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static controllers.JDBC.connection;
import static controllers.JDBC.getConnection;

/**
 * This class represents the inventory of parts and products in the system.
 * Functionality includes: add, lookup, update, and delete parts and products.
 * @author Tyler Bradshaw
 */
public class DbClass {
    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    /**
     * Adds a new appointment to the inventory.
     * @param appointment the appointment to add to the inventory
     */
    public static void addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, appointment.getId());
            statement.setString(2, appointment.getTitle());
            statement.setString(3, appointment.getDescription());
            statement.setString(4, appointment.getLocation());
            statement.setString(5, appointment.getType());
            statement.setObject(6, appointment.getStartDateTime());
            statement.setObject(7, appointment.getEndDateTime());
            statement.setInt(8, appointment.getCustomerId());
            statement.setInt(9, appointment.getUserId());
            statement.setInt(10, appointment.getContactId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database insertion here
        }
    }
    /**
     * Adds a new customer to the inventory.
     * @param customer the customer to add to the inventory
     */
    public static void addCustomer(Customer customer) {

        String sql = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customer.getId());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getAddress());
            statement.setString(4, customer.getPostalCode());
            statement.setString(5, customer.getPhone());
            statement.setInt(6, customer.getDivisionId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database insertion here
        }
    }

    /**
     * Searches for a part by its ID.
     * @param appointmentId the ID of the part to search for
     * @return the found part, or null if not found
     */
    public static Appointment lookUpAppointment(int appointmentId) {
        String sql = "SELECT * FROM appointments WHERE Appointment_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, appointmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int appointmentID = resultSet.getInt("Appointment_ID");
                    String title = resultSet.getString("Title");
                    String description = resultSet.getString("Description");
                    String location = resultSet.getString("Location");
                    String type = resultSet.getString("Type");
                    ZonedDateTime start = resultSet.getTimestamp("Start").toLocalDateTime().atZone(ZoneOffset.UTC);
                    ZonedDateTime end = resultSet.getTimestamp("End").toLocalDateTime().atZone(ZoneOffset.UTC);

                    int customerId = resultSet.getInt("Customer_ID");
                    int userId = resultSet.getInt("User_ID");
                    int contactId = resultSet.getInt("Contact_ID");

                    return new Appointment(appointmentID, title, description, location, type, start, end,
                            customerId, userId, contactId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database retrieval here
        }

        // If no appointment with the given ID is found, return null
        return null;
    }


    /**
     * Searches for a product by its ID.
     * @param customerId the ID of the product to search for
     * @return the found product, or null if not found
     */
    public static Customer lookUpCustomer(int customerId) {
        String sql = "SELECT * FROM customers WHERE Customer_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int customerID = resultSet.getInt("Customer_ID");
                    String customerName = resultSet.getString("Customer_Name");
                    String address = resultSet.getString("Address");
                    String postalCode = resultSet.getString("Postal_Code");
                    String phone = resultSet.getString("Phone");
                    int divisionId = resultSet.getInt("Division_ID");

                    return new Customer(customerID, customerName, address, postalCode, phone,
                            divisionId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database retrieval here
        }

        // If no customer with the given ID is found, return null
        return null;
    }

    /**
     * Searches for parts with a name that contains the given string.
     * @param appointmentName the string to search for in part names
     * @return a filtered list of parts whose names contain the given string
     */
    public static ObservableList<Appointment> lookUpAppointment(String appointmentName) {
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments WHERE Title LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + appointmentName + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int appointmentId = resultSet.getInt("Appointment_ID");
                    String title = resultSet.getString("Title");
                    String description = resultSet.getString("Description");
                    String location = resultSet.getString("Location");
                    String type = resultSet.getString("Type");
                    ZonedDateTime start = resultSet.getTimestamp("Start").toLocalDateTime().atZone(ZoneOffset.UTC);
                    ZonedDateTime end = resultSet.getTimestamp("End").toLocalDateTime().atZone(ZoneOffset.UTC);
                    int customerId = resultSet.getInt("Customer_ID");
                    int userId = resultSet.getInt("User_ID");
                    int contactId = resultSet.getInt("Contact_ID");

                    Appointment appointment = new Appointment(appointmentId, title, description, location, type, start, end,
                            customerId, userId, contactId);
                    filteredAppointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database retrieval here
        }

        return filteredAppointments;
    }

    /**
     * Searches for products with a name that contains the given string.
     * @param customerName the string to search for in product names
     * @return a filtered list of products whose names contain the given string
     */
    public static ObservableList<Customer> lookUpCustomer(String customerName) {
        ObservableList<Customer> filteredCustomers = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customers WHERE Customer_Name LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + customerName + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int customerId = resultSet.getInt("Customer_ID");
                    String name = resultSet.getString("Customer_Name");
                    String address = resultSet.getString("Address");
                    String postalCode = resultSet.getString("Postal_Code");
                    String phone = resultSet.getString("Phone");
                    int divisionId = resultSet.getInt("Division_ID");

                    Customer customer = new Customer(customerId, name, address, postalCode, phone,
                            divisionId);
                    filteredCustomers.add(customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database retrieval here
        }

        return filteredCustomers;
    }


    public static void updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, " +
                "End = ?, Customer_ID = ?, " +
                "User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, appointment.getTitle());
            statement.setString(2, appointment.getDescription());
            statement.setString(3, appointment.getLocation());
            statement.setString(4, appointment.getType());
            statement.setObject(5, appointment.getStartDateTime());
            statement.setObject(6, appointment.getEndDateTime());
            statement.setInt(7, appointment.getCustomerId());
            statement.setInt(8, appointment.getUserId());
            statement.setInt(9, appointment.getContactId());
            statement.setInt(10, appointment.getId());

            statement.executeUpdate();

            // Get the index of the appointment in the observable list
            int index = allAppointments.indexOf(appointment);
            if (index != -1) {
                // Update the appointment in the observable list
                allAppointments.set(index, appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database update here
        }
    }

    /**
     * Updates a customer at the specified index.
     * @param index the index of the customer to update
     * @param customer the new customer to replace the old one
     */
    public static void updateCustomer(int index, Customer customer) {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? WHERE Customer_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPostalCode());
            statement.setString(4, customer.getPhone());
            statement.setInt(9, customer.getDivisionId());
            statement.setInt(10, customer.getId());

            statement.executeUpdate();
            // Update the customer in the observable list
            allCustomers.set(index, customer);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database update here
        }
    }

    /**
     * Deletes the specified appointment from the inventory.
     * @param appointment the appointment to be deleted
     * @return true if the appointment was successfully deleted
     */
    public static boolean deleteAppointment(Appointment appointment){
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, appointment.getId());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database deletion here
        }
        return allAppointments.remove(appointment);
    }
    /**
     * Deletes the specified customer from the inventory.
     * @param customer the part to be deleted
     * @return true if the customer was successfully deleted
     */
    public static boolean deleteCustomer(Customer customer){
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customer.getId());
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                // If at least one row is deleted, return true
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions related to database deletion here
        }

        return allCustomers.remove(customer);
    }
    public static ObservableList<Customer> fetchCustomersFromDatabase(Connection connection) throws SQLException {
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

        String query = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, " +
                "Last_Update, Last_Updated_By, Division_ID FROM customers";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int customerId = resultSet.getInt("Customer_ID");
                String customerName = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                int divisionId = resultSet.getInt("Division_ID");

                Customer customer = new Customer(customerId, customerName, address, postalCode, phone,
                        divisionId);
                allCustomers.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions here
        }

        return allCustomers;
    }
    /**
     * Returns a list of all products in the inventory.
     * @return a list of all products in the inventory
     */
    public static ObservableList<Appointment> fetchAppointmentsFromDatabase(Connection connection) {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

        String query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, " +
                "Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID FROM appointments";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {


            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("Appointment_ID");
                String title = resultSet.getString("Title");
                String description = resultSet.getString("Description");
                String location = resultSet.getString("Location");
                String type = resultSet.getString("Type");
                ZonedDateTime start = resultSet.getTimestamp("Start").toLocalDateTime().atZone(ZoneOffset.systemDefault());
                ZonedDateTime end = resultSet.getTimestamp("End").toLocalDateTime().atZone(ZoneOffset.systemDefault());

                int customerId = resultSet.getInt("Customer_ID");
                int userId = resultSet.getInt("User_ID");
                int contactId = resultSet.getInt("Contact_ID");



                Appointment appointment = new Appointment(appointmentId, title, description, location, type, start, end,
                        customerId, userId, contactId);
                allAppointments.add(appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions here
        }

        return allAppointments;
    }


}
