package com.apparte.pongfinal.Game;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by jlsanchez on 25/1/16.
 */
public class Ball {
    public float x, y, xp, yp, vx, vy;
    private static final Random RNG = new Random();
    public static final float VELOCIDAD_BASE = 9.0f;
    public float velocidad;
    protected double mAngle;
    private int anchoPantalla;
    private Paint mPaint;
    public static final int RADIO = 20;
    protected int mContador = 0;
    public static final double BOUND = Math.PI / 9;
    public static final double SALT = 4 * Math.PI / 9;
    public Ball( Paint mPaint, int anchoPantalla) {
        this.anchoPantalla=anchoPantalla;
        this.mPaint=mPaint;
        findVector();
    }
    public Ball(Ball other) {
        x = other.x;
        y = other.y;
        xp = other.xp;
        yp = other.yp;
        vx = other.vx;
        vy = other.vy;
        velocidad = other.velocidad;
        mAngle = other.mAngle;
    }
    //Calculo la velocidad en x y en y respecto al ángulo de rebote
    protected void findVector() {
        vx = (float) (velocidad * Math.cos(mAngle));
        vy = (float) (velocidad * Math.sin(mAngle));
    }
    public boolean irHaciaArriba() {
        return mAngle >= Math.PI;
    }

    public boolean irHAciaAbajo() {
        return !irHaciaArriba();
    }

    public boolean irHaciaLaIzquierda() {
        return mAngle <= 3 * Math.PI / 2 && mAngle > Math.PI / 2;
    }

    public boolean irHaciaLaDerecha() {
        return !irHaciaLaIzquierda();
    }
    public double getAngle() {
        return mAngle;
    }
    public boolean serving() {
        return mContador > 0;
    }
    public void pause() {
        mContador = 60;
    }
    public void move() {
        if(mContador <= 0) {
            x = keepX(x + vx);
            y += vy;
        }
        else {
            mContador--;
        }
    }
    public void randomAngle() {
        setAngle( Math.PI / 2 + RNG.nextInt(2) * Math.PI + Math.PI / 2 * RNG.nextGaussian() );
    }
    public void setAngle(double angle) {
        mAngle = angle % (2 * Math.PI);
        mAngle = boundAngle(mAngle);
        findVector();
    }
    public void draw(Canvas canvas) {
        if((mContador / 10) % 2 == 1 || mContador == 0)
            canvas.drawCircle(x, y, Ball.RADIO, mPaint);
    }
    /**
     * Método que hace rebotar la bola a través de un eje vertical
     */
    public void rebotaEnPlayer(Player p) {
        double angle;

        // up-right case
        if(mAngle >= Math.PI) {
            angle = 4 * Math.PI - mAngle;
        }
        // down-left case
        else {
            angle = 2 * Math.PI - mAngle;
        }

        angle %= (2 * Math.PI);
        angle = salt(angle, p);
//			normalize(p);
        setAngle(angle);
    }
    /**
     * Rebotar la pelota de un eje horizontal.
     */
    public void rebotaEnPared() {
        setAngle(3 * Math.PI - mAngle);
    }

    protected double salt(double angle, Player player) {
        int cx = player.centerX();
        double halfWidth = player.getWidth() / 2;
        double change = 0.0;

        if(irHaciaArriba())
            change = SALT * ((cx - x) / halfWidth);
        else
            change = SALT * ((x - cx) / halfWidth);

        return boundAngle(angle, change);
    }
    /**
     * Normaliza la posición de una bola después de que ha golpeado un jugador
     *
     *
     */
    protected void normalize(Player p) {
        // Sale si el balón está fuera de la anchura del jugador
        if(x < p.getLeft() || x > p.getRight()) {
            return;
        }

        // Caso si la bola está por encima del jugador
        if(y < p.getTop()) {
            y = Math.min(y, p.getTop() - Ball.RADIO);
        }
        else if(y > p.getBottom()) {
            y = Math.max(y, p.getBottom() + Ball.RADIO);
        }
    }
    /**
     * Ángulo de rebote
     */
    protected double boundAngle(double angle, double angleChange) {
        return boundAngle(angle + angleChange, angle >= Math.PI);
    }

    protected double boundAngle(double angle) {
        return boundAngle(angle, angle >= Math.PI);
    }
    /**
     * Angulo
     */
    protected double boundAngle(double angle, boolean top) {
        if(top) {
            return Math.max(Math.PI + BOUND, Math.min(2 * Math.PI - BOUND, angle));
        }

        return Math.max(BOUND, Math.min(Math.PI - BOUND, angle));
    }
    /**
     * Transformo a coordenadas de la pelota
     */
    protected float keepX(float x) {
        return bound(x, Ball.RADIO, anchoPantalla - Ball.RADIO);
    }
    protected float bound(float x, float low, float hi) {
        return Math.max(low, Math.min(x, hi));
    }
}
