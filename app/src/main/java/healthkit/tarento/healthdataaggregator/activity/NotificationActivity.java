package healthkit.tarento.healthdataaggregator.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import healthkit.tarento.healthdataaggregator.R;
import healthkit.tarento.healthdataaggregator.notification.NotificationMessage;

public class NotificationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient mFusedLocationClient;

    NotificationMessage notificationMessage;
    private String TAG = "NITISH";
    AlertDialog alertDialog = null;
    private TextToSpeech textToSpeech;
    LinearLayout mLinearLayout;

    TextView tvName, tvSeverity, tvStatus, tvHeartRate, tvLocation;
    GoogleMap mGoogleMap;
    MapView mapView;
    LatLng mCurrentLocation, mPatientLocation;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.mapview);
        mLinearLayout = findViewById(R.id.mainlayout);
        tvName = findViewById(R.id.name);
        tvSeverity = findViewById(R.id.sev);
        tvStatus = findViewById(R.id.status);
        tvHeartRate = findViewById(R.id.heart);
//        tvLocation = findViewById(R.id.location);
//        initTextToSpeech();
        mapView.onCreate(savedInstanceState);


        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if (mapView != null) {

                        mapView.onResume();
                        mapView.getMapAsync(NotificationActivity.this);
                    }
                }
            }
        });

    }

    private void initTextToSpeech() {

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @SuppressLint("NewApi")
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
                    speakNow();
                }


            }
        });
    }


    private void speakNow() {
        String test = "Patient Critical !!!!! :";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        textToSpeech.speak(test, TextToSpeech.QUEUE_FLUSH, map);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        notificationMessage = (NotificationMessage) intent.getSerializableExtra("DATA");
        Log.e(TAG, "onNewIntent: " + notificationMessage.getName());
        setIntent(intent);
        //showNormalNotification();

        /*if (notificationMessage.getSeverity() ==3){
            initTextToSpeech();
        } else {
            showNormalNotification();
        }*/
    }

    private void showNormalNotification() {
        Log.e(TAG, "showNormalNotification: " + notificationMessage.getSeverity());
        if (notificationMessage.getSeverity() == 0) {
            mLinearLayout.setBackgroundColor(Color.parseColor("#EDDA74"));
        } else if (notificationMessage.getSeverity() == 1) {
            mLinearLayout.setBackgroundColor(Color.parseColor("#FF8040"));
        } else if (notificationMessage.getSeverity() == 2) {
            mLinearLayout.setBackgroundColor(Color.parseColor("#F62217"));
            showActualDialog();
            initTextToSpeech();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationMessage = (NotificationMessage) getIntent().getSerializableExtra("DATA");

        Log.e(TAG, "onResume: " + notificationMessage.getName());
        showNormalNotification();
        setUpPatientData();
        //showActualDialog();
    }


    UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            Log.e(TAG, "onStart: Voice Command Started now");
        }

        @Override
        public void onDone(String utteranceId) {
            Log.e(TAG, "onStart: Voice Command ended now");
            speakNow();
        }

        @Override
        public void onError(String utteranceId) {
            Log.e(TAG, "onStart: Voice Command error now");

        }
    };

    private void setUpPatientData() {

        mPatientLocation = new LatLng(notificationMessage.getLocation().getLatitude(), notificationMessage.getLocation().getLongitude());
        tvName.setText(notificationMessage.getName());
        tvHeartRate.setText(notificationMessage.getHeartrate() + " bpm");
        /*tvLocation.setText(notificationMessage.getLocation());*/
        tvSeverity.setText("" + notificationMessage.getSeverity());
        tvStatus.setText(notificationMessage.getStatus());

    }

    private void showActualDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(notificationMessage.getName())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (textToSpeech != null) {
                            textToSpeech.stop();
                            textToSpeech.shutdown();
                        }
                        alertDialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (textToSpeech != null) {
                            textToSpeech.stop();
                            textToSpeech.shutdown();
                        }
                        alertDialog.dismiss();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(Objects.requireNonNull(this));
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if (mPatientLocation != null){
            if (mGoogleMap != null){
                mGoogleMap.addMarker(new MarkerOptions().position(mPatientLocation).title("Patient Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_patient_marker)));

            }

        }

        if (mCurrentLocation != null) {

            mGoogleMap.addMarker(new MarkerOptions().position(mCurrentLocation).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital_marker)));
            CameraPosition cameraPosition = CameraPosition.builder().target(mCurrentLocation).zoom(16).bearing(0).tilt(0).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }


    }
}
