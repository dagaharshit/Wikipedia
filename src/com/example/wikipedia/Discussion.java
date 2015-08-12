package com.example.wikipedia;

import com.example.wikipedia.Database.MyDatabase;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Discussion extends ListActivity implements OnClickListener {

	Cursor cursor;
	MyDatabase db;
	MyDiscussionListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		getDiscussionFromDatabase();

		// setContentView(R.layout.discussion_xml);
		adapter = new MyDiscussionListAdapter(this, cursor);
		setListAdapter(adapter);
		setSelection(cursor.getCount() - 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.discussion_menu, menu);
		return true;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_clear:
			clearAll();
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (db != null) {
			cursor.close();
			db.close();
		}
	}

	private void getDiscussionFromDatabase() {
		// TODO Auto-generated method stub

		try {
			db = new MyDatabase(this, 1);
			cursor = db.getDiscussion();
		} catch (SQLException e) {
			Toast.makeText(this, "Could not load from Database",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

	private void clearAll() {
		setListAdapter(null);
		adapter = null;
		cursor.close();
		db = new MyDatabase(this, 1);
		try {
			db.delete();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db = null;
		cursor = null;

	}

}
