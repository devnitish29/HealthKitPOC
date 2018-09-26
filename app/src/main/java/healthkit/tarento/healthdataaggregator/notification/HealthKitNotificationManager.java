package healthkit.tarento.healthdataaggregator.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import healthkit.tarento.healthdataaggregator.R;
import healthkit.tarento.healthdataaggregator.activity.HomeActivity;
import healthkit.tarento.healthdataaggregator.activity.NotificationActivity;
import healthkit.tarento.healthdataaggregator.utility.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;
import static healthkit.tarento.healthdataaggregator.utility.Constants.CHANNEL_ID;

public class HealthKitNotificationManager {

    private Context mCtx;
    private static HealthKitNotificationManager mInstance;

    public HealthKitNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }


    public static synchronized HealthKitNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HealthKitNotificationManager(context);
        }
        return mInstance;
    }


    public void displayNotification(String title, NotificationMessage body) {
        Log.e("NITISH", "displayNotification: " + body.getName());
        Intent intent = new Intent(mCtx, NotificationActivity.class);
        intent.putExtra("DATA",body);
        mCtx.startActivity(intent);

    }





}
