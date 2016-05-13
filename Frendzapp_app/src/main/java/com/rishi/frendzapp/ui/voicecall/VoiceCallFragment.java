package com.rishi.frendzapp.ui.voicecall;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.mediacall.OutgoingCallFragment;
import com.rishi.frendzapp.ui.views.RoundedImageView;
import com.rishi.frendzapp.utils.Consts;
import com.rishi.frendzapp_core.db.managers.UsersDatabaseManager;
import com.rishi.frendzapp_core.models.User;

/**
 * Implementation of {@link OutgoingCallFragment} for AUDIO calls representation
 */

public class VoiceCallFragment extends OutgoingCallFragment {

    @Override
    protected void initUI(View rootView) {
        super.initUI(rootView);

        User friendFromDB = UsersDatabaseManager.getUserById(getActivity().getBaseContext(), opponent.getUserId());

        if (friendFromDB != null) {
            ((TextView) rootView.findViewById(R.id.name_textview)).setText(friendFromDB.getFullName());
            RoundedImageView avatarView = (RoundedImageView) rootView.findViewById(R.id.avatar_imageview);
            avatarView.setOval(true);

            if (!TextUtils.isEmpty(friendFromDB.getAvatarUrl())) {
                ImageLoader.getInstance().displayImage(friendFromDB.getAvatarUrl(),
                        avatarView, Consts.UIL_USER_AVATAR_DISPLAY_OPTIONS);
            }
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_voice_call;
    }

}
