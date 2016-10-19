package com.simplecropimage.cs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Refer This Link to The Base Library --- https://github.com/IsseiAoki/SimpleCropView --- Current Mod By CS
 **/
public class CropActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = CropActivity.class.getSimpleName();
    private CropImageView mCropView;
    private ImageButton imgButtonRotateLeft, imgButtonRotateRight, imgButtonDone;
    private TextView txtViewBack;
    private static final String PROGRESS_DIALOG = "ProgressDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop);
        initViews();
        setUpViews();
        setCropMode();
        mCropView.setGuideShowMode(CropImageView.ShowMode.NOT_SHOW); //hide grid on CropView
        mCropView.startLoad(getIntent().getData(), mLoadCallback);
    }

    private void initViews() {
        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        imgButtonRotateLeft = (ImageButton) findViewById(R.id.imgButtonRotateLeft);
        imgButtonRotateRight = (ImageButton) findViewById(R.id.imgButtonRotateRight);
        imgButtonDone = (ImageButton) findViewById(R.id.imgButtonDone);
        txtViewBack = (TextView) findViewById(R.id.txtViewBack);
    }

    private void setUpViews() {
        imgButtonRotateLeft.setOnClickListener(this);
        imgButtonRotateRight.setOnClickListener(this);
        imgButtonDone.setOnClickListener(this);
        txtViewBack.setOnClickListener(this);
    }

    private void setCropMode() {
        String cropType;
        //if user sends crop-type via intent then set that crop type, else set square crop type as default
        if (getIntent().hasExtra("crop-type")) {
            cropType = getIntent().getStringExtra("crop-type");
            if (cropType.equals("square")) {
                mCropView.setCropMode(CropImageView.CropMode.SQUARE);
            } else if (cropType.equals("fitImage")) {
                mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
            } else if (cropType.equals("free")) {
                mCropView.setCropMode(CropImageView.CropMode.FREE);
            } else if (cropType.equals("circle")) {
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
            } else if (cropType.equals("circleSquare")) {
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
            } else if (cropType.equals("ratio3*4")) {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
            } else if (cropType.equals("ratio4*3")) {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
            } else if (cropType.equals("ratio9*16")) {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
            } else if (cropType.equals("ratio16*9")) {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
            } else if (cropType.equals("customRatio")) {
                int xRatio = getIntent().getIntExtra("xRatio", 1);
                int yRatio = getIntent().getIntExtra("yRatio", 1);
                mCropView.setCustomRatio(xRatio, yRatio);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgButtonDone:
                cropImage();
                break;
            case R.id.imgButtonRotateLeft:
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                break;
            case R.id.imgButtonRotateRight:
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                break;
            case R.id.txtViewBack:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    public void cropImage() {
        showProgress();
        mCropView.startCrop(createSaveUri(), mCropCallback, mSaveCallback);
    }

    public Uri createSaveUri() {
        return Uri.fromFile(new File(getCacheDir(), "cropped"));
    }

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {
            hideProgress();
        }

        @Override
        public void onError() {
            hideProgress();
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
        }

        @Override
        public void onError() {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {
            //create bitmap from temporary file uri
            Bitmap bmp = getBitmap(outputUri);
            Intent intent = new Intent();
            //save the bitmap and send saved bitmap path to calling activity
            intent.putExtra("cropped-image-path", saveImage(bmp));
            setResult(RESULT_OK, intent);
            hideProgress();
            finish();
        }

        @Override
        public void onError() {
            hideProgress();
        }
    };

    private Bitmap getBitmap(Uri uri) {
        InputStream in;
        try {
            final int IMAGE_MAX_SIZE = 1024;
            in = getContentResolver().openInputStream(uri);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();
            return b;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file " + uri + " not found");
        } catch (IOException e) {
            Log.e(TAG, "file " + uri + " not found");
        }
        return null;
    }

    protected String saveImage(Bitmap imageBitmap) {
        String FOLDER_NAME_TO_STORE_IMAGES = "SimpleCropImage";
        String JPEG_FILE_PREFIX = "IMG_";
        String JPEG_FILE_SUFFIX = ".jpg";

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME_TO_STORE_IMAGES);
        imagesFolder.mkdirs();
        String getImagePath = imagesFolder.getAbsolutePath() + "/" + JPEG_FILE_PREFIX + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + JPEG_FILE_SUFFIX;
        if (getImagePath != null && getImagePath.trim().length() > 0) {
            File imageFile = new File(getImagePath);
            imageFile.delete();
            try {
                imageFile.createNewFile();
                imageFile.deleteOnExit();
                FileOutputStream out = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getImagePath;
    }

    public void showProgress() {
        ProgressDialogFragment progressFragment = ProgressDialogFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(progressFragment, PROGRESS_DIALOG).commitAllowingStateLoss();
    }

    public void hideProgress() {
        ProgressDialogFragment progressFragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(PROGRESS_DIALOG);
        if (progressFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(progressFragment).commitAllowingStateLoss();
        }
    }
}