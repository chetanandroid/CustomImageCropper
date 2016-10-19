package com.simplecropimage.cs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageViewMain;
    private TextView txtViewOpenDialog;
    private int REQUEST_CAPTURE_IMAGE = 1, REQUEST_PICK_IMAGE = 2, REQUEST_CROP_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setUpViews();
    }

    private void initViews() {
        imageViewMain = (ImageView) findViewById(R.id.imageViewMain);
        txtViewOpenDialog = (TextView) findViewById(R.id.txtViewOpenDialog);
    }

    private void setUpViews() {
        txtViewOpenDialog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtViewOpenDialog:
                selectImage();
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
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAPTURE_IMAGE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if ((requestCode == REQUEST_CAPTURE_IMAGE || requestCode == REQUEST_PICK_IMAGE) && resultCode == RESULT_OK) {
            Uri uri = result.getData();
            Intent intent = new Intent(MainActivity.this, CropActivity.class);
            intent.setData(uri);
            //intent.putExtra("crop-type","circle");
            startActivityForResult(intent, REQUEST_CROP_IMAGE);
        } else if (requestCode == REQUEST_CROP_IMAGE && resultCode == RESULT_OK) {
            imageViewMain.setImageBitmap(BitmapFactory.decodeFile(result.getStringExtra("cropped-image-path")));
        }
    }
}
