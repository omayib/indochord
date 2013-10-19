package com.indo.chord;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ChordActivity extends Activity {

	// . class
	// # id
	ProgressBar pb;
	Document document;
	Element div;
	TextView t, s;
	ListView list;
	WebView wview;
	String[] title = { "" };
	String[] linkTitle = { "" };

	String BLOG_URL;
	SharedPreferences sp;
	SharedPreferences.Editor spe;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// set layout view
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chord);
		t = (TextView) findViewById(R.id.title);
		s = (TextView) findViewById(R.id.singer);

		sp = this.getSharedPreferences("dataLagu", 0);
		spe = sp.edit();

		BLOG_URL = sp.getString("addr", "");
		String j = sp.getString("jdl", "");
		String si = sp.getString("singer", "");

		t.setText(j);
		s.setText(si);

		// process
		pb = (ProgressBar) findViewById(R.id.pb);
		pb.setMax(100);

		ImageButton bRefresh = (ImageButton) findViewById(R.id.bRefresh);
		bRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isOnline()) {
					try {
						(new GetChordTaks()).execute((Object) null);
					} catch (Exception ex) {
						Log.d("eror", ex.toString());
					}
				} else {
					Toast.makeText(ChordActivity.this, "no connection",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		ImageButton bHome = (ImageButton) findViewById(R.id.bHome);
		bHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent a = new Intent(ChordActivity.this,
				// MainActivity.class);
				//
				// startActivity(a);

				spe.clear();
				finish();
			}
		});

		wview = (WebView) findViewById(R.id.webView1);
		wview.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.paternbgpaper));
		wview.getSettings().setJavaScriptEnabled(true);
		if (isOnline()) {
			try {
				(new GetChordTaks()).execute((Object) null);
			} catch (Exception ex) {
				Log.d("eror", ex.toString());
			}
		} else {
			Toast.makeText(ChordActivity.this, "no connection",
					Toast.LENGTH_SHORT).show();
		}

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.finish();
	}

	@SuppressWarnings("rawtypes")
	private class GetChordTaks extends AsyncTask {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (pb.getVisibility() == View.INVISIBLE) {
				pb.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected void onPostExecute(Object objResult) {
			// TODO Auto-generated method stub
			if (objResult != null && objResult instanceof String) {
				if (pb.getVisibility() == View.VISIBLE) {
					pb.setVisibility(View.INVISIBLE);
				}
				Log.d("res betul", "c");
				String lirik = (String) objResult;
				wview.loadData(lirik, "text/html", null);

			} else {
				Log.d("resc", "c");
			}

		}

		@Override
		protected Object doInBackground(Object... params) {
			String result = "";
			// TODO Auto-generated method stub
			try {
				document = Jsoup.connect(BLOG_URL).timeout(10000).get();
				div = document.select("div.chord").first();
				// Log.d("data", div.toString());
				result = div.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(ChordActivity.this,
						"Sorry for error connection. Refresh please..",
						Toast.LENGTH_SHORT).show();
			}

			// ArrayAdapter<String> a = new ArrayAdapter<String>(this,
			// R.layout.rowlayout, title);

			return result;
		}

	}

}
