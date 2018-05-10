package com.apparte.pongfinal.Game;

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

import com.apparte.pongfinal.R;

/**
 * Created by jlsanchez on 25/1/16.
 */
public class PongView extends View implements View.OnTouchListener {
    private boolean juegoIniciado = false;
    Player jugadorAzul;
    Player jugadorRojo;
    Paint jugadorAzulPincel;
    Paint jugadorRojoPincel;
    Paint bolaPincel;
    Ball bola;
    boolean nuevaBola = false;
    boolean continuar = true;
    private Rect reiniciarBoton;
    boolean reiniciar;
    /////
    // Ultimo momento en el que se actualizó
// Factor de conversión de metros a píxeles
    final float METER_TO_PIXEL = 50.0f;
    long lastUpdateTime = 0;
    // Posición  del jugador
    float radius = 0;
    float posX = 0;
    float posY = 0;

    // Velocidad de la bola
    float speedX = 0;
    float speedY = 0;
    ///Variable para controlar el sonido
    public boolean mute;

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    ///Variable para seleccionar el numero de jugadores
    public int numeroJugadores;

    public int getNumeroJugadores() {
        return numeroJugadores;
    }

    public void setNumeroJugadores(int numeroJugadores) {
        this.numeroJugadores = numeroJugadores;
    }

    ///Variable para seleccionar la dificultad del juego
    public int dificultad;

    public int getDificultad() {
        return dificultad;
    }

    public void setDificultad(int dificultad) {
        this.dificultad = dificultad;
    }


    /**
     * Pool for our sound effects
     */
    protected SoundPool mPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

    protected int ganarSFX, bolaPerdidaSFX, toqueRaquetaSFX, toqueParedSFX;
    /**
     * Redraws the screen according to FPS
     */
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

    public void iniciarBotonNuevoJuego() {
        int min = Math.min(getWidth() / 4, getHeight() / 4);
        int xmid = getWidth() / 2;
        int ymid = getHeight() / 2;
        reiniciarBoton = new Rect(xmid - min, ymid - min, xmid + min, ymid + min);
    }

    public void update() {
        if (getHeight() == 0 || getWidth() == 0) {
            mRedrawHandler.sleep(1000 / 30);
            return;
        }
        if (!juegoIniciado) {
            inicializarJuego();

        }
        long now = System.currentTimeMillis();
        if (juegoIniciado) {
            if (now >= 1000 / 30) {
                if (nuevaBola) {
                    bolaInicio();
                    nuevaBola = false;
                }
                logicaDeJuego();
            }
        }
        if (continuar) {
            long diff = System.currentTimeMillis() - now;
            mRedrawHandler.sleep(Math.max(0, (1000 / 30) - diff));
        }

    }

    public void nuevoJuego() {
        juegoIniciado = false;
        continuar = true;
    }

    public void resume() {
        continuar = true;
        update();
    }

    public void stop() {
        continuar = false;
    }

    public void logicaDeJuego() {
        //Mover jugadores
        float px = bola.x;
        float py = bola.y;
        bola.move();
        jugadorAzul.move();
        jugadorRojo.move();
        handleBounces(px, py);
        // Compruebo si ha perdido alguno
        if (bola.y >= getHeight()) {
            nuevaBola = true;
            jugadorRojo.marcaGol();
            jugadorAzul.pierdeUnaVida();
            if (!jugadorRojo.haGanado())
                playSound(bolaPerdidaSFX);
            else {
                playSound(ganarSFX);
                stop();
            }

        } else if (bola.y <= 0) {
            nuevaBola = true;
            jugadorAzul.marcaGol();
            if (!jugadorAzul.haGanado())
                playSound(bolaPerdidaSFX);
            else {
                playSound(ganarSFX);
                stop();
            }

        }

    }

    protected void handleBounces(float px, float py) {
        handleTopFastBounce(jugadorRojo, px, py);
        handleBottomFastBounce(jugadorAzul, px, py);


        if (bola.x <= Ball.RADIO || bola.x >= getWidth() - Ball.RADIO) {
            bola.rebotaEnPared();
            playSound(toqueParedSFX);
            if (bola.x == Ball.RADIO)
                bola.x++;
            else
                bola.x--;
        }

    }

    protected void handleTopFastBounce(Player jugador, float px, float py) {
        if (bola.irHaciaArriba() == false)
            return;

        float tx = bola.x;
        float ty = bola.y - Ball.RADIO;
        float ptx = px;
        float pty = py - Ball.RADIO;
        float dyp = ty - jugador.getBottom();
        float xc = tx + (tx - ptx) * dyp / (ty - pty);

        if (ty < jugador.getBottom() && pty > jugador.getBottom()
                && xc > jugador.getLeft() && xc < jugador.getRight()) {

            bola.x = xc;
            bola.y = jugador.getBottom() + Ball.RADIO;
            bola.rebotaEnPlayer(jugador);
            playSound(toqueRaquetaSFX);

        }

    }

    protected void handleBottomFastBounce(Player jugador, float px, float py) {
        if (bola.irHAciaAbajo() == false)
            return;

        float bx = bola.x;
        float by = bola.y + Ball.RADIO;
        float pbx = px;
        float pby = py + Ball.RADIO;
        float dyp = by - jugador.getTop();
        float xc = bx + (bx - pbx) * dyp / (pby - by);

        if (by > jugador.getTop() && pby < jugador.getTop()
                && xc > jugador.getLeft() && xc < jugador.getRight()) {

            bola.x = xc;
            bola.y = jugador.getTop() - Ball.RADIO;
            bola.rebotaEnPlayer(jugador);
            playSound(toqueRaquetaSFX);
            if (jugadorAzul.onePlayer)
                jugadorAzul.tocaLaPelota();

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Pinto jugadores
        if (null != jugadorAzul) {
            if (continuar) {
                jugadorAzul.draw(canvas);
                jugadorRojo.draw(canvas);
                bola.draw(canvas);
            } else {

                pintaReiniciarJuego(canvas);
            }
        }

    }

    private void pintaReiniciarJuego(Canvas canvas) {
        reiniciar = true;
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        String gana;
        if (jugadorAzul.haGanado()) {
            gana = "Gana el azul";
        } else {
            gana = "Gana el rojo";
        }
        String texto = null;
        if (numeroJugadores != 1) {
            texto = "Juego terminado \n" + gana + "\n" + "Pulse para reiniciar";
        } else {
            texto = "Juego terminado \n" + "Has tocado " + String.valueOf(jugadorAzul.toques) + " veces la bola \n"
                    + "Pulsa para reiniciar";
        }
        int pausew = (int) paint.measureText(texto);
        paint.setStyle(Paint.Style.STROKE);
        //canvas.drawRect(reiniciarBoton, paint);
        int y = getHeight() / 2 - 100;
        int x = getWidth() / 2 - pausew / 2 - 90;
        paint.setTextSize(50);
        paint.setColor(Color.BLACK);
        for (String line : texto.split("\n")) {
            canvas.drawText(line, x, y, paint);
            y += paint.descent() - paint.ascent();
        }

    }

    public void inicializarJuego() {
        cargarSonidos();
        inicalizarJugadores();
        inicializarBola();
        iniciarBotonNuevoJuego();
        juegoIniciado = true;
        continuar = true;
        reiniciar = false;
    }

    public void inicializarBola() {
        bolaPincel = new Paint();
        bolaPincel.setColor(Color.GREEN);
        bola = new Ball(bolaPincel, getWidth());
        bolaInicio();
    }

    public void inicalizarJugadores() {
        Rect redTouch = new Rect(0, 0, getWidth(), getHeight() / 8);
        Rect blueTouch = new Rect(0, 7 * getHeight() / 8, getWidth(), getHeight());
        jugadorAzulPincel = new Paint();
        jugadorAzulPincel.setColor(Color.BLUE);
        jugadorRojoPincel = new Paint();
        jugadorRojoPincel.setColor(Color.GREEN);
        jugadorRojo = new Player(Color.RED, redTouch.bottom + 3, getWidth() / 2, getHeight() / 2, jugadorAzulPincel);
        jugadorAzul = new Player(Color.BLUE, blueTouch.top - 3 - Player.ALTO_PALA, getWidth() / 2, getHeight() / 2, jugadorRojoPincel);
        jugadorRojo.setTouchbox(redTouch);
        jugadorAzul.setTouchbox(blueTouch);

        ///Cambiar a solo un jugador//
        if (numeroJugadores == 1) {
            jugadorRojo.cambiarABarreraTotal();
            jugadorAzul.onePlayer = true;
            jugadorRojo.onePlayer = true;
        } else {
            jugadorAzul.onePlayer = false;
            jugadorRojo.onePlayer = false;
        }


    }

    /**
     * Bola Inicio
     */
    private void bolaInicio() {
        bola.x = getWidth() / 2;
        bola.y = getHeight() / 2;
        bola.velocidad = Ball.VELOCIDAD_BASE + (3 * dificultad);//añadir velocidad
        bola.randomAngle();
        bola.pause();

    }

    //Reproducir Sonidos
    private void playSound(int rid) {
        if (!mute)
            mPool.play(rid, 0.2f, 0.2f, 1, 0, 1.0f);
    }

    //Liberar sonidos
    public void release() {
        mPool.release();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {

        InputHandler handle = InputHandler.getInstance();
        for (int i = 0; i < handle.getTouchCount(motionEvent); i++) {
            int tx = (int) handle.getX(motionEvent, i);
            int ty = (int) handle.getY(motionEvent, i);
            if (null != jugadorAzul) {
                if (jugadorAzul.inTouchbox(tx, ty)) {
                    jugadorAzul.destination = tx;
                } else if (jugadorRojo.inTouchbox(tx, ty)) {
                    jugadorRojo.destination = tx;
                }
            }
        }
        if (reiniciar) {
            reiniciar = false;
            inicializarJuego();
            resume();
        }
        return false;
    }
    public void updateFromSensor(float gravityX, float gravityY) {
        // Primera vez
        if (lastUpdateTime == 0) {
            lastUpdateTime = System.currentTimeMillis();
            return;
        }
        if (gravityX > 0) {
            jugadorAzul.destination = 0;
        } else {
            jugadorAzul.destination = getWidth();
        }
    }

}
