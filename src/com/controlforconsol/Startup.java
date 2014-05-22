package com.controlforconsol;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.Window;


public class Startup extends Activity {
    
	Timer timer;
//	private Controller controll;
	private MyTimerTask myTimerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        
//        controll = new Controller();
        
        if(timer!=null){
        	timer.cancel();
        }
        timer = new Timer();
        myTimerTask = new MyTimerTask(this);
        timer.schedule(myTimerTask, 500);
        
        System.out.println("AXAXA");
    }
    public void goToMainMenu() {
		Intent i = new Intent(getApplicationContext(), General.class);
//		i.putExtra("controller",(Parcelable) controll);
//		i.putExtra("message", "layout");
		startActivity(i);
		finish();
	}
}

class MyTimerTask extends TimerTask {

	Startup splash;
	public MyTimerTask(Startup splash){
		this.splash = splash;
	}
	  @Override
	  public void run() {
		  splash.goToMainMenu();
	  }

	
	  }