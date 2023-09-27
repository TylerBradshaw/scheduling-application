module schedulingapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens Main to javafx.fxml;
    exports Main;
    exports controllers;
    exports models;
    opens controllers to javafx.fxml;
}