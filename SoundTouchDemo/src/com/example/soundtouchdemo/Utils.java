package com.example.soundtouchdemo;

public class Utils {

	
	
	/*public static RecordingText convert(String s){
		
		RecordingText text = new RecordingText();
		
		int index = s.indexOf(" ");
		text.fileNum = s.substring(0, index);
		
		// 后面的内容
		String content = s.substring(index+1, s.length());
		text.fileContent = content.replaceAll("\t", "\n");
				
		return text;
	}*/
	
	
	public static byte[] shortToByteSmall(short[] buf){
		
		byte[] bytes = new byte[buf.length * 2];
		for(int i = 0, j = 0; i < buf.length; i++, j+=2){
			short s = buf[i];
			
			byte b1 = (byte) (s & 0xff);
			byte b0 = (byte) ((s >> 8) & 0xff);
			
			bytes[j] = b1;
			bytes[j+1] = b0;
		}
		return bytes;
		
	}
	
}
