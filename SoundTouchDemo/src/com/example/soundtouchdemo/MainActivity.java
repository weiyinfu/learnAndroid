package com.example.soundtouchdemo;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private Button startBtn;
	private Button stopBtn;
	private Button playBtn;
	
	private SoundTouchClient soundTouchClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startBtn = (Button) findViewById(R.id.start_btn);
		startBtn.setOnClickListener(this);

		stopBtn = (Button) findViewById(R.id.stop_btn);
		stopBtn.setOnClickListener(this);

		playBtn = (Button) findViewById(R.id.play_btn);
		playBtn.setOnClickListener(this);
		
		soundTouchClient = new SoundTouchClient(handler);
		
		// 检查sdcard状态
        String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			
			File yzsPath = new File(Settings.recordingPath);
	        if(!yzsPath.isDirectory()){
	        	yzsPath.mkdir();
	        }
	        
	    }else{
	    	Toast.makeText(this, "sdcard error!", 0).show();
	    }
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_btn:
			
			startBtn.setVisibility(View.INVISIBLE);
			stopBtn.setVisibility(View.VISIBLE);
			playBtn.setVisibility(View.INVISIBLE);
			
			soundTouchClient.start();
			break;

		case R.id.stop_btn:
			
			startBtn.setVisibility(View.INVISIBLE);
			stopBtn.setVisibility(View.INVISIBLE);
			playBtn.setVisibility(View.VISIBLE);
			
			soundTouchClient.stop();
			break;
			
		case R.id.play_btn:
			
			
			break;
		}
	}
	
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			
			startBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.INVISIBLE);
			playBtn.setVisibility(View.INVISIBLE);
			
		}
	};

}
