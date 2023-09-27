package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import models.Appointment;
import models.DbClass;
import models.Users;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static controllers.DBUtility.getKeyByValue;
import static controllers.DBUtility.populateComboBox;
import static controllers.JDBC.connection;

public class AddAppointmentController {
    public TextField appointmentId;
    public TextField appointmentTitle;
    public TextField appointmentDescription;
    public TextField appointmentLocation;
    public TextField appointmentType;
    public ComboBox<String> appointmentStartTime;
    public ComboBox<String> appointmentEndTime;
    public DatePicker appointmentDate;
    public TextField appointmentCreateDate;
    public TextField appointmentCreatedBy;
    public TextField appointmentLastUpdate;
    public TextField appointmentLastUpdateBy;
    public ComboBox<String> appointmentCustomerId;
    public ComboBox<String> appointmentUserId;
    public ComboBox<String> appointmentContactId;
    private Map<Integer, String> customerIdMap = new HashMap<>();
    private Map<Integer, String> userIdMap = new HashMap<>();
    private Map<Integer, String> contactIdMap = new HashMap<>();
    static int id;



    public void initialize() {
        appointmentDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                JfxUtility.populateTime(appointmentStartTime, appointmentEndTime, appointmentDate);
            }
        });

        populateComboBox(appointmentCustomerId, connection,
                "SELECT Customer_ID, Customer_Name FROM customers",
                resultSet -> {
                    try {
                        return resultSet.getInt("Customer_ID");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                resultSet -> {
                    try {
                        return resultSet.getString("Customer_Name");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                customerIdMap)
        ;

        // Populate the user combo box
        populateComboBox(appointmentUserId, connection,
                "SELECT User_ID, User_Name FROM users",
                resultSet -> {
                    try {
                        return resultSet.getInt("User_ID");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                resultSet -> {
                    try {
                        return resultSet.getString("User_Name");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                userIdMap);

        populateComboBox(appointmentContactId, connection,
                "SELECT Contact_ID, Contact_Name FROM contacts",
                resultSet -> {
                    try {
                        return resultSet.getInt("Contact_ID");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                resultSet -> {
                    try {
                        return resultSet.getString("Contact_Name");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                contactIdMap);
    }
    @FXML
    void exitClicked() {
        JfxUtility.exitApplication();
    }

    /**
     * Cancels adding the new part and returns to the Main screen.
     * @param event an action event that triggers the method
     * @throws IOException if the FXML resource cannot be found
     */
    @FXML
    void cancelClicked(ActionEvent event) throws IOException {

        JfxUtility.loadView("Home.fxml", event);
    }
    /**
     * Handles the event when the save button is clicked. Validates the input fields and saves the new
     * part to the inventory if all required fields are present and valid. Displays error messages if any required fields
     * are missing or invalid. After the part is successfully saved, the main GUI form is loaded.
     * Had some issues getting the method to check for blank fields and
     * ensuring that the current stock is between the minimum and maximum values.
     * @param event The event generated when the save button is clicked.
     */
    @FXML
    void saveClicked(ActionEvent event) {

        if (JfxUtility.checkBlankFields(
                this.appointmentTitle.getText(),
                this.appointmentDescription.getText(),
                this.appointmentLocation.getText(),
                this.appointmentType.getText()) ||
                JfxUtility.checkBlankDates(
                        this.appointmentDate) ||
                JfxUtility.checkBlankCombos(
                        this.appointmentStartTime,
                        this.appointmentEndTime,
                        this.appointmentCustomerId,
                        this.appointmentUserId,
                        this.appointmentContactId)) {
        } else {

            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("Select MAX(Appointment_ID) AS MaxId FROM appointments");
                int maxId= 0 ;
                if(resultSet.next()){
                    maxId = resultSet.getInt("MaxId");
                }
                resultSet.close();
                statement.close();
                int Id = maxId +1;
                String selectedCustomerName = appointmentCustomerId.getValue();
                String selectedUserName = appointmentUserId.getValue();
                String selectedContactName = appointmentContactId.getValue();

                String title = this.appointmentTitle.getText();
                String description = this.appointmentDescription.getText();
                String location = this.appointmentLocation.getText();
                String type =  this.appointmentType.getText();
                LocalDate selectedDate = appointmentDate.getValue();

                // Parse the selected time from the combo boxes as LocalTime
                LocalTime startTime = LocalTime.parse(appointmentStartTime.getValue(), DateTimeFormatter.ofPattern("hh:mm a"));
                LocalTime endTime = LocalTime.parse(appointmentEndTime.getValue(), DateTimeFormatter.ofPattern("hh:mm a"));




                if (startTime.isAfter(endTime)) {
                    JfxUtility.showAlertDialog("Error!", "Appointment Start Time Must" +
                            "Be Before End Time.");
                }
                else{

                    ZoneId utcZone = ZoneId.of("UTC");
                    ZonedDateTime startDateTimeinit = ZonedDateTime.of(selectedDate, startTime, ZoneOffset.UTC);
                    ZonedDateTime endDateTimeinit = ZonedDateTime.of(selectedDate, endTime, ZoneOffset.UTC);
                    ZonedDateTime startDateTime = ZonedDateTime.ofInstant(startDateTimeinit.toInstant(), utcZone);
                    ZonedDateTime endDateTime = ZonedDateTime.ofInstant(endDateTimeinit.toInstant(), utcZone);

                    int customerId = getKeyByValue(customerIdMap, selectedCustomerName);
                    int userId = getKeyByValue(userIdMap, selectedUserName);
                    int contactId = getKeyByValue(contactIdMap, selectedContactName);


                    Appointment appointment = new Appointment(Id, title, description, location, type, startDateTime, endDateTime, customerId, userId, contactId);

                    DbClass.addAppointment(appointment);

                    JfxUtility.loadView("Home.fxml", event);


                }
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}