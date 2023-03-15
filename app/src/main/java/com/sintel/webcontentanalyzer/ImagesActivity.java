package com.sintel.webcontentanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class ImagesActivity extends AppCompatActivity {
    GridView gridView;
    Bitmap bitmap;
    ArrayList<Bitmap> liste_images;
    ArrayList<String> lien_images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        liste_images = new ArrayList<Bitmap>();
        lien_images = new ArrayList<String>();
        liste_images = null;

        liste_images = getIntent().getParcelableArrayListExtra("liste_images");
        lien_images = getIntent().getStringArrayListExtra("liste_lien_images");

        gridView = findViewById(R.id.grid_view);

        gridView.setAdapter(new ImageAdapterGridView(this,liste_images,lien_images));

    }

    private class ImageAdapterGridView extends BaseAdapter {
         //private Context mcontext;
        private ArrayList<String> liens_images;
        private  ArrayList<Bitmap> list_images;
         private Activity activity;

        public ImageAdapterGridView(Activity activity, ArrayList<Bitmap> list_images, ArrayList<String> liens_images) {
            //this.mcontext = context;
            super();
            this.liens_images=liens_images;
            this.list_images=list_images;
            this.activity=activity;
        }

        @Override
        public int getCount() {
            return list_images.size();
        }

        @Override
        public Object getItem(int position) {
            return list_images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class  ViewHolder{
            public  ImageView imageView;
            public  TextView textView;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //ImageView imageView;
            ViewHolder view;
            LayoutInflater inflater =activity.getLayoutInflater();

            if (convertView == null) {
                view= new ViewHolder();
                convertView=inflater.inflate(R.layout.custom_grid,null);

                //view.textView = convertView.findViewById(R.id.nom_image);
                view.imageView=convertView.findViewById(R.id.custom_image);

                convertView.setTag(view);

                //imageView = new ImageView(mcontext);
                //view.imageView.setLayoutParams(new GridView.LayoutParams(100, 90));
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //imageView.setPadding(16, 16, 16, 16);
            } else {
                view =(ViewHolder) convertView.getTag();
                //imageView = (ImageView) convertView;
            }
            view.imageView.setImageBitmap(liste_images.get(position));
           // view.textView.setText(lien_images.get(position));
            //imageView.setImageBitmap(liste_images.get(position));

            view.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    option(position);
                }
            });

            return convertView;
        }
    }

    private void option(final int position) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_images, null);
        TextView btn_copier_image = view.findViewById(R.id.copie_lien_image);
        TextView btn_enregistrer = view.findViewById(R.id.telecharger_image);
        TextView btn_partager = view.findViewById(R.id.partager_lien_image);
        TextView btn_fermer = view.findViewById(R.id.btn_fermer_image);

        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_fermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_copier_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setText(lien_images.get(position));
                Toast.makeText(ImagesActivity.this, "Lien copier dans presse papier", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        btn_partager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, lien_images.get(position));
                startActivity(intent);
                alertDialog.dismiss();
            }
        });


        btn_enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImageNew("Img"+position, lien_images.get(position));
            }
        });

        alertDialog.show();
    }

    private void downloadImageNew(String filename, String downloadUrlOfImage) {
        try {
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/png") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".png");
            dm.enqueue(request);
            Toast.makeText(this, "Téléchargement commencé", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Echec", Toast.LENGTH_SHORT).show();
        }
    }
}