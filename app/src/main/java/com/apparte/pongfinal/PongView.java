package com.apparte.pongfinal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jlsanchez on 25/1/16.
 */
public class PongView extends View implements View.OnTouchListener{
    private boolean juegoIniciado=false;
    Player jugadorzul;
    Player jugadorRojo;
    Paint jugadorAzulPincel;
    Paint jugadorRojoPincel;
    Paint bolaPincel;
    Ball bola;
    boolean nuevaBola =false;
    /** Pool for our sound effects */
    protected SoundPool mPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

    protected int ganarSFX, bolaPerdidaSFX, toqueRaquetaSFX, toqueParedSFX;
    /** Redraws the screen according to FPS */
    private RefreshHandler mRedrawHandler = new RefreshHandler();
    public PongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);

    }
    protected void cargarSonidos() {
        Context ctx = getContext();
        ganarSFX = mPool.load(ctx, R.raw.toquegana, 1);
        bolaPerdidaSFX = mPool.load(ctx, R.raw.bolaperdida, 1);
        toqueRaquetaSFX = mPool.load(ctx, R.raw.toqueraqueta, 1);
        toqueParedSFX = mPool.load(ctx, R.raw.toquepared, 1);
    }
    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            PongView.this.update();
            PongView.this.invalidate(); // Mark the view as 'dirty'
        }

        public void sleep(long delay) {
            this.removeMessages(0);
            this.sendMessageDelayed(obtainMessage(0), delay);
        }
    }

    public void update(){
        if(getHeight() == 0 || getWidth() == 0) {
            mRedrawHandler.sleep(1000 / 30);
            return;
        }
        if(!juegoIniciado){
            inicializarJuego();
        }
        long now = System.currentTimeMillis();
        if(juegoIniciado) {
            if(now >= 1000 / 30) {
                if(nuevaBola) {
                    bolaInicio();
                    nuevaBola = false;
                }
                logicaDeJuego();
            }
        }
        if(true) {
            long diff = System.currentTimeMillis() - now;
            mRedrawHandler.sleep(Math.max(0, (1000 / 30) - diff) );
        }

    }

    public void logicaDeJuego(){
        //Mover jugadores
        float px = bola.x;
        float py = bola.y;
        bola.move();
        jugadorzul.move();
        jugadorRojo.move();
        handleBounces(px, py);
        // Compruebo si ha perdido alguno
        if(bola.y >= getHeight()) {
            nuevaBola = true;
            jugadorzul.loseLife();

            if(jugadorzul.living())
                playSound(bolaPerdidaSFX);
            else
                playSound(ganarSFX);
        }
        else if (bola.y <= 0) {
            nuevaBola = true;
            jugadorRojo.loseLife();
            if(jugadorRojo.living())
                playSound(bolaPerdidaSFX);
            else
                playSound(ganarSFX);
        }

    }
    protected void handleBounces(float px, float py) {
        handleTopFastBounce(jugadorRojo, px, py);
        handleBottomFastBounce(jugadorzul, px, py);


        if(bola.x <= Ball.RADIO || bola.x >= getWidth() - Ball.RADIO) {
            bola.rebotaEnPared();
            playSound(toqueParedSFX);
            if(bola.x == Ball.RADIO)
                bola.x++;
            else
                bola.x--;
        }

    }

    protected void handleTopFastBounce(Player jugador, float px, float py) {
        if(bola.irHaciaArriba() == false)
            return;

        float tx = bola.x;
        float ty = bola.y - Ball.RADIO;
        float ptx = px;
        float pty = py - Ball.RADIO;
        float dyp = ty - jugador.getBottom();
        float xc = tx + (tx - ptx) * dyp / (ty - pty);

        if(ty < jugador.getBottom() && pty > jugador.getBottom()
                && xc > jugador.getLeft() && xc < jugador.getRight()) {

            bola.x = xc;
            bola.y = jugador.getBottom() + Ball.RADIO;
            bola.rebotaEnPlayer(jugador);
            playSound(toqueRaquetaSFX);

        }
    }

    protected void handleBottomFastBounce(Player jugador, float px, float py) {
        if(bola.irHAciaAbajo() == false)
            return;

        float bx = bola.x;
        float by = bola.y + Ball.RADIO;
        float pbx = px;
        float pby = py + Ball.RADIO;
        float dyp = by - jugador.getTop();
        float xc = bx + (bx - pbx) * dyp / (pby - by);

        if(by > jugador.getTop() && pby < jugador.getTop()
                && xc > jugador.getLeft() && xc < jugador.getRight()) {

            bola.x = xc;
            bola.y = jugador.getTop() - Ball.RADIO;
            bola.rebotaEnPlayer(jugador);
            playSound(toqueRaquetaSFX);

        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Pinto jugadores
        jugadorzul.draw(canvas);
        jugadorRojo.draw(canvas);
        bola.draw(canvas);

    }

    public void inicializarJuego(){
        cargarSonidos();
        inicalizarJugadores();
        inicializarBola();
        juegoIniciado=true;
    }
    public void inicializarBola(){
        bolaPincel = new Paint();
        bolaPincel.setColor(Color.GREEN);
        bola=new Ball(bolaPincel,getWidth());
        bolaInicio();
    }
    public void inicalizarJugadores(){
        Rect redTouch = new Rect(0,0,getWidth(),getHeight() / 8);
        Rect blueTouch = new Rect(0, 7 * getHeight() / 8, getWidth(), getHeight());
        jugadorAzulPincel = new Paint();
        jugadorAzulPincel.setColor(Color.BLUE);
        jugadorRojoPincel = new Paint();
        jugadorRojoPincel.setColor(Color.GREEN);
        jugadorRojo = new Player(Color.RED, redTouch.bottom + 3,getWidth()/2,getHeight()/2,jugadorAzulPincel);
        jugadorzul = new Player(Color.BLUE, blueTouch.top - 3 -Player.ALTO_PALA,getWidth()/2,getHeight()/2,jugadorRojoPincel);
        jugadorRojo.setTouchbox(redTouch);
        jugadorzul.setTouchbox(blueTouch);
        //TODO
        ///Cambiar a solo un jugador//
        //jugadorRojo.cambiarABarreraTotal();


    }
    /**
     * Bola Inicio
     */
    private void bolaInicio() {
        bola.x = getWidth() / 2;
        bola.y = getHeight() / 2;
       // bola.velocidad = bola.velocidad ;//añadir velocidad
        bola.randomAngle();
        bola.pause();

    }

    //Reproducir Sonidos
    private void playSound(int rid) {

        mPool.play(rid, 0.2f, 0.2f, 1, 0, 1.0f);
    }
    public boolean onTouch(View view, MotionEvent motionEvent) {


        InputHandler handle = InputHandler.getInstance();
        for(int i = 0; i < handle.getTouchCount(motionEvent); i++) {
            int tx = (int) handle.getX(motionEvent, i);
            int ty = (int) handle.getY(motionEvent, i);

            if( jugadorzul.inTouchbox(tx,ty)) {
                jugadorzul.destination = tx;
            }
            else if( jugadorRojo.inTouchbox(tx,ty)) {
                jugadorRojo.destination = tx;
            }

        }
        return false;
    }
}
