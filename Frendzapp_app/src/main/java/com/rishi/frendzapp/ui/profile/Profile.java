package com.rishi.frendzapp.ui.profile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp_core.qb.helpers.QBAuthHelper;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp_core.utils.Utils;
import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.models.UserCustomData;
import com.rishi.frendzapp_core.qb.commands.QBUpdateUserCommand;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;
import com.rishi.frendzapp.ui.uihelper.SimpleTextWatcher;
import com.rishi.frendzapp_core.utils.DialogUtils;
import com.rishi.frendzapp.utils.ImageUtils;
import com.rishi.frendzapp.utils.KeyboardUtils;
import com.rishi.frendzapp.utils.ReceiveFileFromBitmapTask;
import com.rishi.frendzapp.utils.ReceiveUriScaledBitmapTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Profile extends BaseLogeableActivity implements ReceiveFileFromBitmapTask.ReceiveFileListener,
        View.OnClickListener, ReceiveUriScaledBitmapTask.ReceiveUriScaledBitmapListener, View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private View actionCancelView;
    private View actionDoneView;
    private View actionView;
    private Bitmap avatarBitmapCurrent;
    private ImageView avatarImageView;
    private ImageButton edit_email;
    private ImageButton edit_image;
    private ImageButton edit_name;
    private ImageButton edit_status;
    private ImageButton edit_audio_status;
    private String emailCurrent;
    private String emailOld;
    private EditText et_email;
    private EditText et_name;
    private EditText et_status;
    private TextView tv_mobile_no;
    private String mobile_no;

    private TextView txtCancel;
    private TextView txtSet;

    private String fullNameCurrent;
    private String fullNameOld;
    private ImageUtils imageUtils;
    private boolean isNeedUpdateAvatar;
    private boolean isNeedUpdateStatus;
    private ActionMode mActionMode;
    private Uri outputUri;
    private String statusCurrent;
    private String statusOld;
    private QBUser user;
    private UserCustomData userCustomData;
    private PopupWindow pwindo;

    private static MediaRecorder mediaRecorder;

    private static String audioFilePath;
    private static Button stopButton;
    private static Button playButton;
    private static Button recordButton;
    private boolean isRecording = false;
    private Timer timer = new Timer();
    private ProgressBar pb;
    private int MAX_DURATION=60000;
    private boolean stopped;
    private boolean plyed;
    private long mStartTime;
    private long endTime;
    private static final int MIN_INTERVAL_TIME = 700;

    private ImageButton buttonPlayPause;
    private SeekBar seekBarProgress;

    private MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class

    private final Handler handler = new Handler();


    public static void start(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        useDoubleBackPressed = false;
        user = AppSession.getSession().getUser();
        imageUtils = new ImageUtils(this);



        initUI();
        initListeners();
        initUIWithUsersData();
        initBroadcastActionList();
        initTextChangedListeners();
        updateOldUserData();
        hideAction();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initUI() {
        edit_image = _findViewById(R.id.edit_imaage);
        edit_name = _findViewById(R.id.edit_name);
        edit_status = _findViewById(R.id.edit_status);
        edit_audio_status = _findViewById(R.id.edit_audio_status);
        edit_email = _findViewById(R.id.edit_email);
        et_name = _findViewById(R.id.et_name);
        tv_mobile_no = _findViewById(R.id.txt_mobile_no);
        et_status = _findViewById(R.id.et_status);
        et_email = _findViewById(R.id.et_email);
        avatarImageView = _findViewById(R.id.avatarImageView);
        actionView = _findViewById(R.id.action_view);
        actionCancelView = _findViewById(R.id.action_cancel_view);
        actionDoneView = _findViewById(R.id.action_done_view);

        buttonPlayPause = _findViewById(R.id.ButtonTestPlayPause);
        buttonPlayPause.setOnClickListener(this);

        seekBarProgress = (SeekBar) findViewById(R.id.SeekBarTestPlay);
        seekBarProgress.setMax(99); // It means 100% .0-99
        seekBarProgress.setOnTouchListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    private void initListeners() {
        edit_image.setOnClickListener(this);
        edit_name.setOnClickListener(this);
        edit_status.setOnClickListener(this);
        edit_email.setOnClickListener(this);
        edit_audio_status.setOnClickListener(this);


        actionCancelView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetUserData();
                initUIWithUsersData();
                hideAction();
            }
        });

        actionDoneView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateUserData();
            }
        });

        et_status.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View view) {
                mActionMode = startActionMode(new ActionBarCallBack());
                return true;
            }
        });
    }

    private void initCustomData() {
        userCustomData = Utils.customDataToObject(user.getCustomData());

        if (userCustomData == null) {
            userCustomData = new UserCustomData();
        }
    }

    private void initUIWithUsersData() {
        initCustomData();
        loadAvatar();
        fullNameOld = user.getFullName();

        et_name.setText(fullNameOld);
        mobile_no = user.getLogin();
        tv_mobile_no.setText(mobile_no);
        statusOld = userCustomData.getStatus();
        et_status.setText(statusOld);
        emailOld = user.getEmail();
        et_email.setText(emailOld);
    }

    private void initBroadcastActionList() {
        addAction(QBServiceConsts.UPDATE_USER_SUCCESS_ACTION, new UpdateUserSuccessAction());
        addAction(QBServiceConsts.UPDATE_USER_FAIL_ACTION, new UpdateUserFailAction());
    }

    private void loadAvatar() {
        if (userCustomData != null && !TextUtils.isEmpty(userCustomData.getAvatar_url())) {
            ImageLoader.getInstance().displayImage(userCustomData.getAvatar_url(),
                    avatarImageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }

    private void initTextChangedListeners() {
        TextWatcherListener textwatcherlistener = new TextWatcherListener();
        et_name.addTextChangedListener(textwatcherlistener);
        et_email.addTextChangedListener(textwatcherlistener);
        et_status.addTextChangedListener(textwatcherlistener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar_imageview:
            case R.id.edit_imaage:
                changeAvatarOnClick();
                break;

            case R.id.et_name:
                changeFullNameOnClick();
                break;

            case R.id.edit_email:
                changeEmailOnClick();
                break;

            case R.id.edit_status:
                changeStatusOnClick();
                break;

            case R.id.edit_audio_status:
                changeAudioStatusOnClick();
                break;

            case R.id.ButtonTestPlayPause:
                System.out.println("audio: "+userCustomData.getAudio_status());
                playAudioStatus();
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        } else if (requestCode == ImageUtils.GALLERY_INTENT_CALLED && resultCode == RESULT_OK) {
            Uri originalUri = data.getData();
            if (originalUri != null) {
                showProgress();
                new ReceiveUriScaledBitmapTask(this).execute(imageUtils, originalUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            isNeedUpdateAvatar = true;
            avatarBitmapCurrent = imageUtils.getBitmap(outputUri);
            avatarImageView.setImageBitmap(avatarBitmapCurrent);
            showAction();
        } else if (resultCode == Crop.RESULT_ERROR) {
            DialogUtils.showLong(this, Crop.getError(result).getMessage());
        }
        canPerformLogout.set(true);
    }

    private void startCropActivity(Uri originalUri) {
        outputUri = Uri.fromFile(new File(getCacheDir(), Crop.class.getName()));
        new Crop(originalUri).output(outputUri).asSquare().start(this);
    }

    @Override
    public void onUriScaledBitmapReceived(Uri originalUri) {
        hideProgress();
        startCropActivity(originalUri);
    }

    public void changeAvatarOnClick() {
        canPerformLogout.set(false);
        imageUtils.getImage();
    }

    public void changeFullNameOnClick() {
        initChangingEditText(et_name);
    }

    private void initChangingEditText(EditText editText) {
        editText.setEnabled(true);
        editText.requestFocus();
        KeyboardUtils.showKeyboard(this);
    }

    private void stopChangingEditText(EditText editText) {
        editText.setEnabled(false);
        KeyboardUtils.hideKeyboard(this);
    }

    public void changeEmailOnClick() {
        initChangingEditText(et_email);
    }
    public void changeStatusOnClick() {
        initChangingEditText(et_status);
    }

    public void changeAudioStatusOnClick() {
        //initChangingEditText(et_stat
        initiatePopupWindow();
    }

    public void playAudioStatus(){
        try {
            mediaPlayer.setDataSource(userCustomData.getAudio_status()); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
            mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            buttonPlayPause.setImageResource(R.drawable.button_pause);
        } else {
            mediaPlayer.pause();
            buttonPlayPause.setImageResource(R.drawable.button_play);
        }

        primarySeekBarProgressUpdater();
    }
    private void primarySeekBarProgressUpdater() {
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
        else
        {
            //stopPlaying();
        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.SeekBarTestPlay) {
            if (mediaPlayer.isPlaying()) {
                SeekBar sb = (SeekBar) v;
                int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                mediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        buttonPlayPause.setImageResource(R.drawable.button_play);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBarProgress.setSecondaryProgress(percent);
    }

    @Override
    public void onCachedImageFileReceived(File imageFile) {
        QBUser newUser = createUserForUpdating();
        QBUpdateUserCommand.start(this, newUser, imageFile);
    }

    @Override
    public void onAbsolutePathExtFileReceived(String absolutePath) {
    }

    private void updateCurrentUserData() {
        fullNameCurrent = et_name.getText().toString();
        emailCurrent = et_email.getText().toString();
        statusCurrent = et_status.getText().toString();
    }

    private void updateUserData() {
        updateCurrentUserData();
        if (isUserDataChanged()) {
            saveChanges();
        }
    }

    private boolean isUserDataChanged() {
        return isNeedUpdateAvatar || !fullNameCurrent.equals(fullNameOld) || !emailCurrent.equals(emailOld) || !statusCurrent.equals(statusOld);
    }

    private void saveChanges() {
        if (!isUserDataCorrect()) {
            DialogUtils.showLong(this, getString(R.string.dlg_not_all_fields_entered));
            return;
        }

        showProgress();

        QBUser newUser = createUserForUpdating();

        if (isNeedUpdateAvatar) {
            new ReceiveFileFromBitmapTask(this).execute(imageUtils, avatarBitmapCurrent, true);
        } else {
            QBUpdateUserCommand.start(this, newUser, null);
        }

        stopChangingEditText(et_name);
        stopChangingEditText(et_status);
        stopChangingEditText(et_email);
    }

    private QBUser createUserForUpdating() {
        QBUser newUser = new QBUser();
        newUser.setId(user.getId());

        if (isFieldValueChanged(fullNameCurrent, fullNameOld)) {
            user.setFullName(fullNameCurrent);
            newUser.setFullName(fullNameCurrent);
        }

        if (isFieldValueChanged(emailCurrent, emailOld)) {
            user.setEmail(emailCurrent);
            newUser.setEmail(emailCurrent);
        }

        if (isFieldValueChanged(statusCurrent, statusOld) || isNeedUpdateAvatar) {
            userCustomData.setStatus(statusCurrent);
            user.setCustomData(Utils.customDataToString(userCustomData));
        }

        newUser.setCustomData(Utils.customDataToString(userCustomData));

        return newUser;
    }

    private boolean isFieldValueChanged(String fieldValue, String oldFieldValue) {
        return !fieldValue.equals(oldFieldValue);
    }

    private boolean isUserDataCorrect() {
        return fullNameCurrent.length() > ConstsCore.ZERO_INT_VALUE;
    }

    private void updateOldUserData() {
        fullNameOld = et_name.getText().toString();
        emailOld = et_email.getText().toString();
        statusOld = et_status.getText().toString();
        isNeedUpdateAvatar = false;
    }

    private void resetUserData() {
        user.setFullName(fullNameOld);
        initCustomData();

        isNeedUpdateAvatar = false;
    }

    private boolean isActionViewVisible() {
        return actionView.getVisibility() == View.VISIBLE;
    }

    private void showAction() {
        actionView.setVisibility(View.VISIBLE);
    }

    private void hideAction() {
        actionView.setVisibility(View.GONE);
    }

    private class TextWatcherListener extends SimpleTextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isActionViewVisible()) {
                showAction();
            }
        }
    }

    public class UpdateUserFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Exception exception = (Exception) bundle.getSerializable(QBServiceConsts.EXTRA_ERROR);
            DialogUtils.showLong(Profile.this, exception.getMessage());
            resetUserData();
            hideProgress();
        }
    }

    private class UpdateUserSuccessAction implements Command {
        @Override
        public void execute(Bundle bundle) {
            QBUser user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
            AppSession.getSession().updateUser(user);
            updateOldUserData();
            hideAction();
            hideProgress();
        }
    }

    private String getText(){
        String msg = et_status.getText().toString();
        return  msg;
    }

    class ActionBarCallBack implements ActionMode.Callback {
        public ActionBarCallBack(){
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            switch (item.getItemId()) {
                case R.id.select_all:
                    if (et_status.getText().length()>0) {
                        et_status.setSelectAllOnFocus(true);
                        et_status.setSelection(0);
                        et_status.selectAll();
                    }
                    return true;

                case R.id.copy:
                    if (et_status.getText().length()>0) {
                        ClipboardManager cManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData cData = ClipData.newPlainText("text", getText());
                        cManager.setPrimaryClip(cData);
                        et_status.setSelection(et_status.getText().length());
                        et_status.setSelectAllOnFocus(false);
                        mode.finish();
                    }
                    return true;

                case R.id.cut:
                    if (et_status.getText().length()>0) {
                        ClipboardManager cManager1 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData cData1 = ClipData.newPlainText("text", getText());
                        cManager1.setPrimaryClip(cData1);
                        et_status.setText("");
                        et_status.setSelectAllOnFocus(false);
                        mode.finish();
                    }
                    return true;

                case R.id.paste:
                    ClipboardManager clipMan = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    et_status.setText(clipMan.getText());
                    et_status.setSelectAllOnFocus(false);
                    return true;
            }
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.getMenuInflater().inflate(R.menu.select_cut_copy_paste, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub

            return false;
        }

    }

    private void initiatePopupWindow(){
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater)Profile.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_audio_status,
                    (ViewGroup) findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout,ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            recordButton = (Button) layout.findViewById(R.id.start);
            stopButton = (Button) layout.findViewById(R.id.stop);
            playButton = (Button) layout.findViewById(R.id.play);
            txtCancel = (TextView) layout.findViewById(R.id.txt_cancel);
            txtSet = (TextView) layout.findViewById(R.id.txt_set);
            pb =(ProgressBar) layout.findViewById(R.id.progressBar);
            audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/myaudiostatus.3gp";

            txtCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    isNeedUpdateStatus = false;
                }
            });

            txtSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNeedUpdateStatus){
                        final File audioStatus = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"myaudiostatus.3gp");
                        if (audioStatus==null){
                            Toast.makeText(Profile.this,"File is not present",Toast.LENGTH_SHORT).show();
                            pwindo.dismiss();
                        }else {
                            //Toast.makeText(Profile.this,audioFilePath+" From else",Toast.LENGTH_SHORT).show();
                            pwindo.dismiss();
                            showProgress();
                            try {
                                Thread thread = new Thread(new Runnable(){
                                    @Override
                                    public void run() {
                                        try {

                                            QBAuthHelper authHelper = new QBAuthHelper(getApplicationContext());
                                            String url= authHelper.upLoadAudioStatus(audioStatus);
                                            QBUser newUser = createUserForUpdating(url);
                                            QBUpdateUserCommand.start(Profile.this, newUser, null);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                thread.start();

                            }catch (Exception e){
                                Log.d("Audio Update Error:", e.toString());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgress();
                                        Toast.makeText(Profile.this,"Cannot Update voice status",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            hideProgress();

                        }
                    }else {
                        Toast.makeText(Profile.this,"Please recored audio",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Profile",e.toString());
        }
    }
    private QBUser createUserForUpdating(String url) {
        QBUser newUser = new QBUser();
        newUser.setId(user.getId());
//        newUser.setEmail(user.getEmail());
//        newUser.setLogin(user.getLogin());
//        newUser.setFullName(user.getFullName());
        userCustomData.setAudio_status(url);

        user.setCustomData(Utils.customDataToString(userCustomData));

        newUser.setCustomData(Utils.customDataToString(userCustomData));

        return newUser;
    }

    public void recordAudio (View view) throws IOException
    {
        mStartTime = System.currentTimeMillis();
        isRecording = true;
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopped=false;

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setMaxDuration(MAX_DURATION);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pb.setMax(MAX_DURATION); //supposing u want to give maximum length of 60 seconds
        pb.setProgress(0);
        startProgress();
        mediaRecorder.start();
    }

    public void stopClicked (View view) {
        long intervalTime = System.currentTimeMillis() - mStartTime;
        endTime = intervalTime;
        System.out.println("onClicked-end time:" + endTime);
        stopButton.setEnabled(false);
        playButton.setEnabled(true);
        pb.setProgress(0);
        stopped=true;
        isNeedUpdateStatus = true;


        if (isRecording) {
            recordButton.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
            plyed = false;
        }
        if (intervalTime < MIN_INTERVAL_TIME) {
            //AppContext.showToastShort(R.string.record_sound_short);
            // File file = new File(audioFilePath);
            // file.delete();
            //return;
            Toast.makeText(Profile.this, "record sound short", Toast.LENGTH_SHORT).show();
        }
    }


    public void playAudio (View view) throws IOException {
        pb.setMax((int) endTime); //supposing u want to give maximum length of 60 seconds
        pb.setProgress(0);
        startPlayProgress();
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    protected void addRecordingToMediaLibrary() {
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + "myaudio");
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.DATA, audioFilePath);
        ContentResolver contentResolver = getContentResolver();

        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();
    }

    void startProgress()
    {

        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (!stopped)  // call ui only when  the progress is not stopped
                {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {

                                pb.setProgress(pb.getProgress() + 1000);
                                if (pb.getProgress() == MAX_DURATION) {
                                    recordButton.setEnabled(false);
                                    mediaRecorder.stop();
                                    mediaRecorder.release();
                                    mediaRecorder = null;
                                    isRecording = false;
                                    stopButton.setEnabled(false);
                                    playButton.setEnabled(true);
                                    pb.setProgress(0);
                                    stopped = true;
                                    plyed = false;
                                    endTime = MAX_DURATION;

                                }

                            } catch (Exception e) {
                            }


                        }
                    });
                }
            }


        }, 1, 1000);


    }

    void startPlayProgress()
    {
        // final long startTime = System.currentTimeMillis();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (!plyed)  // call ui only when  the progress is not stopped
                {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {

                                pb.setProgress(pb.getProgress() + 1000);
                                if (pb.getProgress() == endTime) {
                                    plyed = true;
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                    recordButton.setEnabled(true);
                                    stopButton.setEnabled(false);
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
                }
            }
        }, 1, 1000);
    }


//    public void stopPlaying() {
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.pause();
//
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//
//
//    }

    @Override
    public void onBackPressed() {

//      // stopPlaying();
//        if(mediaPlayer!=null && mediaPlayer.isPlaying())
//        {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }else
//        {
//            //show a message or something
//        }
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        //Toast.makeText(Profile.this,"You clicked on back",Toast.LENGTH_SHORT).show();

    }




}