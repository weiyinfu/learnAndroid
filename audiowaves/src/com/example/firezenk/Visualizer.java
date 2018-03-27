package com.example.firezenk;

import java.util.ArrayList;
import java.util.Random;

import com.example.audiowaves.R;
import com.example.randomColor.RandomColor;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
 
public class Visualizer extends LinearLayout {
	private static int FREQUENCY = 16000;
	private static int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
	private static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static int bufferSize = AudioRecord.getMinBufferSize(FREQUENCY,
			CHANNEL, ENCODING); 
	private volatile boolean setToStopped = false;
    private int FORMAT = 2;
 
    WindowManager wm = (WindowManager) getContext()
    	    .getSystemService(Context.WINDOW_SERVICE);
    	    int width = wm.getDefaultDisplay().getWidth();
    
    private int VISUALIZER_NUM_WAVES = 5;
    private int VISUALIZER_GRAVITY = 0;
    private int LINE_WIDTH = width/8;
    private int LINE_MIN_WIDTH=LINE_WIDTH;
    private int LINE_HEIGHT = LINE_WIDTH*2; 
    private int LINE_MIN_HEIGHT=LINE_HEIGHT;
    private int LINE_SPACING = LINE_WIDTH/4;
    private int LINE_BORDER_RADIUS = LINE_SPACING*2;
    private int BALL_DIAMETER =80;
    private int COLOR_UNIFORM = android.R.color.holo_green_dark;
     
    private Context context;
    private Random randomNum = new Random();
    private Handler uiThread ;
    private LinearLayout.LayoutParams params ;
    private ArrayList<View> waveList = new ArrayList<View>();
    MyThread thread=new  MyThread();
    
    public Visualizer(Context context) {
        super(context);
        this.context = context;
        if(!isInEditMode()) {
            this.init();
        }
    }
    public Visualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        if(!isInEditMode()){
            this.attributes(attrs);
            this.init();
        }
    }

    public Visualizer(Context context, AttributeSet attrs, int defStyleAttr){
            super(context, attrs, defStyleAttr);
        this.context = context;

        if(!isInEditMode()){
            this.attributes(attrs);
            this.init();
        }
    }

    private void attributes(AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.audiowaves__style,
                0, 0);
        try {
            FORMAT = a.getInteger(R.styleable.audiowaves__style_aw_format, FORMAT);
              VISUALIZER_NUM_WAVES = a.getInteger(R.styleable.audiowaves__style_aw_num_waves, VISUALIZER_NUM_WAVES);
            VISUALIZER_GRAVITY = a.getInteger(R.styleable.audiowaves__style_aw_gravity, VISUALIZER_GRAVITY);
            LINE_WIDTH = a.getInteger(R.styleable.audiowaves__style_aw_line_with, LINE_WIDTH);
            LINE_MIN_WIDTH = a.getInteger(R.styleable.audiowaves__style_aw_line_min_with, LINE_MIN_WIDTH);
            LINE_HEIGHT = a.getInteger(R.styleable.audiowaves__style_aw_line_height, LINE_HEIGHT);
            LINE_MIN_HEIGHT = a.getInteger(R.styleable.audiowaves__style_aw_line_min_height, LINE_MIN_HEIGHT);
            LINE_SPACING = a.getInteger(R.styleable.audiowaves__style_aw_line_spacing, LINE_SPACING);
            LINE_BORDER_RADIUS = a.getInteger(R.styleable.audiowaves__style_aw_line_border_radius, LINE_BORDER_RADIUS);
            BALL_DIAMETER = a.getInteger(R.styleable.audiowaves__style_aw_ball_diameter, BALL_DIAMETER);
            COLOR_UNIFORM = a.getColor(R.styleable.audiowaves__style_aw_color_uniform, getResources().getColor(COLOR_UNIFORM));
            } finally {
            a.recycle();
        }
    }
    private void init() {
        this.setLayoutParams(
                new LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        switch (FORMAT) {
            case 0:
            case 1:
                this.setOrientation(VERTICAL);
                break;
            case 2:
            case 3:
                this.setOrientation(HORIZONTAL);
                break;
        }

        switch (VISUALIZER_GRAVITY) {
            case 0:
                this.setGravity(Gravity.CENTER);
                break;
            case 1:
                this.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
                break;
            case 2:
                this.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
                break;
            case 3:
                this.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);
                break;
            case 4:
                this.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
                break;
        }

        this.addWaves();
        this.prepare();
    }

    private void addWaves() {
    	  params = new LinearLayout.LayoutParams(LINE_MIN_WIDTH, LINE_MIN_HEIGHT);
    	  params.setMargins(LINE_SPACING, 0, LINE_SPACING, 0);

        for (int i = 0; i < VISUALIZER_NUM_WAVES; i++) {
            View v = new View(context);
            v.setLayoutParams(params);
            this.setBackground(v);
            waveList.add(v);
            this.addView(v);
        }
    }
    
    private void setBackground(View v) {
        if (android.os.Build.VERSION.SDK_INT < 16)
            setBackgroundAPI15(v);
        else
            setBackgroundAPI16(v);
    }

    @TargetApi(15) private void setBackgroundAPI15(View v) {
        GradientDrawable gd = null;
        RandomColor rc=new RandomColor(); 
        int color=rc.randomColor();
        gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, color});        
        gd.setCornerRadius(LINE_BORDER_RADIUS);
        gd.setGradientRadius(90.f);
        v.setBackgroundDrawable(gd);
    }

    @TargetApi(16) private void setBackgroundAPI16(View v) {
        GradientDrawable gd = new GradientDrawable();       
        gd.setCornerRadius(LINE_BORDER_RADIUS);
        gd.setGradientRadius(90.f);
        RandomColor rc=new RandomColor(); 
        int color=rc.randomColor();
        gd.setColors(new int[]{color, color});    
        v.setBackground(gd);
    }
  
    private double calculateVolume(short[] buffer){	
		double sumVolume = 0.0;
		double avgVolume = 0.0;
		double volume = 0.0;
		for(short b : buffer){
			sumVolume += Math.abs(b);
		}
		avgVolume = sumVolume / buffer.length;	
		volume = Math.log10(1 + avgVolume) * 10;		
		return volume;
	}
    private void prepare() {   	
    	uiThread=new Handler(){
    		public void handleMessage(Message msg){
    			super.handleMessage(msg);
    			final int pitchInHz=msg.getData().getInt("volume");       			 
    			uiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        int pitch =  pitchInHz > 0 ? (int) pitchInHz : 1;
                        pitch=pitch*LINE_HEIGHT/13;
                        for (int i = 0; i < waveList.size(); i++) {
                            int random = randomNum.nextInt(10 - 8) + 2;
                            int size = pitch/random;                                           
                            switch (FORMAT) {
                            case 0:
                                params = new LinearLayout.LayoutParams(
                                        size < LINE_MIN_WIDTH ? LINE_MIN_WIDTH : size,
                                        LINE_HEIGHT);
                                params.setMargins(0, LINE_SPACING, 0, LINE_SPACING);
                                break;
                            case 1:
                                params = new LinearLayout.LayoutParams(
                                        size < LINE_MIN_WIDTH ? LINE_MIN_WIDTH : size/2,
                                        size < LINE_MIN_HEIGHT ? LINE_MIN_HEIGHT : size/2);
                                params.setMargins(0, LINE_SPACING, 0, LINE_SPACING);
                                break;
                            case 2:
                                params = new LinearLayout.LayoutParams(
                                        LINE_WIDTH,
                                        size < LINE_MIN_HEIGHT ? LINE_MIN_HEIGHT : size);
                                params.setMargins(LINE_SPACING, 0, LINE_SPACING, 0);
                                break;
                            case 3:
                                params = new LinearLayout.LayoutParams(
                                        size < LINE_MIN_WIDTH ? LINE_MIN_WIDTH : size/2,
                                        size < LINE_MIN_HEIGHT ? LINE_MIN_HEIGHT : size/2);
                                params.setMargins(LINE_SPACING, 0, LINE_SPACING, 0);
                                break;
                            }
                            waveList.get(i).setLayoutParams(params);
                        }
                    }
                });	    			
    		}
    	};	 	 
    }  
    public void startListening() {
        thread.start();
    }
    public void stopListening() {
        thread.interrupt();     
    }
    class MyThread extends Thread{	    	
		@Override	    		
		public void run()
		{
			AudioRecord audioRecord = null;
			try {
				short[] buffer = new short[bufferSize];
				audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
						FREQUENCY, CHANNEL, ENCODING, bufferSize);
				int state = audioRecord.getState();
				if (state == AudioRecord.STATE_INITIALIZED) {
					audioRecord.startRecording();
					while (!setToStopped) {
						int len = audioRecord.read(buffer, 0, buffer.length);
						short[] data = new short[len];    					 
						System.arraycopy(buffer, 0, data, 0, len);
						double volume = calculateVolume(data);
						Log.v("volume", "volume=" + volume);	
						Message Msg=uiThread.obtainMessage();
						Msg.what=1;
					    Bundle data1=new Bundle();
					    data1.putInt("volume", (int) volume);
						Msg.setData(data1);
						uiThread.sendMessage(Msg);   						
					}
					audioRecord.stop();
				}
			} catch (Exception e) {
			} finally {
				audioRecord.release();
				audioRecord = null;
			}
		}		
	}
}
