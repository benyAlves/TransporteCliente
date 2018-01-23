package cliente.transporte.sd.transportecliente.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by SD on 1/23/2018.
 */

public interface IGoogleAPI {

    @GET
    Call<String> getPath(@Url String url);
}
