package com.wiryaimd.yamltranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.wiryaimd.yamltranslator.fragment.dialog.DevinfoFragment;
import com.wiryaimd.yamltranslator.fragment.dialog.DownloadingFragment;
import com.wiryaimd.yamltranslator.fragment.PastetextFragment;
import com.wiryaimd.yamltranslator.fragment.SelectedfileFragment;
import com.wiryaimd.yamltranslator.fragment.dialog.SettingswordFragment;
import com.wiryaimd.yamltranslator.fragment.TranslateFragment;
import com.wiryaimd.yamltranslator.util.DataLanguage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner spinfrom;
    private Spinner spinto;
    public static Button btntranslate;
    private TextView blacklist;
    private ImageView imgdevinfo;

    public static boolean middlepersen = true;
    public static boolean middlekurawal = false;
    public static boolean startwith = false;
    public static boolean sidedan = true;
    public static boolean uppercase = false;
    public static boolean stripe = true;

    public static ArrayList<String> disabledword;
    public static RewardedAd rewardedAd;

    private int from = 0;
    private int to = 0;
    public static boolean typetrans; // true = paste, false = selectfile

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rewardedAd = new RewardedAd(MainActivity.this, getString(R.string.ad1));

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdLoaded() {
                System.out.println("success load ad");
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                System.out.println("failed load ad");
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        typetrans = true;
        disabledword = new ArrayList<>();
        disabledword.add("true");
        disabledword.add("false");
        disabledword.add("enable");
        disabledword.add("disable");

        spinfrom = findViewById(R.id.main_spinfrom);
        spinto = findViewById(R.id.main_spinto);
        btntranslate = findViewById(R.id.main_btntranslate);
        blacklist = findViewById(R.id.main_blacklistword);
        imgdevinfo = findViewById(R.id.main_devinfo);

        ArrayAdapter<String> flagadapterfrom = new ArrayAdapter<>(MainActivity.this, R.layout.item_mspinner, R.id.itemspin_text, DataLanguage.flagfrom);
        ArrayAdapter<String> flagadapterto = new ArrayAdapter<>(MainActivity.this, R.layout.item_mspinner, R.id.itemspin_text, DataLanguage.flagto);
        spinfrom.setAdapter(flagadapterfrom);
        spinto.setAdapter(flagadapterto);

        PastetextFragment pastetextFragment = new PastetextFragment(MainActivity.this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, pastetextFragment);
        ft.commit();

        imgdevinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DevinfoFragment(MainActivity.this).show(getSupportFragmentManager(), "DevInfo");
            }
        });

        spinfrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("selected item: " + position);
                from = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("select to: " + position);
                to = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btntranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (from != 0 && to != 0) {
                        TranslatorOptions options = new TranslatorOptions.Builder()
                                .setSourceLanguage(DataLanguage.flagid[from])
                                .setTargetLanguage(DataLanguage.flagid[to])
                                .build();

                        final Translator translator = Translation.getClient(options);

                        DownloadConditions conditions = new DownloadConditions.Builder()
                                .build();

                        if (typetrans) {
                            if (!PastetextFragment.getPasteText().isEmpty()) {
                                showInfo(translator, conditions, PastetextFragment.getPasteText());
                            } else {
                                PastetextFragment.getPasteText();
                            }
                        } else {
                            if (!SelectedfileFragment.getPasteText().isEmpty()) {
                                showInfo(translator, conditions, SelectedfileFragment.getPasteText());
                            } else {
                                Toast.makeText(MainActivity.this, "Your yml file is empty", Toast.LENGTH_LONG).show();
                            }
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Please Select Language", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Failed to translate please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        blacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingswordFragment settingswordFragment = new SettingswordFragment(
                        disabledword,
                        middlepersen,
                        middlekurawal,
                        startwith,
                        sidedan,
                        uppercase,
                        stripe);
                settingswordFragment.show(getSupportFragmentManager(), "SettingsWord");
            }
        });

    }

    public void translateProcess(Translator translator, ArrayList<String> text){
        btntranslate.setEnabled(false);
        btntranslate.setBackgroundResource(R.drawable.custom_background1b);
        TranslateFragment translateFragment = new TranslateFragment(MainActivity.this, translator, text);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, translateFragment);
        ft.commit();
        System.out.println("translating..");
    }

    public void showInfo(Translator translator, DownloadConditions conditions, ArrayList<String> text){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.downloadneed_item, null);
        Button btnok = view.findViewById(R.id.downloadneed_ok);
        Button btncancel = view.findViewById(R.id.downloadneed_cancel);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadModel(translator, conditions, text);
                dialog.dismiss();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void downloadModel(Translator translator, DownloadConditions conditions, ArrayList<String> text){
        DownloadingFragment downloadingFragment = new DownloadingFragment();
        downloadingFragment.show(getSupportFragmentManager(), "Downloading");
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translateProcess(translator, text);
                downloadingFragment.dismiss();
                System.out.println("download successfuly");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("failed download: " + e.getMessage());
            }
        });
    }
}
