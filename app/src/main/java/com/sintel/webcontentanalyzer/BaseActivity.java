package com.sintel.webcontentanalyzer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void noter(MenuItem item) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.sintel.webcontentanalyzer"));
            startActivity(intent);
    }

    public void fulfulde(MenuItem item) {
        startActivity(new Intent(this,FulfuldeAnalysis.class));
    }

    public void StartApropos(MenuItem item) {
        Intent intent = new Intent(BaseActivity.this, APropos.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    public void exit(View view) {
        AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(BaseActivity.this);
        alertDialogBuilder.setTitle("Confirmer");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_warning_24);
        alertDialogBuilder.setMessage("Voulez-vous vraiment quitter?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog= alertDialogBuilder.create();
        alertDialog.show();
    }
}

