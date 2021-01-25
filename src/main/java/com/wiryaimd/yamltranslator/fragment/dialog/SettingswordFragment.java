package com.wiryaimd.yamltranslator.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiryaimd.yamltranslator.MainActivity;
import com.wiryaimd.yamltranslator.R;
import com.wiryaimd.yamltranslator.adapter.BlacklistAdapter;

import java.util.ArrayList;

public class SettingswordFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private Switch middlepersen, middlekurawal, startwith, sidedan, uppercase, stripe;

    private Button save, addword;
    private EditText edtaddword;

    private ArrayList<String> disabledword;
    public boolean bmiddlepersen;
    public boolean bmiddlekurawal;
    public boolean bstartwith;
    public boolean bsidedan;
    public boolean buppercase;
    public boolean bstripe;

    public SettingswordFragment(){}

    public SettingswordFragment(ArrayList<String> disabledword, boolean bmiddlepersen, boolean bmiddlekurawal, boolean bstartwith, boolean bsidedan, boolean buppercase, boolean bstripe) {
        this.disabledword = disabledword;
        this.bmiddlepersen = bmiddlepersen;
        this.bmiddlekurawal = bmiddlekurawal;
        this.bstartwith = bstartwith;
        this.bsidedan = bsidedan;
        this.buppercase = buppercase;
        this.bstripe = bstripe;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }catch (NullPointerException e){
            Toast.makeText(getContext(), "Failed crop", Toast.LENGTH_SHORT).show();
        }
        return inflater.inflate(R.layout.blacklistword_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.blacklist_recyclerview);

        middlepersen = view.findViewById(R.id.blacklist_middlepersen);
        middlekurawal = view.findViewById(R.id.blacklist_middlekurawal);
        startwith = view.findViewById(R.id.blacklist_startwith);
        sidedan = view.findViewById(R.id.blacklist_sidedan);
        uppercase = view.findViewById(R.id.blacklist_uppercase);
        save = view.findViewById(R.id.blacklist_btnsave);
        addword = view.findViewById(R.id.blacklist_btnaddword);
        edtaddword = view.findViewById(R.id.blacklist_inputword);
        stripe = view.findViewById(R.id.blacklist_stripe);

        middlepersen.setChecked(bmiddlepersen);
        middlekurawal.setChecked(bmiddlekurawal);
        startwith.setChecked(bstartwith);
        sidedan.setChecked(bsidedan);
        uppercase.setChecked(buppercase);
        stripe.setChecked(bstripe);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new BlacklistAdapter(getContext(), disabledword, recyclerView));

        addword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtaddword.getText().toString().isEmpty()){
                    disabledword.add(edtaddword.getText().toString());
                    MainActivity.disabledword = disabledword;
                    recyclerView.setAdapter(new BlacklistAdapter(getContext(), disabledword, recyclerView));
                    edtaddword.setText("");
                }else{
                    edtaddword.setError("Please input a word");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.middlepersen = middlepersen.isChecked();
                MainActivity.middlekurawal = middlekurawal.isChecked();
                MainActivity.startwith = startwith.isChecked();
                MainActivity.sidedan = sidedan.isChecked();
                MainActivity.uppercase = uppercase.isChecked();
                MainActivity.stripe = stripe.isChecked();
                try {
                    getDialog().dismiss();
                }catch (NullPointerException e){
                    Toast.makeText(getContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
