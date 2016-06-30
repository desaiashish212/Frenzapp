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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.utils.RateTextCircularProgressBar;
import com.rishi.frendzapp_core.db.managers.ChatDatabaseManager;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.models.MessageCache;
import com.rishi.frendzapp_core.models.User;
import com.rishi.frendzapp.ui.chats.emoji.EmojiTextView;
import com.rishi.frendzapp.ui.views.MaskedImageView;
import com.rishi.frendzapp.ui.views.RoundedImageView;
import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp.utils.DateUtils;

public class GroupDialogMessagesAdapter extends BaseDialogMessagesAdapter {
    private ActionMode mActionMode;
    private Activity myaAtivity;
    public GroupDialogMessagesAdapter(Context context, Cursor cursor,
            ChatUIHelperListener chatUIHelperListener, QBDialog dialog) {
        super(context, cursor);
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
                view = layoutInflater.inflate(R.layout.list_item_group_message_opponent, null, true);
                viewHolder.avatarImageView = (RoundedImageView) view.findViewById(R.id.avatar_imageview);
                setViewVisibility(viewHolder.avatarImageView, View.VISIBLE);
                viewHolder.nameTextView = (TextView) view.findViewById(R.id.name_textview);
                setViewVisibility(viewHolder.nameTextView, View.VISIBLE);
            }

            viewHolder.attachMessageRelativeLayout = (RelativeLayout) view.findViewById(R.id.attach_message_relativelayout);
            viewHolder.timeAttachMessageTextView = (TextView) view.findViewById(R.id.time_attach_message_textview);
            viewHolder.progressRelativeLayout = (RelativeLayout) view.findViewById(R.id.progress_relativelayout);
            viewHolder.textMessageView = view.findViewById(R.id.text_message_view);
            viewHolder.messageTextView = (EmojiTextView) view.findViewById(R.id.message_textview);
            viewHolder.attachImageView = (MaskedImageView) view.findViewById(R.id.attach_imageview);
            viewHolder.timeTextMessageTextView = (TextView) view.findViewById(R.id.time_text_message_textview);
            viewHolder.verticalProgressBar = (ProgressBar) view.findViewById(R.id.vertical_progressbar);
            viewHolder.verticalProgressBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.vertical_progressbar));
            viewHolder.centeredProgressBar = (RateTextCircularProgressBar) view.findViewById(R.id.centered_progressbar);
        } else {
            view = layoutInflater.inflate(R.layout.list_item_notification_message, null, true);
            viewHolder.textMessageView = view.findViewById(R.id.text_message_view);
            viewHolder.messageTextView = (EmojiTextView) view.findViewById(R.id.message_textview);
            viewHolder.timeTextMessageTextView = (TextView) view.findViewById(
                    R.id.time_text_message_textview);
        }

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        String avatarUrl = null;
        String senderName;

        MessageCache messageCache = ChatDatabaseManager.getMessageCacheFromCursor(cursor);

        boolean ownMessage = isOwnMessage(messageCache.getSenderId());
        boolean notificationMessage = messageCache.getMessagesNotificationType() != null;

        if (notificationMessage) {
            viewHolder.messageTextView.setText(messageCache.getMessage());
//            viewHolder.timeTextMessageTextView.setText(DateUtils.longToMessageDate(messageCache.getTime()));
        } else {
            resetUI(viewHolder);

            if (ownMessage) {
                avatarUrl = getAvatarUrlForCurrentUser();
            } else {
                User senderFriend = UsersDatabaseManager.getUserById(context, messageCache.getSenderId());
                if (senderFriend != null) {
                    senderName = senderFriend.getFullName();
                    avatarUrl = getAvatarUrlForFriend(senderFriend);
                } else {
                    senderName = messageCache.getSenderId() + ConstsCore.EMPTY_STRING;
                }
                viewHolder.nameTextView.setTextColor(getTextColor(messageCache.getSenderId()));
                viewHolder.nameTextView.setText(senderName);
            }

            if (!TextUtils.isEmpty(messageCache.getAttachUrl())) {
                viewHolder.timeAttachMessageTextView.setText(DateUtils.longToMessageDate(messageCache.getTime()));
                setViewVisibility(viewHolder.progressRelativeLayout, View.VISIBLE);
                //displayAttachImage(messageCache.getAttachUrl(), viewHolder);
                if (messageCache.getMessage().equals(context.getString(R.string.dlg_attached_last_message))){
                    showImage(messageCache.getAttachUrl(), viewHolder);
                }else if (messageCache.getMessage().equals(context.getString(R.string.dlg_attached_video_last_message))) {
                    //displayAttachImage(messageCache.getAttachUrl(), viewHolder, messageCache.getMessage());
                    showVideo(messageCache.getAttachUrl(), viewHolder);
                }
            } else {
                setViewVisibility(viewHolder.textMessageView, View.VISIBLE);
                viewHolder.timeTextMessageTextView.setText(DateUtils.longToMessageDate(messageCache.getTime()));
                viewHolder.messageTextView.setText(messageCache.getMessage());
            }
        }

//        if (!messageCache.isRead() && !ownMessage) {
//            messageCache.setRead(true);
//            QBUpdateStatusMessageCommand.start(context, dialog, messageCache, false);
//        }
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
        displayAvatarImage(avatarUrl, viewHolder.avatarImageView);
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
    private String getText(ViewHolder viewHolder){
        String msg = viewHolder.messageTextView.getText().toString();
        return  msg;
    }
}