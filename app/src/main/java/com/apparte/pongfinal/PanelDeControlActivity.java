package com.apparte.pongfinal;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

public class PanelDeControlActivity extends AppCompatActivity {
    Button button;
    DisplayMetrics metrics = new DisplayMetrics();
    boolean mute;
    int level;
    int tipoControl;
    public static final String PLAYER = "player";
    public static final String MUTE = "mute";
    public static final String LEVEL = "level";
    public static final String TIPOCONTROL = "tipocontrol";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_de_control);


        //Deaful settings
        level =1;
        tipoControl=0;
        mute=false;


    }
    public void animacionEntradaIzquierdaBaja(Button button){


        //button.getWidth()
        // Creo la animación
        this.getWindow().getWindowManager().getDefaultDisplay().getWidth();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
       int a = metrics.heightPixels;
        int ad= button.getWidth();
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(button, "x", button.getX(),
               metrics.widthPixels/2-150);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(button, "y", button.getY(), metrics.heightPixels/2-500);

        // Indico sus duraciones
        objectAnimatorX.setDuration(3000);
        objectAnimatorY.setDuration(3000);
        // Creo el consjunto de animaciones
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        // Comienzo el conjunto de animaciones
        animatorSet.start();
    }

    public void onClickOnePlayer(View view){
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(PLAYER, 1);
        i.putExtra(MUTE, mute);
        i.putExtra(LEVEL,level);
        i.putExtra(TIPOCONTROL,tipoControl);
        startActivity(i);
    }
    public void onClickConfiguration(View view) {


        final Dialog dialog = new Dialog(PanelDeControlActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle(getResources().getString(R.string.settings));
        RadioGroup  grupoDificultad= (RadioGroup) dialog.findViewById(R.id.niveldificultad);
        grupoDificultad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                level = radioGroup.indexOfChild(radioButton);
            }
        });
        RadioGroup  grupoControl= (RadioGroup) dialog.findViewById(R.id.controlador);
        grupoControl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                tipoControl = radioGroup.indexOfChild(radioButton);
            }
        });
        final CheckBox sonido = (CheckBox)dialog.findViewById(R.id.checkBoxSonido);

        Button dialog_btn = (Button) dialog.findViewById(R.id.confirmConfig);
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(sonido.isChecked()){
                        mute=true;
                    }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

     // animacionEntradaIzquierdaBaja(button);
    }
}
