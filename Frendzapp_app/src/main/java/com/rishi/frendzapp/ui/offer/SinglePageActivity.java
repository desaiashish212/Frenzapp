package com.rishi.frendzapp.ui.offer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.core.server.BaseService;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.custome.activity.DisplayNoteListActivity;
import com.rishi.frendzapp.custome.helper.CategoryDataHoler;
import com.rishi.frendzapp.custome.helper.OfferDataHolder;
import com.rishi.frendzapp.ui.base.BaseActivity;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.PrefsHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SinglePageActivity extends Activity {

    private ImageView ivMain;
    private ImageView ivMain1;
    private ImageView ivMain2;
    private ImageView ivMain3;
    private ImageView ivMain4;
    private ImageView ivMain5;
    private TextView txtOfferTitle;
    private TextView txtDay;
    private TextView txtHr;
    private TextView txtMin;
    private TextView txtSec;
    private TextView txtPrice;
    private TextView txtSale;
    private TextView txtDiscount;
    private TextView txtSave;
    private TextView txtDescription;
    private TextView txtShopName;
    private TextView txtShopAddress;
    private TextView txtContactPerson;
    private TextView txtMobile1;
    private TextView txtMobile2;
    private View viewImage1;
    private View viewImage2;
    private View viewImage3;
    private View viewImage4;
    private View viewImage5;
    //ProgressBar progressBar;
    private final String POSITION = "position";
    private int position;
    private DisplayImageOptions displayImageOptions;

    public static void start(Context context, int position) {
        Intent intent = new Intent(context, SinglePageActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_ITEM_POSITION, position);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getIntent().getExtras().getInt(QBServiceConsts.EXTRA_ITEM_POSITION);
        setContentView(R.layout.activity_single_page);
        init();
        initListener();
        fillFields();

    }

    private  void init(){

         ivMain=(ImageView)findViewById(R.id.imageView_main);
        ivMain1=(ImageView)findViewById(R.id.imageView_main1);
        ivMain2=(ImageView)findViewById(R.id.imageView_main2);
        ivMain3=(ImageView)findViewById(R.id.imageView_main3);
        ivMain4=(ImageView)findViewById(R.id.imageView_main4);
        ivMain5=(ImageView)findViewById(R.id.imageView_main5);

        viewImage1 = (View) findViewById(R.id.view_image1);
        viewImage2 = (View) findViewById(R.id.view_image2);
        viewImage3 = (View) findViewById(R.id.view_image3);
        viewImage4 = (View) findViewById(R.id.view_image4);
        viewImage5 = (View) findViewById(R.id.view_image5);

        txtOfferTitle=(TextView)findViewById(R.id.txt_offerTitle);
        txtDay=(TextView)findViewById(R.id.txt_day);
        txtHr=(TextView)findViewById(R.id.txt_hr);
        txtMin=(TextView)findViewById(R.id.txt_min);
        txtSec=(TextView)findViewById(R.id.txt_sec);

        txtPrice=(TextView)findViewById(R.id.txt_price);
        txtSale=(TextView)findViewById(R.id.txt_sale);
        txtDiscount=(TextView)findViewById(R.id.txt_discount);
        txtSave=(TextView)findViewById(R.id.txt_save);
        txtDescription=(TextView)findViewById(R.id.txt_description);

        txtShopName=(TextView)findViewById(R.id.txt_shopName);
        txtShopAddress=(TextView)findViewById(R.id.txt_shopAddress);
        txtContactPerson=(TextView)findViewById(R.id.txt_contactPerson);
        txtMobile1=(TextView)findViewById(R.id.txt_mobile1);
        txtMobile2=(TextView)findViewById(R.id.txt_mobile2);

        //progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        initImageLoaderOptions();

    }
    private  void initListener(){
        ivMain1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage1(position) ;
                if (uid != null){
                    //imageLoader(imageView,uid);
                    ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                                    "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                            ivMain, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
                    viewImage1.setVisibility(View.VISIBLE);
                    viewImage2.setVisibility(View.INVISIBLE);
                    viewImage3.setVisibility(View.INVISIBLE);
                    viewImage4.setVisibility(View.INVISIBLE);
                    viewImage5.setVisibility(View.INVISIBLE);
                }
            }
        });

        ivMain2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage2(position) ;
                if (uid != null){
                    //imageLoader(imageView,uid);
                    ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                                    "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                            ivMain, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
                    viewImage1.setVisibility(View.INVISIBLE);
                    viewImage2.setVisibility(View.VISIBLE);
                    viewImage3.setVisibility(View.INVISIBLE);
                    viewImage4.setVisibility(View.INVISIBLE);
                    viewImage5.setVisibility(View.INVISIBLE);
                }
            }
        });

        ivMain3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage3(position) ;
                if (uid != null){
                    //imageLoader(imageView,uid);
                    ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                                    "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                            ivMain, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
                    viewImage1.setVisibility(View.INVISIBLE);
                    viewImage2.setVisibility(View.INVISIBLE);
                    viewImage3.setVisibility(View.VISIBLE);
                    viewImage4.setVisibility(View.INVISIBLE);
                    viewImage5.setVisibility(View.INVISIBLE);
                }
            }
        });

        ivMain4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage4(position) ;
                if (uid != null){
                    //imageLoader(imageView,uid);
                    ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                                    "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                            ivMain, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
                    viewImage1.setVisibility(View.INVISIBLE);
                    viewImage2.setVisibility(View.INVISIBLE);
                    viewImage3.setVisibility(View.INVISIBLE);
                    viewImage4.setVisibility(View.VISIBLE);
                    viewImage5.setVisibility(View.INVISIBLE);
                }
            }
        });

        ivMain5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage5(position) ;
                if (uid != null){
                    //imageLoader(imageView,uid);
                    ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                                    "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                            ivMain, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
                    viewImage1.setVisibility(View.INVISIBLE);
                    viewImage2.setVisibility(View.INVISIBLE);
                    viewImage3.setVisibility(View.INVISIBLE);
                    viewImage4.setVisibility(View.INVISIBLE);
                    viewImage5.setVisibility(View.VISIBLE);
                }
            }
        });



    }
    public void initImageLoaderOptions() {
        displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(
                        true).cacheOnDisc(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
    private void fillFields() {
        txtOfferTitle.setText(OfferDataHolder.getOfferDataHolder().getOfferName(position));
        txtPrice.setText(OfferDataHolder.getOfferDataHolder().getOfferPrice(position) + "/-");
        txtSale.setText(OfferDataHolder.getOfferDataHolder().getOfferSale(position) + "/-");
        txtDescription.setText(OfferDataHolder.getOfferDataHolder().getOfferDescription(position));

        txtShopName.setText(OfferDataHolder.getOfferDataHolder().getOfferShopName(position));
        txtShopAddress.setText(OfferDataHolder.getOfferDataHolder().getOfferShopAddress(position));
        txtContactPerson.setText(OfferDataHolder.getOfferDataHolder().getOfferContactPerson(position));
        txtMobile1.setText(OfferDataHolder.getOfferDataHolder().getOfferMobieNo(position));

        applyImages(ivMain,position);
        applyDiscount(txtDiscount,position);
        applySave(txtSave,position);
        applyCountDownTime(txtDay, txtHr, txtMin, txtSec, position);

        applyProductImage1(ivMain1,position);
        applyProductImage2(ivMain2,position);
        applyProductImage3(ivMain3,position);
        applyProductImage4(ivMain4,position);
        applyProductImage5(ivMain5,position);




    }
    private void applyImages(final ImageView imageView,int position){
       // String uid = "";
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage1(position) ;
        if (uid != null){
            //imageLoader(imageView,uid);
            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions);
            imageView.setVisibility(View.VISIBLE);
            viewImage1.setVisibility(View.VISIBLE);
        }

    }

    private void applyDiscount(TextView discount, int position) {
        int price = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferPrice(position));
        int sale = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferSale(position));
        int result = (price - sale)*100/price;
        discount.setText(String.valueOf(result) + "%");
    }

    private void applySave(TextView save, int position) {
        int price = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferPrice(position));
        int sale = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferSale(position));
        int result = price - sale;
        save.setText(String.valueOf(result) + "/-");
    }

    private void applyCountDownTime(final TextView txtday,final TextView txthr,final TextView txtmin,final TextView txtsec, int position) {

        long currentDate = Calendar.getInstance().getTime().getTime();
        long limitDate = dateStringToLong(OfferDataHolder.getOfferDataHolder().getOfferEndDate(position));
        long difference = limitDate - currentDate;

        CountDownTimer countDownTimer = new CountDownTimer(difference,1000) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long diffSeconds = leftTimeInMilliseconds / 1000 % 60;
                long diffMinutes = leftTimeInMilliseconds / (60 * 1000) % 60;
                long diffHours = leftTimeInMilliseconds / (60 * 60 * 1000) % 24;
                long diffDays = leftTimeInMilliseconds / (24 * 60 * 60 * 1000);

                txtday.setText(String.valueOf(diffDays));
                txthr.setText(String.valueOf(diffHours));
                txtmin.setText(String.valueOf(diffMinutes));
                txtsec.setText(String.valueOf(diffSeconds));
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    private void applyProductImage1(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage1(position) ;
        if (uid != null){
            //imageLoader(imageView,uid);
            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions);
            imageView.setVisibility(View.VISIBLE);
        }

    }

    private void applyProductImage2(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage2(position) ;
        if (uid != null){
            //imageLoader(imageView,uid);
            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions);
            imageView.setVisibility(View.VISIBLE);

        }

    }

    private void applyProductImage3(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage3(position) ;
        if (uid != null){
           // imageLoader(imageView,uid);
            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions);
            imageView.setVisibility(View.VISIBLE);
        }

    }

    private void applyProductImage4(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage4(position) ;
        if (!uid.equals(null)){
            //imageLoader(imageView,uid);
            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions);
            imageView.setVisibility(View.VISIBLE);
        }

    }

    private void applyProductImage5(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage5(position) ;
        if (!uid.equals(null)){
            //imageLoader(imageView,uid);
            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions);
            imageView.setVisibility(View.VISIBLE);
        }

    }



    private long dateStringToLong(String date){
        long mills=0;
        try {
            //   SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date d  = df.parse(date);
            System.out.println(d);
            mills = d.getTime();
        }catch (Exception e){
            Log.d("DateError", e.toString());
            e.printStackTrace();
        }
        return mills;
    }
    private void startDisplayNoteListActivity(String name) {
        DisplayNoteListActivity.start(this, name);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startDisplayNoteListActivity(OfferDataHolder.getOfferDataHolder().getOfferCategory(position));
        finish();
    }
}
