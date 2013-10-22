package com.udsigry.zs1;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.view.Menu;

public class Ustawienia extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_ustawienia, menu);
		return true;
	}

}
