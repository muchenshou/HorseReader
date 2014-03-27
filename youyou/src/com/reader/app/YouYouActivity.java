package com.reader.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class YouYouActivity extends FragmentActivity {
	AllUi _allui;
	MainUi _MainUi;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
//		_allui = new AllUi(this);
		_MainUi = new MainUi(this);
		setContentView(_MainUi.create(R.layout.mainui));
	}
	
}