package com.rishi.frendzapp.ui.friends;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.exception.QBResponseException;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;
import com.rishi.frendzapp.ui.chats.PrivateDialogActivity;
import com.rishi.frendzapp.ui.dialogs.AlertDialog;
import com.rishi.frendzapp.ui.mediacall.CallActivity;
import com.rishi.frendzapp.ui.views.RoundedImageView;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.db.managers.ChatDatabaseManager;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.models.User;
import com.rishi.frendzapp_core.qb.commands.QBDeleteDialogCommand;
import com.rishi.frendzapp_core.qb.commands.QBReloginCommand;
import com.rishi.frendzapp_core.qb.commands.QBRemoveFriendCommand;
import com.rishi.frendzapp_core.qb.helpers.QBPrivateChatHelper;
import com.rishi.frendzapp_core.service.QBService;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.DialogUtils;
import com.rishi.frendzapp_core.utils.ErrorUtils;
import com.quickblox.videochat.webrtc.QBRTCTypes;

public class FriendDetails extends BaseLogeableActivity implements View.OnClickListener,View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private ImageView avatarImageView;
    private TextView dobTextView;
    private View dobView;
    private TextView emailTextView;
    private View emailView;
    private View audioStatusView;
    private Cursor friendCursor;
    private TextView nameTextView;
    private ImageView onlineImageView;
    private TextView onlineStatusTextView;
    private TextView phoneTextView;
    private QBPrivateChatHelper privateChatHelper;
    private ContentObserver statusContentObserver;
    private TextView statusTextView;
    private ImageButton buttonPlayPause;
    private SeekBar seekBarProgress;
    private MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds;
    private final Handler handler = new Handler();
    private User user;
    private RelativeLayout videoCallButton;
    private RelativeLayout voiceCallButton;


    public static void start(Context context, int friendId) {
        Intent intent = new Intent(context, FriendDetails.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIEND_ID, friendId);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_demo);
        canPerformLogout.set(true);
        int friendId = getIntent().getExtras().getInt(QBServiceConsts.EXTRA_FRIEND_ID);
        friendCursor = UsersDatabaseManager.getFriendCursorById(this, friendId);
        user = UsersDatabaseManager.getUserById(this, friendId);
        initUI();
        registerStatusChangingObserver();
        initUIWithFriendsData();
        initBroadcastActionList();
    }

    private void initUI() {
        avatarImageView = _findViewById(R.id.avatarImageView);
        nameTextView = _findViewById(R.id.txt_name);
        statusTextView = _findViewById(R.id.txt_status);
        onlineImageView = _findViewById(R.id.online_imageview);
        onlineStatusTextView = _findViewById(R.id.online_status_textview);
        emailTextView = _findViewById(R.id.txt_email);
        emailView = _findViewById(R.id.linearLayout_email);
        audioStatusView = _findViewById(R.id.linearLayout_audio_status);
        dobTextView = _findViewById(R.id.txt_dob);
        dobView = _findViewById(R.id.linearLayout_dob);
        phoneTextView = _findViewById(R.id.txt_number);
        //initButtons
        videoCallButton = _findViewById(R.id.video_call_button);
        voiceCallButton = _findViewById(R.id.voice_call_button);
        buttonPlayPause = _findViewById(R.id.ButtonTestPlayPause);
        buttonPlayPause.setOnClickListener(this);

        seekBarProgress = (SeekBar) findViewById(R.id.SeekBarTestPlay);
        seekBarProgress.setMax(99); // It means 100% .0-99
        seekBarProgress.setOnTouchListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

    }

    private void registerStatusChangingObserver() {
        statusContentObserver = new ContentObserver(new Handler()) {

            @Override
            public boolean deliverSelfNotifications() {
                return true;
            }

            @Override
            public void onChange(boolean selfChange) {
                if (FriendDetails.this.user != null) {
                    user = UsersDatabaseManager.getUserById(FriendDetails.this,
                            FriendDetails.this.user.getUserId());
                    setOnlineStatus(user);
                }
            }
        };
        friendCursor.registerContentObserver(statusContentObserver);
    }

    private void unregisterStatusChangingObserver() {
        friendCursor.unregisterContentObserver(statusContentObserver);
    }

    private void initBroadcastActionList() {
        addAction(QBServiceConsts.REMOVE_FRIEND_SUCCESS_ACTION, new RemoveFriendSuccessAction());
        addAction(QBServiceConsts.REMOVE_FRIEND_FAIL_ACTION, failAction);
        addAction(QBServiceConsts.GET_FILE_FAIL_ACTION, failAction);
        addAction(QBServiceConsts.GET_FILE_FAIL_ACTION, failAction);
    }

    private void initUIWithFriendsData() {
        loadAvatar();
        setName();
        setOnlineStatus(user);
        setStatus();
        setAudioStatus();
        setEmail();
        setDob();
        setPhone();
    }

    private void setStatus() {
        if (!TextUtils.isEmpty(user.getStatus())) {
            statusTextView.setText(user.getStatus());
        }
    }

    private void setAudioStatus() {
            System.out.println("Audio:"+user.getAudio_status());
        if (!TextUtils.isEmpty(user.getAudio_status())) {
            System.out.println("VISIBLE");
            audioStatusView.setVisibility(View.VISIBLE);
        } else {
            audioStatusView.setVisibility(View.GONE);
            System.out.println("GONE");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ButtonTestPlayPause:
                if (!TextUtils.isEmpty(user.getAudio_status())) {
                    playAudioStatus();
                }else {
                    Toast.makeText(FriendDetails.this, "NO Present voice Status", Toast.LENGTH_SHORT).show();
                }
                System.out.println("audio: " + user.getAudio_status());
                break;
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
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);

        if (privateChatHelper == null) {
            privateChatHelper = (QBPrivateChatHelper) service.getHelper(QBService.PRIVATE_CHAT_HELPER);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterStatusChangingObserver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.friend_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                showRemoveUserDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void playAudioStatus(){
        try {
            mediaPlayer.setDataSource(user.getAudio_status()); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
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
    }

    private void setName() {
        nameTextView.setText(user.getFullName());
    }

    private void setPhone() {
        phoneTextView.setText(user.getLogin());
    }

    private void setEmail() {
        if (user.getEmail() != null) {
            emailView.setVisibility(View.VISIBLE);
        } else {
            emailView.setVisibility(View.GONE);
        }
        emailTextView.setText(user.getEmail());
    }

    private void setDob() {
        if (user.getDob() != null) {
            dobView.setVisibility(View.VISIBLE);
        } else {
            dobView.setVisibility(View.GONE);
        }
        dobTextView.setText(user.getDob());
    }


    private void setOnlineStatus(User user) {
        if (user != null) {
            if (user.isOnline()) {
                onlineImageView.setVisibility(View.VISIBLE);
            } else {
                onlineImageView.setVisibility(View.GONE);
            }
            onlineStatusTextView.setText(user.getOnlineStatus(this));
        }
    }

    private void loadAvatar() {
        String url = user.getAvatarUrl();
        ImageLoader.getInstance().displayImage(url, avatarImageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void showRemoveUserDialog() {
        AlertDialog alertDialog = AlertDialog.newInstance(getResources().getString(
                R.string.frd_dlg_remove_friend, user.getFullName()));
        alertDialog.setPositiveButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgress();
                QBRemoveFriendCommand.start(FriendDetails.this, user.getUserId());
            }
        });
        alertDialog.setNegativeButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show(getFragmentManager(), null);
    }

    public void videoCallClickListener(View view) {
        if (isConnectionEnabled()) {
            callToUser(user, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.feature_unavailable), Toast.LENGTH_SHORT).show();
        }
    }

    private void callToUser(User friend, QBRTCTypes.QBConferenceType callType) {
        if (friend.getUserId() != AppSession.getSession().getUser().getId()) {
            if (checkFriendStatus(friend.getUserId())) {
                if(QBChatService.getInstance().isLoggedIn()) {
                    CallActivity.start(FriendDetails.this, friend, callType);
                }else {
                    QBReloginCommand.start(FriendDetails.this);
//                    Toast.makeText(this, getString(R.string.dlg_fail_connection), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void voiceCallClickListener(View view) {
        if (isConnectionEnabled()) {
            callToUser(user, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.feature_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkFriendStatus(int userId) {
        boolean isFriend = UsersDatabaseManager.isFriendInBase(this, userId);
        if (isFriend) {
            return true;
        } else {
            DialogUtils.showLong(this, getResources().getString(R.string.dlg_user_is_not_friend));
            return false;
        }
    }

    public void chatClickListener(View view) {
        if (checkFriendStatus(user.getUserId())) {
            try {
                QBDialog existingPrivateDialog = privateChatHelper.createPrivateDialogIfNotExist(
                        user.getUserId());
                PrivateDialogActivity.start(FriendDetails.this, user, existingPrivateDialog,null);
            } catch (QBResponseException e) {
                ErrorUtils.showError(this, e);
            }
        }
    }

    private void deleteDialog() {
        String dialogId = ChatDatabaseManager.getPrivateDialogIdByOpponentId(this, user.getUserId());
        QBDeleteDialogCommand.start(this, dialogId, QBDialogType.PRIVATE);
    }

    private class RemoveFriendSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            deleteDialog();
            DialogUtils.showLong(FriendDetails.this, getString(R.string.dlg_friend_removed,
                    user.getFullName()));
            finish();
        }
    }

    @Override
    public void onConnectionChange(boolean isConnected) {
        super.onConnectionChange(isConnected);
    }
}