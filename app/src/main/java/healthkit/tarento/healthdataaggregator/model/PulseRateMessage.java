package healthkit.tarento.healthdataaggregator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PulseRateMessage implements Serializable,CommonMessage {

    @SerializedName("timestamp")
    @Expose
    private long timestamp;
    @SerializedName("pulserate")
    @Expose
    private int pulserate;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getPulserate() {
        return pulserate;
    }

    public void setPulserate(int pulserate) {
        this.pulserate = pulserate;
    }
}
