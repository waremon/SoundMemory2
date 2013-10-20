package org.waremon.soundmemory2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class Top extends Activity implements OnClickListener {
	
	ImageButton[] pButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_top);
		
		pButton = new ImageButton[2];
		for(int i=1; i<=2; i++) {
			pButton[i-1] = (ImageButton) findViewById(getResources().getIdentifier("play"+i, "id", getPackageName()));
			pButton[i-1].setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == pButton[0]) {
			Intent i = new Intent(this, Play1.class);
			startActivity(i);
		}
	}
}
