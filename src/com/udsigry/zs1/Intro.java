package com.udsigry.zs1;

import com.udsigry.zs1.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Intro extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 * 
	 * 
	 */
	private final int SPLASH_DISPLAY_LENGHT = 2500;
	private static final boolean AUTO_HIDE = true;
	public SharedPreferences sharedPrefs;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_intro);


		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
			
					

		// Set up the user interaction to manually show or hide the system UI.
		

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(sharedPrefs.getBoolean("welcome", false)==true)
		{
		  FrameLayout lay = (FrameLayout) findViewById(R.id.introo);
		  Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade);
		  lay.startAnimation(myFadeInAnimation);
		  new Handler().postDelayed(new Runnable(){
	            public void run() {
	                /* Create an Intent that will start the Menu-Activity. */
	                Intent mainIntent = new Intent(Intro.this,MainActivity.class);
	                Intro.this.startActivity(mainIntent);
	                Intro.this.finish();
	            }
	        }, SPLASH_DISPLAY_LENGHT);
		} else {
			Intent mainIntent = new Intent(Intro.this,MainActivity.class);
            Intro.this.startActivity(mainIntent);
            Intro.this.finish();
		}
	}

	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */

	Handler mHideHandler = new Handler();

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
}
