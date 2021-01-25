package com.wiryaimd.yamltranslator.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.wiryaimd.yamltranslator.MainActivity;
import com.wiryaimd.yamltranslator.R;
import com.wiryaimd.yamltranslator.fragment.dialog.InfoFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class PastetextFragment extends Fragment{

    private LinearLayout selectfile;
    private static EditText pastetext;

    private static final int PERM_CODE = 10;

    private Activity activity;

    public PastetextFragment(Activity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pastetext_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        selectfile = view.findViewById(R.id.pastetxt_selectfilelinear);
        pastetext = view.findViewById(R.id.pastetxt_edt);

        selectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionCheck()){
                    openSelectFile();
                }
            }
        });
    }

    public static ArrayList<String> getPasteText() {
        ArrayList<String> text = new ArrayList<>();
        Scanner scanner = new Scanner(pastetext.getText().toString());
        while (scanner.hasNextLine()) {
            text.add(scanner.nextLine());
        }
        scanner.close();
        return text;
    }

    public static void requestErr() {
        pastetext.setError("");
        pastetext.requestFocus();
    }


    public void openSelectFile() {
        Intent intent;
        if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            intent.putExtra("CONTENT_TYPE", "*/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT); // or ACTION_OPEN_DOCUMENT
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        }
        try {
            startActivityForResult(Intent.createChooser(intent, "Select File"), PERM_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "Please install a File Manager.", Toast.LENGTH_LONG);
        }
    }

    public boolean permissionCheck(){
        boolean cekperm = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] perm = {Manifest.permission.READ_EXTERNAL_STORAGE};
                activity.requestPermissions(perm, PERM_CODE);
            }else{
                cekperm = true;
            }
        }else{
            cekperm = true;
        }
        return cekperm;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PERM_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        String uriString = uri.toString();
                        File yaml = new File(uriString);
                        String path = yaml.getAbsolutePath();
                        String displayName = null;

                        if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = activity.getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    System.out.println("cek cursor: " + displayName);
                                }
                            } finally {
                                try {
                                    cursor.close();
                                } catch (NullPointerException e) {
                                    Toast.makeText(activity, "Failed to get name file or use paste text", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = yaml.getName();
                            System.out.println("cek elseif: " + displayName);
                        } else {
                            displayName = path;
                            System.out.println("cek path: " + displayName);
                        }

                        System.out.println("displayname: " + displayName);

                        if (displayName != null) {
                            if (displayName.contains(".")) {
                                System.out.println("extension: " + displayName.contains(".yml"));
                                if (displayName.contains(".yml")) {
                                    MainActivity.typetrans = false;
                                    FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.main_frame, new SelectedfileFragment(activity, uri, displayName));
                                    ft.commit();
                                } else {
                                    Toast.makeText(activity, "Please select a .yml file", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(activity, "Please select a .yml file or use paste text", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(activity, "Failed to load file, please try again", Toast.LENGTH_SHORT).show();
                        }

                    }catch (NullPointerException e){
                        new InfoFragment("Failed to open file, or try using paste text").show(getFragmentManager(), "OpenFile");
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERM_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openSelectFile();
                }else{
                    Toast.makeText(activity, "Permission to open File is denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
