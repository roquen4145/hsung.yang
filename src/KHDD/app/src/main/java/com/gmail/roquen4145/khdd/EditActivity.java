package com.gmail.roquen4145.khdd;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import static java.sql.Types.NULL;

public class EditActivity extends AppCompatActivity {

    private static final int ALIGN_LEFT = 1;
    private static final int ALIGN_CENTER = 2;
    private static final int ALIGN_RIGHT = 3;

    private Integer currentPara;

    private Button btn_align_left;
    private Button btn_align_center;
    private Button btn_align_right ;
    private TextView tv_preview;
    private EditText tv_text;
    private Button btn_preview;
    private Button btn_save;
    private Button btn_delete;
    private Button btn_next;

    private String currentText;

    private AnnotClass receivedAnnot;
    private ParaStruct currentParaStruct;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        btn_align_left = (Button) findViewById(R.id.LeftAlignButton);
        btn_align_center = (Button) findViewById(R.id.CenterAlignButton);
        btn_align_right = (Button) findViewById(R.id.RightAlignButton);
        tv_preview = (TextView) findViewById(R.id.ParagraphPreview);
        tv_text = (EditText) findViewById(R.id.ParagraphText);
        btn_preview = (Button) findViewById(R.id.PreviousButton);
        btn_save = (Button) findViewById(R.id.SaveButton);
        btn_delete = (Button) findViewById(R.id.DeleteButton);
        btn_next = (Button) findViewById(R.id.NextButton);


        Intent intent = getIntent();
        receivedAnnot = (AnnotClass) intent.getSerializableExtra("Annot");

        ParaInit();

        btn_align_left.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               tv_text.setGravity(Gravity.START);
               currentParaStruct.para_align = 1;
               receivedAnnot.Paras.set(currentPara,currentParaStruct);
            }
        });

        btn_align_center.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                tv_text.setGravity(Gravity.CENTER_HORIZONTAL);
                currentParaStruct.para_align = 2;
                receivedAnnot.Paras.set(currentPara,currentParaStruct);
            }
        });

        btn_align_right.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                tv_text.setGravity(Gravity.END);
                currentParaStruct.para_align = 3;
                receivedAnnot.Paras.set(currentPara,currentParaStruct);
            }
        });


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
                        ParaRefresh();
                    }
                    else
                    {
                        currentParaStruct.para_text = editedText;
                        receivedAnnot.Paras.set(currentPara,currentParaStruct);
                        currentPara-=1;
                        ParaRefresh();
                    }

                }

                ParaRefresh();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Document document = new Document(PageSize.A4, 50, 50, 30, 40);
                String Dirname = Environment.getExternalStorageDirectory().getAbsolutePath()+"/KHDD";
                String filename = "TempPDFFile";
                String fullname = Dirname + "/" +filename + ".pdf";
                File pdfFile = new File(fullname);

                try {
                    PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                document.open();

                for(int i=0;i<receivedAnnot.numPara;i++)
                {
                    ParaStruct temp = receivedAnnot.Paras.get(i);
                    Font myFont = getFont(temp.para_text_size);
                    Paragraph paragraph = new Paragraph(temp.para_text , myFont);
                    switch(temp.para_align)
                    {
                        case ALIGN_LEFT:
                            paragraph.setAlignment(Element.ALIGN_LEFT);
                            break;
                        case ALIGN_CENTER:
                            paragraph.setAlignment(Element.ALIGN_CENTER);
                            break;
                        case ALIGN_RIGHT:
                            paragraph.setAlignment(Element.ALIGN_RIGHT);
                            break;
                    }

                    paragraph.setIndentationLeft(temp.para_padding);

                    try {
                        document.add(paragraph);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }

                document.close();

                String msg = "문서를 " + filename + ".pdf로 저장하였습니다.";
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile((pdfFile))));
                finish();
            }
        });


        btn_delete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(tv_text.getText().length() == 0)
                {
                    receivedAnnot.numPara-=1;
                    receivedAnnot.Paras.remove(currentPara.intValue());

                    if(currentPara > 0)
                        currentPara-=1;

                    if(receivedAnnot.numPara == 0)
                    {
                        Toast.makeText(getApplicationContext(),"남아있는 문단이 없습니다", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    ParaRefresh();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"비어있지 않은 문단은 삭제 할 수 없습니다.",Toast.LENGTH_SHORT).show();
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
                        ParaRefresh();
                    }
                    else
                    {
                        currentParaStruct.para_text = editedText;
                        receivedAnnot.Paras.set(currentPara,currentParaStruct);
                        currentPara+=1;
                        ParaRefresh();
                    }
                }
            }
        });

    }

    protected void ParaInit()
    {
        currentPara = 0;
        ParaRefresh();
    }

    protected void SetParaAlign()
    {
        switch( receivedAnnot.Paras.get(currentPara).para_align )
        {
            case NULL:
                tv_text.setGravity(Gravity.START);
                break;
            case 1:
                tv_text.setGravity(Gravity.START);
                break;
            case 2:
                tv_text.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            case 3:
                tv_text.setGravity(Gravity.END);
                break;
        }
    }

    protected void ParaRefresh()
    {
        if(tv_text.getText().length()== 0)
        {
            btn_delete.setClickable(true);
        }
        else if (currentPara == receivedAnnot.numPara -1)
        {
            btn_save.setVisibility(View.VISIBLE);
            btn_save.setClickable(true);
            btn_delete.setVisibility(View.GONE);
            btn_delete.setClickable(false);
        }
        else
        {
            btn_save.setVisibility(View.GONE);
            btn_save.setClickable(false);
            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setClickable(true);
        }

        currentParaStruct = receivedAnnot.Paras.get(currentPara);
        currentText = currentParaStruct.para_text;
        tv_text.setText(currentText);
        SetParaAlign();

    }



    Font getFont(int fontSize)
    {
        BaseFont baseFont = null;
        try {
            InputStream is = getResources().getAssets().open("malgun.ttf");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            baseFont = BaseFont.createFont("malgun.ttf",BaseFont.IDENTITY_H,true,false,buffer,null);
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (DocumentException e)
        {
            e.printStackTrace();
        }

        Font font =  new Font(baseFont , fontSize);

        return font;
    }

}

