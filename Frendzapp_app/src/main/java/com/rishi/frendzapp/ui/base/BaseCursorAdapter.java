package com.rishi.frendzapp.ui.base;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rishi.frendzapp.utils.Consts;
import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.models.LoginType;
import com.rishi.frendzapp_core.models.User;

public abstract class BaseCursorAdapter extends CursorAdapter {

    protected final Context context;
    protected final Resources resources;
    protected final LayoutInflater layoutInflater;

    protected QBUser currentUser;
    protected LoginType currentLoginType;

    public BaseCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        this.context = context;
        resources = context.getResources();
        layoutInflater = LayoutInflater.from(context);
        currentUser = AppSession.getSession().getUser();
        currentLoginType = AppSession.getSession().getLoginType();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }

    protected void displayAvatarImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    protected void displayGroupPhotoImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, Consts.UIL_GROUP_AVATAR_DISPLAY_OPTIONS);
    }

    protected String getAvatarUrlForCurrentUser() {
        return currentUser.getWebsite();
    }

    protected String getAvatarUrlForFriend(User friend) {
        return friend.getAvatarUrl();
    }
}