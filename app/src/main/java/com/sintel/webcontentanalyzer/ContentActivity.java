package com.sintel.webcontentanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.Result;
import com.detectlanguage.errors.APIError;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class ContentActivity extends AppCompatActivity {

    String contenu,titre,url;
    EditText zone_affichage_contenu, title;
    ImageButton btn_copier, btn_pdf, btn_fermer, btn_share;
    Button btn_analyse,btn_class,btn_clean;
    String algorithm="null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        zone_affichage_contenu=findViewById(R.id.contenu);
        title=findViewById(R.id.titre);
        btn_copier=findViewById(R.id.btn_copie);
        btn_pdf=findViewById(R.id.btn_pdf);
        btn_fermer=findViewById(R.id.btn_fermer);
        btn_share=findViewById(R.id.btn_share);
        btn_analyse=findViewById(R.id.btn_analyse);
        btn_class=findViewById(R.id.btn_class);
        btn_clean=findViewById(R.id.btn_clean);

         contenu = (String) getIntent().getSerializableExtra("contenu");
         titre = (String) getIntent().getSerializableExtra("titre");
         url = (String) getIntent().getSerializableExtra("url");

        zone_affichage_contenu.setText(contenu);
        title.setText(titre);

        btn_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TraitementTexte();
            }
        });

        btn_class.setOnClickListener(new View.OnClickListener() {
             String  language;
            @Override
            public void onClick(View v) {
                /*
                List<Result> results = null;
                try {
                    results = DetectLanguage.detect(contenu);
                } catch (APIError apiError) {
                    apiError.printStackTrace();
                }
                Result result =results.get(0);
                language = result.language;
                Toast.makeText(ContentActivity.this, ""+language, Toast.LENGTH_SHORT).show();
                 */
                ChooseAlgorithm();
            }
        });

        btn_analyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analyse();
            }
        });

        btn_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //createPdf(contenu);
                Save_as_PDF();
            }
        });

        btn_copier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texte_à_copier = zone_affichage_contenu.getText().toString();
                if (texte_à_copier.isEmpty()){
                    Toast.makeText(ContentActivity.this, "Le texte à copier est vide !", Toast.LENGTH_LONG).show();
                } else {
                    copier(texte_à_copier);
                }
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texte_à_partager = zone_affichage_contenu.getText().toString();
                String titre_texte = title.getText().toString();

                if (texte_à_partager.isEmpty()){
                    Toast.makeText(ContentActivity.this, "Le texte à partager est vide !", Toast.LENGTH_LONG).show();
                } else {
                    share(titre_texte+"\n \n"+texte_à_partager);
                }
            }
        });

        btn_fermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void TraitementTexte() {
        String text_traitement=null;
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.activity_pretraitement,null);
        final EditText editText=view.findViewById(R.id.text_contenu);
        Button btn_close = view.findViewById(R.id.btn_close);
        Button btn_copy = view.findViewById(R.id.btn_copy);

        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        text_traitement=zone_affichage_contenu.getText().toString();
        editText.setText(AnalyseActivity.Clean_texte(text_traitement));
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copier(editText.getText().toString());
            }
        });

        alertDialog.show();
    }

    private void ChooseAlgorithm() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_class,null);
        RadioGroup radioGroup=view.findViewById(R.id.radiogroup);
        final RadioButton btn_knn=view.findViewById(R.id.knn);
        final RadioButton btn_arbre=view.findViewById(R.id.arbre);
        final RadioButton btn_bayes=view.findViewById(R.id.bayes);
        Button btn_confirm = view.findViewById(R.id.btn_confirm);
        Button btn_reset = view.findViewById(R.id.btn_reset);

        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btn_knn.isChecked()){
                    algorithm="KNN";
                } else if(btn_arbre.isChecked()){
                    algorithm="Arbre de décision";
                } else if (btn_bayes.isChecked()){
                    algorithm="Naive Bayes";
                }

                if(algorithm.equals("null")){
                    Toast.makeText(ContentActivity.this, "Choisir un algorithme de classification", Toast.LENGTH_SHORT).show();
                }
                else if (algorithm.equalsIgnoreCase("knn")) {
                    Non_disponible();
                }
                else if (algorithm.equalsIgnoreCase("Arbre de décision")) {
                    Non_disponible();
                }
                else {
                    Intent intent = new Intent(ContentActivity.this, ClassifierActivity.class);
                    intent.putExtra("titre", titre);
                    intent.putExtra("texte", zone_affichage_contenu.getText().toString());
                    intent.putExtra("url", url);
                    intent.putExtra("algorithm",algorithm);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    alertDialog.dismiss();
                }
                algorithm="null";
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (btn_arbre.isChecked()){
                  btn_arbre.setChecked(false);
                  algorithm="null";
              } else if (btn_bayes.isChecked()){
                  btn_bayes.setChecked(false);
                  algorithm="null";
              }else if (btn_knn.isChecked()){
                  btn_knn.setChecked(false);
                  algorithm="null";
              }
            }
        });

        alertDialog.show();
    }

    private void Non_disponible() {
        AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(ContentActivity.this);
        alertDialogBuilder.setTitle("Pas encore disponible !");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_help_outline_24);
        alertDialogBuilder.setMessage("Bien vouloir choisir la méthode Naive Bayes!");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog= alertDialogBuilder.create();
        alertDialog.show();
    }

    private void Analyse() {
        Intent intent = new Intent(this,AnalyseActivity.class);
        intent.putExtra("titre",title.getText().toString());
        intent.putExtra("texte",zone_affichage_contenu.getText().toString());
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private void share(String s) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,s);
        startActivity(intent);
    }

    private void copier(String contenu) {
        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setText(contenu);
        Toast.makeText(ContentActivity.this, "Texte copier dans presse papier", Toast.LENGTH_SHORT).show();
    }

    private void Enregistrer_pdf(){
        try {
            Document document = new Document(PageSize.A4,36,72,108,180);
            PdfWriter.getInstance(document, new FileOutputStream("pdfFile.pdf"));
            document.open();
            document.addTitle(titre);
            document.add(new Paragraph(contenu));
            Toast.makeText(this, "Enregistré avec succes!", Toast.LENGTH_SHORT).show();
            document.close();
        }catch (Exception e){
            Toast.makeText(this, "Echec", Toast.LENGTH_SHORT).show();
        }
    }
    private void Save_as_PDF() {
        try {
            String path = Environment.getExternalStorageDirectory()+"/Web Content Analyzer";
            File file = new File(path+titre+".pdf");
            if (!file.exists()){
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e){
                }
            }

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory()
                    +File.separator
                    +"Web Content Analyzer"
                    +File.separator
                    +titre+".pdf"));
            document.open();
            document.addTitle(titre);
            document.add(new Paragraph(contenu));
            document.close();
            //Log.d("OK","done!");
            Toast.makeText(this, "Enregistré avec succes!", Toast.LENGTH_SHORT).show();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    // Method for creating a pdf file from text, saving it then opening it for display
    public void createPdf(String text) {

        Document doc = new Document();
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";
            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

            File file = new File(dir, "newFile.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Paragraph p1 = new Paragraph(text);
            Font paraFont= new Font(Font.FontFamily.COURIER);
            p1.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally {
            doc.close();
        }

        viewPdf("newFile.pdf", "Dir");
    }
    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ContentActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }



}