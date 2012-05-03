package com.Reader.Main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;



import com.Reader.Main.R;
import com.Reader.Record.RecordHistory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HorseReaderActivity extends Activity {
	/** Called when the activity is first created. */
	public static final int FILE_RESULT_CODE = 1;
	public TextView textView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button butexit = (Button) findViewById(R.id.exit);
		butexit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ListView listview = (ListView) findViewById(R.id.selectFile);
		if (listview == null) {
			Log.d("song", "is null");
			finish();
		}
		listview.setOnItemClickListener(new ListView.OnItemClickListener() {
		
			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long id) {
				if (((String) adapterview.getAdapter().getItem(position))
						.equals("selectfile")) {
					Intent intent = new Intent(HorseReaderActivity.this,
							FileManager.class);
					startActivityForResult(intent,
							HorseReaderActivity.FILE_RESULT_CODE);
				} else {
					// openbook
					String bookName = adapterview.getAdapter()
							.getItem(position).toString();
					RecordHistory recordHistory = new RecordHistory();
					// if (recordHistory.isHaveRecord(bookName)){
					//	
					// }
					Intent intent = new Intent(HorseReaderActivity.this,
							ReadingActivity.class);
					intent.putExtra("bookname", bookName);
					intent.putExtra("position",
							recordHistory.getPosition(recordHistory
									.getRecordIndex(bookName)));
					recordHistory.setRecordFirst(recordHistory
							.getRecordIndex(bookName));
					recordHistory.writeFile();
					startActivity(intent);

				}
			}

		});
		

	}
	@Override
	protected void onStart (){
		ListView listview = (ListView) findViewById(R.id.selectFile);
		RecordHistory history = new RecordHistory();
		BookAdapter adapter = new BookAdapter(this, new File(history
				.getFileName()));
		listview.setAdapter(adapter);
		super.onStart();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (FILE_RESULT_CODE == requestCode) {

			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				Log.d("result", "limian");

				Intent intent = new Intent(HorseReaderActivity.this,
						ReadingActivity.class);
				intent.putExtra("bookname", bundle.getString("umdfile"));
				intent.putExtra("position", "" + 0);
				RecordHistory recordhistory = new RecordHistory();
				recordhistory.addFirst(bundle.getString("umdfile"), "" + 0);
				startActivity(intent);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}

class BookAdapter extends BaseAdapter {

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
	}

	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private LayoutInflater mInflater;
	private Context ActivityContext;

	public BookAdapter(Context context, File Data) {
		ActivityContext = context;
		this.mInflater = LayoutInflater.from(context);
		getData(getRecord(Data));
	}

	private List<String> getRecord(File history) {
		List<String> list = new Vector<String>();
		try {
			//

			history.createNewFile();
			InputStream in = new BufferedInputStream(new FileInputStream(
					history));

			BufferedReader bufin = new BufferedReader(new InputStreamReader(in,
					"utf-8"));
			String strHistoryItem = null;
			while ((strHistoryItem = bufin.readLine()) != null) {
				list.add(strHistoryItem);

				if (ActivityContext.getClass() == HorseReaderActivity.class) {
					bufin.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<Map<String, Object>> getData(List<String> list) {
		mData.clear();
		Map<String, Object> booktitle = null;

		for (int i = 0; i < list.size() && i < 5; i++) {
			String str = list.get(i).toString().substring(
					list.get(i).toString().lastIndexOf('.') + 1).toLowerCase();
			if (str.equals("umd")) {
				booktitle = new HashMap<String, Object>();
				booktitle.put("file", list.get(i));
				booktitle.put("img", R.drawable.umd);
				booktitle.put("title", new File(list.get(i)).getName());
			}
			if (str.equals("txt")) {
				booktitle = new HashMap<String, Object>();
				booktitle.put("file", list.get(i));
				booktitle.put("img", R.drawable.txt);
				booktitle.put("title", new File(list.get(i)).getName());
			}
			mData.add(booktitle);
		}
		if (ActivityContext.getClass() == FileManager.class) {
			return this.mData;
		}
		booktitle = new HashMap<String, Object>();
		booktitle.put("file", "selectfile");
		booktitle.put("img", R.drawable.txt);
		booktitle.put("title", "Ñ¡ÔñÎÄ¼þ");
		mData.add(booktitle);
		return this.mData;
	}


	public int getCount() {
		return mData.size();
	}

	public Object getItem(int position) {

		return mData.get(position).get("file");
	}

	public long getItemId(int position) {

		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		holder.img.setBackgroundResource((Integer) mData.get(position).get(
				"img"));
		holder.title.setText((String) mData.get(position).get("title"));
		return convertView;
	}

}
