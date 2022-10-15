package com.code10.libsys.User.Activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);

        String dirtype = getIntent().getStringExtra("EXFILEDIR");
        String position = getIntent().getStringExtra("POSITION");
        File x = getApplicationContext().getExternalFilesDir(dirtype);
        File[] files = x.listFiles();
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);

        pdfView.fromFile(files[Integer.parseInt(position)]).load();
        ImageView back = findViewById(R.id.imageArrowleft);
        back.setOnClickListener(v -> finish());
    }
}