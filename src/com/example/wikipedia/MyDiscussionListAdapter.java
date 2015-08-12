package com.example.wikipedia;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MyDiscussionListAdapter implements ListAdapter {

	Cursor cursor;
	
	Context context;
	long key;

	public MyDiscussionListAdapter(Context con, Cursor c) {
		cursor = c;
		context = con;
	
		key = 0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cursor.getCount();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		cursor.moveToPosition(arg0);

		return cursor.getLong(0);
	}

	@Override
	public int getItemViewType(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView messageSearch = null;
		TextView messageResult = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.display_result, parent, false);
		}
		
		
		messageSearch = (TextView) convertView
				.findViewById(R.id.message_query);
		messageResult = (TextView) convertView
				.findViewById(R.id.message_result);
		
		cursor.moveToPosition(position);
		// messageQuery textview will contain the value of the column
		// named Search..
		messageSearch
				.setText(cursor.getString(cursor.getColumnIndex("Search")));
		
		
		// messageResult textView will contain the value of the column
		// named Result..
		messageResult
				.setText(cursor.getString(cursor.getColumnIndex("Result")));

		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
