package cliente.transporte.sd.transportecliente;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SD on 1/11/2018.
 */

public class BottomSheetClienteFragment extends BottomSheetDialogFragment
{
    String mTag;

    public static  BottomSheetClienteFragment newInstance(String tag){
        BottomSheetClienteFragment bf = new BottomSheetClienteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TAG", tag);
        bf.setArguments(bundle);

        return bf;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString("TAG");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_rider, container, false);
        return view;
    }
}
