package com.wiryaimd.yamltranslator.fragment.dialog;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wiryaimd.yamltranslator.R;

public class DevinfoFragment extends DialogFragment {

    private TextView name, version, problem, contact;

    private Activity activity;

    public DevinfoFragment(){}

    public DevinfoFragment(Activity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }catch (NullPointerException e){
            Toast.makeText(getContext(), "Failed crop", Toast.LENGTH_SHORT).show();
        }
        return inflater.inflate(R.layout.devinfo_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        name = view.findViewById(R.id.devinfo_devname);
        version = view.findViewById(R.id.devinfo_versionapp);
        problem = view.findViewById(R.id.devinfo_problemissue);
        contact = view.findViewById(R.id.devinfo_contact);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", "adnyasutha003@gmail.com");
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(activity, "Copied Contact", Toast.LENGTH_LONG).show();
            }
        });

    }
}
