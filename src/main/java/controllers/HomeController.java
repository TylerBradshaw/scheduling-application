package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Appointment;
import models.Customer;
import models.DbClass;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static controllers.JDBC.connection;
import static models.DbClass.fetchAppointmentsFromDatabase;
import static models.DbClass.fetchCustomersFromDatabase;

/**
 * This class is the controller for the main view of the application.
 * It is responsible for handling user interactions and displaying data for the customers and appointments tables.
 * RUNTIME ERROR: Search functionality was causing a target invocation exception due to trying to add an item to an
 * empty list. When searching by ID, the method attempted to add the resulting part to an empty "parts." By using a
 * separate observable list, the issue is avoided because we are guaranteed that each list will contain
 * at least one element.
 * LOGICAL ERROR: index.set() was incorrectly within the if statement of "modify part", causing the index to be
 * incorrect when modifying parts. Placing it outside of the statement fixed the issue.
 * @author Tyler Bradshaw
 */
public class HomeController implements Initializable{

    public TextField searchFieldAppointment;
    public TextField searchFieldCustomer;
    public TableView<Appointment> appointmentTable;
    public TableView<Customer> customerTable;
    public TableColumn<Appointment, Integer> apptId;
    public TableColumn<Appointment, String> apptTitle;
    public TableColumn<Appointment, String> apptDesc;
    public TableColumn<Appointment, String> apptLocation;
    public TableColumn<Appointment, String> apptType;
    public TableColumn<Appointment, ZonedDateTime> apptStart;
    public TableColumn<Appointment, ZonedDateTime> apptEnd;
    public TableColumn<Appointment, ZonedDateTime> apptCreated;
    public TableColumn<Appointment, String> apptCreatedBy;
    public TableColumn<Appointment, ZonedDateTime> apptLastUpdate;
    public TableColumn<Appointment, String> apptUpdatedBy;
    public TableColumn<Appointment, Integer> apptCustId;
    public TableColumn<Appointment, Integer> apptUserId;
    public TableColumn<Appointment, Integer> apptContactId;
    public TableColumn<Customer,Integer> customerId;
    public TableColumn<Customer, String> customerName;
    public TableColumn<Customer, String> customerAddress;
    public TableColumn<Customer, String> customerPostal;
    public TableColumn<Customer, String> customerPhone;
    public TableColumn<Customer, ZonedDateTime> customerCreated;
    public TableColumn<Customer, String> customerCreatedBy;
    public TableColumn<Customer, ZonedDateTime> customerLastUpdate;
    public TableColumn<Customer, String> customerUpdatedBy;
    public TableColumn<Customer, Integer> customerDivisionId;
    public Button addAppointment;
    public Button addCustomer;
    public Button deleteAppointment;
    public Button deleteCustomer;
    public Button modifyAppointment;
    public Button modifyCustomer;
    private static final AtomicInteger index = new AtomicInteger();
    /**
     * This method returns the index of the selected row in the table.
     * @return The index of the selected row.
     */
    public static int returnIndex(){

        return index.get();
    }
    /**
     * This method is called when the "Add Appointment" button is clicked.
     * It loads the AddAppointment view and displays it.
     */
    @FXML
    void addAppointmentClicked(ActionEvent event) throws IOException {

        JfxUtility.loadView("AddAppointment.fxml", event);
    }
    /**
     * This method is called when the "Add Product" button is clicked.
     * It loads the AddProduct view and displays it.
     */
    @FXML
    void addCustomerClicked(ActionEvent event) throws IOException {

        JfxUtility.loadView("AddCustomer.fxml", event);
    }
    /**
     * This method is called when the "Delete Part" button is clicked.
     * It deletes the selected part from the inventory if confirmed by the user.
     */
    @FXML
    void deleteAppointmentClicked() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            JfxUtility.showConfirmationDialog("Delete this appointment?", () -> {
                if (DbClass.deleteAppointment(selectedAppointment)) {
                    // If the appointment is successfully deleted from the database, remove it from the table view
                    appointmentTable.getItems().remove(selectedAppointment);
                    JfxUtility.showAlertDialog("Success", "Appointment deleted successfully.");
                } else {
                    // Handle case when deletion from the database fails
                    JfxUtility.showAlertDialog("Error!", "Failed to delete the appointment from the database.");
                }
            });
        } else {
            JfxUtility.showAlertDialog("Error!", "Select an appointment to delete.");
        }
    }
    /**
     * This method is called when the "Delete Product" button is clicked.
     * It deletes the selected product from the inventory if confirmed by the user.
     * If the product has associated parts, it displays a warning message and does not allow deletion.
     */
    @FXML
    void deleteCustomerClicked() {

        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            if (selectedCustomer.getAllAssociatedAppointments().size() > 0) {
                JfxUtility.showAlertDialog("Cannot Delete!", "All appointments associated with the customer must be removed before deletion.");
            } else {
                JfxUtility.showConfirmationDialog("Delete this customer?", () -> {
                    boolean deleted = DbClass.deleteCustomer(selectedCustomer);
                    if (deleted) {
                        // Remove the customer from the observable list
                        customerTable.getItems().remove(selectedCustomer);
                        JfxUtility.showAlertDialog("Success", "Customer deleted successfully.");
                    } else {
                        JfxUtility.showAlertDialog("Error!", "Failed to delete customer.");
                    }
                });
            }
        } else {
            JfxUtility.showAlertDialog("Error!", "Select a customer to delete.");
        }
    }
    /**
     * This method is called when the modify part button is clicked. It opens the ModifyPart.fxml view and
     * passes the selected part to the ModifyPart controller for editing.
     * @param event the event that triggered this method
     * @throws IOException if there is an error loading the ModifyPart.fxml view
     */
    @FXML
    void modifyAppointmentClicked(ActionEvent event) throws IOException {

        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        index.set(appointmentTable.getItems().indexOf(selectedAppointment));

        if (selectedAppointment != null) {


            JfxUtility.loadAndPopulateView("ModifyAppointment.fxml", event, controller -> {
                ModifyAppointmentController modifyAppointmentController = (ModifyAppointmentController) controller;
                modifyAppointmentController.populateAppointment(selectedAppointment);
            });
        } else {

            JfxUtility.showAlertDialog("Error!", "Select an appointment to modify.");

        }
    }
    /**
     * This method is called when the modify product button is clicked. It opens the ModifyProduct.fxml view and
     * passes the selected product to the ModifyProduct controller for editing.
     * @param event the event that triggered this method
     * @throws IOException if there is an error loading the ModifyProduct.fxml view
     */

    @FXML
    void modifyCustomerClicked(ActionEvent event) throws IOException {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        index.set(customerTable.getItems().indexOf(selectedCustomer));

        if (selectedCustomer != null) {
            JfxUtility.loadAndPopulateView("ModifyCustomer.fxml", event, controller -> {
                ModifyCustomerController modifyCustomerController = (ModifyCustomerController) controller;
                modifyCustomerController.populateCustomer(selectedCustomer);
            });
        } else {
            JfxUtility.showAlertDialog("Warning", "You must select a customer to modify.");
        }
    }
    /**
     * This method is called when the exit button is clicked. It exits the application.
     */
    @FXML
    void exitClicked() {
        JfxUtility.exitApplication();
    }

    /**
     * This method is triggered when the user enters text into the search field for Parts.
     * It searches for Parts that match the entered query and displays them in the Parts table.
     * If no Parts match the query, it tries to parse the query as an integer to search for a Part by ID.
     * If still no Part is found, it displays a warning message and resets table to its initial state.
     * Clicking enter while search box is empty returns it to its original state. Future functionality could
     * include adding a listener to highlight matches as they are being typed.
     */


    /**
     * Responsible for initializing the All Parts Table in the Main View.
     */



    private void initAllAppointmentsTable(Connection connection, TableView<Appointment> appointmentTable) {
        // Set cell value factories for each column
        apptId.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptStart.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        apptEnd.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        apptCustId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        apptUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        apptContactId.setCellValueFactory(new PropertyValueFactory<>("contactId"));



        appointmentTable.setItems(fetchAppointmentsFromDatabase(connection));
    }

    // Custom TableCell for formatting ZonedDateTime





    /**
     * Responsible for initializing the All Products Table in the Main View.
     */
    private void initAllCustomersTable(Connection connection, TableView<Customer> customerTable) {
        try {


            customerId.setCellValueFactory(new PropertyValueFactory<>("id"));
            customerName.setCellValueFactory(new PropertyValueFactory<>("name"));
            customerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
            customerPostal.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            customerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            customerDivisionId.setCellValueFactory(new PropertyValueFactory<>("divisionId"));



            customerTable.setItems(fetchCustomersFromDatabase(connection));
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions here
        }
    }
    /**
     * Initializes the All Parts Table and All Products Table in the Main View.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initAllAppointmentsTable(connection, appointmentTable);
        initAllCustomersTable(connection, customerTable );

        appointmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                int selectedIndex = appointmentTable.getItems().indexOf(newSelection);
                index.set(selectedIndex);
            }
        });

        // For Customer Table
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                int selectedIndex = customerTable.getItems().indexOf(newSelection);
                index.set(selectedIndex);
            }
        });
    }
}
