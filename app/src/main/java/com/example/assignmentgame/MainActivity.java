package com.example.assignmentgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static TextView txt_score, txt_best_score, txt_score_over, txt_choose_lvl;
    public static Button imgView_night, imgView_day;
    public static RelativeLayout rl_game_over;
    public static Button btn_start;
    public static Spinner spinner_levels;
    private GameView gv;
    private int difficulty;
    private RelativeLayout layout_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);


        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        setContentView(R.layout.activity_main);

        txt_score = findViewById(R.id.txt_score);
        txt_best_score = findViewById(R.id.txt_best_score);
        txt_score_over = findViewById(R.id.txt_score_over);
        rl_game_over = findViewById(R.id.rl_game_over);
        btn_start = findViewById(R.id.btn_start);
        gv = findViewById(R.id.gv);
        txt_choose_lvl = findViewById(R.id.txt_choose_lvl);
        imgView_night = findViewById(R.id.btn_night);
        imgView_day = findViewById(R.id.btn_day);
        layout_main = findViewById(R.id.layout_main);
        difficulty = 1;

        spinner_levels = findViewById(R.id.spinner_levels);
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this, R.array.Levels, R.layout.spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_levels.setAdapter(spinner_adapter);
        spinner_levels.setOnItemSelectedListener(this);


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gv.setStart(true, difficulty);
                txt_score.setVisibility(View.VISIBLE);
                btn_start.setVisibility(View.INVISIBLE);
                txt_choose_lvl.setVisibility(View.INVISIBLE);
                spinner_levels.setVisibility(View.INVISIBLE);
                imgView_night.setVisibility(View.INVISIBLE);
                imgView_day.setVisibility(View.INVISIBLE);
            }
        });

        rl_game_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start.setVisibility(View.VISIBLE);
                txt_choose_lvl.setVisibility(View.VISIBLE);
                spinner_levels.setVisibility(View.VISIBLE);
                imgView_night.setVisibility(View.VISIBLE);
                imgView_day.setVisibility(View.VISIBLE);
                rl_game_over.setVisibility(View.INVISIBLE);
                gv.setStart(false, difficulty);
                gv.reset();
            }
        });

        imgView_night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_main.setBackgroundResource(R.drawable.background_night);
            }
        });

        imgView_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_main.setBackgroundResource(R.drawable.background_day);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        difficulty = position + 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        difficulty = 1;
    }
}