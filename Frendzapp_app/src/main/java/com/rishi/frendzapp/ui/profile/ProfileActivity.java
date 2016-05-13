package com.rishi.frendzapp.ui.profile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rishi.frendzapp.ui.chats.BaseDialogMessagesAdapter;
import com.rishi.frendzapp.utils.Consts;
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
import com.rishi.frendzapp.ui.views.RoundedImageView;
import com.rishi.frendzapp_core.utils.DialogUtils;
import com.rishi.frendzapp.utils.ImageUtils;
import com.rishi.frendzapp.utils.KeyboardUtils;
import com.rishi.frendzapp.utils.ReceiveFileFromBitmapTask;
import com.rishi.frendzapp.utils.ReceiveUriScaledBitmapTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class ProfileActivity extends BaseLogeableActivity implements ReceiveFileFromBitmapTask.ReceiveFileListener,
        View.OnClickListener, ReceiveUriScaledBitmapTask.ReceiveUriScaledBitmapListener {

    private LinearLayout changeAvatarLinearLayout;
    private RoundedImageView avatarImageView;
    private LinearLayout phoneLinearLayout;
    private RelativeLayout changeFullNameRelativeLayout;
    private RelativeLayout changeEmailRelativeLayout;
    private RelativeLayout changedobRelativeLayout;
    private RelativeLayout changecityRelativeLayout;
    private RelativeLayout changeStatusRelativeLayout;
    private TextView phoneTextView;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText cityEditText;
    private EditText dobEditText;
    private EditText statusEditText;
    private View actionView;
    private View actionCancelView;
    private View actionDoneView;

    private ImageUtils imageUtils;
    private Bitmap avatarBitmapCurrent;
    private String fullNameCurrent;
    private String emailCurrent;
    private String dobCurrent;
    private String cityCurrent;
    private String statusCurrent;
    private String fullNameOld;
    private String emailOld;
    private String dobOld;
    private String cityOld;
    private String statusOld;
    private QBUser user;
    private boolean isNeedUpdateAvatar;
    private Uri outputUri;
    private UserCustomData userCustomData;
    private ActionMode mActionMode;

    public static void start(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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
    }

    private void initUI() {
        actionView = _findViewById(R.id.action_view);
        actionCancelView = _findViewById(R.id.action_cancel_view);
        actionDoneView = _findViewById(R.id.action_done_view);
        changeAvatarLinearLayout = _findViewById(R.id.change_avatar_linearlayout);
        avatarImageView = _findViewById(R.id.avatar_imageview);
        phoneLinearLayout = _findViewById(R.id.phone_linearlayout);
        changeFullNameRelativeLayout = _findViewById(R.id.change_fullname_relativelayout);
        changeEmailRelativeLayout = _findViewById(R.id.change_email_relativelayout);
        changedobRelativeLayout = _findViewById(R.id.change_dob_relativelayout);
        changecityRelativeLayout = _findViewById(R.id.change_city_relativelayout);
        changeStatusRelativeLayout = _findViewById(R.id.change_status_relativelayout);
        phoneTextView = _findViewById(R.id.phone_textview);
        fullNameEditText = _findViewById(R.id.fullname_edittext);
        emailEditText = _findViewById(R.id.email_edittext);
        dobEditText = _findViewById(R.id.dob_edittext);
        cityEditText = _findViewById(R.id.city_edittext);
        statusEditText = _findViewById(R.id.status_edittext);
    }

    private void initListeners() {
        changeAvatarLinearLayout.setOnClickListener(this);
        avatarImageView.setOnClickListener(this);
        changeFullNameRelativeLayout.setOnClickListener(this);
        changeEmailRelativeLayout.setOnClickListener(this);
        changedobRelativeLayout.setOnClickListener(this);
        changecityRelativeLayout.setOnClickListener(this);
        changeStatusRelativeLayout.setOnClickListener(this);

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

        statusEditText.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mActionMode = ProfileActivity.this.startActionMode(new ActionBarCallBack());
              /*  ClipboardManager cManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", getText(viewHolder));
                cManager.setPrimaryClip(cData);
                Toast.makeText(context, "Coped", Toast.LENGTH_SHORT);*/
                // AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
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
        fullNameEditText.setText(fullNameOld);

        if (TextUtils.isEmpty(user.getLogin())) {
            phoneLinearLayout.setVisibility(View.GONE);
        } else {
            phoneLinearLayout.setVisibility(View.VISIBLE);
            phoneTextView.setText(user.getLogin());
        }

        emailOld = user.getEmail();

        emailEditText.setText(emailOld);

        dobOld = userCustomData.getDob();

        dobEditText.setText(dobOld);

        cityOld = userCustomData.getCity();

        cityEditText.setText(cityOld);

        statusOld = userCustomData.getStatus();

        statusEditText.setText(statusOld);
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
        TextWatcher textWatcherListener = new TextWatcherListener();
        fullNameEditText.addTextChangedListener(textWatcherListener);
        emailEditText.addTextChangedListener(textWatcherListener);
        dobEditText.addTextChangedListener(textWatcherListener);
        statusEditText.addTextChangedListener(textWatcherListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar_imageview:
            case R.id.change_avatar_linearlayout:
                changeAvatarOnClick();
                break;
            case R.id.change_fullname_relativelayout:
                changeFullNameOnClick();
                break;
            case R.id.change_email_relativelayout:
                changeEmailOnClick();
                break;
            case R.id.change_dob_relativelayout:
                changeDobOnClick();
                break;
            case R.id.change_city_relativelayout:
                changeCityOnClick();
                break;
            case R.id.change_status_relativelayout:
                changeStatusOnClick();
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
        initChangingEditText(fullNameEditText);
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
        initChangingEditText(emailEditText);
    }
    public void changeDobOnClick() {
        initChangingEditText(dobEditText);
    }
    public void changeCityOnClick() {
        initChangingEditText(cityEditText);
    }
    public void changeStatusOnClick() {
        initChangingEditText(statusEditText);
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
        fullNameCurrent = fullNameEditText.getText().toString();
        emailCurrent = emailEditText.getText().toString();
        dobCurrent = dobEditText.getText().toString();
        cityCurrent = cityEditText.getText().toString();
        statusCurrent = statusEditText.getText().toString();
    }

    private void updateUserData() {
        updateCurrentUserData();
        if (isUserDataChanged()) {
            saveChanges();
        }
    }

    private boolean isUserDataChanged() {
        return isNeedUpdateAvatar || !fullNameCurrent.equals(fullNameOld) || !emailCurrent.equals(emailOld ) ||
                !dobCurrent.equals(dobOld ) || !cityCurrent.equals(cityOld ) ||!statusCurrent.equals(statusOld);
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

        stopChangingEditText(fullNameEditText);
        stopChangingEditText(emailEditText);
        stopChangingEditText(dobEditText);
        stopChangingEditText(cityEditText);
        stopChangingEditText(statusEditText);
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

        if (isFieldValueChanged(dobCurrent, dobOld) || isNeedUpdateAvatar) {
            userCustomData.setDob(dobCurrent);
            user.setCustomData(Utils.customDataToString(userCustomData));
        }
        if (isFieldValueChanged(cityCurrent, cityOld) || isNeedUpdateAvatar) {
            userCustomData.setCity(cityCurrent);
            user.setCustomData(Utils.customDataToString(userCustomData));
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
        fullNameOld = fullNameEditText.getText().toString();
        emailOld = emailEditText.getText().toString();
        dobOld = dobEditText.getText().toString();
        cityOld = cityEditText.getText().toString();
        statusOld = statusEditText.getText().toString();
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
            DialogUtils.showLong(ProfileActivity.this, exception.getMessage());
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
        String msg = statusEditText.getText().toString();
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
                    if (statusEditText.getText().length()>0) {
                        statusEditText.setSelectAllOnFocus(true);
                        statusEditText.setSelection(0);
                        statusEditText.selectAll();
                    }
                    return true;

                case R.id.copy:
                    if (statusEditText.getText().length()>0) {
                        ClipboardManager cManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData cData = ClipData.newPlainText("text", getText());
                        cManager.setPrimaryClip(cData);
                        statusEditText.setSelection(statusEditText.getText().length());
                        statusEditText.setSelectAllOnFocus(false);
                        mode.finish();
                    }
                    return true;

                case R.id.cut:
                    if (statusEditText.getText().length()>0) {
                        ClipboardManager cManager1 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData cData1 = ClipData.newPlainText("text", getText());
                        cManager1.setPrimaryClip(cData1);
                        statusEditText.setText("");
                        statusEditText.setSelectAllOnFocus(false);
                        mode.finish();
                    }
                    return true;

                case R.id.paste:
                    ClipboardManager clipMan = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    statusEditText.setText(clipMan.getText());
                    statusEditText.setSelectAllOnFocus(false);
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
}