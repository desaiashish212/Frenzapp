package com.rishi.frendzapp.ui.main;

import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseActivity;
import com.rishi.frendzapp.ui.base.BaseListAdapter;
import com.rishi.frendzapp_core.utils.ConstsCore;

import java.util.List;

public class NavigationDrawerAdapter extends BaseListAdapter<String> implements NavigationDrawerFragment.NavigationDrawerCounterListener {

    private TextView counterUnreadChatsDialogsTextView;
    private TypedArray icon;

    public NavigationDrawerAdapter(BaseActivity activity, List<String> objects,TypedArray icon) {
        super(activity, objects);
        this.icon = icon;
    }

    @Nullable
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final String data = getItem(position);
        final int id = icon.getResourceId(position,-1);
        System.out.println("Position:"+id);
        String chatItem = resources.getStringArray(
                R.array.nvd_items_array)[MainActivity.ID_HOME_PAGE_FRAGMENT];

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_navigation_drawer, null);
            viewHolder = new ViewHolder();
            viewHolder.menuImageView = (ImageView)convertView.findViewById(R.id.menu_imageView);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            viewHolder.counterTextView = (TextView) convertView.findViewById(
                    R.id.counter_textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.equals(chatItem)) {
            counterUnreadChatsDialogsTextView = viewHolder.counterTextView;
        }

        viewHolder.nameTextView.setText(data);
        viewHolder.menuImageView.setImageResource(id);

        return convertView;
    }

    @Override
    public void onUpdateCountUnreadDialogs(int count) {
        updateCounter(counterUnreadChatsDialogsTextView, count);
    }

    private void updateCounter(TextView counterTextView, int count) {
        if (count > ConstsCore.ZERO_INT_VALUE) {
            counterTextView.setVisibility(View.VISIBLE);
            counterTextView.setText(count + ConstsCore.EMPTY_STRING);
        } else {
            counterTextView.setVisibility(View.GONE);
        }
    }

    private static class ViewHolder {
        ImageView menuImageView;
        TextView nameTextView;
        TextView counterTextView;
    }
}