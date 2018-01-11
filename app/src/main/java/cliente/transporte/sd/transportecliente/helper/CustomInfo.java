package cliente.transporte.sd.transportecliente.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import cliente.transporte.sd.transportecliente.R;

/**
 * Created by SD on 1/11/2018.
 */

public class CustomInfo implements GoogleMap.InfoWindowAdapter {

    View view;

    public CustomInfo(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.custom_rider_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txtPickupTitle = ((TextView) view.findViewById(R.id.pickUpInfo));
        txtPickupTitle.setText(marker.getTitle());

        TextView txtPickupSnippet = ((TextView) view.findViewById(R.id.pickUpSnippet));
        txtPickupSnippet.setText(marker.getSnippet());

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
