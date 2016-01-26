package com.apparte.pongfinal.Game;

import android.app.Activity;
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
import com.apparte.pongfinal.PanelDeControlActivity;
import com.apparte.pongfinal.R;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
/**
 * Created by jlsanchez on 25/1/16.
 */
public class GameActivity extends Activity implements SensorEventListener {
    private PongView mPongView;
    Bitmap bitmap;
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean sensorActivo;
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
        //Compruebo qu eel jugador ha configurado un fondo y lo casco
        if(i.hasExtra(PanelDeControlActivity.BITMAP)) {
             bitmap = BitmapFactory.decodeByteArray(
                    i.getByteArrayExtra(PanelDeControlActivity.BITMAP),0,i.getByteArrayExtra(PanelDeControlActivity.BITMAP).length);
            fondo.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
        //Miro el tipo de control e intento cargarlo si no puedo con el sensor vuelvo al touch
        if(b.getInt(PanelDeControlActivity.TIPOCONTROL)==0) {
            mPongView.update();
            sensorActivo = false;
        }else {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorActivo =true;
            if (sensor == null) {
                sensorActivo=false;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("El sensor no esta disponible en este dispositivo")
                        .setTitle("¡Atención!")
                        .setCancelable(false)
                        .setNeutralButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        comienza();
                                    }
                                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }


    }
    private void comienza(){
        mPongView.update();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Elimino el listener para el sensor
        if(sensorActivo)
        sensorManager.unregisterListener(this);
    }

    protected void onStop() {
        super.onStop();
        //Paro el juego
        mPongView.stop();
    }

    protected void onResume() {
        super.onResume();
        mPongView.resume();
        //registro el listener del sensor
        if(sensorActivo)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }
    protected void onDestroy() {
        super.onDestroy();
        //Libero recursos del juego
        mPongView.release();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Envío los datos del sensor
        mPongView.updateFromSensor(sensorEvent.values[0], sensorEvent.values[1]);
        Log.i("Cambio posición x:", String.valueOf(sensorEvent.values[1]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
