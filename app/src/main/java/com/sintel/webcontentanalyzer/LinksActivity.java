package com.sintel.webcontentanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LinksActivity extends AppCompatActivity {
    ArrayList<String> Liste_liens;
    ArrayList<String> liste_url_liens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);

        Liste_liens = new ArrayList<String>();
        liste_url_liens= new ArrayList<String>();

        Liste_liens = getIntent().getStringArrayListExtra("links");
        liste_url_liens = getIntent().getStringArrayListExtra("url_links");

        ViewLink();
    }

    private void ViewLink() {
        final  ListView listView = findViewById(R.id.Links);
        class LienAdapter extends BaseAdapter{

            @Override
            public int getCount() {
                return liste_url_liens.size();
            }

            @Override
            public Object getItem(int position) {
                return liste_url_liens.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View  view = getLayoutInflater().inflate(R.layout.link_adapter, null);

                TextView texte = (TextView) view.findViewById(R.id.texte);
                TextView texte_url = (TextView)view.findViewById(R.id.url_texte);

                texte.setText(Liste_liens.get(position));
                texte_url.setText(liste_url_liens.get(position));

                return view;
            }
        }
        LienAdapter adapter = new LienAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                option(position);
            }
        });
    }

    private void option(final int position) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_link,null);
        TextView btn_copier=view.findViewById(R.id.copie_lien);
        TextView btn_ouvrir=view.findViewById(R.id.ouvrir_lien);
       // ImageView img_btn_copier=view.findViewById(R.id.img_copie_lien);
        //ImageView img_btn_ouvrir=view.findViewById(R.id.img_ouvrir_lien);
        TextView btn_fermer=view.findViewById(R.id.btn_annuler);
        //ImageView btn_img_partger=view.findViewById(R.id.img_partager_lien);
        TextView btn_partager=view.findViewById(R.id.partager_lien);

        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);


        btn_copier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setText(liste_url_liens.get(position));
                Toast.makeText(LinksActivity.this, "Lien copier dans presse papier", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        btn_ouvrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browerIntent=new Intent("android.intent.action.VIEW", Uri.parse(liste_url_liens.get(position)));
                startActivity(browerIntent);
                alertDialog.dismiss();
            }
        });


        btn_partager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,liste_url_liens.get(position));
                startActivity(intent);
                alertDialog.dismiss();
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