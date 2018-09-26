package healthkit.tarento.healthdataaggregator.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import healthkit.tarento.healthdataaggregator.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        requestRecordAudioPermission();
        callToMainScreen();
    }

    private void callToMainScreen() {
        new CountDownTimer(5000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {
                // presenter.messageToDisplay("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Intent intent = new Intent(SplashScreenActivity.this,FormActivity.class);
                startActivity(intent);
                finish();
            }

        }.start();
    }

    private void requestRecordAudioPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] requiredPermission = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_COARSE_LOCATION};


            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED && checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(requiredPermission, 101);
            }
        }
    }
}
