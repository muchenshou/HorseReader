package com.reader.app;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;

public class NetUi {
	WebView _webview;
	FragmentActivity mfa;
	public NetUi(FragmentActivity fa) {
		mfa = fa;
	}
	public View create() {
		_webview = new WebView(mfa);
		return _webview;
	}
}
