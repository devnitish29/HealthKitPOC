package healthkit.tarento.healthdataaggregator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable{

    @SerializedName("timestamp")
    @Expose
    private long timestamp;
    @SerializedName("bloodpressure")
    @Expose
    private int bloodpressure;
    @SerializedName("pulserate")
    @Expose
    private int pulserate;
    @SerializedName("respirationrate")
    @Expose
    private int respirationrate;
    @SerializedName("temperature")
    @Expose
    private Double temperature;

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

    public int getPulserate() {
        return pulserate;
    }

    public void setPulserate(int pulserate) {
        this.pulserate = pulserate;
    }

    public int getRespirationrate() {
        return respirationrate;
    }

    public void setRespirationrate(int respirationrate) {
        this.respirationrate = respirationrate;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
