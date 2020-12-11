//package com.example.opencv;
//

//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.Matrix;
//import android.media.ExifInterface;
//import android.net.Uri;
//import android.os.Build;
//import android.provider.MediaStore;
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import org.opencv.android.Utils;
//import org.opencv.core.Mat;
//
//import java.io.IOException;
//
//import androidx.activity.OnBackPressedCallback;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.SpannableString;
//import android.text.method.LinkMovementMethod;
//import android.text.method.ScrollingMovementMethod;
//import android.text.style.ClickableSpan;
//import android.view.View;
//import android.widget.TextView;
//
//public class CameraActivity extends AppCompatActivity {
//
//    static {
//        System.loadLibrary("opencv_java4");
//        System.loadLibrary("native-lib");
//    }
//
//    ImageView imageVIewInput;
//    ImageView imageVIewOuput;
//    private Mat img_input;
//    private Mat img_output;
//    private int threshold1 = 50;
//    private int threshold2 = 150;
//
////TODO:
////    private Bitmap bitmapInput;
////    private Bitmap bitmapOutput = null;
//
//    private static final String TAG = "opencv";
//    private final int GET_GALLERY_IMAGE = 200;
//
//    boolean isReady = false;
//
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_img_upload);
//
//        imageVIewInput = (ImageView) findViewById(R.id.imageViewInput);
//        imageVIewOuput = (ImageView) findViewById(R.id.imageViewOutput);
//
//        Button Button = (Button) findViewById(R.id.button);
//        Button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                imageprocess_and_showResult(threshold1, threshold2);
//            }
//        });
//
//        // Let user select image from their local gallery
//        imageVIewInput.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                startActivityForResult(intent, GET_GALLERY_IMAGE);
//            }
//        });
//
//        // Set up seek bar to adjust threshold of canny function
//        final TextView textView1 = (TextView) findViewById(R.id.textView_threshold1);
//        SeekBar seekBar1 = (SeekBar) findViewById(R.id.seekBar_threshold1);
//        seekBar1.setProgress(threshold1);
//        seekBar1.setMax(200);
//        seekBar1.setMin(0);
//        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                threshold1 = progress;
//                textView1.setText(threshold1 + "");
//                imageprocess_and_showResult(threshold1, threshold2);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//
//
//        final TextView textView2 = (TextView) findViewById(R.id.textView_threshold2);
//        SeekBar seekBar2 = (SeekBar) findViewById(R.id.seekBar_threshold2);
//        seekBar2.setProgress(threshold2);
//        seekBar2.setMax(200);
//        seekBar2.setMin(0);
//        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                threshold2 = progress;
//                textView2.setText(threshold2 + "");
//                imageprocess_and_showResult(threshold1, threshold2);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//
//        // check if the user was asked for permission
//        if (!hasPermissions(PERMISSIONS)) {
//            requestNecessaryPermissions(PERMISSIONS);
//        }
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        isReady = true;
//    }
//
//    public static Intent makeIntent(Context context) {
//        Intent intent = new Intent(context, CameraActivity.class);
//        return intent;
//    }
//
//    //Declare and call functions in the openCV library
//    public native void nBitmapToMat(Bitmap bitmap, long input, boolean needUnPremultiplyAlpha);
//    public native void detectEdge(long inputImage, long outputImage, int th1, int th2);
//    public native void nMatToBitmap (long src, Bitmap bitmap, boolean needPremultiplyAlpha);
//
//    // Create Mat object of img_output to store output
//    private void imageprocess_and_showResult(int th1, int th2) {
//
//        if (isReady == false) return;
//
//        if (img_output == null)
//            img_output = new Mat();
//
//        // Send args to the function in the native-lib.cpp file
//        detectEdge(img_input.getNativeObjAddr(), img_output.getNativeObjAddr(), th1, th2);
//
//        // Show the result to the user
//        Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
//        nMatToBitmap(img_output.getNativeObjAddr(), bitmapOutput, false);
////        Utils.matToBitmap(img_output, bitmapOutput);
//        imageVIewOuput.setImageBitmap(bitmapOutput);
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == GET_GALLERY_IMAGE) {
//
//            if (data.getData() != null) {
//                Uri uri = data.getData();
//                try {
//                    String path = getRealPathFromURI(uri);
//                    int orientation = getOrientationOfImage(path);
//                    Bitmap temp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                    Bitmap bitmap = getRotatedBitmap(temp, orientation);
//                    imageVIewInput.setImageBitmap(bitmap);
//
//                    // Convert the image from user gallery to Mat object
//                    img_input = new Mat();
//                    Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//                    nBitmapToMat(bmp32, img_input.getNativeObjAddr(), false);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private String getRealPathFromURI(Uri contentUri) {
//
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
//        cursor.moveToFirst();
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//
//        return cursor.getString(column_index);
//    }
//
//    // Code based on [http://snowdeer.github.io/android/2016/02/02/android-image-rotation/]
//    public int getOrientationOfImage(String filepath) {
//        ExifInterface exif = null;
//
//        try {
//            exif = new ExifInterface(filepath);
//        } catch (IOException e) {
//            Log.d("@@@", e.toString());
//            return -1;
//        }
//
//        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
//
//        if (orientation != -1) {
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    return 90;
//
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    return 180;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    return 270;
//            }
//        }
//
//        return 0;
//    }
//
//    public Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) throws Exception {
//        if (bitmap == null) return null;
//        if (degrees == 0) return bitmap;
//
//        Matrix m = new Matrix();
//        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
//
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
//    }
//
//
//    // Check and ask permission of user to access external storage
//    static final int PERMISSION_REQUEST_CODE = 1;
//    String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE"};
//
//    private boolean hasPermissions(String[] permissions) {
//        int ret = 0;
//        for (String perms : permissions) {
//            ret = checkCallingOrSelfPermission(perms);
//            if (!(ret == PackageManager.PERMISSION_GRANTED)) {
//                return false;
//            }
//
//        }
//        return true;
//    }
//
//    private void requestNecessaryPermissions(String[] permissions) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (permsRequestCode) {
//
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0) {
//                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                        if (!writeAccepted) {
//                            showDialogforPermission("Permission is required to run the App.");
//                            return;
//                        }
//                    }
//                }
//                break;
//        }
//    }
//
//    private void showDialogforPermission(String msg) {
//
//        final AlertDialog.Builder myDialog = new AlertDialog.Builder(CameraActivity.this);
//        myDialog.setTitle("Notice");
//        myDialog.setMessage(msg);
//        myDialog.setCancelable(false);
//        myDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
//                }
//            }
//        });
//        myDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//                finish();
//            }
//        });
//        myDialog.show();
//    }
//}
//
//
//
