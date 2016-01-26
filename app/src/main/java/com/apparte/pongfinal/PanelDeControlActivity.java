package com.apparte.pongfinal;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;


import com.apparte.pongfinal.Game.GameActivity;

import java.io.ByteArrayOutputStream;

public class PanelDeControlActivity extends AppCompatActivity {
    Button jugador1;
    Button jugador2;
    Button configuracion;
    Button photo;
    ImageView logo;
    DisplayMetrics metrics = new DisplayMetrics();
    boolean mute;
    int level;
    int tipoControl;
    String uriStr;
    public static final String PLAYER = "player";
    public static final String MUTE = "mute";
    public static final String LEVEL = "level";
    public static final String TIPOCONTROL = "tipocontrol";
    public static final String BITMAP = "bitmap";
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_de_control);
        setDefaultSettings();
        iniciarAnimacionPropiedades();
        jugador1 = (Button) findViewById(R.id.oneplayer);
        jugador2 = (Button) findViewById(R.id.twoplayer);
        configuracion = (Button) findViewById(R.id.sonido);
        photo = (Button) findViewById(R.id.button);
        logo = (ImageView) findViewById(R.id.logoImagen);
        //Animación para que los botones vengan desde la izquierda y reboten (no conseguí que hicieran una salida hacia la derecha al salir de la pantalla ;()
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        animation.setInterpolator(new BounceInterpolator());
        animation.setDuration(4000);
        jugador1.startAnimation(animation);
        jugador1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOnePlayer();
            }
        });
        configuracion.startAnimation(animation);
        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickConfiguration();
            }
        });
        jugador2.startAnimation(animation);
        jugador2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickTwoPlayer();
            }
        });
        photo.startAnimation(animation);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickTakePhoto();
            }
        });
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.rotation);
        logo.setAnimation(animation1);


    }

    //Animación del fondo de pantalla al iniciar
    private void iniciarAnimacionPropiedades() {
        ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(
                findViewById(R.id.ll_container), "backgroundColor", new ArgbEvaluator(), 0xff000000,
                0xff0000ff, 0xff00ffff, 0xffffffff, 0xffff0000, 0xffffff00, 0xff003399);
        backgroundColorAnimator.setDuration(1000);
        backgroundColorAnimator.setRepeatCount(1);
        backgroundColorAnimator.start();
    }
    //OPCIONES POR DEFECTO
    void setDefaultSettings() {
        //Deaful settings
        level = 0;
        tipoControl = 0;
        mute = false;
        uriStr = null;

    }
    ///LLamamos a la activity del juego pasándole opciones; en este caso comprobamos también que no haya configurado sensor pues en este caso no se permite
    // lo dejamos para la versión 2.0 ;)
    public void onClickTwoPlayer() {

        if (tipoControl == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El sensor no esta disponible en modo dos jugadores")
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
        } else {
            Intent i = new Intent(this, GameActivity.class);
            i.putExtra(PLAYER, 2);
            i.putExtra(MUTE, mute);
            i.putExtra(LEVEL, level);
            i.putExtra(TIPOCONTROL, tipoControl);
            if (mBitmap != null) {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                i.putExtra(BITMAP, bs.toByteArray());
            }
            startActivity(i);
        }
    }

    ///LLamamos a la activity del juego pasándole opciones
    public void onClickOnePlayer() {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(PLAYER, 1);
        i.putExtra(MUTE, mute);
        i.putExtra(LEVEL, level);
        i.putExtra(TIPOCONTROL, tipoControl);
        if (mBitmap != null) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            i.putExtra(BITMAP, bs.toByteArray());
        }
        startActivity(i);
    }

    //Abrimos cuadro de diálogo y recibimos la configuración
    public void onClickConfiguration() {
        setDefaultSettings();
        final Dialog dialog = new Dialog(PanelDeControlActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle(getResources().getString(R.string.settings));
        RadioGroup grupoDificultad = (RadioGroup) dialog.findViewById(R.id.niveldificultad);
        grupoDificultad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                level = radioGroup.indexOfChild(radioButton);
            }
        });
        RadioGroup grupoControl = (RadioGroup) dialog.findViewById(R.id.controlador);
        grupoControl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                tipoControl = radioGroup.indexOfChild(radioButton);
            }
        });
        final CheckBox sonido = (CheckBox) dialog.findViewById(R.id.checkBoxSonido);
        Button dialog_btn = (Button) dialog.findViewById(R.id.confirmConfig);
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sonido.isChecked()) {
                    mute = true;
                } else {
                    mute = false;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //Abrimos el cuadro de dialogo para seleccionar la cámara
    public void onClickTakePhoto() {
        DialogImage.selectImage(PanelDeControlActivity.this, PanelDeControlActivity.this);
    }

    ////Recibimos el resultado de realizar la foto con la cámara
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DialogImage.REQUEST_CAMERA) {
                mBitmap = (Bitmap) data.getExtras().get("data");

            }
        }

    }


}
