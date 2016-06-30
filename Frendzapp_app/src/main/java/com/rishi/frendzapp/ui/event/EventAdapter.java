package com.rishi.frendzapp.ui.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.views.RoundedImageView;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp.utils.EventUtils;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.models.User;

import java.util.Calendar;
import java.util.List;

/**
 * Created by AD on 08-Feb-16.
 */
public class EventAdapter extends BaseAdapter {
    private Context context;
    private List<User> listData;
    private String databaseDateFormat = getDatabaseDateFormat();
    private  String diff;
    int pos;

    public EventAdapter(Context context, List<User> listData) {
        this.context = context;
        this.listData = listData;
    }

    class ViewHolder {
        private TextView name;
        private TextView dob;
        private TextView when;
        private RoundedImageView dp;
        private ImageView img_dob;
    }

    private String getDatabaseDateFormat() {
        // Not yet needed till Issue #9676 & #2947 are fixed - currently the database format is yyyy-mm-dd
        return "dd-mm-yyyy";
    }


    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
         pos=position;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_birthday, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name_textview);
            viewHolder.dob = (TextView) view.findViewById(R.id.dob_textview);
            viewHolder.when = (TextView) view.findViewById(R.id.when_textview);
            viewHolder.dp = (RoundedImageView) view.findViewById(R.id.avatar_imageview);
            viewHolder.img_dob = (ImageView) view.findViewById(R.id.img_dob);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final User user = listData.get(position);

        viewHolder.name.setText(user.getFullName());
        viewHolder.dob.setText(user.getDob());
        displayAvatarImage(user.getAvatarUrl(), viewHolder.dp);
        String dateString = user.getDob();
        int day = getDatabaseDateDay(dateString);
        int month = getDatabaseDateMonth(dateString);
       // int year = getDatabaseDateYear(dateString);

     //   System.out.println("ddmmyy:" + day + ":" + month + ":" + year);
        diff = EventUtils.diffMonth(day, month);
        viewHolder.when.setText(diff);
        Calendar today = Calendar.getInstance();
        if (day == today.get(Calendar.DAY_OF_MONTH) && month ==today.get(Calendar.MONTH))
            viewHolder.img_dob.setVisibility(View.VISIBLE);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked on the position"+position, Toast.LENGTH_SHORT).show();
                boolean isFriend = UsersDatabaseManager.isFriendInBaseWithPending(context,
                        user.getUserId());
                if (isFriend) {
                    startEventDetailsActivity(user.getUserId());
                }
            }
        });

        return view;
    }

    private void displayAvatarImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private int getDatabaseDateYear(String string) {
        int index = this.databaseDateFormat.indexOf("yyyy");
        String str = string.substring(index, index + 4);
        String str1 = str.replace(" ", "");
        return Integer.parseInt(str1);
    }

    /**
     * @param string
     * @return
     */
    private int getDatabaseDateMonth(String string) {
        int index = this.databaseDateFormat.indexOf("mm");
        String str = string.substring(index, index + 2);
        String str1 = str.replace("/", "");
        return Integer.parseInt(str1)-1;
    }

    /**
     * @param string
     * @return
     */
    private int getDatabaseDateDay(String string) {
        int index = this.databaseDateFormat.indexOf("dd");
        String str = string.substring(index, index + 2);
        String str1 = str.replace("/", "");
        return Integer.parseInt(str1);

    }

    private void startEventDetailsActivity(int userId) {
        EventDetailActivity.start(context,userId);
    }


}
