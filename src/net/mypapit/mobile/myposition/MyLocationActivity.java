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
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;


public class MyLocationActivity extends Activity implements OnClickListener {
	
	LocationListener myLocationListener;
	 String bestProvider;
	 Location location;
	 TextView tvDecimalCoord,tvDegreeCoord,tvLocation,tvMessage;
	 ImageView shareLocation,shareDecimal,shareDegree;
	 double lat, lon, uncertainity;
	 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_location);
		tvDecimalCoord = (TextView) findViewById(R.id.tvDecimalCoord);
		tvDegreeCoord = (TextView) findViewById(R.id.tvDegreeCoord);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvMessage = (TextView) findViewById(R.id.tvMessage);
		
		ImageView shareLocation = (ImageView) findViewById(R.id.shareLocation);
		ImageView shareDecimal= (ImageView) findViewById(R.id.shareDecimal);
		ImageView shareDegree= (ImageView) findViewById(R.id.shareDegree);
		ImageView shareMessage= (ImageView) findViewById(R.id.shareMessage);
		
		shareLocation.setClickable(true);
		shareDecimal.setClickable(true);
		shareDegree.setClickable(true);
		shareMessage.setClickable(true);
		
		shareLocation.setOnClickListener(this);
		shareDecimal.setOnClickListener(this);
		shareDegree.setOnClickListener(this);
		shareMessage.setOnClickListener(this);
	}
	
	public void onResume() {
		super.onResume();
		
		GPSThread thread = new GPSThread(this);
		
		thread.start();
	}
	
	  public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.activity_my_location, menu);
	        return true;
	    }
	
	    public boolean onOptionsItemSelected(MenuItem item){
	    	Intent intent;
	    	switch(item.getItemId()) {
			
			case R.id.menu_about:
				AboutDialog dialog = new AboutDialog(this);
				//dialog.setContentView(R.layout.activity_about_dialog);
				try {
					dialog.setTitle("About My GPS Location " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName );
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				dialog.setCancelable(true);
				dialog.show();
				
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
	  
	  
	  class GPSThread extends Thread {
		  Activity activity;
		  StringBuffer sb;
		  
		  
		  public GPSThread(Activity activity){
			  this.activity = activity;
			  
			  
			  
		  }
		  
		  public void run() {
			  
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
		    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		    	String strTime = pref.getString("updateFreq", "3");
		    	Looper.prepare();
		    	
		    	int time = Integer.parseInt(strTime)*1000;
		    	Log.d("net.mypapit.mobile.myposition","preference retrieved " + time + "ms");
		    	int distance = 50;
		    	
		    	
		    	bestProvider = locationManager.getBestProvider(criteria, false);
				location = locationManager.getLastKnownLocation(bestProvider);
				lat = 0.0;
				lon = 0.0;
				
				if (location!=null){
					sb = new StringBuffer("");
					lat = location.getLatitude();
					lon = location.getLongitude();
					
					sb.append(new DecimalFormat("#.#####").format(lat));
					sb.append(","+new DecimalFormat("#.#####").format(lon));
						
					
				} else {
					
					sb = new StringBuffer("Unknown Address");
				}
				
				
				
				activity.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						tvDecimalCoord.setText(sb.toString());
						tvDegreeCoord.setText(toDegree(lat,lon));
						//tvLocation.setText(geocode(lat,lon));
						GeocodeTask task = new GeocodeTask();
						task.execute(new LatLong(lat,lon));
						
						
						

						
					}
					
				});
				
				
				myLocationListener = new LocationListener(){

					@Override
					public void onLocationChanged(Location location) {
						// TODO Auto-generated method stub
						
						lat = location.getLatitude();
						lon = location.getLongitude();
						uncertainity = location.getAccuracy();
						sb = new StringBuffer();
						
						sb.append(new DecimalFormat("#.#####").format(lat));
						sb.append(","+new DecimalFormat("#.#####").format(lon));
						Log.d("net.mypapit.mobile.myposition","Got location: " + sb.toString());
						activity.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								tvDecimalCoord.setText(sb.toString());
								tvDegreeCoord.setText(toDegree(lat,lon));
								StringBuffer message = new StringBuffer();
								message.append("\nKliknij na jeden z poniższych linków aby zobaczyć moją pozycję na mapie:\nhttps://openstreetmap.org/go/");
								message.append(MapUtils.createShortLinkString(lat, lon, 15));
								message.append("?m");
								message.append("\n\nhttps://maps.google.com/maps?q=" + lat + "," + lon + "&z=15");
								message.append("\n\ngeo:");
								message.append(new DecimalFormat("#.######").format(lat));
								message.append(",");
								message.append(new DecimalFormat("#.######").format(lon));
								message.append(";u=");
								message.append(new DecimalFormat("#.#").format(uncertainity));

								tvMessage.setText(message.toString());
								
								//tvLocation.setText(geocode(lat,lon));
								GeocodeTask task = new GeocodeTask();
								task.execute(new LatLong(lat,lon));
								
								
							}
							
						});

						
						
						
						
					}

					@Override
					public void onProviderDisabled(String provider) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onProviderEnabled(String provider) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						// TODO Auto-generated method stub
						
					}
					
					
				};

//				locationManager.requestLocationUpdates(bestProvider, time, distance,
//		                 myLocationListener);

				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance,
		                 myLocationListener);
				 
			  Looper.loop();
			  
		  }
		  
		  
		  
		  
		  public String toDegree(double lat, double lon)
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
				// TODO Auto-generated method stub
				Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
				List<Address> addresslist=null;
				StringBuffer returnString= new StringBuffer();
				
				try {
					
					addresslist = gc.getFromLocation(lat,lon, 3);
					Log.d("net.mypapit","lat - " + lat);
					Log.d("net.mypapit","lon - " + lon);
					//returnString = new StringBuffer();
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
						//tvLocation.setText(sb.toString());
						
						
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
					Log.d("net.mypapit","lat - " + lat);
					Log.d("net.mypapit","lon - " + lon);
					//returnString = new StringBuffer();
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
						//tvLocation.setText(sb.toString());
						
						
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
		  
	  } //sux


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
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
