package healthkit.tarento.healthdataaggregator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RespirationMessage implements Serializable,CommonMessage {

    @SerializedName("timestamp")
    @Expose
    private long timestamp;
    @SerializedName("respirationrate")
    @Expose
    private int respirationrate;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRespirationrate() {
        return respirationrate;
    }

    public void setRespirationrate(int respirationrate) {
        this.respirationrate = respirationrate;
    }
}
