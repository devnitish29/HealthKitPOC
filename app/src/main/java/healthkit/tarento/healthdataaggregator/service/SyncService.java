package healthkit.tarento.healthdataaggregator.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.google.gson.JsonObject;

import healthkit.tarento.healthdataaggregator.network.NetworkApiClient;
import healthkit.tarento.healthdataaggregator.network.RestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncService extends JobService {

    RestInterface restInterface;



    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.e("NITISH", "onStartJob: SyncService");
        scheduleRefresh();
        jobFinished(jobParameters, false);

    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            scheduleRefresh();
        }
        jobFinished(jobParameters, false);*/

        /*restInterface = NetworkApiClient.getClient().create(RestInterface.class);

        Call<JsonObject> call = restInterface.postBloodPressureData();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    scheduleRefresh();
                }
                jobFinished(jobParameters, false);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });*/


        return true;
    }

    private void scheduleRefresh() {

        JobScheduler mJobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder mJobBuilder = new JobInfo.Builder((int) System.nanoTime(), new ComponentName(getApplicationContext().getPackageName(),SyncService.class.getName()));

        /* For Android N and Upper Versions */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mJobBuilder
                    .setMinimumLatency(60 *2000) //YOUR_TIME_INTERVAL
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        }

        assert mJobScheduler != null;
        mJobScheduler.cancelAll();
        int resultCode = mJobScheduler.schedule(mJobBuilder.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.e("NITISH", "Job scheduled! in service class");
        } else {
            Log.e("NITISH", "Job not scheduled in service class");
        }

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
