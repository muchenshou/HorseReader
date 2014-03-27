package com.reader.app;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reader.animation.AnimationView;
import com.reader.animation.IAnimation;

public class AllUi extends ViewGroup implements IAnimation{
	private View _MainUiView;
	private View _NetUiView;
	private AnimationView _animationView;
	private FragmentActivity mfa;
	private MainUi _MainUi;
	public AllUi(FragmentActivity fa) {
		super(fa);
		mfa = fa;
		_MainUi = new MainUi(mfa);
		_MainUiView = _MainUi.create(R.layout.mainui);
		_NetUiView = new NetUi(mfa).create();
		_NetUiView.setVisibility(View.GONE);
		
//		addView(_NetUiView);
		_MainUiView.setVisibility(View.VISIBLE);
		addView(_MainUiView);
//		_animationView = new SimpleAnimationView(mfa, this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		_MainUiView.layout(l, t, r, b);
//		_NetUiView.layout(l, t, r, b);
//		_animationView.layout(l, t, r, b);
	}

	@Override
	public int startAnimation(DIR flags) {
		return 0;
	}

	@Override
	public int endAnimation(DIR flags) {
		return 0;
	}
	
}
