package com.udsigry.zs1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import android.preference.PreferenceManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	 public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	 private ProgressDialog mProgressDialog;
	 public final static String EXTRA_MESSAGE = "com.example.tre.MESSAGE";
	 public SharedPreferences sharedPrefs;
	 public String line;
	 public NotificationManager nm;
	 
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            
        
        
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPrefs.getBoolean("uczen", false)==true)
    	{
    		String klasa =  sharedPrefs.getString("klasa", "-1");
    		if (klasa=="-1")
    		{
    			AlertDialog alertDialog;
    			alertDialog = new AlertDialog.Builder(this).create();
    			alertDialog.setTitle("Brak Ustawien");
    			alertDialog.setMessage("Nie wprowadzono ustawieÅ„ zostajesz przeniesiony do okna ustawien.");
    			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                ust();
    		           }
    		       });
    			
    			alertDialog.show();
    			
    		}
    	}  else {
        	if (isNetworkConnected() == true){
        		UpDate();
        	}
        }
        if(sharedPrefs.getBoolean("uczen", false)==false)
        {
        	String nauczyciel =  sharedPrefs.getString("nauczyciel", "-1");
        	if (nauczyciel=="-1")
    		{
    			AlertDialog alertDialog;
    			alertDialog = new AlertDialog.Builder(this).create();
    			alertDialog.setTitle("Brak Ustawien");
    			alertDialog.setMessage("Nie wprowadzono ustawieÅ„ zostajesz przeniesiony do okna ustawien.");
    			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
 		           public void onClick(DialogInterface dialog, int id) {
 		                ust();
 		           }
 		       });
    			alertDialog.show();
    		}
        } else {
        	if (isNetworkConnected() == true){
        		UpDate();
        	}
        }
        
        if (isNetworkConnected() == true){

        	WebView zmian = (WebView) findViewById(R.id.zmiana);
        	//zmian.setBackgroundColor(0);
        	zmian.loadUrl("http://android.zs-1.edu.pl/zmiany.php");
        	new Download().execute("http://android.zs-1.edu.pl/zmiany.html", "zmiany.html");
        	
        	
        } else {
        	TextView brak = (TextView) findViewById(R.id.brak);
        	brak.setText("Brak po³¹czenia z internetem");
        	WebView zmian = (WebView) findViewById(R.id.zmiana);
        	//zmian.setBackgroundColor(0);
        	String mmm = Environment.getExternalStorageDirectory () + "/plany/zmiany.html";
        	mmm = "file://" + mmm;
        	zmian.loadUrl(mmm);
        	
        }
        
    }
   
	private boolean isNetworkConnected() {
  	  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
  	  NetworkInfo ni = cm.getActiveNetworkInfo();
  	  if (ni == null) {
  	   // There are no active networks.
  	   return false;
  	  } else
  	   return true;
  	 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.item1:
        	if (isNetworkConnected() == true){
        	startDownload();
        	} else {}
            return true;
        case R.id.info:
        	Intent intent = new Intent(this, Informacje.class);
        	startActivity(intent);
        	
            return true;
        case R.id.sett:
        	Intent sett = new Intent(this, Ustawienia.class);
        	startActivity(sett);
        	
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void ust() {
    	Intent sett = new Intent(this, Ustawienia.class);
    	startActivity(sett);
    }
    private void startDownload() {
    	if(sharedPrefs.getBoolean("uczen", false)==true)
    	{
    		String klasa =  sharedPrefs.getString("klasa", "-1");
    		String url = "http://zs-1.pl/plany/plany/" + klasa + ".html";
    		new DownloadFileAsync().execute(url);
    	} else {
    		String nauczyciel =  sharedPrefs.getString("nauczyciel", "-1");
    		String url = "http://zs-1.pl/plany/plany/" + nauczyciel + ".html";
    		new DownloadFileAsync().execute(url);
    	}
        
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Pobieranie pliku..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }
    class DownloadFileAsync extends AsyncTask<String, String, String> {
       
        @SuppressWarnings("deprecation")
		@Override
        protected void onPreExecute() {
       
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                
                File folder = new File(Environment.getExternalStorageDirectory () + "/plany"); 
                if(!folder.exists())
                {
                 folder.mkdir();
                } else {
                
                }
                OutputStream output;
    	if(sharedPrefs.getBoolean("uczen", false)==true)
    	{
    		String klasa =  sharedPrefs.getString("klasa", "-1");
    		output = new FileOutputStream(Environment.getExternalStorageDirectory () + "/plany/" + klasa + ".html");
    	} else {
    		String nauczyciel =  sharedPrefs.getString("nauczyciel", "-1");
    		output = new FileOutputStream(Environment.getExternalStorageDirectory () + "/plany/" + nauczyciel + ".html");
    	} 

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
               
                
            } catch (Exception e) {}
            return null;

        }
        protected void onProgressUpdate(String... progress) {
             Log.d("ANDRO_ASYNC",progress[0]);
             mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }
        
       
        
        @SuppressWarnings("deprecation")
		@Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            
        }
    }

    public void planlekcji(View view)
    {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	
    	if(sharedPrefs.getBoolean("uczen", false)==true)
    	{
    		String klasa =  sharedPrefs.getString("klasa", "-1");
    		Intent intent = new Intent(this, PokazPlan.class);
        	String message = Environment.getExternalStorageDirectory () + "/plany/";
        	message = message + klasa;
        	message = message + ".html";
        	File file = new File(message);
        	if (file.exists())
        	{
        		message = "file://" + message;
        		intent.putExtra(EXTRA_MESSAGE, message);
        		startActivity(intent);
        		finish();
        	} else
        	{
        		if (isNetworkConnected() == true){
                	startDownload();
                	} else {}
        	}
    	} else {
    		String nauczyciel =  sharedPrefs.getString("nauczyciel", "-1");
    		Intent intent = new Intent(this, PokazPlan.class);
    		String message = Environment.getExternalStorageDirectory () + "/plany/";
        	message = message + nauczyciel;
        	message = message + ".html";
        	File file = new File(message);
        	if (file.exists())
        	{
        		message = "file://" + message;
        		intent.putExtra(EXTRA_MESSAGE, message);
        		startActivity(intent);
        		finish();
        	} else
        	{
        		if (isNetworkConnected() == true){
                	startDownload();
                	} else {}
        	}
    	}
        	}
    
	@SuppressWarnings("deprecation")
	public void UpDate()
    {
		
    	final String ver = this.getString(R.string.akk);
    	try
    	{
    			if ( new Download().execute("http://android.zs-1.edu.pl/wersjapk.txt", "wersjapk.txt") != null) {
    				File fff = new File(Environment.getExternalStorageDirectory().getPath()  + "/plany/wersjapk.txt");
    				InputStream i = fff.toURL().openStream();
    				Scanner scan = new Scanner(i);
        			final String wersja = scan.nextLine();
        			i.close();
    				if (!wersja.equalsIgnoreCase(ver))
        			{
        				AlertDialog alertDialog;
            			alertDialog = new AlertDialog.Builder(this).create();
            			alertDialog.setTitle("Nowa wersja! ");
            			alertDialog.setMessage("Dostêpna jest nowa wersja aplikacji po naciœniêciu zostaniesz automatycznie przeniesiony do instalatora.");
            			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
         		           public void onClick(DialogInterface dialog, int id) {
         		        	   new DownloadApk().execute("http://android.zs-1.edu.pl/appki/" + wersja , wersja);
         		           }
         		       });
            			alertDialog.show();
        			}
    			}
    			
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }
	 
    class Download extends AsyncTask<String, String, String>
    {
    	
    	
    	@SuppressWarnings("deprecation")
		@Override
        protected void onPreExecute() {
       
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    	
    	protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
       }
       
      
       
       @SuppressWarnings("deprecation")
       @Override
       protected void onPostExecute(String unused) {
           dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
           
       }
    	
		@Override
		protected String doInBackground(String... params) {
			try {
	    		URL url = new URL(params[0]);
	             HttpURLConnection c = (HttpURLConnection) url.openConnection();
	             c.setRequestMethod("GET");
	             c.setDoOutput(true);
	             c.connect();

	             String PATH = Environment.getExternalStorageDirectory().getPath()  + "/plany/";
	             File file = new File(PATH);
	             file.mkdirs();
	             File outputFile = new File(file, params[1]);
	             FileOutputStream fos = new FileOutputStream(outputFile);

	             InputStream is = new BufferedInputStream(c.getInputStream());


	             byte[] buffer = new byte[1024];
	             int len1 = 0;
	             while ((len1 = is.read(buffer)) != -1) {
	                 fos.write(buffer, 0, len1);
	             }
	             fos.close();
	             is.close();//till here, it works fine - .apk is download to my sdcard in download file
	             


	         } catch (IOException e) {
	        	 e.printStackTrace();
	        	 
	         }
			return null;
		}
    }
		
    
    
    
    
    class DownloadApk extends AsyncTask<String, String, String>
    {
    	
    	@SuppressWarnings("deprecation")
		@Override
        protected void onPreExecute() {
       
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    	
    	protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
       }
       
      
       
       @SuppressWarnings("deprecation")
       @Override
       protected void onPostExecute(String unused) {
           dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
           
       }
    	
		@Override
		protected String doInBackground(String... params) {
			try {
	    		URL url = new URL(params[0]);
	             HttpURLConnection c = (HttpURLConnection) url.openConnection();
	             c.setRequestMethod("GET");
	             c.setDoOutput(true);
	             c.connect();

	             String PATH = Environment.getExternalStorageDirectory().getPath()  + "/plany/";
	             File file = new File(PATH);
	             file.mkdirs();
	             File outputFile = new File(file, params[1]);
	             FileOutputStream fos = new FileOutputStream(outputFile);

	             InputStream is = new BufferedInputStream(c.getInputStream());


	             byte[] buffer = new byte[1024];
	             int len1 = 0;
	             while ((len1 = is.read(buffer)) != -1) {
	                 fos.write(buffer, 0, len1);
	             }
	             fos.close();
	             is.close();//till here, it works fine - .apk is download to my sdcard in download file
	             
	             
	             Intent intent = new Intent(Intent.ACTION_VIEW);
	             intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/plany/" + params[1])), "application/vnd.android.package-archive");
	             startActivity(intent);
	             


	         } catch (IOException e) {
	        	 e.printStackTrace();
	        	 
	         }
			return null;
		}
    }
    
    
    
    
    
    class DownloadFiletxt extends AsyncTask<String, String, String> {
        
        @SuppressWarnings("deprecation")
		@Override
        protected void onPreExecute() {
       
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                
                File folder = new File(Environment.getExternalStorageDirectory () + "/plany"); 
                if(!folder.exists())
                {
                 folder.mkdir();
                } else {
                
                }
                OutputStream output;
    		output = new FileOutputStream(Environment.getExternalStorageDirectory () + "/plany/zmiany.txt");
    	

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
               
                
            } catch (Exception e) {}
            return null;

        }
        protected void onProgressUpdate(String... progress) {
             Log.d("ANDRO_ASYNC",progress[0]);
             mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }
        
       
        
        @SuppressWarnings("deprecation")
		@Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            
        }
    }
    	
    
}
