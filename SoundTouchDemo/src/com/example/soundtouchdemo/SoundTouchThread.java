package com.example.soundtouchdemo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

public class SoundTouchThread extends Thread {

	private BlockingQueue<short[]> recordQueue;	
	private Handler handler;	
	private static final long TIME_WAIT_RECORDING = 100;
	private volatile boolean setToStopped = false;
	private JNISoundTouch soundtouch = new JNISoundTouch();
	private LinkedList<byte[]> wavDatas = new LinkedList<byte[]>();
	
	public SoundTouchThread(Handler handler, BlockingQueue<short[]> recordQueue){
		this.handler = handler;
		this.recordQueue = recordQueue;
	}
	
	public void stopSoundTounch(){
		setToStopped = true;
	}
	
	
	@Override
	public void run() {
		
		
		soundtouch.setSampleRate(16000);
		soundtouch.setChannels(1);
		soundtouch.setPitchSemiTones(10);
		soundtouch.setRateChange(-0.7f);
		soundtouch.setTempoChange(0.5f);
		
		wavDatas.clear();
		
		short[] recordingData;		
		while(true)	{	
			
			try{
				recordingData = recordQueue.poll(TIME_WAIT_RECORDING, TimeUnit.MILLISECONDS);
				if(recordingData != null){
					
					soundtouch.putSamples(recordingData, recordingData.length);
					
					short[] buffer;
					do {
						
						buffer = soundtouch.receiveSamples();
						byte[] bytes = Utils.shortToByteSmall(buffer);					
						wavDatas.add(bytes);
						
					} while (buffer.length > 0);
					
				}
				
				if(setToStopped	&& recordQueue.size()==0) {
					break;
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		int fileLength = 0;
		for(byte[] bytes: wavDatas){
			fileLength += bytes.length;
		}
		
		try {
			
			WaveHeader header = new WaveHeader(fileLength);
			byte[] headers = header.getHeader();	
			
			// 保存文件
			FileOutputStream out = new FileOutputStream(Settings.recordingPath  + "soundtouch.wav");
			out.write(headers);
			
			for(byte[] bytes: wavDatas){
				out.write(bytes);
			}
			
			out.close();
			handler.sendEmptyMessage(Settings.MSG_FILE_SAVE_SUCCESS);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
		}
		
	}
}
