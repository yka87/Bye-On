package com.example.opencv;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.opencv.core.Mat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

/*
    This class manages image processing of user's uploaded image by calling openCV C++ library functions.
 */
public class ImageActivity extends AppCompatActivity {
    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    //Declare and call functions in the openCV C++ library to make use of
    // the faster speed of processing images in C++ and library functions
    public native void nBitmapToMat(Bitmap bitmap, long input, boolean needUnPremultiplyAlpha);
    public native void processImage(long inputImage, long outputImage, int th1, int th2, String nameInput);
    public native void nMatToBitmap (long src, Bitmap bitmap, boolean needPremultiplyAlpha);

    ImageView imageVIewInput, imageViewOutput;
    private Mat img_input, img_output;
    private Bitmap bitmapOutput;
    private SeekBar seekBar1;
    private int threshold1 = 25;
    private int threshold2 = 25;
    boolean isReady = false;

    private EditText editPetName;
    private String nameInput;
    private Button btnUpload, btnSave, btnProcess, btnGetName;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final String TAG = "opencv";
    private final int GET_GALLERY_IMAGE = 200;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_upload);

        imageVIewInput = findViewById(R.id.imageViewInput);
        imageViewOutput = findViewById(R.id.imageViewOutput);
        editPetName = findViewById(R.id.edit_pet_name);
        ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        btnGetName = findViewById(R.id.btn_getname);
        btnGetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPetName();
            }
        });

        btnProcess = findViewById(R.id.btnProcess);
        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOutputImage(threshold1, threshold2);
            }
        });

        btnSave = findViewById(R.id.btnOpen);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewImage();
            }
        });

        btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Let user select image from their local gallery
        imageVIewInput.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        // Set up seek bar to adjust threshold of canny function
        final TextView textView1 = findViewById(R.id.textView_threshold1);
        textView1.setText(threshold1 + " Smooth");
        seekBar1 = findViewById(R.id.seekBar_threshold1);
        seekBar1.setMax(51);
        seekBar1.setMin(0);
        seekBar1.setProgress(threshold1);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        int stepSize = 2;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshold1 = ((int)Math.round(progress/stepSize))*stepSize +1;
                textView1.setText(threshold1 + " Smooth");
                getOutputImage(threshold1, threshold2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // check if the user was asked for permission
        if (!hasPermissions(PERMISSIONS)) {
            requestNecessaryPermissions(PERMISSIONS);
        }
    }

    private void setPetName(){
        nameInput = editPetName.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isReady = true;
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, ImageActivity.class);
        return intent;
    }

    // Create Mat object of img_output to store output
    private void getOutputImage(int th1, int th2) {
//        if (isReady == false) return;
        if (img_output == null) {
            img_output = new Mat();
        }
        if (nameInput == null){
            String message = "Enter your pet name";
            Toast.makeText(ImageActivity.this, message, Toast.LENGTH_SHORT).show();
            return;
        }

        // Send args to the function in the native-lib.cpp file and get processed bitmap file from C++ functions
        processImage(img_input.getNativeObjAddr(), img_output.getNativeObjAddr(), th1, th2, nameInput);
        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        nMatToBitmap(img_output.getNativeObjAddr(), bitmapOutput, false);
        imageViewOutput.setImageBitmap(bitmapOutput);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE) {
            if (data.getData() != null) {
                filePath = data.getData();
                try {
                    String path = getPathfromUri(filePath);
                    int orientation = getOrientationOfImage(path);
                    Bitmap temp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    Bitmap bitmap = processBitmapbyRotating(temp, orientation);
                    imageVIewInput.setImageBitmap(bitmap);

                    // Convert the image from user gallery to Mat object by using C++ function
                    img_input = new Mat();
                    Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    nBitmapToMat(bmp32, img_input.getNativeObjAddr(), false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // upload processed image to the firebase server
    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        // Get the data from an ImageView as bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapOutput.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference petsRef = storageReference.child("UserImages/"+ UUID.randomUUID().toString());
        UploadTask uploadTask = petsRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ImageActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(ImageActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded "+(int)progress+"%");
            }
        });
    }

    // Code based on [https://developer.android.com/reference/android/content/Context#getExternalFilesDir(java.lang.String)]
    private void openNewImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);


//        String filename = String.format("%d.png", System.currentTimeMillis());
//        File file = new File(getExternalFilesDir(null), filename);
//
//        try {
//            // Very simple code to copy a picture from the application's
//            // resource into the external file.  Note that this code does
//            // no error checking, and assumes the picture is small (does not
//            // try to copy it in chunks).  Note that if external storage is
//            // not currently mounted this will silently fail.
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmapOutput.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            InputStream is = new ByteArrayInputStream(baos.toByteArray());
//            OutputStream os = new FileOutputStream(file);
//            byte[] data = new byte[is.available()];
//            is.read(data);
//            os.write(data);
//            is.close();
//            os.close();
//        } catch (IOException e) {
//            Log.w("ExternalStorage", "Error writing " + file, e);
//        }
    }

    private String getPathfromUri(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        return cursor.getString(column_index);
    }

    // Code based on [http://snowdeer.github.io/android/2016/02/02/android-image-rotation/]
    public int getOrientationOfImage(String filepath) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            Log.d("@@@", e.toString());
            return -1;
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

        if (orientation != -1) {
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        }
        return 0;
    }

    public Bitmap processBitmapbyRotating(Bitmap bitmap, int degrees) throws Exception {
        if (bitmap == null) return null;
        if (degrees == 0) return bitmap;
        Matrix m = new Matrix();
        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    // Check and ask permission of user to access external storage
    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        for (String perms : permissions) {
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (permsRequestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!writeAccepted) {
                            showDialogforPermission("Permission is required to run the App.");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ImageActivity.this);
        dialog.setTitle("Notice");
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        dialog.show();
    }
}
