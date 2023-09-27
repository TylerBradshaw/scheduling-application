package Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import controllers.DBUtility;

import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class LoginController {
    @FXML
    private Button loginButton;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label timezoneToSet;
    @FXML
    private Label timezoneLabel;
    @FXML
    private Label header;


    @FXML
    public void initialize(){
        Locale userLocale = Locale.getDefault();
        TimeZone timeZone = TimeZone.getDefault();
        ZoneId zoneId = timeZone.toZoneId();
        timezoneToSet.setText(zoneId.getId());

        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", userLocale);

        loginButton.setText(resourceBundle.getString("button.login"));
        header.setText(resourceBundle.getString("header.label"));
        timezoneLabel.setText(resourceBundle.getString("timezone.label"));
        username.setPromptText(resourceBundle.getString("username.prompt"));
        password.setPromptText(resourceBundle.getString("password.prompt"));

    }
    @FXML
    void loginButtonClicked(ActionEvent event) {
        DBUtility.logIn(event, username.getText(),password.getText());
    }


}