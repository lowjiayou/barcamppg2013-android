package com.barcamppenang2013;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.barcamppenang2013.tabfragment.AgendaFragment;
import com.barcamppenang2013.tabfragment.HomeFragment;
import com.barcamppenang2013.tabfragment.TabInterface;

public class MainActivity extends SherlockFragmentActivity {

	private Fragment fragment;
	// private String mUrlAgenda =
	// "https://docs.google.com/spreadsheet/pub?key=0AhLn4HpbOY9JdEJqVTBFNU5MaHdHMGRuMDFIcEVxX3c&output=html";
	private String mUrlAgenda = "http://barcamppenang.org/schedule/";
	private String mUrlSponsor = "http://barcamppenang.org/partners-sponsors/";
	private final static int REFRESH_MENU_ID = 0x1234;
	private FragmentTabHost mTabHost;
	private final static String HOME_TAB = "Home";
	private final static String MAP_TAB = "Map";
	private final static String AGENDA_TAB = "Agenda";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (fragment == null) {
			fragment = new HomeFragment();
			updateActionBarTitle(fragment);
		}
		setContentView(R.layout.activity_main);

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		// float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
		// getResources().getDisplayMetrics());
		// mTabHost.getTabWidget().getChildAt(0).getLayoutParams().width =
		// (int)px;
//		Drawable backgroundDrawable = getResources().getDrawable(
//				R.drawable.tab_info_layout);
//		ScaleDrawable scaleDrawable = new ScaleDrawable(backgroundDrawable,
//				Gravity.CENTER, 50f,50f );
//		mTabHost.addTab(
//				mTabHost.newTabSpec(HOME_TAB).setIndicator("",scaleDrawable),
//				HomeFragment.class, null);
		//Info Tab
		ImageView imageView1 = new ImageView(this);
		imageView1.setImageResource(R.drawable.tab_info_front);
		imageView1.setBackgroundResource(R.drawable.tab_info_back);
		
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		imageView1.setLayoutParams(new LinearLayout.LayoutParams((width/4),100));

		
		mTabHost.addTab(mTabHost.newTabSpec(HOME_TAB).setIndicator(imageView1),HomeFragment.class, null);
//		mTabHost.addTab(mTabHost.newTabSpec(HOME_TAB).setIndicator("caicaiz"),HomeFragment.class, null);
		
		//Profile Tab
		ImageView imageView2 = new ImageView(this);
		imageView2.setImageResource(R.drawable.tab_profile_front);
		imageView2.setBackgroundResource(R.drawable.tab_profile_back);
		mTabHost.addTab(mTabHost.newTabSpec(AGENDA_TAB).setIndicator(imageView2),AgendaFragment.class, null);
		
		//Agenda Tab
		ImageView imageView3 = new ImageView(this);
		imageView3.setImageResource(R.drawable.tab_agenda_front);
		imageView3.setBackgroundResource(R.drawable.tab_agenda_back);
		mTabHost.addTab(mTabHost.newTabSpec(HOME_TAB).setIndicator(imageView3),HomeFragment.class, null);
		
		//Friends Tab
		ImageView imageView4 = new ImageView(this); 
		imageView4.setImageResource(R.drawable.tab_friends_front);
		imageView4.setBackgroundResource(R.drawable.tab_friends_back);
		mTabHost.addTab(mTabHost.newTabSpec(AGENDA_TAB).setIndicator(imageView4),AgendaFragment.class, null);
		
		
		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				ActionBar actionBar = getSupportActionBar();
				actionBar.setTitle(tabId);
				// Toast.makeText(getApplicationContext(), tabId,
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, REFRESH_MENU_ID, Menu.NONE, "Refresh")
				.setIcon(R.drawable.ic_refresh_inverse)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			removeHomeFragment();
			FragmentManager fm = getSupportFragmentManager();
			if (fm.getBackStackEntryCount() > 0) {
				fm.popBackStack();
			}
			return true;

		case REFRESH_MENU_ID:
			(new Downloader(this, "agenda.html")).execute(mUrlAgenda);
			(new Downloader(this, "sponsor.html")).execute(mUrlSponsor);
			Toast.makeText(getApplicationContext(),
					"Updating To Latest Agenda...", Toast.LENGTH_SHORT).show();
			// switchContent(new BadgeFragment());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			removeHomeFragment();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void removeHomeFragment() {
		FragmentManager fragment_manager = getSupportFragmentManager();
		HomeFragment home_fragment = (HomeFragment) fragment_manager
				.findFragmentByTag(HOME_TAB);
		if (home_fragment.isAdded()) {
			fragment_manager.beginTransaction().remove(home_fragment).commit();
		}
	}

	public void switchContent(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.realtabcontent, fragment).addToBackStack(null)
				.commit();
		updateActionBarTitle(fragment);
	}

	public void updateActionBarTitle(Fragment fragment) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(((TabInterface) fragment).printTitle());
		// actionBar.setIcon(R.drawable.ic_action_github);
	}

	@Override
	protected void onResume() {
		super.onResume();
		(new Downloader(this, "agenda.html")).execute(mUrlAgenda);
		(new Downloader(this, "sponsor.html")).execute(mUrlSponsor);
		// ImageView imageView = (ImageView)
		// findViewById(R.id.sponsor_imageview);
		// (new BitmapDownloaderTask(imageView))
		// .execute("http://icons.iconarchive.com/icons/deleket/sleek-xp-software/256/Yahoo-Messenger-icon.png");
		// Bitmap temp = BitmapFactory.decodeFile(getFilesDir() +
		// "sponsor.png");
		// Log.d("ddw", "out here!");
		// if (temp != null && imageView != null) {
		// imageView.setImageResource(R.drawable.ic_launcher);
		// imageView.setImageBitmap(temp);
		// }
	}

	static class Downloader extends AsyncTask<String, Void, String> {
		MainActivity activity = null;
		private Exception exception;
		private String fileName;

		Downloader(MainActivity pActivity, String pFileName) {
			activity = pActivity;
			fileName = pFileName;

		}

		protected String doInBackground(String... urls) {
			try {
				return httpGet(urls[0]);
			} catch (Exception e) {
				Log.d("ddw", e.toString());
				return null;
			}
		}

		@SuppressWarnings("deprecation")
		protected void onPostExecute(String result) {
			// Log.d("ddw",result);
			if (result != null) {
				FileOutputStream fOut = null;

				try {
					fOut = activity.openFileOutput(fileName,
							Context.MODE_WORLD_WRITEABLE);
				} catch (FileNotFoundException e) {
					Log.d("ddw1", e.toString());
				}

				OutputStreamWriter osw = new OutputStreamWriter(fOut);
				try {
					osw.write(result);
					osw.flush();
					osw.close();
				} catch (IOException e) {
					Log.d("ddw", e.toString());
				}
			}
		}

		public String httpGet(String url) throws URISyntaxException,
				ClientProtocolException, IOException {
			String htmlBody = null;
			try {
				HttpGet request = new HttpGet();
				HttpClient client = new DefaultHttpClient();

				request.setURI(new URI(url));
				HttpResponse response = client.execute(request);

				htmlBody = EntityUtils.toString(response.getEntity());
			} catch (Exception ex) {
				Log.d("ddw", ex.toString());
			}
			return htmlBody;
		}
	}

	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		// Actual download method, run in the task thread
		protected Bitmap doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			return downloadBitmap(params[0]);
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
			try {
				if (bitmap != null) {
					Log.d("ddw", "here 1");
					FileOutputStream out = new FileOutputStream(getFilesDir()
							+ "sponsor.png");
					Log.d("ddw", "here 2");
					bitmap.compress(CompressFormat.PNG, 90, out);
					Log.d("ddw", "here 3");
					if (imageViewReference != null) {
						ImageView imageView = imageViewReference.get();
						if (imageView != null) {
							Bitmap temp = BitmapFactory
									.decodeFile(getFilesDir() + "sponsor.png");
							if (temp == null) {
								Log.d("ddw", "null");
							} else {
								Log.d("ddw", "not null");
							}
							// imageView.setImageBitmap(temp);
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient
				.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory
							.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}
}
