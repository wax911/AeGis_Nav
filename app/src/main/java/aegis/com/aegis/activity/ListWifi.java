package aegis.com.aegis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ListWifi extends Activity {
    ArrayList<Integer>c11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new BubbleSurfaceView(this));
        Intent i = getIntent();
        c11 = i.getIntegerArrayListExtra("Cir");
    }

    public class BubbleSurfaceView extends SurfaceView implements SurfaceHolder.Callback
    {
        private SurfaceHolder sh;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        public BubbleSurfaceView(Context context)
        {
            super(context);
            sh = getHolder();
            sh.addCallback(this);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
        }
        public void surfaceCreated(SurfaceHolder holder) {
            Circle c1 = new Circle(new XYPoint(200, 400), c11.get(0).intValue());
            Circle c2 = new Circle(new XYPoint(250, 100), c11.get(1).intValue());
            Circle c3 = new Circle(new XYPoint(485, 300), c11.get(2).intValue());
            ApolloniusSolver s = new ApolloniusSolver();

            Canvas canvas = sh.lockCanvas();
            canvas.drawColor(Color.BLACK);
            canvas.drawCircle(c1.getCenterX(), c1.getCenterY(), c1.getRadius(), paint);
            paint.setColor(Color.RED);
            Circle cap = s.solveApollonius(c1, c2, c3, -1, -1, -1);
            canvas.drawCircle(cap.getCenterX(),cap.getCenterY(), cap.getRadius(), paint);
            sh.unlockCanvasAndPost(canvas);
        }
        public void surfaceChanged(SurfaceHolder holder, int format, int width, 			int height) {
        }
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

}
