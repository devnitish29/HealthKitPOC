package healthkit.tarento.healthdataaggregator.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import healthkit.tarento.healthdataaggregator.notification.HealthKitNotificationManager;
import healthkit.tarento.healthdataaggregator.notification.Location;
import healthkit.tarento.healthdataaggregator.notification.NotificationMessage;


public class HealthKitFirebaseMessagingService extends FirebaseMessagingService {
    String TAG = "NITISH";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationMessage notificationMessage = null;
        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily
        Log.e(TAG, "onMessageReceived: data size "+remoteMessage.getData().size() );
        if(remoteMessage.getData().size() > 0){
            //handle the data message here
            String data = remoteMessage.getData().toString();
            notificationMessage = new NotificationMessage();
            notificationMessage.setName(remoteMessage.getData().get("name"));
            notificationMessage.setSeverity(Integer.parseInt(remoteMessage.getData().get("severity")));
            notificationMessage.setStatus(remoteMessage.getData().get("status"));
            notificationMessage.setHeartrate(remoteMessage.getData().get("heartrate"));
            Location location= new Gson().fromJson(remoteMessage.getData().get("location"),Location.class);
            notificationMessage.setLocation(location);
            Log.e(TAG, "onMessageReceived: data  "+remoteMessage.getData().toString() );

        }

        //getting the title and the body



       HealthKitNotificationManager.getInstance(getApplicationContext()).displayNotification("Healthkit",notificationMessage);
        //then here we can use the title and body to build a notification
    }





}
