package com.indo.chord;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.indo.chord.helper.ChordBaseAdapter;
import com.indo.chord.helper.SearchResults;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ArrayList<SearchResults> searchResults = new ArrayList<SearchResults>();
	private SearchResults asr1;
	Dialog d;
	LinearLayout abj;
	ProgressBar pb;
	ImageButton bRefresh, ibSearch, ibCari;
	EditText inBand, inTitle, inCari;
	Button bSearch;
	Document document, doc;
	Element div;
	Elements links;
	TextView t;
	ListView list;
	WebView wview;
	String[] title = { "" };
	String[] singer = { "" };
	String[] linkTitle = { "" };
	String cari;
	SharedPreferences sp;
	SharedPreferences.Editor spe;
	static final String BLOG_URL = "http://www.chordfrenzy.com/";

	// ==============akhir deklarasi
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// set layout view
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// AndroidSDKProvider.setTestMode(true);
		if (savedInstanceState != null) {
			Toast.makeText(this, savedInstanceState.getString("message"),
					Toast.LENGTH_LONG).show();
		}
		// membuat deretean huruf A-Z
		// shared preference untuk menyimpan data sementara
		sp = this.getSharedPreferences("dataLagu", 0);
		spe = sp.edit();
		spe.clear();

		pb = (ProgressBar) findViewById(R.id.pb);
		pb.setMax(100);
		inCari = (EditText) findViewById(R.id.inCari);

		ibCari = (ImageButton) findViewById(R.id.ibCari);
		ibCari.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cari = inCari.getText().toString();
				Log.i("cari", cari);
				spe.putString("judul", cari);
				spe.putString("band", "");

				spe.commit();

				// method untuk memulai activity baru
				startActivity(new Intent(MainActivity.this,
						HasilCariActivity.class));
			}
		});

		ibSearch = (ImageButton) findViewById(R.id.ibSearch);
		ibSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialogSearch();
			}
		});
		bRefresh = (ImageButton) findViewById(R.id.bRefresh);
		bRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					(new GetListRecentChord()).execute((Object) null);
				} catch (Exception ex) {
					Log.d("eror", ex.toString());
				}
				Log.d("trs", "testestes");
			}
		});

		list = (ListView) findViewById(R.id.listSong);
		if (isOnline()) {
			try {
				(new GetListRecentChord()).execute((Object) null);
			} catch (Exception ex) {
				Log.d("eror", ex.toString());
			}
		} else {
			Toast.makeText(MainActivity.this, "no connection",
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
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("message", "This is my message to be reloaded");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	protected void dialogSearch() {
		// TODO Auto-generated method stub
		d = new Dialog(this);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.customdialog);
		ImageButton bExit = (ImageButton) d.findViewById(R.id.bExit);
		bExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				d.cancel();
			}
		});
		inBand = (EditText) d.findViewById(R.id.inBand);
		inTitle = (EditText) d.findViewById(R.id.inTitle);
		bSearch = (Button) d.findViewById(R.id.button1);
		abj = (LinearLayout) d.findViewById(R.id.abjad);
		bSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String band = inBand.getText().toString();
				String judul = inTitle.getText().toString();

				spe.putString("band", band);
				spe.putString("judul", judul);

				spe.commit();

				// method untuk memulai activity baru
				startActivity(new Intent(MainActivity.this,
						HasilCariActivity.class));
				d.cancel();
			}
		});
		setupAbjad();
		d.show();
	}

	/*
	 * fungsi untuk men generate huruf A-Z dengan melakukan loooping. pada tabel
	 * ASCII, karakter A-Z nilai desimalnya 65-91
	 */
	private void setupAbjad() {
		// TODO Auto-generated method stub
		for (int i = 65; i < 91; i++) {
			Log.d("char", String.valueOf((char) i));
			String c = String.valueOf((char) i);
			TextView tc = new TextView(this);
			tc.setId((char) i);
			tc.setText(" | " + c + " | ");
			abj.addView(tc);
			tc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					v.setBackgroundColor(Color.CYAN);
					iii(v.getId());
					d.cancel();
				}
			});
		}

	}

	// Method ViewHolder dan SpecialAdapter dipakai untuk membuat custom
	// listview
	static class ViewHolder {
		TextView text, singer;
	}

	// Method untuk konek ke URL secara background menggunakan kelas AsyncTaks
	@SuppressWarnings("rawtypes")
	private class GetListRecentChord extends AsyncTask {
		// sebelum membuka koneksi, disiapkan dulu progres bar untuk proses
		// loading
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			bRefresh.setEnabled(false);
			if (pb.getVisibility() == View.INVISIBLE) {
				pb.setVisibility(View.VISIBLE);
			}
		}

		// Proses membuka koneksi ke URL secara background
		@Override
		protected Element doInBackground(Object... params) {
			// TODO Auto-generated method stub
			div = null;
			try {
				document = Jsoup.connect(BLOG_URL).timeout(10000).get();

				div = document.select("div.box").first();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return div; // deretan list lagu dikirim keluar dari method ini
		}

		@Override
		protected void onPostExecute(Object objResult) {
			// TODO Auto-generated method stub
			// menangkap hasil keluaran method doInBackground()
			// jika objResult sudah berisi data, maka progres bar berhenti
			// berputar. Asumsinya proses mendownload list telah selesai
			// dilakukan.
			try {
				if (objResult != null && objResult instanceof Element) {
					if (pb.getVisibility() == View.VISIBLE) {
						pb.setVisibility(View.INVISIBLE);
					}
					searchResults.clear();
					Element d = (Element) objResult;

					// Elements listitem = d.getElementsByClass("listitem");
					// String url = listitem.first().attr("a[href]");

					Elements subtitles = d.getElementsByClass("subtitle");
					Elements titles = d.getElementsByClass("title");

					// List<String> links = new ArrayList<String>();
					int j = 0, k = 0, m = 0;
					title = new String[titles.size()];
					linkTitle = new String[titles.size()];
					for (Element element : d.select("a[href]")) {
						linkTitle[k++] = element.attr("href");
					}
					singer = new String[subtitles.size()];
					for (Element e : subtitles) {
						String s = e.text();
						singer[m++] = s;
						Log.d("subtitles", s);
					}
					for (Element e : titles) {
						String t = e.text();
						title[j++] = t;
					}

					for (int h = 0; h < title.length; h++) {
						asr1 = new SearchResults();
						asr1.setJudul(title[h]);
						asr1.setSinger(singer[h]);
						searchResults.add(asr1);
					}
					ChordBaseAdapter a = new ChordBaseAdapter(
							getApplicationContext(), searchResults);
					list.setAdapter(a);
					// method unutk mengaktifkan listview supaya bisa diklik
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {

							spe.putString("addr", linkTitle[arg2]);
							spe.putString("jdl", searchResults.get(arg2)
									.getJudul());
							spe.putString("singer", searchResults.get(arg2)
									.getSinger());
							spe.commit();

							Intent myintent = new Intent(MainActivity.this,
									ChordActivity.class);
							myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(myintent);

						}
					});
				} else {
					if (pb.getVisibility() == View.VISIBLE) {
						pb.setVisibility(View.INVISIBLE);
					}
					Log.d("resc", "c");
					Toast.makeText(
							getApplicationContext(),
							"Content not Loaded. \nSorry for error connection. \nRefresh please..",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("resc", e.toString());
			}

			bRefresh.setEnabled(true);
		}
	}

	//
	protected void iii(int id) {
		// TODO Auto-generated method stub
		Log.d("b", String.valueOf(id));
		switch (id) {
		case 65:
			spe.putString("tag", "a");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));

			break;

		case 66:
			spe.putString("tag", "b");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 67:
			spe.putString("tag", "c");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 68:
			spe.putString("tag", "d");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 69:
			spe.putString("tag", "e");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 70:
			spe.putString("tag", "f");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 71:
			spe.putString("tag", "g");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 72:
			spe.putString("tag", "h");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 73:
			spe.putString("tag", "i");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 74:
			spe.putString("tag", "j");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 75:
			spe.putString("tag", "k");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 76:
			spe.putString("tag", "l");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 77:
			spe.putString("tag", "m");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 78:
			spe.putString("tag", "n");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 79:
			spe.putString("tag", "o");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 80:
			spe.putString("tag", "p");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 81:
			spe.putString("tag", "q");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 82:
			spe.putString("tag", "r");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 83:
			spe.putString("tag", "s");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 84:
			spe.putString("tag", "t");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 85:
			spe.putString("tag", "u");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 86:
			spe.putString("tag", "v");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 87:
			spe.putString("tag", "w");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;

		case 88:
			spe.putString("tag", "x");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 89:
			spe.putString("tag", "y");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		case 90:
			spe.putString("tag", "z");
			spe.commit();
			startActivity(new Intent(this, TagResultActivity.class));
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		super.onBackPressed();
	}

}
