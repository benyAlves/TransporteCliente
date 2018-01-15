package cliente.transporte.sd.transportecliente.model;

/**
 * Created by SD on 1/15/2018.
 */

public class Sender {

    public Notification notification;
    private String destination;


    public Sender() {
    }

    public Sender(Notification notification, String destination) {
        this.notification = notification;
        this.destination = destination;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}
