package weidiao.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;


public class MainActivity extends Activity {
    TextView textView;
    Button button;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://www.baidu.com");
                            URLConnection connection = url.openConnection();
                            Scanner cin = new Scanner(connection.getInputStream());
                            StringBuilder builder=new StringBuilder();
                            while (cin.hasNextLine()) {
                                builder.append(cin.nextLine()).append("\n");
                            }
                            final String s = builder.toString();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(s);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
