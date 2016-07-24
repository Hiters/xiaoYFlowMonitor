package com.cqu.android.allservice.monitoring;

import java.math.BigDecimal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

public class TrafficMonitoring {
	Context context;
	ConnectivityManager cm ;
	NetworkInfo nwi;
	long lastTraffic = 0;
	long currentTraffic;

	// ���캯��
	public TrafficMonitoring() {
	}
	/**Context-------
	 * Interface to global information about an application environment.  This is
	 * an abstract class whose implementation is provided by
	 * the Android system.  It
	 * allows access to application-specific resources and classes, as well as
	 * up-calls for application-level operations such as launching activities,
	 * broadcasting and receiving intents, etc.
	 */

	public TrafficMonitoring(Context context) {
		this.context = context;
		/**
	     * Return the handle to a system-level service by name. The class of the
	     * returned object varies by the requested name. Currently available names
	     * are:
	     */
		
		cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);	
		
		/**
	     * Returns details about the currently active data network. When connected,
	     * this network is the default route for outgoing connections. You should
	     * always check {@link NetworkInfo#isConnected()} before initiating network
	     * traffic. This may return {@code null} when no networks are available.
	     * <p>This method requires the caller to hold the permission
	     * {@link android.Manifest.permission#ACCESS_NETWORK_STATE}.
	     */
		nwi = cm.getActiveNetworkInfo();
	}

	// ��ȡ��ǰ�ֻ����������ͣ�����String
	public int getNetType() {
		if(nwi != null){
			 /**
		     * Return a human-readable name describe the type of the network,
		     * for example "WIFI" or "MOBILE".
		     * @return the name of the network type
		     */
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

	// ��ѯ�ֻ�������
	public static long traffic_Monitoring() {
		long recive_Total = TrafficStats.getTotalRxBytes();
		long send_Total = TrafficStats.getTotalTxBytes();
		long total = recive_Total + send_Total;
		return total;
	}

	//��ѯ�ֻ���Mobile��������
	public static long mReceive(){
		return  TrafficStats.getMobileRxBytes();
	}
	
	//��ѯ�ֻ���Mobile��������
	public static long mSend(){
		return  TrafficStats.getMobileTxBytes();
	}
	
	//��ѯ�ֻ���WIFI��������
	public static long wSend(){
		return  TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes();
	}
	
	//��ѯ�ֻ�Wifi����������
	public static long wReceive(){
		return TrafficStats.getTotalTxBytes() - TrafficStats.getMobileRxBytes();
	}

	// ��ѯĳ��Uid������ֵ
	public static long monitoringEachApplicationReceive(int uid) {
		return TrafficStats.getUidRxBytes(uid);
	}

	// ��ѯĳ��Uid������ֵ
	public static long monitoringEachApplicationSend(int uid) {
		return TrafficStats.getUidTxBytes(uid);
	}

	// ����ת��
	public static String convertTraffic(long traffic) {
		BigDecimal trafficKB;
		BigDecimal trafficMB;
		BigDecimal trafficGB;
		
	/**
		 * This class represents immutable(���ɸı�) integer numbers of arbitrary�����⣩ length. Large
		 * numbers are typically used in security applications and therefore BigIntegers
		 * offer dedicated functionality��ר�õĹ��ܣ� like the generation of large prime numbers������������ or
		 * the computation of modular inverse��ģ�������㣩.
		 */

		BigDecimal temp = new BigDecimal(traffic);
		BigDecimal divide = new BigDecimal(1000);
//  ctrl+/   Returns a new {@code BigDecimal} whose value is {@code this / divisor}. 
//  ����divisor  scale����2λ    ����ģʽ
		/**
	     * Rounding mode where the values are rounded towards zero.
	     *
	     * @see RoundingMode#DOWN      public static final int ROUND_DOWN = 1;
	     * 
	     * ROUND_HALF_UP: ����.5�����ʱ���Ͻ���,��: 1.5 ->;2
           ROUND_HALF_DOWN : ����.5�����ʱ���½���,��: 1.5 ->;1

                BigDecimal a = new BigDecimal(1.5);
                System.out.println("down="+a.setScale(0,BigDecimal.ROUND_HALF_DOWN)+"/tup="+a.setScale(0,BigDecimal.ROUND_HALF_UP));
                                              ���:down=1  up=2
                                            ��������Ӿ�������!

                                           ��������˵��

 

ROUND_CEILING     
  ���   BigDecimal   �����ģ�����   ROUND_UP   ���������Ϊ��������   ROUND_DOWN   ������     
  ROUND_DOWN     
  �Ӳ�������(���ض�)��С��֮ǰ�������֡�     
  ROUND_FLOOR     
  ���   BigDecimal   Ϊ��������   ROUND_UP   �����Ϊ��������   ROUND_DOWN   ��     
  ROUND_HALF_DOWN     
  ����������>   .5������   ROUND_UP��������   ROUND_DOWN   ��     
  ROUND_HALF_EVEN     
  �������������ߵ�����Ϊ����������   ROUND_HALF_UP   �������Ϊż��������   ROUND_HALF_DOWN   ��     
  ROUND_HALF_UP     
  ����������>=.5������   ROUND_UP   ��������   ROUND_DOWN   ��     
  ROUND_UNNECESSARY     
  �á�α����ģʽ��ʵ����ָ����Ҫ��Ĳ��������Ǿ�ȷ�ģ�����˲���Ҫ���������     
  ROUND_UP     
  �����ڷ�   0   ����С��(���ض�)֮ǰ�������֡�     
	     */
	   
		trafficKB = temp.divide(divide, 2, 1);
		//��divide����   �����㷵��1  �����㷵��0 С���㷵��-1
		//��1000���Դ���1000���������ֱ��С��1000   �ֳ�GB MB KB
		if (trafficKB.compareTo(divide) > 0) {
			trafficMB = trafficKB.divide(divide, 2, 1);
		} else {
			return trafficKB.doubleValue()+"KB";
		}
			
		if (trafficMB.compareTo(divide) > 0) {
				trafficGB = trafficMB.divide(divide, 2, 1);
				return trafficGB.doubleValue()+"GB";
		} else {
				
			return trafficMB.doubleValue()+"MB";
		}
		
	}
}
