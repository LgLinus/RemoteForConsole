package com.controlforconsol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.StrictMode;



/**
 * A thread that connects to the server and sends messages
 * @author LgLinuss
 *
 */
public class Press extends Thread {
	General main = null;
	private boolean finished = true;
	private String sendInfo;

	public Press(General main) {
		this.main = main;
	}

	/**
	 * Stop the thread
	 */
	public void stopMe() {
		finished = true;
	}

	/**
	 * Runs when the thread is started
	 */
	@Override
	public void run() {
		while (true) {
			if (main.socket == null) {
				try {
					InetAddress serverAddress = InetAddress
							.getByName(main.SERVERIP);
					main.socket = new Socket(serverAddress, main.SERVERPORT); // Try and establish connection to the server
					this.sleep(1000);
					main.sendIp(); // Send the devices ip to the server, so the server can reach the device
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			while (!finished) { 
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.detectAll().penaltyLog().build(); 
				

				PrintWriter outp = null;
				try {
					if(main.socket!=null){
					outp = new PrintWriter(main.socket.getOutputStream(), true); // Allows us to send information
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
}
