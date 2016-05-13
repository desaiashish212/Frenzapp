package com.rishi.frendzapp.custome.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.quickblox.core.server.BaseService;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.custome.activity.DisplayNoteListActivity;
import com.rishi.frendzapp.custome.helper.CategoryDataHoler;
import com.rishi.frendzapp.custome.helper.OfferDataHolder;
import com.rishi.frendzapp.custome.model.Category;
import com.rishi.frendzapp.ui.offer.SinglePageActivity;
import com.rishi.frendzapp_core.utils.PrefsHelper;

/**
 * Created by AD on 18-Apr-16.
 */
public class CategoryListAdapter extends BaseAdapter {

    private Context mContext;
    private final String POSITION = "position";
    private DisplayImageOptions displayImageOptions;
    private LayoutInflater layoutInflater;
    public CategoryListAdapter(Context c) {
        mContext = c;
        this.layoutInflater = LayoutInflater.from(c);
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
        // TODO Auto-generated method stub
        return CategoryDataHoler.getCategoryrDataHolder().getCategoryListSize();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.view_category, null);
            viewHolder = new ViewHolder();
            viewHolder.imgCategory = (ImageView) convertView.findViewById(R.id.grid_image);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.grid_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        applyImage(viewHolder.imgCategory,viewHolder.progressBar,position);
        applyCategory(viewHolder.txtCategory, position);
        final int pos=position;
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startDisplayNoteListActivity(CategoryDataHoler.getCategoryrDataHolder().getCategoryName(pos));
//                Intent intent = new Intent(context, SinglePageActivity.class);
//                intent.putExtra(POSITION, pos);
//                context.startActivity(intent);
                Toast.makeText(mContext, "You Clicked at " + CategoryDataHoler.getCategoryrDataHolder().getCategoryName(pos), Toast.LENGTH_SHORT).show();

            }
        });
        return convertView;
    }


    private void applyImage(ImageView img,final ProgressBar progressBar,int position) {
        //img.setImageBitmap(DataHolder.getDataHolder().getNoteCity(position));
        String uid = CategoryDataHoler.getCategoryrDataHolder().getCategoryImage(position);
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

    private void applyCategory(TextView title, int position) {
        title.setText(CategoryDataHoler.getCategoryrDataHolder().getCategoryName(position));
    }

    private void startDisplayNoteListActivity(String name) {
//        Intent intent = new Intent(mContext, DisplayNoteListActivity.class);
//        intent.putExtra("category",name);
//        mContext.startActivity(intent);
        DisplayNoteListActivity.start(mContext,name);
    }

    private static class ViewHolder {
        ImageView imgCategory;
        ProgressBar progressBar;
        TextView txtCategory;
    }
}
