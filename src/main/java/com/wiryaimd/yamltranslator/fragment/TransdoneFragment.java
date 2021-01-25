package com.wiryaimd.yamltranslator.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.wiryaimd.yamltranslator.MainActivity;
import com.wiryaimd.yamltranslator.R;
import com.wiryaimd.yamltranslator.fragment.dialog.InfoFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class TransdoneFragment extends Fragment {

    private Activity activity;
    private ImageView imgsave, imgcopy;
    private Button btnclose;

    private StringBuilder strbuilder;

    public TransdoneFragment(Activity activity, StringBuilder strbuilder) {
        this.activity = activity;
        this.strbuilder = strbuilder;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.translatedone_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

            }
        };
        MainActivity.rewardedAd.show(activity, rewardedAdCallback);

        imgsave = view.findViewById(R.id.transdone_imgsave);
        imgcopy = view.findViewById(R.id.transdone_imgcopy);
        btnclose = view.findViewById(R.id.transdone_btnclose);

        imgcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", strbuilder.toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(activity, "Copied Translated Text", Toast.LENGTH_LONG).show();
            }
        });

        imgsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream fos = null;
                    String path = activity.getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/translatedymlfile";
                    File dir = new File(path);
                    dir.mkdir();

                    String filename = "translatedyml-" + UUID.randomUUID().toString() + ".yml";
                    File output = new File(dir, filename);

                    fos = new FileOutputStream(output);
                    fos.write(strbuilder.toString().getBytes());
                    fos.flush();
                    fos.close();

                    new InfoFragment("Translated text stored on " + path + "/" + filename).show(getFragmentManager(), "SavedYml");
                } catch (IOException e) {
                    new InfoFragment("Failed to save translated text, try using copy text").show(getFragmentManager(), "FailedSave");
                    System.out.println(e.getMessage());
                } catch (NullPointerException e){
                    new InfoFragment("Failed to save translated text, try using copy text").show(getFragmentManager(), "FailedSave");
                }
            }
        });

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.rewardedAd = new RewardedAd(activity, getString(R.string.ad1));

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
                MainActivity.rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.main_frame, new PastetextFragment(activity));
                ft.commit();
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.translate_frame)).commit();
                MainActivity.btntranslate.setEnabled(true);
                MainActivity.btntranslate.setBackgroundResource(R.drawable.custom_background1);
            }
        });

    }
}
