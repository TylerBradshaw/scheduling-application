package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import models.Users;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;


public class DBUtility {
    private static String getCurrentDateTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    private static void logActivity(String username, boolean isSuccess) throws IOException{
        String activityLog = String.format("%s  -  %s:  %s", username, getCurrentDateTime(), isSuccess ? "Success" : "Failure");
        try (PrintWriter writer = new PrintWriter(new FileWriter("login_activity.txt", true))){
            writer.println(activityLog);
        }
    }
    public static void logIn(ActionEvent event, String username, String password) {
        Connection connection = JDBC.getConnection();
        try {
            PreparedStatement currentStatement = connection.prepareStatement("SELECT Password FROM users WHERE User_Name = ?");
            currentStatement.setString(1, username);
            ResultSet currentResult = currentStatement.executeQuery();

            if (!currentResult.isBeforeFirst()) {
                JfxUtility.showAlertDialog("Error", "User not found.");
            } else {
                boolean loginSuccessful = false;
                while (currentResult.next()) {
                    String retrievedPassword = currentResult.getString("Password");
                    if (retrievedPassword.equals(password)) {
                        JfxUtility.loadView("Home.fxml", event);
                        loginSuccessful = true;
                    }
                }
                logActivity(username, loginSuccessful); // Log the login attempt
                Users.setLoggedInUsername(username);
                if (!loginSuccessful) {
                    JfxUtility.showAlertDialog("Error", "Incorrect Password.");
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void populateComboBox(ComboBox<String> comboBox, Connection connection, String query,
                                        Function<ResultSet, Integer> idMapper, Function<ResultSet, String> nameMapper,
                                        Map<Integer, String> idMap) {
        ObservableList<String> comboList = FXCollections.observableArrayList();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Integer id = idMapper.apply(resultSet);
                if (!idMap.containsKey(id)) {
                    String name = nameMapper.apply(resultSet);
                    comboList.add(name);
                    idMap.put(id, name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        comboBox.setItems(comboList);
    }

    // Helper method to get the key (ID) by the corresponding value (name) in the map
    public static Integer getKeyByValue(Map<Integer, String> map, String value) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}

