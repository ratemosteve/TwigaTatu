package com.digitalmatatus.twigatatu.model;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stephineosoro on 20/06/16.
 */
public class Post {

    public static String baseURL = "http://169.254.85.197";

    public static void PostData(String url, JSONObject parameter, Response.Listener<JSONObject> response) {

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, baseURL + url, parameter,
                response, new Error()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
                setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
                headers.put("Content-Type", "application/json; charset=utf-8");

                String creds = String.format("%s:%s","odhiamborobinson@hotmail.com","powerpoint1994");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", auth);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
//                Log.e("category id", getIntent().getStringExtra("category_id"));
//                params.put("categoryId", 2 + "");


                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        Log.e("request is", req.toString());

    }

    public static void PostString(String url,final String parameter, final String parameter2, Response.Listener<String> response) {

        StringRequest req = new StringRequest(Request.Method.POST, url,
                response, new Error()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                Log.e("category id", getIntent().getStringExtra("category_id"));
                params.put("username",parameter);
                params.put("password",parameter2);


                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        Log.e("request is", req.toString());

    }

    public static void getData(String url, Response.Listener<String> response) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response, new Error());
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
        Log.e("request is", stringRequest.toString());
    }

    private static class Error implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                    JSONObject obj = new JSONObject(res);
                    Log.e("obj", obj.toString());
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    Log.e("e1", e1.toString());
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                    Log.e("e2", e2.toString());
                }
            }

        }
    }

//    private void CreateAccount() {
//        // Toast.makeText(getBaseContext(), "Inside function!", Toast.LENGTH_SHORT).show();
//        JSONObject finalJS = new JSONObject();
//        try {
//
//            finalJS.put("password", _password.getText().toString());
//            finalJS.put("username", _email.getText().toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("JSONErrorin serializing", e.toString());
//        }
//        Log.e("JSON serializing", finalJS.toString());
//        String tag_string_req = "req_Categories";
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
//                Request.Method.POST, MyShortcuts.baseURL() + "twiga/auth/signup", finalJS,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.e("Response from server is", response.toString());
//                        try {
//                            String status = response.getString("success");
//                            if (status.equals("true")) {
//                                Toast.makeText(getBaseContext(), "You have successfully registered!", Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//
//                                startActivity(intent);
//                            } else {
//                                MyShortcuts.showToast(response.getString("message"), getBaseContext());
//                            }
//                        } catch (JSONException e) {
//                            // JSON error
//                            e.printStackTrace();
//                            Toast.makeText(getBaseContext(), "Server errror, Try again later", Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d("VolleyError", "Error: " + error.getMessage());
////                hideProgressDialog()
//                MyShortcuts.showToast("Check you internet connection or try again later", getBaseContext());
//                Log.d("error volley", error.toString());
//            }
//        }) {
//
//            /**
//             * Passing some request headers
//             * */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
//                setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
//                headers = MyShortcuts.AunthenticationHeaders(getBaseContext());
//                return headers;
//            }
//        };
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
//        Log.e("request is", jsonObjReq.toString());
//    }


   /* private void logindetail(final String username, final String password) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                MyShortcuts.baseURL() + "twiga/auth/login", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("All Data", "response from the server is: " + response.toString());
//                TODO dismiss progress bar once data from the server has been received
//                hideDialog();


                try {
                    JSONObject jObj = new JSONObject(response);
                    String success = jObj.getString("success");
                    String session = "";
                    if (success.equals("true")) {
                        session = jObj.getString("user_id");
                        setDefaults("email", _emailText.getText().toString(), getBaseContext());
                        setDefaults("password", _passwordText.getText().toString(), getBaseContext());


                        MyShortcuts.setDefaults("user_id", session, getBaseContext());
                        MyShortcuts.set(_emailText.getText().toString(), _passwordText.getText().toString(), getBaseContext());
                        Intent intent = new Intent(getApplicationContext(), RadarChartActivity.class);
                        intent.putExtra("user_id", session);
                        startActivity(intent);
                    } else {
                        MyShortcuts.showToast("Wrong credentials, please try again", getBaseContext());
                    }




                } catch (JSONException e) {
                    MyShortcuts.showToast("Wrong credentials, please try again", getBaseContext());

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Getting data error", "Error: " + error.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Check your credentials or internet connectivity!", Toast.LENGTH_LONG).show();
//
            }
        }) {

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
    }*/

}
