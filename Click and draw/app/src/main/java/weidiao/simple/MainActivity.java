package weidiao.simple;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    class MyView extends View {
        MyView(Context context) {
            super(context);
            pen.setStrokeWidth(30);
            pen.setStrokeCap(Paint.Cap.ROUND);
            pen.setColor(Color.RED);
        }

        ArrayList<Point> a = new ArrayList<>();
        Paint pen = new Paint();

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Toast.makeText(getContext(),event.getX()+","+event.getY(),Toast.LENGTH_LONG).show();
            a.add(new Point((int) event.getX(), (int) event.getY()));
            postInvalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (Point p : a) {
                canvas.drawPoint(p.x, p.y, pen);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
    }
}
