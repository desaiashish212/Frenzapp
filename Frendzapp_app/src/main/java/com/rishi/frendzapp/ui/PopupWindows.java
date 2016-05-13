package com.rishi.frendzapp.ui;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.authorization.intializing.IntializingActivity;
import com.rishi.frendzapp.ui.authorization.service.HttpService;

/**
 * Created by AD on 20-Apr-16.
 */
public class PopupWindows {
    private Context context;

    public PopupWindows(Context context){
        context = context;
    }
    public static void initiatePopupWindow( final Context context) {
        TextView txtOk;
        final  PopupWindow pwindo;
        try {
// We need to get the instance of the LayoutInflater
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_alert_verifed, null);
            pwindo = new android.widget.PopupWindow(layout, width, height, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            txtOk = (TextView) layout.findViewById(R.id.txt_ok);

            txtOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    Intent intent = new Intent(context, IntializingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
