package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import models.Customer;
import models.DbClass;
import models.Users;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static controllers.DBUtility.getKeyByValue;
import static controllers.DBUtility.populateComboBox;
import static controllers.JDBC.connection;

public class AddCustomerController {
    public TextField customerID;
    public TextField customerName;
    public TextField customerAddress;
    public TextField customerPhone;
    public TextField customerPostal;
    public TextField customerDateCreate;
    public TextField customerCreatedBy;
    public TextField customerDateUpdated;
    public TextField customerUpdatedBy;
    public ComboBox<String> customerDivisionId;
    public ComboBox<String> customerCountryId;
    private Map<Integer, String> customerDivisionMap = new HashMap<>();
    private Map<Integer, String> customerCountryMap = new HashMap<>();

    static int id;

    public void initialize() {
        customerCountryId.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                customerDivisionId.setDisable(false);
                String selectedCustomername = customerCountryId.getValue();
                int selectedCountryId = getKeyByValue(customerCountryMap, selectedCustomername);
                DBUtility.populateComboBox(customerDivisionId, connection,
                        "SELECT Division_ID, Division FROM first_level_divisions WHERE Country_ID = " + selectedCountryId,
                        resultSet -> {
                            try {
                                return resultSet.getInt("Division_ID");
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        resultSet -> {
                            try {
                                return resultSet.getString("Division");
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        customerDivisionMap)
                ;
            }
        });

        populateComboBox(customerCountryId, connection,
                "SELECT Country_ID, Country FROM countries",
                resultSet -> {
                    try {
                        return resultSet.getInt("Country_ID");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                resultSet -> {
                    try {
                        return resultSet.getString("Country");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                customerCountryMap)
        ;
    }

    /**
     * Cancels adding the new customer and returns to the Main screen.
     * @param event an action event that triggers the method
     * @throws IOException if the FXML resource cannot be found
     */
    @FXML
    void cancelClicked(ActionEvent event) throws IOException {

        JfxUtility.loadView("Home.fxml", event);
    }
    /**
     * This method is called when the exit button is clicked. It exits the application.
     */
    @FXML
    void exitClicked() {
        JfxUtility.exitApplication();
    }

    /**
     * Handles the event when the save button is clicked. Validates the input fields and saves the new
     * product to the inventory if all required fields are present and valid. Displays error messages if any required fields
     * are missing or invalid. After the part is successfully saved, the main GUI view is loaded. Had some issues getting the method to
     * check for blank fields and ensuring that the current stock is between the minimum and maximum values.
     * @param event The event generated when the save button is clicked.
     * @throws IOException if the FXML resource for the main GUI form is not found.
     */
    @FXML
    void saveClicked(ActionEvent event) throws IOException {

        if (JfxUtility.checkBlankFields(
                this.customerName.getText(),
                this.customerAddress.getText(),
                this.customerPhone.getText(),
                this.customerPostal.getText())||
                JfxUtility.checkBlankCombos(
                        this.customerDivisionId)) {

        } else{


            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("Select MAX(Customer_ID) AS MaxId FROM customers");
                int maxId= 0 ;
                if(resultSet.next()){
                    maxId = resultSet.getInt("MaxId");
                }
                resultSet.close();
                statement.close();
                int Id = maxId +1;
                String selectedDivision = customerDivisionId.getValue();
                String name = this.customerName.getText();
                String address = this.customerAddress.getText();
                String postal = this.customerPostal.getText();
                String phone = this.customerPhone.getText();
                int divisionId = getKeyByValue(customerDivisionMap, selectedDivision);

                Customer customer = new Customer(Id, name, address, postal, phone,divisionId);

                DbClass.addCustomer(customer);

                JfxUtility.loadView("Home.fxml", event);


            } catch (NumberFormatException | SQLException exception) {

                JfxUtility.showAlertDialog("Error!", "Price, current stock, minimum and maximum must be numbers.");

            }

        }
    }
}
