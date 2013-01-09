package org.waremon.soundmemory2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;

public class Help extends Activity implements OnTouchListener {
	
	private View view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.help);
        
        view = findViewById(R.id.help);
        view.setOnTouchListener(this);
   }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d("Debug", String.format("touched"));
		Intent i = new Intent(this, SoundMemory2.class);
		startActivity(i);
		return false;
	}
}