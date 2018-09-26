package healthkit.tarento.healthdataaggregator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import healthkit.tarento.healthdataaggregator.utility.Utils;

public class StartServiceReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.scheduleJob(context);
    }
}
