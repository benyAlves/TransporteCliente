package cliente.transporte.sd.transportecliente.model;

/**
 * Created by SD on 1/10/2018.
 */

public class Rider {

    private String name;
    private String phone;


    public Rider(){}


    public Rider(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
