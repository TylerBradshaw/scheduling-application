package controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import models.Appointment;
import models.DbClass;
import models.Users;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static controllers.DBUtility.getKeyByValue;
import static controllers.DBUtility.populateComboBox;
import static controllers.HomeController.returnIndex;
import static controllers.JDBC.connection;
import static java.time.ZoneOffset.UTC;

public class ModifyAppointmentController implements Initializable {


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


    /**
     * This method is called when the exit button is clicked. It exits the application.
     */
    @FXML
    void exitClicked() {
        JfxUtility.exitApplication();
    }
    /**
     * This method populates the text fields with the data from the modifiable part.
     * If the part is an instance of Outsourced, it selects the Outsourced radio button and populates the Company Name text field.
     * If the part is an instance of InHouse, it selects the In-House radio button and populates the Machine ID text field.
     * @param modifiableAppointment the part that is being modified
     */
    public void populateAppointment(Appointment modifiableAppointment) {

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
        ZoneId zoneId = ZoneId.systemDefault();
        appointmentId.setText(String.valueOf(modifiableAppointment.getId()));
        appointmentTitle.setText(modifiableAppointment.getTitle());
        appointmentDescription.setText(modifiableAppointment.getDescription());
        appointmentLocation.setText(modifiableAppointment.getLocation());
        appointmentType.setText(modifiableAppointment.getType());

        ZonedDateTime startDateTime = modifiableAppointment.getStartDateTime().withZoneSameInstant(UTC);
        ZonedDateTime endDateTime = modifiableAppointment.getEndDateTime().withZoneSameInstant(UTC);;


        LocalDate startDate = startDateTime.toLocalDate();
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = endDateTime.toLocalTime();

        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        appointmentDate.setValue(startDate);


        ObservableList<String> availableTimes = JfxUtility.populateTime(appointmentStartTime, appointmentEndTime, appointmentDate);


        appointmentStartTime.setItems(availableTimes);
        appointmentEndTime.setItems(availableTimes);


        appointmentStartTime.setValue(startTimeStr);
        appointmentEndTime.setValue(endTimeStr);



        String customerName = customerIdMap.get(modifiableAppointment.getCustomerId());
        String userName = userIdMap.get(modifiableAppointment.getUserId());
        String contactName = contactIdMap.get(modifiableAppointment.getContactId());

        appointmentCustomerId.setValue(customerName);
        appointmentUserId.setValue(userName);
        appointmentContactId.setValue(contactName);




        }

    /**
     * This method is called when the save button is clicked, and it saves the modified part.
     * If any of the fields are empty, it displays an error message to the user.
     * If the values are valid, it updates the part's attributes and saves the changes to the inventory.
     * If the user selects a radio button for In-House, the method creates an InHouse object and
     * updates the inventory with the modified part. If the user selects a radio button for Outsourced,
     * the method creates an Outsourced object and updates the inventory with the modified part.
     * Had some issues getting the method to check for blank fields and
     * ensuring that the current stock is between the minimum and maximum values.
     * @param event the action of clicking the save button.
     * @throws IOException if the resource cannot be loaded.
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
                int id =Integer.parseInt(appointmentId.getText());
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

                ZonedDateTime startDateTime = ZonedDateTime.of(selectedDate, startTime, UTC);
                ZonedDateTime endDateTime = ZonedDateTime.of(selectedDate, endTime, UTC);



                // Check if all the required items are selected
                if ((selectedCustomerName == null) && (selectedUserName == null) && (selectedContactName == null)) {

                    JfxUtility.showAlertDialog("Error!", "Missing Selections.");

                } else if (startTime.isAfter(endTime)) {
                    JfxUtility.showAlertDialog("Error!", "Appointment Start Time Must" +
                            "Be Before End Time.");
                }
                else{

                    int customerId = getKeyByValue(customerIdMap, selectedCustomerName);
                    int userId = getKeyByValue(userIdMap, selectedUserName);
                    int contactId = getKeyByValue(contactIdMap, selectedContactName);


                    Appointment appointment = new Appointment(id, title, description, location, type, startDateTime, endDateTime, customerId, userId, contactId);

                    appointment.setId(id);
                    appointment.setTitle(title);
                    appointment.setDescription(description);
                    appointment.setLocation(location);
                    appointment.setType(type);
                    appointment.setStartDateTime(startDateTime);
                    appointment.setEndDateTime(endDateTime);

                    appointment.setCustomerId(customerId);
                    appointment.setUserId(userId);
                    appointment.setContactId(contactId);



                    DbClass.updateAppointment(appointment);

                    JfxUtility.loadView("Home.fxml", event);


                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * This method is called when the Cancel button is clicked.
     * It loads the MainGUI.fxml file and sets the scene to the stage.
     * @param event the action event triggered by the user clicking the Cancel button
     * @throws IOException if the FXMLLoader is unable to load the MainGUI.fxml file
     */
    @FXML
    void cancelClicked(ActionEvent event) throws IOException {

        JfxUtility.loadView("Home.fxml", event);
    }

    /**
     * This method is called by the FXMLLoader when the controller is initialized.
     * @param url the URL of the FXML file that was loaded
     * @param resourceBundle the resource bundle for the FXML file
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
