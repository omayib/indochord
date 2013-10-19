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

public class HasilCariActivity extends Activity {

	// . class
	// # id

	ProgressBar pb;
	Document document;
	Elements div;
	Elements links;
	TextView t;
	ListView list;
	WebView wview;
	String[] title = { "" };
	String[] linkTitle = { "" };
	String[] singer = { "" };
	String sBand, sTitle;
	String BLOG_URL;
	SharedPreferences sp;
	SharedPreferences.Editor spe;
	ImageButton brefresh;
	TextView notfound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// set layout view
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// process
		notfound = (TextView) findViewById(R.id.notfound);
		notfound.setVisibility(View.GONE);
		pb = (ProgressBar) findViewById(R.id.pb);
		pb.setMax(100);
		sp = this.getSharedPreferences("dataLagu", 0);
		spe = sp.edit();

		sBand = sp.getString("band", "");
		sTitle = sp.getString("judul", "");
		Log.d("b", sBand);
		Log.d("t", sTitle);
		list = (ListView) findViewById(R.id.listSong);
		BLOG_URL = "http://www.chordfrenzy.com/browse.php?artis=" + sBand
				+ "&judul=" + sTitle + "&search=Search";
		ImageButton bHome = (ImageButton) findViewById(R.id.bHome);
		bHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HasilCariActivity.this,
						MainActivity.class));

				finish();
			}
		});
		brefresh = (ImageButton) findViewById(R.id.bRefresh);
		brefresh.setOnClickListener(new OnClickListener() {

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

					Toast.makeText(HasilCariActivity.this, "no connection",
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

			Toast.makeText(HasilCariActivity.this, "no connection",
					Toast.LENGTH_SHORT).show();
		}
		// list = (ListView) findViewById(R.id.list);

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

	static class ViewHolder {
		TextView text;
	}

	private class SpecialAdapter extends BaseAdapter {
		// Defining the background color of rows. The row will alternate between
		// green light and green dark.
		// private int[] colors = new int[] { 0xAA46A4FF, 0xAA4CB6DB };
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

			brefresh.setEnabled(false);
			if (pb.getVisibility() == View.INVISIBLE) {
				pb.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected void onPostExecute(Object objResult) {
			// TODO Auto-generated method stub
			try {
				if (objResult != null && objResult instanceof Elements) {
					if (pb.getVisibility() == View.VISIBLE) {
						pb.setVisibility(View.INVISIBLE);
					}

					Elements d = (Elements) objResult;

					links = d.select("a[href]");
					title = new String[links.size()];
					linkTitle = new String[links.size()];
					Elements subt = d.select("div.subtitle");
					Log.d("link size ", String.valueOf(links.size()));
					Log.d(" subt ", String.valueOf(subt.size()));
					int f = 0;
					int g = 0;

					if (links.size() > 0) {
						if (notfound.getVisibility() != View.GONE) {
							notfound.setVisibility(View.GONE);
						}
						for (Element link : links) {
							title[f++] = link.text();
							linkTitle[g++] = link.attr("abs:href");
							Log.d("link", link.attr("abs:href"));
							Log.d("link teks ", link.text());
						}
					} else {
						if (notfound.getVisibility() == View.GONE) {
							notfound.setVisibility(View.VISIBLE);
							notfound.setText("not found");
						}
					}

					SpecialAdapter a = new SpecialAdapter(
							getApplicationContext(), title);
					list.setAdapter(a);
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							spe.putString("addr", linkTitle[arg2]);
							spe.putString("jdl", title[arg2]);
							spe.putString("singer", sBand);

							spe.commit();
							Intent myintent = new Intent(
									HasilCariActivity.this, ChordActivity.class);
							startActivity(myintent);
							finish();
						}
					});
					brefresh.setEnabled(true);
				} else {
					Log.d("resc", "c");
					if (pb.getVisibility() == View.VISIBLE) {
						pb.setVisibility(View.INVISIBLE);
					}
					Toast.makeText(
							getApplicationContext(),
							"Content not Loaded. \nSorry for error connection. \nRefresh please..",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			brefresh.setEnabled(true);
		}

		@Override
		protected Object doInBackground(Object... params) {
			String url = params[0].toString();
			Log.d("u r l   ====>> ", url);
			// TODO Auto-generated method stub
			try {
				document = Jsoup.connect(url).timeout(10000).get();
				div = document.select("div#maincontent");
				Log.d("data", div.toString());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			// ArrayAdapter<String> a = new ArrayAdapter<String>(this,
			// R.layout.rowlayout, title);

			return div;
		}

	}

}
