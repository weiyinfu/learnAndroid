package weidiao.multiply2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class Main extends Activity implements SeekBar.OnSeekBarChangeListener {
    String apiId = "8614141";
    String apiKey = "qXUeB1kXyOPgD9mkGCzcnIID";
    String secretKey = "3447f3b010f22ad1eabe54614ef4519d";
    String accessToken = null;
    SharedPreferences sharedPreferences;
    Random random = new Random();
    Handler handler = new Handler();
    int one, two;

    int[] seekBarIds = {R.id.thinkTime, R.id.intervalTime, R.id.pitch, R.id.speed};
    int[] textViewIds = {R.id.thinkTimeTextView, R.id.intervalTimeTextView, R.id.pitchTextView, R.id.speedTextView};
    SeekBar[] seekBarList;
    TextView[] textViewList;

    final int MAX_THINK_TIME = 30000;
    final int MAX_INTERVAL_TIME = 20000;
    final int MAX_SPEED = 5;
    final int MAX_PITCH = 5;
    Thread thread = new Thread() {
        @Override
        public void run() {
            int exceptionCount = 3;
            while (true) {
                try {
                    nextProblem();
                    Thread.sleep(1000 + (long) Math.ceil(seekBarList[0].getProgress() / 100.0 * MAX_THINK_TIME));
                    synthetize(one + "乘以" + two + "等于" + one * two);
                    Thread.sleep(1000 + (long) Math.ceil(seekBarList[1].getProgress() / 100.0 * MAX_INTERVAL_TIME));
                } catch (Exception e) {
                    exceptionCount--;
                    if (exceptionCount == 0) System.exit(0);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main.this, "please check the network", Toast.LENGTH_LONG).show();
                        }
                    });

                    e.printStackTrace();
                    try {
                        Thread.sleep(8000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }
    };
    private PowerManager.WakeLock wakeLock;

    void nextProblem() throws Exception {
        one = Math.abs(random.nextInt()) % 89 + 11;
        two = Math.abs(random.nextInt()) % 89 + 11;
        synthetize(one + "乘以" + two);
    }

    void synthetize(String s) throws UnsupportedEncodingException {
        MediaPlayer player = new MediaPlayer().create(this, Uri.parse(getAudio(s)));
        player.start();
    }

    void saveToken(String s) throws IOException {
        sharedPreferences.edit().putString("accessToken", s).commit();
    }

    void loadToken() throws IOException {
        if (sharedPreferences.getString("accessToken", null) == null) {
            getAccessTocken();
        } else {
            accessToken = sharedPreferences.getString("accessToken", null);
            if (accessToken == null) {
                getAccessTocken();
            }
        }
    }

    // accessTocken有效期默认为一个月
    void getAccessTocken() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://openapi.baidu.com/oauth/2.0/token";
        RequestParams params = new RequestParams();
        params.put("grant_type", "client_credentials");
        params.put("client_id", apiKey);
        params.put("client_secret", secretKey);
        client.post(url, params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(Main.this,response.getString("access_token"),Toast.LENGTH_LONG).show();
                    saveToken(response.getString("access_token"));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.exit(0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sharedPreferences = getSharedPreferences("accessToken", 0);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        seekBarList = new SeekBar[seekBarIds.length];
        textViewList = new TextView[textViewIds.length];
        for (int i = 0; i < seekBarIds.length; i++) {
            seekBarList[i] = (SeekBar) findViewById(seekBarIds[i]);
            textViewList[i] = (TextView) findViewById(textViewIds[i]);
            seekBarList[i].setTag(textViewList[i]);
            seekBarList[i].setProgress(sharedPreferences.getInt("progress" + i, seekBarList[i].getProgress()));
            textViewList[i].append("=" + seekBarList[i].getProgress());
            seekBarList[i].setOnSeekBarChangeListener(this);
        }
        ((Button) findViewById(R.id.stop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        try {
            loadToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        thread.start();
    }

    String getAudio(String text) throws UnsupportedEncodingException {
        int speed = (int) (seekBarList[3].getProgress() / 100.0 * MAX_SPEED), pitch = (int) (seekBarList[2].getProgress() / 100.0 * MAX_PITCH), person = 0, volume = 3;
        return "http://tsn.baidu.com/text2audio?lan=zh&tok=" + accessToken
                + "&ctp=1&cuid=weidiao&spd=" + speed + "&pit=" + pitch + "&per="
                + person + "&vol=" + volume + "&tex="
                + URLEncoder.encode(text, "utf8");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TextView textView = ((TextView) seekBar.getTag());
        int pos = textView.getText().toString().lastIndexOf('=');
        textView.setText(textView.getText().toString().substring(0, pos) + "=" + progress);
        if (seekBar == seekBarList[1]) {
            seekBarList[0].setProgress(Math.max(progress, seekBarList[0].getProgress()));
        }
        if (seekBar == seekBarList[0]) {
            seekBarList[1].setProgress(Math.min(progress, seekBarList[1].getProgress()));
        }
        for (int i = 0; i < seekBarList.length; i++) {
            if (seekBarList[i] == seekBar) {
                sharedPreferences.edit().putInt("progress" + i, progress).commit();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
