package com.armpk.goatregistrator.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.User;
import com.armpk.goatregistrator.database.UserRole;
import com.armpk.goatregistrator.utilities.Globals;
import com.armpk.goatregistrator.utilities.RestConnection;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;
import static com.armpk.goatregistrator.utilities.RestConnection.Action.GET;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements RestConnection.OnConnectionCompleted{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "admin:admin", "user:user"
    };
    //Keep track of the login task to ensure we can cancel it if requested.
    //private UserLoginTask mAuthTask = null;
    private RestConnection mRestConn = null;

    // UI references.
    private EditText mEditTextUserName;
    private EditText mEditTextUserPass;
    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences mSharedPreferences;
    private DatabaseHelper dbHelper;

    private String mUsername;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEditTextUserName = (EditText) findViewById(R.id.edit_user_name);
        mEditTextUserName.setText(mSharedPreferences.getString(Globals.SETTING_LAST_USER_NAME, ""));

        mEditTextUserPass = (EditText) findViewById(R.id.edit_user_pass);
        mEditTextUserPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditTextUserPass.getWindowToken(), 0);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.buttonSignIn);
        if (mEmailSignInButton != null) {
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    attemptLogin();
                }
            });
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if(mEditTextUserName.getText().length()>3){
            mEditTextUserPass.requestFocus();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mRestConn != null) {
            return;
        }

        // Reset errors.
        mEditTextUserName.setError(null);
        mEditTextUserPass.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mEditTextUserName.getText().toString();
        mPassword = mEditTextUserPass.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            mEditTextUserPass.setError("Твърде кратка парола");
            focusView = mEditTextUserPass;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername) && !isUsernameValid(mUsername)) {
            mEditTextUserName.setError("Твърде кратко потребителско име");
            focusView = mEditTextUserName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            getUserRoles();

            /*mRestConn = new RestConnection(this, RestConnection.DataType.LOGIN, this);
            mRestConn.setAction(RestConnection.Action.LOGIN);
            try {
                mRestConn.addJSONAttribute("username", username);
                mRestConn.addJSONAttribute("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mRestConn.execute((Void) null);*/
        }
    }

    private void getUserRoles(){
        RestConnection mRestUserRoles = new RestConnection(this, RestConnection.DataType.USERROLES,this);
        mRestUserRoles.setAction(RestConnection.Action.GET);
        mRestUserRoles.execute((Void) null);
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length()>4;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onResultReceived(RestConnection.Action action, RestConnection.DataType dataType, String result) {
        if(result!=null){
            switch (action){
                case GET:
                    if (synchronizeUserRoles(result)){
                        mRestConn = new RestConnection(this, RestConnection.DataType.LOGIN, this);
                        //mRestConn.setDialogSwitcher(RestConnection.DialogSwitcher.CLOSER);
                        mRestConn.setAction(RestConnection.Action.LOGIN);
                        try {
                            mRestConn.addJSONAttribute("username", mUsername);
                            mRestConn.addJSONAttribute("password", mPassword);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mRestConn.execute((Void) null);
                    }else{
                        showProgress(false);
                    }
                    break;
                case LOGIN:
                    logUserIn(result);
                    break;
            }
        }else{
            RestConnection.closeProgressDialog();
            showProgress(false);
            mRestConn = null;
        }
    }

    private boolean synchronizeUserRoles(String result){
        boolean success = false;

        try {
            JSONArray res = new JSONArray(result);
            for (int i = 0; i < res.length(); i++) {
                dbHelper.updateUserRoleByDate(Globals.jsonToObject(res.getJSONObject(i), UserRole.class));
            }
            success = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return success;
    }

    public void logUserIn(String result){
        String user = null;
        String pass = null;
        long id = 0;
        String firnstn = null;
        String familyn = null;
        JSONObject res = null;
        try {
            res = new JSONObject(result);
            user = res.optString("username");
            pass = res.optString("password");
            id = res.optLong("id");
            firnstn = res.optString("firstName");
            familyn = res.optString("familyName");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        if(dbHelper.createOrUpdateUser(res)){

            Globals.savePreferences(Globals.SETTING_ACTIVE_USER_NAME, user, LoginActivity.this);
            Globals.savePreferences(Globals.SETTING_ACTIVE_USER_ID, id, LoginActivity.this);
            Globals.savePreferences(Globals.SETTING_ACTIVE_USER_FIRSTNAME, firnstn, LoginActivity.this);
            Globals.savePreferences(Globals.SETTING_ACTIVE_USER_FAMILYNAME, familyn, LoginActivity.this);
            Globals.savePreferences(Globals.SETTING_LAST_USER_NAME, user, LoginActivity.this);
            Globals.savePreferences(Globals.SETTING_USER_PASS, pass, LoginActivity.this);
            Globals.savePreferences(Globals.SETTING_SIGNED_IN, true, LoginActivity.this);

            RestConnection.closeProgressDialog();

            Log.d("DB", "LOGGED IN USER INFO UPDATED");
            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    /*public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mUsername = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mUsername)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                                logUserIn(mUsername, mPassword);
            } else {
                mEditTextUserPass.setError("Грешна парола");
                mEditTextUserPass.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }*/
}

