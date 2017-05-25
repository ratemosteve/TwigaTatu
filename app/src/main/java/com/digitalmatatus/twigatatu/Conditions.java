package com.digitalmatatus.twigatatu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digitalmatatus.twigatatu.model.AppController;
import com.digitalmatatus.twigatatu.model.MyShortcuts;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.ceryle.radiorealbutton.library.RadioRealButton;
import co.ceryle.radiorealbutton.library.RadioRealButtonGroup;

public class Conditions extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private RadioRealButton button1, button2, button3, button4, button5, button6, button7, button8, button9, button10;
    private RadioRealButtonGroup group1, group2, group3, group4, group5;
    private String peak,rush, demand, traffic,weather;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_conditions);

        getData2();
        editText=(EditText)findViewById(R.id.inputSearchEditText);


//        button1 = (Button) findViewById(R.id.button1);
        group1 = (RadioRealButtonGroup) findViewById(R.id.group1);

//        button2 = (Button) findViewById(R.id.button2);

//        button3 = (Button) findViewById(R.id.button3);
        group2 = (RadioRealButtonGroup) findViewById(R.id.group2);

//        button4 = (Button) findViewById(R.id.button4);

//        button5 = (Button) findViewById(R.id.button5);
        group3 = (RadioRealButtonGroup) findViewById(R.id.group3);

//        button6 = (Button) findViewById(R.id.button6);

//        button7 = (Button) findViewById(R.id.button7);
        group4 = (RadioRealButtonGroup) findViewById(R.id.group4);

//        button8 = (Button) findViewById(R.id.button8);

//        button9 = (Button) findViewById(R.id.button9);
        group5 = (RadioRealButtonGroup) findViewById(R.id.group5);

//        button10 = (Button) findViewById(R.id.button10);


        getIntent().getStringExtra("stop_from");
//TODO Put this in a button to show once a person clicks it
        Button button = (Button) findViewById(R.id.pick_time);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Conditions.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                dpd.show(getFragmentManager(), "Date Picker");
            }
        });

        group1.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                MyShortcuts.showToast(button.getText().toString(), getBaseContext());
                peak=button.getText().toString();
                Log.e("peak",button.getText().toString());
//                updateText(position, button1);
            }
        });

        group2.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                MyShortcuts.showToast(button.getText().toString(), getBaseContext());
                rush=button.getText().toString();
                Log.e("rush",button.getText().toString());

//                updateText(position, button2);
            }
        });
        group3.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                MyShortcuts.showToast(button.getText().toString(), getBaseContext());
                demand=button.getText().toString();
//                updateText(position, button1);
            }
        });
        group4.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                MyShortcuts.showToast(button.getText().toString(), getBaseContext());
                traffic=button.getText().toString();
//                updateText(position, button1);
            }
        });


        group5.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                MyShortcuts.showToast(button.getText().toString(), getBaseContext());
                weather=button.getText().toString();
//                updateText(position, button1);
            }
        });



       /* button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = group1.getPosition();

                position = ++position % group1.getNumberOfButtons();
                group1.setPosition(position);
            }
        });*/

        Button bt = (Button)findViewById(R.id.submit_conditions);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                postFare();
                Intent intent = new Intent(getBaseContext(),LineChartActivity.class);
                startActivity(intent);
            }
        });

    }

    private void updateText(int position, Button button) {
        button.setText("Position: " + position);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Date d = new Date();
        CharSequence s = DateFormat.format("hh:mm:ss", d.getTime());
        editText.setText(dayOfMonth+"/"+monthOfYear+"/"+year+" "+s);

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

    }


    private void postFare() {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                MyShortcuts.baseURL() + "twiga/fares/addFare", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("All Data", "response from the server is: " + response.toString());
//                hideDialog();

//                Log.e("Url is",  MyShortcuts.getDefaults("url",getBaseContext()) + "twiga/auth/login?" + "username=" + username + "&password=" + password);

                try {
                    JSONObject jObj = new JSONObject(response);

                    Intent intent = new Intent(getBaseContext(),RadarChartActivitry.class);
                    startActivity(intent);
                    /*String success = jObj.getString("success");
                    String session = "";
                    if (success.equals("true")) {
                        session = jObj.getString("user_id");

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("user_id", session);
                        startActivity(intent);
                    } else {
                        MyShortcuts.showToast("Wrong credentials, please try again", getBaseContext());
                    }
*/

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
                /*params.put("stop_from", getIntent().getStringExtra("stop_from"));
                params.put("stop_to", getIntent().getStringExtra("stop_to"));
                params.put("amount", getIntent().getStringExtra("amount"));
                params.put("route", " fs");
                params.put("weather", weather);
                params.put("traffic", traffic);
                params.put("demand", demand);
                params.put("rush_hour", rush);
                params.put("peak", peak);*/

                params.put("stop_from", "dss");
                params.put("stop_to", "sds");
                params.put("amount", "dssd");
                params.put("route", " fs");
                params.put("weather", "fddf");
                params.put("traffic", "dsddf");
                params.put("demand", "dffd");
                params.put("rush_hour", "fdvs");
                params.put("peak", "cvccvc");







                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    private void getData2() {
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
