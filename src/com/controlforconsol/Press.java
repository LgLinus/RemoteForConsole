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
//	Socket socket = null; // Socket som anv�nds f�r att n� servern
	private boolean finished = true;
	private String sendInfo;

	public Press(General main) {
		this.main = main;
	}

	/**
	 * Stoppar tr�den
	 */
	public void stopMe() {
		finished = true;
	}

	/**
	 * K�rs n�r tr�den startas
	 */
	@Override
	public void run() {
		while (true) { // loopa hela tiden
			//Om vi ej har stoppat tr�den utf�r nedan kod
			while (!finished) { 
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.detectAll().penaltyLog().build(); // Inte helt hundra p� vad detta g�r
				// om v�ran socket �r null f�rs�ker vi skapa en anslutning till servern
				if (main.socket == null) {
					try {
						InetAddress serverAddress = InetAddress
								.getByName(main.SERVERIP);
						main.socket = new Socket(serverAddress, main.SERVERPORT); // F�rs�k uppr�tta en anslutning till servern
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				PrintWriter outp = null;
				
				// F�rs�k skicka ett v�rde som �r lagrat lokalt f�r Press objektet till servern.
				// Exempel ett v�rde som motsvarar ett knapp tryck
				try {
					if(main.socket!=null){
					outp = new PrintWriter(main.socket.getOutputStream(), true); // Till�t oss skicka
					outp.println(sendInfo); // Skicka v�rdet
					stopMe(); // Stoppa tr�den s� vi inte forts�tter skicka hela tiden
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
	 * Starta tr�den
	 */
	public void startThread() {
		finished = false;
	}

	/**
	 * Kallas f�r att skicka ett v�rde. Startar ocks� tr�den
	 * @param button v�rde som representerar en knapp
	 */
	public void send(String button) {
		sendInfo = button;
		finished = false;
		System.out.println(button);
	}
}
