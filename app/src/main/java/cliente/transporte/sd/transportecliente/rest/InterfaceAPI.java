package cliente.transporte.sd.transportecliente.rest;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by dalves on 9/12/17.
 */

public interface InterfaceAPI {

//
//     @POST("registo")
//     @FormUrlEncoded
//     Call<AccessToken> registarUser(@Field("nome") String nome,
//                                    @Field("email") String email,
//                                    @Field("telefone") String telefone,
//                                    @Field("password") String password);
//
//
//     @POST("login")
//     @FormUrlEncoded
//     Call<AccessToken> login(@Field("telefone") String telefone,
//                             @Field("password") String password);
//
//     @POST("refresh")
//     @FormUrlEncoded
//     Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);


     @POST("logout")
     Call<ResponseBody> logOut();

     @GET
     Call<String> getPath(@Url String url);

}
