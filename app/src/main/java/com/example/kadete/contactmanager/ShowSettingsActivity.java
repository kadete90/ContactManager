package com.example.kadete.contactmanager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Kadete on 08/10/2014.
 */
public class ShowSettingsActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_settings_layout);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        StringBuilder builder = new StringBuilder();

        builder.append("\n" + sharedPrefs.getBoolean("perform_updates", false));
        builder.append("\n" + sharedPrefs.getString("updates_interval", "-1"));
        builder.append("\n" + sharedPrefs.getString("welcome_message", "NULL"));


        Set<String> selections = sharedPrefs.getStringSet("thoth_classes_selected", null);
        builder.append("\n" + selections);

        Toast.makeText(getBaseContext(), selections.iterator().next(), Toast.LENGTH_LONG).show();

        TextView settingsTextView = (TextView) findViewById(R.id.settings_text_view);
        settingsTextView.setText(builder.toString());

    }
}
