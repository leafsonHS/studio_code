package com.leafson.lifecycle;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.leafson.lifecycle.bean.LineObj;
import com.leafson.lifecycle.bean.Setting;
import com.leafson.lifecycle.db.DAO;
import com.leafson.lifecycle.nanjing.bean.BusBaseBean;
import com.leafson.lifecycle.nanjing.bean.BusStationBean;
import com.leafson.lifecycle.nanjing.http.pro.BusLineStationsUpdateReq;
import com.leafson.lifecycle.nanjing.http.pro.BusLineStationsUpdateRes;
import com.leafson.lifecycle.nanjing.http.pro.BusLinesUpdateReq;
import com.leafson.lifecycle.nanjing.http.pro.BusLinesUpdateRes;
import com.leafson.lifecycle.nanjing.http.pro.BusStationsUpdateReq;
import com.leafson.lifecycle.nanjing.http.pro.BusStationsUpdateRes;
import com.leafson.lifecycle.nanjing.http.pro.BusUpdownsUpdateReq;
import com.leafson.lifecycle.nanjing.http.pro.BusUpdownsUpdateRes;
import com.leafson.lifecycle.nanjing.http.pro.GetLineReq;
import com.leafson.lifecycle.nanjing.http.pro.GetLineRes;
import com.leafson.lifecycle.nanjing.util.DBUtil;
import com.leafson.lifecycle.utils.LogInfo;
import com.leafson.lifecycle.utils.SpecialAdapter;
import com.leafson.lifecycle.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
public class HomeActivity extends Activity  {
	public static TravelInfoLoader loadThread = null;
	public static TravelInfoLoaderMenu loadThreadmenu = null;
	
	public static java.util.Timer timer;
    private ProgressDialog progressDialog = null;
	private TextToSpeech mSpeech;
	private int isUpline = 1;
	private ListView list;
	private boolean isArrived = false;
	private ImageView refreashIcon;
	private TextView waitingStationText = null;
	private TextView derectionText = null;
	private TextView textviewx = null;
	public static  String logtext = "tt";
	public static  int updatedinited = 0;
	public static  int load = 0;
	
	
    HashMap<String, String> cookieMap = null;  
	
	private static String cookieString = null;
	private TextView noDataText = null;
	private LinearLayout derectionChangeLinearLayout;
	private LinearLayout waitingStationLinearLayout;
	private LinearLayout lineSelectorLinearLayout;
	private Button refmenu;
	private Button refauto;
	public  static int count = 0;
	
	private WebView common_webview ;
	private TextView lineSelectorText = null;
	private TextView  astxt = null;
	
	private ImageView back;
	private Handler handler = null;
	public static List<Map<String, Object>> datas;
	public static Map<String, Map<String, Object>> staticStations;
	public static List<Map<String, Object>> upLineList = new ArrayList<Map<String, Object>>();
	public static List<Map<String, Object>> downLineList = new ArrayList<Map<String, Object>>();
	
	
	public static  HashMap<String, String> confighashMap ;
	public static String upLine = "请选择线路";
	public static String downLine = null;
	private int currentStation = 1;
	public static String currentStationName = "请选择";
	public static String currentSelectedLine="请选择线路";
	public static String lineNumbber = "";
	public static String preLine = "";
	private DAO LineObjDao = null;
	public static  Setting setting = new Setting();
	public static int intervel=25;
	protected static String lineName="";
	public jiangningInitLoader loadjiangningInitLoaderThread = null;
	  long lineStationsUpdateTime = 0L;
	  long linesUpdateTime = 0L;
	private AdView mAdView;
	  private int page_index_line = 1;
	  private int page_index_linestation = 1;
	  private int page_index_station = 1;
	  private int page_index_updown = 1;
	  private int page_size = 100;
	  long stationsUpdateTime = 0L;
	  long updownsUpdateTime = 0L;
	static Toast  yoasty=null;
	private  static int showNextCount=1;
	public static String fanxiangid=null;
	HttpClient httpclient = new DefaultHttpClient();
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		// 创建属于主线程的handler
		handler = new Handler();
		LineObjDao=MainActivity.LineObjDao;
		setting = MainActivity.setting;
		if(null!=setting.getRefreashInterval())
		{
			if(SettingActivity.REFREASHFAST.equals(setting.getRefreashInterval()))
			{
				intervel=10;	
			}else
			{
				intervel=15;
			}
		}
		list = (ListView) findViewById(R.id.ListView01);
		
		back = (ImageView) findViewById(R.id.titlebar_back_id);
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog();
			}
		});
		
		astxt = (TextView ) findViewById(R.id.astxt);
		astxt.setText(confighashMap.get("adtxt")==null?"更新全部站点数据":confighashMap.get("adtxt"));
		astxt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				loadjiangningInitLoaderThread = new jiangningInitLoader();
				loadjiangningInitLoaderThread.start();
			}

			
		});
		
		
		refauto = (Button) findViewById(R.id.refauto);
		refauto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				loadingTravelInfo();
			}
		});
		refmenu = (Button) findViewById(R.id.refmenu);
		refmenu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				loadingTravelInfoMenu();
			}

			
		});
		refreashIcon = (ImageView) findViewById(R.id.icon_refreash);
		refreashIcon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toSetting();
			}
		});
		// 等到站点设置
		waitingStationLinearLayout = (LinearLayout) this
				.findViewById(R.id.waitingStation);
		waitingStationLinearLayout
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						selectStation();
					}
				});
		lineSelectorLinearLayout = (LinearLayout) this
				.findViewById(R.id.lineSelector);
		lineSelectorLinearLayout
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						selectLine();
					}
				});
		
		// 方向设置交互
		derectionChangeLinearLayout = (LinearLayout) this
				.findViewById(R.id.derectionChange);
		derectionChangeLinearLayout
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						
						showNextCount=1;
						datas = new ArrayList<Map<String, Object>>();
						handler.post(runnableTravelData);
						if (isUpline == 1) {
							isUpline = 2;
						
							for (Map<String, Object> station : downLineList) {
								if (station.get("sn")
										.equals(currentStationName)) {
									currentStation = Integer
											.parseInt((String) station
													.get("sno"));
								}
							}
							derectionText.setText( downLine );
						} else {
							isUpline = 1;
							for (Map<String, Object> station : upLineList) {
								if (station.get("sn")
										.equals(currentStationName)) {
									currentStation = Integer
											.parseInt((String) station
													.get("sno"));
								}
							}
							derectionText.setText(upLine);
						}
						loadingTravelInfo();
					}
				});
		
		textviewx = (TextView) this
				.findViewById(R.id.textviewx);
		
		waitingStationText = (TextView) this
				.findViewById(R.id.waitingStationText);
		
		waitingStationText.setText(currentStationName);

		derectionText = (TextView) this.findViewById(R.id.derectionText);
		derectionText.setText(upLine);
		
		lineSelectorText = (TextView) this.findViewById(R.id.lineSelectorText);
		lineSelectorText.setText(currentSelectedLine);
		
		noDataText = (TextView) this.findViewById(R.id.noDataText);

		 if (!isNetworkAvailable(HomeActivity.this))
        {
			    Toast.makeText(getApplicationContext(), "当前没有可用网络！", Toast.LENGTH_LONG).show();
	            return;
        }
	
			ImageView spaceshipImage = (ImageView)findViewById(R.id.icon_refreash);
			Animation hyperspaceJumpAnimation=AnimationUtils.loadAnimation(HomeActivity.this, R.anim.settingnotice);
			spaceshipImage.startAnimation(hyperspaceJumpAnimation);

		//加载广告
		// 横幅广告
		MobileAds.initialize(this, "ca-app-pub-0966333701823765~1003341168");

		 mAdView = findViewById(R.id.ad_view);

		AdRequest adRequest = new AdRequest.Builder()
				//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.build();
		mAdView.loadAd(adRequest);

		if("请选择线路".equals(currentSelectedLine))
		{
			selectLine();
			return;
		}
		
		loadingTravelInfo();


	}

	/** Called when leaving the activity */
	@Override
	public void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();
	}

	/** Called when returning to the activity */
	@Override
	public void onResume() {
		super.onResume();
		if (mAdView != null) {
			mAdView.resume();
		}
	}

	/** Called before the activity is destroyed */
	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}
	 public boolean isNetworkAvailable(Activity activity)
	    {
	        Context context = activity.getApplicationContext();
	        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
	        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        
	        if (connectivityManager == null)
	        {
	            return false;
	        }
	        else
	        {
	            // 获取NetworkInfo对象
	            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
	            
	            if (networkInfo != null && networkInfo.length > 0)
	            {
	                for (int i = 0; i < networkInfo.length; i++)
	                {
	                    System.out.println(i + "===状态===" + networkInfo[i].getState());
	                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
	                    // 判断当前网络状态是否为连接状态
	                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
	                    {
	                        return true;
	                    }
	                }
	            }
	        }
	        return false;
	    }
	
	 private void refreash() {
			// TODO Auto-generated method stub
			
		}
	private void selectStation() {
		try {
			Intent intent = new Intent();
			intent.putExtra("sno", currentStation);
			intent.putExtra("sn", currentStationName);
			intent.putExtra("updown", isUpline);
			intent.setClass(HomeActivity.this, SelectStationActivity.class);
			startActivityForResult(intent, 100001);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void selectLine() {
		try {	
			Intent intent = new Intent();
			intent.putExtra("currentSelectedLine", currentSelectedLine);
			intent.putExtra("lineNumbber", lineNumbber);
			intent.setClass(HomeActivity.this, AllLineActivity.class);
			startActivityForResult(intent, 100002);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void toSetting() {
		try {	
			Intent intent = new Intent();
			//intent.putExtra("currentSelectedLine", currentSelectedLine);
			//intent.putExtra("lineNumbber", lineNumbber);
			intent.setClass(HomeActivity.this, SettingActivity.class);
			startActivityForResult(intent, 100003);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void changemageResource(ImageView savedInstanceState, int resourceId) {
		refreashIcon.setImageResource(resourceId);
	}
	
	
	// 加载数据
	private void loadingTravelInfo() {
		if (loadThread != null) {
			loadThread.x = 1;
		}
		if("".equals(lineNumbber)){return;}
		handler.post(runnableUi);
		loadThread = new TravelInfoLoader();
		loadThread.start();
		
	

	}
	// 加载数据
	private void loadingTravelInfoMenu() {
		if (loadThread != null) {
			loadThread.x = 1;
		}
		if("".equals(lineNumbber)){return;}
		handler.post(runnableUi);
		loadThreadmenu = new TravelInfoLoaderMenu();
		loadThreadmenu.start();
		
	

	}
	
	
	public  synchronized int increas(String info)
	{
		 updatedinited++;
		return updatedinited;
		
		
	}
	
	private void initNanjing() {
		SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean isjiangninginited = DBUtil.isJiangninginited(getApplicationContext());
		if (Math.random()*10>0) {
		 
		    this.linesUpdateTime = DBUtil.getLinesUpdateTime(getApplicationContext());
		    this.updownsUpdateTime = DBUtil.getUpdownsUpdateTime(getApplicationContext());
		    this.stationsUpdateTime = DBUtil.getStationsUpdateTime(getApplicationContext());
		    this.lineStationsUpdateTime = DBUtil.getLineStationsUpdateTime(getApplicationContext());
		 
			// 如果基础数据部存在。 先获取基础数据。
		    int pages=1;
		    while(page_index_line<=pages){
				    BusLinesUpdateReq localBusLinesUpdateReq = new BusLinesUpdateReq();
				    localBusLinesUpdateReq.addParams("line_updated", String.valueOf(linesUpdateTime));
			         localBusLinesUpdateReq.addParams("page",  String.valueOf(this.page_index_line));
			        localBusLinesUpdateReq.addParams("per_page",  String.valueOf(this.page_size));
			    	page_index_line++;
			    	String path =  localBusLinesUpdateReq.getUrl();
					URL url;
					try {
						url = new URL(path);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setReadTimeout(6  * 1000);
						conn.setRequestMethod("GET");
						InputStream inStream = conn.getInputStream();
						String s=Utils .convertStreamToStringNoz(inStream);
						BusLinesUpdateRes   localBusLinesUpdateRes= new BusLinesUpdateRes();
						localBusLinesUpdateRes.init(s);
						pages=localBusLinesUpdateRes.getPages();
						DBUtil.doUpdateLines(getApplicationContext(), localBusLinesUpdateRes.getLines());
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//			// 如果基础数据部存在。 先获取基础数据。
		    int pagesUpdowns=1;
		    while(page_index_updown<=pagesUpdowns){
		    
		    	BusUpdownsUpdateReq localBusUpdownsUpdateReq= new BusUpdownsUpdateReq();
		    	localBusUpdownsUpdateReq.addParams("updown_updated", String.valueOf(updownsUpdateTime));
		    	localBusUpdownsUpdateReq.addParams("page",  String.valueOf(this.page_index_updown));
		    	localBusUpdownsUpdateReq.addParams("per_page",  String.valueOf(this.page_size));
		    	page_index_updown++;
			    	String path =  localBusUpdownsUpdateReq.getUrl();
					URL url;
					try {
						url = new URL(path);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setReadTimeout(6 * 1000);
						conn.setRequestMethod("GET");
						InputStream inStream = conn.getInputStream();
						String s=Utils .convertStreamToStringNoz(inStream);
						BusUpdownsUpdateRes   localBusUpdownsUpdateRes= new BusUpdownsUpdateRes();
						localBusUpdownsUpdateRes.init(s);
						pagesUpdowns=localBusUpdownsUpdateRes.getPages();
						DBUtil.doUpdateUpdowns(getApplicationContext(), localBusUpdownsUpdateRes.getUpdowns());
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		    
			// 如果基础数据部存在。 先获取基础数据。
		    int pagesStations=1;
		    while(page_index_station<=pagesStations){
			    
		    	BusStationsUpdateReq localBusStationsUpdateReq= new BusStationsUpdateReq();
		    	localBusStationsUpdateReq.addParams("st_updated", String.valueOf(stationsUpdateTime));
		    	localBusStationsUpdateReq.addParams("page",  String.valueOf(this.page_index_station));
		    	localBusStationsUpdateReq.addParams("per_page",  String.valueOf(this.page_size));
		    	page_index_station++;
			    	String path =  localBusStationsUpdateReq.getUrl();
					URL url;
					try {
						url = new URL(path);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setReadTimeout(6  * 1000);
						conn.setRequestMethod("GET");
						InputStream inStream = conn.getInputStream();
						String s=Utils .convertStreamToStringNoz(inStream);
						BusStationsUpdateRes   localBusStationsUpdateRes= new BusStationsUpdateRes();
						localBusStationsUpdateRes.init(s);
						pagesStations=localBusStationsUpdateRes.getPages();
						DBUtil.doUpdateStations(getApplicationContext(), localBusStationsUpdateRes.getStations());
						//DBUtil.getAllStations(this);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
//		     
//		 // 如果基础数据部存在。 先获取基础数据。
		    int pageslinestation=1; 
		    while(page_index_linestation<=pageslinestation){
		    	BusLineStationsUpdateReq localBusLineStationsUpdateReq= new BusLineStationsUpdateReq();
		    	localBusLineStationsUpdateReq.addParams("line_st_updated", String.valueOf(lineStationsUpdateTime));
		    	localBusLineStationsUpdateReq.addParams("page",  String.valueOf(this.page_index_linestation));
		    	localBusLineStationsUpdateReq.addParams("per_page",  String.valueOf(this.page_size));
		    	page_index_linestation++;
			    	String path =  localBusLineStationsUpdateReq.getUrl();
					URL url;
					try {
						url = new URL(path);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setReadTimeout(6  * 1000);
						conn.setRequestMethod("GET");
						InputStream inStream = conn.getInputStream();
						String s=Utils .convertStreamToStringNoz(inStream);
						BusLineStationsUpdateRes   localBusLineStationsUpdateRes= new BusLineStationsUpdateRes();
						localBusLineStationsUpdateRes.init(s);
						pageslinestation=localBusLineStationsUpdateRes.getPages();
						DBUtil.doUpdateLineStations(getApplicationContext(), localBusLineStationsUpdateRes.getLineStations());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
		    updateNJLine();
		}
		  
		    
	}
	private void updateNJLine() {
		LineObjDao.exeC("delete  from TABLE_LINEOBJ where source =2;");
		
		LineObjDao.exeC("insert into TABLE_LINEOBJ "+
"select line.[line_name] as lineobj_linename ,line.[line_id]  as lineobj_busLineId,"+
"sa.[st_name]||'-'||sb.[st_name] as lineobj_upLine,sa1.[st_name]||'-'||sb1.[st_name]  as lineobj_downLine,"+
"ud.[bus_start]||'-'||ud.[bus_end] as lineobj_upTime,ud1.[bus_start]||'-'||ud1.[bus_end] as lineobj_downTime,"+
" line.[line_name] as lineobj_linenamePinYin,"+
"2 as source "+
"from ibus_line line "+
"left join ibus_line_updown ud on  line.line_id=ud.line_id  and ud.[updown_type]=1 "+
"left join ibus_station sa on ud.st_start_id = sa.st_id "+
"left join ibus_station sb on ud.st_end_id = sb.st_id "+

" left join ibus_line_updown ud1 on  line.line_id=ud1.line_id  and ud1.[updown_type]=0 "+
"left join ibus_station sa1 on ud1.st_start_id = sa1.st_id "+
"left join ibus_station sb1 on ud1.st_end_id = sb1.st_id "+
"where line.[line_status]=0  ");
		
	}
	
	private void initJiangNing(){
		SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean isjiangninginited = DBUtil.isJiangninginited(getApplicationContext());
		if (!isjiangninginited||Math.random()*10>0) {
			setLoadingText("江宁数据初始化进度0");
		    HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			HttpPost httpgets = new HttpPost(
					"http://223.112.2.69:5902/pubserverT/bus/busDis.do?command=list");		
			 List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
		     nvps.add(new BasicNameValuePair("order", "asc"));  
		     nvps.add(new BasicNameValuePair("page","1"));  
		     nvps.add(new BasicNameValuePair("rows", "300")); 
		     nvps.add(new BasicNameValuePair("sort", "busLineName")); 
		     try {
				httpgets.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			httpgets.setHeader("Accept","application/json, text/javascript, */*; q=0.01"); 
			httpgets.setHeader("Accept-Encoding","gzip, deflate"); 
			httpgets.setHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"); 
			//httpgets.setHeader("Connection","keep-alive"); 
			httpgets.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"); 
			httpgets.setHeader("Cookie","JSESSIONID=F335B69E73A111B97EB1F7619BDCD49F"); 
			httpgets.setHeader("Host","223.112.2.69:5902"); 
			httpgets.setHeader("Referer","http://223.112.2.69:5902/pubserverT/dis2.do"); 
			httpgets.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0"); 
			httpgets.setHeader("X-Requested-With","XMLHttpRequest"); 
			HttpResponse response = null;
			try {
				response = httpclient.execute(httpgets);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			HttpEntity entity = response.getEntity();
			InputStream instreams = null;
			try {
				instreams = entity.getContent();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String uplineString1 =Utils.convertStreamToStringNoz(instreams);
			stationStringToList(uplineString1);
			System.out.println(11111);
			setLoadingText("初始化完成，请重启应用");
			
		}
	}
	
	
	private class jiangningInitLoader extends Thread {
		@Override
		public void run() {
			try {
				Looper.prepare();
				initJiangNing();
				increas("江宁已经就绪");
			//	initNanjing();
				increas("南京市区已经就绪");
			} catch (Exception e) {
				e.printStackTrace();
				dialog();
			}

		}
	};
	
	

	
	/**
	 * 下载文件线程
	 */
	private class TravelInfoLoaderMenu extends Thread {
		public void run() {
			try {
					count=0;	
					handler.post(timmAutoStatic);
							if(AllLineActivity.source.equals("1")){
									// 如果基础数据部存在。 先获取基础数据。
									String path = "http://223.112.2.69:5902/pubserverT/bus/busQuery.do?random=0.2767780260182917&loginCnt="+LogInfo.getLocCode(lineNumbber)+"&command=real&busLineId="+lineNumbber;
									URL url;
									url = new URL(path);
									HttpURLConnection conn = (HttpURLConnection) url
											.openConnection();
									conn.setReadTimeout(30 * 1000);
									conn.setRequestMethod("GET");
									InputStream inStream = conn.getInputStream();
									datas = busLineToList(Utils
											.convertStreamToStringNoz(inStream));
									String dre="0";
									if((showNextCount+1)%2==0){
										
									
										if(isUpline==1)
										{
											dre="0";
										}
										else
										{
											dre="1";	
										}
										
									HttpPost httpgets = new HttpPost(
											"http://223.112.2.69:5902/pubserverT/bus/busQuery.do?command=prt");		
									 List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
									 nvps.add(new BasicNameValuePair("a", "")); 
									 nvps.add(new BasicNameValuePair("busLineId", lineNumbber));  
									 nvps.add(new BasicNameValuePair("endTime", Utils.dateToString(new Date(),"yyyy-MM-dd ")+"23:59:59"));  
									 nvps.add(new BasicNameValuePair("inDown", dre));  
									 nvps.add(new BasicNameValuePair("order", "asc"));  
									 nvps.add(new BasicNameValuePair("page", "1"));  
									 nvps.add(new BasicNameValuePair("rows", "20"));  
									 nvps.add(new BasicNameValuePair("sort", "stime"));
									 nvps.add(new BasicNameValuePair("type", "1"));
									 nvps.add(new BasicNameValuePair("startTime", Utils.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss")));  			
									 httpgets.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));  
									httpgets.setHeader("Accept","application/json, text/javascript, */*; q=0.01"); 
									httpgets.setHeader("Accept-Encoding","gzip, deflate"); 
									httpgets.setHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"); 
									httpgets.setHeader("Content-Type","application/x-www-form-urlencoded"); 
									httpgets.setHeader("Cookie","JSESSIONID=1A12FEBA9FDDFD1776AAC37EDCF71C9D"); 
									httpgets.setHeader("Host","223.112.2.69:5902"); 
									httpgets.setHeader("Referer","http://223.112.2.69:5902/pubserverT/prt/linePrt.do?command=pt"); 
									httpgets.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0"); 
									httpgets.setHeader("X-Requested-With","XMLHttpRequest"); 
									HttpResponse response = httpclient.execute(httpgets);			
									HttpEntity entity = response.getEntity();
									InputStream instreams = entity.getContent();
									String uplineString1 =Utils.convertStreamToStringNoz(instreams);
									JSONObject jsonObjs = new JSONObject(uplineString1);
									JSONArray jsonaRRY=jsonObjs.getJSONArray("rows");
									int  length=jsonaRRY.length();
									preLine="";
									if(length>2)
									{
										length=2;
										for(int inx=0; inx<length;inx++)
										{
											 JSONObject obj =(JSONObject)jsonaRRY.get(inx);
											 String pre=obj.getString("stime");
											 preLine=preLine+" "+pre;
										}
										
									}else if(length<=2&&length>0)
									{
									
										for(int inx=0; inx<length;inx++)
										{
											 JSONObject obj =(JSONObject)jsonaRRY.get(inx);
											 String pre=obj.getString("stime");
											 preLine=preLine+" "+pre;
										}
									
										
									}else{
										preLine="暂无";
									}
									if(preLine!=null&&!preLine.equals("")){
										setLoadingText("发车："+preLine);
									}
								}
								showNextCount++;
							}else if(AllLineActivity.source.equals("3")) 
							{
								
								getCookie();
								setLoadingText("南京实时公交");
								String dre="0";
								if(isUpline==1)
								{
									dre="1";
								}
								else
								{
									dre="0";	
								}
								
								GetLineReq localGetLineReq = new GetLineReq();
							//	localGetLineReq.addParams("line_code", "1270");
								localGetLineReq.addParams("line_id",  lineNumbber);
								localGetLineReq.addParams("updown_type",  String.valueOf(dre));
						    	String path =  localGetLineReq.getUrl();
								URL url;
								try {
									url = new URL(path);
									HttpURLConnection conn = (HttpURLConnection) url
											.openConnection();
									conn.setReadTimeout(10 * 1000);
									conn.setRequestMethod("GET");
									conn.setRequestProperty("Cookie",cookieString); 
									
									InputStream inStream = conn.getInputStream();
									String s=Utils .convertStreamToStringNoz(inStream);
									GetLineRes   localGetLineRes= new GetLineRes();
									localGetLineRes.init(s);
									ArrayList<BusStationBean>	stions=localGetLineRes.getStations();
									datas=busLineToListNj(stions);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else 
							{
								getCookie();
								setLoadingText("南京实时公交");
								String dre="0";
								if(isUpline==1)
								{
									dre="1";
								}
								else
								{
									dre="0";	
								}
								String busId ="";
								if(lineName!=null){
									String path = "http://njbus.zhihui.cc/njgjc/webapp.do?method=term&term="+URLEncoder.encode(lineName);
									URL url;       
									url = new URL(path);
									HttpURLConnection conn = (HttpURLConnection) url
											.openConnection();
									conn.setRequestProperty("Cookie",cookieString); 
									conn.setReadTimeout(30 * 1000);
									conn.setRequestMethod("GET");
									InputStream inStream = conn.getInputStream();
									
	
									 busId = getBusId(Utils
											.convertStreamToStringNoz(inStream));
									
								}
								String[] businfo=busId.split(",");
								String dreData=businfo[0];
								String infoData=businfo[1];
								
								busId=businfo[0].replace("0|", "");
								String[] ids=busId.split("-");
								String ds2="";
								String specialId="";
								boolean isCommon=true;
								
								String fcd=downLine.replace("-", "→").replaceAll(" ", "");
								String fcu=upLine.replace("-", "→").replaceAll(" ", "");
								String fb=infoData.replaceAll(" ", "");
								String[] dreSplit=dreData.split("-");
								if(dreSplit.length<2)
								{
									specialId=dreData.split("\\|")[1];
									isCommon=false;
									
								}else{
									ds2=dreSplit[2];
								}
								
								String curDre="";
								//如果我是1 要求对方也是upline 
								//得到当前我的方向
								if(isUpline==1)
								{
									curDre=fcu;
								}
								else{
									curDre=fcd;
								}
								//如果当前他的方向不是我的方向 给他换个方向
								if(!fb.contains(curDre))
								{
									if(isCommon==false){
										if(fanxiangid!=null){
											specialId=fanxiangid;
										}else
										{
											HomeActivity.currentStationName = (String) downLineList.get(0)
													.get("sn");
											isUpline=2;
											waitingStationText.setText(currentStationName);
											derectionText.setText(downLine);
										}
									}
									if(ds2.equals("1"))
									{
										ds2="0";
									}else
									{
										ds2="1";
									}
								}
								String path ="";
								
								if(isCommon==false)
								{
									path = "http://njbus.zhihui.cc/njgjc/webapp.do?method=standby&lineId="+specialId;
								}else{
									path = "http://njbus.zhihui.cc/njgjc/webapp.do?method=standby&lineId="+ids[0]+"-"+URLEncoder.encode(ids[1])+"-"+ds2;
								}
								URL url;
								url = new URL(path);
								HttpURLConnection conn = (HttpURLConnection) url
										.openConnection();
								conn.setReadTimeout(30 * 1000);
								conn.setRequestMethod("GET");
								InputStream inStream = conn.getInputStream();
								datas = busLineToListNJBase(inStream);
								
							}
								handler.post(runnableTravelData);
							} catch (Exception e) {
								e.printStackTrace();
							}
					}

	
	}
	
	
	/**
	 * 下载文件线程
	 */
	private class TravelInfoLoader extends Thread {
		public int x = 0;
	
	
		@Override
		public void run() {
			//if(true){return;}
			try {
				try{
					timercount.cancel();
				}catch(Exception e){}
				try{
					timer.cancel();
				}catch(Exception e){}
				timer = new java.util.Timer(true);
				timer.schedule(new java.util.TimerTask() {
					public void run() {
						if (x != 0) {
							timer.cancel();
						} else { 
							try {
								new Timmr().start();
							if(AllLineActivity.source.equals("1")){
									// 如果基础数据部存在。 先获取基础数据。
									String path = "http://223.112.2.69:5902/pubserverT/bus/busQuery.do?random=0.2767780260182917&loginCnt="+LogInfo.getLocCode(lineNumbber)+"&command=real&busLineId="+lineNumbber;
									URL url;
									url = new URL(path);
									HttpURLConnection conn = (HttpURLConnection) url
											.openConnection();
									conn.setReadTimeout(30 * 1000);
									conn.setRequestMethod("GET");
									InputStream inStream = conn.getInputStream();
									datas = busLineToList(Utils
											.convertStreamToStringNoz(inStream));
									String dre="0";
									if((showNextCount+1)%2==0){
										
									
										if(isUpline==1)
										{
											dre="0";
										}
										else
										{
											dre="1";	
										}
										
									HttpPost httpgets = new HttpPost(
											"http://223.112.2.69:5902/pubserverT/bus/busQuery.do?command=prt");		
									 List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
									 nvps.add(new BasicNameValuePair("a", "")); 
									 nvps.add(new BasicNameValuePair("busLineId", lineNumbber));  
									 nvps.add(new BasicNameValuePair("endTime", Utils.dateToString(new Date(),"yyyy-MM-dd ")+"23:59:59"));  
									 nvps.add(new BasicNameValuePair("inDown", dre));  
									 nvps.add(new BasicNameValuePair("order", "asc"));  
									 nvps.add(new BasicNameValuePair("page", "1"));  
									 nvps.add(new BasicNameValuePair("rows", "20"));  
									 nvps.add(new BasicNameValuePair("sort", "stime"));
									 nvps.add(new BasicNameValuePair("type", "1"));
									 nvps.add(new BasicNameValuePair("startTime", Utils.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss")));  			
									 httpgets.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));  
									httpgets.setHeader("Accept","application/json, text/javascript, */*; q=0.01"); 
									httpgets.setHeader("Accept-Encoding","gzip, deflate"); 
									httpgets.setHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"); 
									httpgets.setHeader("Content-Type","application/x-www-form-urlencoded"); 
									httpgets.setHeader("Cookie","JSESSIONID=1A12FEBA9FDDFD1776AAC37EDCF71C9D"); 
									httpgets.setHeader("Host","223.112.2.69:5902"); 
									httpgets.setHeader("Referer","http://223.112.2.69:5902/pubserverT/prt/linePrt.do?command=pt"); 
									httpgets.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0"); 
									httpgets.setHeader("X-Requested-With","XMLHttpRequest"); 
									HttpResponse response = httpclient.execute(httpgets);			
									HttpEntity entity = response.getEntity();
									InputStream instreams = entity.getContent();
									String uplineString1 =Utils.convertStreamToStringNoz(instreams);
									JSONObject jsonObjs = new JSONObject(uplineString1);
									JSONArray jsonaRRY=jsonObjs.getJSONArray("rows");
									int  length=jsonaRRY.length();
									preLine="";
									if(length>2)
									{
										length=2;
										for(int inx=0; inx<length;inx++)
										{
											 JSONObject obj =(JSONObject)jsonaRRY.get(inx);
											 String pre=obj.getString("stime");
											 preLine=preLine+" "+pre;
										}
										
									}else if(length<=2&&length>0)
									{
									
										for(int inx=0; inx<length;inx++)
										{
											 JSONObject obj =(JSONObject)jsonaRRY.get(inx);
											 String pre=obj.getString("stime");
											 preLine=preLine+" "+pre;
										}
									
										
									}else{
										preLine="暂无";
									}
									if(preLine!=null&&!preLine.equals("")){
										setLoadingText("发车："+preLine);
									}
								}
								showNextCount++;
							}else if(AllLineActivity.source.equals("3")) 
							{
								
								getCookie();
								setLoadingText("南京实时公交");
								String dre="0";
								if(isUpline==1)
								{
									dre="1";
								}
								else
								{
									dre="0";	
								}
								
								GetLineReq localGetLineReq = new GetLineReq();
							//	localGetLineReq.addParams("line_code", "1270");
								localGetLineReq.addParams("line_id",  lineNumbber);
								localGetLineReq.addParams("updown_type",  String.valueOf(dre));
						    	String path =  localGetLineReq.getUrl();
								URL url;
								try {
									url = new URL(path);
									HttpURLConnection conn = (HttpURLConnection) url
											.openConnection();
									conn.setReadTimeout(10 * 1000);
									conn.setRequestMethod("GET");
									conn.setRequestProperty("Cookie",cookieString); 
									
									InputStream inStream = conn.getInputStream();
									String s=Utils .convertStreamToStringNoz(inStream);
									GetLineRes   localGetLineRes= new GetLineRes();
									localGetLineRes.init(s);
									ArrayList<BusStationBean>	stions=localGetLineRes.getStations();
									datas=busLineToListNj(stions);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else 
							{
								getCookie();
								setLoadingText("南京实时公交");
								String dre="0";
								if(isUpline==1)
								{
									dre="1";
								}
								else
								{
									dre="0";	
								}
								String busId ="";
								if(lineName!=null){
									String path = "http://njbus.zhihui.cc/njgjc/webapp.do?method=term&term="+URLEncoder.encode(lineName);
									URL url;       
									url = new URL(path);
									HttpURLConnection conn = (HttpURLConnection) url
											.openConnection();
									conn.setRequestProperty("Cookie",cookieString); 
									conn.setReadTimeout(30 * 1000);
									conn.setRequestMethod("GET");
									InputStream inStream = conn.getInputStream();
									
	
									 busId = getBusId(Utils
											.convertStreamToStringNoz(inStream));
									
								}
								String[] businfo=busId.split(",");
								String dreData=businfo[0];
								String infoData=businfo[1];
								
								busId=businfo[0].replace("0|", "");
								String[] ids=busId.split("-");
								String ds2="";
								String specialId="";
								boolean isCommon=true;
								
								String fcd=downLine.replace("-", "→").replaceAll(" ", "");
								String fcu=upLine.replace("-", "→").replaceAll(" ", "");
								String fb=infoData.replaceAll(" ", "");
								String[] dreSplit=dreData.split("-");
								if(dreSplit.length<2)
								{
									specialId=dreData.split("\\|")[1];
									isCommon=false;
									
								}else{
									ds2=dreSplit[2];
								}
								
								String curDre="";
								//如果我是1 要求对方也是upline 
								//得到当前我的方向
								if(isUpline==1)
								{
									curDre=fcu;
								}
								else{
									curDre=fcd;
								}
								//如果当前他的方向不是我的方向 给他换个方向
								if(!fb.contains(curDre))
								{
									if(isCommon==false){
										if(fanxiangid!=null){
											specialId=fanxiangid;
										}else
										{
											HomeActivity.currentStationName = (String) downLineList.get(0)
													.get("sn");
											isUpline=2;
											waitingStationText.setText(currentStationName);
											derectionText.setText(downLine);
										}
									}
									if(ds2.equals("1"))
									{
										ds2="0";
									}else
									{
										ds2="1";
									}
								}
								String path ="";
								
								if(isCommon==false)
								{
									path = "http://njbus.zhihui.cc/njgjc/webapp.do?method=standby&lineId="+specialId;
								}else{
									path = "http://njbus.zhihui.cc/njgjc/webapp.do?method=standby&lineId="+ids[0]+"-"+URLEncoder.encode(ids[1])+"-"+ds2;
								}
								URL url;
								url = new URL(path);
								HttpURLConnection conn = (HttpURLConnection) url
										.openConnection();
								conn.setReadTimeout(30 * 1000);
								conn.setRequestMethod("GET");
								InputStream inStream = conn.getInputStream();
								datas = busLineToListNJBase(inStream);
								
							}
								handler.post(runnableTravelData);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

			

				
				}, 0, intervel * 1000);

				
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	};
	
	java.util.Timer timercount;
	/**
	 * 下载文件线程
	 */
	private class Timmr extends Thread {
		
	
		@Override
		public void run() {
				count = intervel;
				try{
					timercount.cancel();
				}catch(Exception e){}
				timercount = new java.util.Timer(true);
				timercount.schedule(new java.util.TimerTask() {
					public void run() {
						if (count == 0) {
							timercount.cancel();
						} else { 
							
							handler.post(timmAble);
							count--;
						}
					}
				
				}, 0, 1000);
				
			
				}
		

	};
	Runnable timmAble = new Runnable() {
		@Override
		public void run() {
			try{
				
				refauto.setText("自动刷新"+count)	;
				
			
		}catch(Exception e){
			e.printStackTrace();}
		}

	};
	Runnable timmAutoStatic = new Runnable() {
		@Override
		public void run() {
			try{
				
				refauto.setText("自动刷新")	;
				
			
		}catch(Exception e){
			e.printStackTrace();}
		}

	};
	private String getBusId(String convertStreamToStringNoz) {
		JSONArray jsonObjs = null;
		String node=null;
		try {
			jsonObjs = new JSONArray(convertStreamToStringNoz);
			if(jsonObjs.length()>0){
				JSONObject jsonObj = (JSONObject) jsonObjs.opt(0);
				node= jsonObj.getString("value");
				node=node+","+ jsonObj.getString("label");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return node;
	}
	

	// 构建Runnable对象，在runnable中更新界面
	Runnable runnableTravelData = new Runnable() {
		@Override
		public void run() {
			try{
				
			// 生成适配器的Item和动态数组对应的元素
			SpecialAdapter listItemAdapter = new SpecialAdapter(
					HomeActivity.this, datas,// 数据源
					R.layout.list_items,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项

					new String[] { "carZbh", "speed", "desc", 
							"STime", "remainStation" },
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.Item1, R.id.Item2, R.id.Item3, 
							R.id.Item5, R.id.Item6 });
			// 添加并且显示
			list.setAdapter(listItemAdapter);
			if(progressDialog!=null&&progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			if(datas.isEmpty())
			{
				noDataText.setText("持续查找班车中。。。");
			}else
			{
				noDataText.setText("");
			}	
			
			
				
			
		}catch(Exception e){
			e.printStackTrace();}
		}

	};
	private void getCookie() {
		try {
			if(cookieString==null){
			java.net.CookieManager manager = new java.net.CookieManager();  
	        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);  
	        CookieHandler.setDefault(manager);  
			String path="http://njbus.zhihui.cc/njgjc/webapp.do?method=toStandBy&p=o"+getOpenId();
			URL url;
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			conn.setReadTimeout(30 * 1000);
			conn.setRequestMethod("GET");
			InputStream inStream = conn.getInputStream();
			conn.getHeaderFields();  
	        CookieStore store = manager.getCookieStore();  
	         responseUpdateCookieHttpURL(store); 
	         cookieString=getCookieInfo(cookieMap);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	/** 
	  * 获取cookie信息 
	  *  
	  * @param cookieMap 
	  * @return 
	  */  
	 public  String getCookieInfo(HashMap<String, String> cookieMap) {  
	     StringBuilder cookieInfo = new StringBuilder();  
	     if (cookieMap != null && cookieMap.size() > 0) {  
	         Iterator<Entry<String, String>> iter = cookieMap.entrySet().iterator();  
	         Entry<String, String> entry;  
	         while (iter.hasNext()) {  
	             String key = "";  
	             String value = "";  
	             entry = iter.next();  
	             key = entry.getKey();  
	             value = entry.getValue();  
	             cookieInfo.append(key).append("=").append(value).append(";");  
	         }  
	     }  
	     return cookieInfo.toString();  
	 }  
	  
	
	@SuppressLint("NewApi")  
	  @TargetApi(Build.VERSION_CODES.GINGERBREAD)  
	  public void responseUpdateCookieHttpURL(CookieStore store) {  
	      boolean needUpdate = false;  
	      List<HttpCookie> cookies = store.getCookies();  
	  
	      if (cookieMap == null) {  
	          cookieMap = new HashMap<String, String>();  
	      }  
	      for (HttpCookie cookie : cookies) {  
	          String key = cookie.getName();  
	          String value = cookie.getValue();  
	          if (cookieMap.size() == 0 || !value.equals(cookieMap.get(key))) {  
	              needUpdate = true;  
	          }  
	          cookieMap.put(key, value);  
	  //        BDebug.e(HTTP_COOKIE, cookie.getName() + "---->" + cookie.getDomain() + "------>" + cookie.getPath());  
	      }  
	        
	  }  
	private String getOpenId() {
		// TODO Auto-generated method stub
		try {
			return EncryptUtil.getBase64(String.valueOf(Math.floor(Math.random()*23000)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "80fOjjYC-tTJyH9befdawZuoU9c";
	}


	private List<Map<String, Object>> busLineToListNJBase(
			InputStream inStream) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> jsonObjs=	Utils.getXpathBusBeiFen(inStream);
			MediaPlayer mediaPlayer01;
			mediaPlayer01 = MediaPlayer.create(this, R.raw.nanjing);
			// 获取最近的站点
			// String location = ((Location) getApplication()).mTv;
			// Map<String, Object> nearStation = getNearStation(location);
			isArrived = false;
			// 获取最近的站点
			// String location = ((Location) getApplication()).mTv;
			// Map<String, Object> nearStation = getNearStation(location);
			List<Map<String, Object>> datas = isUpline==1?HomeActivity.upLineList:HomeActivity.downLineList;
			
			for (int i = 0; i < jsonObjs.size(); i++) {
				Map<String, Object> sta=null;
				Map<String, Object> jsonObj =  jsonObjs.get(i);
				String STime = null;
				String speed  = null;
				String no = null;
				String log = null;
				String lat = null;
				String inOrOut = null;
				String groupId = null;
				String carNo = null;
				String inDown = null;
				String carZbh = null;
				String devIdStr = null;
				String lineId = null;
				String desc = null;
				String derection = null;
				String remainStation = null;
				int sortNo = 0;
				try {
					STime = (String) jsonObj.get("updateTime");
					lineId = (String)jsonObj.get("stNo");
					inOrOut = (String)jsonObj.get("distance")+"";
					
					desc = "无法获取";
				
					for (Map<String, Object> station : datas) {
						if (station.get("sno")
								.equals(lineId)) {
							sta=station;
							no = (String) station
											.get("sno");
							break;
						}
					}
					
					

					

					int busStationNo = Integer.parseInt(no);
					if(no.equals("null")){
						System.out.println(111);
						
					}
					
					
				
					//摆齐 都从0 开始
					int minusNo=busStationNo - currentStation;
				
				
					//没到站或刚到站的显示 剩余战数和到站剩余时间
					if (minusNo < 0) {
						remainStation = "还有" + (currentStation - busStationNo)
								+ "站   大约" + 2.5
								* (currentStation - busStationNo) + "分钟";
						sortNo = currentStation - busStationNo;
					}
					if (minusNo > 0) {
						remainStation ="已过站";
						sortNo = currentStation + busStationNo;
						}
					
					if(busStationNo==0)
					{
						remainStation = "等待发车 ";
						sortNo = 1000;
					}
					
					//行车方向
					//if ("1".equals(inDown)) {
					//	derection = downLine;
					//} else {
					//	derection = upLine;
					//}
					//有车还有一站就到了。
					if ((busStationNo - currentStation >= -2&&busStationNo - currentStation <0)) {
						isArrived = true;
					}
					if (no != null) {
						// 获取车站
						
					
						if (null!=sta){
							if(inOrOut.contains("已经")||inOrOut.contains("已到"))
							{
								desc = "抵达["+sta.get("sn") + "]站附近";;	
								carZbh=inOrOut.split("已")[0];
								
								
							}else{
								desc = "开往["+sta.get("sn") + "]站,距离"+inOrOut.split("约")[1]+"米";
								carZbh=inOrOut.split("约")[0];
						
							}
							}else
								
								
						{
							desc = "待发车";	
							if(currentStation==0)
							{
							
								sortNo =  -1000;
							}else
							{
								sortNo = 100000;
								
							}
						
						}
						
						if (busStationNo!=0&&currentStation!=0&&minusNo == 0) {
						
								remainStation = "抵达["+sta.get("sn") + "]站附近";
							sortNo = 0;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				// 最近站点的站号
				// if (nearStation != null) {
				// double sno = Double
				// .valueOf((String) nearStation.get("sno"));
				// remainTime = String.valueOf((sno - Double.valueOf(no)) * 4);
				// W}
				// double
				// distance=MapDistance.getDistance(Double.valueOf(la1[0]),Double.valueOf(
				// la1[1]),Double.valueOf( lat),Double.valueOf(log) );
				Map<String, Object> bus = new HashMap<String, Object>();
				bus.put("STime", "更新时间 " +STime);
				bus.put("speed","");
				bus.put("no", no);
				bus.put("lat", lat);
				bus.put("log",  log);
				//bus.put("inOrOut",inOrOut.split(" ")[1]);
				bus.put("groupId",  groupId);
				bus.put("carNo",  carNo);
				bus.put("inDown",  inDown);
				bus.put("lineId",  lineId);
				bus.put("devIdStr",  devIdStr);
				bus.put("carZbh", carZbh);
				bus.put("desc", desc);
				bus.put("derection", derection);
				bus.put("remainStation", remainStation == null ? "暂无"
						: remainStation);
				bus.put("sortNo",sortNo);
				data.add(bus);
			}
			if (isArrived) {
				if(null!=setting.getNoticeType()&&SettingActivity.NOTICE2MIN.equals(setting.getNoticeType())){
					if(	!mediaPlayer01.isPlaying()){
						mediaPlayer01.start();
				}}
				
			}
		
		ComparatorMap cmap = new ComparatorMap();
		Collections.sort(data, cmap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return data;}

	/***
	 * json字符串转java List
	 * 
	 * @param rsContent
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> busLineToList(String rsContent)
			throws Exception {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonObjs = new JSONArray(rsContent);
			MediaPlayer mediaPlayer01;
			mediaPlayer01 = MediaPlayer.create(this, R.raw.nanjing);
			// 获取最近的站点
			// String location = ((Location) getApplication()).mTv;
			// Map<String, Object> nearStation = getNearStation(location);
			isArrived = false;
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonObjs.opt(i);
				String STime = null;
				String speed = null;
				String no = null;
				String log = null;
				String lat = null;
				String inOrOut = null;
				String groupId = null;
				String carNo = null;
				String inDown = null;
				String carZbh = null;
				String devIdStr = null;
				String lineId = null;
				String desc = null;
				String derection = null;
				String remainStation = null;
				int sortNo = 0;
				try {
					STime = jsonObj.getString("STime");
					speed = jsonObj.getString("speed");
					no = jsonObj.getString("no");
					log = jsonObj.getString("log");
					//log = log.replaceFirst("118", "118.");
					lat = jsonObj.getString("lat");
					//lat = lat.replaceFirst("31", "31.");
					inOrOut = jsonObj.getString("inOrOut");
					groupId = jsonObj.getString("groupId");
					carNo = jsonObj.getString("carNo");
					inDown = jsonObj.getString("inDown");
					lineId = jsonObj.getString("lineId");
					devIdStr = jsonObj.getString("devIdStr");
					carZbh = jsonObj.getString("carZbh");
					desc = "无法获取";

					int busStationNo = Integer.parseInt(no);
				
					//摆齐 都从0 开始
					int minusNo=busStationNo - currentStation;
					//不是当前选中的行驶方向的过滤掉
					if (Integer.parseInt(inDown) != isUpline) {
						continue;
					}
				
					//没到站或刚到站的显示 剩余战数和到站剩余时间
					if (minusNo < 0) {
						remainStation = "还有" + (currentStation - busStationNo)
								+ "站   大约" + 2.5
								* (currentStation - busStationNo) + "分钟";
						sortNo = currentStation - busStationNo;
					}
					if (minusNo > 0) {
						remainStation = "已过站";
						sortNo = currentStation + busStationNo;
						}
					
					if(busStationNo==0)
					{
						remainStation = "等待发车 ";
						sortNo = 1000;
					}
					String dre="0";
					if(inDown.equals("1"))
					{
						dre="1";
					}
					else
					{
						dre="0";	
					}
					//有车还有一站就到了。
					if ((busStationNo - currentStation >= -2&&busStationNo - currentStation <0&&!"1".equals(inOrOut))) {
						isArrived = true;
					}
					if (no != null) {
						// 获取车站
						
						
						@SuppressWarnings("unchecked")
								Map<String, String> station = (Map) (staticStations
								.get(String.valueOf(busStationNo) + "#" + dre));
						if (null!=station){
							if ("1".equals(inOrOut)) {
								desc = "驶入[" + station.get("sn") + "]站";
							} else {
								desc = "驶出[" + station.get("sn") + "]站";
							}
						}else
						{
							desc = "待发车";	
							if(currentStation==0)
							{
							
								sortNo =  -1000;
							}else
							{
								sortNo = 100000;
								
							}
						
						}
						
						if (busStationNo!=0&&currentStation!=0&&minusNo == 0) {
							if ("1".equals(inOrOut)) {
								remainStation = "进["+station.get("sn") + "]站";
							} else {
								remainStation = "出["+station.get("sn") + "]站";
							}
							
							sortNo = 0;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				// 最近站点的站号
				// if (nearStation != null) {
				// double sno = Double
				// .valueOf((String) nearStation.get("sno"));
				// remainTime = String.valueOf((sno - Double.valueOf(no)) * 4);
				// W}
				// double
				// distance=MapDistance.getDistance(Double.valueOf(la1[0]),Double.valueOf(
				// la1[1]),Double.valueOf( lat),Double.valueOf(log) );
				Map<String, Object> bus = new HashMap<String, Object>();
				bus.put("STime", "更新时间 " +STime);
				bus.put("speed", "车速" +speed);
				bus.put("no", no);
				bus.put("lat", lat);
				bus.put("log",  log);
				bus.put("inOrOut",inOrOut);
				bus.put("groupId",  groupId);
				bus.put("carNo",  carNo);
				bus.put("inDown",  inDown);
				bus.put("lineId",  lineId);
				bus.put("devIdStr",  devIdStr);
				bus.put("carZbh",  carZbh);
				bus.put("desc", desc);
				bus.put("derection", derection);
				bus.put("remainStation", remainStation == null ? "暂无"
						: remainStation);
				bus.put("sortNo",sortNo);
				data.add(bus);
			}
			if (isArrived) {
				if(null!=setting.getNoticeType()&&SettingActivity.NOTICE2MIN.equals(setting.getNoticeType())){
					if(	!mediaPlayer01.isPlaying()){
						mediaPlayer01.start();
				}}
				
			}
		
		ComparatorMap cmap = new ComparatorMap();
		Collections.sort(data, cmap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return data;
	}
	private List<Map<String, Object>> busLineToListNj(ArrayList<BusStationBean>	 rsContent)
			throws Exception {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			MediaPlayer mediaPlayer01;
			mediaPlayer01 = MediaPlayer.create(this, R.raw.nanjing);
			// 获取最近的站点
			// String location = ((Location) getApplication()).mTv;
			// Map<String, Object> nearStation = getNearStation(location);
			isArrived = false;
			List<Map<String, Object>> datas = isUpline==1?HomeActivity.upLineList:HomeActivity.downLineList;
			
			for (int i = 0; i < rsContent.size(); i++) {
				Map<String, Object> sta=null;
				BusStationBean jsonObj = (BusStationBean) rsContent.get(i);
				String STime = null;
				String speed = null;
				String no = null;
				String log = null;
				String lat = null;
				String inOrOut = null;
				String groupId = null;
				String carNo = null;
				String inDown = null;
				String carZbh = null;
				String devIdStr = null;
				String lineId = null;
				String desc = null;
				String derection = null;
				String remainStation = null;
				int sortNo = 0;
				try {
					STime = jsonObj.getUpdate_time()+"秒前";
					lineId = jsonObj.getSt_id();
					inOrOut = jsonObj.getDistence();
					desc = "无法获取";
				
					for (Map<String, Object> station : datas) {
						if (station.get("sn")
								.equals(lineId)) {
							sta=station;
							no = (String) station
											.get("sno");
						}
					}
					
					

					

					int busStationNo = Integer.parseInt(no);
				
					//摆齐 都从0 开始
					int minusNo=busStationNo - currentStation;
				
				
					//没到站或刚到站的显示 剩余战数和到站剩余时间
					if (minusNo < 0) {
						remainStation = "还有" + (currentStation - busStationNo)
								+ "站   大约" + 2.5
								* (currentStation - busStationNo) + "分钟";
						sortNo = currentStation - busStationNo;
					}
					if (minusNo > 0) {
						remainStation ="已过站";
						sortNo = currentStation + busStationNo;
						}
					
					if(busStationNo==0)
					{
						remainStation = "等待发车 ";
						sortNo = 1000;
					}
					
					//行车方向
					//if ("1".equals(inDown)) {
					//	derection = downLine;
					//} else {
					//	derection = upLine;
					//}
					//有车还有一站就到了。
					if ((busStationNo - currentStation >= -2&&busStationNo - currentStation <0)) {
						isArrived = true;
					}
					if (no != null) {
						// 获取车站
						
					
						if (null!=sta){
								desc = "距离站点"+inOrOut+"米";
						}else
						{
							desc = "待发车";	
							if(currentStation==0)
							{
							
								sortNo =  -1000;
							}else
							{
								sortNo = 100000;
								
							}
						
						}
						
						if (busStationNo!=0&&currentStation!=0&&minusNo == 0) {
						
								remainStation = "抵达["+sta.get("sn") + "]站附近";
							sortNo = 0;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				// 最近站点的站号
				// if (nearStation != null) {
				// double sno = Double
				// .valueOf((String) nearStation.get("sno"));
				// remainTime = String.valueOf((sno - Double.valueOf(no)) * 4);
				// W}
				// double
				// distance=MapDistance.getDistance(Double.valueOf(la1[0]),Double.valueOf(
				// la1[1]),Double.valueOf( lat),Double.valueOf(log) );
				Map<String, Object> bus = new HashMap<String, Object>();
				bus.put("STime", "更新时间 " +STime);
				bus.put("speed","");
				bus.put("no", no);
				bus.put("lat", lat);
				bus.put("log",  log);
				bus.put("inOrOut",inOrOut);
				bus.put("groupId",  groupId);
				bus.put("carNo",  carNo);
				bus.put("inDown",  inDown);
				bus.put("lineId",  lineId);
				bus.put("devIdStr",  devIdStr);
				bus.put("carZbh",  "运行车辆("+sta.get("sn")+")");
				bus.put("desc", desc);
				bus.put("derection", derection);
				bus.put("remainStation", remainStation == null ? "暂无"
						: remainStation);
				bus.put("sortNo",sortNo);
				data.add(bus);
			}
			if (isArrived) {
				if(null!=setting.getNoticeType()&&SettingActivity.NOTICE2MIN.equals(setting.getNoticeType())){
					if(	!mediaPlayer01.isPlaying()){
						mediaPlayer01.start();
				}}
				
			}
		
		ComparatorMap cmap = new ComparatorMap();
		Collections.sort(data, cmap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return data;
	}
	public class ComparatorMap implements Comparator<Object> {

		public int compare(Object arg0, Object arg1) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data0 = (Map<String, Object>) arg0;
			@SuppressWarnings("unchecked")
			Map<String, Object> data1 = (Map<String, Object>) arg1;
			try{
			// 首先比较年龄，如果年龄相同，则比较名字
			int data0SortNo = (Integer) data0.get("sortNo");
			int data1SortNo = (Integer) data1.get("sortNo");
			if (data0SortNo > data1SortNo) {
				return 1;
			} else if (data0SortNo == data1SortNo) {
				return 0;
			} else {
				return -1;
			}
			}catch(Exception e)
			{
				return 1;
			}
		}
	}


	private 	void	 stationStringToList(
			String rsContent) {
		
		try {
			JSONObject jSONob = new JSONObject(rsContent);
			
			JSONArray jSONArray = jSONob.getJSONArray("rows");
			int length=jSONArray.length();
			List<Map<String, Object>> allLineDatas=new  ArrayList<Map<String, Object>>();
			 HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();  
			 outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			 outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			 outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
			for(int index=0;index<length;index++ )
			{
				Map<String, Object> mp=new HashMap<String, Object>();
				JSONObject ob=jSONArray.getJSONObject(index);
				String busLineName=ob.getString("busLineName");
				String startStation=ob.getString("startStation");
				String b=ob.getString("b");
				String a=ob.getString("a");
				String busLineId=ob.getString("busLineId");
				String endStation=ob.getString("endStation");
				mp.put("linename", busLineName);
				mp.put("upLine", startStation+"-"+endStation );
				mp.put("downLine", endStation+"-"+startStation );
				mp.put("downTime", b);
				mp.put("upTime", a);
				mp.put("busLineId", busLineId);
				mp.put("linenamePinYin", PinyinHelper.toHanyuPinyinString(busLineName, outputFormat,  ""));
		
				char[] pinyinArray =busLineName.toCharArray();
				String fina="";
			   for(char c:pinyinArray){
				   String[] trs=  PinyinHelper.toHanyuPinyinStringArray(c, outputFormat);
				   if(null!=trs&&trs.length>0){
					   fina+= trs[0].charAt(0);}
				   else
				   {
					   fina+=c; 
				   }
			   }
				mp.put("linenamePinYinShort",fina);
				allLineDatas.add(mp);
			
			}
			saveLineObj(allLineDatas);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	private void saveLineObj(List<Map<String, Object>>  maps) {
		LineObjDao.exeC("delete  from TABLE_LINEOBJ where 1=1 and source=1;");
		LineObj lineObj = null;
		for(Map<String, Object> m:maps){
			lineObj=new LineObj();
			lineObj.setBusLineId((String) m.get("busLineId"));
			lineObj.setDownLine((String) m.get("downLine"));
			lineObj.setDownTime((String) m.get("downTime"));
			lineObj.setLinename((String) m.get("linename"));
			lineObj.setLinenamePinYin((String) m.get("linenamePinYin"));
			lineObj.setUpLine((String) m.get("upLine"));
			lineObj.setUpTime((String) m.get("upTime"));
			lineObj.setSource("1");
			LineObjDao.insert(lineObj);
			
			initJiangNingStation(lineObj);
		}
		
		
		
		
	}
	


	private void initJiangNingStation(LineObj lineObj) {
		setLoadingText("江宁数据初始化站点"+load);
		load++;
		String path = "http://223.112.2.69:5902/pubserverT/bus/busQuery.do?random=0.545071161352098&command=ln&busLineId="
				+ lineObj.getBusLineId();
			URL url = null;
			try {
				url = new URL(path);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) url
						.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn.setReadTimeout(30 * 1000);
			try {
				conn.setRequestMethod("GET");
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InputStream inStream = null;
			try {
				inStream = conn.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			staticStations = null;
			String  ss=Utils
					.convertStreamToStringNoz(inStream);
			staticStations = stationStringToListInfo(ss, lineObj.getBusLineId());
	}
	
	private Map<String, Map<String, Object>> stationStringToListInfo(
			String rsContent,String linid ) {
		Calendar c =Calendar.getInstance();
	
		long mills=c.getTimeInMillis();
	
		Map<String, Map<String, Object>> updata = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> downdata = new HashMap<String, Map<String, Object>>();
		upLineList = new ArrayList<Map<String, Object>>();
		downLineList = new ArrayList<Map<String, Object>>();

		try {
			JSONObject obj = new JSONObject(rsContent);
			JSONArray jSONArray = obj.getJSONArray("data");
			JSONObject datas = (JSONObject) jSONArray.get(0);
			JSONArray downArray = datas.getJSONArray("downlist");
			JSONObject downtimeObject = datas.getJSONObject("downtime");
			JSONObject uptimeObject = datas.getJSONObject("uptime");
			String downTimeS=downtimeObject.getString("stime");
			String downTimeE=downtimeObject.getString("etime");
			String downTimeString=downTimeS+"——"+downTimeE;
			String upTimeS=uptimeObject.getString("stime");
			String upTimeE=uptimeObject.getString("etime");
			String upTimeString=upTimeS+"——"+upTimeE;
			JSONArray upObjs = datas.getJSONArray("uplist");
			updata.putAll(downdata);
			Object downs=	downArray.get(1);
			   ArrayList<BusStationBean> stations = new ArrayList();
			    ArrayList<BusBaseBean> lineStations = new ArrayList();
			    clearLineData("10095"+linid);
				for (int i = 0; i < downArray.length(); i++) {
					JSONObject jsonObj = (JSONObject) downArray.opt(i);
						  BusStationBean localBusStationBean = new BusStationBean();
					      localBusStationBean.st_id ="10095"+jsonObj.getString("si");
					      localBusStationBean.st_name =jsonObj.getString("sn");
					      localBusStationBean.st_real_lat = 0;
					      localBusStationBean.st_real_lon =0;
					      localBusStationBean.st_status = "0";
					      localBusStationBean.weiba_id =jsonObj.getString("si");
					      localBusStationBean.st_side = "0";
					      localBusStationBean.is_true = 1;
					      localBusStationBean.update_time =String.valueOf(mills);
					      stations.add(localBusStationBean);
					      
					      BusBaseBean localBusBaseBean = new BusBaseBean();
					      localBusBaseBean.line_station_id =linid+jsonObj.getString("si");
					      localBusBaseBean.line_id ="10095"+linid;
					      localBusBaseBean.line_code = "10095"+linid;
					      localBusBaseBean.line_interval ="0";
					      localBusBaseBean.updown_type ="0";
					      localBusBaseBean.st_id = "10095"+jsonObj.getString("si");
					      localBusBaseBean.st_name = jsonObj.getString("sn");
					      localBusBaseBean.st_stop_level =Integer.parseInt(jsonObj.getString("sno")) ;
					      localBusBaseBean.line_st_status = "0";
					      localBusBaseBean.is_true =1;
					      localBusBaseBean.update_time =String.valueOf(mills);
					      lineStations.add(localBusBaseBean);
				}   
				
			  DBUtil.doUpdateStations(getApplicationContext(), stations);
			  DBUtil.doUpdateLineStations(getApplicationContext(),lineStations);
						
			  stations.clear();
			  lineStations.clear();
				for (int i = 0; i < upObjs.length(); i++) {
					setLoadingText("江宁数据初始化站点"+load);
					load++;
					JSONObject jsonObj = (JSONObject) upObjs.opt(i);
						  BusStationBean localBusStationBean = new BusStationBean();
						  localBusStationBean.st_id ="10095"+jsonObj.getString("si");
					      localBusStationBean.st_name =jsonObj.getString("sn");
					      localBusStationBean.st_real_lat = 0;
					      localBusStationBean.st_real_lon =0;
					      localBusStationBean.st_status = "0";
					      localBusStationBean.weiba_id =jsonObj.getString("si");
					      localBusStationBean.st_side = "0";
					      localBusStationBean.is_true = 1;
					      localBusStationBean.update_time =String.valueOf(mills);
					      stations.add(localBusStationBean);
					      
					      BusBaseBean localBusBaseBean = new BusBaseBean();
					      localBusBaseBean.line_station_id =linid+jsonObj.getString("si");
					      localBusBaseBean.line_id ="10095"+linid;
					      localBusBaseBean.line_code = "10095"+linid;
					      localBusBaseBean.line_interval ="0";
					      localBusBaseBean.updown_type ="1";
					      localBusBaseBean.st_id = "10095"+jsonObj.getString("si");
					      localBusBaseBean.st_name = jsonObj.getString("sn");
					      localBusBaseBean.st_stop_level =Integer.parseInt(jsonObj.getString("sno")) ;
					      localBusBaseBean.line_st_status = "0";
					      localBusBaseBean.is_true =1;
					      localBusBaseBean.update_time =String.valueOf(mills);
					      lineStations.add(localBusBaseBean);
				}   
				
			  DBUtil.doUpdateStations(getApplicationContext(), stations);
			  DBUtil.doUpdateLineStations(getApplicationContext(),lineStations);
		
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("selectStation", e.getMessage());
		}
		return updata;
	}
	
	
	private void clearLineData(String lineId) {
		LineObjDao.exeC("delete  from ibus_line_stations where line_id = "+lineId);
	}
	
	/**
	 * 解析车站数据到列表中。
	 * 
	 * @param data
	 * @param jsonObjs
	 * @param updwon
	 */
	private void parseStation(Map<String, Map<String, Object>> data,
			JSONArray jsonObjs, int updwon) {
		String begin = "";
		String end = "";
		for (int i = 0; i < jsonObjs.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonObjs.opt(i);
			String sn = null;
			String sno = null;
			String log = null;
			String lat = null;
			String si = null;
			String derection = null;
			try {
				sn = jsonObj.getString("sn");
				sno = jsonObj.getString("sno");
				log = jsonObj.getString("log");
				lat = jsonObj.getString("lat");
				si = jsonObj.getString("si");
				derection = String.valueOf(updwon);
				if (i == 0) {
					begin = sn;
				}

				if (i == jsonObjs.length() - 1) {
					end = sn;
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
			Map<String, Object> station = new HashMap<String, Object>();
			station.put("sn", sn == null ? "暂无" : sn);
			station.put("sno", sno == null ? "暂无" : sno);
			station.put("log", log == null ? "暂无" : log);
			station.put("lat", lat == null ? "暂无" : lat);
			station.put("si", si == null ? "暂无" : si);
			station.put("derection", "联系电话:"
					+ (derection == null ? "暂无" : derection));
			data.put(sno + "#" + derection, station);

			if (updwon == 1) {
				upLineList.add(station);
			} else {
				downLineList.add(station);
			}
		}
		ComparatorStationMap cmap = new ComparatorStationMap();
		if (null != upLineList) {
			Collections.sort(upLineList, cmap);
		}
		if (null != downLineList) {
			Collections.sort(downLineList, cmap);
		}
		if (updwon == 1) {
			upLine = begin + " → " + end;
		} else {
			downLine = begin + " → " + end;
		}

	}

	public class ComparatorStationMap implements Comparator {

		public int compare(Object arg0, Object arg1) {
			Map<String, Object> data0 = (Map<String, Object>) arg0;
			Map<String, Object> data1 = (Map<String, Object>) arg1;

			// 首先比较年龄，如果年龄相同，则比较名字
			int data0SortNo = Integer.parseInt((String) data0.get("sno"));
			int data1SortNo = Integer.parseInt((String) data1.get("sno"));

			if (data0SortNo > data1SortNo) {
				return 1;
			} else if (data0SortNo == data1SortNo) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	// 构建Runnable对象，在runnable中更新界面

	
	// 构建Runnable对象，在runnable中更新界面
		Runnable runnableUi = new Runnable() {
			@Override
			public void run() {
				try{
					 Toast.makeText(getApplicationContext(), "刷新数据中...", Toast.LENGTH_SHORT).show();
			}catch(Exception e){
				e.printStackTrace();}
			}
		};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

////		// Initialize the Mobile Ads SDK.
//		MobileAds.initialize(this, "ca-app-pub-0966333701823765~1003341168");
//
//		AdView mAdView = findViewById(R.id.ad_view);
//
//		AdRequest adRequest = new AdRequest.Builder()
//			//	.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//				.build();
//		mAdView.loadAd(adRequest);
		if (requestCode == 100001) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle ext = data.getExtras();
				int sno = ext.getInt("sno");
				String sn = ext.getString("sn");
				if (null != sn) {
					currentStation = sno;
					currentStationName = sn;
					waitingStationText.setText(sn);
					datas = new ArrayList<Map<String, Object>>();
					handler.post(runnableTravelData);
					loadingTravelInfo();
				}
			}

		}else if(requestCode == 100002) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle ext = data.getExtras();
				String lineNumbber = ext.getString("lineNumbber");
				String currentSelectedLine = ext.getString("currentSelectedLine");
				if(!"-xx".equals(lineNumbber))
				{
					this.lineNumbber=lineNumbber;
					this.currentSelectedLine=currentSelectedLine;
					currentStation = 0;
					isUpline=1;
					HomeActivity.fanxiangid=null;
					showNextCount=1;
					waitingStationText.setText(currentStationName);
					derectionText.setText(upLine);
					lineSelectorText.setText(currentSelectedLine);
					datas = new ArrayList<Map<String, Object>>();
					handler.post(runnableTravelData);
					loadingTravelInfo();
				}
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return true;
		}
		return true;
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("确定要退出在线公交吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
	
	
	public  void setLoadingText(String text)
	{
		logtext=text;
		handler.post(runnableTravelDatatext);
	}
	
	
	
	// 构建Runnable对象，在runnable中更新界面
	Runnable runnableTravelDatatext = new Runnable() {
		@Override
		public void run() {
			try{
				textviewx.setText(logtext);
		}catch(Exception e){
			e.printStackTrace();}
		}

	};
	

}
