package org.waremon.soundmemory2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;  
import com.google.ads.AdSize;  
import com.google.ads.AdView;

public class Game extends Activity implements OnClickListener, OnTouchListener {

	private int TURN = -1;
	private int TOGLE = 0;
	private int CARD_NUM = 16;
	private int clicked = 16;
	private int CARD_SEQ = 0;
	private int kingpoint;
	private int queenpoint;
	private int END_FLAG;
	private int[] card_state;
	private int[] card_sound;
	private int[] card_sound_which;
	ImageView king_image;
	ImageView queen_image;
	ImageView king_point_image;
	ImageView queen_point_image;
	ImageButton[] cards;
	ImageView win;
	private MediaPlayer mp = null;
	int game_num = 1;
	int level_num = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		//kingとqueenの得点
		kingpoint = 0;
		queenpoint = 0;

		//レベルの受け取り
		Bundle extras = getIntent().getExtras();
		int level = extras.getInt("level");
		if (level == 2) {
			level_num = 2;
			CARD_NUM = 32;
		} else if (level == 3) {
			game_num = 2;
		} else if (level == 4) {
			game_num = 2;
			level_num = 2;
			CARD_NUM = 32;
		}
		clicked = CARD_NUM;
		card_state = new int[CARD_NUM];
		card_sound = new int[CARD_NUM+1];
		card_sound_which = new int[CARD_NUM+1];
		cards = new ImageButton[CARD_NUM];

		setContentView(getResources().getIdentifier("level"+level_num, "layout", getPackageName()));

		for(int i=1; i<=CARD_NUM; i++) {
			cards[i-1] = (ImageButton) findViewById(getResources().getIdentifier("card"+i, "id", getPackageName()));
			cards[i-1].setOnClickListener(this);
			card_state[i-1]=0;
			//2でわる->同じ整数2つ
			card_sound[i-1]=(int)((i-1)/2);
			if (game_num == 1) {
				//全カード0 -> 音
				card_sound_which[i-1] = 0;
			} else {
				//2で割ってあまりだけ1 -> 音でない
				card_sound_which[i-1] = (int)((i-1)%2);
			}
		}

		shuffle(card_sound, card_sound_which);
		card_sound[CARD_NUM]=-1;

		//勝敗ボタンセット
		win = (ImageView) findViewById(R.id.win_pic);
		win.setVisibility(View.INVISIBLE);

		//各イメージセット
		king_image = (ImageView) findViewById(R.id.player1);
		queen_image = (ImageView) findViewById(R.id.player2);
		king_point_image = (ImageView) findViewById(R.id.player1point);
		queen_point_image = (ImageView) findViewById(R.id.player2point);

		//ゲーム終了フラグ
		END_FLAG = 0;
		
		// create adView
		AdView adView = new AdView(this, AdSize.BANNER, "a14fd30d0b926f6");
		// get View for ad
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout3);
		// add adView
		layout.addView(adView);
		// request ad
		AdRequest request = new AdRequest();
        adView.loadAd(request);
	}

	@Override
	public void onClick(View v) {
		for(int i=1; i<=CARD_NUM; i++) {
			//クリックされたカードだったら、かつ、前にクリックしていなかったら
			if (v == cards[i-1] && clicked != i-1) {
				game(i);
				//Log.d("Debug", String.format("king:%d queen:%d",kingpoint, queenpoint));
			}
		}
		//turnが変わるときに顔変える
		if (TOGLE == 1) {
			int king_num = (int)Math.floor(Math.random()*10)+1;
			int queen_num = (int)Math.floor(Math.random()*10)+1;
			if (TURN == -1) {
				king_image.setImageResource(getResources().getIdentifier("player1_"+king_num, "drawable", getPackageName()));
				queen_image.setImageResource(getResources().getIdentifier("player2_"+queen_num+"_rev", "drawable", getPackageName()));
			} else {
				king_image.setImageResource(getResources().getIdentifier("player1_"+king_num+"_rev", "drawable", getPackageName()));
				queen_image.setImageResource(getResources().getIdentifier("player2_"+queen_num, "drawable", getPackageName()));
			}
			TOGLE = 0;
		}
	}

	public void gameEnd() {
		for (int i=1; i<=CARD_NUM; i++) {
			cards[i-1].setOnClickListener(null);
		}
		if (kingpoint > queenpoint) {
			win.setVisibility(View.VISIBLE);
		} else if (kingpoint < queenpoint){
			win.setImageResource(R.drawable.player2win);
			win.setVisibility(View.VISIBLE);
		} else {
			win.setImageResource(R.drawable.draw);
			win.setVisibility(View.VISIBLE);
		}
		win.setOnTouchListener(this);
		END_FLAG = 1;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d("Debug", String.format("touched!"));
		if (END_FLAG == 1) {
			Intent i = new Intent(this, SoundMemory2.class);
			startActivity(i);
		}
		return false;
	}

	public void setPoint(String player) {
		if (player == "king") {
			king_point_image.setImageResource(getResources().getIdentifier("number"+kingpoint, "drawable", getPackageName()));
		} else if (player == "queen") {
			queen_point_image.setImageResource(getResources().getIdentifier("number"+queenpoint, "drawable", getPackageName()));
		}
	}

	public void playSound(int sound) {
		if (mp != null) {
			mp.stop();
			mp.reset();
			mp.release();
		}
		mp = MediaPlayer.create(this, getResources().getIdentifier("sound"+sound, "raw", getPackageName()));
		mp.start();
	}

	public static void shuffle(int[] arr1, int[] arr2){
		for(int i=arr1.length-2; i>0; i--){
			int t = (int)(Math.random() * i);
			int tmp1 = arr1[i];
			arr1[i]  = arr1[t];
			arr1[t] = tmp1;
			int tmp2 = arr2[i];
			arr2[i]  = arr2[t];
			arr2[t] = tmp2;
		}
	}
	
	public void touchAnimation(int num, String card) {
		if (card == "") {
			if (card_sound_which[num] == 1) {
				int tmp = card_sound[num] + 1;
				cards[num].setImageResource(getResources().getIdentifier("animnune_pannel"+tmp, "drawable", getPackageName()));
			} else {
				cards[num].setImageResource(getResources().getIdentifier("animnune", "drawable", getPackageName()));
			}
		} else if (card == "king") {
			if (card_sound_which[num] == 1) {
				int tmp = card_sound[num] + 1;
				cards[num].setImageResource(getResources().getIdentifier("animking_pannel"+tmp, "drawable", getPackageName()));
			} else {
				cards[num].setImageResource(getResources().getIdentifier("animking", "drawable", getPackageName()));
			}
		} else if (card == "queen") {
			if (card_sound_which[num] == 1) {
				int tmp = card_sound[num] + 1;
				cards[num].setImageResource(getResources().getIdentifier("animqueen_pannel"+tmp, "drawable", getPackageName()));
			} else {
				cards[num].setImageResource(getResources().getIdentifier("animqueen", "drawable", getPackageName()));
			}
		}
		AnimationDrawable anim = (AnimationDrawable)cards[num].getDrawable();
		anim.setOneShot(true); // 1回で終わる
		if(anim.isRunning()) anim.stop(); // アニメーション中なら止める
		anim.start();
	}
	
	public void game(int i) {
		//ゲーム２の文字カードじゃなければ音をならす
		if (card_sound_which[i-1] == 0) {
			playSound(card_sound[i-1]+1);
		}
		//前にクリックされたものと同じ番号のカードだったら
		if (card_sound[i-1] == card_sound[clicked]) {
			if (TURN == -1) {//king's turn
				cards[i-1].setOnClickListener(null);
				touchAnimation(i-1, "king");
				cards[clicked].setOnClickListener(null);
				touchAnimation(clicked, "king");
				kingpoint ++;
				setPoint("king");
			} else {//queen's turn
				cards[i-1].setOnClickListener(null);
				touchAnimation(i-1, "queen");
				cards[clicked].setOnClickListener(null);
				touchAnimation(clicked, "queen");
				queenpoint ++;
				setPoint("queen");
			}
			clicked = CARD_NUM;
			//枚数リセット
			CARD_SEQ=0;
			//ゲーム終わり
			if (kingpoint > CARD_NUM/4 || queenpoint > CARD_NUM/4 || (kingpoint == CARD_NUM/4 && queenpoint == CARD_NUM/4)) {
				gameEnd();
			}
		//1枚目なら(めくってないカード && めくったカード0)
		} else if (card_state[i-1] == 0 && CARD_SEQ < 1) {
			// game1か、音カードであれば
			if (game_num != 2 || card_sound_which[i-1] == 0) {
				cards[i-1].setImageResource(getResources().getIdentifier("card_icon_rev", "drawable", getPackageName()));
			} else {
				int tmp = card_sound[i-1]+1;
				cards[i-1].setImageResource(getResources().getIdentifier("pannel"+tmp, "drawable", getPackageName()));
			}
			card_state[i-1] = 1;
			CARD_SEQ = 1;
			clicked = i-1;
		//はずれなら
		} else {
			touchAnimation(i-1, "");
			touchAnimation(clicked, "");
			card_state[i-1] = 0;
			card_state[clicked] = 0;
			clicked = CARD_NUM;
			CARD_SEQ=0;
			TURN *= -1;
			TOGLE = 1;
		}
	}
}
