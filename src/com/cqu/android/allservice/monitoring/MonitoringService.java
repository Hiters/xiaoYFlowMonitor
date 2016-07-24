package com.cqu.android.allservice.monitoring;


import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.cqu.android.Activity.R;
import com.cqu.android.Activity.mainPage;
import com.cqu.android.db.DatabaseAdapter;

public class MonitoringService extends Service {

	private DatabaseAdapter dbAdapter;
	private Handler handler = new Handler() ;
	private long mobileRx = 0 , mobileTx = 0  ,wifiRx = 0 ,wifiTx = 0;
	private static long old_mobileRx = 0 ,old_mobileTx = 0  ,old_wifiRx = 0, old_wifiTx= 0,totalRx = 0 , totalTx = 0 ;
	private long mrx = 0,mtx = 0 , wrx = 0 ,wtx = 0 ;
	private static long mobileRx_all= 0 ,mobileTx_all= 0 ,wifiRx_all = 0,wifiTx_all = 0 ;
	int threadNum; // �߳���
	private static int count = 1;
	public static final String TAG="TRAFFIC";
	NetworkInfo nwi;
	Notification notification;
	Intent notificationIntent;
	PendingIntent pendingIntent;
	private ConnectivityManager connManager; 
	private Calendar currentCa;
	private static boolean float_open=false;
	//bindServicer()����� Service �͵��� Service �Ŀͻ���������������������ͻ��౻���٣�
	//Service Ҳ�ᱻ���١������������һ���ô��ǣ�bindService()
	//����ִ�к� Service ��ص��ϱ��ᵽ�� onBind() ������
	//����Դ����ﷵ��һ��ʵ���� IBind �ӿڵ��࣬�ڿͻ��˲����������ܺ��������ͨ����
	//������õ� Service ���е�״̬��������������� Service ��û�����У�
	//ʹ������������� Service �ͻ� onCreate() ������������� onStart()��
	public IBinder onBind(Intent intent) {
		return null;
	}
	//ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); 

	public void onCreate() {
		 connManager = (ConnectivityManager)
					this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    //3G״̬��������
		old_mobileRx = TrafficStats.getMobileRxBytes();
		old_mobileTx = TrafficStats.getMobileTxBytes();

		// ��ȡȫ��������ա�������������
		totalRx = TrafficStats.getTotalRxBytes();
		totalTx = TrafficStats.getTotalTxBytes();
		if(isWifiConnected()){
		// ����WiFi������ա�������������
		old_wifiRx = totalRx - old_mobileRx;
		old_wifiTx = totalTx - old_mobileTx;	
		}else{
			old_wifiRx = 0;
			old_wifiTx = 0;
			
		}
		
		handler.postDelayed(thread, 1000);//���Ӧ���ǿ�������̵߳�Ŷ��
		
		
		
	    notification = new Notification(R.drawable.notificatonicon, getText(R.string.service_notification),
		      System.currentTimeMillis());
	    /* notificationIntent = new Intent(this, mainPage.class);
		 pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		 notification=new Notification.Builder(this).addAction(R.drawable.notificatonicon,getText(R.string.service_notification), pendingIntent)
		 .setShowWhen(true).setWhen(System.currentTimeMillis()).build();
		 notification.notify();*/
		
		notificationIntent = new Intent(this, mainPage.class);
		pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.service_notification),
		        getText(R.string.service_notification), pendingIntent);
		startForeground(Notification.FLAG_ONGOING_EVENT, notification);
		
		super.onCreate();
	        
	}
	 public void toggleGprs(boolean isEnable) throws Exception {  
	        Class<?> cmClass = connManager.getClass();  
	        Class<?>[] argClasses = new Class[1];  
	        argClasses[0] = boolean.class;  
	  
	        // ����ConnectivityManager��hide�ķ���setMobileDataEnabled�����Կ����͹ر�GPRS����  
	        Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);  
	        method.invoke(connManager, isEnable);  
	    } 
	    public boolean isMobileConnected() {  
			  
	        NetworkInfo mMobile = connManager  
	                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	  
	        if (mMobile != null) {  
	            return mMobile.isConnected();  
	        }  
	        return false;  
	    }
	    public boolean isWifiConnected() {  
	    	  
	        NetworkInfo mWifi = connManager  
	                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	  
	        if (mWifi != null) {  
	            return mWifi.isConnected();  
	        }  
	  
	        return false;  
	    }  

	


		
		
	
    Runnable thread = new Runnable(){

		public void run() {
			
			dbAdapter = new DatabaseAdapter(MonitoringService.this);
			dbAdapter.open();		
			//��Ҫ���������޶�ʱ�Զ�����,����Ƿ�Ϊ2G��3G״̬
			if(isMobileConnected()){
			 currentCa =  Calendar.getInstance();
			int year = currentCa.get(Calendar.YEAR);
			int month = currentCa.get(Calendar.MONTH)+1;
			String month3GTraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 1));
			double remain,Limit;
			String tempString[];// ��ʱ�洢3G����
			String mLimit=getSharedPreferences("Settings", 0).getString(
					"mLimit", "0");
			if(mLimit.equals("0")){
    			Limit=30.0;
    			
    		}else{
    			Limit=Double.valueOf(mLimit);
    		}
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
			if(remain<0.5){
				try {
					toggleGprs(false);
				} catch (Exception e) {
					Log.i("resume","����");
				}				
			  }
			}
			//����״̬�µļ����������
			if (isMobileConnected()) {
				// ������������
				// ��ȡ�ƶ�������ա�����������������λΪbyte������ͬ��
				mobileRx = TrafficStats.getMobileRxBytes();
				mobileTx = TrafficStats.getMobileTxBytes();
				mrx = (mobileRx - old_mobileRx); // �õ�˲ʱGPRS����
				mtx = (mobileTx - old_mobileTx); // �õ�˲ʱ
				// ����С�������λ
				mrx = (long) ((float) (Math.round(mrx * 100.0)) / 100);
				mtx = (long) ((float) (Math.round(mtx * 100.0)) / 100);
				mobileRx_all += mrx; // ��ͬһ�������֮��
				mobileTx_all += mtx; // ��ͬһ�������֮��
			}
		    
		 	if(isWifiConnected()){
		 		// ��ȡȫ��������ա�������������
				totalRx = TrafficStats.getTotalRxBytes();
				totalTx = TrafficStats.getTotalTxBytes();
				// ��ȡ�ƶ�������ա�����������������λΪbyte������ͬ��
				mobileRx = TrafficStats.getMobileRxBytes();
				mobileTx = TrafficStats.getMobileTxBytes();

			    // ����WiFi������ա�������������
			    wifiRx = totalRx - mobileRx;
			    wifiTx = totalTx - mobileTx;
			 
				wrx = (wifiRx - old_wifiRx); // �õ�˲ʱwifi����
				wtx = (wifiTx - old_wifiTx); // �õ�˲ʱwifi����
				wrx = (long) ((float) (Math.round(wrx * 100.0)) / 100);// ������λС��
				wtx = (long) ((float) (Math.round(wtx * 100.0)) / 100);
				wifiTx_all += wtx; // ��ͬһ�������֮��
				wifiRx_all += wrx; // ��ͬһ�������֮��
			
		    }
			
		
			//ִ��5�θ���һ�����ݿ⣬5�����һ�����ݿ�
			if(count==5){
				Date date = new Date() ;		
				//������ڸ���GPRS�����ļ�¼����±�����¼
				if(mobileTx_all!=0||mobileRx_all!=0){
					Cursor checkMobile = dbAdapter.check(1, date);//1 Ϊ GPRS��������
				  if(checkMobile.moveToNext()){
					long up = dbAdapter.getProFlowUp(1, date);
					long dw = dbAdapter.getProFlowDw(1, date);
					mobileTx_all += up ;
					mobileRx_all += dw ;
					dbAdapter.updateData(mobileTx_all, mobileRx_all, 1, date);
					Log.i(TAG,"upmobile"+up);
					mobileTx_all=0;
					mobileRx_all=0;			
					}
				  else{					
						dbAdapter.insertData(mobileTx_all, mobileRx_all, 1, date);	
						Log.i(TAG, "insert");
					}
				  
				}
				
				if(wifiTx_all!=0 ||wifiRx_all!=0){
					Cursor checkWifi = dbAdapter.check(0, date);//0Ϊ wifi��������
					long up = dbAdapter.getProFlowUp(0, date);
					long dw = dbAdapter.getProFlowDw(0, date);
					if(checkWifi.moveToNext()){
					wifiTx_all += up ;
					wifiRx_all += dw ;
					dbAdapter.updateData(wifiTx_all, wifiRx_all, 0, date);
					wifiTx_all = 0 ;
					wifiRx_all = 0 ;				
					}
					else{					
						dbAdapter.insertData(wifiTx_all, wifiRx_all, 0, date);
						Log.i(TAG, "insert wifi");				
					}
				}
				count = 1 ;
			}
			count++;
			dbAdapter.close();
			handler.postDelayed(thread, 1000);
			old_mobileRx = TrafficStats.getMobileRxBytes();
			old_mobileTx = TrafficStats.getMobileTxBytes();
			// ��ȡȫ��������ա�������������
			totalRx = TrafficStats.getTotalRxBytes();
			totalTx = TrafficStats.getTotalTxBytes();
			old_wifiRx = totalRx - old_mobileRx;
			old_wifiTx = totalTx - old_mobileTx;
		

		
	   }
    	
    };
    
    public int onStartCommand(Intent intent, int flags, int startId) {
		// service�Ѿ����������  ����������������service  ��ִ��onStart��������ִ��onCreate����
		handler.postDelayed(thread,1000);
		/*notificationIntent = new Intent(this, mainPage.class);
		pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification=new Notification.Builder(this).addAction(R.drawable.notificatonicon,getText(R.string.service_notification), pendingIntent)
		.setShowWhen(true).setWhen(System.currentTimeMillis()).build();
		notification.notify();*/
		
		notification = new Notification
		(R.drawable.notificatonicon, getText(R.string.service_notification),
		        System.currentTimeMillis());
		notificationIntent = new Intent(this, mainPage.class);
		pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.service_notification),
		       getText(R.string.service_notification), pendingIntent);
		startForeground(Notification.FLAG_ONGOING_EVENT, notification);
		return super.onStartCommand(intent, flags, startId);
	}
	public static long monitoringEachApplicationReceive(int uid) {
		long   receive=TrafficStats.getUidRxBytes(uid);
		if(receive==-1)receive=0;
	  return receive;
}

    public static long monitoringEachApplicationSend(int uid) {
	long   send=TrafficStats.getUidRxBytes(uid);
		if(send==-1)send=0;
	  return send;
}
    public int getNetType() {
		if(nwi != null){
			String net = nwi.getTypeName();
			if(net.equals("WIFI")){
				return 0;
			}else {
				return 1;
			}
		}else {
			return -1;
		}
	}

	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(thread);
		stopForeground(true);
		Log.v("CountService", "on destroy");
	}
}




