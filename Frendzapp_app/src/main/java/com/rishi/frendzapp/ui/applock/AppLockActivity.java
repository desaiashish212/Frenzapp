package com.rishi.frendzapp.ui.applock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.l;
import com.rishi.frendzapp.App;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.core.lock.AppLock;
import com.rishi.frendzapp.core.lock.LockManager;
import com.rishi.frendzapp.ui.authorization.app.Config;
import com.rishi.frendzapp.ui.authorization.intializing.IntializingActivity;
import com.rishi.frendzapp.ui.base.BaseActivity;
import com.rishi.frendzapp.ui.verification.OtpVerificationActivity;
import com.rishi.frendzapp_core.models.AppSession;
import com.rishi.frendzapp_core.utils.PrefsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AppLockActivity extends BaseActivity {
	public static final String TAG = "AppLockActivity";

	private int type = -1;
	private String oldPasscode = null;
	private PopupWindow pwindo;
	private EditText et_email;
	protected EditText codeField1 = null;
	protected EditText codeField2 = null;
	protected EditText codeField3 = null;
	protected EditText codeField4 = null;
	protected InputFilter[] filters = null;
	protected TextView tvMessage = null;
	private LinearLayout linear_forgotpassword;
	private ProgressBar progressBar;
	PrefsHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_applock);
		helper = new PrefsHelper(this);

		tvMessage = (TextView) findViewById(R.id.tv_message);
		linear_forgotpassword = (LinearLayout) findViewById(R.id.linear_forgotpassword);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String message = extras.getString(AppLock.MESSAGE);
			if (message != null) {
				tvMessage.setText(message);
			}

			type = extras.getInt(AppLock.TYPE, -1);
		}

		filters = new InputFilter[2];
		filters[0] = new InputFilter.LengthFilter(1);
		filters[1] = numberFilter;

		codeField1 = (EditText) findViewById(R.id.passcode_1);
		setupEditText(codeField1);

		codeField2 = (EditText) findViewById(R.id.passcode_2);
		setupEditText(codeField2);

		codeField3 = (EditText) findViewById(R.id.passcode_3);
		setupEditText(codeField3);

		codeField4 = (EditText) findViewById(R.id.passcode_4);
		setupEditText(codeField4);

		// setup the keyboard
		((Button) findViewById(R.id.button0)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button1)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button2)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button3)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button4)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button5)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button6)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button7)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button8)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button9)).setOnClickListener(btnListener);

		((Button) findViewById(R.id.button_clear))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						clearFields();
					}
				});

		((Button) findViewById(R.id.button_erase))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						onDeleteKey();
					}
				});

		overridePendingTransition(R.anim.slide_up, R.anim.nothing);

		switch (type) {

		case AppLock.DISABLE_PASSLOCK:
			this.setTitle("Disable Passcode");
			break;
		case AppLock.ENABLE_PASSLOCK:
			this.setTitle("Enable Passcode");
			break;
		case AppLock.CHANGE_PASSWORD:
			this.setTitle("Change Passcode");
			break;
		case AppLock.UNLOCK_PASSWORD:
			this.setTitle("Unlock Passcode");
			break;
		}
	}

	public int getType() {
		return type;
	}

	protected void onPasscodeInputed() {
		String passLock = codeField1.getText().toString()
				+ codeField2.getText().toString()
				+ codeField3.getText().toString() + codeField4.getText();

		codeField1.setText("");
		codeField2.setText("");
		codeField3.setText("");
		codeField4.setText("");
		codeField1.requestFocus();

		switch (type) {

		case AppLock.DISABLE_PASSLOCK:
			if (LockManager.getInstance().getAppLock().checkPasscode(passLock)) {
				setResult(RESULT_OK);
				LockManager.getInstance().getAppLock().setPasscode(null);
				finish();
			} else {
				onPasscodeError();
			}
			break;

		case AppLock.ENABLE_PASSLOCK:
			if (oldPasscode == null) {
				tvMessage.setText(R.string.reenter_passcode);

				oldPasscode = passLock;

				System.out.println(oldPasscode);
			} else {
				if (passLock.equals(oldPasscode)) {
					setResult(RESULT_OK);
					LockManager.getInstance().getAppLock()
							.setPasscode(passLock);
					finish();
				} else {
					oldPasscode = null;
					tvMessage.setText(R.string.enter_passcode);
					onPasscodeError();
				}
			}
			break;

		case AppLock.CHANGE_PASSWORD:
			if (LockManager.getInstance().getAppLock().checkPasscode(passLock)) {
				tvMessage.setText(R.string.enter_passcode);
				type = AppLock.ENABLE_PASSLOCK;
			} else {
				onPasscodeError();
			}
			break;

		case AppLock.UNLOCK_PASSWORD:
			if (LockManager.getInstance().getAppLock().checkPasscode(passLock)) {
				setResult(RESULT_OK);
				finish();
			} else {
				onPasscodeError();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (type == AppLock.UNLOCK_PASSWORD) {
			// back to home screen
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(intent);
			finish();
		} else {
			finish();
		}
	}

	protected void setupEditText(EditText editText) {
		editText.setInputType(InputType.TYPE_NULL);
		editText.setFilters(filters);
		editText.setOnTouchListener(touchListener);
		editText.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DEL) {
			onDeleteKey();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void onDeleteKey() {
		if (codeField1.isFocused()) {

		} else if (codeField2.isFocused()) {
			codeField1.requestFocus();
			codeField1.setText("");
		} else if (codeField3.isFocused()) {
			codeField2.requestFocus();
			codeField2.setText("");
		} else if (codeField4.isFocused()) {
			codeField3.requestFocus();
			codeField3.setText("");
		}
	}

	private OnClickListener btnListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int currentValue = -1;
			int id = view.getId();
			if (id == R.id.button0) {
				currentValue = 0;
			} else if (id == R.id.button1) {
				currentValue = 1;
			} else if (id == R.id.button2) {
				currentValue = 2;
			} else if (id == R.id.button3) {
				currentValue = 3;
			} else if (id == R.id.button4) {
				currentValue = 4;
			} else if (id == R.id.button5) {
				currentValue = 5;
			} else if (id == R.id.button6) {
				currentValue = 6;
			} else if (id == R.id.button7) {
				currentValue = 7;
			} else if (id == R.id.button8) {
				currentValue = 8;
			} else if (id == R.id.button9) {
				currentValue = 9;
			} else {
			}

			// set the value and move the focus
			String currentValueString = String.valueOf(currentValue);
			if (codeField1.isFocused()) {
				codeField1.setText(currentValueString);
				codeField2.requestFocus();
				codeField2.setText("");
			} else if (codeField2.isFocused()) {
				codeField2.setText(currentValueString);
				codeField3.requestFocus();
				codeField3.setText("");
			} else if (codeField3.isFocused()) {
				codeField3.setText(currentValueString);
				codeField4.requestFocus();
				codeField4.setText("");
			} else if (codeField4.isFocused()) {
				codeField4.setText(currentValueString);
			}

			if (codeField4.getText().toString().length() > 0
					&& codeField3.getText().toString().length() > 0
					&& codeField2.getText().toString().length() > 0
					&& codeField1.getText().toString().length() > 0) {
				onPasscodeInputed();
			}
		}
	};

	protected void onPasscodeError() {
		Toast toast = Toast.makeText(this, getString(R.string.passcode_wrong),
				Toast.LENGTH_SHORT);
		linear_forgotpassword.setVisibility(View.VISIBLE);
		linear_forgotpassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initiatePopupWindow();
			}
		});
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 30);
		toast.show();

		Thread thread = new Thread() {
			public void run() {
				Animation animation = AnimationUtils.loadAnimation(
						AppLockActivity.this, R.anim.shake);
				findViewById(R.id.ll_applock).startAnimation(animation);
				codeField1.setText("");
				codeField2.setText("");
				codeField3.setText("");
				codeField4.setText("");
				codeField1.requestFocus();
			}
		};
		runOnUiThread(thread);
	}

	private InputFilter numberFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			if (source.length() > 1) {
				return "";
			}

			if (source.length() == 0) // erase
			{
				return null;
			}

			try {
				int number = Integer.parseInt(source.toString());
				if ((number >= 0) && (number <= 9))
					return String.valueOf(number);
				else
					return "";
			} catch (NumberFormatException e) {
				return "";
			}
		}
	};

	private OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.performClick();
			clearFields();
			return false;
		}
	};

	private void clearFields() {
		codeField1.setText("");
		codeField2.setText("");
		codeField3.setText("");
		codeField4.setText("");

		codeField1.postDelayed(new Runnable() {

			@Override
			public void run() {
				codeField1.requestFocus();
			}
		}, 200);
	}


	public void initiatePopupWindow() {
		TextView txtOk,txt_cancel;
		try {
// We need to get the instance of the LayoutInflater
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();
			int width = metrics.widthPixels;
			int height = metrics.heightPixels;
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.popup_forgot_password, null);
			pwindo = new PopupWindow(layout, width, height, true);
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

			txtOk = (TextView) layout.findViewById(R.id.txt_ok);
			txt_cancel = (TextView) layout.findViewById(R.id.txt_cancel);
			et_email=(EditText) layout.findViewById(R.id.et_email);
			progressBar=(ProgressBar)layout.findViewById(R.id.progressBar);

			txtOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String password;



					String entered_Email=et_email.getText().toString();

					//get email from shared preferences

					String Db_Email= AppSession.getSession().getUser().getEmail();
					if(entered_Email.equals(Db_Email))
					{
						Toast.makeText(AppLockActivity.this,"Both email are same",Toast.LENGTH_SHORT).show();

						//get password from shared preferences
						if (helper.isPrefExists(PrefsHelper.PREF_APPLOCK_PASS))
						{
							password=helper.getPref(PrefsHelper.PREF_APPLOCK_PASS);
							progressBar.setVisibility(View.VISIBLE);
							requestForEmail(Db_Email,password);


						}

					}
					else {
						Toast.makeText(AppLockActivity.this,"Both email are unequal",Toast.LENGTH_SHORT).show();
					}



				}
			});


			txt_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pwindo.dismiss();

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void requestForEmail(final String email,final String password) {

		StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_FORGOT_PASS, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d(TAG, response.toString());

				try {
					JSONObject responseObj = new JSONObject(response);

					// Parsing json object response
					// response will be a json object
					boolean error = responseObj.getBoolean("error");
					String message = responseObj.getString("message");

					// checking for error, if not error SMS is initiated
					// device should receive it shortly
					if (!error) {
						// boolean flag saying device is waiting for sms

						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
						pwindo.dismiss();
					} else {
						Toast.makeText(getApplicationContext(),
								"Error: " + message,
								Toast.LENGTH_LONG).show();
						pwindo.dismiss();
					}

					// hiding the progress bar
					progressBar.setVisibility(View.GONE);

				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(),
							"Error: " + e.getMessage(),
							Toast.LENGTH_LONG).show();

					progressBar.setVisibility(View.GONE);
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Error: " + error.getMessage());
				Toast.makeText(getApplicationContext(),
						error.getMessage(), Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
			}
		}) {

			/**
			 * Passing user parameters to our server
			 * @return
			 */
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("email", email);
				params.put("password", password);
				Log.e(TAG, "Posting params: " + params.toString());

				return params;
			}

		};

		int socketTimeout = 10000;
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		strReq.setRetryPolicy(policy);
		// Adding request to request queue
		App.getInstance().addToRequestQueue(strReq);
	}

}