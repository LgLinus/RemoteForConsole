package com.controlforconsol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;



/**
 * A thread that connects to the server and sends messages
 * @author LgLinuss
 *
 */
public class Sender extends Thread implements Parcelable {
	Controller main = null;
	private boolean finished = true;
	private String sendInfo;
	private int mData;

	public Sender(Controller controller) {
		this.main = controller;
	}

	/**
	 * Stop the thread
	 */
	public void stopMe() {
		finished = true;
	}
	
	@SuppressWarnings("deprecation")
	public void stopThread(){
		finished = true;
		this.stop();
	}

	/**
	 * Runs when the thread is started
	 */
	@Override
	public void run() {
		while (true) {
			try{
				Thread.sleep(5);
			}
			catch(InterruptedException e){
				
			}
			while (!finished) { 
				if (main.getSocket() == null) {
					try {
						InetAddress serverAddress = InetAddress
								.getByName(main.getServerIP());
						Socket socket = new Socket();
						socket.connect(new InetSocketAddress(serverAddress, main.getServerPort()),5000);
//						socket.setSoTimeout(5000);
						main.setSocket(socket); // Try and establish connection to the server
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						main.sendIp(); // Send the devices ip to the server, so the server can reach the device
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}     
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.detectAll().penaltyLog().build(); 	
				
		
				PrintWriter outp = null;
				try {
					if(main.getSocket()!=null){
					outp = new PrintWriter(main.getSocket().getOutputStream(), true); // Allows us to send information
					outp.println(sendInfo); // Skicka värdet
					stopMe(); // Stop the thread so we don't resend the information
				}} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	/**
	 * Starta the thread
	 */
	public void startThread() {
		finished = false;
	}

	/**
	 * Called when we want to send a value
	 * @param button information to send
	 */
	public void send(String button) {
		sendInfo = button;
		finished = false;
		System.out.println(button);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(main, flags);
		out.writeInt(flags);
	}
	  // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Sender> CREATOR = new Parcelable.Creator<Sender>() {
        public Sender createFromParcel(Parcel in) {
            return new Sender(in);
        }

        public Sender[] newArray(int size) {
            return new Sender[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Sender(Parcel in) {
    	main = in.readParcelable(Controller.class.getClassLoader());
        mData = in.readInt();
    }
}
