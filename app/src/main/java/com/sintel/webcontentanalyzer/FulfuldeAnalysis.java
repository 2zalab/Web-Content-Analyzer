package com.sintel.webcontentanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FulfuldeAnalysis extends AppCompatActivity {

    EditText text;
    Button button;
    String texte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulfulde);

        text = findViewById(R.id.texte_fulfulde);
        button = findViewById(R.id.btn_analysis);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                texte = text.getText().toString();
                if (texte.isEmpty()) {
                    Toast.makeText(FulfuldeAnalysis.this, "Bien voulir saisir le texte svp!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Classifier(texte);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void Classifier(String texte) throws IOException {

        Map<String, String> train = new HashMap<>();
        train.put("positif", getResources().getString(R.string.positif));
        train.put("negatif", getResources().getString(R.string.negatif));


        //loading examples in memory
        Map<String, String[]> trainingExamples = new HashMap<>();
        for (Map.Entry<String, String> entry : train.entrySet()) {
            try {
                trainingExamples.put(entry.getKey(), ClassifierActivity.readLines(entry.getValue()));
            } catch (NullPointerException e) {
                Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
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
        String output_class = nb.predict(texte);

        //Toast.makeText(this, ""+output_class, Toast.LENGTH_SHORT).show();

        switch (output_class) {
            case "positif": AffichageResultsAnalysis("Weli",R.drawable.ic_baseline_sentiment_satisfied_alt_24); ;
                break;
            case "negatif": AffichageResultsAnalysis("bonnɗo",R.drawable.ic_baseline_sentiment_very_dissatisfied_24);
                break;
            default:
                break;
        }

    }

    public void AffichageResultsAnalysis(String texte,int i){
        AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(FulfuldeAnalysis.this);
        alertDialogBuilder.setTitle(texte);
        alertDialogBuilder.setIcon(i);
        //alertDialogBuilder.setMessage("Voulez-vous vraiment quitter?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog= alertDialogBuilder.create();
        alertDialog.show();
    }
}