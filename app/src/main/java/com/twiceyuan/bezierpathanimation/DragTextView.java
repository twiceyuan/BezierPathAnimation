package com.twiceyuan.bezierpathanimation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by twiceYuan on 2017/3/8.
 * <p>
 * 可以拖动的 TextView
 */
public class DragTextView extends android.support.v7.widget.AppCompatTextView {
    float dX, dY;

    public DragTextView(Context context) {
        super(context);
    }

    public DragTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;
            default:
                return false;
        }
        return true;
    }
}
