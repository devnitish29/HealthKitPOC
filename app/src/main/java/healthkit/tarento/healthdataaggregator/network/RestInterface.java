package healthkit.tarento.healthdataaggregator.network;

import com.google.gson.JsonObject;

import healthkit.tarento.healthdataaggregator.model.HeartRate;
import healthkit.tarento.healthdataaggregator.model.LocationData;
import healthkit.tarento.healthdataaggregator.model.SapData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestInterface {


    @POST("v1/api/http/data/d2355ff9-04c7-4ced-a61d-ac93db557112")
    Call<JsonObject> postBloodPressureData(@Body SapData data);


    @POST("v1/api/http/data/d2355ff9-04c7-4ced-a61d-ac93db557112")
    Call<JsonObject> postPulseData(@Body SapData data);


    @POST("v1/api/http/data/d2355ff9-04c7-4ced-a61d-ac93db557112")
    Call<JsonObject> postTemperatureData(@Body SapData data);


    @POST("v1/api/http/data/d2355ff9-04c7-4ced-a61d-ac93db557112")
    Call<JsonObject> postRespirationData(@Body SapData data);


    @POST("v1/api/http/data/d2355ff9-04c7-4ced-a61d-ac93db557112")
    Call<JsonObject> postLocationData(@Body LocationData data);

}
