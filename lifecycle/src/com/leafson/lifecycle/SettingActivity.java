package com.leafson.lifecycle;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.leafson.lifecycle.bean.Setting;
import com.leafson.lifecycle.bean.User;
import com.leafson.lifecycle.db.DAO;
import com.leafson.lifecycle.db.DAOImpl;

public class SettingActivity extends Activity {
	public static final String NOTICE2MIN="1";
	public static final String NONOTICE2MIN="0";
	
	public static final String REFREASHFAST="1";
	public static final String REFREASHNOMAL="0";
	private ImageView back;
	
	private Button bt2chat;
	private Handler handler = null;
	private ImageView notice2TitleCheckBox;
	private ImageView 	refreashIntervalEdit;
	
	private  Setting setting = new Setting();
	private DAO settingDao = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting1);
		handler = new Handler();


	
		
		
		
		
		back = (ImageView) findViewById(R.id.titlebarsettinge_back_id);
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				guit();
			}
		});
		
		notice2TitleCheckBox = (ImageView) findViewById(R.id.notice2TitleCheckBox);
		notice2TitleCheckBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ImageView checkBox = (ImageView) v;
				
				List<Setting> list = settingDao.findAll(null);
				if (!list.isEmpty()) {
					setting=list.get(0);
				}
				if(null==setting.getNoticeType()||NONOTICE2MIN.equals(setting.getNoticeType()))
				{
					checkBox.setImageResource(R.drawable.checkbox_on);
					setting.setNoticeType(NOTICE2MIN);
					HomeActivity.setting.setNoticeType(NOTICE2MIN);
				}else
				{
					checkBox.setImageResource(R.drawable.checkbox_off);
					setting.setNoticeType(NONOTICE2MIN);
					HomeActivity.setting.setNoticeType(NONOTICE2MIN);
				}
				
			
				if (setting.getId()!=null) {
					settingDao.update(setting);
					
				}else
				{
					settingDao.insert(setting);
				}
			}
		});
		
		
		
		refreashIntervalEdit = (ImageView) findViewById(R.id.refreashIntervalEdit);
		refreashIntervalEdit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ImageView checkBox = (ImageView) v;
				List<Setting> list = settingDao.findAll(null);
				if (!list.isEmpty()) {
					setting=list.get(0);
				}
				if(null==setting.getNoticeType()||REFREASHNOMAL.equals(setting.getRefreashInterval()))
				{
					checkBox.setImageResource(R.drawable.checkbox_on);
					setting.setRefreashInterval(REFREASHFAST);
					HomeActivity.intervel=15;
				}else
				{
					checkBox.setImageResource(R.drawable.checkbox_off);
					setting.setRefreashInterval(REFREASHNOMAL);
					HomeActivity.intervel=25;
				}
				
				if (setting.getId()!=null) {
					settingDao.update(setting);
					
				}else
				{
					settingDao.insert(setting);
				}
			}
		});

		init();
	}
	private void guit() {
		try {
			this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void init() {

		settingDao = new DAOImpl<User>(getApplicationContext(),
				Setting.class);
	
		boolean isTableExist=settingDao.tabbleIsExist("TABLE_SETTING");
		if(!isTableExist)
		{
			
			settingDao.createTable(
					"CREATE TABLE TABLE_SETTING(" + //
							"setting_id  integer primary key autoincrement, " + //
							"setting_noticeType varchar(50), " + //
							"setting_defaultBusLine varchar(50), " + //
							"setting_refreashInterval VARCHAR(50))"
					);
		}
		// 锟斤拷锟斤拷欠锟斤拷陆
		List<Setting> list = settingDao.findAll(null);
		if (!list.isEmpty()) {
			setting = list.get(0);
			Log.d("DEBUG",
					"锟斤拷陆 锟矫伙拷锟斤拷+" + setting.getNoticeType() + " 锟斤拷锟斤拷"
							+ setting.getRefreashInterval() + "默锟斤拷站:"
							+ setting.getDefaultBusLine() + "选锟斤拷陆 锟劫斤拷锟斤拷锟斤拷页");
		}
		if(null!=setting.getNoticeType()&&NOTICE2MIN.equals(setting.getNoticeType()))
		{
			notice2TitleCheckBox.setImageResource(R.drawable.checkbox_on);
		}else
		{
			notice2TitleCheckBox.setImageResource(R.drawable.checkbox_off);
		}
		
		if(null!=setting.getRefreashInterval()&&REFREASHFAST.equals(setting.getRefreashInterval()))
		{
			refreashIntervalEdit.setImageResource(R.drawable.checkbox_on);
		}else
		{
			refreashIntervalEdit.setImageResource(R.drawable.checkbox_off);
		}
		
		
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
