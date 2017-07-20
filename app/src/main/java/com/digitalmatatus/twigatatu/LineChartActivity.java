
package com.digitalmatatus.twigatatu;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digitalmatatus.twigatatu.model.AppController;
import com.digitalmatatus.twigatatu.model.MyShortcuts;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.digitalmatatus.twigatatu.RadarChartActivity.toNearestHourInterval;

public class LineChartActivity extends Base implements
        OnChartGestureListener, OnChartValueSelectedListener {

    private LineChart mChart;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.appbar_linechart);

        tvX = (TextView) findViewById(R.id.tvXMax);//18
        tvY = (TextView) findViewById(R.id.tvYMax);//73


        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 1f, 0f);

//        xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line


        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(150f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(0f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(-20f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
//        setData(45, 100);

        getData();

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500);
        //mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleIcons: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawIcons(!set.isDrawIconsEnabled());
                }

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionToggleFilled: {

                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCubic: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            : LineDataSet.Mode.CUBIC_BEZIER);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStepped: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.STEPPED
                            ? LineDataSet.Mode.LINEAR
                            : LineDataSet.Mode.STEPPED);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHorizontalCubic: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            : LineDataSet.Mode.HORIZONTAL_BEZIER);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000, Easing.EasingOption.EaseInCubic);
                break;
            }
            case R.id.animateXY: {
                mChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionSave: {
                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();

                // mChart.saveToGallery("title"+System.currentTimeMillis())
                break;
            }
        }
        return true;
    }
*/
    /*@Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

        // redraw
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
*/
    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Fare Expenditure");

            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

//    private void GetData() {
//        Post.getData("http://41.204.186.47:8000/twiga/fares/filteredFares", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("response",response);
//                /*try {
//                    ArrayList<Double> doublers = new ArrayList<>();
//
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONObject jsonObject2 = jsonObject.getJSONObject("fares");
//
//                    JSONArray jsonArray = jsonObject.getJSONArray("N");
//                    Log.e("Array", jsonArray.toString());
////                    Calling process data to process the actual data
//                    String[] xVal = new String[4];
//                    String[] yVal = new String[jsonArray.length()];
//
//                    xVal[1] ="Tuesday";
//                    xVal[2] ="Wednesday";
//                    xVal[3] ="Thursday";
//                    xVal[4] ="Friday";
//                    xVal[5] ="Saturday";
//
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject dataItem = jsonArray.getJSONObject(i);
//
//                        doublers.add(Double.parseDouble(dataItem.getString("stop_name")));
//
//                        if (i % 2 == 0) {
//                            xVal[i] = "6am";
//                        } else if (i % 3 == 0) {
//                            xVal[i] = "9am";
//                        } else if (i % 5 == 0) {
//                            xVal[i] = "12pm";
//                        } else {
//                            xVal[i] = "6pm";
//
//                        }
////                        yVal[i] =dataItem.getString("count");
//
//                    }
//
////                    TODO call my function to populate this obtained data
////                    drawChart(doublers, xVal);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }*/
//
//            }
//        });
//    }

    private void getData() {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                MyShortcuts.baseURL() + "twiga/fares/fares", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("All Data", "response from the server is: " + response.toString());
//                hideDialog();

                ArrayList<Entry> values = new ArrayList<Entry>();

                int six = 0, nine = 0, twelve = 0, fifteen = 0, eighteen = 0;
                float sixA = 0, nineA = 0, twelveA = 0, fifteenA = 0, eighteenA = 0;
                float fin6 = 0, fin9 = 0, fin12 = 0, fin15 = 0, fin18 = 0;

                /*int sixb = 0, nineb = 0, twelveb = 0, fifteenb = 0, eighteenb = 0;
                float sixB = 0, nineB = 0, twelveB = 0, fifteenB = 0, eighteenB = 0;
                float fin6B = 0, fin9B = 0, fin12B = 0, fin15B = 0, fin18B = 0;
                boolean noData = false;
*/
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("fares");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject dataItem = jsonArray.getJSONObject(i);
                        Date travel_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataItem.getString("travel_time"));
                        int hour = toNearestHourInterval(travel_time);

                        if (hour == 6) {
                            six = +1;
                            sixA = +Float.parseFloat(dataItem.getString("amount"));
                            fin6 = sixA / six;

                        } else if (hour == 9) {
                            nine = +1;
                            nineA = +Float.parseFloat(dataItem.getString("amount"));
                            fin9 = nineA / nine;
                        } else if (hour == 12) {
                            twelve = +1;
                            twelveA = +Float.parseFloat(dataItem.getString("amount"));
                            fin12 = twelveA / twelve;
                        } else if (hour == 15) {
                            fifteen = +1;
                            fifteenA = +Float.parseFloat(dataItem.getString("amount"));
                            fin15 = fifteenA / fifteen;
                        } else {
                            eighteen = +1;
                            eighteenA = +Float.parseFloat(dataItem.getString("amount"));
                            fin18 = eighteenA / eighteen;
                        }
                    }


//                    Setting up the average time used
                    if (sixA!=0){
                        values.add(new Entry(6, fin6, getResources().getDrawable(R.drawable.star)));
                    }else{
                        values.add(new Entry(6, 0, getResources().getDrawable(R.drawable.star)));

                    }

                    if (nineA!=0){
                        values.add(new Entry(9, fin9, getResources().getDrawable(R.drawable.star)));
                    }else{
                        values.add(new Entry(9, 0, getResources().getDrawable(R.drawable.star)));

                    }
                    if (twelveA!=0){
                        values.add(new Entry(12, fin6, getResources().getDrawable(R.drawable.star)));
                    }else{
                        values.add(new Entry(12, 0, getResources().getDrawable(R.drawable.star)));

                    }
                    if (fifteenA!=0){
                        values.add(new Entry(15, fin15, getResources().getDrawable(R.drawable.star)));
                    }else{
                        values.add(new Entry(15, 0, getResources().getDrawable(R.drawable.star)));

                    }
                    if (eighteenA!=0){
                        values.add(new Entry(18, fin18, getResources().getDrawable(R.drawable.star)));
                    }else{
                        values.add(new Entry(18, 0, getResources().getDrawable(R.drawable.star)));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                LineDataSet set1;

                if (mChart.getData() != null &&
                        mChart.getData().getDataSetCount() > 0) {
                    set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                    set1.setValues(values);
                    mChart.getData().notifyDataChanged();
                    mChart.notifyDataSetChanged();
                } else {
                    // create a dataset and give it a type
                    set1 = new LineDataSet(values, "Fare Expenditure");

                    set1.setDrawIcons(false);

                    // set the line to be drawn like this "- - - - - -"
                    set1.enableDashedLine(10f, 5f, 0f);
                    set1.enableDashedHighlightLine(10f, 5f, 0f);
                    set1.setColor(Color.BLACK);
                    set1.setCircleColor(Color.BLACK);
                    set1.setLineWidth(1f);
                    set1.setCircleRadius(3f);
                    set1.setDrawCircleHole(false);
                    set1.setValueTextSize(9f);
                    set1.setDrawFilled(true);
                    set1.setFormLineWidth(1f);
                    set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                    set1.setFormSize(15.f);

                    if (Utils.getSDKInt() >= 18) {
                        // fill drawable only supported on api level 18 and above
                        Drawable drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.fade_red);
                        set1.setFillDrawable(drawable);
                    } else {
                        set1.setFillColor(Color.BLACK);
                    }

                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(set1); // add the datasets

                    // create a data object with the datasets
                    LineData data = new LineData(dataSets);

                    // set data
                    mChart.setData(data);
//                Log.e("Url is",  MyShortcuts.getDefaults("url",getBaseContext()) + "twiga/auth/login?" + "username=" + username + "&password=" + password);
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

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("stop_name", "Ngara");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}


