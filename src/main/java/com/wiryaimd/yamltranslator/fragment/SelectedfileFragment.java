package com.wiryaimd.yamltranslator.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.CoreComponentFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.wiryaimd.yamltranslator.MainActivity;
import com.wiryaimd.yamltranslator.R;
import com.wiryaimd.yamltranslator.util.RealPath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class SelectedfileFragment extends Fragment{

    private String filename;
    private Uri path;
    private static ArrayList<String> ymlcode;

    private Activity activity;
    private TextView tvfielname;
    private LinearLayout pastetextlinear;

    public SelectedfileFragment(Activity activity, Uri path, String filename){
        this.activity = activity;
        this.path = path;
        this.filename = filename;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.selectedfile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tvfielname = view.findViewById(R.id.selectfile_filename);
        pastetextlinear = view.findViewById(R.id.selectfile_pastetextlinear);
        tvfielname.setText(filename);

        ymlcode = new ArrayList<>();

        pastetextlinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.main_frame, new PastetextFragment(activity));
                ft.commit();
                MainActivity.typetrans = true;
            }
        });

        try {

            BufferedReader breader = new BufferedReader(new InputStreamReader(new FileInputStream(RealPath.from(activity, path))));
            String line = breader.readLine();
            while (line != null) {
                ymlcode.add(line);
                line = breader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<String> getPasteText() {
        return ymlcode;
    }

}
