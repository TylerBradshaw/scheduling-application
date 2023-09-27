package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;
/**
 * A utility class for loading, populating FXML views and generating alert and confirmation dialogs.
 * It provides static methods to load views and populate them with data using a consumer in addition to alerting a user
 * to errors and confirming deletions.
 * FUTURE ENHANCEMENT: Future functionality could pull the search operations to this utility class and
 * call on the method within the controllers. In addition, search methods could be upgraded by adding a listener to
 * highlight matching parts and products as it is being typed into the search bar.
 * @author Tyler Bradshaw
 */
public class JfxUtility {


    public static ObservableList<String> populateTime(ComboBox<String> startTimeComboBox, ComboBox<String> endTimeComboBox, DatePicker datePicker) {
        ObservableList<String> startTimes = FXCollections.observableArrayList();
        ObservableList<String> endTimes = FXCollections.observableArrayList();

        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            startTimeComboBox.setDisable(true);
            endTimeComboBox.setDisable(true);
            return startTimes;
        } else {
            startTimeComboBox.setPromptText("Choose Start Time");
            endTimeComboBox.setPromptText("Choose End Time");
            startTimeComboBox.setDisable(false);
            endTimeComboBox.setDisable(false);
        }

        // Define the restricted start and end times (12 PM to 2 AM of the following day) in UTC
        ZonedDateTime startTime = ZonedDateTime.of(selectedDate, LocalTime.of(12, 0), ZoneOffset.UTC); // 12:00 PM UTC
        ZonedDateTime endTime = ZonedDateTime.of(selectedDate.plusDays(1), LocalTime.of(2, 15), ZoneOffset.UTC);   // 2:15 AM UTC

        // Get the user's time zone
        ZoneId userTimeZone = ZoneId.systemDefault();

        // Loop through the available times from 12 PM to 2 AM of the following day and add them to the combo boxes
        ZonedDateTime currentTimeSlot = startTime;
        while (currentTimeSlot.isBefore(endTime)) {
            // Convert the current time slot to the user's time zone
            ZonedDateTime zonedDateTimeUser = currentTimeSlot.withZoneSameInstant(userTimeZone);

            // Format the time and add it to the combo boxes
            String formattedTime = zonedDateTimeUser.format(DateTimeFormatter.ofPattern("hh:mm a"));
            startTimes.add(formattedTime);
            endTimes.add(formattedTime);

            // Move to the next time slot (add 15 minutes)
            currentTimeSlot = currentTimeSlot.plusMinutes(15);
        }

        startTimeComboBox.setItems(startTimes);
        endTimeComboBox.setItems(endTimes);

        return startTimes;
    }


    /**
     * This method is called when the exit button is clicked. It exits the application.
     */
    public static void exitApplication() {
        JDBC.closeConnection();
        System.exit(0);
    }
    /**
     * Loads an FXML view with the given name and sets it as the scene of the current stage.
     * @param viewName the name of the FXML file to load
     * @param event the event that triggered the view load
     * @throws IOException if the view cannot be loaded
     */

    public static void loadView(String viewName, ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(JfxUtility.class.getResource(viewName)));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads an FXML view with the given name, populates it with data using a consumer, and sets it as the scene of the current stage.
     * @param viewName the name of the FXML file to load
     * @param event the event that triggered the view load
     * @param dataPopulator a consumer that populates the view with data
     * @param <T> the type of the controller for the FXML view
     * @return the controller for the FXML view
     * @throws IOException if the view cannot be loaded
     */
    public static <T> T loadAndPopulateView(String viewName, ActionEvent event, Consumer<T> dataPopulator) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(JfxUtility.class.getResource(viewName));
        Parent root = loader.load();
        T controller = loader.getController();
        dataPopulator.accept(controller);
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        return controller;
    }
    /**
     * Checks whether any of the specified fields are blank.
     * If a blank field is found, displays an error dialog box and returns true.
     * Otherwise, returns false.
     * @param fields An array of Strings representing the fields to check for blankness
     * @return A boolean value indicating whether any blank fields were found
     */
    public static boolean checkBlankFields(String... fields) {
        for (String field : fields) {
            if (field.isBlank()) {
                JfxUtility.showAlertDialog("Error!", "Please fill out all fields.");
                return true;
            }
        }

        return false;
    }

    public static boolean checkBlankCombos(ComboBox<?>... comboBoxes) {
        for (ComboBox<?> comboBox : comboBoxes) {
            if (comboBox.getValue() == null) {
                JfxUtility.showAlertDialog("Error!", "Missing selection from one(or more) drop-down menus.");
                return true;
            }
        }

        return false;
    }

    public static boolean checkBlankDates(DatePicker... datePickers) {
        for (DatePicker datePicker : datePickers) {
            if (datePicker.getValue() == null) {
                JfxUtility.showAlertDialog("Error!", "Please select a date.");
                return true;
            }
        }

        return false;
    }
    /**
     * This method displays a warning dialog box with the specified title and message.
     * @param title The title of the dialog box.
     * @param message The message to be displayed in the dialog box.
     */

    public static void showAlertDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Shows a confirmation dialog box with the specified message and callback function.
     * The dialog box allows the user to confirm or cancel the action.
     * @param message the message to display in the dialog box
     * @param callback the function to call if the user confirms the action
     */
    public static void showConfirmationDialog(String message, Runnable callback) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete?");
        alert.setHeaderText("Delete?");
        alert.setContentText(message);
        alert.showAndWait().ifPresent(btnType -> {
            if (btnType == ButtonType.OK) {
                callback.run();
            }
        });
    }
}
