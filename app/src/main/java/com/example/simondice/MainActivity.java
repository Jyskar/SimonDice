package com.example.simondice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private int increment = 1000;
    private int base = 500;
    private  List<Integer> sequence = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void playOnClick(View view){
        sequence.clear();
        playSequence((int) (Math.random() * ((20 - 5) + 1)) + 5);
    }

    private void playSequence(int length) {
        if (length <= 0) length = 10;
        for (int i = 0; i < length; i++) {
            sequence.add(getRandom());
        }

        final Handler handler = new Handler();
        int time= base;
        for (final Integer x : sequence) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        basicPlay(handler, x);
                    }
                },time);
                time += increment;
        }
    }
    private void basicPlay(Handler handler, final int x){
        changeColorBright(x);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeColorNormal(x);
            }
        },base);
    }

    private int getRandom() {
        return (int) (Math.random() * ((4 - 1) + 1)) + 1;
    }

    public void changeColorBright(int num) {
        Button blue = findViewById(R.id.blueButton);
        Button red = findViewById(R.id.redButton);
        Button yellow = findViewById(R.id.yellowButton);
        Button green = findViewById(R.id.greenButton);

        switch (num) {
            case 1:
                green.setBackgroundColor(getResources().getColor(R.color.brightGreen));
                break;
            case 2:
                yellow.setBackgroundColor(getResources().getColor(R.color.brightYellow));
                break;
            case 3:
                red.setBackgroundColor(getResources().getColor(R.color.brightRed));
                break;
            case 4:
                blue.setBackgroundColor(getResources().getColor(R.color.brightBlue));
                break;
        }


    }

    public void changeColorNormal(int num) {
        Button blue = findViewById(R.id.blueButton);
        Button red = findViewById(R.id.redButton);
        Button yellow = findViewById(R.id.yellowButton);
        Button green = findViewById(R.id.greenButton);

        switch (num) {
            case 1:
                green.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                break;
            case 2:
                yellow.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                break;
            case 3:
                red.setBackgroundColor(getResources().getColor(R.color.colorRed));
                break;
            case 4:
                blue.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                break;
        }


    }
}
