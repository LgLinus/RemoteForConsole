package com.controlforconsol;

import android.content.Context;
import android.widget.Button;

public class DynamicLayoutButton extends Button {

	private String valuePressed;
	private String valueReleased;
	
	public DynamicLayoutButton(Context context,String pressed,String released) {
		super(context);
		this.valuePressed = pressed;
		this.valueReleased = released;
	}

	public String getValuePressed(){
		return this.valuePressed;
	}
	public String getValueReleased(){
		return this.valueReleased;
	}
	
}
