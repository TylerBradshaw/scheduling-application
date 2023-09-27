package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Appointment;
import models.Customer;
import models.DbClass;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

import static controllers.HomeController.returnIndex;
import static models.DbClass.lookUpAppointment;

public class ModifyCustomerController implements Initializable {

    private ObservableList<Appointment> associatedAppointments = FXCollections.observableArrayList();
    public TextField customerID;
    public TextField customerName;
    public TextField customerAddress;
    public TextField customerPhone;
    public TextField customerPostal;
    public TextField customerDateCreate;
    public TextField customerCreatedBy;
    public TextField customerDateUpdated;
    public TextField customerUpdatedBy;
    public ComboBox customerDivisionId;
    public TextField searchFieldAppointment;

    public TableView<Appointment> associatedAppointmentTable;
    public TableColumn<Appointment, Integer> associatedAppointmentID;
    public TableColumn<Appointment, String> associatedAppointmentTitle;
    public TableColumn<Appointment, String> associatedAppointmentDesc;
    public TableColumn<Appointment, ZonedDateTime> associatedAppointmentStart;
    public TableColumn<Appointment, ZonedDateTime> associatedAppointmentEnd;
    int customerIndex = returnIndex();
    private Customer customer;
    /**
     * This method is called when the exit button is clicked. It exits the application.
     */
    @FXML
    void exitClicked() {
        JfxUtility.exitApplication();
    }

    /**
     * Populates the fields of the AddProductController with the information of the specified modifiable product.
     * @param modifiableCustomer The product whose information will be used to populate the fields.
     */
    public void populateCustomer(Customer modifiableCustomer) {

        customerID.setText(String.valueOf(modifiableCustomer.getId()));
        customerName.setText(modifiableCustomer.getName());
        customerAddress.setText(String.valueOf(modifiableCustomer.getAddress()));
        customerPhone.setText(String.valueOf(modifiableCustomer.getPhone()));
        customerPostal.setText(String.valueOf(modifiableCustomer.getPostalCode()));

        customer = modifiableCustomer;
        associatedAppointments = customer.getAllAssociatedAppointments();
        associatedAppointmentTable.setItems(associatedAppointments);

    }


    /**
     * Removes a selected part from the list of associated parts for the product.
     * Displays an error message if no part is selected.
     */
    @FXML
    void removeClicked() {

        Appointment appointment = associatedAppointmentTable.getSelectionModel().getSelectedItem();

        if (appointment != null) {

            JfxUtility.showConfirmationDialog("Remove this appointment?",
                    () -> customer.deleteAssociatedAppointment(appointment));
        } else {

            JfxUtility.showAlertDialog("Error!", "Select an appointment to remove.");

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

        JfxUtility.loadView("MainGUI.fxml", event);
    }


    /**
     * Called when the "Save" button is clicked in the Modify Product screen.
     * This method validates the input fields and updates the selected product if valid.
     * If any input field is empty or invalid, an error message is displayed.
     * If the current stock is not between the minimum and maximum allowed stock, an error message is displayed.
     * If the maximum allowed stock is less than the minimum allowed stock, an error message is displayed.
     * If all input fields are valid, the product is updated and the user is redirected to Main.
     * Had some issues getting the method to check for blank fields and ensuring that the
     * current stock is between the minimum and maximum values.
     * @param event the ActionEvent triggered by clicking the "Save" button
     * @throws IOException if there is an issue loading the Main screen view.
     */

    @FXML
    void saveClicked(ActionEvent event) throws IOException {

        if (JfxUtility.checkBlankFields(this.customerName.getText(), this.customerAddress.getText(),
                this.customerPhone.getText(), this.customerPostal.getText())) {
        }

        else {

            try {

                int id = Integer.parseInt(customerID.getText());
                String name = this.customerName.getText();
                String address = this.customerAddress.getText();
                String postal = this.customerPostal.getText();
                String phone = this.customerPhone.getText();
                ZonedDateTime createDate = ZonedDateTime.from(LocalDateTime.parse(this.customerDateCreate.getText()));
                String createdBy = this.customerCreatedBy.getText();
                ZonedDateTime lastUpdate = ZonedDateTime.from(LocalDateTime.parse(this.customerDateUpdated.getText()));
                String lastUpdateBy = this.customerUpdatedBy.getText();
                int divisionId = Integer.parseInt(customerDivisionId.getItems().toString());


                    Customer updatedCustomer = new Customer(id, name, address, postal, phone, divisionId);
                    customer.setId(id);
                    customer.setName(name);
                    customer.setAddress(address);
                    customer.setPostalCode(postal);
                    customer.setPhone(phone);


                    associatedAppointments.forEach(updatedCustomer::addAssociatedAppointment);

                    DbClass.updateCustomer(customerIndex, customer);

                    JfxUtility.loadView("MainGUI.fxml", event);


            } catch (NumberFormatException exception) {

                JfxUtility.showAlertDialog("Error!", "Price, current stock, minimum and maximum must be numbers.");

            }
        }
    }

    /**
     * This class is responsible for initializing the Associated Parts Table in the ModifyProductController.
     */
    private void initAssociatedPartsTable() {

        associatedAppointmentID.setCellValueFactory(new PropertyValueFactory<>("id"));
        associatedAppointmentTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
        associatedAppointmentEnd.setCellValueFactory(new PropertyValueFactory<>("address"));
        associatedAppointmentDesc.setCellValueFactory(new PropertyValueFactory<>("phone"));
        associatedAppointmentStart.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
    /**
     * Initializes the All Parts Table and Associated Parts Table.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAssociatedPartsTable();
    }
}
