package healthkit.tarento.healthdataaggregator.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import healthkit.tarento.healthdataaggregator.R;
import healthkit.tarento.healthdataaggregator.model.BloodpressureMessage;
import healthkit.tarento.healthdataaggregator.model.CommonMessage;
import healthkit.tarento.healthdataaggregator.model.LocationData;
import healthkit.tarento.healthdataaggregator.model.LocationMessage;
import healthkit.tarento.healthdataaggregator.model.Message;
import healthkit.tarento.healthdataaggregator.model.PulseRateMessage;
import healthkit.tarento.healthdataaggregator.model.RespirationMessage;
import healthkit.tarento.healthdataaggregator.model.SapData;
import healthkit.tarento.healthdataaggregator.model.TemperatureMessage;
import healthkit.tarento.healthdataaggregator.network.NetworkApiClient;
import healthkit.tarento.healthdataaggregator.network.RestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private FusedLocationProviderClient mFusedLocationClient;
    /*Spinner spMockData;*/
    private String TAG = "NITISH";
    GraphView graph;
    LineGraphSeries<DataPoint> averageSeries;
    LineGraphSeries<DataPoint> highSeries;
    LineGraphSeries<DataPoint> lowSeries;
    LineGraphSeries<DataPoint> currentSeries;
    CountDownTimer timer, lowTimer, highTimer, normalTimer;
    RestInterface restInterface;
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
    Button btnHigh, btnLow, btnNormal;
    Toolbar toolbar;
    LinearLayout linearLayout;
    LatLng mCurrentLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        linearLayout = findViewById(R.id.btnlayout);
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Health Tracker");
            toolbar.setTitleTextColor(Color.WHITE);
        }
        btnHigh = findViewById(R.id.high);
        btnLow = findViewById(R.id.low);
        btnNormal = findViewById(R.id.normal);
        btnNormal.setOnClickListener(this);
        btnHigh.setOnClickListener(this);
        btnLow.setOnClickListener(this);
        graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(300);
        graph.getViewport().setScalable(true);
        graph.setTitle("Heart Rate");
        graph.setTitleColor(Color.BLACK);
        graph.setTitleTextSize(60);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.e(TAG, "onSuccess: current location --->> Latitude   "+location.getLatitude()+ "   longitude   "+location.getLongitude() );
                    mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    sendLocationData(System.currentTimeMillis() / 1000);
                }
            }
        });



        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {

                if (isValueX) {
                    return sdf.format(new Date((long) value));
                } else {
                    return super.formatLabel(value, isValueX);
                }

            }
        });
     /*   spMockData = findViewById(R.id.spMock);

        spMockData.setOnItemSelectedListener(this);*/

        loadCurrentHearRate();
    }


    private void loadDefaultChart() {
        graph.removeAllSeries();
        averageSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(50, 55),
                new DataPoint(60, 65),
                new DataPoint(70, 75),
                new DataPoint(80, 65),
                new DataPoint(90, 70),
                new DataPoint(100, 80),
                new DataPoint(110, 75),
                new DataPoint(120, 70),
                new DataPoint(130, 60),
                new DataPoint(140, 55),
                new DataPoint(150, 70),
                new DataPoint(160, 75),
                new DataPoint(170, 80),
        });
        graph.addSeries(averageSeries);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        if (position == 0) {
            loadDefaultChart();
            linearLayout.setVisibility(View.GONE);
        } else if (position == 1) {
            loadLowRateData();
            linearLayout.setVisibility(View.GONE);
        } else if (position == 2) {
            loadHighRateData();
            linearLayout.setVisibility(View.GONE);
        } else if (position == 3) {
            loadCurrentHearRate();
            linearLayout.setVisibility(View.VISIBLE);
        }

    }

    private void loadCurrentHearRate() {

        currentSeries = new LineGraphSeries<>();
        graph.removeAllSeries();
        graph.addSeries(currentSeries);
        timer = new CountDownTimer(6000000, 600000) {

            @Override
            public void onTick(long l) {

                long currentTime = System.currentTimeMillis();
                Log.e(TAG, "onTick: ------>>> " + currentTime);
                double valueY = getRandomY();
                DataPoint dataPoint = new DataPoint(currentTime, valueY);
                currentSeries.appendData(dataPoint, true, 50);
                graph.getViewport().scrollToEnd();
                sendToNetwork(currentTime, valueY);

            }

            @Override
            public void onFinish() {

            }
        }.start();


    }


    private void mockHighHeartRate() {


        timer.cancel();
        if (lowTimer != null) {
            Log.e(TAG, "mockHighHeartRate: lowTimer is not null");
            lowTimer.cancel();
        }
        if (normalTimer != null) {
            normalTimer.cancel();
        }

        highTimer = new CountDownTimer(6000000, 600000) {

            @Override
            public void onTick(long l) {

                long currentTime = System.currentTimeMillis();
                Log.e(TAG, "onTick: ------>>> " + currentTime);
                double valueY = updateHighHeartRate();
                Log.e(TAG, "onTick: updateHighHeartRate  -->>" + valueY);
                DataPoint dataPoint = new DataPoint(currentTime, valueY);
                currentSeries.appendData(dataPoint, true, 50);
                sendToNetwork(currentTime, valueY);

            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    private void mockLowHeartRate() {

        timer.cancel();
        if (highTimer != null) {
            Log.e(TAG, "mockHighHeartRate: highTimer is not null");
            highTimer.cancel();
        }

        if (normalTimer != null) {
            normalTimer.cancel();
        }
        lowTimer = new CountDownTimer(6000000, 600000) {

            @Override
            public void onTick(long l) {

                long currentTime = System.currentTimeMillis();
                Log.e(TAG, "onTick: ------>>> " + currentTime);
                double valueY = updateLowHeartRate();
                Log.e(TAG, "onTick: updateLowHeartRate  -->>" + valueY);
                DataPoint dataPoint = new DataPoint(currentTime, valueY);
                currentSeries.appendData(dataPoint, true, 50);
                sendToNetwork(currentTime, valueY);

            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void mockNormalHeartRate() {

        timer.cancel();
        if (highTimer != null) {
            Log.e(TAG, "mockHighHeartRate: highTimer is not null");
            highTimer.cancel();
        }

        if (lowTimer != null) {
            lowTimer.cancel();
        }
        normalTimer = new CountDownTimer(6000000, 600000) {

            @Override
            public void onTick(long l) {

                long currentTime = System.currentTimeMillis();
                Log.e(TAG, "onTick: ------>>> " + currentTime);
                double valueY = updateNormalHeartRate();
                Log.e(TAG, "onTick: updateNormalHeartRate  -->>" + valueY);
                DataPoint dataPoint = new DataPoint(currentTime, valueY);
                currentSeries.appendData(dataPoint, true, 50);
                sendToNetwork(currentTime, valueY);

            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private int updateHighHeartRate() {

        final int min = 140;
        final int max = 300;
        return new Random().nextInt(max - min) + min;


    }

    private int updateLowHeartRate() {

        final int min = 0;
        final int max = 50;

        return new Random().nextInt(max - min) + min;


    }
    private int updateNormalHeartRate() {

        final int min = 50;
        final int max = 130;

        return new Random().nextInt(max - min) + min;


    }

    private void sendToNetwork(long x, double y) {

        restInterface = NetworkApiClient.getClient().create(RestInterface.class);


        SapData sapData = new SapData();
        sapData.setMode("sync");
        BloodpressureMessage bloodpressureMessage = new BloodpressureMessage();
        bloodpressureMessage.setBloodpressure((int) y);
        bloodpressureMessage.setTimestamp(x / 1000);
        List<CommonMessage> messages = new ArrayList<>();
        messages.add(bloodpressureMessage);
        sapData.setMessages(messages);
        sapData.setMessageType("7cbb2f5a3fcaaf42451d");
        Call<JsonObject> call = restInterface.postBloodPressureData(sapData);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.e(TAG, "onResponse: HeartData " + response.message().toString());

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());

            }
        });

        sendTemperatureData(x / 1000);
        sendPulseData(x / 1000);
        sendRespirationData(x / 1000);
        sendLocationData(x/1000);


    }

    private void sendRespirationData(long timeStamp) {
        if (restInterface != null){
            SapData data = new SapData();
            data.setMode("sync");
            RespirationMessage respirationMessage = new RespirationMessage();
            respirationMessage.setTimestamp(timeStamp);
            respirationMessage.setRespirationrate(getRandomRespirationRate());
            List<CommonMessage> messages = new ArrayList<>();
            messages.add(respirationMessage);
            data.setMessages(messages);
            data.setMessageType("529fd85222a78d756a71");
            Call<JsonObject> call = restInterface.postRespirationData(data);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "onResponse: RespirationData " + response.message().toString());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }

    }

    private void sendPulseData(long timeStamp) {

        if (restInterface != null){
            SapData data = new SapData();
            data.setMode("sync");
            PulseRateMessage pulseRateMessage = new PulseRateMessage();
            pulseRateMessage.setTimestamp(timeStamp);
            pulseRateMessage.setPulserate(getRandomPulseRate());
            List<CommonMessage> messages = new ArrayList<>();
            messages.add(pulseRateMessage);
            data.setMessages(messages);
            data.setMessageType("3cbfa25a6bff271e4c05");
            Call<JsonObject> call = restInterface.postPulseData(data);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "onResponse: PulseData " + response.message().toString());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }

    }

    private void sendTemperatureData(long timeStamp) {
        if (restInterface != null){
            SapData data = new SapData();
            data.setMode("sync");
            TemperatureMessage temperatureMessage = new TemperatureMessage();
            temperatureMessage.setTimestamp(timeStamp);
            temperatureMessage.setTemperature(getRandomTemperature());
            List<CommonMessage> messages = new ArrayList<>();
            messages.add(temperatureMessage);
            data.setMessages(messages);
            data.setMessageType("52b160f5049749ffb4e9");
            Call<JsonObject> call = restInterface.postTemperatureData(data);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "onResponse: TemperatureData " + response.message().toString());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    }


    private void sendLocationData(long timeStamp) {
        mCurrentLocation = new LatLng(12.936936936936936,77.60959875038247);
        if (restInterface != null && mCurrentLocation != null ){
            LocationData data = new LocationData();
            LocationMessage message = new LocationMessage();
            List<LocationMessage> messageList = new ArrayList<>();
            data.setMode("sync");
            data.setMessageType("0155336d7e783e11b56d");
            message.setTimestamp(timeStamp);
            message.setLatitude(mCurrentLocation.latitude);
            message.setLongitude(mCurrentLocation.longitude);
            messageList.add(message);
            data.setMessages(messageList);

            Call<JsonObject> call = restInterface.postLocationData(data);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "onResponse: LocationData " + response.message().toString());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    }

    private int getRandomTemperature() {

        final int min = 90;
        final int max = 110;
        return new Random().nextInt((int) ((max - min) + 1)) + min;

    }

    private int getRandomRespirationRate() {
        final int min = 0;
        final int max = 200;
        return (int) (new Random().nextInt((int) ((max - min) + 1)) + min);

    }

    private int getRandomPulseRate() {
        final int min = 0;
        final int max = 150;
        return new Random().nextInt((int) ((max - min) + 1)) + min;
    }


    private double getRandomY() {
        final int min = 0;
        final int max = 140;
        return new Random().nextInt((max - min) + 1) + min;

    }


    private void loadHighRateData() {

        graph.removeAllSeries();
        highSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(new Date(System.nanoTime() + 1000).getTime(), 80),
                new DataPoint(new Date(System.nanoTime() + 2000).getTime(), 85),
                new DataPoint(new Date(System.nanoTime() + 3000).getTime(), 78),
                new DataPoint(new Date(System.nanoTime() + 4000).getTime(), 85),
                new DataPoint(new Date(System.nanoTime() + 5000).getTime(), 93),
                new DataPoint(new Date(System.nanoTime() + 6000).getTime(), 100),
                new DataPoint(new Date(System.nanoTime() + 7000).getTime(), 79),
                new DataPoint(new Date(System.nanoTime() + 8000).getTime(), 99),
                new DataPoint(new Date(System.nanoTime() + 9000).getTime(), 110),
                new DataPoint(new Date(System.nanoTime() + 10000).getTime(), 90),
        });
        graph.addSeries(highSeries);
    }


    private void loadLowRateData() {
        graph.removeAllSeries();
        lowSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(30, 40),
                new DataPoint(40, 45),
                new DataPoint(50, 58),
                new DataPoint(60, 45),
                new DataPoint(70, 63),
                new DataPoint(80, 50),
                new DataPoint(90, 42),
                new DataPoint(100, 59),
                new DataPoint(110, 52),
                new DataPoint(120, 45),
        });
        graph.addSeries(lowSeries);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private Date convertToUTCTime(long timeInMilliSeconds) {
        String pattern = "EEEE MMMM yyyy HH:mm:ss.SSSZ";
        SimpleDateFormat sdf2 = new SimpleDateFormat(pattern);
        return new Date(timeInMilliSeconds);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.high:
                mockHighHeartRate();
                break;
            case R.id.low:
                mockLowHeartRate();
                break;
            case R.id.normal:
                mockNormalHeartRate();
                break;
        }
    }
}
