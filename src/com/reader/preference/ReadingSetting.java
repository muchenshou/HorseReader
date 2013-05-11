package com.reader.preference;

import com.reader.app.ReadingActivity;
import com.reader.app.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ReadingSetting extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.reading_preference);
	}

	@Override
	protected void onDestroy() {
		setResult(ReadingActivity.TURN_SETTING);
		super.onDestroy();
	}

}
