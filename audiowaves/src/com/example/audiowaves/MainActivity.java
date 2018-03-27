package com.example.audiowaves;

 
import com.example.firezenk.Visualizer;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {
    
    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Visualizer a=new Visualizer(MainActivity.this);   
        a=(Visualizer)findViewById(R.id.visualizer);  
       
        a.startListening();
   
     //  ((Visualizer) findViewById(R.id.visualizer)).startListening();
    }

}