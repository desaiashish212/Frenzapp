package com.rishi.frendzapp.ui.selfiemode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;
import com.rishi.frendzapp.utils.ImageUtils;
import com.rishi.frendzapp.utils.ReceiveFileFromBitmapTask;
import com.rishi.frendzapp.utils.ReceiveUriScaledBitmapTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AD on 15-Apr-16.
 */
public class CameraActivity extends BaseLogeableActivity implements ReceiveFileFromBitmapTask.ReceiveFileListener, ReceiveUriScaledBitmapTask.ReceiveUriScaledBitmapListener{
    private Uri mFileUri;
    private ImageButton btnRotate;
    ImageView imageView;
    Bitmap bitmap;
    private float angle=0;
    int requestCode;
    int resultCode;
    Intent data;
    ImageUtils imageUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUtils = new ImageUtils(this);
        mFileUri = getOutputMediaFileUri(1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        // start the image capture Intent
        startActivityForResult(intent, 100);
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
       System.out.println("requestCode" + requestCode + " resultCode" + resultCode);
        this.resultCode = resultCode;
        this.requestCode = requestCode;
        this.data = imageReturnedIntent;
        if (resultCode == RESULT_OK) {
            if (mFileUri != null) {
                String mFilePath = mFileUri.toString();
                if (mFilePath != null) {
                    setImage(mFilePath);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }


    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    // Return image / video
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == 1) { // image
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == 2) { // video
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void setImage(String filepath) {
        filepath = filepath.replace("file://", ""); // remove to avoid BitmapFactory.decodeFile return null
        File imgFile = new File(filepath);
        if (imgFile.exists()) {
            //

            int rotate = 0;
            try {
                File imageFile = new File(filepath);
                ExifInterface exif = new ExifInterface(
                        imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), null);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(bitmap);
        }
    }

    public void initListener(){
        btnRotate = _findViewById(R.id.btn_rotate);
        imageView = (ImageView) findViewById(R.id.result_image);
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle += 90;
                Bitmap rotatedImage = rotateImage(bitmap, angle);
                imageView.setImageBitmap(rotatedImage);
            }
        });
    }
    public static Bitmap rotateImage(Bitmap sourceImage, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.selfie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_set_profile:
                Toast.makeText(this, "You clicked on SetProfile", Toast.LENGTH_SHORT).show();

                break;
            // action with ID action_settings was selected
            case R.id.action_share:
                Toast.makeText(this, "You clicked on Share", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public void onCachedImageFileReceived(File imageFile) {

    }

    @Override
    public void onAbsolutePathExtFileReceived(String absolutePath) {

    }

    @Override
    public void onUriScaledBitmapReceived(Uri uri) {

    }
}