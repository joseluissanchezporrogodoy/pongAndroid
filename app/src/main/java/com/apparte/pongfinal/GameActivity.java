package com.apparte.pongfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.apparte.pongfinal.Game.PongView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private PongView mPongView;
    Bitmap bitmap;
    private SensorManager sensorManager;
    private Sensor sensor;

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
        if(false)
            mPongView.update();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        //Si el sensor no está disponible lo indico
        if(sensor == null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if(sensor==null){
                AlertDialog.Builder builder = new  AlertDialog.Builder(this);
                builder.setMessage("El sensor no esta disponible en este dispositivo")
                        .setTitle("¡Atención!")
                        .setCancelable(false)
                        .setNeutralButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Elimino el listener para el sensor
        sensorManager.unregisterListener(this);
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
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }
    protected void onDestroy() {
        super.onDestroy();
        mPongView.release();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        mPongView.updateFromSensor(sensorEvent.values[0], sensorEvent.values[1]);
        Log.i("Cambio posición x:", String.valueOf(sensorEvent.values[1]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
