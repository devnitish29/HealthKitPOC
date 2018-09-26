package healthkit.tarento.healthdataaggregator.model;

import java.io.Serializable;

public class HeartRate implements Serializable {

    double time;
    double rate;

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
