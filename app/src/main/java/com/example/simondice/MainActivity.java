package com.example.simondice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private int increment = 1000;
    private int base = 500;
    private boolean acceptInput = false;
    private List<Integer> sequence = new ArrayList<>();
    private List<Integer> userSequence = new ArrayList<>();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void playOnClick(View view) {
        sequence.clear();
        userSequence.clear();
        acceptInput = false;
        playSequence((int) (Math.random() * ((6 - 3) + 1)) + 3);
    }

    private void playSequence(int length) {
        if (length <= 0) length = 10;
        for (int i = 0; i < length; i++) {
            sequence.add(getRandom());
        }

        int time = base;
        for (final Integer x : sequence) {
            final int aux = time;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    basicPlay(handler, x,aux);
                }
            }, time);
            time += increment;
        }
    }

    private void basicPlay(Handler handler, final int x,final int timeEnd) {
        changeColorBright(x);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeColorNormal(x, timeEnd);
            }
        }, base);
    }

    private int getRandom() {
        return (int) (Math.random() * ((4 - 1) + 1)) + 1;
    }

    public void buttonOnClick(View view) {
        if (acceptInput) {
            int input=0;
            switch (view.getId()) {
                case R.id.greenButton:
                    basicPlay(handler, 1,0);
                    input=1;
                    userSequence.add(1);
                    break;
                case R.id.yellowButton:
                    basicPlay(handler, 2,0);
                    input=2;
                    userSequence.add(2);
                    break;
                case R.id.redButton:
                    basicPlay(handler, 3,0);
                    input=3;
                    userSequence.add(3);
                    break;
                case R.id.blueButton:
                    basicPlay(handler, 4,0);
                    input=4;
                    userSequence.add(4);
                    break;
            }
            if (!sequence.get(userSequence.size()-1).equals(input)) {
                acceptInput = false;
                openDialog(false);
                userSequence.clear();
                sequence.clear();
            } else if (sequence.size() == userSequence.size() && sequence.equals(userSequence)) {
                acceptInput = false;
                openDialog(true);
                userSequence.clear();
                sequence.clear();
            }
        }
    }

    public void openDialog(Boolean win) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage((win) ? "Great job!" :"Wrong sequence, try again!");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

    public void changeColorNormal(int num, int timeEnd) {
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
        if (timeEnd == base + increment*(sequence.size()-1))
            acceptInput = true;

    }
}
