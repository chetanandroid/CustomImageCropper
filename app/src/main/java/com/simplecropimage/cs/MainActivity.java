package com.simplecropimage.cs;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("all")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = MainActivity.class.getSimpleName();
    private String capturedImagePath;
    private ImageView imageViewMain;
    private Button btnOpenDialog;
    private int REQUEST_CAPTURE_IMAGE = 1, REQUEST_PICK_IMAGE = 2, REQUEST_CROP_IMAGE = 3;
    private static final int REQUEST_CODE_APP_PERMISSIONS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setUpViews();
    }

    private void initViews() {
        imageViewMain = (ImageView) findViewById(R.id.imageViewMain);
        btnOpenDialog = (Button) findViewById(R.id.btnOpenDialog);
    }

    private void setUpViews() {
        btnOpenDialog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpenDialog:
                //checking runtime permission for marshmallow and higher versions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkIfRequiredPermissionsAreGrantedForApp();
                } else {
                    selectImage();
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose From Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    openCamera();
                } else if (position == 1) {
                    openGallery();
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        File photoFile = null;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (photoFile == null) {
            Toast.makeText(MainActivity.this, getString(R.string.no_space), Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
        }
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), REQUEST_PICK_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File image = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        capturedImagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            if (capturedImagePath != null) {
                Log.d("GET Camera PATH:", capturedImagePath);
            }
            /**
             * Here appending "file://" to filepath is must,
             * otherwise the uri is not properly generated to pass to the cropping activity
             * */
            Uri uri = Uri.parse("file://" + capturedImagePath);
            if (!TextUtils.isEmpty(capturedImagePath)) {
                Intent intent = new Intent(MainActivity.this, CropActivity.class);
                intent.setData(uri);
                intent.putExtra("crop-type", "customRatio");
                //Specify the crop view type using intent -- if you don't set crop type then square is set as default
                /*****
                 *intent.putExtra("crop-type","square");
                 *intent.putExtra("crop-type","fitImage");
                 *intent.putExtra("crop-type", "free");
                 *intent.putExtra("crop-type", "circle");
                 *intent.putExtra("crop-type", "circleSquare");
                 *intent.putExtra("crop-type", "ratio3*4");
                 *intent.putExtra("crop-type", "ratio4*3");
                 *intent.putExtra("crop-type", "ratio9*16");
                 *intent.putExtra("crop-type", "ratio16*9");
                 *
                 * //If you use custom ratio, you should also send x and y ratios through intents as below,
                 * //if you don't send x and y ratios then, 1 is taken bydefault
                 * intent.putExtra("crop-type", "customRatio");
                 * intent.putExtra("xRatio", 1);
                 * intent.putExtra("yRatio", 1);
                 ***/
                startActivityForResult(intent, REQUEST_CROP_IMAGE);
                capturedImagePath = null;//reset filepath to remove previous image path from variable
            }
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uri = result.getData();
            Intent intent = new Intent(MainActivity.this, CropActivity.class);
            intent.setData(uri);
            intent.putExtra("crop-type", "customRatio");
            //Specify the crop view type using intent -- if you don't set crop type then square is set as default
            /*****
             *intent.putExtra("crop-type","square");
             *intent.putExtra("crop-type","fitImage");
             *intent.putExtra("crop-type", "free");
             *intent.putExtra("crop-type", "circle");
             *intent.putExtra("crop-type", "circleSquare");
             *intent.putExtra("crop-type", "ratio3*4");
             *intent.putExtra("crop-type", "ratio4*3");
             *intent.putExtra("crop-type", "ratio9*16");
             *intent.putExtra("crop-type", "ratio16*9");
             *
             * //If you use custom ratio, you should also send x and y ratios through intents as below,
             * //if you don't send x and y ratios then, 1 is taken bydefault
             * intent.putExtra("crop-type", "customRatio");
             * intent.putExtra("xRatio", 1);
             * intent.putExtra("yRatio", 1);
             ***/
            startActivityForResult(intent, REQUEST_CROP_IMAGE);
        } else if (requestCode == REQUEST_CROP_IMAGE && resultCode == RESULT_OK) {
            imageViewMain.setImageBitmap(BitmapFactory.decodeFile(result.getStringExtra("cropped-image-path")));
        }
    }

    /*****
     * All The code below checks Runtime Permissions for Andoid Marshmallow and Higher
     ***/
    @TargetApi(Build.VERSION_CODES.M)
    private void checkIfRequiredPermissionsAreGrantedForApp() {
        // Check if we have SYSTEM_ALERT_WINDOW permission for Android 6.0
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add(getString(R.string.camera));
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add(getString(R.string.write_storage_permission));
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = getString(R.string.you_need_to_grant_access_to) + " " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_APP_PERMISSIONS);
                    }
                });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_APP_PERMISSIONS);
            return;
        }
        selectImage();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_APP_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, getString(R.string.permission_required_by_application_denied), Toast.LENGTH_SHORT).show();
                            //We require all permissions to run app, so close app user rejects any permission
                            finish();
                            return;
                        }
                    }
                    checkIfRequiredPermissionsAreGrantedForApp();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.permission_required_by_application_denied), Toast.LENGTH_SHORT).show();
                    //We require all permissions to run app, so close app user rejects any permission
                    finish();
                }
                return;
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }
    /***** Permission check code ends here *****/
}
