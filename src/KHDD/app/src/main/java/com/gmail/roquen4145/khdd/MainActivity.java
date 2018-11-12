package com.gmail.roquen4145.khdd;


import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.vision.v1.model.CropHint;
import com.google.api.services.vision.v1.model.CropHintsAnnotation;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Block;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Page;
import com.google.api.services.vision.v1.model.Paragraph;
import com.google.api.services.vision.v1.model.Symbol;
import com.google.api.services.vision.v1.model.TextAnnotation;
import com.google.api.services.vision.v1.model.Word;

import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity  {
    private static final String CLOUD_VISION_API_KEY = BuildConfig.API_KEY;
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final int CROP_FROM_IMAGE = 4;
    private static int MAIN_MODE = 0;

    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;

    private Uri mImageCaptureUri;
    private String ImageFilePath;
    private TextView tv_Title;
    private ImageView iv_ToRead;
    private ImageView iv_Prep;
    private String absolutePath;
    private TextView tv_command;
    private TextView tv_ImageDescription;
    private Button btn_Copy;
    private Button btn_PDF;
    private String savePath;

    static AnnotClass savedAnnot;

    public native void process(long matAddrInput, long matAddrResult);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        tv_Title = (TextView)findViewById(R.id.titleText);
        iv_ToRead = (ImageView)findViewById(R.id.imgToRead);
        tv_command = (TextView)findViewById(R.id.commandText);
        tv_ImageDescription = (TextView)findViewById(R.id.ImageDescription);
        iv_Prep = (ImageView)findViewById(R.id.imgPrep);
        btn_Copy = (Button)findViewById(R.id.btn_copy);
        btn_PDF = (Button)findViewById(R.id.btn_pdf);


        savedAnnot = new AnnotClass();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {
                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                btn_Copy.setVisibility(View.VISIBLE);
                btn_PDF.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery,
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog,int whichButton){
                                        startGalleryChooser();;
                                    }
                                })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog,int whichButton){
                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });


        btn_Copy.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ClipboardManager cManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                String text = tv_ImageDescription.getText().toString();
                if(text.length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"복사할 내용이 없습니다.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ClipData clipData = ClipData.newPlainText("OCR",text);
                    cManager.setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(),"클립보드에 내용이 복사되었습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_PDF.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("Annot",savedAnnot);
                startActivity(intent);
            }
        });
    }

    public void startGalleryChooser(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent , GALLERY_IMAGE_REQUEST);
    };

    public void startCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager())!=null)
        {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(photoFile != null)
            {
                mImageCaptureUri = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("noFaceDetection",true); //
                intent.putExtra( "crop", "true" );
                intent.putExtra("scale", true);
                startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
            }
        }
    };

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        ImageFilePath = image.getAbsolutePath();
        return image;
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode != RESULT_OK)
            return;

        switch(requestCode)
        {
            case CROP_FROM_IMAGE:

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                iv_ToRead.setImageBitmap(bitmap);

                storeCropImage(bitmap);
                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }

                uploadImage(bitmap);

                break;
            case GALLERY_IMAGE_REQUEST:
            {
                mImageCaptureUri = data.getData();
                ImageFilePath = getRealPathFromURI(mImageCaptureUri);
            }
            case CAMERA_IMAGE_REQUEST:
            {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri,"image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra("return-data",true);
                intent.putExtra("output",mImageCaptureUri);
                startActivityForResult(intent,CROP_FROM_IMAGE);

                break;
            }
        }
    }



    private void storeCropImage(Bitmap bitmap)
    {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/KHDD";
        File directory_KHDD = new File(dirPath);

        if(!directory_KHDD.exists())
            directory_KHDD.mkdir();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        String imageFileName =  timeStamp + ".jpg";
        File copyFile = new File(dirPath + "/" + imageFileName);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile((copyFile))));

            out.flush();;
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*   Cloud Vision Code Starts Here     */
    public void uploadImage(Bitmap bitmap) {
        iv_ToRead.setImageBitmap(bitmap);
        callCloudVision(processImage(bitmap));

        savePath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/KHDD";
    }
    private Bitmap processImage(Bitmap orig_img)
    {
        matInput = new Mat();
        matResult = new Mat();

        Utils.bitmapToMat(orig_img,matInput);
        process(matInput.getNativeObjAddr(),matResult.getNativeObjAddr());

        Bitmap output_bitmap = Bitmap.createBitmap(orig_img.getWidth(),orig_img.getHeight(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matResult,output_bitmap);

        iv_Prep.setImageBitmap(output_bitmap);
        return output_bitmap;
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }


    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("DOCUMENT_TEXT_DETECTION");
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class DocumentTextDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;
        private String mSavePath;

        DocumentTextDetectionTask(MainActivity activity, Vision.Images.Annotate annotate, String savePath) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
            mSavePath = savePath;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                TextView imageDetail = activity.findViewById(R.id.ImageDescription);
                TextView command = activity.findViewById(R.id.commandText);
                command.setText("이미지 처리 내용");
                imageDetail.setText(result);
                imageDetail.scrollTo(0,0);
                imageDetail.setMovementMethod(new ScrollingMovementMethod());

            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        tv_command.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> documentTextDetectionTask = new DocumentTextDetectionTask(this, prepareAnnotationRequest(bitmap), savePath);
            documentTextDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder();
        TextAnnotation annotation = response.getResponses().get(0).getFullTextAnnotation();

        int page_width = 0;

        savedAnnot.numPara =0;
        savedAnnot.Paras.clear();

        for (Page page : annotation.getPages())
        {
            page_width = page.getWidth();
            String pageText = "";
            for (Block block : page.getBlocks())
            {
                String blockText = "";
                for (Paragraph para : block.getParagraphs())
                {
                    savedAnnot.numPara+=1;

                    String paraText = "";
                    for(Word word: para.getWords())
                    {
                        String wordText = "";
                        //message.append("Symbol text: ");
                        for( Symbol symbol : word.getSymbols())
                        {
                            wordText = wordText + symbol.getText();
                            //message.append( symbol.getText() +" ");
                        }
                        //message.append("\n");
                        //message.append("Word text : "+ wordText + "  \n\n");
                        paraText = paraText + " " + wordText;
                    }
                    //message.append("\nParagraph: \n" + paraText + "\n\n\n");
                    blockText = blockText + paraText;

                    ParaStruct temp = new ParaStruct();
                    temp.para_padding = 0;
                    temp.para_pos_x1 = para.getBoundingBox().getVertices().get(0).getX();
                    temp.para_pos_x2 = para.getBoundingBox().getVertices().get(2).getX();
                    temp.para_text = paraText;
                    temp.para_text_size = 10;
                    temp.para_width = page_width;

                    if ( Math.abs(temp.para_width/2 - (temp.para_pos_x1 + temp.para_pos_x2)/2) <= temp.para_width/20)
                        temp.para_align = 2;
                    else
                    {
                        if(temp.para_width/2 - (temp.para_pos_x1 + temp.para_pos_x2)/2 > 0)
                            temp.para_align = 1;
                        else
                            temp.para_align = 3;
                    }

                    savedAnnot.Paras.add(temp);

                }
                pageText = pageText + blockText;
            }
        }

        message.append("\n\n" + annotation.getText());
        return message.toString();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */


    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS  = {"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};


    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }


}
