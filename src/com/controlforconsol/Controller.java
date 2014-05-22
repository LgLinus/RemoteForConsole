package com.controlforconsol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Controller object that handles the logic for the remote
 * 
 * @author LgLinuss
 * 
 */
public class Controller implements Parcelable {

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
	 * Construct the controller and initialize the threads
	 * 
	 * @param gui
	 */
	public Controller() {
		System.out.println(this.toString());
//		gyro = new Gyro(this);
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
		System.out.println("CALLED");
		if(!(v instanceof DynamicLayoutButton)){
			System.out.println("PRESSED");
		if (v == gui.getBtnLeft()) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				sendThread.send(Values.SENDLEFTPRESSED);
				gui.vibrate();
			} else if (event.getAction() == MotionEvent.ACTION_UP)
				sendThread.send(Values.SENDLEFTRELEASED);
		} else if (v == gui.getBtnUp()) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if(sendThread!=null)
				sendThread.send("12");
				if(gui!=null)
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
		}
		else if (v == gui.getBtnReconnect()) {
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
		}
		else{
			System.out.println("PRESSED DYNAMIC");
			DynamicLayoutButton btn = (DynamicLayoutButton) v;
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				sendThread.send(btn.getValuePressed());
				gui.vibrate();
			}
			else if(event.getAction()==MotionEvent.ACTION_UP){
				sendThread.send(btn.getValueReleased());
			}
		}
	}

	/**
	 * Send the ip of the current device
	 */
	public void sendIp() {
		WifiManager wifiManager = (WifiManager) gui
				.getSystemService(gui.WIFI_SERVICE);
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
		if(receiveThread!=null){
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

	private void initializeThreads() {
		
		if(sendThread==null){
			System.out.println("initalize sendthread");
		sendThread = new Sender(this);
		sendThread.start();}
	}

	public Activity getActivity() {
		// TODO Auto-generated method stub
		return gui;
	}

	public Gyro getGyro() {
		return gyro;
	}

	public Sender getSendThread() {
		return sendThread;
	}

	public ServerSocket getReceiveSocket() {
		return this.receiveSocket;
	}

	public Socket getReceiveClientSocket() {
		return this.receiveClientSocket;
	}

	public void setReceiveSocket(ServerSocket serverSocket) {
		this.receiveSocket = serverSocket;
	}

	public int getPort() {
		return this.SERVERPORT;
	}

	public void setReceiveClientSocket(Socket accept) {
		this.receiveClientSocket = accept;
	}

	public Socket getSocket() {
		return this.socket;
	}

	public String getServerIP() {
		return this.SERVERIP;
	}

	int getServerPort() {
		return this.SERVERPORT;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void closeApplication() {
		try {
			socket.close();
			receiveSocket.close();
			this.receiveClientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		receiveSocket = null;
		receiveClientSocket = null;
		sendThread.stopThread();
		receiveThread = null;
		socket = null;
	}

	public void changePlayerText(String string) {
		this.gui.changePlayerText(string);
	}

	public void readFile() {
		System.out.println("START PLEASE");
//		Intent i = new Intent(gui.getApplicationContext(), General.class);
//		i.putExtra("controller", (Parcelable)this);
//		i.putExtra("message", "layout");
		removeLayout();
		addDynamicLayout();
//		gui.startActivity(i);
		if(receiveThread!=null){
			receiveThread.remove();
			receiveThread = null;
		}
		receiveThread = new Receive(this);
}

	public void addDynamicLayout() {
		final Runnable myRunnable = new Runnable() {
			   public void run() {
					gui.addDynamicLayout();
				   }
				};
				myHandler.post(myRunnable);
	}
	
	public void addGeneralLayout() {
		final Runnable myRunnable = new Runnable() {
			   public void run() {
					gui.addGeneralLayout();
				   }
				};
				myHandler.post(myRunnable);
	}


	public void removeLayout() {
		final Runnable myRunnable = new Runnable() {
			   public void run() {
					gui.getLayout().removeAllViews();
				   }
				};
				myHandler.post(myRunnable);
	}


	private void read(String line) {
		String[] split = line.split(",");
		if(split[0].equals("Button")){
//			LayoutButton btn = new LayoutButton(this.getApplicationContext(),split[5]);
//			layout.addView(btn);
//			ViewGroup.LayoutParams params = btn.getLayoutParams(); 
//			btn.setX((int) (screenWidth * Double.valueOf(split[1])));
//			btn.setY((int) (screenHeight *Double.valueOf(split[2])));
//			params.width = (int) (screenWidth*Double.valueOf(split[3]));
//			params.height = (int) (screenHeight*Double.valueOf(split[4]));
//			btn.setOnTouchListener(buttonListener);
			Button btn = new Button(gui.getApplicationContext());
			gui.getLayout().addView(btn);
			ViewGroup.LayoutParams params = btn.getLayoutParams(); 
			int screenWidth = gui.getWidth(),screenHeight = gui.getHeight();
			btn.setX((int) (screenWidth * Double.valueOf(split[1])));
			btn.setY((int) (screenHeight *Double.valueOf(split[2])));
			params.width = (int) (screenWidth*Double.valueOf(split[3]));
			params.height = (int) (screenHeight*Double.valueOf(split[4]));
//			btn.setOnTouchListener(buttonListener);
		}
		}

	public General getGUI() {
		return gui;
	}
	public void setGUI(General general){
			this.gui = general;
			initializeThreads();
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel out, int flags) {
//		out.writeParcelable(socket, flags);
//		out.writeParcelable(receiveClientSocket, flags);
		out.writeInt(flags);
	}
	  // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Controller> CREATOR = new Parcelable.Creator<Controller>() {
        public Controller createFromParcel(Parcel in) {
        	
            return new Controller(in);
        }

        public Controller[] newArray(int size) {
            return new Controller[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Controller(Parcel in) {
//    	this.socket = (SocketParceable)in.readParcelable(SocketParceable.class.getClassLoader());
//    	this.receiveClientSocket =  (SocketParceable)in.readParcelable(SocketParceable.class.getClassLoader());
    	this.sendThread = new Sender(this);
    }
	}
