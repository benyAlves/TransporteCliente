package cliente.transporte.sd.transportecliente.model;

/**
 * Created by SD on 1/15/2018.
 */

public class Sender {

    public Notification notification;
    private String to;


    public Sender() {
    }


    public Sender(Notification notification, String to) {
        this.notification = notification;
        this.to = to;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public String getTo() {
        return to;
    }
}
