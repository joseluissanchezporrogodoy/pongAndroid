package com.apparte.pongfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.apparte.pongfinal.Game.PongView;

public class GameActivity extends AppCompatActivity {
    private PongView mPongView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        mPongView = (PongView) findViewById(R.id.pong);


        mPongView.update();
    }
    protected void onStop() {
        super.onStop();
        mPongView.stop();
    }

    protected void onResume() {
        super.onResume();
        mPongView.resume();
    }
    protected void onDestroy() {
        super.onDestroy();
        mPongView.release();
    }
}
