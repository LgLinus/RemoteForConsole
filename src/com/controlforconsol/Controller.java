package com.controlforconsol;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Controller object that handles the logic for the remote
 * 
 * @author LgLinuss
 * 
 */
public class Controller {

	public static int SERVERPORT = 8080;
	public final int SERVERRECEIVEPORT = 8081;
	private ServerSocket receiveSocket;
	private Socket receiveClientSocket = null, socket = null;
	private Receive receiveThread = null;
	private Sender sendThread = null;
	private General gui;
	private String SERVERIP = "192.168.1.41";
	private Gyro gyro;
	private Handler myHandler = new Handler();

	/**
	 * Construct the controller and initialize the threads
	 * 
	 * @param gui
	 */
	public Controller(General gui) {
		this.gui = gui;
		initializeThreads();
		gyro = new Gyro(this);
	}


	/**
	 * Called from the gui when a button is pressed. The controller handles this
	 * and forwards the command to the sender
	 * 
	 * @param v
	 *            View that is pressed (Button)
	 * @param event
	 *            that occurs(example, press, release)
	 */
	public void checkTouch(View v, MotionEvent event) {
		if (!(v instanceof DynamicLayoutButton)) {
			if (v == gui.getBtnLeft()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDLEFTPRESSED);
					gui.vibrate();
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDLEFTRELEASED);
			} else if (v == gui.getBtnUp()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDUPPRESSED);
						gui.vibrate();
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDUPRELEASED);
			} else if (v == gui.getBtnDown()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDDOWNPRESSED);
					gui.vibrate();
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDDOWNRELEASED);
			} else if (v == gui.getBtnRight()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDRIGHTPRESSED);
					gui.vibrate();
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDRIGHTRELEASED);
			} else if (v == gui.getBtnReconnect()) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					gui.vibrate();
					reconnect();
				}
			} else if (v == gui.getBtnPi()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDPIPRESSED);
					gui.vibrate();
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDPIRELEASED);
			} else if (v == gui.getBtnOhm()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendThread.send(Values.SENDOMEGAPRESSED);
					gui.vibrate();
				} else if (event.getAction() == MotionEvent.ACTION_UP)
					sendThread.send(Values.SENDOMEGARELEASED);
			}
		} else {
			DynamicLayoutButton btn = (DynamicLayoutButton) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				sendThread.send(btn.getValuePressed());
				gui.vibrate();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				sendThread.send(btn.getValueReleased());
			}
		}
	}

	/**
	 * Send the ip of the current device
	 */
	public void sendIp() {
		WifiManager wifiManager = (WifiManager) gui
				.getSystemService(Context.WIFI_SERVICE);
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

	/**
	 * Reestablish connection to the console by setting the socket to null.
	 */
	public void reconnect() {
		SERVERIP = gui.getEtIP().getText().toString();
		this.receiveClientSocket = null;
		if (receiveThread != null) {
			receiveThread.remove();
			receiveThread = null;
		}
		receiveThread = new Receive(this);
		try {
			if (this.socket != null)
				this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.socket = null;

		sendThread.send("Request connection");
	}

	/**
	 * Get the gyro
	 * 
	 * @return gyro
	 */
	public Gyro getGyro() {
		return gyro;
	}

	/**
	 * Get our sendthread
	 * 
	 * @return sendThread
	 */
	public Sender getSendThread() {
		return sendThread;
	}

	/**
	 * Get our receive socket
	 * 
	 * @return receiveSocket
	 */
	public ServerSocket getReceiveSocket() {
		return this.receiveSocket;
	}

	/**
	 * Get our receive client socket
	 * 
	 * @return receiveclientsocket
	 */
	public Socket getReceiveClientSocket() {
		return this.receiveClientSocket;
	}

	/**
	 * Set our receive socket
	 * 
	 * @param serverSocket
	 *            socket
	 */
	public void setReceiveSocket(ServerSocket serverSocket) {
		this.receiveSocket = serverSocket;
	}

	/**
	 * Get the port of the server
	 * 
	 * @return
	 */
	public int getPort() {
		return Controller.SERVERPORT;
	}

	/**
	 * Set our receive client socket
	 * 
	 * @param accept
	 *            socket
	 */
	public void setReceiveClientSocket(Socket accept) {
		this.receiveClientSocket = accept;
	}

	/**
	 * Get our socket
	 * 
	 * @return socket
	 */
	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * Retrieve the ip
	 * 
	 * @return ip
	 */
	public String getServerIP() {
		return this.SERVERIP;
	}

	/**
	 * Retrieve the serverport
	 * 
	 * @return
	 */
	int getServerPort() {
		return Controller.SERVERPORT;
	}

	/**
	 * Set the socket of our app
	 * 
	 * @param socket
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/**
	 * If app is closed, close down sockets and stop threads
	 */
	public void closeApplication() {
		try {
			if(socket!=null)
			this.socket.close();
			if(receiveSocket!=null)
			this.receiveSocket.close();
			if(receiveClientSocket!=null)
			this.receiveClientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		receiveSocket = null;
		receiveClientSocket = null;
		if(sendThread!=null)
		sendThread.stopThread();
		if(receiveThread!=null)
		receiveThread.stopThread();
		receiveThread = null;
		socket = null;
	}

	/**
	 * Change the player text of our gui
	 * 
	 * @param string
	 *            player
	 */
	public void changePlayerText(String string) {
		this.gui.changePlayerText(string);
	}

	/**
	 * Remove current layout, add our dynamic layout
	 */
	public void readFile() {
		removeLayout();
		addDynamicLayout();
		if (receiveThread != null) {
			receiveThread.remove();
			receiveThread = null;
		}
		receiveThread = new Receive(this);
	}

	/**
	 * Called from gui, add the buttons from the retrieved text file.
	 */
	public void readFileAddButtons() {
		BufferedReader br = null;
		try {
			FileInputStream fis = gui.getApplicationContext().openFileInput(
					"layout.txt");
			br = new BufferedReader(new InputStreamReader(fis));
			String line = "";
			while ((line = br.readLine()) != null) {
				read(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called to add our dynamic layout, executed in a runnable because we
	 * update our UI thread.
	 */
	public void addDynamicLayout() {
		final Runnable myRunnable = new Runnable() {
			public void run() {
				gui.addDynamicLayout();
			}
		};
		myHandler.post(myRunnable);
	}

	/**
	 * Add the standard layout to our gui
	 */
	public void addStandardLayout() {
		final Runnable myRunnable = new Runnable() {
			public void run() {
				gui.addStandardLayout();
			}
		};
		myHandler.post(myRunnable);
	}

	/**
	 * Remove the current layout of our gui
	 */
	public void removeLayout() {
		final Runnable myRunnable = new Runnable() {
			public void run() {
				gui.getLayout().removeAllViews();
			}
		};
		myHandler.post(myRunnable);
	}

	/**
	 * Get our gui
	 * 
	 * @return
	 */
	public General getGUI() {
		return gui;
	}

	// Read the current line of the layout file. Add a button to our dynamic
	// layout if instructions
	// Found in line.
	private void read(String line) {
		String[] split = line.split(",");
		if (split[0].equals("Button")) {

			DynamicLayoutButton btn = new DynamicLayoutButton(
					gui.getApplicationContext(), split[5], split[6]);
			gui.addDynamicButton(btn);
			ViewGroup.LayoutParams params = btn.getLayoutParams();
			int screenWidth = gui.getWidth(), screenHeight = gui.getHeight();
			btn.setX((int) (screenWidth * Double.valueOf(split[1])));
			btn.setY((int) (screenHeight * Double.valueOf(split[2])));
			params.width = (int) (screenWidth * Double.valueOf(split[3]));
			params.height = (int) (screenHeight * Double.valueOf(split[4]));
			if (split.length > 7)
				btn.setText(split[7]);
			btn.setOnTouchListener(gui.getButtonListener());
		}
	}

	// Initialize our threads
	private void initializeThreads() {

		if (sendThread == null) {
			sendThread = new Sender(this);
			sendThread.start();
		}
	}


	public Context getActivity() {
		return this.gui;
	}
}
