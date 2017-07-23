package com.example.roma.testtask.data;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.example.roma.testtask.R;
import com.example.roma.testtask.model.Contact;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

public class ContactAddHelper {

    public List<String> getAllEmails(TextInputLayout mEmail, LinearLayout mEmailContainer){

        List<String> emailList = new ArrayList<>();
        if(!mEmail.getEditText().getText().toString().isEmpty()){
            emailList.add(mEmail.getEditText().getText().toString());
        }

        int childCount = mEmailContainer.getChildCount();
        for(int i = 0; i < childCount; i++){
            View thisChild = mEmailContainer.getChildAt(i);

            TextInputLayout childTextInputLayout = (TextInputLayout) thisChild.findViewById(R.id.new_row);

            if(!childTextInputLayout.getEditText().getText().toString().isEmpty()) {
                emailList.add(childTextInputLayout.getEditText().getText().toString());
            }

        }
        return emailList;
    }

    public List<String> getAllPhones(TextInputLayout mPhoneNumber, LinearLayout mPhoneNumberContainer){

        List<String> phoneList = new ArrayList<>();

        if(!mPhoneNumber.getEditText().getText().toString().isEmpty()){
            phoneList.add(mPhoneNumber.getEditText().getText().toString());
        }

        int childCount = mPhoneNumberContainer.getChildCount();
        for(int i = 0; i < childCount; i++){
            View thisChild = mPhoneNumberContainer.getChildAt(i);

            TextInputLayout childTextInputLayout = (TextInputLayout) thisChild.findViewById(R.id.new_row);

            if(!childTextInputLayout.getEditText().getText().toString().isEmpty()) {
                phoneList.add(childTextInputLayout.getEditText().getText().toString());
            }

        }
        return phoneList;
    }

    public Person createContact(String firstName, String lastName
            , List<String> phoneNumbers, List<String> emailAddress){

        Person contactToCreate = new Person();

        List<com.google.api.services.people.v1.model.Name> names = new ArrayList<>();
        List<com.google.api.services.people.v1.model.PhoneNumber> googlePhoneNumber = new ArrayList<>();
        List<com.google.api.services.people.v1.model.EmailAddress> googleEmailAddres = new ArrayList<>();

        names.add(new Name()
                .setGivenName(firstName)
                .setFamilyName(lastName));

        for (int i = 0; i < phoneNumbers.size(); i++){
            googlePhoneNumber.add(new PhoneNumber().setValue(phoneNumbers.get(i)));
        }
        for (int i = 0; i < emailAddress.size(); i++){
            googleEmailAddres.add(new EmailAddress().setValue(emailAddress.get(i)));
        }

        contactToCreate.setNames(names);
        contactToCreate.setEmailAddresses(googleEmailAddres);
        contactToCreate.setPhoneNumbers(googlePhoneNumber);

        return contactToCreate;
    }

    public Person updateContact(Person contactToUpdate, Contact editContact){

        List<com.google.api.services.people.v1.model.Name> names = new ArrayList<>();
        List<com.google.api.services.people.v1.model.PhoneNumber> googlePhoneNumber = new ArrayList<>();
        List<com.google.api.services.people.v1.model.EmailAddress> googleEmailAddres = new ArrayList<>();

        names.add(new Name()
                .setGivenName(editContact.getFirstName())
                .setFamilyName(editContact.getLastName()));

        for (int i = 0; i < editContact.getPhoneNumber().size(); i++){
            googlePhoneNumber.add(new PhoneNumber().setValue(editContact.getPhoneNumber().get(i)));
        }
        for (int i = 0; i < editContact.getEmailAdress().size(); i++){
            googleEmailAddres.add(new EmailAddress().setValue(editContact.getEmailAdress().get(i)));
        }

        contactToUpdate.setNames(names);
        contactToUpdate.setEmailAddresses(googleEmailAddres);
        contactToUpdate.setPhoneNumbers(googlePhoneNumber);

        return contactToUpdate;
    }
}

