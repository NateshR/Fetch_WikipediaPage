package com.example.wikipedia;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	EditText et;
	Button bt;
	TextView tv;
	private static String url;
	private Dialog pDialog;

	private String TAG_PARSE = "parse";
	private String TAG_TEXT = "text";
	private String TAG_DEBUG = "wiki";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		et = (EditText) findViewById(R.id.etGet);
		bt = (Button) findViewById(R.id.bSubmit);
		tv = (TextView) findViewById(R.id.tvText);
		tv.setMovementMethod(new ScrollingMovementMethod());
		bt.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		String got = et.getText().toString();
		new GetContent().execute(got);
	}

	private class GetContent extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			((ProgressDialog) pDialog).setMessage("Please Wait..");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			tv.setText(result);

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			ServiceHandler sh = new ServiceHandler();
			url = "http://en.wikipedia.org/w/api.php?action=parse&page="
					+ params[0] + "&prop=text&section=0&format=json";
			String jsonURL = sh.makeServiceCall(url, ServiceHandler.GET);
			String text = "";
			if (jsonURL != null) {

				try {
					JSONObject obj = new JSONObject(jsonURL);
					JSONObject objParse = obj.getJSONObject(TAG_PARSE);
					JSONObject objText = objParse.getJSONObject(TAG_TEXT);
					String text_html = objText.getString("*");

					Document text_document = Jsoup.parse(text_html);
					Elements pElements = text_document.select("p");
					for (Element e : pElements) {
						text =  text +"\n"+ e.text();
						
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Log.d(TAG_DEBUG, "Null response");

			}
			return text;
		}
	}
}