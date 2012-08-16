package net.mypapit.mobile.myposition;

import java.text.DecimalFormat;
import java.util.StringTokenizer;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class ConverterActivity extends Activity  {

	EditText tvDecimalLat, tvDecimalLon, tvDegreeLat,tvMinuteLat, tvSecondLat,tvDegreeLon,tvMinuteLon,tvSecondLon;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        
        tvDecimalLat = (EditText) findViewById(R.id.latDecimal);
        tvDecimalLon = (EditText) findViewById(R.id.lonDecimal);
        
        tvDegreeLat = (EditText) findViewById(R.id.degreelat);
        tvMinuteLat = (EditText) findViewById(R.id.minutelat);
        tvSecondLat = (EditText) findViewById(R.id.secondlat);
        
        tvDegreeLon = (EditText) findViewById(R.id.degreelon);
        tvMinuteLon = (EditText) findViewById(R.id.minutelon);
        tvSecondLon = (EditText) findViewById(R.id.secondlon);
        String coordinates = (String) getIntent().getSerializableExtra("Coordinate");
        StringTokenizer token = new StringTokenizer(coordinates, ",");
        tvDecimalLat.setText(token.nextToken());
        tvDecimalLon.setText(token.nextToken());
        
        this.toDegree();
        
  	  AdView adView = new AdView(this,AdSize.BANNER, "a1502d044a3018b");
		
  		//RelativeLayout layout= (RelativeLayout) findViewById(R.id.mainLayout);
  		//layout.addView(adView);
  		adView.loadAd(new AdRequest());

                
        
       /*
        *  
        tvDecimalLat.addTextChangedListener(this);
        
        tvDecimalLon.addTextChangedListener(this);
        
        tvDegreeLat.addTextChangedListener(this);
        tvMinuteLat.addTextChangedListener(this);
        tvSecondLat.addTextChangedListener(this);
        
        tvDegreeLon.addTextChangedListener(this);
        tvMinuteLon.addTextChangedListener(this);
        tvSecondLon.addTextChangedListener(this);
        */
        
        
        
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_converter, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    		case R.id.menu_toDecimal:
    			this.toDecimal();
    		break;
    		
    		case R.id.menu_toDegree:
    			this.toDegree();
    		break;
    	
    	
    	}
    	
    	
    	
    	return super.onOptionsItemSelected(item);
    	
    	
    }
    

    public void toDegree(){
    	
    	
    	double lat = Double.parseDouble(tvDecimalLat.getText().toString());
    	
    	double lon = Double.parseDouble(tvDecimalLon.getText().toString());
    	
    	LatLonConvert convert = new LatLonConvert(lat);
    	
    	tvDegreeLat.setText(""+new DecimalFormat("#").format(convert.getDegree()));
    	tvMinuteLat.setText(""+new DecimalFormat("#").format(convert.getMinute()));
    	tvSecondLat.setText(""+new DecimalFormat("#.##").format(convert.getSecond()));
    	
    	convert = new LatLonConvert(lon);
    	tvDegreeLon.setText(""+new DecimalFormat("#").format(convert.getDegree()));
    	tvMinuteLon.setText(""+new DecimalFormat("#").format(convert.getMinute()));
    	tvSecondLon.setText(""+new DecimalFormat("#.##").format(convert.getSecond()));
    	
    	
    	
    	
    }
    
    public void toDecimal()
    {
    	LatLonConvert convert = new LatLonConvert(
    			Double.parseDouble(tvDegreeLat.getText().toString()),
    			Double.parseDouble(tvMinuteLat.getText().toString()),
    			Double.parseDouble(tvSecondLat.getText().toString())
    			);
    	
    	
    	//StringBuffer sb = new StringBuffer(); 
    	
    	tvDecimalLat.setText(""+new DecimalFormat("#.#####").format(convert.getDecimal()));
    	
    	convert = new LatLonConvert(
    			Double.parseDouble(tvDegreeLon.getText().toString()),
    			Double.parseDouble(tvMinuteLon.getText().toString()),
    			Double.parseDouble(tvSecondLon.getText().toString())
    			);
    	tvDecimalLon.setText(""+ new DecimalFormat("#.#####").format(convert.getDecimal()));
    	
    	
    	
    	
    }
    
    
}
