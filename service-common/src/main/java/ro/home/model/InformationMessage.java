package ro.home.model;

import ro.home.model.enums.InformationMessageType;

import java.util.Date;

public class InformationMessage extends BasicInformationDTO {

    private final String message;
    private final Date date;

    public InformationMessage() {
        this("");
    }

    public InformationMessage(String message) {
        this(InformationMessageType.INFO, message);
    }

    public InformationMessage(InformationMessageType type, String message) {
        super(type.name());
        this.message = message;
        this.date = new Date();
    }

    @Override
    public String toString() {
        return "InformationMessage{" + "message=" + message + ", date=" + date + '}';
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

}