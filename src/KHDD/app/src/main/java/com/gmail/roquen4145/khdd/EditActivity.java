package com.gmail.roquen4145.khdd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class EditActivity extends AppCompatActivity {

    private static final int ALIGN_LEFT = 0;
    private static final int ALIGN_CENTER = 1;
    private static final int ALIGN_RIGHT = 2;

    private Integer currentPara;

    private Button btn_align_left;
    private Button btn_align_center;
    private Button btn_align_right ;
    private TextView tv_preview;
    private TextView tv_text;
    private Button btn_preview;
    private Button btn_save;
    private Button btn_next;

    private String currentText;

    private AnnotClass receivedAnnot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        btn_align_left = (Button) findViewById(R.id.LeftAlignButton);
        btn_align_center = (Button) findViewById(R.id.CenterAlignButton);
        btn_align_right = (Button) findViewById(R.id.RightAlignButton);
        tv_preview = (TextView) findViewById(R.id.ParagraphPreview);
        tv_text = (TextView) findViewById(R.id.ParagraphText);
        btn_preview = (Button) findViewById(R.id.PreviousButton);
        btn_save = (Button) findViewById(R.id.SaveButton);
        btn_next = (Button) findViewById(R.id.NextButton);


        Intent intent = getIntent();
        receivedAnnot = (AnnotClass) intent.getSerializableExtra("Annot");

        currentPara = 0;

        currentText = receivedAnnot.para_text.get(currentPara);

        tv_text.setText(currentText);

        btn_preview.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(currentPara == 0)
                {
                    Toast.makeText(getApplicationContext(),"첫번째 문단입니다.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String editedText = tv_text.getText().toString();
                    if(currentText.equals(editedText))
                    {
                        currentPara-=1;
                        tv_text.setText(receivedAnnot.para_text.get(currentPara));
                    }
                    else
                    {
                        receivedAnnot.para_text.set(currentPara,editedText);
                        currentPara-=1;
                        tv_text.setText(receivedAnnot.para_text.get(currentPara));
                    }

                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(currentPara == receivedAnnot.numPara - 1)
                {
                    Toast.makeText(getApplicationContext(),"마지막 문단입니다.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String editedText = tv_text.getText().toString();
                    if(currentText.equals(editedText))
                    {
                        currentPara+=1;
                        tv_text.setText(receivedAnnot.para_text.get(currentPara));
                    }
                    else
                    {
                        receivedAnnot.para_text.set(currentPara,editedText);
                        currentPara+=1;
                        tv_text.setText(receivedAnnot.para_text.get(currentPara));
                    }
                }
            }
        });

    }


}
