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
 * ConverterActivity.java
 * Tool for converting between Decimal GPS coordinate and DD MM SS coordinate
 * My GPS Location Tool
 */
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.openlocationcode.OpenLocationCode;

public class ConverterActivity extends Activity  {

    EditText tvDecimalLat, tvDecimalLon, tvDegreeLat,tvMinuteLat, tvSecondLat,tvDegreeLon,tvMinuteLon,tvSecondLon,tvOLC,tvOLCLength;

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

        tvOLC = (EditText) findViewById(R.id.OLCInput);
        tvOLCLength = (EditText) findViewById(R.id.OLCLengthInput);

        String coordinates = (String) getIntent().getSerializableExtra("Coordinate");
        StringTokenizer token = new StringTokenizer(coordinates, ",");

        String tlat,tlon;

        tlat = token.nextToken();
        tlon = token.nextToken();

        new DecimalFormat("#.#####").format(Double.parseDouble(tlon));

        tvDecimalLat.setText( new DecimalFormat("#.#####").format(Double.parseDouble(tlat)));
        tvDecimalLon.setText(new DecimalFormat("#.#####").format(Double.parseDouble(tlon)));
        tvOLCLength.setText("10");

        this.fromDecimal();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_converter, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_fromDegree:
                this.fromDegree();
                break;

            case R.id.menu_fromDecimal:
                this.fromDecimal();
                break;

            case R.id.menu_fromOLC:
                this.fromOLC();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setOLC(double lat, double lon){
        final int defaultLength = 10;
        int length;
        try {
            length = Integer.parseInt(tvOLCLength.getText().toString());
        } catch (NumberFormatException e) {
            length = defaultLength;
        }

        OpenLocationCode OLC;
        try {
            OLC = new OpenLocationCode(lat, lon, length);
        } catch (IllegalArgumentException e) {
            OLC = new OpenLocationCode(lat, lon, defaultLength);
        }
        tvOLC.setText(OLC.getCode());
    }

    private void setDegree(double lat, double lon){
        LatLonConvert convert = new LatLonConvert(lat);

        tvDegreeLat.setText(""+new DecimalFormat("#").format(convert.getDegree()));
        tvMinuteLat.setText(""+new DecimalFormat("#").format(convert.getMinute()));
        tvSecondLat.setText(""+new DecimalFormat("#.##").format(convert.getSecond()));

        convert = new LatLonConvert(lon);
        tvDegreeLon.setText(""+new DecimalFormat("#").format(convert.getDegree()));
        tvMinuteLon.setText(""+new DecimalFormat("#").format(convert.getMinute()));
        tvSecondLon.setText(""+new DecimalFormat("#.##").format(convert.getSecond()));
    }

    private void setDecimal(double lat, double lon){
        tvDecimalLat.setText(""+new DecimalFormat("#.#####").format(lat));
        tvDecimalLon.setText(""+ new DecimalFormat("#.#####").format(lon));
    }

    public void fromDecimal(){
        try {
            double lat = Double.parseDouble(tvDecimalLat.getText().toString());
            double lon = Double.parseDouble(tvDecimalLon.getText().toString());

            setDegree(lat, lon);
            setOLC(lat, lon);
        } catch (NumberFormatException nfe){
            Toast.makeText(getApplicationContext(), "Invalid value", Toast.LENGTH_LONG).show();
        }
    }

    public void fromDegree()
    {
        try {
            double lat = new LatLonConvert(
                    Double.parseDouble(tvDegreeLat.getText().toString()),
                    Double.parseDouble(tvMinuteLat.getText().toString()),
                    Double.parseDouble(tvSecondLat.getText().toString())
                    ).getDecimal();

            double lon = new LatLonConvert(
                    Double.parseDouble(tvDegreeLon.getText().toString()),
                    Double.parseDouble(tvMinuteLon.getText().toString()),
                    Double.parseDouble(tvSecondLon.getText().toString())
                    ).getDecimal();

            setDecimal(lat, lon);
            setOLC(lat, lon);
        } catch (NumberFormatException nfe){
            Toast.makeText(getApplicationContext(), "Invalid value", Toast.LENGTH_LONG).show();
        }
    }

    public void fromOLC()
    {
        try {
            OpenLocationCode OLC = new OpenLocationCode(tvOLC.getText().toString());
            OpenLocationCode.CodeArea CA = OLC.decode();
            double lat = CA.getCenterLatitude();
            double lon = CA.getCenterLongitude();

            setDecimal(lat, lon);
            setDegree(lat, lon);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "Short codes are not supported\n"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
