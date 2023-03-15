package com.sintel.webcontentanalyzer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class HistoriqueActivity extends AppCompatActivity {
    ArrayList<String> liens ;
    ArrayList<Date> dates;
    ListView listView;
    SharedPreferences sharedPreferences;
    Set<String> historique = new HashSet<>();
    ImageView delete_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.historique_bar_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        liens= new ArrayList<String>();
        dates=new ArrayList<Date>();
        listView=findViewById(R.id.liste_historique);
        delete_all=findViewById(R.id.ImgDelete);

        sharedPreferences = getSharedPreferences("com.sintel.webcontentanalyzer", MODE_PRIVATE);
        historique = sharedPreferences.getStringSet("historique", new HashSet<String>());

        liens = getIntent().getStringArrayListExtra("liens");
        dates = (ArrayList<Date>) getIntent().getSerializableExtra("date");

        Afficher_Historiques();
        
        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HistoriqueActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                deleleAll();
            }
        });
    }

    private void deleleAll() {
        final AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(HistoriqueActivity.this);
        alertDialogBuilder.setTitle("Confirmer la suppression !");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_warning_24);
        alertDialogBuilder.setMessage("Voulez-vous vraiment vider l'historique?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SupprimerTousHistorique();
            }

            private void SupprimerTousHistorique() {
                //on supprime le lien dans la liste des liens et ainsi que la date
                //ArrayList<String> corbeille_liens = new ArrayList<String>() ;
                //ArrayList<String> corbeille_date = new ArrayList<String>() ;
                liens.clear();
                dates.clear();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json_lien=gson.toJson(liens);
                String json_date=gson.toJson(dates);
                editor.putString("liens",json_lien);
                editor.putString("dates",json_date);
                editor.apply();
                Toast.makeText(HistoriqueActivity.this, "Historique vidé", Toast.LENGTH_SHORT).show();
                //alertDialogBuilder.dismiss();

                Intent intent = new Intent(HistoriqueActivity.this, HistoriqueActivity.class);
                intent.putExtra("liens", liens);
                intent.putExtra("date", dates);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });

        alertDialogBuilder.setNeutralButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog= alertDialogBuilder.create();
        alertDialog.show();
    }


    private void Afficher_Historiques() {
        class HistoriqueAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return liens.size();
            }

            @Override
            public Object getItem(int position) {
                return liens.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View  view = getLayoutInflater().inflate(R.layout.custom_historique_2, null);

                TextView lien = (TextView) view.findViewById(R.id.lien);
                TextView date = (TextView)view.findViewById(R.id.date_lien);

                DateFormat format = new SimpleDateFormat("dd-MM yyyy HH:mm");

                lien.setText(liens.get(position));
                date.setText(format.format(dates.get(position)));

                return view;
            }
        }
        HistoriqueAdapter adapter = new HistoriqueAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Afficher_option(position);
            }
        });

    }

    private void Afficher_option(final int position) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_option_historique,null);
        TextView btn_copier=view.findViewById(R.id.copie_lien_historique);
        TextView btn_ouvrir=view.findViewById(R.id.ouvrir_lien_historique);
        TextView btn_fermer=view.findViewById(R.id.btn_annuler_historique);
        TextView btn_partager=view.findViewById(R.id.partager_lien_historique);
        TextView btn_supprimer=view.findViewById(R.id.supprimer_lien_historique);

        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);


        btn_copier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setText(liens.get(position));
                Toast.makeText(HistoriqueActivity.this, "Lien copier dans presse papier", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        btn_ouvrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browerIntent=new Intent("android.intent.action.VIEW", Uri.parse(liens.get(position)));
                startActivity(browerIntent);
                alertDialog.dismiss();
            }
        });


        btn_partager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,liens.get(position));
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        btn_supprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                final AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(HistoriqueActivity.this);
                alertDialogBuilder.setTitle("Confirmer la suppression !");
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_warning_24);
                alertDialogBuilder.setMessage("Voulez-vous vraiment supprimer l'historique?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SupprimerHistorique();
                    }
                });

                alertDialogBuilder.setNeutralButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                AlertDialog alertDialog= alertDialogBuilder.create();
                alertDialog.show();
            }

            private void SupprimerHistorique() {
                //on supprime le lien dans la liste des liens et ainsi que la date
                liens.remove(position);
                dates.remove(position);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json_lien=gson.toJson(liens);
                String json_date=gson.toJson(dates);
                editor.putString("liens",json_lien);
                editor.putString("dates",json_date);
                editor.apply();
                Toast.makeText(HistoriqueActivity.this, "Historique supprimé", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

                Intent intent = new Intent(HistoriqueActivity.this, HistoriqueActivity.class);
                intent.putExtra("liens", liens);
                intent.putExtra("date", dates);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });

        btn_fermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }
}