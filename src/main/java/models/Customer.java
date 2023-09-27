package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.ZonedDateTime;

public class Customer {
    private final ObservableList<Appointment> associatedAppointments = FXCollections.observableArrayList();
    private Integer id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private Integer divisionId;

    public Customer(Integer id, String name, String address, String postalCode, String phone, Integer divisionId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Integer getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Integer divisionId) {
        this.divisionId = divisionId;
    }

    public void addAssociatedAppointment(Appointment appointment) {

        associatedAppointments.add(appointment);
    }
    /**
     * This method removes the selected associated part from the list of associated parts for this product.
     * @param selectedAppointment the part to be removed from the list of associated parts
     * @return true if the part was removed successfully, false otherwise
     */
    public boolean deleteAssociatedAppointment(Appointment selectedAppointment) {
        return associatedAppointments.remove(selectedAppointment);
    }
    /**
     * This method returns a list of all associated parts for this product.
     * @return an ObservableList of all associated parts for this product
     */
    public ObservableList<Appointment> getAllAssociatedAppointments() {

        return associatedAppointments;
    }
}
