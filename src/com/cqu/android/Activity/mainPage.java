package com.cqu.android.Activity;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.cqu.android.allservice.monitoring.Speed;
import com.cqu.android.allservice.monitoring.TrafficMonitoring;
import com.cqu.android.db.DatabaseAdapter;



public class mainPage extends Activity {
	
	private GridView toolbarGrid;
	public static TextView upRate,downRate;
	
	private Calendar currentCa;
	private DatabaseAdapter dbAdapter;
	private final int TOOLBAR_ITEM_KEEPINTIME = 0;
	private final int TOOLBAR_ITEM_NETLIST = 1;
	private final int TOOLBAR_ITEM_STATIST = 2;
	private final int TOOLBAR_ITEM_SETTING = 3;
	private final int TOOLBAR_ITEM_ABOUT = 4;
	
	

	/** �ײ���ťͼƬ **/
	int[] menu_toolbar_image_array = { R.drawable.shishi2,R.drawable.menu_debug,
			R.drawable.tongji,
			R.drawable.shezhi, R.drawable.women,
			};
	/** �ײ���ť���� **/
	String[] menu_toolbar_name_array = { "ʵʱ���","��������" ,"����ͳ��", "ϵͳ����", "��������",};
    
	private Handler handler = new Handler();
	
	    
	private Runnable runnable = new Runnable() {
	        public void run() {
	        
	            this.update();	            
	            handler.postDelayed(this,1500);// ���1.5��  
	           
	        }
	        void update() {
	            //ˢ��msg������
	        	currentCa =  Calendar.getInstance();
	    		int year = currentCa.get(Calendar.YEAR);
	    		int month = currentCa.get(Calendar.MONTH)+1;
	    		int day = currentCa.get(Calendar.DATE);
	    		//���ڴ˴�ʹ��  ��Ϊ�ֲ�����
	    		TextView today3G = (TextView) findViewById(R.id.Today_3G);
	    		TextView todayWifi = (TextView) findViewById(R.id.Today_WIFI);
	    		TextView month3G = (TextView) findViewById(R.id.Month_3G);
	    		TextView monthWifi = (TextView) findViewById(R.id.Month_WIFI);
	    		TextView remain3G = (TextView) findViewById(R.id.Remain);
	    		TextView limit = (TextView) findViewById(R.id.limit);
//	  
	    		String month3GTraffic;
	    		String day3GTraffic;
	    		String dayWIFITraffic;
	    		String monthWIFITraffic;
               
	    		//�����ݿ��������
	
	    		// ��ʾ��������3G����
	    		//public static String convertTraffic(long traffic)������λת��
	    		//public long calculateForMonth(int year, int Month, int netType) 
	    		month3GTraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 1));
	    		month3G.setText(month3GTraffic);

	    		// ��������3G����;�����ݿ��л�ȡ
	    		day3GTraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculate(year, month, day, 1));
	    		today3G.setText(day3GTraffic);
	    		

	    		// ��������WIFI����;�����ݿ��л�ȡ
	    		dayWIFITraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculate(year, month, day, 0));
	    		todayWifi.setText(dayWIFITraffic);
	    		
	    		// ��������WIFI�����������ݿ��л�ȡ
	    		monthWIFITraffic =TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 0));
	    		monthWifi.setText(monthWIFITraffic);
	    		
	    		// ��ʾ�����޶��mLimit�ļ��ж�ȡ
	    		try{
	    		String mLimit=getSharedPreferences("Settings", 0).getString(
	    					"mLimit", "0");
	    		double Limit,iLimit;
	    		//Ĭ����30����Ϊ�û��Լ��趨����Ϊ����
	    		if(mLimit.equals("0")){
	    			Limit=30.0;
	    			limit.setText("30.0MB");
	    		}else{
	    			Limit=Double.valueOf(mLimit);
	    			limit.setText(mLimit+"MB");
	    		}
	    		
	    		//��������ֵ

	    		  String mWarn = getSharedPreferences("Settings", 0).getString(
    					"mWarn", "0"); 		
	    			
	    			
	    			//iLimit=��������, �û��趨���޶���Ϊ�޶�  ����Ĭ��30
	    			if(mWarn.equals("0")){
	    				iLimit = 27.0;
	    			}else{
	    				iLimit = Double.valueOf(mWarn);
	    			}
	    			//Ū��������-----------------------------------------------
	    			double remain;
	    			String tempString[];// ��ʱ�洢3G����
	    			if (month3GTraffic.contains("KB")) {
	    				tempString = month3GTraffic.split("KB");
	    				double temp = Double.valueOf(tempString[0]);
	    				//iLimitת��Ϊkb  ��ȥ��ʹ�õ�����  �ٳ�1000 ������λ  ��ȥ
	    				remain = new BigDecimal(Limit * 1000 - temp).divide(new BigDecimal(1000),2,1).doubleValue();
	    			} else if (month3GTraffic.contains("MB")) {
	    				tempString = month3GTraffic.split("MB");
	    				double temp = Double.valueOf(tempString[0]);
	    				remain = Limit - temp;
	    			} else {
	    				tempString = month3GTraffic.split("GB");
	    				double temp = Double.valueOf(tempString[0]);
	    				remain = new BigDecimal(Limit * 1000 - temp).doubleValue();
	    			}
	    			remain3G.setText(remain + "MB");
	    			
	    			String warn = getSharedPreferences("Settings", 0).getString(
	    					"mWarn", "0");
	    			if(warn.equals("1")){
	    				if(remain>(Limit-iLimit)){
	    				   remain3G.setTextColor(Color.WHITE);
	    				}else{
	    				   remain3G.setTextColor(Color.RED);
	    				}
	    				
	    			}else{
	    				remain3G.setTextColor(Color.WHITE);
	    			}
	    			
	    		} catch (Exception ex) {
	    			Toast.makeText(mainPage.this,"������",Toast.LENGTH_SHORT).show();
	    		}
	    		
	        }
	        
	    }; 
	   
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTitle("СY���������ͳ��ϵͳ");
		dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
        downRate=(TextView) findViewById(R.id.downRate);
	    upRate=(TextView) findViewById(R.id.upRate);
        //�����������
      	handler.postDelayed(runnable,500);
      	//����˲ʱ�ٶ�
      	Intent service = new Intent();
		service.setClass(mainPage.this, Speed.class);		
		startService(service);
      	
       
    
        //�˵�
        
        toolbarGrid = (GridView)findViewById(R.id.GridView_toolbar);
        /**
         * Set a Drawable that should be used to highlight the currently selected item.
         *
         * @param resID A Drawable resource to use as the selection highlight.
         *
         * @attr ref android.R.styleable#AbsListView_listSelector
         * 
         */
        //���õ���İ�ť�ı�����ɫ
		toolbarGrid.setSelector(R.drawable.toolbar_menu_item);
		// ���ñ���
		toolbarGrid.setBackgroundResource(R.drawable.menu_bg2);
		// ����ÿ������
		toolbarGrid.setNumColumns(5);
		// λ�þ���
		toolbarGrid.setGravity(Gravity.CENTER);
		toolbarGrid.setVerticalSpacing(20);// ��ֱ���
		toolbarGrid.setHorizontalSpacing(7);// ˮƽ���
		toolbarGrid.setAdapter(getMenuAdapter(menu_toolbar_name_array,
				menu_toolbar_image_array));// ���ò˵�Adapter
		toolbarGrid.setOnItemClickListener(new OnItemClickListener() {
			//void onItemClick(AdapterView<?> parent, View view, int position, long id);
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case TOOLBAR_ITEM_KEEPINTIME:
					Intent intent1 = new Intent();
					intent1.setClass(mainPage.this, keepInTime.class);
//					�ⲿ�����ڲ����е��÷���
					mainPage.this.startActivity(intent1);

					break;
				case TOOLBAR_ITEM_NETLIST:
					Intent intent5 = new Intent();
					intent5.setClass(mainPage.this, MainActivity.class);
					mainPage.this.startActivity(intent5);

					break;
				case TOOLBAR_ITEM_STATIST:
					Intent intent2 = new Intent();
					intent2.setClass(mainPage.this, statist.class);
					mainPage.this.startActivity(intent2);

					break;
				case TOOLBAR_ITEM_SETTING:
					Intent intent3 = new Intent();
					intent3.setClass(mainPage.this, setting.class);
					mainPage.this.startActivity(intent3);

					break;
				case TOOLBAR_ITEM_ABOUT:
					Intent intent4 = new Intent();
					intent4.setClass(mainPage.this, aboutus.class);
					mainPage.this.startActivity(intent4);

					break;

			
				}
			}
		});
		
    }
    public void onResume(){
    	super.onResume();
    }


	
	 private SimpleAdapter getMenuAdapter(String[] menuNameArray,
				int[] imageResourceArray) {
			ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < menuNameArray.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("itemImage", imageResourceArray[i]);
				map.put("itemText", menuNameArray[i]);
				data.add(map);
			}
			SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
					R.layout.item_menu, new String[] { "itemImage", "itemText" },
					new int[] { R.id.item_image, R.id.item_text });
			return simperAdapter;
		}
	 
	 static long waitTime = 2000;  
	 static long touchTime = 0;  
	   
	 @Override  
	 //�ٰ�һ���˳�
	 public boolean onKeyDown(int keyCode, KeyEvent event) {  
		 /**
		     * {@link #getAction} value: the key has been pressed down.
		     */
		 /** Key code constant: Back key. */
	     if(keyCode == KeyEvent.KEYCODE_BACK) {  
	         long currentTime = System.currentTimeMillis();  
	         if((currentTime-touchTime)>=waitTime) {  
	        	 
	             Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT).show();  
	             touchTime = currentTime;  
	         }else { 
	        	 Intent serviceStop = new Intent();
				 serviceStop.setClass(mainPage.this,Speed.class);
				  stopService(serviceStop);
	              Intent out=new Intent(Intent.ACTION_MAIN);
	              out.addCategory(Intent.CATEGORY_HOME);
	              mainPage.this.startActivity(out);
	             
	         }  
	        
	     }  
	    return true;
	     //return super.onKeyDown(keyCode, event);  
	 }  
	


}
