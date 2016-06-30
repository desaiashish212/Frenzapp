package com.rishi.frendzapp.ui.chats;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.quickblox.chat.model.QBDialog;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp.utils.FileHelper;
import com.rishi.frendzapp.utils.RateTextCircularProgressBar;
import com.rishi.frendzapp_core.db.tables.MessageTable;
import com.rishi.frendzapp.ui.base.BaseCursorAdapter;
import com.rishi.frendzapp.ui.views.MaskedImageView;
import com.rishi.frendzapp.ui.views.RoundedImageView;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp.utils.DateUtils;
import com.rishi.frendzapp.utils.ImageUtils;
import com.rishi.frendzapp.utils.ReceiveFileFromBitmapTask;
import com.rishi.frendzapp_core.utils.PrefsHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class BaseDialogMessagesAdapter extends BaseCursorAdapter implements ReceiveFileFromBitmapTask.ReceiveFileListener, StickyListHeadersAdapter {

    protected static int TYPE_REQUEST_MESSAGE = 0;
    protected static int TYPE_OWN_MESSAGE = 1;
    protected static int TYPE_OPPONENT_MESSAGE = 2;
    protected static int COMMON_TYPE_COUNT = 3;

    private final int colorMaxValue = 255;
    private final float colorAlpha = 0.8f;
    protected ChatUIHelperListener chatUIHelperListener;
    protected ImageUtils imageUtils;
    protected QBDialog dialog;
    private Random random;
    private static Map<Integer, Integer> colorsMap = new HashMap<Integer, Integer>();
    private FileHelper fileHelper;
    private Handler handler = new Handler();;
    private String filePath = "/mnt/sdcard/Frendzapp/";
    private MediaPlayer mp;

    public BaseDialogMessagesAdapter(Context context, Cursor cursor) {
        super(context, cursor, true);
        random = new Random();
        imageUtils = new ImageUtils((android.app.Activity) context);
        fileHelper = new FileHelper();
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = null;

        if (getCursor().getCount() > ConstsCore.ZERO_INT_VALUE) {
            cursor = (Cursor) getItem(position);
        }

        return getItemViewType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return ConstsCore.MESSAGES_TYPE_COUNT;
    }

    protected boolean isOwnMessage(int senderId) {
        return senderId == currentUser.getId();
    }

    protected void displayAttachImage(String attachUrl, final ViewHolder viewHolder) {
        ImageLoader.getInstance().displayImage(attachUrl, viewHolder.attachImageView,
                Consts.UIL_DEFAULT_DISPLAY_OPTIONS, new ImageLoadingListener(viewHolder),
                new SimpleImageLoadingProgressListener(viewHolder));
    }

    protected void showImage(String attachUrl, final ViewHolder viewHolder) {
        final String fileName = attachUrl.substring(attachUrl.lastIndexOf('/')+1, attachUrl.length() )+".jpeg";
        if (getImage()){

            if (fileHelper.checkExsistFile(fileName)){
                ImageLoader.getInstance().displayImage("file:///" + filePath + fileName, viewHolder.attachImageView,
                        Consts.UIL_DEFAULT_DISPLAY_OPTIONS, new ImageLoadingListener(viewHolder),
                        new SimpleImageLoadingProgressListener(viewHolder));
            }else{

                ImageLoader.getInstance().displayImage(attachUrl, viewHolder.attachImageView,
                        Consts.UIL_DEFAULT_DISPLAY_OPTIONS, new ImageLoadingListener(viewHolder),
                        new SimpleImageLoadingProgressListener(viewHolder));

            }

        }else {

            if (fileHelper.checkExsistFile(fileName)){
                ImageLoader.getInstance().displayImage("file:///" + filePath + fileName, viewHolder.attachImageView,
                        Consts.UIL_DEFAULT_DISPLAY_OPTIONS, new ImageLoadingListener(viewHolder),
                        new SimpleImageLoadingProgressListener(viewHolder));
            }else{

                System.out.println("InElse:"+fileName);

            }

        }
    }

    protected void showVideo(String attachUrl, final ViewHolder viewHolder) {
        final String fileName = attachUrl.substring(attachUrl.lastIndexOf('/')+1, attachUrl.length() )+".mp4";
        if (getVideo()){

            if (fileHelper.checkExsistFile(fileName)){
                ImageLoader.getInstance().displayImage("file:///" + filePath + fileName, viewHolder.attachImageView,
                        Consts.UIL_DEFAULT_DISPLAY_OPTIONS, new ImageLoadingListener(viewHolder),
                        new SimpleImageLoadingProgressListener(viewHolder));
            }else{

                MyTaskParams myTaskParams = new MyTaskParams(attachUrl,viewHolder,".mp4");
                new DownloadFileFromURL().execute(myTaskParams);

            }

        }else {

            if (fileHelper.checkExsistFile(fileName)){
                ImageLoader.getInstance().displayImage("file:///" + filePath + fileName, viewHolder.attachImageView,
                        Consts.UIL_DEFAULT_DISPLAY_OPTIONS, new ImageLoadingListener(viewHolder),
                        new SimpleImageLoadingProgressListener(viewHolder));
            }else{

                System.out.println("Please Download video");

            }

        }
    }

    protected void showAudio(String attachUrl, final ViewHolder viewHolder) {
        final String fileName = attachUrl.substring(attachUrl.lastIndexOf('/')+1, attachUrl.length() )+".mp3";
        if (getVideo()){

            if (fileHelper.checkExsistFile(fileName)){
                showAudioLayouts(viewHolder,fileName);
            }else{

                MyTaskParams myTaskParams = new MyTaskParams(attachUrl,viewHolder,".mp3");
                new DownloadFileFromURL().execute(myTaskParams);

            }

        }else {

            if (fileHelper.checkExsistFile(fileName)){
                showAudioLayouts(viewHolder,fileName);
            }else{

                System.out.println("Please Download video");

            }

        }
    }

    private static class MyTaskParams {
        String filePath;
        ViewHolder viewHolder;
        String fileType;

        MyTaskParams(String filePath, ViewHolder viewHolder,String fileType) {
            this.filePath = filePath;

             this.viewHolder = viewHolder;
            this.fileType = fileType;
        }
    }

    class DownloadFileFromURL extends AsyncTask<MyTaskParams, String, String> {

        private static final String folderName = "/Frendzapp";
        private String fileName;
        private String filePath;
        private String fileType;
        private ViewHolder viewHolder;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(MyTaskParams... params) {
            int count;

            viewHolder = params[0].viewHolder;
            filePath =  params[0].filePath;
            fileType =  params[0].fileType;
            fileName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.length() )+fileType;
            try {
                URL url = new URL(params[0].filePath);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + folderName+"/"+fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error:", e.getMessage());
            }

            return fileType;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            viewHolder.centeredProgressBar.setProgress(Integer.parseInt(values[0]));

        }

        @Override
        protected void onPostExecute(String fileType) {
            if (fileType.equals(".mp4")){
        ImageLoader.getInstance().displayImage("file:///" + "/mnt/sdcard/Frendzapp/" + fileName, viewHolder.attachImageView,
                Consts.UIL_DEFAULT_DISPLAY_OPTIONS, new BaseDialogMessagesAdapter.ImageLoadingListener(viewHolder),
                new BaseDialogMessagesAdapter.SimpleImageLoadingProgressListener(viewHolder));
            }else if (fileType.equals(".mp3")){
                showAudioLayouts(viewHolder,filePath);

            }

        }

    }

    private void showAudioLayouts(final ViewHolder viewHolder,final  String fileName){
        setViewVisibility(viewHolder.progressRelativeLayout, View.GONE);
        setViewVisibility(viewHolder.attachAudioRelativeLayout, View.VISIBLE);
        viewHolder.startMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp == null) {


                    try
                    {
                        mp=new MediaPlayer();

                        if (mp.isPlaying())
                            mp.reset();
                        mp.setDataSource("file:///" + "/mnt/sdcard/Frendzapp/" + fileName);
                        mp.prepare();
                        mp.start();
                        viewHolder.seekBar.setEnabled(true);


                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                if (mp.isPlaying())
                { ;
                    mp.pause();
                    //tartMedia.setText("play");
                    viewHolder.startMedia.setImageResource(R.drawable.play);

                }
                else
                {
                    mp.start();
                    //startMedia.setText("pause");
                    viewHolder.startMedia.setImageResource(R.drawable.pause);
                    viewHolder.seekBar.setMax(mp.getDuration());

                    Thread thread = new Thread() {
                        int currentPosition = mp.getCurrentPosition();
                        int total = mp.getDuration();
                        @Override
                        public void run() {
                            try {
                                while (mp != null && currentPosition < total) {
                                    try {
                                        Thread.sleep(1000);
                                        currentPosition = mp.getCurrentPosition();
                                    } catch (InterruptedException e) {
                                        return;
                                    } catch (Exception e) {
                                        return;
                                    }
                                    viewHolder.seekBar.setProgress(currentPosition);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    thread.start();
                }
            }
        });
    }


    class FileSize extends AsyncTask<MyTaskParams, Integer, String>
    {
        ViewHolder viewHolder;
        protected void onPreExecute (){
            Log.d("PreExceute","On pre Exceute......");
        }

        protected String doInBackground(MyTaskParams...arg0) {
            Log.d("DoINBackGround","On doInBackground...");
            try {
                viewHolder = arg0[0].viewHolder;
                URL url = new URL(arg0[0].filePath);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                return String.valueOf(urlConnection.getContentLength());
            }catch (Exception e){
                Log.d("FileSize",e.toString());
                return "0";
            }
        }


        protected void onPostExecute(String result) {
            Log.d("Size:",result);
            //viewHolder.messageTextView.setText(result);
        }
    }

    private boolean getImage() {
        PrefsHelper helper = new PrefsHelper(context);
        return helper.getPref(PrefsHelper.PREF_IMAGE, false);
    }

    private boolean getVideo() {
        PrefsHelper helper = new PrefsHelper(context);
        return helper.getPref(PrefsHelper.PREF_VIDEOS, false);
    }

    private boolean getAudio() {
        PrefsHelper helper = new PrefsHelper(context);
        return helper.getPref(PrefsHelper.PREF_AUDIO, false);
    }

    protected int getTextColor(Integer senderId) {
        if (colorsMap.get(senderId) != null) {
            return colorsMap.get(senderId);
        } else {
            int colorValue = getRandomColor();
            colorsMap.put(senderId, colorValue);
            return colorsMap.get(senderId);
        }
    }

    protected int getMessageStatusIconId(boolean isDelivered, boolean isRead) {
        int iconResourceId;
        if (/*isDelivered && */isRead) {
            iconResourceId = R.drawable.ic_status_mes_sent_received;
        } else if (isDelivered) {
            iconResourceId = R.drawable.ic_status_mes_sent;
        } else {
            iconResourceId = 0;
        }
        return iconResourceId;
    }

    protected void setMessageStatus(ImageView imageView, boolean messageDelivered, boolean messageRead) {
        imageView.setImageResource(getMessageStatusIconId(messageDelivered, messageRead));
    }

    protected void resetUI(ViewHolder viewHolder) {
        setViewVisibility(viewHolder.attachMessageRelativeLayout, View.GONE);
        setViewVisibility(viewHolder.progressRelativeLayout, View.GONE);
        setViewVisibility(viewHolder.textMessageView, View.GONE);
    }

    private int getRandomColor() {
        float[] hsv = new float[3];
        int color = Color.argb(colorMaxValue, random.nextInt(colorMaxValue), random.nextInt(colorMaxValue),
                random.nextInt(colorMaxValue));
        Color.colorToHSV(color, hsv);
        hsv[2] *= colorAlpha;
        color = Color.HSVToColor(hsv);
        return color;
    }

    private int getItemViewType(Cursor cursor) {
        int senderId = cursor.getInt(cursor.getColumnIndex(MessageTable.Cols.SENDER_ID));
        if (isOwnMessage(senderId)) {
            return ConstsCore.OWN_DIALOG_MESSAGE_TYPE;
        } else {
            return ConstsCore.OPPONENT_DIALOG_MESSAGE_TYPE;
        }
    }

    @Override
    public void onCachedImageFileReceived(File imageFile) {
    }

    @Override
    public void onAbsolutePathExtFileReceived(String absolutePath) {
        chatUIHelperListener.onScreenResetPossibilityPerformLogout(false);
        imageUtils.showFullImage((android.app.Activity) context, absolutePath);
    }

    protected void setViewVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        Cursor cursor = null;

        if (getCursor().getCount() > ConstsCore.ZERO_INT_VALUE) {
            cursor = (Cursor) getItem(position);
        }

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item_chat_sticky_header_date, parent, false);
            holder.headerTextView = (TextView) convertView.findViewById(R.id.header_date_textview);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        if (cursor != null) {
            long time = cursor.getLong(cursor.getColumnIndex(MessageTable.Cols.TIME));
            holder.headerTextView.setText(DateUtils.longToMessageListHeaderDate(time));
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        String timeString;

        if (getCursor().getCount() > ConstsCore.ZERO_INT_VALUE) {
            Cursor cursor = (Cursor) getItem(position);
            long time = cursor.getLong(cursor.getColumnIndex(MessageTable.Cols.TIME));
            timeString = DateUtils.longToMessageListHeaderDate(time);
        } else {
            return ConstsCore.ZERO_INT_VALUE;
        }

        if (!TextUtils.isEmpty(timeString)) {
            return timeString.subSequence(ConstsCore.ZERO_INT_VALUE, timeString.length() - 1).charAt(
                    ConstsCore.ZERO_INT_VALUE);
        } else {
            return ConstsCore.ZERO_INT_VALUE;
        }
    }

    private class HeaderViewHolder {

        TextView headerTextView;
    }

    public class ImageLoadingListener extends SimpleImageLoadingListener {

        private ViewHolder viewHolder;
        private Bitmap loadedImageBitmap;

        public ImageLoadingListener(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            super.onLoadingStarted(imageUri, view);
            viewHolder.verticalProgressBar.setProgress(ConstsCore.ZERO_INT_VALUE);
            viewHolder.centeredProgressBar.setProgress(ConstsCore.ZERO_INT_VALUE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            updateUIAfterLoading();
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedBitmap) {
            initMaskedImageView(loadedBitmap);
            fileHelper.checkExsistFile(imageUri, loadedBitmap);
            System.out.println("imageUri:"+imageUri);
        }

        private void initMaskedImageView(Bitmap loadedBitmap) {
            loadedImageBitmap = loadedBitmap;
            viewHolder.attachImageView.setOnClickListener(receiveImageFileOnClickListener());
            viewHolder.attachImageView.setImageBitmap(loadedImageBitmap);
            setViewVisibility(viewHolder.attachMessageRelativeLayout, View.VISIBLE);
            setViewVisibility(viewHolder.attachImageView, View.VISIBLE);

            updateUIAfterLoading();
//            chatUIHelperListener.onScrollMessagesToBottom();
        }

        private void updateUIAfterLoading() {
            if (viewHolder.progressRelativeLayout != null) {
                setViewVisibility(viewHolder.progressRelativeLayout, View.GONE);
            }
        }

        private View.OnClickListener receiveImageFileOnClickListener() {
            return new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    view.startAnimation(AnimationUtils.loadAnimation(context,
                            R.anim.chat_attached_file_click));
                    new ReceiveFileFromBitmapTask(BaseDialogMessagesAdapter.this).execute(imageUtils,
                            loadedImageBitmap, false);
                }
            };
        }
    }

    public class SimpleImageLoadingProgressListener implements ImageLoadingProgressListener {

        private ViewHolder viewHolder;

        public SimpleImageLoadingProgressListener(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            viewHolder.centeredProgressBar.setProgress(Math.round(100.0f * current / total));
        }
    }

    public class ViewHolder {

        public RoundedImageView avatarImageView;
        public TextView nameTextView;
        public View textMessageView;
        public ImageView messageDeliveryStatusImageView;
        public ImageView attachDeliveryStatusImageView;
        public RelativeLayout progressRelativeLayout;
        public RelativeLayout attachMessageRelativeLayout;
        public TextView messageTextView;
        public MaskedImageView attachImageView;
        public TextView timeTextMessageTextView;
        public TextView timeAttachMessageTextView;
        public ProgressBar verticalProgressBar;
        public RateTextCircularProgressBar centeredProgressBar;
        public ImageView acceptFriendImageView;
        public View dividerView;
        public ImageView rejectFriendImageView;
        public SeekBar seekBar;
        public ImageView startMedia;
        public RelativeLayout attachAudioRelativeLayout;

    }
}