package com.twiceyuan.bezierpathanimation;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by twiceYuan on 2017/3/9.
 * <p>
 * 拖拽帮助类
 */
@SuppressWarnings("WeakerAccess")
public class DragHelper {

    /**
     * 使一个 View 变为内容可拖拽
     *
     * @param view 需要变为拖拽的 View
     */
    public static void attach(final View view) {
        final float dX[] = new float[1];
        final float dY[] = new float[1];

        final float downX[] = new float[1];
        final float downY[] = new float[1];

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX[0] = view.getX() - event.getRawX();
                        dY[0] = view.getY() - event.getRawY();
                        downX[0] = event.getRawX();
                        downY[0] = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + dX[0])
                                .y(event.getRawY() + dY[0])
                                .setDuration(0)
                                .start();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float xMove = event.getRawX() - downX[0];
                        float yMove = event.getRawY() - downY[0];
                        return (xMove * xMove + yMove * yMove) > 100;
                    default:
                        return false;
                }
            }
        });
    }
}