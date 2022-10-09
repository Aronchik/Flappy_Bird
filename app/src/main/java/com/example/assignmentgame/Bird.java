package com.example.assignmentgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.ArrayList;

public class Bird extends BaseObject {
    //This Bitmap ArrayList is used to animate the bird in flight
    private ArrayList<Bitmap> arrBms = new ArrayList<>();

    //To alternate between upflap and downlflap bitmap of the bird
    private int count, vFlap, idCurrentBitMap;

    //To make the bird fall automatically
    private float drop;

    public Bird(){
        this.count = 0;
        this.vFlap = 5;
        this.idCurrentBitMap = 0;
        this.drop = 0;
    }

    // This method will draw the bird
    public void draw(Canvas canvas){
        drop();
        canvas.drawBitmap(this.getBm(), this.x, this.y, null);
    }

    private void drop() {
        this.drop += 0.6;
        this.y += this.drop;
    }

    public ArrayList<Bitmap> getArrBms() {
        return arrBms;
    }

    public void setArrBms(ArrayList<Bitmap> arrBms) {
        this.arrBms = arrBms;
        for(int i = 0; i < arrBms.size(); i++){
            this.arrBms.set(i, Bitmap.createScaledBitmap(this.arrBms.get(i), this.width, this.height, true));
        }
    }

    @Override
    public Bitmap getBm() {
        count++;

        //Alternating between upflap and downflap bitmaps
        if(this.count == this.vFlap) {
            if(idCurrentBitMap == 0)
                idCurrentBitMap =1;
            else if(idCurrentBitMap == 1)
                idCurrentBitMap = 0;
            count = 0;
        }

//        if(this.count == this.vFlap){
//            for(int i = 0; i < arrBms.size(); i++){
//                if(i == arrBms.size()-1) {
//                    this.idCurrentBitMap = 0;
//                    break;
//                }else if(this.idCurrentBitMap == 1){
//                    idCurrentBitMap = i+1;
//                    break;
//                }
//            }
//            count = 0;
//        }

        //Rotate the bird on up
        if(this.drop < 0){
            Matrix matrix = new Matrix();
            matrix.postRotate(-25);
            return Bitmap.createBitmap(arrBms.get(idCurrentBitMap), 0,0, arrBms.get(idCurrentBitMap).getWidth(), arrBms.get(idCurrentBitMap).getHeight(), matrix, true);
        }else if(drop >= 0){
            Matrix matrix = new Matrix();

            if(drop < 70){
                matrix.postRotate(-25 + (drop*2));
            }else{
                matrix.postRotate(45);
            }

            return Bitmap.createBitmap(arrBms.get(idCurrentBitMap), 0,0, arrBms.get(idCurrentBitMap).getWidth(), arrBms.get(idCurrentBitMap).getHeight(), matrix, true);
        }

        return this.arrBms.get(idCurrentBitMap);
    }

    public float getDrop() {
        return drop;
    }

    public void setDrop(float drop) {
        this.drop = drop;
    }
}
