package com.digitalmatatus.twigatatu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digitalmatatus.twigatatu.model.AppController;
import com.digitalmatatus.twigatatu.model.MyShortcuts;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

//import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;


import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.digitalmatatus.twigatatu.Login.applyFontForToolbarTitle;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnActivityUpdatedListener, OnGeofencingTransitionListener, OnLocationUpdatedListener {

    private Location mlocation;
    private Criteria criteria;
    private String latitude = "", longitude = "";
    private LocationManager locationManager;
    LocationListener locationListener;
    private GoogleApiClient googleApiClient;
    boolean gps_enabled = false;
    private static final int REQUEST_FINE_LOCATION = 0;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;
    TextView _location, fare;
    ProgressDialog progressDialog;
    private EditText input;
    private static AlertDialog.Builder alert;
    private LocationGooglePlayServicesProvider provider;

    private EditText dest;
    private EditText source;
    private int amnt=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Setting up custom font
        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        setContentView(R.layout.appbar_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");
        applyFontForToolbarTitle(this);
        _location = (TextView) findViewById(R.id.location);
        _location.setTypeface(mTfLight);
        fare = (TextView) findViewById(R.id.fare);
        fare.setTypeface(mTfLight);
        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(mTfLight);
        dest = (EditText) findViewById(R.id.stop_to);
        dest.setTypeface(mTfLight);
        source = (EditText) findViewById(R.id.stop_from);
        source.setTypeface(mTfLight);
        DiscreteSeekBar discreteSeekBar1 = (DiscreteSeekBar) findViewById(R.id.discrete3);
        discreteSeekBar1.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                amnt = i;
                fare.setText("Fare is:" + i + " /=");

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }
        });


        Button button = (Button) findViewById(R.id.submit_fare);
        button.setTypeface(mTfLight);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), Conditions.class);
                intent.putExtra("stops_to", dest.getText().toString());
                intent.putExtra("stops_from", source.getText().toString());
                intent.putExtra("amount", amnt+"");
                Log.e("amount is",amnt+"");

                if (amnt > 0) {
                    startActivity(intent);
                } else {
                    MyShortcuts.showToast("Please select a fare amount", getBaseContext());
                }

            }
        });




    }

//    TODO If the project would require getting location of the user, use the below code
/*
* This function leverages on using both GPS and Network Provided location.
* If it realises that you have turned off your data, It will try to get you location using the GPS,
* otherwise it obtains the location data using the Network provider
*
* */
    private void getLocation() {
        if (checkPermission(getBaseContext())) {
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("getting coordinates... it may take a while for the first time! if it takes long move outside the house for the satellites to give you the coordinates quickly");
            progressDialog.show();

            startLocation();
        } else {
            showPermissionDialog();
        }


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(MainActivity.this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.

                            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (gps_enabled && checkPermission(getBaseContext())) {

                                Log.e("inside accept", "inside");

                            } else {
                                showPermissionDialog();
                            }

                            showPermissionDialog();
//            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                            if (!checkPermission(getBaseContext())) {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            }

                            progressDialog = new ProgressDialog(MainActivity.this,
                                    R.style.AppTheme_Dark_Dialog);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("getting coordinates... it may take a while for the first time! if it takes long move outside the house for the satellites to give you the coordinates quickly");
                            progressDialog.show();

                            if (!progressDialog.isShowing()) {

                            }
//                            progressDialog.setCancelable(false);

                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
//                                 startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
//                            TODO asking the user for the location permission
                            Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gpsOptionsIntent);
                            MyShortcuts.showToast("Please turn on the GPS/Location setting to record your location!", getBaseContext());
                            break;
                    }
                }
            });
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        Location gps_loc = null;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!progressDialog.isShowing()) {
                    Verify();
                }
                mlocation = location;
                Log.e("Location Changes", location.toString());
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());

                if (progressDialog != null) {
                    progressDialog.setMessage("refining accuracy...");
                }
                _location.setText(latitude + " ," + longitude);
                if (location.hasAccuracy()) {
                    Log.e("stopped updates", "stopped ");
                    if (checkPermission(getBaseContext())) {
                        locationManager.removeUpdates(locationListener);
                        alert = new AlertDialog.Builder(MainActivity.this);

                        alert.setTitle("Confirm the Coordinates and enter the Location Name below");
                        alert.setMessage(latitude + " ," + longitude);
                        input = new EditText(getBaseContext());
                        input.setHint("Type the location name here");
                        alert.setView(input);

                        // Set an EditText view to get user input


                        alert.setPositiveButton("Proceed",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                        }
                                        // store the meter index to sp
                                        String value = input.getText().toString();
                                        Log.e("value", value);
                                        if (!value.isEmpty()) {
                                            MyShortcuts.setDefaults("locationname", value, getBaseContext());
                                            MyShortcuts.setDefaults("longitude", longitude, getBaseContext());
                                            MyShortcuts.setDefaults("latitude", latitude, getBaseContext());
                                            Intent intent = new Intent(getBaseContext(), Conditions.class);
                                            startActivity(intent);
                                        } else {
                                            MyShortcuts.showToast("Please add a location name", getBaseContext());
                                            showAlert();
                                        }


                                    }
                                });

                        alert.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        // Canceled.
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });

                        alert.show();

                    }
                }
/*                latitude.setText(String.valueOf(location.getLatitude()));
                longitude.setText(String.valueOf(location.getLongitude()));*/


                Log.e("long lang", longitude + ", " + latitude);
            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }


        };

        if (gps_enabled && checkPermission(getBaseContext())) {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (gps_loc != null) {
                latitude = String.valueOf(gps_loc.getLatitude());
                longitude = String.valueOf(gps_loc.getLongitude());
                Log.e("inside gps enabled & cp", "inside");
                Log.e("gps_loc get  last", longitude + "," + latitude);
            } else {
                showPermissionDialog();
            }

//            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("getting coordinates...");
            progressDialog.show();

        }
    }



    public void showAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle("Confirm the Coordinates and enter the Location Name");
        alert.setMessage(latitude + " ," + longitude);
        input = new EditText(getBaseContext());
        input.setHint("Type the location name here");
        int color = _location.getCurrentTextColor();
        input.setTextColor(color);
        input.setHintTextColor(color);
        alert.setView(input);


        alert.setPositiveButton("Proceed",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        // store the meter index to sp
                        String value = input.getText().toString();
                        Log.e("value", value + " ");
                        if (!value.isEmpty()) {

                            value = input.getText().toString();
                            Log.e("value", value + " ");
                            MyShortcuts.setDefaults("locationname", value, getBaseContext());
                            MyShortcuts.setDefaults("longitude", longitude, getBaseContext());
                            MyShortcuts.setDefaults("latitude", latitude, getBaseContext());
                            Intent intent = new Intent(getBaseContext(), Conditions.class);
                            startActivity(intent);
                        } else {
                            MyShortcuts.showToast("Please add a location name", getBaseContext());
                            showAlert();
                        }

                        if (MyShortcuts.hasInternetConnected(getBaseContext())) {
                            stopLocation();
                        }


                    }
                });

        alert.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {

                        // Canceled.
                        progressDialog.dismiss();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

        alert.show();
    }

    public void Verify() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle("Do you want to exit this screen and avoid getting the location?");


        // Set an EditText view to get user input


        alert.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }


                        Intent intent = new Intent(getBaseContext(), Conditions.class);
                        startActivity(intent);


                    }
                });

        alert.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {

                        // Canceled.
                        progressDialog.dismiss();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted

                    Looper looper = null;
                    if (checkPermission(getBaseContext())) {

//                        TODO Commented this location code
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                        locationManager.requestSingleUpdate(criteria, locationListener, looper);
                        if (MyShortcuts.hasInternetConnected(getBaseContext())) {
                            startLocation();
                        }
                    }
                    Log.e("inside", "inside");
                } else {
                    // not granted
                    MyShortcuts.showToast("You cannot read meter details without accepting this permission!", getBaseContext());
                    showPermissionDialog();
                }
                return;
            }

        }

    }

    private void showPermissionDialog() {
        if (!checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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

    /**
     * Used to check the result of the check of the user location settings
     *
     * @param requestCode code of the request made
     * @param resultCode  code of the result of that request
     * @param intent      intent with further information
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        if (provider != null) {
            provider.onActivityResult(requestCode, resultCode, intent);
        }
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (googleApiClient.isConnected()) {
//                            startLocationUpdates();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    private void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);


    }

    private void stopLocation() {
        SmartLocation.with(this).location().stop();


        SmartLocation.with(this).activity().stop();


//        SmartLocation.with(this).geofencing().stop();

    }

    private void showLocation(Location location) {
        if (location != null) {
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
//            locationText.setText(text);

            latitude = location.getLatitude() + "";
            longitude = location.getLongitude() + "";


            // We are going to get the address for the current position
            SmartLocation.with(this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                @Override
                public void onAddressResolved(Location original, List<Address> results) {
                    if (results.size() > 0) {
                        Address result = results.get(0);
                        StringBuilder builder = new StringBuilder(text);
                        builder.append("\nLocation Name is ");
                        List<String> addressElements = new ArrayList<>();
                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
                            addressElements.add(result.getAddressLine(i));
                        }
                        builder.append(TextUtils.join(", ", addressElements));
                        MyShortcuts.setDefaults("locationname", builder.toString(), getBaseContext());
//                        locationText.setText(builder.toString());
                        Log.e("locationname", builder.toString());
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        showSmartAlert(builder.toString());
                    }
                }
            });
        } else {
//            locationText.setText("Null location");
        }
    }

    private void showActivity(DetectedActivity detectedActivity) {
        if (detectedActivity != null) {
          /*  activityText.setText(
                    String.format("Activity %s with %d%% confidence",
                            getNameFromType(detectedActivity),
                            detectedActivity.getConfidence())*/
//            );
        } else {
//            activityText.setText("Null activity");
        }
    }


    @Override
    public void onLocationUpdated(Location location) {
        showLocation(location);
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        showActivity(detectedActivity);
    }


    private String getNameFromType(DetectedActivity activityType) {
        switch (activityType.getType()) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
            default:
                return "unknown";
        }
    }

    private String getTransitionNameFromType(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exit";
            default:
                return "dwell";
        }
    }

    @Override
    public void onGeofenceTransition(TransitionGeofence transitionGeofence) {

    }

    public void showSmartAlert(String name) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle("Confirm the Coordinates and the Location Name");
        alert.setMessage("(" + latitude + " ," + longitude + ")" + " " + name);


        // Set an EditText view to get user input


        alert.setPositiveButton("Proceed",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        // store the meter index to sp


                        MyShortcuts.setDefaults("longitude", longitude, getBaseContext());
                        MyShortcuts.setDefaults("latitude", latitude, getBaseContext());
                        Intent intent = new Intent(getBaseContext(), Conditions.class);
                        startActivity(intent);


                        if (MyShortcuts.hasInternetConnected(getBaseContext())) {
                            stopLocation();
                        }


                    }
                });

        alert.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {

                        // Canceled.
                        progressDialog.dismiss();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

        alert.show();
    }
}
