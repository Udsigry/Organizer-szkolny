package com.udsigry.zs1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.support.v4.app.NavUtils;

public class PokazPlan extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokaz_plan);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        WebView zmian = (WebView) findViewById(R.id.plan);
    	zmian.loadUrl(message);
    	zmian.getSettings().setBuiltInZoomControls(true);
    	zmian.getSettings().setDefaultZoom(ZoomDensity.FAR);
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

}
