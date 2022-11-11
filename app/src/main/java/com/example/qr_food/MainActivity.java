package com.example.qr_food;

import android.app.Person;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button scanBut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        scanBut = findViewById(R.id.scanBut);
        scanBut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        scan();

    }

    private void scan(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scaning code");
        integrator.initiateScan();
    }

    public void getRequest(String s) throws Exception {
        getHTML(s);
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




        Gson g = new Gson();
        Site sitedata = g.fromJson(result.toString(), Site.class);

        Toast.makeText(this, sitedata.getName(), Toast.LENGTH_LONG).show();
        return result.toString();
    }


    @Override
    public void onActivityResult(int requestCode, int resCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resCode, data);
        if (result.getContents() != null){
                AlertDialog.Builder bulder = new AlertDialog.Builder(this);
                bulder.setMessage(result.getContents());
            try {
                getRequest("https://barcodes.olegon.ru/api/card/name/" + result.getContents() + "/B368149299427055938374146157384");
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
                bulder.setPositiveButton("again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scan();
                    }
                }).setNegativeButton("exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dia = bulder.create();
                dia.show();
            }
            else {
                Toast.makeText(this, "Пшол отсюда", Toast.LENGTH_LONG).show();
            }
        }
    }
class Site  {
    public String status;

    public String getStatus() {
        return status;
    }

    public String getName() {
        String c = "";
        for(int i = 0; i < names.size(); i++){
            if (c.length() < names.get(i).length()){
                c = names.get(i);
            }
        }
        return c;
    }

    public ArrayList<String> names;

}
