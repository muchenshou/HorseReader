package com.reader.preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.reader.app.R;
import com.reader.app.ReadingActivity;

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
