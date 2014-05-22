package com.controlforconsol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * The general layout for the remote V0.2
 * 
 * @author LgLinus
 * 
 */
public class General extends Activity {

	private int width, height;
	private TextView tvPlayer;
	private LinearLayout layoutAllBottom;
	private LinearLayout linLayout;
	private RelativeLayout layoutLeft, layoutMiddle, rlTop;
	private ImageButton btnLeft, btnUp, btnRight, btnDown, btnPi, btnOhm;
	private Button btnReconnect;
	private StateListDrawable stateButtonUp, stateButtonDown, stateButtonRight,
			stateButtonLeft, stateButtonAction, stateButtonPi,
			stateButtonOmega;
	private EditText etIP;
	private Controller controller;
	private OnTouchListener buttonListener;
	private Handler myHandler = new Handler();

	/**
	 * Called when the activity is created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		controller = new Controller(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.control_general);
		createStates();
		addStandardLayout();
	}

	public void addDynamicButton(DynamicLayoutButton btn) {
		rlTop.addView(btn);
	}

	@Override
	protected void onPause() {
		super.onPause();
		controller.closeApplication();
		finish();
	}

	// when this Activity starts
	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * register the sensor listener to listen to the gyroscope sensor, use
		 * the callbacks defined in this class, and gather the sensor
		 * information as quick as possible
		 */
	}

	/**
	 * Set listeners to each buttons, so we can detect when they are pressed and
	 * released
	 */
	public void setActionListeners() {
		buttonListener = new ButtonDownListener();
		btnLeft.setOnTouchListener(buttonListener);
		btnRight.setOnTouchListener(buttonListener);
		btnUp.setOnTouchListener(buttonListener);
		btnDown.setOnTouchListener(buttonListener);
		btnReconnect.setOnTouchListener(buttonListener);
		btnPi.setOnTouchListener(buttonListener);
		btnOhm.setOnTouchListener(buttonListener);
	}

	/** If we press the menu button on the phone */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			controller.removeLayout();
			controller.addStandardLayout();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Create the states of the buttons. I.e which images to display when
	 * pressed/released
	 */
	private void createStates() {
		stateButtonUp = new StateListDrawable();
		stateButtonUp.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttonuppressed));
		stateButtonUp.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttonup));
		stateButtonUp.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttonup));

		stateButtonDown = new StateListDrawable();
		stateButtonDown.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttondownpressed));
		stateButtonDown.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttondown));
		stateButtonDown.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttondown));

		stateButtonRight = new StateListDrawable();
		stateButtonRight.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttonrightpressed));
		stateButtonRight.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttonright));
		stateButtonRight.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttonright));

		stateButtonLeft = new StateListDrawable();
		stateButtonLeft.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttonleftpressed));
		stateButtonLeft.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttonleft));
		stateButtonLeft.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttonleft));

		stateButtonAction = new StateListDrawable();
		stateButtonAction.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttonactionpressed));
		stateButtonAction.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttonaction));
		stateButtonAction.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttonaction));

		stateButtonAction = new StateListDrawable();
		stateButtonAction.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttonactionpressed));
		stateButtonAction.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttonaction));
		stateButtonAction.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttonaction));

		stateButtonPi = new StateListDrawable();
		stateButtonPi.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttonpipressed));
		stateButtonPi.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttonpi));
		stateButtonPi.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttonpi));

		stateButtonOmega = new StateListDrawable();
		stateButtonOmega.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttonomegapressed));
		stateButtonOmega.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttonomega));
		stateButtonOmega.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttonomega));

	}

	/**
	 * Vibrate the device for 20ms
	 */
	public void vibrate() {

		Vibrator vib = (Vibrator) getApplicationContext().getSystemService(
				Context.VIBRATOR_SERVICE);
		vib.vibrate(20);
	}

	/**
	 * Change the player text
	 * 
	 * @param string
	 *            text
	 */
	public void changePlayerText(final String string) {
		final Runnable myRunnable = new Runnable() {
			public void run() {
				tvPlayer.setText("Player: " + string);
			}
		};
		myHandler.post(myRunnable);
	}

	/**
	 * Add our dynamiclayout
	 */
	public void addDynamicLayout() {
		createDynamicLayout();
	}

	// Class listening for button events. Forwards any input to our controller
	// class
	private class ButtonDownListener implements OnTouchListener {

		boolean pressed = false;

		public boolean onTouch(View v, MotionEvent event) {
			controller.checkTouch(v, event);
			return pressed;
		}

	}

	/**
	 * Create the standard layout of the controller
	 */
	public void addStandardLayout() {
		android.view.ViewGroup.LayoutParams params;
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;

		int arrowButtonWidth = (int) (width * .15);
		int arrowButtonHeight = (int) (height * 0.25);

		// Main layout
		linLayout = (LinearLayout) findViewById(R.id.linLayoutMain);
		layoutAllBottom = new LinearLayout(this);
		layoutLeft = new RelativeLayout(this);
		layoutMiddle = new RelativeLayout(this);
		rlTop = new RelativeLayout(this);
		btnDown = new ImageButton(this);
		btnUp = new ImageButton(this);
		btnRight = new ImageButton(this);
		btnLeft = new ImageButton(this);
		btnPi = new ImageButton(this);
		btnOhm = new ImageButton(this);
		tvPlayer = new TextView(this);

		// Status layout
		linLayout.addView(rlTop);
		params = rlTop.getLayoutParams();
		params.height = (int) (size.y * 0.30);
		params.width = size.x;

		linLayout.addView(layoutAllBottom);
		params = layoutAllBottom.getLayoutParams();
		params.height = (height - rlTop.getHeight());
		params.width = width;
		layoutAllBottom.setOrientation(LinearLayout.HORIZONTAL);

		btnReconnect = new Button(this);
		btnReconnect.setHeight((int) (size.y * 0.15));
		btnReconnect.setWidth((int) (size.x * 0.25));
		btnReconnect.setText("Reconnect");

		etIP = new EditText(this);
		etIP.setHeight((int) (size.y * 0.15));
		etIP.setWidth((int) (size.x * 0.55));
		etIP.setHint("Ip");
		etIP.setText(controller.getServerIP());
		layoutAllBottom.addView(layoutLeft);
		params = layoutLeft.getLayoutParams();
		params.height = (height - rlTop.getHeight());
		params.width = ((int) (width * 0.40));

		layoutAllBottom.addView(layoutMiddle);
		params = layoutMiddle.getLayoutParams();
		params.height = (height - rlTop.getHeight());
		params.width = ((int) (width * 0.60));

		// ** Add navigation buttons **
		layoutLeft.addView(btnDown);
		params = btnDown.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnDown.setImageDrawable(stateButtonDown);
		btnDown.setX((int) (arrowButtonWidth * .85));
		btnDown.setY((int) (height * 0.16 + arrowButtonHeight));
		btnDown.setBackground(null);
		btnDown.setScaleType(ScaleType.FIT_XY);

		layoutLeft.addView(btnUp);
		params = btnUp.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnUp.setX((int) (arrowButtonWidth * .85));
		btnUp.setImageDrawable(stateButtonUp);
		btnUp.setY((int) (height * 0.24 - arrowButtonHeight));
		btnUp.setBackground(null);
		btnUp.setScaleType(ScaleType.FIT_XY);

		layoutLeft.addView(btnRight);
		params = btnRight.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnRight.setImageDrawable(stateButtonRight);
		btnRight.setX((int) ((width * 0.414) - (width * 0.02 + arrowButtonWidth)));
		btnRight.setY((int) (height * 0.20));
		btnRight.setBackground(null);
		btnRight.setScaleType(ScaleType.FIT_XY);

		layoutLeft.addView(btnLeft);
		params = btnLeft.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnLeft.setImageDrawable(stateButtonLeft);
		btnLeft.setX((int) (width * 0.0145));
		btnLeft.setY((int) (height * 0.20));
		btnLeft.setBackground(null);
		btnLeft.setScaleType(ScaleType.FIT_XY);

		// A and B buttons

		layoutMiddle.addView(btnPi);
		params = btnPi.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnPi.setImageDrawable(stateButtonPi);
		btnPi.setX((int) (width * 0.45));
		btnPi.setY((int) (height * 0.25));
		btnPi.setBackground(null);
		btnPi.setScaleType(ScaleType.FIT_XY);

		layoutMiddle.addView(btnOhm);
		params = btnOhm.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnOhm.setImageDrawable(stateButtonOmega);
		btnOhm.setX((int) (width * 0.30));
		btnOhm.setY((int) (height * 0.40));
		btnOhm.setBackground(null);
		btnOhm.setScaleType(ScaleType.FIT_XY);

		tvPlayer.setText("Player: 1");
		tvPlayer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		tvPlayer.setTextSize(28);
		tvPlayer.setY((float) (height * 0.2));

		rlTop.addView(btnReconnect);
		rlTop.addView(tvPlayer);
		rlTop.addView(etIP);
		RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) btnReconnect
				.getLayoutParams();
		rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		setActionListeners();
	}

	private void createDynamicLayout() {
		buttonListener = new ButtonDownListener();
		android.view.ViewGroup.LayoutParams params;
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;

		// Main layout
		linLayout = (LinearLayout) findViewById(R.id.linLayoutMain);
		rlTop = new RelativeLayout(this);
		linLayout.addView(rlTop);
		params = rlTop.getLayoutParams();
		params.height = (int) (size.y);
		params.width = size.x;
		btnReconnect = new Button(this);
		btnReconnect.setHeight((int) (size.y * 0.15));
		btnReconnect.setWidth((int) (size.x * 0.25));
		btnReconnect.setText("Reconnect");
		btnReconnect.setOnTouchListener(buttonListener);

		etIP = new EditText(this);
		etIP.setHeight((int) (size.y * 0.15));
		etIP.setWidth((int) (size.x * 0.55));
		etIP.setHint("Ip");
		etIP.setText(controller.getServerIP());
		tvPlayer = new TextView(this.getApplicationContext());
		tvPlayer.setText("Player: 1");
		tvPlayer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		tvPlayer.setTextSize(28);
		tvPlayer.setY((float) (height * 0.2));

		rlTop.addView(btnReconnect);
		rlTop.addView(tvPlayer);
		rlTop.addView(etIP);
		RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) btnReconnect
				.getLayoutParams();
		rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		controller.readFileAddButtons();
	}

	// Start of get/set methods
	public ImageButton getBtnLeft() {
		return btnLeft;
	}

	public ImageButton getBtnUp() {
		return btnUp;
	}

	public ImageButton getBtnRight() {
		return btnRight;
	}

	public ImageButton getBtnDown() {
		return btnDown;
	}

	public ImageButton getBtnPi() {
		return btnPi;
	}

	public ImageButton getBtnOhm() {
		return btnOhm;
	}

	public Button getBtnReconnect() {
		return btnReconnect;
	}

	public EditText getEtIP() {
		return this.etIP;
	}

	public LinearLayout getLayout() {
		return linLayout;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public OnTouchListener getButtonListener() {
		return this.buttonListener;
	}

	public void setWidth(int x) {
		this.width = x;
	}

	public void setHeight(int y) {
		this.height = y;
	}

	// End of get and set methods

}