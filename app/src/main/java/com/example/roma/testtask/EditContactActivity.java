package com.example.roma.testtask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.roma.testtask.data.ContactAddHelper;
import com.example.roma.testtask.data.PeopleHelper;
import com.example.roma.testtask.model.Contact;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.util.List;

public class EditContactActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks {


    final int EDIT_INTENT = 203;

    private static final String TAG = "NewContactActivity";
    Toolbar mToolbar;
    private TextInputLayout mFirstName, mLastName, mEmail, mPhoneNumber;
    LinearLayout mEmailContainer, mPhoneNumberContainer;
    ImageButton mAddPhoneBtn, mAddEmailBtn;

    Button mSaveContactBtn;

    GoogleApiClient mGoogleApiClient;
    ContactAddHelper contactAddHelper;
    Contact editContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        mToolbar = (Toolbar) findViewById(R.id.new_contact_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactAddHelper = new ContactAddHelper();

        initView();
        initGoogleApi();
        initData();

        mAddEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmailField();
            }
        });

        mAddPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoneField();
            }
        });

        mSaveContactBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, EDIT_INTENT);

            }
        });
    }

    private TextInputLayout addPhoneField() {
        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.new_row, null);

        TextInputLayout textOut = (TextInputLayout) addView.findViewById(R.id.new_row);
        textOut.getEditText().setHint(R.string.phone_hint);
        textOut.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);

        ImageButton buttonRemove = (ImageButton) addView.findViewById(R.id.new_row_remove_btn);

        final View.OnClickListener thisListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) addView.getParent()).removeView(addView);
            }
        };
        buttonRemove.setOnClickListener(thisListener);
        mPhoneNumberContainer.addView(addView);
        return textOut;
    }

    private TextInputLayout addEmailField() {
        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.new_row, null);

        TextInputLayout textOut = (TextInputLayout) addView.findViewById(R.id.new_row);
        textOut.getEditText().setHint(R.string.email_hint);
        textOut.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        ImageButton buttonRemove = (ImageButton) addView.findViewById(R.id.new_row_remove_btn);

        final View.OnClickListener thisListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) addView.getParent()).removeView(addView);
            }
        };
        buttonRemove.setOnClickListener(thisListener);
        mEmailContainer.addView(addView);
        return textOut;
    }

    private void initGoogleApi() {

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // The serverClientId is an OAuth 2.0 web client ID
                .requestServerAuthCode(getString(R.string.clientID))
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN),
                        new Scope(PeopleServiceScopes.CONTACTS))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

    }

    private void initData() {

        editContact = (Contact) getIntent().getSerializableExtra("contact");

        mFirstName.getEditText().setText(editContact.getFirstName());
        mLastName.getEditText().setText(editContact.getLastName());

        for (int i = 0; i < editContact.getEmailAdress().size(); i++){
            String emailAdress = editContact.getEmailAdress().get(i);
            if(i == 0){
                mEmail.getEditText().setText(emailAdress);
            }else {
                TextInputLayout emailInputLayout = addEmailField();
                emailInputLayout.getEditText().setText(emailAdress);
            }
        }

        for (int i = 0; i < editContact.getPhoneNumber().size(); i++){
            String phoneNumber = editContact.getPhoneNumber().get(i);
            if(i == 0){
                mPhoneNumber.getEditText().setText(phoneNumber);
            }else {
                TextInputLayout phoneNumberInputLayout = addPhoneField();
                phoneNumberInputLayout.getEditText().setText(phoneNumber);
            }
        }

    }

    private void initView() {
        mAddPhoneBtn = (ImageButton) findViewById(R.id.new_contact_add_phone_btn);
        mAddEmailBtn = (ImageButton) findViewById(R.id.new_contact_add_email_btn);

        mSaveContactBtn = (Button) findViewById(R.id.new_contact_save_contact_btn);

        mFirstName = (TextInputLayout) findViewById(R.id.new_contact_first_name);
        mLastName = (TextInputLayout) findViewById(R.id.new_contact_last_name);
        mEmail = (TextInputLayout) findViewById(R.id.new_contact_email);
        mPhoneNumber = (TextInputLayout) findViewById(R.id.new_contact_phone_number);

        mEmailContainer = (LinearLayout) findViewById(R.id.new_contact_container_email);
        mPhoneNumberContainer = (LinearLayout) findViewById(R.id.new_contact_container_phone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case EDIT_INTENT:

                Log.d(TAG, "sign in result EDIT_INTENT");

                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()) {

                    GoogleSignInAccount acct = result.getSignInAccount();
                    Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

                    String firstName = mFirstName.getEditText().getText().toString();
                    String lastName = mLastName.getEditText().getText().toString();

                    List<String> phoneNumbers = contactAddHelper.getAllPhones(mPhoneNumber, mPhoneNumberContainer);
                    List<String> emailAddress = contactAddHelper.getAllEmails(mEmail, mEmailContainer);

                    editContact.setFirstName(firstName);
                    editContact.setLastName(lastName);
                    editContact.setPhoneNumber(phoneNumbers);
                    editContact.setEmailAdress(emailAddress);

                    new EditContactActivity.EditPeoplesAsync().execute(acct.getServerAuthCode());

                } else {

                    Log.d(TAG, result.getStatus().toString() + "\nmsg: " + result.getStatus().getStatusMessage());
                }
                break;

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class EditPeoplesAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                PeopleService peopleService = PeopleHelper.setUp(EditContactActivity.this, params[0]);

                Person contactToUpdate = peopleService.people()
                        .get(editContact.getContactId())
                        .setPersonFields("names,emailAddresses,phoneNumbers")
                        .execute();
                contactAddHelper.updateContact(contactToUpdate, editContact);

                peopleService.people()
                        .updateContact(contactToUpdate.getResourceName(), contactToUpdate)
                        .setUpdatePersonFields("names,emailAddresses,phoneNumbers")
                        .execute();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "IOException " + e.getMessage());
            }
            return null;
        }
    }
}
