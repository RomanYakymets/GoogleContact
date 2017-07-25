package com.example.roma.testtask;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.roma.testtask.data.ContactData;
import com.example.roma.testtask.data.PeopleAdapter;
import com.example.roma.testtask.data.PeopleHelper;
import com.example.roma.testtask.model.Contact;
import com.example.roma.testtask.utils.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks, View.OnClickListener {


    private static final String TAG = "MainActivity";

    final int RC_INTENT = 200;
    final int RC_API_CHECK = 100;

    private GoogleApiClient mGoogleApiClient;
    SignInButton mGoogleSignInButton;
    Toolbar mToolbar;
    PeopleService peopleService;

    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private PeopleAdapter adapter;
    private ContactData contactData;

    private SharedPreferences sharedPreferences;
    String currentUserLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mToolbar = (Toolbar) findViewById(R.id.new_contact_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Contacts");

        contactData = new ContactData(this);

        mGoogleSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);
        mGoogleSignInButton.setOnClickListener(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, NewContactActivity.class);
                startActivity(intent);
            }
        });

        fab.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // The serverClientId is an OAuth 2.0 web client ID
                .requestServerAuthCode(getString(R.string.clientID))
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN),
                        new Scope("https://www.googleapis.com/auth/contacts"))
                .build();


        // To connect with Google Play Services and Sign In
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        if (sharedPreferences.contains(Constants.SIGNED_IN)){
                //updateUI(true);
            getIdToken();
            Log.d(TAG, sharedPreferences.getString(Constants.SIGNED_IN, Constants.SIGNED_IN_BY_GOOGLE_PLUS));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_INTENT:
                Log.d(TAG, "sign in result");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()) {
                    sharedPreferences.edit().putString(Constants.SIGNED_IN, Constants.SIGNED_IN_BY_GOOGLE_PLUS).apply();
                    GoogleSignInAccount acct = result.getSignInAccount();
                    Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());
                    // This is what we need to exchange with the server.
                    Log.d(TAG, "auth Code:" + acct.getServerAuthCode());

                    currentUserLogIn = acct.getId();

                    new MainActivity.PeoplesAsync().execute(acct.getServerAuthCode());

                } else {

                    Log.d(TAG, result.getStatus().toString());
                }
                break;

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                Log.d(TAG, "btn click");
                getIdToken();
                break;
        }
    }

    private void signOutGP() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        sharedPreferences.edit().remove(Constants.SIGNED_IN).apply();
                        updateUI(false);
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("connection", "msg: " + connectionResult.getErrorMessage());

        GoogleApiAvailability mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = mGoogleApiAvailability.getErrorDialog(this, connectionResult.getErrorCode(), RC_API_CHECK);
        dialog.show();
    }

    private void getIdToken() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_INTENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log_out_btn:
                signOutGP();
                return true;
            case R.id.menu_sort_contacts_btn:
                adapter.sortData();
                return true;
            case R.id.menu_get_contact_from_database_btn:
                List<Contact> contactList = contactData.getAllContacts(currentUserLogIn);
                adapter = new PeopleAdapter(MainActivity.this, contactList, peopleService);
                recyclerView.setAdapter(adapter);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void updateUI(boolean signedIn) {
        if (signedIn) {

            findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            mToolbar.getMenu().setGroupVisible(R.id.menu_items_group, true);

        } else {

            findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            mToolbar.getMenu().setGroupVisible(R.id.menu_items_group, false);
            if(adapter != null){
                adapter.clearData();
            }
        }

    }

    class PeoplesAsync extends AsyncTask<String, Void, List<Contact>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            updateUI(true);

        }

        @Override
        protected List<Contact> doInBackground(String... params) {

            List<Contact> contactList = new ArrayList<>();

            try {

                peopleService = PeopleHelper.setUp(MainActivity.this, params[0]);

                ListConnectionsResponse response = peopleService.people().connections()
                        .list("people/me")
                        .setPersonFields("names,emailAddresses,phoneNumbers")
                        .execute();

                if(response.getConnections() == null){
                    return contactList;
                }

                List<Person> connections = response.getConnections();

                for (Person person : connections) {
                    if (!person.isEmpty()) {

//                        List<Name> names = person.getNames();
                        List<EmailAddress> emailAddresses = person.getEmailAddresses();
                        List<PhoneNumber> phoneNumbers = person.getPhoneNumbers();

                        List<String> emailAddressesStrings = new ArrayList<>();
                        List<String> phoneNumbersStrings = new ArrayList<>();

                        if (phoneNumbers != null){
                            for (PhoneNumber phoneNumber : phoneNumbers){
                                phoneNumbersStrings.add(phoneNumber.getValue());
                                Log.d(TAG, "phone: " + phoneNumber.getValue());
                            }
                        }
                        if (emailAddresses != null){
                            for (EmailAddress emailAddress : emailAddresses){
                                emailAddressesStrings.add(emailAddress.getValue());
                                Log.d(TAG, "email: " + emailAddress.getValue());
                            }
                        }

//                        if (names != null)
//                            for (Name name : names)
//                                Log.d(TAG, "nsme: " + name.getGivenName());
                        Log.d(TAG, "account_id: " +  person.getResourceName());

                        String firstName = "";
                        String lastName = "";
                        if(person.getNames() != null){
                            firstName = person.getNames().get(0).getGivenName();
                            lastName = person.getNames().get(0).getFamilyName();
                        }

                        Contact contact = new Contact(person.getResourceName()
                                ,firstName
                                ,lastName
                                ,emailAddressesStrings
                                ,phoneNumbersStrings);

                        contactList.add(contact);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "IOException  " + e.getMessage());
            }

            return contactList;
        }


        @Override
        protected void onPostExecute(List<Contact> contactList) {
            super.onPostExecute(contactList);

            if(!contactList.isEmpty()){
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new PeopleAdapter(MainActivity.this, contactList, peopleService);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(adapter);

                contactData.insertContacts(contactList, currentUserLogIn);
            }

            progressBar.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
    }
}
