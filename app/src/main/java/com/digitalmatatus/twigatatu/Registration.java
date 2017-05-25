package com.digitalmatatus.twigatatu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.digitalmatatus.twigatatu.model.AppController;
import com.digitalmatatus.twigatatu.model.MyShortcuts;
import com.digitalmatatus.twigatatu.model.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stephineosoro on 31/05/16.
 */

public class Registration extends AppCompatActivity {
    private static final String TAG = "CreateAccount";

    Button _signupButton;
    EditText _email;
    EditText _password;
    EditText _confirm;
    int succ = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");

        _signupButton = (Button) findViewById(R.id.btn_signup);
        _email = (EditText) findViewById(R.id.email);
        _password = (EditText) findViewById(R.id.input_password);
        _confirm = (EditText) findViewById(R.id.confirm_password);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();

            }
        });


    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

//        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Registration.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        // TODO: Implement your own signup logic here.
//        CreateAccount();
        createAcc();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        if (succ == 1) {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
//        Toast.makeText(getBaseContext(), "Creating Patient failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String password = _password.getText().toString();
        String confirm = _confirm.getText().toString();
        String email = _email.getText().toString();


        if (password.isEmpty() || password.length() < 6) {
            _password.setError("Input password");
            valid = false;
        } else {
            _password.setError(null);
        }
        if (confirm.isEmpty() || password.length() < 6) {
            _confirm.setError("Confirm password");
            valid = false;
        } else {
            _confirm.setError(null);
        }

//        if (confirm != password) {
//            _confirm.setError("passwords not matching");
//            _password.setError("passwords not matching");
//
//            valid = false;
//        } else {
//            _confirm.setError(null);
//        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError("input a valid email");
            valid = false;
        } else {
            _email.setError(null);
        }

        return valid;
    }

    private void CreateAccount() {
        // Toast.makeText(getBaseContext(), "Inside function!", Toast.LENGTH_SHORT).show();
        JSONObject finalJS = new JSONObject();
        try {

            finalJS.put("password", _password.getText().toString());
            finalJS.put("username", _email.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONErrorin serializing", e.toString());
        }
        Log.e("JSON serializing", finalJS.toString());
        String tag_string_req = "req_Categories";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, MyShortcuts.baseURL() + "twiga/auth/signup", finalJS,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response from server is", response.toString());
                        try {
                            String status = response.getString("success");
                            if (status.equals("true")) {
                                Toast.makeText(getBaseContext(), "You have successfully registered!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);

                                startActivity(intent);
                            } else {
                                MyShortcuts.showToast(response.getString("message"), getBaseContext());
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getBaseContext(), "Server errror, Try again later", Toast.LENGTH_LONG).show();
                        }
                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("VolleyError", "Error: " + error.getMessage());
//                hideProgressDialog()
                MyShortcuts.showToast("Check you internet connection or try again later", getBaseContext());
                Log.d("error volley", error.toString());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
                setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
                headers = MyShortcuts.AunthenticationHeaders(getBaseContext());
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        Log.e("request is", jsonObjReq.toString());
    }

    public void createAcc() {
        Post.PostString(MyShortcuts.baseURL() + "twiga/auth/signup", _email.getText().toString(),_password.getText().toString(), new Response.Listener<String>() {
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