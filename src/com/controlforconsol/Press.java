package com.controlforconsol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.apache.http.client.utils.URIUtils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.text.format.Formatter;

/**
 * A thread that connects to the server and sends messages
 * @author LgLinuss
 *
 */
public class Press extends Thread {
	General main = null;
//	Socket socket = null; // Socket som används för att nå servern
	private boolean finished = true;
	private String sendInfo;

	public Press(General main) {
		this.main = main;
	}

	/**
	 * Stoppar tråden
	 */
	public void stopMe() {
		finished = true;
	}

	/**
	 * Körs när tråden startas
	 */
	@Override
	public void run() {
		while (true) { // loopa hela tiden
			//Om vi ej har stoppat tråden utför nedan kod
			while (!finished) { 
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.detectAll().penaltyLog().build(); // Inte helt hundra på vad detta gör
				// om våran socket är null försöker vi skapa en anslutning till servern
				if (main.socket == null) {
					try {
						InetAddress serverAddress = InetAddress
								.getByName(main.SERVERIP);
						main.socket = new Socket(serverAddress, main.SERVERPORT); // Försök upprätta en anslutning till servern
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				PrintWriter outp = null;
				
				// Försök skicka ett värde som är lagrat lokalt för Press objektet till servern.
				// Exempel ett värde som motsvarar ett knapp tryck
				try {
					if(main.socket!=null){
					outp = new PrintWriter(main.socket.getOutputStream(), true); // Tillåt oss skicka
					outp.println(sendInfo); // Skicka värdet
					stopMe(); // Stoppa tråden så vi inte fortsätter skicka hela tiden
//					if(sendInfo==Values.SENDEXIT){
//						try {
//							main.socket.close();
//							main.socket=null;
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
				}} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * Starta tråden
	 */
	public void startThread() {
		finished = false;
	}

	/**
	 * Kallas för att skicka ett värde. Startar också tråden
	 * @param button värde som representerar en knapp
	 */
	public void send(String button) {
		sendInfo = button;
		finished = false;
		System.out.println(button);
	}
}
