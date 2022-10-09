package com.example.assignmentgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Pipe extends BaseObject {
    //Speed of the pipes moving
    public static int speed;

    public Pipe(float x, float y, int width, int height, int speedMultiplier){
        super(x, y, width, height);
        speed = speedMultiplier*Constants.SCREEN_WIDTH/1000;
    }

    public void draw(Canvas canvas){
        //Moving the water pipe from right to left
        this.x -= speed;
        canvas.drawBitmap(this.bm, this.x, this.y, null);
    }

    public void randomY(){
        Random r = new Random();
        this.y =  r.nextInt((0 + this.height/4) + 1) - this.height/4 ;
    }

    @Override
    public void setBm(Bitmap bm) {
        this.bm = Bitmap.createScaledBitmap(bm, width, height, true);
    }
}
