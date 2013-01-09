package org.waremon.soundmemory2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class SoundMemory2 extends Activity implements OnClickListener {
	
	ImageButton[] levelBtn = new ImageButton[5];
	
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        
        for (int i=1; i<=4; i++) {
        	levelBtn[i-1] = (ImageButton) findViewById(getResources().getIdentifier("imageLevel"+i, "id", getPackageName()));
        	levelBtn[i-1].setOnClickListener(this);
        }
        levelBtn[4] = (ImageButton) findViewById(getResources().getIdentifier("help", "id", getPackageName()));
        levelBtn[4].setOnClickListener(this);
   }
        
	@Override
	public void onClick(View v) {
		for (int i=1; i<=4; i++) {
			if (v == levelBtn[i-1]) {
				Intent intent = new Intent(this, Game.class);
				intent.putExtra("level", i);
				startActivity(intent);
			}
		}
		if (v == levelBtn[4]) {
			Intent intent = new Intent(this, Help.class);
			startActivity(intent);
		}
	}
}