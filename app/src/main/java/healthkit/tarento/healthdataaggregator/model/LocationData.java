package healthkit.tarento.healthdataaggregator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LocationData implements Serializable {

    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("messageType")
    @Expose
    private String messageType;
    @SerializedName("messages")
    @Expose
    private List<LocationMessage> messages = null;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public List<LocationMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<LocationMessage> messages) {
        this.messages = messages;
    }
}
