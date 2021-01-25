package com.wiryaimd.yamltranslator.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wiryaimd.yamltranslator.R;

public class DownloadingFragment extends DialogFragment {

    public DownloadingFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
        }catch (NullPointerException e){
            Toast.makeText(getContext(), "Failed crop", Toast.LENGTH_SHORT).show();
        }
        return inflater.inflate(R.layout.loadingdownload_dialog, container, false);
    }
}
