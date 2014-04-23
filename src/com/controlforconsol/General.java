package com.controlforconsol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.StateListDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
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

	private int width;
	private int height;
	private TextView tvPlayer;
	private LinearLayout layoutAllBottom;
	private RelativeLayout layoutLeft, layoutMiddle, rlTop;
	private ImageButton btnAction, btnLeft, btnUp, btnRight, btnDown, btnA,
			btnB;
	private Button btnReconnect;
	private StateListDrawable stateButtonUp, stateButtonDown, stateButtonRight,
			stateButtonLeft, stateButtonAction, stateButtonA, stateButtonB;
//	public String SERVERIP = "10.1.16.147";
	public String SERVERIP = "10.1.16.170";
	public int SERVERPORT = 8080,SERVERRECEIVEPORT = 8081;
	public ServerSocket receiveSocket;
	public Socket receiveClientSocket, socket;;
	private Receive receiveThread = null;
	private Press sendThread = null;
	private OnTouchListener buttonListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.control_general);
		createStates();
		createLayout();
		initializeThreads();
		setActionListeners();
	}

	/**
	 * Create the layout of the controller
	 */
	private void createLayout() {
		android.view.ViewGroup.LayoutParams params;
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;

		int arrowButtonWidth = (int) (width * .1);
		int arrowButtonHeight = (int) (height * 0.15);

		// Main layout
		LinearLayout layout = (LinearLayout) findViewById(R.id.linLayoutMain);
		layoutAllBottom = new LinearLayout(this);
		layoutLeft = new RelativeLayout(this);
		layoutMiddle = new RelativeLayout(this);
		rlTop = new RelativeLayout(this);
		btnAction = new ImageButton(this);
		btnDown = new ImageButton(this);
		btnUp = new ImageButton(this);
		btnRight = new ImageButton(this);
		btnLeft = new ImageButton(this);
		btnA = new ImageButton(this);
		btnB = new ImageButton(this);

		tvPlayer = new TextView(this);

		// Status layout
		layout.addView(rlTop);
		params = rlTop.getLayoutParams();
		params.height = (int) (size.y * 0.5);
		params.width = size.x;
		// rlTop.setBackgroundColor(Color.BLUE);

		layout.addView(layoutAllBottom);
		params = layoutAllBottom.getLayoutParams();
		params.height = (height - rlTop.getHeight());
		params.width = width;
		layoutAllBottom.setOrientation(LinearLayout.HORIZONTAL);
		// layoutAllBottom.setBackgroundColor(Color.GREEN);

		btnReconnect = new Button(this);
		btnReconnect.setHeight((int) (size.y * 0.15));
		btnReconnect.setWidth((int) (size.x * 0.25));
		btnReconnect.setText("Reconnect");

		layoutAllBottom.addView(layoutLeft);
		params = layoutLeft.getLayoutParams();
		params.height = (height - rlTop.getHeight());
		params.width = ((int) (width * 0.25));
		// layoutLeft.setBackgroundColor(Color.YELLOW);

		layoutAllBottom.addView(layoutMiddle);
		params = layoutMiddle.getLayoutParams();
		params.height = (height - rlTop.getHeight());
		params.width = ((int) (width * 0.75));
		// layoutMiddle.setBackgroundColor(Color.RED);

		layoutMiddle.addView(btnAction);
		params = btnAction.getLayoutParams();
		params.width = (int) (width * 0.5 - width * 0.1);
		params.height = (int) (height * 0.15);
		btnAction.setImageDrawable(stateButtonAction);
		btnAction.setBackground(null);
		btnAction.setScaleType(ScaleType.FIT_XY);
		btnAction.setX((int) (width * 0.05));
		btnAction.setY((int) (height / 3.5));
		btnAction.setPadding(0, 0, 0, 50);

		// ** Add navigation buttons **
		layoutLeft.addView(btnDown);
		params = btnDown.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnDown.setImageDrawable(stateButtonDown);
		btnDown.setX((int) (arrowButtonWidth * .85));
		btnDown.setY((int) (height * 0.11 + arrowButtonHeight));
		btnDown.setBackground(null);
		btnDown.setScaleType(ScaleType.FIT_XY);

		layoutLeft.addView(btnUp);
		params = btnUp.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnUp.setX((int) (arrowButtonWidth * .85));
		btnUp.setImageDrawable(stateButtonUp);
		btnUp.setY((int) (height * 0.19 - arrowButtonHeight));
		btnUp.setBackground(null);
		btnUp.setScaleType(ScaleType.FIT_XY);

		layoutLeft.addView(btnRight);
		params = btnRight.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnRight.setImageDrawable(stateButtonRight);
		btnRight.setX((int) ((width * 0.27) - (width * 0.02 + arrowButtonWidth)));
		btnRight.setY((int) (height * 0.15));
		btnRight.setBackground(null);
		btnRight.setScaleType(ScaleType.FIT_XY);

		layoutLeft.addView(btnLeft);
		params = btnLeft.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnLeft.setImageDrawable(stateButtonLeft);
		btnLeft.setX((int) (width * 0.02));
		btnLeft.setY((int) (height * 0.15));
		btnLeft.setBackground(null);
		btnLeft.setScaleType(ScaleType.FIT_XY);

		// A and B buttons

		layoutMiddle.addView(btnA);
		params = btnA.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnA.setImageDrawable(stateButtonA);
		btnA.setX((int) (width * 0.6));
		btnA.setY((int) (height * 0.10));
		btnA.setBackground(null);
		btnA.setScaleType(ScaleType.FIT_XY);

		layoutMiddle.addView(btnB);
		params = btnB.getLayoutParams();
		params.width = arrowButtonWidth;
		params.height = (int) (arrowButtonHeight);
		btnB.setImageDrawable(stateButtonB);
		btnB.setX((int) (width * 0.5));
		btnB.setY((int) (height * 0.20));
		btnB.setBackground(null);
		btnB.setScaleType(ScaleType.FIT_XY);

		tvPlayer.setText("Player: 1");
		tvPlayer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		tvPlayer.setTextSize(28);

		rlTop.addView(btnReconnect);
		rlTop.addView(tvPlayer);
		RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) btnReconnect
				.getLayoutParams();
		rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

	}
	

	@Override
	protected void onDestroy(){
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}
	 public void finish() {
	        /*
	         * This can only invoked by the user or the app finishing the activity
	         * by navigating from the activity so the HOME key was not pressed.
	         */
			try {
				this.socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        super.finish();
	    }

	    public void onStop() {
	        super.onStop();

	        /*
	         * Check if the HOME key was pressed. If the HOME key was pressed then
	         * the app will be killed. Otherwise the user or the app is navigating
	         * away from this activity so assume that the HOME key will be pressed
	         * next unless a navigation event by the user or the app occurs.
	         */		try {
	 			this.socket.close();
	 		} catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	    }
	/**
	 * Set listeners to each buttons, so we can detect when they are pressed and
	 * released
	 */
	public void setActionListeners() {
		buttonListener = new ButtonDownListener();
		btnLeft.setOnTouchListener(buttonListener);
		btnRight.setOnTouchListener(buttonListener);
		btnUp.setOnTouchListener(new ButtonDownListener());
		btnDown.setOnTouchListener(buttonListener);
		btnAction.setOnTouchListener(buttonListener);
		btnReconnect.setOnTouchListener(buttonListener);
		btnA.setOnTouchListener(buttonListener);
		btnB.setOnTouchListener(buttonListener);
	}

	/**
	 * Reestablish connection to the console by setting the socket to null.
	 */
	public void reconnect() {
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.socket = null;
	}

	/**
	 * Send the ip of the current device
	 */
	public void sendIp() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		sendThread.send(ip);
	}

	/**
	 * Return the given ip int as a String
	 * 
	 * @param i
	 *            ip to convert
	 * @return converted int to string
	 */
	public String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

	/** If we press the menu button on the phone */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			System.out.println("Look at my horse, my horse is amazing");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initializeThreads() {
		receiveThread = new Receive(this);
		receiveThread.start();
		sendThread = new Press(this);
		sendThread.start();
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

		stateButtonA = new StateListDrawable();
		stateButtonA.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttonpipressed));
		stateButtonA.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttonpi));
		stateButtonA.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttonpi));

		stateButtonB = new StateListDrawable();
		stateButtonB.addState(new int[] { android.R.attr.state_pressed },
				getResources().getDrawable(R.drawable.buttonomegapressed));
		stateButtonB.addState(new int[] { android.R.attr.state_focused },
				getResources().getDrawable(R.drawable.buttonomega));
		stateButtonB.addState(new int[] {},
				getResources().getDrawable(R.drawable.buttonomega));

	}

	private class ButtonDownListener implements OnTouchListener {

		boolean pressed = false;

		public boolean onTouch(View v, MotionEvent event) {
			if (v == btnLeft) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDLEFTPRESSED);
					Vibrator vib = (Vibrator) getApplicationContext()
							.getSystemService(Context.VIBRATOR_SERVICE);
					vib.vibrate(20);
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDLEFTRELEASED);
			} else if (v == btnUp) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDUPPRESSED);
					Vibrator vib = (Vibrator) getApplicationContext()
							.getSystemService(Context.VIBRATOR_SERVICE);
					vib.vibrate(20);
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDUPRELEASED);
			} else if (v == btnDown) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDDOWNPRESSED);
					Vibrator vib = (Vibrator) getApplicationContext()
							.getSystemService(Context.VIBRATOR_SERVICE);
					vib.vibrate(20);
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDDOWNRELEASED);
			} else if (v == btnRight) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDRIGHTPRESSED);
					Vibrator vib = (Vibrator) getApplicationContext()
							.getSystemService(Context.VIBRATOR_SERVICE);
					vib.vibrate(20);
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDRIGHTRELEASED);
			} else if (v == btnAction) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDRIGHTPRESSED);
					Vibrator vib = (Vibrator) getApplicationContext()
							.getSystemService(Context.VIBRATOR_SERVICE);
					vib.vibrate(20);
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDSPACE);
			} else if (v == btnReconnect) {
				if (event.getAction() == MotionEvent.ACTION_UP)
					reconnect();
			} else if (v == btnA) {
				System.out.println("A");
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDAPRESSED);
					Vibrator vib = (Vibrator) getApplicationContext()
							.getSystemService(Context.VIBRATOR_SERVICE);
					vib.vibrate(20);
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDARELEASED);
			} else if (v == btnB) {
				System.out.println("B");
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDBPRESSED);
					Vibrator vib = (Vibrator) getApplicationContext()
							.getSystemService(Context.VIBRATOR_SERVICE);
					vib.vibrate(20);
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDBRELEASED);
			}
			return pressed;
		}

	}
	

}