package com.twiceyuan.bezierpathanimation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout container;
    private TextView    source;
    private TextView    target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = (FrameLayout) findViewById(R.id.container);
        source = (TextView) findViewById(R.id.source);
        target = (TextView) findViewById(R.id.target);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BezierHelper.create()
                        .setSource(source)
                        .setTarget(target)
                        .setContainer(container)
                        .start();
            }
        });
    }

    public void changeSpiritButton(final View view) {
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BezierHelper.create()
                        .setSource(source)
                        .setTarget(target)
                        .setSpirit(viewToBitmap(view))
                        .setContainer(container)
                        .start();
            }
        });
    }

    public void changeSpiritSource(View view) {
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BezierHelper.create()
                        .setSource(source)
                        .setTarget(target)
                        .setContainer(container)
                        .start();
            }
        });
    }

    private static Bitmap viewToBitmap(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }
}
