package com.apparte.pongfinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private PongView mPongView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPongView = (PongView) findViewById(R.id.pong);
        mPongView.update();
    }
}
