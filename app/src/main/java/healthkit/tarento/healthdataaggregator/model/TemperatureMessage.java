package healthkit.tarento.healthdataaggregator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TemperatureMessage implements Serializable ,CommonMessage{

    @SerializedName("timestamp")
    @Expose
    private long timestamp;
    @SerializedName("temperature")
    @Expose
    private double temperature;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
