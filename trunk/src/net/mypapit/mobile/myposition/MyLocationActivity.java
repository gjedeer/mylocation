package net.mypapit.mobile.myposition;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MyLocationActivity extends Activity {
	
	LocationListener myLocationListener;
	 String bestProvider;
	 Location location;
	 TextView tvDecimalCoord,tvDegreeCoord,tvLocation;
	 ImageView shareLocation,shareDecimal,shareDegree;
	 double lat, lon;
	 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_location);
		tvDecimalCoord = (TextView) findViewById(R.id.tvDecimalCoord);
		tvDegreeCoord = (TextView) findViewById(R.id.tvDegreeCoord);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		
		
		
		
	}
	
	public void onResume() {
		super.onResume();
		
		GPSThread thread = new GPSThread(this);
		
		thread.run();
		
		
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
					dialog.setTitle("About My GPS Location" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName );
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
		    	
		    	int time = Integer.parseInt(strTime)*1000;
		    	Log.d("net.mypapit.mobile.myposition","preference retrieved " + time + "ms");
		    	int distance = 10;
		    	
		    	
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
						
					
				}
				
				
				
				activity.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						tvDecimalCoord.setText(sb.toString());
						tvDegreeCoord.setText(toDegree(lat,lon));
						tvLocation.setText(geocode(lat,lon));
						
						

						
					}
					
				});
				
				
				myLocationListener = new LocationListener(){

					@Override
					public void onLocationChanged(Location location) {
						// TODO Auto-generated method stub
						
						lat = location.getLatitude();
						lon = location.getLongitude();
						sb = new StringBuffer();
						
						sb.append(new DecimalFormat("#.#####").format(lat));
						sb.append(","+new DecimalFormat("#.#####").format(lon));
						activity.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								tvDecimalCoord.setText(sb.toString());
								tvDegreeCoord.setText(toDegree(lat,lon));
								tvLocation.setText(geocode(lat,lon));
								
								
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

				locationManager.requestLocationUpdates(bestProvider, time, distance,
		                 myLocationListener);
				 
			  
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
		  
		  @SuppressWarnings("finally")
		public String geocode(double lat, double lon){
			  
			  
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
		  
	  }
	
}

