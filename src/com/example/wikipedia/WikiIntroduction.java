package com.example.wikipedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.wikipedia.Database.MyDatabase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WikiIntroduction extends Activity {

	private TextView introText;
	private ImageView image, imgView;

	private ExpandableListView listTableOfContent;

	private HttpClient httpClient;

	String intentString = null;

	final static String URLText = "http://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&exlimit=1&exintro=&indexpageids=&titles=";
	final static String URLImage = "http://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=350&indexpageids=&titles=";
	final static String URITableOfContent = "http://en.wikipedia.org/w/api.php?action=parse&format=json&prop=sections&redirects&page=";

	ArrayList<Category> tableContents;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.intro_page);

		intentString = getIntent().getStringExtra("data");
		initializeVariables();

		String[] argument = new String[3];
		try {
			argument[0] = URLText + URLEncoder.encode(intentString, "UTF-8");
			argument[1] = URITableOfContent
					+ URLEncoder.encode(intentString, "UTF-8");
			argument[2] = URLImage + URLEncoder.encode(intentString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		getActionBar().setTitle(intentString);

		LoadImageandTableContents litc = new LoadImageandTableContents();
		litc.execute(argument);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (imgView.getVisibility() == View.VISIBLE) {
			imgView.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}

	}

	@SuppressLint("NewApi")
	private void initializeVariables() {
		// TODO Auto-generated method stub
		introText = (TextView) findViewById(R.id.txtIntro);
		image = (ImageView) findViewById(R.id.image);
		imgView = (ImageView) findViewById(R.id.imgShowImage);
		httpClient = new DefaultHttpClient();
		listTableOfContent = (ExpandableListView) findViewById(R.id.listContents);


		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgView.setVisibility(View.VISIBLE);

			}
		});

		imgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgView.setVisibility(View.GONE);
			}
		});

	}

	private String fetchingJSONTextFromURL() throws URISyntaxException,
			ClientProtocolException, IOException, UnknownHostException {
		URI webPage = new URI(URLText
				+ URLEncoder.encode(intentString, "UTF-8"));

		HttpGet request = new HttpGet();
		request.setURI(webPage);

		HttpResponse response = httpClient.execute(request);
		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer sBuffer = new StringBuffer("");
		{
			String l;
			while ((l = in.readLine()) != null) {
				sBuffer.append(l + "\n");
			}
			in.close();
		}
		String data = sBuffer.toString();

		return data;
	}

	private String fetchingJSONImageFromURL() throws URISyntaxException,
			ClientProtocolException, IOException, UnknownHostException {
		URI webPage = new URI(URLImage
				+ URLEncoder.encode(intentString, "UTF-8"));

		HttpGet request = new HttpGet();
		request.setURI(webPage);

		HttpResponse response = httpClient.execute(request);
		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer sBuffer = new StringBuffer("");

		String l;
		while ((l = in.readLine()) != null) {
			sBuffer.append(l + "\n");
		}
		in.close();

		return sBuffer.toString();
	}

	private String parsingJSONForImage(String data) throws JSONException {
		// TODO Auto-generated method stub
		String imgUri = null;

		JSONObject jObject = new JSONObject(data);
		jObject = jObject.getJSONObject("query").getJSONObject("pages");

		JSONArray pages = jObject.names();
		jObject = jObject.getJSONObject(pages.getString(0));

		jObject = jObject.getJSONObject("thumbnail");

		// String imgUri = jObject.getJSONArray("source").toString();
		imgUri = jObject.getString("source");

		return imgUri;
	}

	private String parsingJSONForText(String data) {
		// TODO Auto-generated method stub

		String resultString = null;
		try {
			JSONObject jObject = new JSONObject(data);
			jObject = jObject.getJSONObject("query").getJSONObject("pages");

			// jObject = jObject.getJSONObject("extract");
			JSONArray pages = jObject.names();
			jObject = jObject.getJSONObject(pages.getString(0));

			// introText.setText(Html.fromHtml(jObject.getString("extract")));

			resultString = jObject.getString("extract");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultString;

	}

	private Drawable LoadImageFromURL(String path)
			throws MalformedURLException, IOException {

		InputStream is;
		Drawable d = null;

		is = (InputStream) new URL(path).getContent();

		d = Drawable.createFromStream(is, "src name");

		return d;
	}

	class LoadImageandTableContents extends AsyncTask<String, String, Drawable> {

		ArrayList<String> arrayList;
		String jsonIntroText = null;
		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(WikiIntroduction.this);
			progress.setTitle("Please Wait..");
			progress.setMessage("Loading Data for " + intentString);
			progress.setCancelable(true);
			progress.show();

		}

		@Override
		protected Drawable doInBackground(String... arg) {
			// TODO Auto-generated method stub

			// arg[0]--> URL for intro text..
			// arg[1]--> URL for table of content...
			// arg[2] -->URL for image...

			try {
				jsonIntroText = fetchingJSONTextFromURL();

			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			publishProgress("textLoaded");

			tableContents = new ArrayList<Category>();

			BufferedReader in = null;
			try {
				URI tableContentsURI = new URI(arg[1]);

				HttpGet request = new HttpGet();
				request.setURI(tableContentsURI);

				HttpClient httpClient = new DefaultHttpClient();

				HttpResponse response = httpClient.execute(request);

				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));

				StringBuffer buffer = new StringBuffer("");
				String data;
				while ((data = in.readLine()) != null) {
					buffer.append(data + "\n");
				}
				in.close();

				String contentData = buffer.toString();

				JSONObject jObject = new JSONObject(contentData);
				jObject = jObject.getJSONObject("parse");
				JSONArray sections = jObject.getJSONArray("sections");
				int sectionCount = sections.length() - 1;

				for (int i = 0; i < sectionCount;) {
					JSONObject obj = new JSONObject();
					obj = sections.getJSONObject(i);

					String str = obj.getString("line");

					int cLevel = obj.getInt("toclevel");
					int index = obj.getInt("index");
					Category category = new Category();

					while (cLevel > 1) {
						i++;

						obj = sections.getJSONObject(i);

						str = obj.getString("line");
						index = obj.getInt("index");

						cLevel = obj.getInt("toclevel");
					}

					category.setIndex(index);
					category.setTitle(str);
					tableContents.add(category);
					i++;
				}

				publishProgress("tableOfContentLoaded");

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// Toast.makeText(WikiIntroduction.this, "JSONException",
				// Toast.LENGTH_LONG).show();
			}
			String imageJSON = null;
			String imageURI = null;
			try {
				imageJSON = fetchingJSONImageFromURL();
				imageURI = parsingJSONForImage(imageJSON);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Drawable drawable = null;
			if (imageURI != null) {
				try {
					drawable = LoadImageFromURL(imageURI);

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return drawable;

		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub

			if (values[0] == "textLoaded") {
				introText.setText(Html
						.fromHtml(parsingJSONForText(jsonIntroText)));

				String result = introText.getText().toString();
				try {

					if (result.trim() != null) {
						MyDatabase db = new MyDatabase(WikiIntroduction.this, 1); // context,
																					// version
						db.addDiscussion(intentString, result);
						db.close();
					} else {
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				progress.dismiss();

			} else if (values[0] == "tableOfContentLoaded") {
				TextView listHeaderText = new TextView(WikiIntroduction.this);
				
				if (tableContents.size() > 0) {

					listHeaderText.setText("Content");

				} else {
					listHeaderText.setText("Content Not available.");

				}
				listHeaderText.setTypeface(null, Typeface.BOLD);

				listTableOfContent.addHeaderView(listHeaderText);
				listTableOfContent.setAdapter(new TableOfContentsListAdapter(
						tableContents, WikiIntroduction.this, intentString));

			} else {
			}

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Drawable drawable) {
			// TODO Auto-generated method stub

			ProgressBar progressImage = (ProgressBar) findViewById(R.id.progressBarImage);

			progressImage.setVisibility(View.GONE);

			image.setImageDrawable(drawable);
			image.setVisibility(View.VISIBLE);
			image.invalidate();

			imgView.setImageDrawable(drawable); // imgView --> full screen image
												// preview

			WikiIntroduction.this.getActionBar().setIcon(drawable);
		}

	}

}

class TableOfContentsListAdapter extends BaseExpandableListAdapter {

	ArrayList<Category> arrayList;
	private Activity context;
	private String searchData;

	public ArrayList<TVHolder> holderList;

	public TableOfContentsListAdapter(ArrayList<Category> array, Activity con,
			String str) {
		// TODO Auto-generated constructor stub
		arrayList = array;
		context = con;
		searchData = str;

		holderList = new ArrayList<TVHolder>();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		String childContentURI = null;
		try {
			childContentURI = "http://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&rvparse&titles="
					+ URLEncoder.encode(searchData, "UTF-8")
					+ "&rvsection="
					+ arrayList.get(groupPosition).index;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TVHolder holder = null;

		try {
			holder = holderList.get(groupPosition);
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (holder == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.table_of_content_description, null);
			TextView tv = (TextView) convertView
					.findViewById(R.id.txtTableOfContentDesc);
			tv.setText("");
			ProgressBar progress = (ProgressBar) convertView
					.findViewById(R.id.progressBar1);
			holder = new TVHolder(groupPosition, convertView, tv);
			holderList.add(holder);
			LoadTableContentData ltcd = new LoadTableContentData(holder, tv,
					progress);
			ltcd.execute(childContentURI);
		} else {
			convertView = holder.view;
		}

		if (holder.content != null) {
			holder.tv.setText(Html.fromHtml(holder.content));
		}
		return convertView;
	}

	private String loadJSONFormatContentDescription(String childContentURI) {
		// TODO Auto-generated method stub
		HttpClient httpClient = new DefaultHttpClient();
		String text = null;
		try {
			URI webpage = new URI(childContentURI);

			HttpGet request = new HttpGet();
			request.setURI(webpage);

			HttpResponse response = httpClient.execute(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer sBuffer = new StringBuffer("");
			{
				String l;
				while ((l = in.readLine()) != null) {
					sBuffer.append(l + "\n");
				}
				in.close();
			}
			String data = sBuffer.toString();

			JSONObject obj = new JSONObject(data);

			obj = obj.getJSONObject("query").getJSONObject("pages");

			JSONArray pages = obj.names();

			obj = obj.getJSONObject(pages.getString(0));

			JSONArray jArray = obj.getJSONArray("revisions");
			obj = jArray.getJSONObject(0);

			text = obj.getString("*");

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return text;

	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub

		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.table_of_content_group_item, null);
		}

		TextView tvContent = (TextView) convertView
				.findViewById(R.id.txtTableOFContent);
		tvContent.setText(arrayList.get(groupPosition).title);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	class LoadTableContentData extends AsyncTask<String, String, String> {

		private TextView tvContent;
		private ProgressBar progressBar;
		private TVHolder holder;

		public LoadTableContentData(TVHolder holder, TextView tv,
				ProgressBar progress) {
			// TODO Auto-generated constructor stub
			tvContent = tv;
			progressBar = progress;
			this.holder = holder;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String text = loadJSONFormatContentDescription(params[0]);
			if (text == null) {
				text = "<p>No text available</p>";
			}

			return text;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			holder.content = result;

			tvContent.setText(Html.fromHtml(result));
			progressBar.setVisibility(View.GONE);
		}

	}

	class TVHolder {

		public String content = null;
		public View view = null;
		public int index = 0;
		public TextView tv = null;

		public TVHolder(int i, View v, TextView tv) {
			index = i;
			view = v;
			this.tv = tv;
		}

		public TVHolder() {
			// TODO Auto-generated constructor stub
		}
	}

}
