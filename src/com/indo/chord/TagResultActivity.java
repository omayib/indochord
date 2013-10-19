package com.indo.chord;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TagResultActivity extends Activity {

	// . class
	// # id
	ProgressBar pb;
	Document document;
	Elements div;
	Elements links;
	TextView t;
	ListView list;
	WebView wview;
	String[] artist = { "" };
	String[] linkartist = { "" };
	String[] arrartist = { "" };
	String sBand, sartist, sTag;
	String BLOG_URL;
	SharedPreferences sp;
	SharedPreferences.Editor spe;
	ImageButton bRefresh;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// set layout view
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
		// process
		pb = (ProgressBar) findViewById(R.id.pb);
		pb.setMax(100);
		sp = this.getSharedPreferences("dataLagu", 0);
		spe = sp.edit();
		sBand = sp.getString("band", "");
		sartist = sp.getString("judul", "");
		sTag = sp.getString("tag", "");
		Log.d("b", sBand);
		Log.d("t", sartist);
		list = (ListView) findViewById(R.id.listSong);
		BLOG_URL = "http://www.chordfrenzy.com/tag/" + sTag;
		ImageButton bHome = (ImageButton) findViewById(R.id.bHome);
		bHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(TagResultActivity.this,
						MainActivity.class));
			}
		});
		bRefresh = (ImageButton) findViewById(R.id.bRefresh);
		bRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isOnline()) {
					try {
						(new GetListTaks()).execute(BLOG_URL, "", "");
					} catch (Exception ex) {
						// ((TextView) findViewById(R.id.tv)).setText("Error");
						Log.d("eror", ex.toString());
					}
				} else {

					Toast.makeText(TagResultActivity.this, "no connection",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		if (isOnline()) {
			try {
				(new GetListTaks()).execute(BLOG_URL, "", "");
			} catch (Exception ex) {
				// ((TextView) findViewById(R.id.tv)).setText("Error");
				Log.d("eror", ex.toString());
			}
		} else {

			Toast.makeText(TagResultActivity.this, "no connection",
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

	static class ViewHolder {
		TextView text;
	}

	private class SpecialAdapter extends BaseAdapter {
		// Defining the background color of rows. The row will alternate between
		// green light and green dark.
	//	private int[] colors = new int[] { color.transparan, 0xA3ffffff };
		private LayoutInflater mInflater;

		// The variable that will hold our text data to be tied to list.
		private String[] data;

		public SpecialAdapter(Context context, String[] items) {
			mInflater = LayoutInflater.from(context);
			this.data = items;
		}

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// A view to hold each row in the list
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.rowlayout, null);

				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.headline);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// Bind the data efficiently with the holder.
			holder.text.setText(data[position]);

			// Set the background color depending of odd/even colorPos result
			// int colorPos = position % colors.length;
			// convertView.setBackgroundColor(colors[colorPos]);

			return convertView;
		}
	}

	private class GetListTaks extends AsyncTask<Object, Object, Object> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			bRefresh.setEnabled(false);
			if (pb.getVisibility() == View.INVISIBLE) {
				pb.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected void onPostExecute(Object objResult) {
			// TODO Auto-generated method stub
			if (objResult != null && objResult instanceof Elements) {
				if (pb.getVisibility() == View.VISIBLE) {
					pb.setVisibility(View.INVISIBLE);
				}

				Log.d("res", "b");

				Elements d = (Elements) objResult;
				links = d.select("a[href]");
				Elements subt = d.select("div.subtitle");
				artist = new String[links.size()];
				linkartist = new String[links.size()];
				Log.d(" subt ", String.valueOf(subt.size()));
				int f = 0;
				int g = 0;
				if (links.size() > 0) {
					for (Element link : links) {
						artist[f++] = link.text();
						linkartist[g++] = link.attr("abs:href");
						Log.d("link", link.attr("abs:href"));

						Log.d("judul teks ", link.text());
					}
				} else {
					artist[0] = "not found";
				}
				// SpecialAdapter adapter = new SpecialAdapter(this, artist);
				Log.d("link teks ", String.valueOf(artist.length));

				// arrartist = (String[]) objResult;
				SpecialAdapter a = new SpecialAdapter(getApplicationContext(),
						artist);
				list.setAdapter(a);
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Log.d("klik", String.valueOf(arg2));
						// TODO Auto-generated method stub
						Log.d("artist", artist[arg2]);
						String a = artist[arg2];
						spe.putString("artist", a);
						spe.commit();
						Intent myintent = new Intent(TagResultActivity.this,
								TagArtistActivity.class);
						startActivity(myintent);
					}
				});
				bRefresh.setEnabled(true);
			} else {
				Log.d("resc", "c");
				if (pb.getVisibility() == View.VISIBLE) {
					pb.setVisibility(View.INVISIBLE);
				}
				Toast.makeText(
						getApplicationContext(),
						"Content not Loaded. \nSorry for error connection. \nRefresh please..",
						Toast.LENGTH_LONG).show();

				bRefresh.setEnabled(true);
			}

		}

		@Override
		protected Object doInBackground(Object... params) {
			String url = params[0].toString();
			Log.d("url ===>>", url);
			// TODO Auto-generated method stub
			try {
				document = Jsoup.connect(url).timeout(10000).get();
				div = document.select("div.boxmain");
				Log.d("data", div.toString());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// ArrayAdapter<String> a = new ArrayAdapter<String>(this,
			// R.layout.rowlayout, artist);

			return div;
		}

	}

}
