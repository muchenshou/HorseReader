package com.Reader.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.Reader.Book.BookView.BookView;
import com.Reader.Book.Manager.BookManager;
import com.Reader.Command.Command;
import com.Reader.Command.CommandExit;
import com.Reader.Command.CommandNextChapter;
import com.Reader.Command.CommandPreChapter;
import com.Reader.Command.CommandReturn;
import com.Reader.Main.HorseReaderActivity;
import com.Reader.Main.R;
import com.Reader.Record.RecordHistory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ReadingActivity extends Activity {
	public BookView bookView;
	public BookManager bookmanager;
	private GridView mGrid;
	private PopupWindow popup;
	private LayoutInflater layoutInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		String bookName = getIntent().getStringExtra("bookname");
		String position = getIntent().getStringExtra("position");
		try {
			bookmanager = new BookManager(ReadingActivity.this, new File(
					bookName));
			bookmanager.openBook();
			bookView = bookmanager.getBookView();
			//bookView.getTextUtil().setLocal(Integer.parseInt(position));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setLookingBookView();
	}

	@Override
	protected void onStop() {
		Log.d("Onstop", "ok?");
		RecordHistory recordhistory = new RecordHistory();
		recordhistory.setFirstRecordPosition(bookmanager.getReadingPosition());
		recordhistory.writeFile();
		super.onStop();
	}

	public void setLookingBookView() {
		setContentView(bookmanager.getBookView());
	}

	public void setBookSet() {

		Context context = ReadingActivity.this;
		layoutInflater = LayoutInflater.from(context);
		View popView = layoutInflater.inflate(R.layout.gridpage, null, false);
		if (popup == null) {
			popup = new PopupWindow(popView, bookView.getWidth()-20, bookView
					.getHeight()-20, true);
		}
		mGrid = (GridView) popView.findViewById(R.id.grid);
		if (popup == null) {
			Log.d("popup", "is null");
		} else {
			Log.d("popup", "is not null");
		}
		mGrid.setAdapter(new CommandAdapter());
		mGrid.setOnItemClickListener(new GridView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Command command = (Command) arg0.getAdapter().getItem(position);
				if (command != null)
					command.excute();
			}
		});
		// popup.setContentView(popView);
		popup.showAtLocation(bookmanager.getBookView(), Gravity.CENTER, 0, 0);

	}

	public class CommandAdapter extends BaseAdapter {
		class CommandItem {
			public int _com;
			public String _str;

			CommandItem(int com, String str) {
				_com = com;
				_str = str;
			}
		}

		List<CommandItem> operator = new ArrayList<CommandItem>();

		public CommandAdapter() {
			operator.add(new CommandItem(Command.PRECHAPTER, "上一章"));
			operator.add(new CommandItem(Command.NEXTCHAPTER, "下一章"));
			operator.add(new CommandItem(Command.JUMP, "跳转"));
			operator.add(new CommandItem(Command.RETURN, "返回"));
			operator.add(new CommandItem(Command.EXIT, "退出"));
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView i;

			if (convertView == null) {
				i = new TextView(ReadingActivity.this);
				i.setText(operator.get(position)._str);
				i.setGravity(Gravity.CENTER);
				i.setLayoutParams(new GridView.LayoutParams(50, 50));
			} else {
				i = (TextView) convertView;
			}
			return i;
		}

		public final int getCount() {
			return operator.size();
		}

		public final Object getItem(int position) {
			switch (operator.get(position)._com) {
			case Command.EXIT:
				return new CommandExit(ReadingActivity.this,popup);
			case Command.RETURN:
				return new CommandReturn(popup);
			case Command.NEXTCHAPTER:
				return new CommandNextChapter(ReadingActivity.this);
			case Command.PRECHAPTER:
				return new CommandPreChapter(ReadingActivity.this);
			default:
				break;
			}
			return null;
		}

		public final long getItemId(int position) {
			return position;
		}
	}
}
