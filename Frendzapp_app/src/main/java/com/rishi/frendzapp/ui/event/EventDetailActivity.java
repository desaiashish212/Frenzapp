package com.rishi.frendzapp.ui.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.exception.QBResponseException;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;
import com.rishi.frendzapp.ui.chats.PrivateDialogActivity;
import com.rishi.frendzapp.ui.views.RoundedImageView;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp.utils.EventUtils;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.models.User;
import com.rishi.frendzapp_core.qb.helpers.QBPrivateChatHelper;
import com.rishi.frendzapp_core.service.QBService;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.DialogUtils;
import com.rishi.frendzapp_core.utils.ErrorUtils;

import java.util.Calendar;

public class EventDetailActivity extends BaseLogeableActivity {
    private TextView name_textView;
    private TextView dob_textView;
    private TextView days_textView;
    private Button congo_button;
    private User user;
    private QBPrivateChatHelper privateChatHelper;
    private int day;
    private int month;
    private int year;
    private RoundedImageView dp;
    private String databaseDateFormat = getDatabaseDateFormat();

    public static void start(Context context, int friendId) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIEND_ID, friendId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_one_detail_one);
        int friendId = getIntent().getExtras().getInt(QBServiceConsts.EXTRA_FRIEND_ID);
        user = UsersDatabaseManager.getUserById(this, friendId);
        day = getDatabaseDateDay(user.getDob());

        month = getDatabaseDateMonth(user.getDob());

        year = getDatabaseDateYear(user.getDob());
        init();
        initListener();
    }

    public void init(){
        name_textView = (TextView) findViewById(R.id.name_textview);
        dob_textView = (TextView) findViewById(R.id.dob_textview);
        days_textView = (TextView) findViewById(R.id.days_textview);
        congo_button = (Button) findViewById(R.id.congo_button);
       // congo_button.setVisibility(View.GONE);
        dp = (RoundedImageView) findViewById(R.id.avatar_imageview);
        name_textView.setText(user.getFullName());
        dob_textView.setText(user.getDob());
        days_textView.setText(EventUtils.diffMonth(day, month));
        displayAvatarImage(user.getAvatarUrl(), dp);
    }

    public void initListener(){
        congo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if (day == calendar.get(Calendar.DAY_OF_MONTH) && month == calendar.get(Calendar.MONTH)) {
                    if (checkFriendStatus(user.getUserId())) {
                        try {
                            QBDialog existingPrivateDialog = privateChatHelper.createPrivateDialogIfNotExist(
                                    user.getUserId());
                            PrivateDialogActivity.start(EventDetailActivity.this, user, existingPrivateDialog,null);
                        } catch (QBResponseException e) {
                            ErrorUtils.showError(EventDetailActivity.this, e);
                        }
                    }
                    congo_button.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(EventDetailActivity.this, "Today is not his Birthday", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    private void displayAvatarImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
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

    @Override
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);

        if (privateChatHelper == null) {
            privateChatHelper = (QBPrivateChatHelper) service.getHelper(QBService.PRIVATE_CHAT_HELPER);
        }
    }
    private String getDatabaseDateFormat() {
        // Not yet needed till Issue #9676 & #2947 are fixed - currently the database format is yyyy-mm-dd
        return "dd-mm-yyyy";
    }
    private int getDatabaseDateYear(String string) {
        int index = this.databaseDateFormat.indexOf("yyyy");

        String str = string.substring(index, index + 4);


        return Integer.parseInt(str);
    }

    /**
     * @param string
     * @return
     */
    private int getDatabaseDateMonth(String string) {
       // int index = this.databaseDateFormat.indexOf("mm");
        int index = Integer.valueOf(databaseDateFormat.indexOf("mm"));

        String str = string.substring(index, index + 2);
        String sSource = str.replaceAll("/", "");
//
        String[] schoolbag2 = { "M","J","F","M","M","J","F","M","M","J","F","M","l" };
        for(int i=0;i<=sSource.length();i++)
        {
            if(sSource==schoolbag2[i])
            {
                Toast.makeText(EventDetailActivity.this, "Month is"+sSource, Toast.LENGTH_LONG).show();

            }
        }


        return Integer.parseInt(sSource) - 1;



    }

    /**
     * @param string
     * @return
     */
    private int getDatabaseDateDay(String string) {
       int index = this.databaseDateFormat.indexOf("dd");

        String str = string.substring(index, index + 2);
        return Integer.parseInt(str);

    }

}
