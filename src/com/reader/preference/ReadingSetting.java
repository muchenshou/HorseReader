package com.reader.preference;

import com.reader.main.R;
import com.reader.main.ReadingActivity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ReadingSetting extends PreferenceActivity{

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
