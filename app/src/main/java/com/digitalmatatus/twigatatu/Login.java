package com.digitalmatatus.twigatatu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digitalmatatus.twigatatu.model.AppController;
import com.digitalmatatus.twigatatu.model.MyShortcuts;
import com.digitalmatatus.twigatatu.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.digitalmatatus.twigatatu.model.MyShortcuts.getDefaults;


/**
 * Created by stephineosoro on 31/05/16.
 */
public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;


    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Setting up custom font
        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        setContentView(R.layout.app_bar_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");
        applyFontForToolbarTitle(this);

        _signupLink = (TextView) findViewById(R.id.link_signup);
        _emailText = (EditText) findViewById(R.id.input_email);
        _emailText.setTypeface(mTfLight);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _passwordText.setTypeface(mTfLight);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _loginButton.setTypeface(mTfLight);

//        Autorefilling passwords on subsequent login from the sharedPreference
        if (MyShortcuts.checkDefaults("email", this)) {
            _emailText.setText(getDefaults("email", this));
            _passwordText.setText(getDefaults("password", this));
        }



        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                If it has internet, try to login into the app
                if (MyShortcuts.hasInternetConnected(getBaseContext())) {

//                     TODO Uncomment to disable demo mode and login using credentials cerated on the server
//                    login();

//                    Comment below lines to prevent automatic login without credentials
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("user_id", "3");
                    startActivity(intent);

                } else {
                    MyShortcuts.showToast("No internet connection!", getBaseContext());
                }

            }
        });


        _signupLink.setTypeface(mTfLight);
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
            }
        });

    }

    public static void applyFontForToolbarTitle(Activity context) {
        Toolbar toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

//        TODO Uncomment to show progress bar while logging in
      /*  final ProgressDialog progressDialog = new ProgressDialog(MedicLogin.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();*/

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: sending credentials to the server.
        loginPost();
//        logindetail(email, password);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
//                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
//                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //TODO Disable going back to the MainActivity
//        moveTaskToBack(true);
        super.onBackPressed();
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
//        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("Enter a valid username");
            valid = false;
        } else {
            _emailText.setError(null);
        }


        if (password.isEmpty()) {
            _passwordText.setError("Password is empty");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void loginPost() {
        Post.PostString(MyShortcuts.baseURL() + "twiga/auth/signup", _emailText.getText().toString(),_passwordText.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("data is", response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    MyShortcuts.setDefaults("user_id",jsonObject.getString("user_id"),getBaseContext());
                    Intent intent = new Intent(getBaseContext(),Login.class);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }



}


