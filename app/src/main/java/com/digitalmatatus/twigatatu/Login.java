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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


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
        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        setContentView(R.layout.app_bar_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        setTypeface(mTfLight);
        setTitle("Twiga Tatu");
        applyFontForToolbarTitle(this);

        _signupLink = (TextView) findViewById(R.id.link_signup);


        _emailText = (EditText) findViewById(R.id.input_email);
        _emailText.setTypeface(mTfLight);

        _passwordText = (EditText) findViewById(R.id.input_password);
        _passwordText.setTypeface(mTfLight);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _loginButton.setTypeface(mTfLight);

//        MyShortcuts.setDefaults("url","http://10.38.32.7:8081/knap2",this);
        _emailText.setText(getDefaults("email", this));
        _passwordText.setText(getDefaults("password", this));

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MyShortcuts.hasInternetConnected(getBaseContext())) {

                    Intent intent = new Intent(getApplicationContext(), RadarChartActivitry.class);
                    intent.putExtra("user_id", "3");
                    startActivity(intent);
//                    login();
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
//                startActivityForResult(intent, REQUEST_SIGNUP);
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

      /*  final ProgressDialog progressDialog = new ProgressDialog(MedicLogin.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();*/

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        logindetail(email, password);
        // TODO: Implement your own authentication logic here.

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
        // Disable going back to the MainActivity
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

       /* if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }*/

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

    private void logindetail(final String username, final String password) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                MyShortcuts.baseURL() + "twiga/auth/login", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("All Data", "response from the server is: " + response.toString());
//                hideDialog();

//                Log.e("Url is",  MyShortcuts.getDefaults("url",getBaseContext()) + "twiga/auth/login?" + "username=" + username + "&password=" + password);

                try {
                    JSONObject jObj = new JSONObject(response);
                    String success = jObj.getString("success");
                    String session = "";
                    if (success.equals("true")) {
                        session = jObj.getString("user_id");
                        setDefaults("email", _emailText.getText().toString(), getBaseContext());
                        setDefaults("password", _passwordText.getText().toString(), getBaseContext());


//                    MyShortcuts.setDefaults("user",js.getString(""));
                        MyShortcuts.setDefaults("user_id", session, getBaseContext());
                        MyShortcuts.set(_emailText.getText().toString(), _passwordText.getText().toString(), getBaseContext());
                        Intent intent = new Intent(getApplicationContext(), RadarChartActivitry.class);
                        intent.putExtra("user_id", session);
                        startActivity(intent);
                    } else {
                        MyShortcuts.showToast("Wrong credentials, please try again", getBaseContext());
                    }


//                    String success = jObj.getString("success");


                } catch (JSONException e) {
                    MyShortcuts.showToast("Wrong credentials, please try again", getBaseContext());

                    // JSON error
//                   loginUser(username,password);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Getting data error", "Error: " + error.getMessage());
//                Log.e("Url is", MyShortcuts.baseURL() + "cargo_handling/api/login/?" + "username=" + username + "&password=" + password);

                Toast.makeText(getApplicationContext(),
                        "Check your credentials or internet connectivity!", Toast.LENGTH_LONG).show();
//                loginUser(username,password);
//                hideDialog();
            }
        }) {
           /* @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                setRetryPolicy(new DefaultRetryPolicy(5* DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
                setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
                headers.put("Content-Type", "application/json; charset=utf-8");
                String creds = String.format("%s:%s",username,password);
                Log.e("pass",password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", auth);
                return headers;
            }*/

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", _emailText.getText().toString());
                params.put("password", _passwordText.getText().toString());

                setDefaults("email", _emailText.getText().toString(), getBaseContext());
                setDefaults("password", _passwordText.getText().toString(), getBaseContext());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    /*private void loginUser(final String username, final String password) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                MyShortcuts.baseURL()+"login/customer", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("All Data", "response from the server is: " + response.toString());
//                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
//                    String success = jObj.getString("success");
                    setDefaults("email", _emailText.getText().toString(), getBaseContext());
                    setDefaults("password", _passwordText.getText().toString(), getBaseContext());
                    MyShortcuts.set( _emailText.getText().toString(),_passwordText.getText().toString(),getBaseContext());
                    Intent intent = new Intent(getApplicationContext(), GetPrescription.class);
                    startActivity(intent);

                } catch (JSONException e) {
                    // JSON error
                    MyShortcuts.showToast("Wrong username/password",getBaseContext());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Getting data error", "Error: " + error.getMessage());

                if (MyShortcuts.hasInternetConnected(getBaseContext())){
                    Toast.makeText(getApplicationContext(),
                            "No internet connection", Toast.LENGTH_LONG).show();
                }else{
                Toast.makeText(getApplicationContext(),
                        "wrong username/password", Toast.LENGTH_LONG).show();}
//                hideDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                setRetryPolicy(new DefaultRetryPolicy(5* DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
                setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
                headers.put("Content-Type", "application/json; charset=utf-8");
                String creds = String.format("%s:%s",username,password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", auth);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", _emailText.getText().toString());
                params.put("password", _passwordText.getText().toString());
                setDefaults("email", _emailText.getText().toString(), getBaseContext());
                setDefaults("password",_passwordText.getText().toString(),getBaseContext());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }*/
    public void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

}


