package net.mypapit.mobile.myposition;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;

public class SettingsActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.preference);
        
        PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preference, false);
        
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Log.d("net.mypapit.sharedpreference",sp.getString("updateFreq", "3"));
        
        
        
    }

   
}
