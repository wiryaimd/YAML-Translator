package com.wiryaimd.yamltranslator.fragment;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.Translator;
import com.wiryaimd.yamltranslator.MainActivity;
import com.wiryaimd.yamltranslator.R;

import java.util.ArrayList;

public class TranslateFragment extends DialogFragment {

    private TextView tvline;
    private EditText edtline;
    private Button btncancel;
    private ProgressBar loading;

    private Translator translator;
    private ArrayList<String> text;
    private int mindex;
    private int countrawlist;

    private Activity activity;

    private StringBuilder sbtranslated;
    private String[] rawlist;
    private ArrayList<String> persenword;
    private ArrayList<String> sidedanword;
    private String[] keyside = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f", "m", "n", "k", "l", "u", "o"};

    public TranslateFragment(Activity activity, Translator translator, ArrayList<String> text){
        this.activity = activity;
        this.translator = translator;
        this.text = text;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.translate_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mindex = 0;
        countrawlist = 0;
        sbtranslated = new StringBuilder();

        tvline = view.findViewById(R.id.translate_line);
        edtline = view.findViewById(R.id.translate_edtlive);
        btncancel = view.findViewById(R.id.translate_btncancel);
        loading = view.findViewById(R.id.translate_loading);
        loading.setVisibility(View.VISIBLE);

        tvline.setText("Translating line " + countrawlist + "...");
        edtline.setText("");

        startwith(text.get(mindex));

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.main_frame, new PastetextFragment(activity));
                ft.commit();
                MainActivity.btntranslate.setEnabled(true);
                MainActivity.btntranslate.setBackgroundResource(R.drawable.custom_background1);
            }
        });

    }

    public void startwith(String text){
        rawlist = null;

        if (!text.trim().isEmpty()) {
            for (int i = 0; i < text.length(); i++) {
                if (!text.substring(i, i + 1).equalsIgnoreCase(" ")) {
                    if (!text.substring(i, i + 1).equalsIgnoreCase("#")) {
                        defaultSet(text);
                        break;
                    } else {
                        if (MainActivity.startwith) {
                            sbtranslated.append(text).append("\n");
                            nextLine();
                            break;
                        } else {
                            if (text.length() > 4) {
                                if (text.replaceAll("#", "").length() < text.length() / 2){
                                    sbtranslated.append(text).append("\n");
                                    nextLine();
                                }else if(text.replaceAll("~", "").length() < text.length() / 2){
                                    sbtranslated.append(text).append("\n");
                                    nextLine();
                                }else if(text.replaceAll("=", "").length() < text.length() / 2) {
                                    sbtranslated.append(text).append("\n");
                                    nextLine();
                                }else {
                                    translateText(text, true);
                                }
                            }else{
                                sbtranslated.append(text).append("\n");
                                nextLine();
                            }
                            break;
                        }
                    }
                }
            }
        }else{
            sbtranslated.append("").append("\n");
            countrawlist = 0;
            nextLine();
        }
    }

    public void defaultSet(String text){
        boolean stripcheck = false;
        for (int i = 0; i < text.length(); i++) {
            if (!text.substring(i, i + 1).equalsIgnoreCase(" ")) {
                if (text.substring(i, i + 1).equalsIgnoreCase("-")) {
                    stripcheck = true;
                    break;
                }else{
                    break;
                }
            }
        }

        if (!stripcheck) {
            if (text.contains(":")) {
                if (text.substring(text.indexOf(":")).length() > 1) {
                    String textvalue;
                    textvalue = text.substring(text.indexOf(":") + 2);
                    sbtranslated.append(text.substring(0, text.indexOf(":") + 2));

                    System.out.println("txtprefix:" + text.substring(0, text.indexOf(":") + 2));
                    System.out.println("txtvalue:" + textvalue);

                    configSettings(textvalue);
                }else{
                    sbtranslated.append(text).append("\n");
                    nextLine();
                }
            } else {
                checkVoid(text);
            }
        }else{
            if (MainActivity.stripe) {
                sbtranslated.append(text).append("\n");
                nextLine();
            }else{
                checkVoid(text);
            }
        }
    }

    public void checkVoid(String text){
        for (int i = 0; i < text.length(); i++){
            if (!text.substring(i, i + 1).equalsIgnoreCase(" ")){
                if (text.substring(i + 1).length() <= 4){
                    System.out.println("voidcek: " + text.substring(i + 1));
                    sbtranslated.append(text).append("\n");
                    nextLine();
                }else{
                    sbtranslated.append(text.substring(0, i));
                    configSettings(text);
                }
                break;
            }
        }
    }

    public void configSettings(String text){

        sidedanword = new ArrayList<String>();
        persenword = new ArrayList<String>();

        if(MainActivity.middlepersen){
            int persen = text.length() - text.replaceAll("%", "").length();
            int fromp = 0;
            int top = 0;
            if (persen >= 2) {
                ArrayList<Point> mpersen = new ArrayList<Point>();
                for (int count = 0; count < persen / 2; count++) {
                    from: for (int i = (mpersen.size() >= 1 ? top + 1 : top); i < text.length(); i++) {
                        if (text.substring(i, i + 1).equalsIgnoreCase("%")) {
                            fromp = i;
                            System.out.println("% first: " + i);
                            break from;
                        }
                    }

                    to: for (int j = fromp + 1; j < text.length(); j++){
                        if (text.substring(j, j + 1).equalsIgnoreCase(" ")){
                            fromp = j;
                            top = j;
                            persen += 1;
                            System.out.println("% white: " + j);
                            break to;
                        }else if(text.substring(j, j + 1).equalsIgnoreCase("%")){
                            System.out.println("% second: " + j);
                            top = j + 1;
                            break to;
                        }
                    }

                    if (fromp != top) {
                        mpersen.add(new Point(fromp, top));
                    }

                }

                String strpersen = text;
                for (Point point : mpersen) {
                    strpersen = strpersen.replaceAll(text.substring(point.x, point.y), "!@" + text.substring(point.x, point.y) + "!@");
                    System.out.println("strpersen: " + strpersen);
                    persenword.add(text.substring(point.x, point.y));
                }

                if (MainActivity.sidedan){
                    if (strpersen.contains("&")) {
                        for (int i = 0; i < keyside.length; i++){
                            if (strpersen.contains("&" + keyside[i])){
                                strpersen = strpersen.replaceAll("&" + keyside[i], "@@" + "&" + keyside[i] + "@@");
                                sidedanword.add("&" + keyside[i]);
                            }
                        }
                    }
                }

                // WILL BE FIXED SOON
//                if (uppercase){
//                    for (int i = 0; i < anjas.length; i++){
//                        for (int j = 0; j < persenword.size(); j++) {
//                            if (!anjas[i].equalsIgnoreCase(persenword.get(j))){
//                                boolean isuppercase = false;
//                                for (int k = 0; k < anjas[i].length(); k++) {
//                                    char[] arrchar = anjas[i].toCharArray();
//                                    if (Character.isUpperCase(arrchar[k])) {
//                                        isuppercase = true;
//                                    }else{
//                                        isuppercase = false;
//                                        break;
//                                    }
//                                }
//                                if (isuppercase){
//                                    cek = cek.replaceAll(anjas[i], "~@" + anjas[i] + "~@");
//                                    uppercaseword.add(anjas[i]);
//                                }
//                            }
//                        }
//                    }
//                }

                rawlist = strpersen.split("!@|@@");
            }
        }

        if (MainActivity.sidedan && rawlist == null){
            String sidedanstr = text;
            if (sidedanstr.contains("&")) {
                for (int i = 0; i < keyside.length; i++){
                    if (sidedanstr.contains("&" + keyside[i])){
                        sidedanstr = sidedanstr.replaceAll("&" + keyside[i], "@@" + "&" + keyside[i] + "@@");
                        sidedanword.add("&" + keyside[i]);
                        rawlist = sidedanstr.split("@@");
                    }
                }
            }
        }

        // WILL BE FIXED SOON
//        if (uppercase && !middlepersen){
//                arrupper = text.split(" ");
//                for (int i = 0; i < arrupper.length; i++){
//                    boolean isuppercase = false;
//                    for (int j = 0; j < anjas[i].length(); j++) {
//                        char[] arrchar = anjas[i].toCharArray();
//                        if (Character.isUpperCase(arrchar[j])) {
//                            isuppercase = true;
//                        }else{
//                            isuppercase = false;
//                            break;
//                        }
//                    }
//                    if (isuppercase){
//                        uppercaseword.add(arrupper[i]);
//                    }
//                }
//            System.out.println("mantapu: " + sb.toString());
//
//        }
        translateText(text, false);
    }

    public void translateText(String text, boolean isPagar){
        if (isPagar){
            translator.translate(text.substring(text.indexOf("#"))).addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public synchronized void onSuccess(String s) {
                    sbtranslated.append(s).append("\n");
                    nextLine();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public synchronized void onFailure(@NonNull Exception e) {
                    sbtranslated.append(text).append("\n");
                    nextLine();
                }
            });
        }else{
            boolean disableworld = false;
            for (int i = 0; i < MainActivity.disabledword.size(); i++){
                if (text.equalsIgnoreCase(MainActivity.disabledword.get(i))){
                    disableworld = true;
                    break;
                }
            }

            if (!disableworld) {
                if (rawlist != null) {
                    countrawlist = 0;
                    validWord(rawlist[countrawlist]);
                } else {
                    translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            sbtranslated.append(s).append("\n");
                            nextLine();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            sbtranslated.append(text).append("\n");
                            nextLine();
                        }
                    });
                }
            }else{
                sbtranslated.append(text).append("\n");
                countrawlist = 0;
                nextLine();
            }
        }
    }

    public void validWord(String text){
        if (countrawlist < rawlist.length) {
            if (MainActivity.middlepersen && !MainActivity.sidedan) {
                boolean notvalid = false;
                for (int i = 0; i < persenword.size(); i++) {
                    if (text.equalsIgnoreCase(persenword.get(i))) {
                        notvalid = true;
                        break;
                    }
                }
                if (notvalid) {
                    sbtranslated.append(text);
                    countrawlist += 1;
                    validWord(rawlist[countrawlist]);
                } else {
                    processingTranslation(rawlist[countrawlist]);
                }
            } else if (!MainActivity.middlepersen && MainActivity.sidedan) {
                boolean notvalid = false;
                for (int i = 0; i < sidedanword.size(); i++) {
                    if (text.equalsIgnoreCase(sidedanword.get(i))) {
                        notvalid = true;
                        break;
                    }
                }
                if (notvalid) {
                    sbtranslated.append(text);
                    countrawlist += 1;
                    if (countrawlist < rawlist.length) {
                        validWord(rawlist[countrawlist]);
                    }else {
                        validWord(rawlist[countrawlist - 1]);
                    }
                } else {
                    processingTranslation(rawlist[countrawlist]);
                }
            } else if (MainActivity.middlepersen && MainActivity.sidedan) {
                boolean notvalid = false;
                for (int i = 0; i < persenword.size(); i++) {
                    if (text.equalsIgnoreCase(persenword.get(i))) {
                        notvalid = true;
                        break;
                    }
                }

                boolean notvalid2 = false;
                for (int i = 0; i < sidedanword.size(); i++) {
                    if (text.equalsIgnoreCase(sidedanword.get(i))) {
                        notvalid2 = true;
                        break;
                    }
                }

                if (notvalid || notvalid2) {
                    sbtranslated.append(text);
                    countrawlist += 1;
                    if (countrawlist < rawlist.length) {
                        validWord(rawlist[countrawlist]);
                    }else {
                        validWord(rawlist[countrawlist - 1]);
                    }
                } else {
                    processingTranslation(rawlist[countrawlist]);
                }

            }else{
                processingTranslation(rawlist[countrawlist]);
            }
        }else{
            countrawlist = 0;
            nextLine();
        }
    }

    public void processingTranslation(String text){

        translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                countrawlist += 1;
                if (countrawlist <= rawlist.length) {
                    System.out.println("check space: " + s);
                    sbtranslated.append(checkSpace(text, s));
                    if (rawlist.length == countrawlist){
                        sbtranslated.append("\n");
                        validWord(rawlist[countrawlist - 1]);
                    }else{
                        validWord(rawlist[countrawlist]);
                    }
                }else{
                    System.out.println("kepake ganih");
                    countrawlist = 0;
                    nextLine();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sbtranslated.append(text).append("\n");
            }
        });

    }

    public void nextLine(){
        mindex += 1;
        if (mindex < text.size()){
            tvline.setText("Translating line " + mindex + "...");
            edtline.setText(sbtranslated.toString());
            edtline.setSelection(sbtranslated.toString().length() - 1);
            startwith(text.get(mindex));
        }else{
            btncancel.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);

            FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.translate_frame, new TransdoneFragment(activity, sbtranslated));
            ft.commit();
        }
    }

    public String checkSpace(String sfrom, String sto){
        String result = sto;

        if(sfrom.length() > 0) {
            if (sfrom.substring(0, 1).equalsIgnoreCase(" ") &&
                    sfrom.substring(sfrom.length() - 1).equalsIgnoreCase(" ")) {
                if (countrawlist != 1) {
                    result = " " + sto + " ";
                }
            } else if (sfrom.substring(0, 1).equalsIgnoreCase(" ")) {
                if (countrawlist != 1) {
                    result = " " + sto;
                }
            } else if (sfrom.substring(sfrom.length() - 1).equalsIgnoreCase(" ")) {
                result = sto + " ";
            }
        }

        return result;
    }
}
