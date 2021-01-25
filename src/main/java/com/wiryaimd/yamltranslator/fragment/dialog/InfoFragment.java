package com.wiryaimd.yamltranslator.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wiryaimd.yamltranslator.R;

public class InfoFragment extends DialogFragment {

    private TextView tvdesc;
    private Button btnok;

    private String desc;

    public InfoFragment(){}

    public InfoFragment(String desc){
        this.desc = desc;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }catch (NullPointerException e){
            Toast.makeText(getContext(), "Failed crop", Toast.LENGTH_SHORT).show();
        }
        return inflater.inflate(R.layout.info_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tvdesc = view.findViewById(R.id.infodialog_desc);
        btnok = view.findViewById(R.id.infodialog_btnok);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        tvdesc.setText(desc);
    }
}
