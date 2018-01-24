package cliente.transporte.sd.transportecliente.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by SD on 1/23/2018.
 */

public class GoogleMapAPI {

    private static Retrofit retrofitClient = null;

    public static Retrofit getClient(String baseUrl){
        if(retrofitClient == null)
            retrofitClient =  new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        return retrofitClient;
    }

}
