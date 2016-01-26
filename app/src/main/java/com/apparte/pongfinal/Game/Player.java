package com.apparte.pongfinal.Game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by jlsanchez on 25/1/16.
 */
public class Player {
    private Paint mPaint;
    protected int mColor;
    protected Rect mRect;
    protected Rect mTouch;
    public static final int ALTO_PALA = 10;
    public static final int ANCHO_PALA= 70;
    protected int mSpeed = 10;
    protected int mGoles = 0;
    private int centroPantallaY;
    private int centroPantallaX;
    private int y;
    public int destination;
    public boolean onePlayer;
    protected int toques=0;
    public Player(int color, int y,int centroPantallaX,int centroPantallaY,Paint paint) {
        mColor = color;
        this.centroPantallaY=centroPantallaY;
        this.centroPantallaX=centroPantallaX;
        this.y=y;
        this.mPaint= paint;

       mRect = new Rect(centroPantallaX - ANCHO_PALA, y, centroPantallaX + ANCHO_PALA, y + ALTO_PALA);


        destination = centroPantallaX;
    }
    public void cambiarABarreraTotal(){
        mRect = new Rect(0, y,centroPantallaX*2, y + ALTO_PALA);
    }
    public void setTouchbox(Rect r) {
        mTouch = r;
    }
    public void move() {
        move(mSpeed);
    }
    public void move(int s) {
        int dx = (int) Math.abs(mRect.centerX() - destination);

        if(destination < mRect.centerX()) {
            mRect.offset( (dx > s) ? -s : -dx, 0);
        }
        else if(destination > mRect.centerX()) {
            mRect.offset( (dx > s) ? s : dx, 0);
        }
    }
    public int getTop() {
        return mRect.top;
    }

    public int getBottom() {
        return mRect.bottom;
    }
    public int getWidth() {
        return Player.ANCHO_PALA;
    }
    public int getLeft() {
        return mRect.left;
    }

    public int getRight() {
        return mRect.right;
    }
    //Devuelve el centro de la raqueta
    public int centerX() {
        return mRect.centerX();
    }

    ///Dibuja la raqueta
    public void draw(Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mRect, mPaint);
        drawTouchbox(canvas);
        pintaGoles(canvas);
    }
    //Dibuja la zona de control
    public void drawTouchbox(Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);

        // Heuristic for deciding which line to paint:
        // draw the one closest to middle
        int mid = centroPantallaY / 2;
        int top = Math.abs(mTouch.top - mid);
        int bot = Math.abs(mTouch.bottom - mid);
        float y = (top < bot) ? mTouch.top : mTouch.bottom;
        canvas.drawLine(mTouch.left, y, mTouch.right, y, mPaint);
    }
    //Comprobación si se ha pulsado dentro de la zona de control del  jugador
    public boolean inTouchbox(int x, int y) {
        return mTouch.contains(x, y);
    }

   public void pintaGoles(Canvas canvas){
       ////TEXTO
       Paint paintFont = new Paint(Paint.ANTI_ALIAS_FLAG);
       paintFont.setTextSize(60);
       paintFont.setColor(mColor);
       paintFont.setTextAlign(Paint.Align.CENTER);
       if(y<centroPantallaY){
           canvas.drawText("Goles:"+String.valueOf(mGoles), 100,50 , paintFont);
       }else{
           canvas.drawText("Goles:"+String.valueOf(mGoles), 100,(centroPantallaY*2)-5, paintFont);
       }

   }

    ///Da un toque
    public void tocaLaPelota(){
        toques++;
    }
    ////Pierde una vida (marcan un gol)
    public void marcaGol() {
        mGoles++;
    }
    ///Saber si ha ganado
    ///Aumentar para que el partido dure más
    public boolean haGanado() {
        return mGoles == 1;
    }

}
