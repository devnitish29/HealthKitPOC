package healthkit.tarento.healthdataaggregator.utility;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import healthkit.tarento.healthdataaggregator.service.SyncService;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class Utils {

    private static long REFRESH_INTERVAL = 5000;

    public static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder((int) System.nanoTime(), new ComponentName(context.getPackageName(), SyncService.class.getName()));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        assert jobScheduler != null;
        jobScheduler.cancelAll();
        int resultCode = jobScheduler.schedule(builder.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.e("NITISH", "Job scheduled!");
        } else {
            Log.e("NITISH", "Job not scheduled");
        }
    }



   /* public static MarkerOptions getEparkeraMarker(Context context, LatLng latLng) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(context, "Kindstugatan 1, 111 31 Stockholm")));
        return markerOptions;
    }*/

    /*public static Bitmap getMarkerBitmapFromView(Context context, String parkingAddress) {
        View mParkingMarkerLayout = ((LayoutInflater) Objects.requireNonNull(Objects.requireNonNull(context).getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(R.layout.layout_parking_greenmarker, null);
        TextView mParkAddress = mParkingMarkerLayout.findViewById(R.id.park_address);
        mParkAddress.setText(parkingAddress);
        mParkingMarkerLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mParkingMarkerLayout.layout(0, 0, mParkingMarkerLayout.getMeasuredWidth(), mParkingMarkerLayout.getMeasuredHeight());
        mParkingMarkerLayout.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(mParkingMarkerLayout.getMeasuredWidth(), mParkingMarkerLayout.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = mParkingMarkerLayout.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        mParkingMarkerLayout.draw(canvas);
        return returnedBitmap;
    }*/
}
