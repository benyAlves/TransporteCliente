package cliente.transporte.sd.transportecliente.common;

import cliente.transporte.sd.transportecliente.rest.ClientAPI;
import cliente.transporte.sd.transportecliente.rest.GoogleMapAPI;
import cliente.transporte.sd.transportecliente.rest.IFCMService;
import cliente.transporte.sd.transportecliente.rest.IGoogleAPI;
import cliente.transporte.sd.transportecliente.rest.InterfaceAPI;

/**
 * Created by SD on 1/12/2018.
 */

public class Common {

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com";

    public static final String driver_table = "drivers";
    public static final String user_driver_table = "driverInformation";
    public static final String user_rider_table = "riderInformation";
    public static final String pickup_request_table = "pickRequest";
    public static final String token_table = "tokens";
    public static final String MESSAGE = "message";

    private static double base_fare = 100; //
    private static double time_rate = 20; // custo por min
    private static double distance_rate = 80; // custo por km

    public static double calculatePrice(double km, int min){
        return (base_fare+ (time_rate*min) + (distance_rate*km));
    }


    public static InterfaceAPI getGoogleAPI(){
        return ClientAPI.getClient(baseURL).create(InterfaceAPI.class);
    }

    public static IGoogleAPI getGoogleService(){
        return GoogleMapAPI.getClient(baseURL).create(IGoogleAPI.class);
    }


    public static IFCMService getFCMService(){
        return ClientAPI.getClient(fcmURL).create(IFCMService.class);
    }
}
