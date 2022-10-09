package com.example.assignmentgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GameView extends View {
    private Bird bird;
    private Handler handler;
    private Runnable r;
    private ArrayList<Pipe> arrPipes;
    private int sumPipe, distance;
    private int score, bestScore = 0, bestScoreLvl1, bestScoreLvl2, bestScoreLvl3, bestScoreLvl4;
    private boolean start;
    private Context context;
    private int soundJump;
    private int soundGameover;
    private int soundHighScore;
    private boolean loadedSound;
    private SoundPool soundPool;
    private boolean yelled;
    private boolean beatHighScore;
    private MediaPlayer mediaPlayer;
    private int pipeDistance;
    private int speedMultiplier;
    private int difficulty;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        refreshScores();

            switch(difficulty){
                case 1:
                    bestScore = bestScoreLvl1;
                    break;
                case 2:
                    bestScore = bestScoreLvl2;
                    break;
                case 3:
                    bestScore = bestScoreLvl3;
                    break;
                case 4:
                    bestScore = bestScoreLvl4;
                    break;
                default:
                    break;
            }

        score = 0;
        start = false;
        pipeDistance = 800;
        sumPipe = 4;
        speedMultiplier = 8;
        initBird();
        initPipe();
        handler = new Handler();

        //Updating the screen
        r =  new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };

        if(Build.VERSION.SDK_INT >= 21){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttributes).setMaxStreams(5);
            this.soundPool = builder.build();
        }else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loadedSound = true;
            }
        });

        soundJump = this.soundPool.load(context, R.raw.jump_02, 1);
        soundGameover = this.soundPool.load(context, R.raw.yell, 1);
        soundHighScore = this.soundPool.load(context, R.raw.yahoo, 1);

        //Initializing game soundtrack
        mediaPlayer = MediaPlayer.create(context, R.raw.sillychipsong);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void initPipe() {
        distance = pipeDistance*Constants.SCREEN_HEIGHT/1920;
        arrPipes = new ArrayList<>();

        for (int i = 0; i < sumPipe; i++){
            //Initialize starting positions of the water pipes
            if(i< sumPipe /2){
                this.arrPipes.add(new Pipe(Constants.SCREEN_WIDTH + i*((Constants.SCREEN_WIDTH+200*Constants.SCREEN_WIDTH/1080)/(sumPipe /2)),
                        0, 200*Constants.SCREEN_WIDTH/1080, Constants.SCREEN_HEIGHT/2, speedMultiplier));
                this.arrPipes.get(this.arrPipes.size()-1).setBm(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe2));
                this.arrPipes.get(this.arrPipes.size()-1).randomY();
            }else{
                this.arrPipes.add(new Pipe(this.arrPipes.get(i- sumPipe /2).getX(), this.arrPipes.get(i- sumPipe /2).getY()
                +this.arrPipes.get(i- sumPipe /2).getHeight() + this.distance, 200*Constants.SCREEN_WIDTH/1080,
                        Constants.SCREEN_HEIGHT/2, speedMultiplier));

                this.arrPipes.get(this.arrPipes.size()-1).setBm(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe1));
            }
        }
    }

    private void initBird() {
        bird = new Bird();

        //Setting the size of the bird
        bird.setWidth(100*Constants.SCREEN_WIDTH/1080);
        bird.setHeight(100*Constants.SCREEN_HEIGHT/1920);

        //Setting the position of the bird
        bird.setX(100*Constants.SCREEN_WIDTH/1080);
        bird.setY(Constants.SCREEN_HEIGHT/2 - bird.getHeight()/2);

        //Creating an ArrayList of Bitmaps to set arrBms
        ArrayList<Bitmap> arrBms = new ArrayList<>();
        arrBms.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.bird1));
        arrBms.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.bird2));
        bird.setArrBms(arrBms);
        yelled = false;
        beatHighScore = false;
    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        if(start){
            bird.draw(canvas);

            //Drawing the pipes
            for(int i = 0; i < sumPipe; i++){
                //Collision Logic
                if(bird.getRect().intersect(arrPipes.get(i).getRect()) || (bird.getY()-bird.getHeight()) < 0
                        || bird.getY() > Constants.SCREEN_HEIGHT){
                    Pipe.speed = 0;
                    MainActivity.txt_score_over.setText(MainActivity.txt_score.getText());

                    switch(difficulty) {
                        case 1:
                            MainActivity.txt_best_score.setText(new StringBuilder().append(getResources().getString(R.string.txt_best_score_text)).append(bestScoreLvl1).toString());
                            break;
                        case 2:
                            MainActivity.txt_best_score.setText(new StringBuilder().append(getResources().getString(R.string.txt_best_score_text)).append(bestScoreLvl2).toString());
                            break;
                        case 3:
                            MainActivity.txt_best_score.setText(new StringBuilder().append(getResources().getString(R.string.txt_best_score_text)).append(bestScoreLvl3).toString());
                            break;
                        case 4:
                            MainActivity.txt_best_score.setText(new StringBuilder().append(getResources().getString(R.string.txt_best_score_text)).append(bestScoreLvl4).toString());
                            break;
                        default:
                            break;
                    }


                    //Changing Screen
                    MainActivity.txt_score.setVisibility(INVISIBLE);
                    MainActivity.rl_game_over.setVisibility(VISIBLE);
                    if(!yelled) {
                        mediaPlayer.pause();
                        yelled = true;
                        int streamIdYell = this.soundPool.play(this.soundGameover, (float) 0.5, (float) 0.5, 1, 0, 1f);
                    }
                }
                //Keeping track of the score
                if(this.bird.getX()+this.bird.getWidth() > arrPipes.get(i).getX()+arrPipes.get(i).getWidth()/2
                        && this.bird.getX()+this.bird.getWidth() <= arrPipes.get(i).getX()+arrPipes.get(i).getWidth()/2+Pipe.speed
                        && i < sumPipe /2){

                    score++;

                    if(score > bestScore){
                        bestScore = score;

                        if(!beatHighScore) {
                            int streamIdHighScore = this.soundPool.play(this.soundHighScore, (float) 0.5, (float) 0.5, 1, 0, 1f);
                            beatHighScore = true;
                        }
                        //Saving the score using shared preferences
                        SharedPreferences sp = context.getSharedPreferences("gameSetting", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        switch(difficulty) {
                            case 1:
                                bestScoreLvl1 = bestScore;
                                editor.putInt("bestScoreLvl1", bestScore);
                                break;
                            case 2:
                                bestScoreLvl2 = bestScore;
                                editor.putInt("bestScoreLvl2", bestScore);
                                break;
                            case 3:
                                bestScoreLvl3 = bestScore;
                                editor.putInt("bestScoreLvl3", bestScore);
                                break;
                            case 4:
                                bestScoreLvl4 = bestScore;
                                editor.putInt("bestScoreLvl4", bestScore);
                                break;
                            default:
                                break;
                        }
                        editor.apply();
                    }
                    MainActivity.txt_score.setText(score+"");
                }

                //Resetting position of outgoing water pipe
                if(this.arrPipes.get(i).getX() < -arrPipes.get(i).getWidth()){
                    this.arrPipes.get(i).setX(Constants.SCREEN_WIDTH);
                    if(i < sumPipe /2){
                        arrPipes.get(i).randomY();
                    }else{
                        arrPipes.get(i).setY(this.arrPipes.get(i- sumPipe /2).getY()
                                +this.arrPipes.get(i- sumPipe /2).getHeight() + this.distance);
                    }
                }
                this.arrPipes.get(i).draw(canvas);
            }
        }else{
            if(bird.getY() > Constants.SCREEN_HEIGHT/2){
                bird.setDrop(-15*Constants.SCREEN_HEIGHT/1920);
            }
            bird.draw(canvas);
        }
        //Re-drawing every 10 milliseconds
        handler.postDelayed(r, 10);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            bird.setDrop(-15);
            if(loadedSound){
                int streamIdJump = this.soundPool.play(this.soundJump, (float)0.5, (float)0.5, 1,0, 1f);
            }
        }
        return true;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start, int difficultyLevel) {
        this.start = start;
        this.difficulty = difficultyLevel;

        switch(difficultyLevel){
            case 1:
                pipeDistance = 800;
                sumPipe = 4;
                speedMultiplier = 8;
                break;
            case 2:
                pipeDistance = 700;
                sumPipe = 6;
                speedMultiplier = 10;
                break;
            case 3:
                pipeDistance = 600;
                sumPipe = 8;
                speedMultiplier = 15;
                break;
            case 4:
                pipeDistance = 500;
                sumPipe = 10;
                speedMultiplier = 20;
                break;
        }
        initPipe();
        initBird();
    }

    public void reset() {
        MainActivity.txt_score.setText("0");
        score = 0;
        initPipe();
        initBird();
        mediaPlayer.start();
    }

    public void refreshScores() {
        SharedPreferences sp = context.getSharedPreferences("gameSetting", Context.MODE_PRIVATE);

        if (sp != null) {
            bestScoreLvl1 = sp.getInt("bestScoreLvl1", 0);
            bestScoreLvl2 = sp.getInt("bestScoreLvl2", 0);
            bestScoreLvl3 = sp.getInt("bestScoreLvl3", 0);
            bestScoreLvl4 = sp.getInt("bestScoreLvl4", 0);
        }
    }
}