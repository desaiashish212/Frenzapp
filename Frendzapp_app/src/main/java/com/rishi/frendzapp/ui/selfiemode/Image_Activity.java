package com.rishi.frendzapp.ui.selfiemode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;

public class Image_Activity extends BaseLogeableActivity {

    private Bitmap bmp;
    private  ImageView camera_Image;
    private ImageView img_profilepic;
    private ImageView img_share;
    private  ImageView img_clockwise_Rotate;
    private  ImageView img_anti_clockwise_Rotate;
    // this code is written for the custom action bar
    float angle=0;
    private Intent data;
    private int requestCode;
    private int resultCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        init();
        initlistener();
        camera_Image.setImageBitmap((Bitmap) data.getExtras().get("data"));

    }
    private  void init()
    {
        camera_Image=(ImageView)findViewById(R.id.img_camera);
        img_profilepic=(ImageView)findViewById(R.id.img_setProfile);
        img_share=(ImageView)findViewById(R.id.img_share);
        img_clockwise_Rotate=(ImageView)findViewById(R.id.img_share);
        img_anti_clockwise_Rotate=(ImageView)findViewById(R.id.img_anticlockwise_arrow);
        bmp = getIntent().getExtras().getParcelable("name");
        data = getIntent().getExtras().getParcelable("data");
        requestCode = getIntent().getExtras().getInt("requestCode");
        resultCode = getIntent().getExtras().getInt("resultCode");
        Toast.makeText(this,"requestCode:"+requestCode+" resultCode"+resultCode,Toast.LENGTH_LONG).show();
    }

    public void initlistener()
    {
        img_profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Image_Activity.this, "You clicked on the Profile Picture", Toast.LENGTH_SHORT).show();
            }
        });
        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Image_Activity.this, "You clicked on the Share", Toast.LENGTH_SHORT).show();
            }
        });
        img_clockwise_Rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                angle += 90;
                Bitmap rotatedImage = rotateImage(bmp, angle);
                camera_Image.setImageBitmap(rotatedImage);
            }
        });
        img_anti_clockwise_Rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle += 270;
                Bitmap rotatedImage = rotateImage(bmp, angle);
                camera_Image.setImageBitmap(rotatedImage);
            }
        });
    }
    public static Bitmap rotateImage(Bitmap sourceImage, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }
    //@Override
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
                //changeAvatarOnClick();
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




}
