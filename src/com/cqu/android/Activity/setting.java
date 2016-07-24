package com.cqu.android.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cqu.android.allservice.monitoring.FloatService;
import com.cqu.android.db.DatabaseAdapter;


public class setting extends Activity{
	
	private View View_quota;
	private View View_left;
	private View View_clear;
	private View View_date;
	private EditText dt;
	private EditText dt1;
	private EditText dt2;
	private DatabaseAdapter dbAdapter;
	public static String mLeft,mCountDate,mLimit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		setTitle("СYϵͳ����");
		dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
     
	}
	public void onResume() {
		super.onResume();
		setContentView(R.layout.setup);
		ToggleButton isWindow;
		ToggleButton isWarn;
		/**
		 * Displays checked/unchecked states as a button
		 * with a "light" indicator and by default accompanied with the text "ON" or "OFF".
		 */
        //��������
		isWindow = (ToggleButton) findViewById(R.id.ToggleButton1);
		
		String window=getSharedPreferences("Settings", 0).getString(
				"Window", "0");
		final TextView is_Window = (TextView)findViewById(R.id.is_statist);
		
		
		if(window.equals("1"))
		{
			is_Window.setText("���������ѿ���");
		}
		else
		{
			is_Window.setText("���������ѹر�");
		}
		
		// �����������ڹ����Ƿ���   Boxstate����Ƿ�Ϊ1
		isWindow.setChecked(this.BoxState(window));
		isWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							
							final Editor editor = getSharedPreferences("Settings", 0).edit();
							editor.putString("Window","1");
							editor.commit();
		                    Toast.makeText(setting.this,"���������ѿ���",Toast.LENGTH_SHORT).show();
							is_Window.setText("���������ѿ���");
							Intent service = new Intent();
			        		service.setClass(setting.this, FloatService.class);		
			        		startService(service);
						} else {
							
							final Editor editor = getSharedPreferences("Settings", 0).edit();
							editor.putString("Window","0");
							editor.commit();
		                    Toast.makeText(setting.this,"���������ѹر�",Toast.LENGTH_SHORT).show();
							is_Window.setText("���������ѹر�");
							Intent serviceStop = new Intent();
			        		serviceStop.setClass(setting.this, FloatService.class);
			        		stopService(serviceStop);
						}
					}
				});

		
		
		//����������ʾ
		 isWarn = (ToggleButton) findViewById(R.id.ToggleButton2);
		// ����������ʾ�����Ƿ���
		final TextView is_warn = (TextView)findViewById(R.id.is_warn);
		String mWarn = getSharedPreferences("Settings", 0).getString(
					"mWarn", "0");
		//��ʼ��is_warn��ֵ
		if(mWarn.equals("1"))
		{
			is_warn.setText("������ʾ�����ѿ���");
		}
		else
		{
			is_warn.setText("������ʾ�����ѹر�");
		}
		
		isWarn.setChecked(this.BoxState(mWarn));
		isWarn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
				
						// TODO Auto-generated method stub
						if (isChecked) {
							
							final Editor editor = getSharedPreferences("Settings", 0).edit();
							editor.putString("mWarn","1");
							editor.commit();
		                    Toast.makeText(setting.this,"������ʾ�����ѿ���",Toast.LENGTH_SHORT).show();
							is_warn.setText("������ʾ�����ѿ���");
						} else {
							
							final Editor editor = getSharedPreferences("Settings", 0).edit();
							editor.putString("mWarn","0");
							editor.commit();
							
							is_warn.setText("������ʾ�����ѹر�");
						}
					}
				});
        //ÿ�������޶�
		ImageView imgView1 = (ImageView) this.findViewById(R.id.imageButton_quota);
		final TextView limit_flow = (TextView)findViewById(R.id.limit_flow);
		//ÿ��ʣ����������
		ImageView imgView4 = (ImageView) this.findViewById(R.id.imageButton_left);
		final TextView flow_remind = (TextView)findViewById(R.id.flow_remind);
		//ÿ�½�����
		ImageView imgView2 = (ImageView) this.findViewById(R.id.imageButton_date);
		final TextView count_date = (TextView)findViewById(R.id.count_date);
		//�������
		ImageView imgView5 = (ImageView) this.findViewById(R.id.imageButton_clear);
		final TextView clear_data = (TextView)findViewById(R.id.clear_data);
		
		
		
		
		
		
		//��ʼ��limit_flow��ֵ
		
		mLimit = getSharedPreferences("Settings", 0).getString(
				"mLimit", "0");
		if(mLimit.equals("0")){
		   limit_flow.setText("ÿ�������޶�Ϊ"+30+"MB");
		}else{
			limit_flow.setText("ÿ�������޶�Ϊ"+mLimit+"MB");
		}
        // ����ÿ�������޶�
		imgView1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
			    /*
				 * Instantiates a layout XML file into its corresponding {@link android.view.View}
				 * objects. It is never used directly. */
                //����xml�ļ�
				LayoutInflater factory = LayoutInflater.from(setting.this);
				View_quota = factory.inflate(R.layout.setup_quota, null);

				new AlertDialog.Builder(setting.this).setTitle("ÿ�������޶�").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_quota)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										
										final Editor editor = getSharedPreferences("Settings", 0).edit();
										
										//��ȡ��ӵ��޶�
										dt = (EditText) View_quota
												.findViewById(R.id.quota12);
										String limit = dt.getText().toString();
										//������mLimit�ļ���
										editor.putString("mLimit",limit);
										editor.commit();
										limit_flow.setText("ÿ�������޶�Ϊ"+limit+"MB");
									}

								}).setNegativeButton("ȡ��", null).show();
			}
		});

		
		//��ʼ��count_date��ֵ
		mCountDate = getSharedPreferences("Settings", 0).getString(
				"mDate", "0");
		if(mCountDate.equals("0")){
		  count_date.setText("�½�����Ϊ����"+1+"��,�ڵ�ǰ�������������Ҳ�֧�ָ���");
		}else{
		  count_date.setText("�½�����Ϊ����"+mCountDate+"��");
		}
		//�����½�����
		imgView2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				LayoutInflater factory = LayoutInflater.from(setting.this);
				View_date = factory.inflate(R.layout.setup_date, null);

				new AlertDialog.Builder(setting.this).setTitle("�½�����").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_date)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									
//										final Editor editor = getSharedPreferences("Settings", 0).edit();
//										dt2 = (EditText) View_date
//												.findViewById(R.id.setup_date);
//										String date = dt2.getText().toString();
//										if(Integer.valueOf(date)<1||Integer.valueOf(date)>31)
//										{
//											Toast.makeText(setting.this, "������1~31�����֣�", Toast.LENGTH_SHORT).show();
//											
//										}
//										else{
//																				
//					                    editor.putString("mDate",date);
//										editor.commit();
//										count_date.setText("�½�����Ϊ����"+date+"��");
//										}
									}

								}).setNegativeButton("ȡ��", null).show();
			}
		});

		
		//��ʼ��ÿ��ʣ����������
		mLeft = getSharedPreferences("Settings", 0).getString(
				"mLeft", "0");
		if(mLeft.equals("0")){
			flow_remind.setText("ÿ��ʣ������Ϊ"+27+"MBʱ������");	
		}else{
		flow_remind.setText("ÿ��ʣ������Ϊ"+mLeft+"MBʱ������");
		}
		//����ÿ��ʣ����������
		imgView4.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater.from(setting.this);
				View_left = factory.inflate(R.layout.setup_left, null);

				new AlertDialog.Builder(setting.this).setTitle("ÿ��ʣ����������").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_left)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										
										// TODO Auto-generated method stub
									
										final Editor editor = getSharedPreferences("Settings", 0).edit();
										dt1 = (EditText) View_left
												.findViewById(R.id.setup_left);
										String strLeft = dt1.getText().toString();
										mLimit=getSharedPreferences("Settings", 0).getString(
												"mLimit", "0");
										//ʣ�����������ò���da�������޶�
										if(Integer.valueOf(strLeft) > Integer.valueOf(mLimit))
										{
											Toast.makeText(setting.this, "ʣ�����������ò��ܴ��������޶�", Toast.LENGTH_SHORT).show();
											
										//	finish();
										}
										else{
									    editor.putString("mLeft",strLeft);
										editor.commit();
										flow_remind.setText("ÿ��ʣ������Ϊ"+strLeft+"MBʱ������");
										}
									}

								}).setNegativeButton("ȡ��", null).show();
			}
		});
		
		
		//�������ͳ������
		imgView5.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater.from(setting.this);
				View_clear = factory.inflate(R.layout.setup_clear, null);

				new AlertDialog.Builder(setting.this).setTitle("��ȷ��Ҫ�������ͳ�Ƽ�¼��").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_clear)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										
										// ɾ����
										dbAdapter.clear();
										clear_data.setText("���������");
										Toast.makeText(setting.this,"���������",Toast.LENGTH_SHORT).show();
									}

								}).setNegativeButton("ȡ��", null).show();
			}
		});
		
		
		
		



	}
    //����ַ����Ƿ�Ϊ1
	public boolean BoxState(String s) {
		if (s.equals("")) {
			return false;
		} else if (Integer.parseInt(s) == 1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// ���¼����Ϸ��ذ�ť

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent1 = new Intent();
			intent1.setClass(setting.this, mainPage.class);
			startActivity(intent1);
			setting.this.finish();
		}
		return true;
	}			
	
}

