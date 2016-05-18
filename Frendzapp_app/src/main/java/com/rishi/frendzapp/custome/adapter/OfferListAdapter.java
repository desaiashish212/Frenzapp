package com.rishi.frendzapp.custome.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.quickblox.core.server.BaseService;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.custome.activity.DisplayNoteListActivity;
import com.rishi.frendzapp.custome.helper.CategoryDataHoler;
import com.rishi.frendzapp.custome.helper.DataHolder;
import com.rishi.frendzapp.custome.helper.OfferDataHolder;
import com.rishi.frendzapp.custome.model.Offer;
import com.rishi.frendzapp.ui.offer.SinglePageActivity;
import com.rishi.frendzapp_core.utils.PrefsHelper;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by AD on 07-Apr-16.
 */
public class OfferListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private  Context context;
    private final String POSITION = "position";
    private DisplayImageOptions displayImageOptions;
    NumberFormat formatter = new DecimalFormat("00");
    public OfferListAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context=context;
        initImageLoaderOptions();
    }

    public void initImageLoaderOptions() {
        displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(
                        true).cacheOnDisc(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return OfferDataHolder.getOfferDataHolder().getOfferListSize();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.demo, null);
            viewHolder = new ViewHolder();
            viewHolder.imgProduct = (ImageView) convertView.findViewById(R.id.product_imageView);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            viewHolder.txtday = (TextView) convertView.findViewById(R.id.offer_day);
            viewHolder.txtHr = (TextView) convertView.findViewById(R.id.offer_hr);
            viewHolder.txtMin = (TextView) convertView.findViewById(R.id.offer_min);
            viewHolder.txtSec = (TextView) convertView.findViewById(R.id.offer_sec);
            viewHolder.txtView = (TextView) convertView.findViewById(R.id.offer_views);
            viewHolder.txtComment = (TextView) convertView.findViewById(R.id.offer_comment);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.offer_title);
            viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.offer_price);
            viewHolder.txtSale = (TextView) convertView.findViewById(R.id.offer_sale);
            viewHolder.txtDiscount = (TextView) convertView.findViewById(R.id.offer_discount);
            viewHolder.txtSave = (TextView) convertView.findViewById(R.id.offer_save);
            viewHolder.imgbtnShare = (ImageButton) convertView.findViewById(R.id.ib_share);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        applyImage(viewHolder.imgProduct,viewHolder.progressBar,position);
        applyTitle(viewHolder.txtTitle, position);
        applyPrice(viewHolder.txtPrice, position);
        applySale(viewHolder.txtSale, position);
        applyDiscount(viewHolder.txtDiscount, position);
        applySave(viewHolder.txtSave, position);
        applyViews(viewHolder.txtView, position);
        applyComment(viewHolder.txtComment,position);
        applyCountDownTime(viewHolder.txtday, viewHolder.txtHr, viewHolder.txtMin, viewHolder.txtSec, position);
        final int pos=position;
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startSinglePageActivity(pos);
//                Intent intent = new Intent(context, SinglePageActivity.class);
//                intent.putExtra(POSITION, pos);
//                context.startActivity(intent);
            }
        });
        return convertView;

    }

    private void applyImage(ImageView img,final ProgressBar progressBar,int position) {
        //img.setImageBitmap(DataHolder.getDataHolder().getNoteCity(position));
        String uid = OfferDataHolder.getOfferDataHolder().getOfferMainImage(position);
        ImageLoader.getInstance().displayImage(
                BaseService.getServiceEndpointURL() + "/blobs/" + uid +
                        "?token=" + PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_SESSION_TOKEN),
                img, displayImageOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }



    private void applyTitle(TextView title, int position) {
        title.setText(OfferDataHolder.getOfferDataHolder().getOfferName(position));
    }

    private void applyPrice(TextView price, int position) {
        price.setText(OfferDataHolder.getOfferDataHolder().getOfferPrice(position)+"/-");
    }

    private void applySale(TextView sale, int position) {
        sale.setText(OfferDataHolder.getOfferDataHolder().getOfferSale(position)+"/-");
    }
    private void applyViews(TextView views, int position) {
        views.setText(OfferDataHolder.getOfferDataHolder().getOffersViews(position));
    }

    private void applyDiscount(TextView discount, int position) {
        int price = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferPrice(position));
        int sale = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferSale(position));
        int result = (price - sale)*100/price;
        discount.setText(String.valueOf(result)+"%");
    }

    private void applySave(TextView save, int position) {
        int price = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferPrice(position));
        int sale = Integer.parseInt(OfferDataHolder.getOfferDataHolder().getOfferSale(position));
        int result = price - sale;
        save.setText(String.valueOf(result)+"/-");
    }

    private void applyComment(TextView comment,int position) {
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
        comment.setText(String.valueOf(ary.length));
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



                txtday.setText(String.format("%02d", diffDays));
                txthr.setText(String.format("%02d", diffHours));
                txtmin.setText(String.format("%02d", diffMinutes));
                txtsec.setText(String.format("%02d", diffSeconds));

            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    private static class ViewHolder {
        ImageView imgProduct;
        ProgressBar progressBar;
        TextView txtday;
        TextView txtHr;
        TextView txtMin;
        TextView txtSec;
        TextView txtView;
        TextView txtComment;
        TextView txtTitle;
        TextView txtPrice;
        TextView txtSale;
        TextView txtSave;
        TextView txtDiscount;
        ImageButton imgbtnShare;
    }

    private long dateStringToLong(String date){
        long mills=0;
        try {
         //   SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date d  = df.parse(date);
           // System.out.println(d);
            mills = d.getTime();
        }catch (Exception e){
            Log.d("DateError",e.toString());
            e.printStackTrace();
        }
        return mills;
    }
    private void startSinglePageActivity(final int pos) {
        SinglePageActivity.start(context, pos);
    }
}
