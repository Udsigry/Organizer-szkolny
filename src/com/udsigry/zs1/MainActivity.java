package com.udsigry.zs1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
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
	public final static String EXTRA_MESSAGE = "";
	public SharedPreferences sharedPrefs;
	public String line;
	public NotificationManager nm;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {					//gdy otwiera sie formularz
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);							//ustawienie layout -u

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);	//sprawdzenie ustawien
		if (sharedPrefs.getBoolean("uczen", false) == true) {				//test trybu
			String klasa = sharedPrefs.getString("klasa", "-1");		//gdy tryb ucznia on
			if (klasa == "-1") {										// czy klasa wybrana
				AlertDialog alertDialog;								//dialog
				alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle("Brak Ustawień");
				alertDialog
						.setMessage("Nie wprowadzono ustawień zostajesz przeniesiony do okna ustawień.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ust();													//wywolanie intentu ustawien
							}
						});

				alertDialog.show();

			}
		} else {
			if (isNetworkConnected() == true) {						//nie wiem po co to ale chyba takie tam pomylki
				UpDate();
			}
		}
		if (sharedPrefs.getBoolean("uczen", false) == false) {					//to samo co @up ale dla nauczycieli
			String nauczyciel = sharedPrefs.getString("nauczyciel", "-1");
			if (nauczyciel == "-1") {
				AlertDialog alertDialog;
				alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle("Brak Ustawień");
				alertDialog
						.setMessage("Nie wprowadzono ustawień zostajesz przeniesiony do okna ustawień.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ust();
							}
						});
				alertDialog.show();
			}
		} else {
			if (isNetworkConnected() == true) {      //to samo
				UpDate();
			}
		}

		if (isNetworkConnected() == true) {

			WebView zmian = (WebView) findViewById(R.id.zmiana);			//wczytywanie zmian gdy jest polaczenie z internetem i ich pobraniem do offline
			// zmian.setBackgroundColor(0);
			zmian.loadUrl("http://android.zs-1.edu.pl/zmiany.php");
			new Download().execute("http://android.zs-1.edu.pl/zmiany.html",
					"zmiany.html");

		} else {															//wczytanie zmian gdy brak polaczenie z siecia internet
			TextView brak = (TextView) findViewById(R.id.brak);
			brak.setText("Brak połączenia z internetem");
			WebView zmian = (WebView) findViewById(R.id.zmiana);
			// zmian.setBackgroundColor(0);
			String mmm = Environment.getExternalStorageDirectory()
					+ "/plany/zmiany.html";
			mmm = "file://" + mmm;
			zmian.loadUrl(mmm);

		}

	}

	private boolean isNetworkConnected() {						//test pol�czenie z internetem
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// gdy brak.
			return false;
		} else
			return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {						//inicjalizacja menu kontekstowego
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {   			//gdy wybierzemy jakas opcje menu kontekstowego
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.item1:												//aktualizator planu
			if (isNetworkConnected() == true) {
				startDownload();
			} else {
			}
			return true;
		case R.id.info:													//informacje
			Intent intent = new Intent(this, Informacje.class);
			startActivity(intent);

			return true;
		case R.id.sett:													//ustawienia
			Intent sett = new Intent(this, Ustawienia.class);
			startActivity(sett);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void ust() {											//procedura otwierania ustawien
		Intent sett = new Intent(this, Ustawienia.class);
		startActivity(sett);
	}

	private void startDownload() {									//do aktualizatora procedura wymuszonej aktualizacji planu
		if (sharedPrefs.getBoolean("uczen", false) == true) {
			String klasa = sharedPrefs.getString("klasa", "-1");
			for (int i = 1; i<6;i++){
				String url = "http://zs-1.pl/android/pliki/" + klasa + "-" + i + ".txt";
				new Download().execute(url,klasa + "-" + i + ".txt");
			}
		} else {
			String nauczyciel = sharedPrefs.getString("nauczyciel", "-1");
			for (int i = 1; i<6;i++){
				String url = "http://zs-1.pl/android/pliki/" + nauczyciel + "-" + i + ".txt";
				new Download().execute(url, nauczyciel + "-" + i + ".txt");
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {					//dialog pobierania aktualizacji
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

	/*class DownloadFileAsync extends AsyncTask<String, String, String> {					//pobieranie asyn tylko dla planu lekcji

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

				File folder = new File(
						Environment.getExternalStorageDirectory() + "/plany");
				if (!folder.exists()) {
					folder.mkdir();
				} else {

				}
				OutputStream output;
				if (sharedPrefs.getBoolean("uczen", false) == true) {
					String klasa = sharedPrefs.getString("klasa", "-1");
					output = new FileOutputStream(
							Environment.getExternalStorageDirectory()
									+ "/plany/" + klasa + ".html");
				} else {
					String nauczyciel = sharedPrefs.getString("nauczyciel",
							"-1");
					output = new FileOutputStream(
							Environment.getExternalStorageDirectory()
									+ "/plany/" + nauczyciel + ".html");
				}

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();

			} catch (Exception e) {
			}
			return null;

		}

		protected void onProgressUpdate(String... progress) {
			Log.d("ANDRO_ASYNC", progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String unused) {
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);

		}
	}*/

	public void planlekcji(View view) {										//otwieranie okna z planem
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sharedPrefs.getBoolean("uczen", false) == true) {
			String klasa = sharedPrefs.getString("klasa", "-1");
			Intent intent = new Intent(this, PokazPlan.class);
			String message = Environment.getExternalStorageDirectory()
					+ "/plany/";
			message = message + klasa;
			message = message + "-1.txt";
			File file = new File(message);
			if (file.exists()) {
				message = klasa;
				intent.putExtra(EXTRA_MESSAGE, message);
				startActivity(intent);
				finish();
			} else {
				if (isNetworkConnected() == true) {
					startDownload();
				} else {
				}
			}
		} else {
			String nauczyciel = sharedPrefs.getString("nauczyciel", "-1");		//wyczytywanie inforamcji o planie
			Intent intent = new Intent(this, PokazPlan.class);
			String message = Environment.getExternalStorageDirectory()
					+ "/plany/";
			message = message + nauczyciel;
			message = message + "-1.txt";
			File file = new File(message);
			if (file.exists()) {							//T: otwieranie okna z planem F: pobranie planu
				message = nauczyciel;
				intent.putExtra(EXTRA_MESSAGE, message);
				startActivity(intent);
				finish();
			} else {
				if (isNetworkConnected() == true) {
					startDownload();
				} else {
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void UpDate() {				//funkcja aktualizatora oprogramowania "Organizer szkolny"

		final String ver = this.getString(R.string.akk);
		try {
			if (new Download().execute(
					"http://android.zs-1.edu.pl/wersjapk.txt", "wersjapk.txt") != null) {
				File fff = new File(Environment.getExternalStorageDirectory()
						.getPath() + "/plany/wersjapk.txt");
				InputStream i = fff.toURL().openStream();
				Scanner scan = new Scanner(i);
				final String wersja = scan.nextLine();
				i.close();
				if (!wersja.equalsIgnoreCase(ver)) {
					AlertDialog alertDialog;
					alertDialog = new AlertDialog.Builder(this).create();
					alertDialog.setTitle("Nowa wersja! ");
					alertDialog
							.setMessage("Dost�pna jest nowa wersja aplikacji po naci�ni�ciu zostaniesz automatycznie przeniesiony do instalatora.");
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									new DownloadApk().execute(
											"http://android.zs-1.edu.pl/appki/"
													+ wersja, wersja);
								}
							});
					alertDialog.show();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class Download extends AsyncTask<String, String, String> {		//pobiereanie async dla pozostalych plikow

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		protected void onProgressUpdate(String... progress) {
			Log.d("ANDRO_ASYNC", progress[0]);
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

				String PATH = Environment.getExternalStorageDirectory()
						.getPath() + "/plany/";
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
				is.close();

			} catch (IOException e) {
				e.printStackTrace();

			}
			return null;
		}
	}

	class DownloadApk extends AsyncTask<String, String, String> {				//pobieranie async dla plikow apk (dlatego osobno bo to jest pobieranie async i sie otwieral nie sciagniety plik

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		protected void onProgressUpdate(String... progress) {
			Log.d("ANDRO_ASYNC", progress[0]);
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

				String PATH = Environment.getExternalStorageDirectory()
						.getPath() + "/plany/";
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
				is.close();

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(Environment
								.getExternalStorageDirectory()
								+ "/plany/"
								+ params[1])),
						"application/vnd.android.package-archive");
				startActivity(intent);

			} catch (IOException e) {
				e.printStackTrace();

			}
			return null;
		}
	}

}
