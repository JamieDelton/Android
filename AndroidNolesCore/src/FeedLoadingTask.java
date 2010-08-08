package com.itnoles.shared;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.*;

public class FeedLoadingTask extends AsyncTask<Void, Void, Void> {
	private Context context;
	private ListView listView;
	private List<News> news;
	private String url;
	private String title;

	public FeedLoadingTask(Context context, ListView listView, String url, String title) {
		this.context = context;
		this.listView = listView;
		this.url = url;
		this.title = title;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if (news != null) {
			if (title != null) {
				SeparatedListAdapter adapter = new SeparatedListAdapter(context);
				adapter.addSection(title, new NewsAdapter(context, news));
				listView.setAdapter(adapter);
			} else {
				listView.setAdapter(new NewsAdapter(context, news));	
			}
		}
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		news = new FeedParser().parse(url);
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		Toast.makeText(context, "Reading Feed, Please wait.", Toast.LENGTH_LONG).show();
	}
}