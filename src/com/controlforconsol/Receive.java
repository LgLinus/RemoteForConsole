package com.controlforconsol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.os.StrictMode;

/**
 * A thread that can receive information from the console
 * 
 * @author LgLinuss
 * 
 */
public class Receive extends Thread {
	General main = null;
	private BufferedReader bufferedReader;
	private Socket clientSocket = null;

	public Receive(General main) {
		this.main = main;
	}

	/**
	 * Run the thread
	 */
	@Override
	public void run() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().build();
		try {
			main.receiveSocket = new ServerSocket(main.SERVERRECEIVEPORT);

			while (true) {
				if (main.receiveSocket != null
						&& main.receiveClientSocket == null) {
					try {
						main.receiveClientSocket = main.receiveSocket.accept(); // Accept

						bufferedReader = new BufferedReader(
								new InputStreamReader(
										main.receiveClientSocket
												.getInputStream()));
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				String inputLine = null;
				if ((bufferedReader != null && (inputLine = bufferedReader
						.readLine()) != null))
					System.out.println(inputLine); // Print out the information
													// in the console
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
