package net.mypapit.mobile.myposition;
/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * MyLocation 1.1c for Android <mypapit@gmail.com> (9w2wtf)
 * Copyright 2012 Mohammad Hafiz bin Ismail. All rights reserved.
 *
 * Info url :
 * http://code.google.com/p/mylocation/
 * http://kirostudio.com
 * http://blog.mypapit.net/
 * 
 * 
 * MyLocationActivity.java
 * Main Activity Class for MyLocation
 * My GPS Location Tool
 */

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ActionBar;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;


import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MyLocationActivity extends Activity implements OnClickListener, LocationListener {

	LocationListener myLocationListener;
	String bestProvider;
	Location location;
	TextView tvDecimalCoord,tvDegreeCoord,tvLocation,tvOLC,tvMHL,tvMessage, tvUpdatedTime;
	double lat, lon, uncertainity;
	OpenLocationCode OLC;
	MaidenheadLocator MHL;
	long fix; // fix time
	boolean nonEmpty = false;
	boolean gpsFixReceived = false;
	StringBuffer sb;
	String messageHeader = "";
	String strTime = "0";
	public static final int MY_DEFAULT_OLC_LENGTH = 10;
	int OLCLength = MY_DEFAULT_OLC_LENGTH;
	private DateFormat df;
	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 0x29b;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
		df.setTimeZone(TimeZone.getDefault());
		setContentView(R.layout.activity_my_location);
		ActionBar ab = getActionBar();
		ab.setTitle("My Location");
		tvDecimalCoord = (TextView) findViewById(R.id.tvDecimalCoord);
		tvDegreeCoord = (TextView) findViewById(R.id.tvDegreeCoord);
		tvUpdatedTime = (TextView) findViewById(R.id.tvUpdatedTime);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvOLC = (TextView) findViewById(R.id.tvOLC);
		tvMHL = (TextView) findViewById(R.id.tvMHL);
		tvMessage = (TextView) findViewById(R.id.tvMessage);

		ImageView shareLocation = (ImageView) findViewById(R.id.shareLocation);
		ImageView shareDecimal= (ImageView) findViewById(R.id.shareDecimal);
		ImageView shareDegree= (ImageView) findViewById(R.id.shareDegree);
		ImageView shareOLC= (ImageView) findViewById(R.id.shareOLC);
		ImageView shareMHL= (ImageView) findViewById(R.id.shareMHL);
		ImageView shareMessage= (ImageView) findViewById(R.id.shareMessage);

		shareLocation.setClickable(true);
		shareDecimal.setClickable(true);
		shareDegree.setClickable(true);
		shareOLC.setClickable(true);
		shareMHL.setClickable(true);
		shareMessage.setClickable(true);

		shareLocation.setOnClickListener(this);
		shareDecimal.setOnClickListener(this);
		shareDegree.setOnClickListener(this);
		shareOLC.setOnClickListener(this);
		shareMHL.setOnClickListener(this);
		shareMessage.setOnClickListener(this);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		messageHeader = pref.getString("messageHeader", "Kliknij na jeden z poniższych linków aby zobaczyć moją pozycję na mapie:");
		strTime = pref.getString("updateFreq", "3");
		try {
			OLCLength = Integer.parseInt(pref.getString("OLCLength", "10"));
		} catch (NumberFormatException e) {
			OLCLength = MY_DEFAULT_OLC_LENGTH;
		}
	}

	public void onResume() {
		super.onResume();

		this.checkLocationPermission();
		this.registerRelativeFixTime();
	}

	public boolean checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
						Manifest.permission.ACCESS_FINE_LOCATION)) {

				Log.d("net.mypapit.mobile.myposition","Location permission requested (explanation).");
				ActivityCompat.requestPermissions(MyLocationActivity.this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_LOCATION);
			} else {
				// No explanation needed, we can request the permission.
				Log.d("net.mypapit.mobile.myposition","Location permission requested (no explanation).");
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_LOCATION);
			}
			return false;
		} else {
			this.registerLocationListener();
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			String permissions[], int[] grantResults) {
		/* Empty method - TODO: handle denial with a message */
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// location-related task you need to do.
					if (ContextCompat.checkSelfPermission(this,
							Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED) {

						Log.d("net.mypapit.mobile.myposition","Location permission request granted.");
						//Request location updates:
						this.registerLocationListener();
					} else {
						Log.d("net.mypapit.mobile.myposition","Location permission request denied [1].");
						/* TODO: handle denial gracefully */
					}

				} else {
					Log.d("net.mypapit.mobile.myposition","Location permission request denied [2].");
					/* TODO: handle denial gracefully */

				}
				return;
			}
		}
	}

	private static String getMessage(double lat, double lon, double uncertainity, String messageHeader, boolean nonEmpty) {
		StringBuffer message = new StringBuffer();

		if(!nonEmpty && lat == 0.0 && lon == 0.0) {
			return "(position not known yet)";
		}

		message.append(messageHeader);
		message.append("\nhttps://openstreetmap.org/go/");
		message.append(MapUtils.createShortLinkString(lat, lon, 15));
		message.append("?m");
		message.append("\n\nhttps://maps.google.com/maps/search/?api=1&query=" + lat + "%2C" + lon);
		message.append("\n\nhttps://download.osmand.net/go?lat=" + lat + "&lon=" + lon + "&z=15");
		message.append("\n\ngeo:");
		message.append(Float.toString((float)lat));
		message.append(",");
		message.append(Float.toString((float)lon));
		message.append(";u=");
		message.append(Float.toString((float)uncertainity));

		return message.toString();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_my_location, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item){
		Intent intent;
		switch(item.getItemId()) {

			case R.id.menu_about:
				AboutDialog dialog = new AboutDialog(this);
				try {
					dialog.setTitle("About My GPS Location " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName );
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				dialog.setCancelable(true);
				dialog.show();

				return true;
			case R.id.menu_get_fix:
				Intent service = new Intent(MyLocationActivity.this, GetFixService.class);
				if (!GetFixService.IS_SERVICE_RUNNING) {
					service.setAction(GetFixService.START_FOREGROUND_ACTION);
					GetFixService.IS_SERVICE_RUNNING = true;
				} else {
					service.setAction(GetFixService.STOP_FOREGROUND_ACTION);
					GetFixService.IS_SERVICE_RUNNING = false;

				}
				startService(service);

				return true;
			case R.id.menu_settings:
				intent = new Intent(getBaseContext(),SettingsActivity.class);
				startActivity(intent);
				return true;

			case R.id.menu_viewmap:
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+lat+","+lon+"?z=15"));
				startActivity(intent);
				return true;

			case R.id.menu_converter:
				intent = new Intent();
				intent.setClass(getBaseContext(), ConverterActivity.class);
				intent.putExtra("Coordinate", lat+","+lon);
				startActivityForResult(intent,-1);

				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void registerRelativeFixTime() {
		final Handler handler = new Handler();
		final String time_header = this.getString(R.string.last_fix_time);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				final String relative_date = DateUtils.getRelativeTimeSpanString(fix, System.currentTimeMillis(), 0, 0).toString();
				tvUpdatedTime.setText(time_header + relative_date);
				handler.postDelayed(this, 3000);
			}
		}, 3000);
	}

	private OpenLocationCode getOLC(double lat, double lon) {
		try {
			return new OpenLocationCode(lat, lon, OLCLength);
		} catch (IllegalArgumentException e) {
			return new OpenLocationCode(lat, lon, MY_DEFAULT_OLC_LENGTH);
		}
	}

	private MaidenheadLocator getMHL(double lat, double lon) {
		return new MaidenheadLocator(lat, lon);
	}

	public void registerLocationListener() {
		LocationManager locationManager;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(false);
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);

		int time = Integer.parseInt(strTime)*1000;
		Log.d("net.mypapit.mobile.myposition","preference retrieved " + time + "ms");
		int distance = 50;

		//			bestProvider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		lat = 0.0;
		lon = 0.0;
		nonEmpty = false;

		if (location!=null){
			sb = new StringBuffer("");
			lat = location.getLatitude();
			lon = location.getLongitude();
			OLC = getOLC(lat, lon);
			MHL = getMHL(lat, lon);
			fix = location.getTime();
			uncertainity = location.getAccuracy();

			sb.append(Float.toString((float)lat)).append(",").append(Float.toString((float)lon));
		} else {
			sb = new StringBuffer("Unknown Address");
		}

		final String time_header = this.getString(R.string.last_fix_time);

		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				tvDecimalCoord.setText(sb.toString());
				tvDegreeCoord.setText(toDegree(lat,lon));
				if(OLC != null) {
					tvOLC.setText(OLC.getCode());
				}
				if(MHL != null) {
					tvMHL.setText(MHL.getLOC());
				}

				final String relative_date = DateUtils.getRelativeTimeSpanString(fix, System.currentTimeMillis(), 0, 0).toString();
				tvUpdatedTime.setText(time_header + relative_date);

				tvMessage.setText(MyLocationActivity.getMessage(lat, lon, uncertainity, messageHeader, nonEmpty));

				GeocodeTask task = new GeocodeTask();
				task.execute(new LatLong(lat,lon));
			}
		});

		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, time, distance, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, this);
		Log.d("net.mypapit.mobile.myposition","GPS location requested from thread");
		if(locationManager.getAllProviders().contains("network")) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, distance, this);
		}

	} // LocationListener
	@Override
	public void onLocationChanged(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
		fix = location.getTime();
		OLC = getOLC(lat, lon);
		nonEmpty = true;
		uncertainity = location.getAccuracy();
		sb = new StringBuffer();
		final String time_header = this.getString(R.string.last_fix_time);

		/* Don't display coarse location once GPS location is known */
		if(location.getProvider() != LocationManager.GPS_PROVIDER && gpsFixReceived) {
			Log.d("net.mypapit.mobile.myposition", "Ignored location from: " + location.getProvider());
		}
		if(location.getProvider() == LocationManager.GPS_PROVIDER) {
			gpsFixReceived = true;
		}

		sb.append(Float.toString((float)lat)).append(",").append(Float.toString((float)lon));
		Log.d("net.mypapit.mobile.myposition","Got location: " + sb.toString());
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				final String relative_date = DateUtils.getRelativeTimeSpanString(fix, System.currentTimeMillis(), 0, 0).toString();
				tvDecimalCoord.setText(sb.toString());
				tvDegreeCoord.setText(toDegree(lat,lon));
				tvOLC.setText(OLC.getCode());
				tvUpdatedTime.setText(time_header + relative_date);
				tvMessage.setText(MyLocationActivity.getMessage(lat, lon, uncertainity, messageHeader, true));

				GeocodeTask task = new GeocodeTask();
				task.execute(new LatLong(lat,lon));
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();

		LocationManager locationManager;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("net.mypapit.mobile.myposition","GPS provider disabled: " + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("net.mypapit.mobile.myposition","GPS provider enabled: " + provider);
	}

	@Override
	public void onStatusChanged(String provider, int status,
			Bundle extras) {
		Log.d("net.mypapit.mobile.myposition","GPS status changed: " + provider + "," + status);
	}

	public static String toDegree(double lat, double lon)
	{
		StringBuffer stringb = new StringBuffer();

		LatLonConvert convert =new LatLonConvert(lat);

		stringb.append(new DecimalFormat("#").format(convert.getDegree())+"\u00b0 ");
		stringb.append(new DecimalFormat("#").format(convert.getMinute())+"\' ");
		stringb.append(new DecimalFormat("#.###").format(convert.getSecond()) +"\" , ");

		convert =new LatLonConvert(lon);

		stringb.append(new DecimalFormat("#").format(convert.getDegree())+"\u00b0 ");
		stringb.append(new DecimalFormat("#").format(convert.getMinute())+"\' ");
		stringb.append(new DecimalFormat("#.###").format(convert.getSecond()) +"\"");

		return stringb.toString();
	}

	protected class GeocodeTask extends AsyncTask<LatLong,Void,String> {
		@SuppressWarnings("finally")
		@Override
		protected String doInBackground(LatLong... params) {
			Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
			List<Address> addresslist=null;
			StringBuffer returnString= new StringBuffer();

			try {
				addresslist = gc.getFromLocation(lat,lon, 3);

				if (addresslist == null){
					return "Unknown Address";
				}
				else {
					if (addresslist.size()>0) {
						Address add = addresslist.get(0);
						for (int i=0;i<add.getMaxAddressLineIndex();i++){
							returnString.append(add.getAddressLine(i)).append("\n");	
						}

						returnString.append(add.getLocality()).append("\n");
					}
				}
			} catch (IOException e){
				Log.d("net.mypapit.mobile","geocoder exception " + e.toString());
				returnString = new StringBuffer("Unknown Address");
			} finally {
				if ( returnString.toString().length() < 5) {
					return "Unknown Address";
				}

				return returnString.toString();
			}
		}

		protected void onPostExecute(String result){
			tvLocation.setText(result);
		}
	}//end GeocodeTask


	@SuppressWarnings("finally")
	public String geocode(double lat, double lon)
	{
		Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
		List<Address> addresslist=null;
		StringBuffer returnString= new StringBuffer();

		try {
			addresslist = gc.getFromLocation(lat,lon, 3);

			if (addresslist == null){
				return "Unknown Address";
			}
			else {
				if (addresslist.size()>0) {
					Address add = addresslist.get(0);
					for (int i=0;i<add.getMaxAddressLineIndex();i++){
						returnString.append(add.getAddressLine(i)).append("\n");	
					}

					returnString.append(add.getLocality()).append("\n");
				}
			}
		} catch (IOException e){
			Log.d("net.mypapit.mobile","geocoder exception " + e.toString());
			returnString = new StringBuffer("Unknown Address");
		} finally {
			if ( returnString.toString().length() < 5) {
				return "Unknown Address";
			}

			return returnString.toString();
		}
	}


	/*
	 * Share button clicked next to one of the text boxes
	 */
	@Override
	public void onClick(View view) {
		Intent intent;
		intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TITLE, "My Location");
		intent.setType("text/plain");
		switch (view.getId()){
			case R.id.shareLocation:
				intent.putExtra(Intent.EXTRA_TEXT, "My current location :\n " + tvLocation.getText());
				break;

			case R.id.shareDecimal:
				intent.putExtra(Intent.EXTRA_TEXT, "My current position: " + tvDecimalCoord.getText());
				break;

			case R.id.shareDegree:
				intent.putExtra(Intent.EXTRA_TEXT, "My current position: \n" + tvDegreeCoord.getText());
				break;

			case R.id.shareOLC:
				intent.putExtra(Intent.EXTRA_TEXT, "My current position: \n" + tvOLC.getText());
				break;

			case R.id.shareMessage:
				intent.putExtra(Intent.EXTRA_TEXT, tvMessage.getText());
				break;
		}

		startActivity(Intent.createChooser(intent, "Share via"));
	}
}

class LatLong {

	double lat, lon;

	public LatLong(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

}
