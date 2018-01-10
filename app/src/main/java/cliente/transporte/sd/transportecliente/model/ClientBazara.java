package cliente.transporte.sd.transportecliente.model;

/**
 * Created by SD on 1/10/2018.
 */

public class ClientBazara {

    private String name;
    private String telefone;


    public ClientBazara(String name, String telefone) {
        this.name = name;
        this.telefone = telefone;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
