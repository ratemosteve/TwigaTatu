package com.digitalmatatus.twigatatu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digitalmatatus.twigatatu.model.AppController;
import com.digitalmatatus.twigatatu.model.MyShortcuts;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FareVisualization extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    MaterialSpinnerAdapter<String> adapter;
    MaterialSpinner spinner;
    ArrayList<String> data2 = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_visualization);

        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        if (MyShortcuts.hasInternetConnected(this)) {
//            TODO uncomment to enable the progress bar
           /* mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Getting data ...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();*/

//            spinner.setItems("Room 1", "Room 2", "Room 3", "Room 4", "Presidential Room®");
//            getStops();
        } else {
            MyShortcuts.showToast("Please turn on your data connection!", getBaseContext());
        }
    }


        /*spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();

            }
        });
*/


    private void getData() {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                MyShortcuts.baseURL() + "twiga/budgeting/expenditure", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("All Data", "response from the server is: " + response.toString());
//                hideDialog();

//                Log.e("Url is",  MyShortcuts.getDefaults("url",getBaseContext()) + "twiga/auth/login?" + "username=" + username + "&password=" + password);


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
                params.put("user_id", "3");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}

