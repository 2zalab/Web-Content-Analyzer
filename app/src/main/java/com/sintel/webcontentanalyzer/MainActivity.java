package com.sintel.webcontentanalyzer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends BaseActivity {

    String url,contenu, titre, title, content, body,urlLink,imgSrc;
    ImageButton btn_charger;
    EditText adresse_url;
    ProgressDialog progressDialog;
    Elements links, images;
    Bitmap bitmap;
    public ArrayList<String> liste_liens,liste_url_liens, lien_images,liens_historiques;
    public ArrayList<Bitmap> liste_images;
    ArrayList<Date> historique_dates;
    ImageView  btn_help;
    Date date;
    SharedPreferences sharedPreferences;
    Set<String> historique = new HashSet<>();
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        //setSupportActionBar(bottomNavigationView);

        bottomNavigationView = findViewById(R.id.botomBar);

        liste_liens= new ArrayList<String>();
        liste_url_liens= new ArrayList<String>();
        liste_images = new ArrayList<Bitmap>();
        lien_images = new ArrayList<String>();
        liens_historiques= new ArrayList<String>();
        historique_dates=new ArrayList<Date>();

        body="";
        contenu="";
        links = null;

        adresse_url=findViewById(R.id.url);
        btn_charger=findViewById(R.id.btn_charger);
        btn_help=findViewById(R.id.help);

        sharedPreferences = getSharedPreferences("com.sintel.webcontentanalyzer", MODE_PRIVATE);
        historique = sharedPreferences.getStringSet("historique", new HashSet<String>());

        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Help_Activity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_in);
            }
        });

        btn_charger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!IsConnected(getApplicationContext())){
                    MessageNoConnection();
                    //Toast.makeText(MainActivity.this, "Aucune Connexion internet!", Toast.LENGTH_SHORT).show();
                } else {
                    if(adresse_url.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Saisir l'adresse URL s'il vous plait ", Toast.LENGTH_LONG).show();
                   } else {
                        //recuperation de l'adresse URL du site saisie par l'utilisateur et sauvegarde de l'historique (date et lien)
                        date=new Date();
                        url=adresse_url.getText().toString();
                        liens_historiques.add(url);
                        historique_dates.add(date);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String json_lien=gson.toJson(liens_historiques);
                        String json_dates =gson.toJson(historique_dates);
                        editor.putString("liens",json_lien);
                        editor.putString("dates",json_dates);
                        editor.apply();

                   if (IsUrl(url)){
                           new Content().execute();

                   } else {
                       //Toast.makeText(MainActivity.this, "URL invalide !", Toast.LENGTH_LONG).show();
                       AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(MainActivity.this);
                       alertDialogBuilder.setTitle("URL Invalide !");
                       alertDialogBuilder.setIcon(R.drawable.ic_baseline_warning_24);
                       alertDialogBuilder.setMessage("Saisir une adresse URL Valide");
                       alertDialogBuilder.setCancelable(false);
                       alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                           }
                       });
                       AlertDialog alertDialog= alertDialogBuilder.create();
                       alertDialog.show();
                   }

                }
             }
            }
        });
    }

    private void MessageNoConnection() {
        final AlertDialog.Builder alert = new AlertDialog.Builder (this);
        View view = getLayoutInflater().inflate(R.layout.custom_no_internet,null);
        TextView btn_fermer=view.findViewById(R.id.btn_fermer_cnx);

        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_fermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private boolean IsConnected(Context applicationContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null){
            return false;
        } else
        return true;
    }

    private boolean IsUrl(String url) {
        try {
            URL tempUrl= new URL(url);
            return URLUtil.isValidUrl(String.valueOf(tempUrl));// && Patterns.WEB_URL.matcher(url).matches();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void AfficherHistorique(View view) {
        Intent intent = new Intent(MainActivity.this, HistoriqueActivity.class);
        Gson gson=new Gson();
        String json_liens= sharedPreferences.getString("liens","");
        String json_dates= sharedPreferences.getString("dates","");

        Type type_lien = new TypeToken<List<String>>(){}.getType();
        Type type_date = new TypeToken<List<Date>>(){}.getType();
        ArrayList<String> arrayLists_liens=gson.fromJson(json_liens,type_lien);
        ArrayList<Date> arrayLists_dates=gson.fromJson(json_dates,type_date);

        intent.putExtra("liens", arrayLists_liens);
        intent.putExtra("date", arrayLists_dates);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    public void Partager(View view){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        String app_url = "Télécharger l'application Web Content Analyzer en cliquant sur le lien : https://play.google.com/store/apps/details?id=com.sintel.webcontentanalyzer";
        shareIntent.putExtra(Intent.EXTRA_TEXT, app_url);
        startActivity(Intent.createChooser(shareIntent, "Partager via"));
    }

    private class  Content extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Analyse de l'URL en cours...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //connexion au site
            Document document = null;

            try {
                document = Jsoup.connect(url).get();
                // recuperation des elements images
                images = document.select("img[src$=.png]");
                 //recuperation des liens
                links =document.select("a");
                // recuperation du titre du site
                title = document.title();
                //recuperation du contenu html
                content=document.html();
                //body = document.getElementsByTag("body").text();
                //body = document.body().text();
                body = Jsoup.clean(document.body().text(), Whitelist.simpleText());
                //Get the logo source of the website

                Element img = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]").first();

                for (Element image : images){
                    // Locate the src attribute
                    imgSrc = image.absUrl("src");
                    // Download image from URL
                    InputStream input = new java.net.URL(imgSrc).openStream();
                    // Decode Bitmap
                    bitmap = BitmapFactory.decodeStream(input);
                    //liste_images.add(bitmap);
                    liste_images.add(
                            Picasso.with(getApplicationContext())
                                    .load(imgSrc)
                                    .resize(300,50)
                                    .centerInside()
                                    .onlyScaleDown()
                                    .get());
                    lien_images.add(imgSrc);

                }

                for (Element element : links){
                        urlLink = element.absUrl("href");
                        if (!element.text().isEmpty() && !urlLink.isEmpty()) {
                            liste_liens.add(element.text());
                            liste_url_liens.add(urlLink);
                        }
                    }

            } catch (IOException e) {
                e.printStackTrace();
            }
           return null;
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            titre = title;
            contenu=content;
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Terminé", Toast.LENGTH_SHORT).show();
        }
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_bottom_bar, menu);
            return true;
        }
     */

    public void view_contenu(View view) {
        if(adresse_url.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Saisir l'adresse URL s'il vous plait ", Toast.LENGTH_SHORT).show();
        } else if (body.isEmpty()){
            Toast.makeText(this, "Le contenu est vide, Vérifier l'adresse URL du site puis cliquer sur le bouton actualiser ", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(MainActivity.this, ContentActivity.class);
            intent.putExtra("contenu", body);
            intent.putExtra("titre", title);
            intent.putExtra("url",url);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    }

    public void ViewImages(View view) {
        // Toast.makeText(this, ""+liste_images, Toast.LENGTH_SHORT).show();
        if(adresse_url.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Saisir l'adresse URL s'il vous plait ", Toast.LENGTH_SHORT).show();
        } else if (liste_images.isEmpty()){
            Toast.makeText(this, "Cette page web ne contient aucune image. Vérifier l'adresse URL du site puis cliquer sur le bouton actualiser.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, ImagesActivity.class);
            intent.putExtra("liste_images", liste_images);
            intent.putExtra("liste_lien_images", lien_images);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    }

    public void ViewLinks(View view) {
        if(adresse_url.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Saisir l'adresse URL s'il vous plait ", Toast.LENGTH_SHORT).show();
        } else if (liste_url_liens.isEmpty()){
            Toast.makeText(this, "Cette page web ne contient aucun lien.Vérifier l'adresse URL du site puis cliquer sur le bouton actualiser.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, LinksActivity.class);
            intent.putExtra("links", liste_liens);
            intent.putExtra("url_links", liste_url_liens);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            //Toast.makeText(this, ""+liste_liens, Toast.LENGTH_LONG).show();
        }
    }

    public void ViewSchemaHTML(View view) {
        if(adresse_url.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Saisir l'adresse URL s'il vous plait ", Toast.LENGTH_SHORT).show();
        } else if (contenu.isEmpty()){
            Toast.makeText(this, "Le Schema HTML est vide, Vérifier l'adresse URL du site puis cliquer sur le bouton actualiser ", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, HTMLActivity.class);
            intent.putExtra("SchemaHtml", contenu);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    }
}