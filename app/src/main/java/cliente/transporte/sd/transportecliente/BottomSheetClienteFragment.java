package cliente.transporte.sd.transportecliente;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cliente.transporte.sd.transportecliente.common.Common;
import cliente.transporte.sd.transportecliente.rest.IGoogleAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SD on 1/11/2018.
 */

public class BottomSheetClienteFragment extends BottomSheetDialogFragment
{
    String mLocation, mDestination;
    IGoogleAPI mService;
    private double riderLat;
    private double riderLng;
    private TextView tvDistance;
    boolean isTapOnMap;

    public static  BottomSheetClienteFragment newInstance(String location, String destination, Location l){
        BottomSheetClienteFragment bf = new BottomSheetClienteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("location", location);
        bundle.putString("destination", destination);
        bundle.putDouble("lat_destination", l.getLatitude());
        bundle.putDouble("lng_destination", l.getLongitude());
        bf.setArguments(bundle);

        return bf;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            if (!savedInstanceState.isEmpty()) {
//
//                if (message == null) {
//                    message = new Gson().fromJson(savedInstanceState.getString(Common.MESSAGE), String.class);
//                }
//            }
//        }
        mLocation = getArguments().getString("location");
        mDestination = getArguments().getString("destination");
        riderLat = getArguments().getDouble("lat_destination");
        riderLng = getArguments().getDouble("lng_destination");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mService = Common.getGoogleService();
        View view = inflater.inflate(R.layout.bottom_sheet_rider, container, false);
        TextView tvLocation = (TextView)view.findViewById(R.id.location);
        TextView tvDestination = (TextView)view.findViewById(R.id.destination);
        tvDistance = (TextView)view.findViewById(R.id.distance);


        tvLocation.setText(mLocation);
        tvDestination.setText(mDestination);
        getDistance();
        return view;
    }

    private void getDistance() {

        String requestAPI;
        try {
            requestAPI = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" +mLocation+ "&" +
                    "destination=" + riderLat + "," + riderLng + "&" +
                    "key=" + getResources().getString(R.string.google_maps_key);
            mService.getPath(requestAPI).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONArray routes = jsonObject.getJSONArray("routes");

                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");

                        JSONObject legsObject = legs.getJSONObject(0);
                        JSONObject distance = legsObject.getJSONObject("distance");
                        String distance_text = distance.getString("text");

                        Double distance_value = Double.parseDouble(distance_text.replaceAll("[^0-9\\\\.]+",""));
                        JSONObject time = legsObject.getJSONObject("duration");
                        String time_text = time.getString("text");
                        String distanceTime = String.format("%s + %s", distance_text, time_text);
                        tvDistance.setText(distanceTime);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("ERROR", t.getMessage());
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
