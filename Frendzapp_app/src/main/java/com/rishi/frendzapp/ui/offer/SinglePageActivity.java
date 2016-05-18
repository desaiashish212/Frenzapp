package com.rishi.frendzapp.ui.offer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.fa;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.core.server.BaseService;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.custome.activity.DisplayNoteListActivity;
import com.rishi.frendzapp.custome.helper.CategoryDataHoler;
import com.rishi.frendzapp.custome.helper.DataHolder;
import com.rishi.frendzapp.custome.helper.OfferDataHolder;
import com.rishi.frendzapp.custome.utils.DialogUtils;
import com.rishi.frendzapp.ui.base.BaseActivity;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp.utils.ReceiveFileFromBitmapTask;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp_core.utils.PrefsHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SinglePageActivity extends BaseActivity {
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

    private TextView txtCalOffer;
    private TextView txtViewComment;
    private TextView txtComment;

    private ListView view_Comment_List;
    private EditText et_Comment;

    private LinearLayout linear_et_Comment;
    private LinearLayout linear_view_Comment_List;

    private View viewImage1;
    private View viewImage2;
    private View viewImage3;
    private View viewImage4;
    private View viewImage5;
    private View comment_view;
    private ScrollView scrollView;
    private ImageButton btn_comment;
    ViewPager viewPager;
    ImagePagerAdapter adapter;

    private final String POSITION = "position";
    private int position;
    private DisplayImageOptions displayImageOptions;
    private TextView txt_titleComment;

    private String[] image_Array=new String[5];


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
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(et_Comment, 0);
        fillFields();
        updateViews();





    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);

       // ivMain = (ImageView) findViewById(R.id.imageView_main);
        ivMain1 = (ImageView) findViewById(R.id.imageView_main1);
        ivMain2 = (ImageView) findViewById(R.id.imageView_main2);
        ivMain3 = (ImageView) findViewById(R.id.imageView_main3);
        ivMain4 = (ImageView) findViewById(R.id.imageView_main4);
        ivMain5 = (ImageView) findViewById(R.id.imageView_main5);

        viewImage1 = (View) findViewById(R.id.view_image1);
        viewImage2 = (View) findViewById(R.id.view_image2);
        viewImage3 = (View) findViewById(R.id.view_image3);
        viewImage4 = (View) findViewById(R.id.view_image4);
        viewImage5 = (View) findViewById(R.id.view_image5);

        txtOfferTitle = (TextView) findViewById(R.id.txt_offerTitle);
        txtDay = (TextView) findViewById(R.id.txt_day);
        txtHr = (TextView) findViewById(R.id.txt_hr);
        txtMin = (TextView) findViewById(R.id.txt_min);
        txtSec = (TextView) findViewById(R.id.txt_sec);

        txtPrice = (TextView) findViewById(R.id.txt_price);
        txtSale = (TextView) findViewById(R.id.txt_sale);
        txtDiscount = (TextView) findViewById(R.id.txt_discount);
        txtSave = (TextView) findViewById(R.id.txt_save);
        txtDescription = (TextView) findViewById(R.id.txt_description);

        txtShopName = (TextView) findViewById(R.id.txt_shopName);
        txtShopAddress = (TextView) findViewById(R.id.txt_shopAddress);
        txtContactPerson = (TextView) findViewById(R.id.txt_contactPerson);
       // txtMobile1 = (TextView) findViewById(R.id.txt_mobile1);
      //  txtMobile2 = (TextView) findViewById(R.id.txt_mobile2);

        txtCalOffer = (TextView) findViewById(R.id.txt_callOffer);
        txtViewComment=(TextView) findViewById(R.id.txtViewComment);
        txtComment=(TextView) findViewById(R.id.txtComment);
        et_Comment=(EditText)findViewById(R.id.et_Comment);
        linear_view_Comment_List=(LinearLayout)findViewById(R.id.linear_view_Comment_List);
        linear_et_Comment=(LinearLayout)findViewById(R.id.linear_et_Comment);

        view_Comment_List=(ListView)findViewById(R.id.view_Comment_List);
        btn_comment=(ImageButton)findViewById(R.id.btn_comment);
        txt_titleComment=(TextView)findViewById(R.id.txt_titleComment);
        comment_view=(View)findViewById(R.id.comment_view);

        adapter = new ImagePagerAdapter();

        initImageLoaderOptions();

    }

    private void initListener() {
        ivMain1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage1(position);
                if (uid != null) {

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
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage2(position);
                if (uid != null) {
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
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage3(position);
                if (uid != null) {

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
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage4(position);
                if (uid != null) {

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
                String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage5(position);
                if (uid != null) {
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

                txtCalOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Mobile_one = OfferDataHolder.getOfferDataHolder().getOfferMobieNo(position);
                if (!isExist(Mobile_one)) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Mobile_one));
                    startActivity(intent);
                }


            }
        });

        txtViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_view.setVisibility(View.VISIBLE);
                txt_titleComment.setVisibility(View.VISIBLE);
                view_Comment_List.setVisibility(View.VISIBLE);
                linear_et_Comment.setVisibility(View.INVISIBLE);
                linear_view_Comment_List.setVisibility(View.VISIBLE);

            }
        });



        txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_view.setVisibility(View.GONE);
                txt_titleComment.setVisibility(View.GONE);
                view_Comment_List.setVisibility(View.GONE);
                linear_view_Comment_List.setVisibility(View.INVISIBLE);

                linear_et_Comment.setVisibility(View.VISIBLE);

            }
        });
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewNote();
            }
        });





    }

    private boolean isExist(String mobile) {
        return TextUtils.isEmpty(mobile);
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


        applyImages(ivMain, position);
        applyDiscount(txtDiscount, position);
        applySave(txtSave, position);
        applyCountDownTime(txtDay, txtHr, txtMin, txtSec, position);

        applyProductImage1(ivMain1, position);
        applyProductImage2(ivMain2, position);
        applyProductImage3(ivMain3, position);
        applyProductImage4(ivMain4, position);
        applyProductImage5(ivMain5, position);

        image_Array[0]=OfferDataHolder.getOfferDataHolder().getOfferProductImage1(position);
        image_Array[1]=OfferDataHolder.getOfferDataHolder().getOfferProductImage2(position);
        image_Array[2]=OfferDataHolder.getOfferDataHolder().getOfferProductImage3(position);
        image_Array[3]=OfferDataHolder.getOfferDataHolder().getOfferProductImage4(position);
        image_Array[4]=OfferDataHolder.getOfferDataHolder().getOfferProductImage5(position);
        viewPager.setAdapter(adapter);
        applyComment();


    }

    private void applyComment() {
        String commentsStr = "";
        for (int i = 0; i < OfferDataHolder.getOfferDataHolder().getNoteComments(position).size(); ++i) {
            commentsStr += "#" + i + "-" + OfferDataHolder.getOfferDataHolder().getNoteComments(position).get(
                    i) + "\n\n";
        }

        System.out.println(commentsStr);
        System.out.println(OfferDataHolder.getOfferDataHolder().getNoteComments(position).size());
        commentsStr.replace("[", "");
        commentsStr.replace("]", "");
        commentsStr = commentsStr.replaceAll("\\[", "").replaceAll("\\]", "");
        commentsStr = commentsStr.replaceAll("\\#", "");
        commentsStr= commentsStr.replaceAll("\\-", "");
        commentsStr= commentsStr.replaceAll("0", "");



        String[] ary = commentsStr.split(",");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(SinglePageActivity.this,
                android.R.layout.simple_list_item_1, ary);
        view_Comment_List.setAdapter(adapter1);
        setListViewHeightBasedOnChildren(view_Comment_List);

        System.out.println(ary.toString() + "......" + ary.length);

    }

    private void applyImages(final ImageView imageView, int position) {

        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage1(position);
        if (uid != null) {

            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions);

            viewImage1.setVisibility(View.VISIBLE);


        }

    }


    private void applyDiscount(TextView discount, int position) {
        int price = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferPrice(position));
        int sale = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferSale(position));
        int result = (price - sale) * 100 / price;
        discount.setText(String.valueOf(result) + "%");
    }

    private void applySave(TextView save, int position) {
        int price = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferPrice(position));
        int sale = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferSale(position));
        int result = price - sale;
        save.setText(String.valueOf(result) + "/-");
    }

    private void applyCountDownTime(final TextView txtday, final TextView txthr, final TextView txtmin, final TextView txtsec, int position) {

        long currentDate = Calendar.getInstance().getTime().getTime();
        long limitDate = dateStringToLong(OfferDataHolder.getOfferDataHolder().getOfferEndDate(position));
        long difference = limitDate - currentDate;

        CountDownTimer countDownTimer = new CountDownTimer(difference, 1000) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long diffSeconds = leftTimeInMilliseconds / 1000 % 60;
                long diffMinutes = leftTimeInMilliseconds / (60 * 1000) % 60;
                long diffHours = leftTimeInMilliseconds / (60 * 60 * 1000) % 24;
                long diffDays = leftTimeInMilliseconds / (24 * 60 * 60 * 1000);

                txtday.setText(String.format("%02d",diffDays));
                txthr.setText(String.format("%02d", diffHours));
                txtmin.setText(String.format("%02d",diffMinutes));
                txtsec.setText(String.format("%02d",diffSeconds));
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    private void applyProductImage1(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage1(position);
        if (uid != null) {

            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions, new ImageLoadingListener());
            imageView.setVisibility(View.VISIBLE);
        }

    }

    private void applyProductImage2(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage2(position);
        if (uid != null) {

            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions, new ImageLoadingListener());
            imageView.setVisibility(View.VISIBLE);

        }

    }

    private void applyProductImage3(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage3(position);
        if (uid != null) {

            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions, new ImageLoadingListener());
            imageView.setVisibility(View.VISIBLE);
        }

    }

    private void applyProductImage4(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage4(position);
        if (!uid.equals(null)) {
            //imageLoader(imageView,uid);
            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions, new ImageLoadingListener());
            imageView.setVisibility(View.VISIBLE);
        }

    }

    private void applyProductImage5(final ImageView imageView, int position) {
        String uid = OfferDataHolder.getOfferDataHolder().getOfferProductImage5(position);
        if (!uid.equals(null)) {
            //imageLoader(imageView,uid);
            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions, new ImageLoadingListener());
            imageView.setVisibility(View.VISIBLE);
        }

    }


    private long dateStringToLong(String date) {
        long mills = 0;
        try {


            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date d = df.parse(date);
            System.out.println(d);
            mills = d.getTime();
        } catch (Exception e) {
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


    //
    public class ImageLoadingListener extends SimpleImageLoadingListener {

        private Bitmap loadedImageBitmap;

        public ImageLoadingListener() {

        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            super.onLoadingStarted(imageUri, view);

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedBitmap) {
            //bitmap_Array.add(loadedBitmap);
           // System.out.println("Array List" + bitmap_Array.toString());
            ///bitmap_Array.size();
           // viewPager.setAdapter(adapter);


        }


        private View.OnClickListener receiveImageFileOnClickListener() {
            return new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }
            };
        }
    }

    // this code is used for the image adapter


    private class ImagePagerAdapter extends PagerAdapter {

        //


        @Override

        public int getCount() {



            return image_Array.length;
        }


        @Override

        public boolean isViewFromObject(View view, Object object) {

            return view == ((ImageView) object);

        }


        @Override

        public Object instantiateItem(ViewGroup container, int position) {

            Context context = SinglePageActivity.this;

           final ImageView imageView = new ImageView(context);

            int padding = context.getResources().getDimensionPixelSize(

                    R.dimen.padding_medium);

            imageView.setPadding(padding, padding, padding, padding);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


            ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + image_Array[position] +
                            "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                    imageView, displayImageOptions, new ImageLoadingListener());
            imageView.setVisibility(View.VISIBLE);

            ((ViewPager) container).addView(imageView, 0);

            final int pos=position;
            ivMain1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                                Toast.makeText(SinglePageActivity.this, "Image 1", Toast.LENGTH_SHORT).show();
                        //imageLoader(imageView,uid);
                        ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + image_Array[pos] +
                                        "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                                imageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
                        viewImage1.setVisibility(View.VISIBLE);
                        viewImage2.setVisibility(View.INVISIBLE);
                        viewImage3.setVisibility(View.INVISIBLE);
                        viewImage4.setVisibility(View.INVISIBLE);
                        viewImage5.setVisibility(View.INVISIBLE);
                    viewPager.setAdapter(adapter);




                }
            });
            ivMain2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(SinglePageActivity.this,"Image 2",Toast.LENGTH_SHORT).show();
                    //imageLoader(imageView,uid);
                    ImageLoader.getInstance().displayImage(BaseService.getServiceEndpointURL() + "/blobs/" + image_Array[pos] +
                                    "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                            imageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
                    viewImage1.setVisibility(View.INVISIBLE);
                    viewImage2.setVisibility(View.VISIBLE);
                    viewImage3.setVisibility(View.INVISIBLE);
                    viewImage4.setVisibility(View.INVISIBLE);
                    viewImage5.setVisibility(View.INVISIBLE);


                }
            });

            return imageView;

        }

        @Override

        public void destroyItem(ViewGroup container, int position, Object object) {

            ((ViewPager) container).removeView((ImageView) object);

        }

    }

    public void updateViews() {


        QBCustomObject record = new QBCustomObject();
        record.setClassName("Offers");
        record.setCustomObjectId(OfferDataHolder.getOfferDataHolder().getOfferId(position));

        QBRequestUpdateBuilder updateBuilder = new QBRequestUpdateBuilder();
        updateBuilder.inc("Views", 1);

        QBCustomObjects.updateObject(record, updateBuilder, new QBEntityCallback<QBCustomObject>() {
            @Override
            public void onSuccess(QBCustomObject object, Bundle params) {

            }

            @Override
            public void onSuccess() {

                System.out.println("Views updated successfully");
            }

            @Override
            public void onError(List<String> list) {
                System.out.println("Views updated fail");
            }

           //@Override
            public void onError(QBResponseException errors) {

            }
        });
    }





    private void createNewNote() {
        // create new score in activity_note class

        String user = AppSession.getSession().getUser().getFullName();
        String comments = et_Comment.getText().toString();

        if(!isValidData(user, comments)) {
            DialogUtils.showLong(this,"please enter comment");
            return;
        }




        QBCustomObject qbCustomObject = new QBCustomObject();
        qbCustomObject.setClassName("Offers");

        QBRequestUpdateBuilder updateBuilder = new QBRequestUpdateBuilder();
        updateBuilder.push("comments", comments);

        qbCustomObject.setCustomObjectId(OfferDataHolder.getOfferDataHolder().getOfferId(position));



        QBCustomObjects.updateObject(qbCustomObject, updateBuilder, new QBEntityCallback<QBCustomObject>() {
            @Override
            public void onSuccess(QBCustomObject object, Bundle params) {

                System.out.println("Comment added successfully");
                et_Comment.setText("");
                Toast.makeText(SinglePageActivity.this,"Comment added successfully",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSuccess() {


            }

            @Override
            public void onError(List<String> list) {
                System.out.println("comment added fail");
                Toast.makeText(SinglePageActivity.this,"please try again",Toast.LENGTH_SHORT).show();

            }

            //@Override
            public void onError(QBResponseException errors) {

            }
        });
    }
    private boolean isValidData(String note, String comments) {
        return (!TextUtils.isEmpty(note) && !TextUtils.isEmpty(comments));
    }
    //

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup)
                listItem.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}