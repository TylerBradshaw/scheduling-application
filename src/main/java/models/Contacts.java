package models;

public class Contacts {
    private Integer ContactId;
    private String ContactName;
    private String ContactEmail;

    public Contacts(Integer contactId, String contactName, String contactEmail) {
        ContactId = contactId;
        ContactName = contactName;
        ContactEmail = contactEmail;
    }

    public int getContactId() {
        return ContactId;
    }

    public void setContactId(Integer contactId) {
        ContactId = contactId;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getContactEmail() {
        return ContactEmail;
    }

    public void setContactEmail(String contactEmail) {
        ContactEmail = contactEmail;
    }
}
