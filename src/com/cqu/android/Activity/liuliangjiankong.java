package com.cqu.android.Activity;


import com.cqu.android.Activity.R;
import com.cqu.android.allservice.monitoring.MonitoringService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
//������
public class liuliangjiankong extends Activity {
    /** Called when the activity is first created. */
	//���Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        //������ط���
        Intent intent = new Intent() ;
        intent.setClass(liuliangjiankong.this, MonitoringService.class);
        this.startService(intent);
        //�������������
        new Handler().postDelayed(new Runnable() {

			public void run() {
				
				Intent intent = new  Intent(liuliangjiankong.this,mainPage.class);
				liuliangjiankong.this.startActivity(intent);
				liuliangjiankong.this.finish();
			}
			
			
		}, 3000);
        
    }
}