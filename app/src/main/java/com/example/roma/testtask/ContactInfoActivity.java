package com.example.roma.testtask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.roma.testtask.model.Contact;

public class ContactInfoActivity extends AppCompatActivity {

    private static final String TAG = "ContactInfoActivity";
    Toolbar mToolbar;
    TextView mFirstName, mLastName;

    LinearLayout emailAddressContainer, phoneNumberContainer;
    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        contact = (Contact) getIntent().getSerializableExtra("Contact");

        mFirstName = (TextView) findViewById(R.id.contact_info_firstname);
        mLastName = (TextView) findViewById(R.id.contact_info_lastname);

        emailAddressContainer = (LinearLayout) findViewById(R.id.contact_info__email_address_container);
        phoneNumberContainer = (LinearLayout) findViewById(R.id.contact_info__phone_number_container);

        mToolbar = (Toolbar) findViewById(R.id.contact_info__toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(contact.getFirstName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String name = getResources().getString(R.string.first_name_text_view) ;
        if (contact.getFirstName() != null){
            name = name + " " + contact.getFirstName();
        }
        mFirstName.setText(name);
        String lastName = getResources().getString(R.string.last_name_text_view);
        if(contact.getLastName() != null){
            lastName = lastName  + " " + contact.getLastName();
        }
        mLastName.setText(lastName);


        for (int i = 0; i < contact.getPhoneNumber().size(); i++){
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.add_text_view, null);
            String phoneNumber = getResources().getString(R.string.phone_number_text_view) + " "
                    + contact.getPhoneNumber().get(i);
            textView.setText(phoneNumber);
            emailAddressContainer.addView(textView);
        }

        for (int i = 0; i < contact.getEmailAdress().size(); i++){
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.add_text_view, null);
            String emailAdress = getResources().getString(R.string.email_address_text_view) + " "
                    + contact.getEmailAdress().get(i);
            textView.setText(emailAdress);
            emailAddressContainer.addView(textView);
        }
    }
}
