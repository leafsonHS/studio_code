package com.leafson.lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectStationActivity extends Activity {
	private ListView list;
	private ImageView back;
	
	private String stn;
	private int stno;
	private int updown;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_selectstation);
			list = (ListView) findViewById(R.id.ListView01);
			
			back = (ImageView) findViewById(R.id.titlebarss_back_id);
			back.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					selectStation(stn,stno);
					
				}
			});
			
			
			
			Bundle extras = getIntent().getExtras();
			if(extras!=null) {
				stn= extras.getString("sn");
				stno  = extras.getInt("sno");
				updown  = extras.getInt("updown");
			}
			List<Map<String, Object>> datas = updown==1?HomeActivity.upLineList:HomeActivity.downLineList;
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					SelectStationActivity.this, datas,// 锟斤拷锟皆�
					R.layout.list_station_items,// ListItem锟斤拷XML实锟斤拷
					// 锟斤拷态锟斤拷锟斤拷锟斤拷ImageItem锟斤拷应锟斤拷锟斤拷锟斤拷

					new String[] { "sn", "sno" },
					// ImageItem锟斤拷XML锟侥硷拷锟斤拷锟斤拷锟揭伙拷锟絀mageView,锟斤拷锟斤拷TextView ID
					new int[] { R.id.Itemname, R.id.Itemno });
			// 锟斤拷硬锟斤拷锟斤拷锟绞�
			list.setAdapter(listItemAdapter);

			list.setOnItemClickListener(new OnItemClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ListView listView = (ListView) parent;
					HashMap<String, String> map = (HashMap<String, String>) listView
							.getItemAtPosition(position);
					String sno = map.get("sno");
					String sn = map.get("sn");
					selectStation(sn, Integer.parseInt(sno));
				}

				
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		

	}
	
	private void selectStation(String sn, int sno) {
		try {
//			Intent intent = new Intent();
//			if(sno!=null){
//				intent.putExtra("sno", sno);
//				intent.putExtra("sn", sn);
//			}
//			intent.setClass(SelectStationActivity.this,
//					HomeActivity.class);
//			startActivity(intent);
			
			Intent intent = new Intent();
			intent.putExtra("sno", sno);
			intent.putExtra("sn", sn);
			this.setResult(Activity.RESULT_OK, intent);
			this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			selectStation(stn,stno);
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


}
