package com.apparte.pongfinal;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.apparte.pongfinal.Game.PongView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class GameActivity extends AppCompatActivity {
    private PongView mPongView;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout fondo = (RelativeLayout)findViewById(R.id.backgroundLayout);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        mPongView = (PongView) findViewById(R.id.pong);
        mPongView.setNumeroJugadores(b.getInt(PanelDeControlActivity.PLAYER));
        mPongView.setDificultad(b.getInt(PanelDeControlActivity.LEVEL));
        mPongView.setMute(b.getBoolean(PanelDeControlActivity.MUTE));
        if(i.hasExtra(PanelDeControlActivity.BITMAP)) {
             bitmap = BitmapFactory.decodeByteArray(
                    i.getByteArrayExtra(PanelDeControlActivity.BITMAP),0,i.getByteArrayExtra(PanelDeControlActivity.BITMAP).length);
            fondo.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
        mPongView.update();
    }


    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
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
