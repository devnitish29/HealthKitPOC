package healthkit.tarento.healthdataaggregator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BloodpressureMessage implements Serializable,CommonMessage {
    @SerializedName("timestamp")
    @Expose
    private long timestamp;
    @SerializedName("bloodpressure")
    @Expose
    private int bloodpressure;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getBloodpressure() {
        return bloodpressure;
    }

    public void setBloodpressure(int bloodpressure) {
        this.bloodpressure = bloodpressure;
    }

}
