package com.digitalmatatus.twigatatu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
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

import static com.digitalmatatus.twigatatu.Login.applyFontForToolbarTitle;

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
    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        Setting up custom font
        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        setContentView(R.layout.app_bar_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");


        applyFontForToolbarTitle(this);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _signupButton.setTypeface(mTfLight);
        _email = (EditText) findViewById(R.id.email);
        _email.setTypeface(mTfLight);
        _password = (EditText) findViewById(R.id.input_password);
        _password.setTypeface(mTfLight);
        _confirm = (EditText) findViewById(R.id.confirm_password);
        _confirm.setTypeface(mTfLight);
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


        // TODO: signup.

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

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError("input a valid email");
            valid = false;
        } else {
            _email.setError(null);
        }

        return valid;
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