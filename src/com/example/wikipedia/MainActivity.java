package com.example.wikipedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	EditText txtSearch;
	Button btnSearch;
	HttpClient httpClient;

	ArrayList<String> listData = null;

	String data = null;
	ListView list;
	final static String URL = "http://en.wikipedia.org/w/api.php?action=opensearch&limit=20&search=";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		initializeVariable();

	}

	private void initializeVariable() {
		// TODO Auto-generated method stub

		txtSearch = (EditText) findViewById(R.id.wikiSearchTxt);
		httpClient = new DefaultHttpClient();

		list = (ListView) findViewById(R.id.listResult);
		list.setOnItemClickListener(this);

		listData = new ArrayList<String>();

		btnSearch = (Button) findViewById(R.id.wikiSearchBtn);
		btnSearch.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {

		case R.id.wikiSearchBtn:
			listData.clear();
			JSONParse jParse = new JSONParse();
			jParse.execute();

			break;

		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		String itemValue = (String) list.getItemAtPosition(position);
		Intent i = new Intent(MainActivity.this, WikiIntroduction.class);

		i.putExtra("data", itemValue);

		startActivity(i);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.menu_discussions:
			Intent i = new Intent(MainActivity.this, Discussion.class);
			startActivity(i);
			break;

		case R.id.menu_about:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.setMessage("App Name: Wikipedia\nApp Version: 1.0\nDeveloper: Harshit Daga\n");
			alertDialog.show();
			break;
		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	class JSONParse extends AsyncTask<String, String, String> {
		ProgressDialog pd;

		public JSONParse() {
			// TODO Auto-generated constructor stub
			pd = new ProgressDialog(MainActivity.this);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			pd.setTitle("Please wait...");
			pd.setMessage("Fetching results from Wikipedia...");
			pd.setCancelable(true);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			// fetching data from the URL...
			// ...
			// ...
			URI webpage;
			try {
				webpage = new URI(URL
						+ URLEncoder.encode(txtSearch.getText().toString(),
								"UTF-8"));

				data = fetchDataFromURL(webpage);
				// Parsing JSON data to the List....
				// ...
				// ...

				parseJSONDataToArrayList(data);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			pd.dismiss();

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					MainActivity.this, android.R.layout.simple_list_item_1,
					listData);

			list.setAdapter(adapter);

		}

		private void parseJSONDataToArrayList(String jsonData)
				throws JSONException {
			int i = 0;

			// JSONObject jObject = new JSONObject(data);

			JSONArray jArray;

			jArray = new JSONArray(jsonData);

			for (i = 0; i < jArray.length(); i++) {
				Object o = jArray.get(i);

				if (o instanceof JSONArray) {
					JSONArray values = (JSONArray) o;
					for (int j = 0; j < values.length(); j++) {
						String string = (String) values.get(j);

						// wikiResult.append("\n" + string);
						listData.add(string);
					}
				}
			}

		}

		private String fetchDataFromURL(URI webpage)
				throws ClientProtocolException, IOException {
			BufferedReader in = null;
			StringBuffer sb;
			try {
				sb = new StringBuffer("");

				HttpGet request = new HttpGet();
				request.setURI(webpage);

				HttpResponse response = httpClient.execute(request);

				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));

				String l;
				while ((l = in.readLine()) != null) {
					sb.append(l + "\n");
				}

			} finally {
				if (in != null) {
					in.close();
				}
			}

			return sb.toString();

		}
	}

}