package com.sintel.webcontentanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class HTMLActivity extends AppCompatActivity {
EditText schema;
String schema_html;
ImageButton btn_copier, btn_pdf,btn_fermer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_t_m_l);

        schema_html = (String) getIntent().getSerializableExtra("SchemaHtml");

        schema=findViewById(R.id.schema_html);
        btn_copier=findViewById(R.id.btn_copie_schema);
        btn_pdf=findViewById(R.id.btn_pdf_schema);
        btn_fermer=findViewById(R.id.btn_fermer_schema);

        schema.setText(schema_html);
    }
}