package com.indo.chord.helper;

import java.util.ArrayList;

import com.indo.chord.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChordBaseAdapter extends BaseAdapter {
	private static ArrayList<SearchResults> searchArrayList;

	private LayoutInflater mInflater;
	String aaa = "a";

	public ChordBaseAdapter(Context context, ArrayList<SearchResults> results) {
		searchArrayList = results;
		mInflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		return searchArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return searchArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.rowlayout, null);
			holder = new ViewHolder();

			holder.nama = (TextView) convertView.findViewById(R.id.headline);
			holder.singer = (TextView) convertView.findViewById(R.id.singer);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nama.setText(searchArrayList.get(position).getJudul());
		holder.singer.setText(searchArrayList.get(position).getSinger());
		return convertView;
	}

	static class ViewHolder {
		TextView nama, singer;

	}

}