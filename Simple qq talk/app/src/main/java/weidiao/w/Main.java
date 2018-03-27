package weidiao.w;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Scanner;


public class Main extends Activity {
    TextView talked;
    EditText saying;
    Button sendButton, refreshButton;
    ScrollView scrollView;
    double id;
    String ip = "http://182.254.156.22:8080/haha/Haha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = Math.random();
        setContentView(R.layout.main);
        talked = (TextView) findViewById(R.id.talked);
        saying = (EditText) findViewById(R.id.saying);
        sendButton = (Button) findViewById(R.id.send_button);
        refreshButton = (Button) findViewById(R.id.refresh);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saying.getText().length() == 0) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cout(saying.getText().toString());
                        refresh();
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }).start();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String newWords = msg.getData().getString("data");
                    if (newWords.length() == 0) return;
                    talked.setText(newWords + talked.getText());
                    break;
                case 1:
                    saying.setText("");
                    break;
            }

        }
    };

    void refresh() {
        Message msg = new Message();
        msg.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString("data", cin());
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    String cin() {
        try {
            URLConnection connection = new URL(ip + "?id=" + id).openConnection();
            Scanner cin = new Scanner(connection.getInputStream());
            String s = "";
            while (cin.hasNext()) {
                s += URLDecoder.decode(cin.next(), "utf-8") + "\n";
            }
            cin.close();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void cout(String s) {
        try {
            URLConnection connection = new URL(ip).openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            PrintWriter cout = new PrintWriter(connection.getOutputStream());
            cout.print(
                    String.format("id=%s&say=%s", URLEncoder.encode(id + "", "utf-8"), URLEncoder.encode(s, "utf-8")));
            cout.close();
            connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
