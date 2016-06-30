package com.rishi.frendzapp.ui.chats;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.utils.RateTextCircularProgressBar;
import com.rishi.frendzapp_core.db.managers.ChatDatabaseManager;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.models.MessagesNotificationType;
import com.rishi.frendzapp_core.models.MessageCache;
import com.rishi.frendzapp_core.qb.commands.QBUpdateStatusMessageCommand;
import com.rishi.frendzapp.ui.chats.emoji.EmojiTextView;
import com.rishi.frendzapp.ui.views.MaskedImageView;
import com.rishi.frendzapp.utils.DateUtils;
import com.rishi.frendzapp_core.utils.ConstsCore;

public class PrivateDialogMessagesAdapter extends BaseDialogMessagesAdapter {

    private static final String TAG = PrivateDialogMessagesAdapter.class.getSimpleName();

    private static int EMPTY_POSITION = -1;

    private int lastRequestPosition = EMPTY_POSITION;
    private int lastInfoRequestPosition = EMPTY_POSITION;
    private PrivateDialogActivity.FriendOperationListener friendOperationListener;
    private ActionMode mActionMode;
    private Activity myaAtivity;

    public PrivateDialogMessagesAdapter(Context context,
            PrivateDialogActivity.FriendOperationListener friendOperationListener, Cursor cursor,
            ChatUIHelperListener chatUIHelperListener, QBDialog dialog) {
        super(context, cursor);
        this.friendOperationListener = friendOperationListener;
        this.chatUIHelperListener = chatUIHelperListener;
        this.dialog = dialog;
        this.myaAtivity = (Activity)context;
    }

    private int getItemViewType(Cursor cursor) {
        MessageCache messageCache = ChatDatabaseManager.getMessageCacheFromCursor(cursor);
        boolean ownMessage = isOwnMessage(messageCache.getSenderId());
        boolean friendsRequestMessage = messageCache.getMessagesNotificationType() != null;

        if (!friendsRequestMessage) {
            if (ownMessage) {
                return TYPE_OWN_MESSAGE;
            } else {
                return TYPE_OPPONENT_MESSAGE;
            }
        } else {
            return TYPE_REQUEST_MESSAGE;
        }
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
        return COMMON_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();

        MessageCache messageCache = ChatDatabaseManager.getMessageCacheFromCursor(cursor);
        boolean ownMessage = isOwnMessage(messageCache.getSenderId());

        if (messageCache.getMessagesNotificationType() == null) {
            if (ownMessage) {
                view = layoutInflater.inflate(R.layout.list_item_message_own, null, true);
            } else {
                view = layoutInflater.inflate(R.layout.list_item_private_message_opponent, null, true);
            }

            viewHolder.attachMessageRelativeLayout = (RelativeLayout) view.findViewById(
                    R.id.attach_message_relativelayout);
            viewHolder.attachAudioRelativeLayout = (RelativeLayout) view.findViewById(
                    R.id.attach_audio_relativelayout);
            viewHolder.timeAttachMessageTextView = (TextView) view.findViewById(
                    R.id.time_attach_message_textview);
            viewHolder.attachDeliveryStatusImageView = (ImageView) view.findViewById(R.id.attach_message_delivery_status_imageview);
            viewHolder.progressRelativeLayout = (RelativeLayout) view.findViewById(
                    R.id.progress_relativelayout);
            viewHolder.textMessageView = view.findViewById(R.id.text_message_view);
            viewHolder.messageTextView = (EmojiTextView) view.findViewById(R.id.message_textview);
            viewHolder.attachImageView = (MaskedImageView) view.findViewById(R.id.attach_imageview);
            viewHolder.timeTextMessageTextView = (TextView) view.findViewById(
                    R.id.time_text_message_textview);
            viewHolder.verticalProgressBar = (ProgressBar) view.findViewById(R.id.vertical_progressbar);
            viewHolder.verticalProgressBar.setProgressDrawable(context.getResources().getDrawable(
                    R.drawable.vertical_progressbar));
            viewHolder.centeredProgressBar = (RateTextCircularProgressBar) view.findViewById(R.id.centered_progressbar);
            viewHolder.messageDeliveryStatusImageView = (ImageView) view.findViewById(R.id.text_message_delivery_status_imageview);
            viewHolder.seekBar = (SeekBar) view.findViewById(R.id.seekBar1);
            viewHolder.startMedia = (ImageView) view.findViewById(R.id.button1);
        } else {
            view = layoutInflater.inflate(R.layout.list_item_friends_notification_message, null, true);
            viewHolder.textMessageView = view.findViewById(R.id.text_message_view);
            viewHolder.messageTextView = (EmojiTextView) view.findViewById(R.id.message_textview);
            viewHolder.timeTextMessageTextView = (TextView) view.findViewById(
                    R.id.time_text_message_textview);
            viewHolder.acceptFriendImageView = (ImageView) view.findViewById(R.id.accept_friend_imagebutton);
            viewHolder.dividerView = view.findViewById(R.id.divider_view);
            viewHolder.rejectFriendImageView = (ImageView) view.findViewById(R.id.reject_friend_imagebutton);
        }

        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        MessageCache messageCache = ChatDatabaseManager.getMessageCacheFromCursor(cursor);

        boolean ownMessage = isOwnMessage(messageCache.getSenderId());
        boolean friendsRequestMessage = MessagesNotificationType.FRIENDS_REQUEST.equals(
                messageCache.getMessagesNotificationType());
        boolean friendsInfoRequestMessage = messageCache
                .getMessagesNotificationType() != null && !friendsRequestMessage;

        if (friendsRequestMessage) {
            viewHolder.messageTextView.setText(messageCache.getMessage());
            viewHolder.timeTextMessageTextView.setText(DateUtils.longToMessageDate(messageCache.getTime()));

            setVisibilityFriendsActions(viewHolder, View.GONE);
        } else if (friendsInfoRequestMessage) {
            viewHolder.messageTextView.setText(messageCache.getMessage());
            viewHolder.timeTextMessageTextView.setText(DateUtils.longToMessageDate(messageCache.getTime()));

            setVisibilityFriendsActions(viewHolder, View.GONE);

            lastInfoRequestPosition = cursor.getPosition();
        } else if (!TextUtils.isEmpty(messageCache.getAttachUrl())) {
            resetUI(viewHolder);

            setViewVisibility(viewHolder.progressRelativeLayout, View.VISIBLE);
            viewHolder.timeAttachMessageTextView.setText(DateUtils.longToMessageDate(messageCache.getTime()));

            if (ownMessage) {
                setMessageStatus(viewHolder.attachDeliveryStatusImageView, messageCache.isDelivered(),
                        messageCache.isRead());
            }
            if (messageCache.getMessage().equals(context.getString(R.string.dlg_attached_last_message))){
                showImage(messageCache.getAttachUrl(), viewHolder);
            }else if (messageCache.getMessage().equals(context.getString(R.string.dlg_attached_video_last_message))) {
                //displayAttachImage(messageCache.getAttachUrl(), viewHolder, messageCache.getMessage());
                showVideo(messageCache.getAttachUrl(), viewHolder);
            }else if (messageCache.getMessage().equals(context.getString(R.string.dlg_attached_audio_last_message))){
                showAudio(messageCache.getAttachUrl(), viewHolder);
            }

        } else {
            resetUI(viewHolder);

            setViewVisibility(viewHolder.textMessageView, View.VISIBLE);
            viewHolder.messageTextView.setText(messageCache.getMessage());
            viewHolder.timeTextMessageTextView.setText(DateUtils.longToMessageDate(messageCache.getTime()));

            if (ownMessage) {
                setMessageStatus(viewHolder.messageDeliveryStatusImageView, messageCache.isDelivered(),
                        messageCache.isRead());
            }
        }

        if (!messageCache.isRead() && !ownMessage) {
            messageCache.setRead(true);
            QBUpdateStatusMessageCommand.start(context, dialog, messageCache, true);
        }

        // check if last message is request message
        boolean lastRequestMessage = cursor.getPosition() == cursor.getCount() - 1 && friendsRequestMessage;
        if (lastRequestMessage) {
            findLastFriendsRequestForCursor(cursor);
        }

        // check if friend was rejected/deleted.
        if (lastRequestPosition != EMPTY_POSITION && lastRequestPosition < lastInfoRequestPosition) {
            lastRequestPosition = EMPTY_POSITION;
        } else if ((lastRequestPosition != EMPTY_POSITION && lastRequestPosition == cursor.getPosition())) { // set visible friends actions
            setVisibilityFriendsActions(viewHolder, View.VISIBLE);
            initListeners(viewHolder, messageCache.getSenderId());
        }
        viewHolder.textMessageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mActionMode = myaAtivity.startActionMode(new ActionBarCallBack(viewHolder));
              /*  ClipboardManager cManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", getText(viewHolder));
                cManager.setPrimaryClip(cData);
                Toast.makeText(context, "Coped", Toast.LENGTH_SHORT);*/
                // AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                return true;
            }
        });

    }

    public void clearLastRequestMessagePosition() {
        lastRequestPosition = EMPTY_POSITION;
    }

    public void findLastFriendsRequestMessagesPosition() {
        new FindLastFriendsRequestThread().run();
    }

    private void setVisibilityFriendsActions(ViewHolder viewHolder, int visibility) {
        setViewVisibility(viewHolder.acceptFriendImageView, visibility);
        setViewVisibility(viewHolder.dividerView, visibility);
        setViewVisibility(viewHolder.rejectFriendImageView, visibility);
    }

    private void initListeners(final ViewHolder viewHolder, final int userId) {
        viewHolder.acceptFriendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendOperationListener.onAcceptUserClicked(userId);
            }
        });

        viewHolder.rejectFriendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendOperationListener.onRejectUserClicked(userId);
            }
        });


    }

    private String getText(ViewHolder viewHolder){
        String msg = viewHolder.messageTextView.getText().toString();
        return  msg;
    }

    private void findLastFriendsRequest() {
        Cursor cursor = getCursor();
            if(cursor.moveToFirst()){
                do{
                    findLastFriendsRequestForCursor(cursor);
                }while (cursor.moveToNext());
            }
    }

    private void findLastFriendsRequestForCursor(Cursor cursor) {
        boolean ownMessage;
        boolean friendsRequestMessage;
        boolean isFriend;

        MessageCache messageCache = ChatDatabaseManager.getMessageCacheFromCursor(cursor);
        if (messageCache.getMessagesNotificationType() != null) {
            ownMessage = isOwnMessage(messageCache.getSenderId());
            friendsRequestMessage = MessagesNotificationType.FRIENDS_REQUEST.equals(
                    messageCache.getMessagesNotificationType());

            if (friendsRequestMessage && !ownMessage) {
                isFriend = UsersDatabaseManager.isFriendInBase(context, messageCache.getSenderId());
                if (!isFriend) {
                    lastRequestPosition = cursor.getPosition();
                }
            }
        }
    }

    private class FindLastFriendsRequestThread extends Thread {

        @Override
        public void run() {
            findLastFriendsRequest();
        }
    }

    class ActionBarCallBack implements ActionMode.Callback {
        ViewHolder viewHolder;
        public ActionBarCallBack(ViewHolder viewHolder){
            this.viewHolder = viewHolder;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            switch (item.getItemId()) {

         /*       case R.id.select_all:
                    System.out.println("Clickd:Select All");
                    return true;
*/
                case R.id.copy:
                    System.out.println("Clickd:Copy");
                    ClipboardManager cManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData cData = ClipData.newPlainText("text", getText(viewHolder));
                    cManager.setPrimaryClip(cData);
                    mode.finish();
                    return true;
/*
                case R.id.cut:
                    System.out.println("Clickd:Cut");
                    return true;

                case R.id.paste:
                    System.out.println("Clickd:Paste");
                    return true;
 */           }
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.getMenuInflater().inflate(R.menu.copy_menu, menu);
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
}