package com.udsigry.zs1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import android.R.string;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class PokazPlan extends Activity {
	
	public int megaday = 0;
	public String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokaz_plan);
        Intent intent = getIntent();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK); 
        megaday = day - 1;
        if (megaday == 0 || megaday == 6) {
        	megaday = 1;
        }
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        loadFile();
    }
    
    protected void onDestroy() {        
	    super.onDestroy();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pokaz_plan, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void wroc(View view)
    {
    	

    	startActivity(new Intent(PokazPlan.this, MainActivity.class));
    	finish();

    }
    
    public void loadFile() {
    	File file = new File(Environment.getExternalStorageDirectory() + "/plany/" + message + "-" + megaday + ".txt");
    	String line = "";
        try {
	            BufferedReader br = new BufferedReader(new FileReader(file));
	            String st = null;
	            while((st = br.readLine()) != null) {
	            	line = line + st + "\n";
	            }
            }
        catch (IOException e) {
        }
        TextView dzien = (TextView) findViewById(R.id.dzien);
        dzien.setText(dzientyg());
        TextView plan = (TextView) findViewById(R.id.planview);
        plan.setText(line);
    }
    
    public void popdzien(View view) {
    	if (megaday -1 == 0) {
    		megaday = 5;
    	} else {
    		megaday--;
    	}
    	loadFile();
    }
    
    public void nastdzien(View view) {
    	if (megaday + 1 == 6) {
    		megaday = 1;
    	} else {
    		megaday++;
    	}
    	loadFile();
    }
    
    public String dzientyg() {
    	String dzien = null;
    	if (megaday == 1) {
    		dzien = "Poniedziałek";
    	} else if (megaday == 2) {
    		dzien = "Wtorek";
    	} else if (megaday == 3) {
    		dzien = "Środa";
    	} else if (megaday == 4) {
    		dzien = "Czwartek";
    	} else if (megaday == 5) {
    		dzien = "Piątek";
    	}
    	return(dzien);
    }


}
