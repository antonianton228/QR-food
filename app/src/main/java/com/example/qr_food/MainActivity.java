package com.example.qr_food;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button scanBut;

    TextView glueCurrTxt;
    TextView glueNeedTxt;
    TextView kkalCurrTxt;
    TextView kkalNeedTxt;


    ProgressBar glueProg;
    ProgressBar kkalProg;

    Button glueCurrBtn;
    Button glueNeedBtn;
    Button kkalCurrBtn;
    Button kkalNeedBtn;



    SharedPreferences prefs;
    String[] b;
    int glueCurr;
    int glueNeed;
    int kkalCurr;
    int kkalNeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        glueCurrTxt = findViewById(R.id.textView);
        glueNeedTxt = findViewById(R.id.textView3);
        kkalCurrTxt = findViewById(R.id.KkalToday);
        kkalNeedTxt = findViewById(R.id.kkalNeed);

        glueProg = findViewById(R.id.glueProg);
        kkalProg = findViewById(R.id.kkalProg);

        glueCurrBtn = findViewById(R.id.todayGlueBut);
        glueNeedBtn = findViewById(R.id.allGlueBut);
        kkalCurrBtn = findViewById(R.id.todayKkalBut);
        kkalNeedBtn = findViewById(R.id.allKkalBut);


        scanBut = findViewById(R.id.scanBut);

        glueCurrBtn.setOnClickListener(this);
        kkalCurrBtn.setOnClickListener(this);
        glueNeedBtn.setOnClickListener(this);
        kkalNeedBtn.setOnClickListener(this);
        scanBut.setOnClickListener(this);


        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);




    }


    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == scanBut.getId()) {
            scan();
        }
        if (view.getId() == glueCurrBtn.getId()){
            EditText text = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Добавить сахар")
                    .setMessage("Запишите, сколько вы съели сахар").setView(text)

                    .setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if(isNumeric(text.getText().toString())){
                            glueCurr += Integer.parseInt(text.getText().toString());
                            updateAll();}
                            else{
                                Toast.makeText(getApplicationContext(), "Не удалось добавить. Введите целочисленное значение!", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }

        if (view.getId() == kkalCurrBtn.getId()){
            EditText text = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Добавить калории")
                    .setMessage("Запишите, сколько вы съели калорий").setView(text)

                    .setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if(isNumeric(text.getText().toString())){
                                kkalCurr += Integer.parseInt(text.getText().toString());
                                updateAll();}
                            else{
                                Toast.makeText(getApplicationContext(), "Не удалось добавить. Введите целочисленное значение!", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }

        if (view.getId() == kkalNeedBtn.getId()){
            EditText text = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Установить максимум калорий")
                    .setMessage("Напишите, сколько вы хотите есть калорий в день").setView(text)

                    .setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if(isNumeric(text.getText().toString())){
                                kkalNeed = Integer.parseInt(text.getText().toString());
                                updateAll();}
                            else{
                                Toast.makeText(getApplicationContext(), "Не удалось добавить. Введите целочисленное значение!", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }

        if (view.getId() == glueNeedBtn.getId()){
            EditText text = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Установить максимум сахара")
                    .setMessage("Напишите, сколько вы хотите есть сахара в день").setView(text)

                    .setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if(isNumeric(text.getText().toString())){
                                glueNeed = Integer.parseInt(text.getText().toString());
                                updateAll();}
                            else{
                                Toast.makeText(getApplicationContext(), "Не удалось добавить. Введите целочисленное значение!", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }
    }

    private void scan(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scaning code");
        integrator.initiateScan();
    }

    public String getRequest(String s) throws Exception {
        return getHTML(s);
    }

    public String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }

        return result.toString();
    }


    @Override
    public void onActivityResult(int requestCode, int resCode, Intent data){
        super.onActivityResult(requestCode, resCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resCode, data);
        if (result.getContents() != null){
            EditText text = new EditText(this);
            try {
                b = getRequest("https://functions.yandexcloud.net/d4e9p891a23v86bld7pd?scan=" + result.getContents()).split("_");
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
            if (b.length == 2){
                AlertDialog.Builder bulder = new AlertDialog.Builder(this);
                bulder.setMessage("Сколько вы съели в граммах? (" + b[0] + " Ккал и " + b[1] + "г. сахара на 100г. продукта)" );

                bulder.setView(text).setPositiveButton("Сканировать ещё", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scan();
                    }
                }).setNeutralButton("Подтвердить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isNumeric(text.getText().toString())){
                            if (b.length == 2){
                            kkalCurr += Integer.parseInt(b[0]) * Integer.parseInt(text.getText().toString()) / 100;
                            glueCurr += Integer.parseInt(b[1]) * Integer.parseInt(text.getText().toString()) / 100;
                            updateAll();}}
                            else{
                                Toast.makeText(getApplicationContext(), "Не удалось добавить. Введите целочисленное значение!", Toast.LENGTH_LONG).show();
                                finish();
                            }

                    }
                }).setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dia = bulder.create();
                dia.show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Товара пока нет в нашей базе данных. Добавьте калории и сахар вручную!", Toast.LENGTH_LONG).show();
            finish();
        }

        }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("kkalCur", kkalCurr).apply();
        editor.putInt("kkalNeed", kkalNeed).apply();
        editor.putInt("glueCur", glueCurr).apply();
        editor.putInt("glueNeed", glueNeed).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        kkalCurr = prefs.getInt("kkalCur", 0);
        kkalNeed = prefs.getInt("kkalNeed", 0);
        glueCurr = prefs.getInt("glueCur", 0);
        glueNeed = prefs.getInt("glueNeed", 0);

        updateAll();

    }

    void updateAll(){
        kkalCurrTxt.setText("За сегодня " + kkalCurr + " Ккал");
        kkalNeedTxt.setText("Ещё можно " + (kkalNeed - kkalCurr) + " Ккал");

        glueCurrTxt.setText("За сегодня " + glueCurr + " грамм сахара");
        glueNeedTxt.setText("Ещё можно " + (glueNeed - glueCurr) + " грамм сахара");

        glueProg.setMax(glueNeed);
        glueProg.setProgress(glueCurr);

        kkalProg.setMax(kkalNeed);
        kkalProg.setProgress(kkalCurr);
    }}
