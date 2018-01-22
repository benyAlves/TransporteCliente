package cliente.transporte.sd.transportecliente;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by SD on 1/11/2018.
 */

public class BottomSheetClienteFragment extends BottomSheetDialogFragment
{
    String mLocation, mDestination;

    public static  BottomSheetClienteFragment newInstance(String location, String destination){
        BottomSheetClienteFragment bf = new BottomSheetClienteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("location", location);
        bundle.putString("destination", destination);
        bf.setArguments(bundle);

        return bf;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = getArguments().getString("location");
        mDestination = getArguments().getString("destination");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_rider, container, false);
        TextView tvLocation = (TextView)view.findViewById(R.id.location);
        TextView tvDestination = (TextView)view.findViewById(R.id.destination);
        TextView tvDistance = (TextView)view.findViewById(R.id.distance);


        tvLocation.setText(mLocation);
        tvDestination.setText(mDestination);
        tvDistance.setText("");
        return view;
    }
}
