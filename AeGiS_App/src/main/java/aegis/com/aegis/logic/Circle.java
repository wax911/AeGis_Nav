package aegis.com.aegis.logic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Lolo on 10/18/2015.
 */
public class Circle extends View {
    Paint paint = new Paint();

    public Circle(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.GREEN);
        Rect rect = new Rect(20, 56, 200, 112);

        canvas.drawRect(rect, paint);
    }

}
