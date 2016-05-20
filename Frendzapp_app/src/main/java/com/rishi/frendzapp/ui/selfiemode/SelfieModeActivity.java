package com.rishi.frendzapp.ui.selfiemode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.quickblox.users.model.QBUser;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.ui.base.BaseActivity;
import com.rishi.frendzapp.ui.profile.Profile;
import com.rishi.frendzapp.ui.share.ShareActivity;
import com.rishi.frendzapp.utils.ImageUtils;
import com.rishi.frendzapp.utils.ReceiveFileFromBitmapTask;
import com.rishi.frendzapp.utils.ReceiveUriScaledBitmapTask;
import com.rishi.frendzapp_core.core.command.Command;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.qb.commands.QBUpdateUserCommand;
import com.rishi.frendzapp_core.service.QBServiceConsts;
import com.rishi.frendzapp_core.utils.DialogUtils;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SelfieModeActivity extends BaseActivity implements ReceiveFileFromBitmapTask.ReceiveFileListener, ReceiveUriScaledBitmapTask.ReceiveUriScaledBitmapListener{
	private ImageButton btnRotate;
	private ImageView imageView;
	private ImageView cropImage;
	private Bitmap bitmap;
	private Bitmap avatarBitmapCurrent;
	private float angle=0;
	private ImageUtils imageUtils;
	private View actionCancelView;
	private View actionDoneView;
	private View actionView;
	private Uri outputUri;
	private Uri tempUri;
	private int requestCode;
	private int resultCode;
	private Intent data;
	private Uri mFileUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		init();
		initListener();
		initBroadcastActionList();
		//imageUtils.getCaptureImage();
		imageUtils = new ImageUtils(this);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mFileUri = getOutputMediaFileUri(1);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
		startActivityForResult(intent, ImageUtils.CAPTURE_INTENT_CALLED);
	}

	public void init(){
		btnRotate = _findViewById(R.id.btn_rotate);
		imageView = (ImageView) findViewById(R.id.result_image);
		cropImage = (ImageView) findViewById(R.id.crop_image);
		imageUtils = new ImageUtils(this);
		actionView = _findViewById(R.id.action_view);
		actionCancelView = _findViewById(R.id.action_cancel_view);
		actionDoneView = _findViewById(R.id.action_done_view);
		hideAction();
	}

	public void  initListener(){
		btnRotate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				angle += 90;
				Bitmap rotatedImage = rotateImage(bitmap, angle);
				imageView.setImageBitmap(rotatedImage);
			}
		});

		actionCancelView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				hideAction();
			}
		});

		actionDoneView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateUserData();
			}
		});
	}

	private void initBroadcastActionList() {
		addAction(QBServiceConsts.UPDATE_USER_SUCCESS_ACTION, new UpdateUserSuccessAction());
		addAction(QBServiceConsts.UPDATE_USER_FAIL_ACTION, new UpdateUserFailAction());
	}

	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	// Return image / video
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == 1) { // image
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if (type == 2) { // video
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}
	public static Bitmap rotateImage(Bitmap sourceImage, float angle)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	private void updateUserData() {
		System.out.println("In updateUserData");

			saveChanges();

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("In onActivityResult");
		if (requestCode == Crop.REQUEST_CROP) {
			System.out.println("In REQUEST_CROP");
			handleCrop(resultCode, data);
		}else {
			if (mFileUri != null) {
				String mFilePath = mFileUri.toString();
				if (mFilePath != null) {
					setImage(mFilePath);
				}
			}
			//bitmap = (Bitmap) data.getExtras().get("data");
			tempUri = getImageUri(this, bitmap);
			//imageView.setImageBitmap(photo);
			this.requestCode = requestCode;
			this.resultCode = resultCode;
			this.data = data;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setImage(String filepath) {
		filepath = filepath.replace("file://", ""); // remove to avoid BitmapFactory.decodeFile return null
		File imgFile = new File(filepath);
		if (imgFile.exists()) {
			//
			int rotate = 0;
			try {
				File imageFile = new File(getRealPathFromURI(tempUri));
				ExifInterface exif = new ExifInterface(
						imageFile.getAbsolutePath());
				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

				switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_270:
						rotate = 270;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);
			bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), null);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			imageView.setImageBitmap(bitmap);

		}
	}
	private void saveChanges() {
		showProgress();

		//QBUser newUser = createUserForUpdating();

		new ReceiveFileFromBitmapTask(this).execute(imageUtils, avatarBitmapCurrent, true);

	}

	private void handleCrop(int resultCode, Intent result) {
		System.out.println("In handleCrop");
		if (resultCode == RESULT_OK) {
			//isNeedUpdateAvatar = true;
			avatarBitmapCurrent = imageUtils.getBitmap(outputUri);
			cropImage.setVisibility(View.VISIBLE);
			cropImage.setImageBitmap(avatarBitmapCurrent);
			showAction();
		} else if (resultCode == Crop.RESULT_ERROR) {
			DialogUtils.showLong(this, Crop.getError(result).getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.selfie_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// action with ID action_refresh was selected
			case R.id.action_set_profile:
				//Toast.makeText(this, "You clicked on SetProfile", Toast.LENGTH_SHORT).show();
				if (requestCode == Crop.REQUEST_CROP) {
					System.out.println("In REQUEST_CROP");
					handleCrop(resultCode, data);
				} else if (requestCode == ImageUtils.CAPTURE_INTENT_CALLED && resultCode == RESULT_OK) {
					Uri originalUri = getImageUri(this,bitmap);
					System.out.println("In originalUri"+originalUri);
					if (originalUri != null) {
						showProgress();
						new ReceiveUriScaledBitmapTask(this).execute(imageUtils, originalUri);
					}
				}
				break;
			// action with ID action_settings was selected
			case R.id.action_share:
				//Toast.makeText(this, "You clicked on Share", Toast.LENGTH_SHORT)	.show();
				ShareActivity.start(SelfieModeActivity.this,mFileUri.toString());
				break;
			default:
				break;
		}
		return true;
	}

	private void showAction() {
		actionView.setVisibility(View.VISIBLE);
	}

	private void hideAction() {
		actionView.setVisibility(View.GONE);
	}

	@Override
	public void onCachedImageFileReceived(File imageFile) {
		System.out.println("In onCachedImageFileReceived");
		QBUser newUser = createUserForUpdating();
		QBUpdateUserCommand.start(this, newUser, imageFile);
	}

	@Override
	public void onAbsolutePathExtFileReceived(String absolutePath) {

	}

	@Override
	public void onUriScaledBitmapReceived(Uri originalUri) {
		System.out.println("In onUriScaledBitmapReceived");
		hideProgress();
		startCropActivity(originalUri);
	}

	private void startCropActivity(Uri originalUri) {
		System.out.println("In startCropActivity");
		outputUri = Uri.fromFile(new File(getCacheDir(), Crop.class.getName()));
		new Crop(originalUri).output(outputUri).asSquare().start(this);
	}

	private QBUser createUserForUpdating() {
		QBUser newUser = new QBUser();
		newUser.setId(AppSession.getSession().getUser().getId());
		return newUser;
	}

	public class UpdateUserFailAction implements Command {

		@Override
		public void execute(Bundle bundle) {
			Exception exception = (Exception) bundle.getSerializable(QBServiceConsts.EXTRA_ERROR);
			DialogUtils.showLong(SelfieModeActivity.this, exception.getMessage());
			hideProgress();
		}
	}

	private class UpdateUserSuccessAction implements Command {
		@Override
		public void execute(Bundle bundle) {
			QBUser user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
			AppSession.getSession().updateUser(user);
			hideAction();
			hideProgress();
			Profile.start(SelfieModeActivity.this);
			if (mFileUri != null) {
				String mFilePath = mFileUri.toString();
				if (mFilePath != null) {
					cropImage.setVisibility(View.INVISIBLE);
					setImage(mFilePath);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(BaseActivity.RESULT_CANCELED);
		finish();
	}
}