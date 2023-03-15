package com.sintel.webcontentanalyzer;

import com.sintel.webcontentanalyzer.NaiveBayes;
import com.sintel.webcontentanalyzer.NaiveBayesKnowledgeBase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassifierActivity extends AppCompatActivity {
    String titre,texte,adresse,algorithm;
    TextView url, titre_page,methode;
    RadioGroup radioGroup;
    RadioButton economie, education, politique, religion,sante,sport,technologie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);
        url=findViewById(R.id.adresse_page);
        titre_page=findViewById(R.id.page_title);
        methode=findViewById(R.id.methode);
        radioGroup=findViewById(R.id.categorie);
        economie=findViewById(R.id.economie);
        education=findViewById(R.id.education);
        religion=findViewById(R.id.religion);
        politique=findViewById(R.id.politique);
        sante=findViewById(R.id.sante);
        sport=findViewById(R.id.sport);
        technologie=findViewById(R.id.technologie);

        titre=(String) getIntent().getSerializableExtra("titre");
        texte=(String) getIntent().getSerializableExtra("texte");
        adresse=(String) getIntent().getSerializableExtra("url");
        algorithm=(String) getIntent().getSerializableExtra("algorithm");

        titre_page.setText(titre);
        url.setText(adresse);
        methode.setText(algorithm);

        if (algorithm.equalsIgnoreCase("Naive Bayes") && !texte.isEmpty()){
            try {
                //ClassificateurBayesienne(titre);
                ClassificateurBayesienne(texte);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
           // Toast.makeText(this, "Le texte à classer est vide!", Toast.LENGTH_SHORT).show();
            final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.custom_texte_vide,null);
            TextView btn_close = view.findViewById(R.id.b_close);

            alert.setView(view);
            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);

            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            alertDialog.show();
        }
    }
/*
    public static String[] readLines(URL url) throws IOException {
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
        List<String> lines;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            lines = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines.toArray(new String[lines.size()]);
    }

 */

    public static String[] readLines(String url) throws IOException {
        //BufferedReader fileReader =new BufferedReader(new FileReader(url));
        //BufferedReader fileReader = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
        //Reader fileReader = new InputStreamReader(url.openStream(), Charset.forName("UTF-8"));
        List<String> lines;
       // try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            lines = new ArrayList<>();
            String [] line;
            //while ((line = url.readLine()) != null) {
               //url = AnalyseActivity.Clean_texte(url);
               url = TextTokenizer.preprocess(url);
               line = url.split(" ");
                //lines.add(line);
            //}
        //}
        return line ;
        //return lines.toArray(new String[lines.size()]);
    }


    private void ClassificateurBayesienne(String text) throws IOException {
        /*
        //map of dataset files
        Map<String, URL> trainingFiles = new HashMap<>();
        trainingFiles.put("economie", NaiveBayesExample.class.getResource(String.valueOf(R.raw.economie)));
        trainingFiles.put("education", NaiveBayesExample.class.getResource(String.valueOf(R.id.education)));
        trainingFiles.put("politique", NaiveBayesExample.class.getResource(String.valueOf(R.id.politique)));
        trainingFiles.put("religion", NaiveBayesExample.class.getResource(String.valueOf(R.id.religion)));
        trainingFiles.put("sante", NaiveBayesExample.class.getResource(String.valueOf(R.id.sante)));
        trainingFiles.put("sport", NaiveBayesExample.class.getResource(String.valueOf(R.id.sport)));
        trainingFiles.put("technologie", NaiveBayesExample.class.getResource(String.valueOf(R.id.technologie)));

        //loading examples in memory
        Map<String, String[]> trainingExamples = new HashMap<>();
        for(Map.Entry<String, URL> entry : trainingFiles.entrySet()) {
            try {
                trainingExamples.put(entry.getKey(), readLines(entry.getValue()));
            }catch (NullPointerException e){
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }
        }

         */
        Map<String,String> train = new HashMap<>();
        train.put("sport",getResources().getString(R.string.sport));
        train.put("sport",getResources().getString(R.string.sport1));
        train.put("sport",getResources().getString(R.string.sport2));
        train.put("sport",getResources().getString(R.string.sport3));
        train.put("sante",getResources().getString(R.string.sante));
        train.put("sante",getResources().getString(R.string.sante1));
        train.put("sante",getResources().getString(R.string.sante2));
        train.put("sante",getResources().getString(R.string.sante3));
        train.put("religion",getResources().getString(R.string.religion));
        train.put("religion",getResources().getString(R.string.religion1));
        train.put("religion",getResources().getString(R.string.religion2));
        train.put("religion",getResources().getString(R.string.religion3));
        train.put("education",getResources().getString(R.string.education));
        train.put("education",getResources().getString(R.string.education1));
        train.put("education",getResources().getString(R.string.education2));
        train.put("education",getResources().getString(R.string.education3));
        train.put("economie",getResources().getString(R.string.economie));
        train.put("economie",getResources().getString(R.string.economie1));
        train.put("economie",getResources().getString(R.string.economie2));
        train.put("economie",getResources().getString(R.string.economie3));
        train.put("technologie",getResources().getString(R.string.technologie));
        train.put("technologie",getResources().getString(R.string.techno1));
        train.put("technologie",getResources().getString(R.string.techno2));
        train.put("technologie",getResources().getString(R.string.techno3));
        train.put("politique",getResources().getString(R.string.politique));
        train.put("politique",getResources().getString(R.string.politique1));
        train.put("politique",getResources().getString(R.string.politique2));
        train.put("politique",getResources().getString(R.string.politique3));

        //loading examples in memory
        Map<String, String[]> trainingExamples = new HashMap<>();
        for(Map.Entry<String, String> entry : train.entrySet()) {
            try {
                trainingExamples.put(entry.getKey(), readLines(entry.getValue()));
            }catch (NullPointerException e){
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }

        }


        //train classifier
        NaiveBayes nb = new NaiveBayes();
        nb.setChisquareCriticalValue(6.63); //0.01 pvalue
        nb.train(trainingExamples);

        //get trained classifier knowledgeBase
        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();

        nb = null;
        trainingExamples = null;


        //Use classifier
        nb = new NaiveBayes(knowledgeBase);
        String output_class = nb.predict(text);

        switch (output_class) {
            case "religion": religion.setChecked(true);
            break;
            case "education": education.setChecked(true);
            break;
            case "sante": sante.setChecked(true);
            break;
            case "sport": sport.setChecked(true);
            break;
            case "technologie": technologie.setChecked(true);
            break;
            case "economie": economie.setChecked(true);
            break;
            case "politique": politique.setChecked(true);
            break;
            default:
                break;
        }
        //Toast.makeText(this, ""+output_class, Toast.LENGTH_LONG).show();


        /*
        String exampleEn = "I am English";
        String outputEn = nb.predict(exampleEn);
        System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleEn, outputEn);

        String exampleFr = "je t'aime bb lover";
        String outputFr = nb.predict(exampleFr);
        System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleFr, outputFr);

        String exampleDe = "Ich bin Deutsch";
        String outputDe = nb.predict(exampleDe);
        System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleDe, outputDe);
         */
    }
}