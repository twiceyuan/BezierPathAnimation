package com.twiceyuan.bezierpathanimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FrameLayout container = (FrameLayout) findViewById(R.id.container);

        final TextView source = (TextView) findViewById(R.id.source);
        final TextView target = (TextView) findViewById(R.id.target);

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
}
