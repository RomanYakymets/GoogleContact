package com.example.roma.testtask.model;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Contact implements Serializable, Comparable<Contact>{

    private String contactId;
    private String firstName;
    private String lastName;
    private List<String> emailAdress;
    private List<String> phoneNumber;

    public Contact(String account_id, String firstName, String lastName, List<String> emailAdress, List<String> phoneNumber) {
        this.contactId = account_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAdress = emailAdress;
        this.phoneNumber = phoneNumber;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String account_id) {
        this.contactId = account_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(List<String> emailAdress) {
        this.emailAdress = emailAdress;
    }

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int compareTo(Contact contact) {

        String first = contact.getFirstName() + contact.getLastName();
        String second = this.getFirstName() + contact.getLastName();
        return second.toLowerCase(Locale.ENGLISH).compareTo(first.toLowerCase(Locale.ENGLISH));
    }
}
