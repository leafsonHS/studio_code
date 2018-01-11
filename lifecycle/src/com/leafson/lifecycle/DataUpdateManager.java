package com.leafson.lifecycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.leafson.lifecycle.bean.User;

/**
 *@author coolszy
 *@date 2012-4-26
 *@blog http://blog.92coding.com
 */

public class DataUpdateManager
{
	/* 下载�?*/
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数�?*/
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;
	/* 更新进度�?*/
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位�?
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public DataUpdateManager(Context context)
	{
		this.mContext = context;
	}

	/**
	 * �?��软件更新
	 */
	public void checkUpdate()
	{
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.dara_updating);
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		isUpdate();

	}

	/**
	 * �?��软件是否有更新版�?
	 * 
	 * @return
	 */
	private boolean isUpdate()
	{
		
		 File localFile = mContext.getDatabasePath("ibus_20170725.db");
      	long dl= localFile.lastModified();
         Date d1 = new Date();
      	 long dc=d1.getTime();
      	long diff= dc-dl;
      	 
          long days = diff / (1000 * 60 * 60 * 24);   
      
    	  if (days<0)
	      {
    		 return false;
    	  }
		// 获取当前软件版本
		int versionCode = getVersionCode(mContext);
		String path = "http://gjgx.hualay.com/lifecycle/version.xml";
		URL url;
	
		InputStream  inStreamx = null;
		try {
			url = new URL(path);
	
			HttpURLConnection conn = (HttpURLConnection) url
				.openConnection();
			conn.setReadTimeout(30 * 1000);
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			inStreamx = conn
				.getInputStream();
		
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 解析XML文件�?由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try
		{
			mHashMap = service.parseXml(inStreamx);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		downloadApk();
		return false;
	}

	/**
	 * 获取软件版本�?
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo("com.leafson.lifecycle", 0).versionCode;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}

	
	private void ToMainPage(User user, boolean logined) {
		Intent intent = new Intent();
		try {

			intent.setClass(mContext, HomeActivity.class);
			mContext.startActivity(intent);
			Activity activity = (Activity) mContext;
			 activity.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软�?
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 *@date 2012-4-26
	 *@blog http://blog.92coding.com
	 */
	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("daraurl"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setUseCaches(false);
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入�?
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024000];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位�?
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} 
					while (
							!cancelUpdate
							);// 点击取消就停止下�?
					fos.close();
					is.close();
					
					copyDB(mContext,apkFile);
					
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显�?
			mDownloadDialog.dismiss();
		}
	};

	public static void copyDB(Context paramContext,File apkFile)
	  {
	    try
	    {
	      File localFile = paramContext.getDatabasePath("ibus_20170725.db");
	  	
    		 if (localFile.exists())
    	      {localFile.delete();}  
    	
	    	
	    	  InputStream localInputStream = new FileInputStream(apkFile) ;
	    	   Log.d("note", "bus db not exist,begin to copy....");
	    	  String[] names= localFile.getAbsolutePath().split("ibus_20170725");
	    	
	    	  File cacheFileDir = new File(names[0]);
	    	  if(!cacheFileDir.exists()){
	      	    cacheFileDir.mkdirs();//鐢熸垚鏂囦欢
	      	  }
	    	  
	    	  File cacheFile = new File(cacheFileDir,"ibus_20170725"+names[1]);
	    	  if(!cacheFile.exists()){
	    	    cacheFile.createNewFile();//鐢熸垚鏂囦欢
	    	  }
	    	      FileOutputStream localFileOutputStream = new FileOutputStream(localFile.getAbsolutePath());
	    	      byte[] arrayOfByte = new byte[1024];
	    	      while(localInputStream.read(arrayOfByte)!=-1){
	    	    	   localFileOutputStream.write(arrayOfByte);  
	    	      }
	    	      localFileOutputStream.flush();
	  	        localFileOutputStream.close();
	  	        localInputStream.close();
	    }
	    catch (IOException localIOException)
	    {
	      localIOException.printStackTrace();
	    }
	    
	  }
	
	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists())
		{
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
