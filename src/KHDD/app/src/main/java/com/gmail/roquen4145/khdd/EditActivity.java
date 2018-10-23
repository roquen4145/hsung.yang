package com.gmail.roquen4145.khdd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class EditActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Button btn_align_left = (Button) findViewById(R.id.LeftAlignButton);
        Button btn_align_center = (Button) findViewById(R.id.CenterAlignButton);
        Button btn_align_right = (Button) findViewById(R.id.RightAlignButton);
        TextView tv_preview = (TextView) findViewById(R.id.ParagraphPreview);
        TextView tv_text = (TextView) findViewById(R.id.ParagraphText);
        Button btn_preview = (Button) findViewById(R.id.PreviousButton);
        Button btn_save = (Button) findViewById(R.id.SaveButton);
        Button btn_next = (Button) findViewById(R.id.NextButton);


        Intent intent = getIntent();
        AnnotClass receivedAnnot = (AnnotClass) intent.getSerializableExtra("Annot");

        tv_text.setText(receivedAnnot.para_text.get(0));

    }


}
