package com.example.simondice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //audio vars
    private String audio = "d3,d3,d4,a4,g_3,g3,f3,d3,f3,g3";
    private List<String> rawIds = new ArrayList<>();
    private int index = 0;


    //Setting
    private boolean bluetooth;

    //params
    private int increment = 1000;
    private int base = 500;
    private boolean acceptInput = false;
    private List<Integer> sequence = new ArrayList<>();
    private List<Integer> userSequence = new ArrayList<>();
    private final Handler handler = new Handler();
    private long t0, t1;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor prefsEditor;
    public static List<Ranking> rankings = new ArrayList<>();
    private int difficulty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = getPreferences(MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
        initializeAudioIds();
        rankings = readRankings();

        //check bluetooth support
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth = (bluetoothAdapter != null);

        //is bluetooth enabled?
        if (bluetooth && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 10);
        }
    }

    public void playOnClick(View view) {
        sequence.clear();
        userSequence.clear();
        acceptInput = false;
        //set desired number of elements
        int length = (difficulty * 4);
        playSequence(length);
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
                    basicPlay(handler, x, aux);
                }
            }, time);
            time += increment;
        }
    }

    private void basicPlay(Handler handler, final int x, final int timeEnd) {
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
            if (userSequence.isEmpty())
                t0 = System.currentTimeMillis();

            int input = 0;
            switch (view.getId()) {
                case R.id.greenButton:
                    basicPlay(handler, 1, 0);
                    input = 1;
                    userSequence.add(1);
                    break;
                case R.id.yellowButton:
                    basicPlay(handler, 2, 0);
                    input = 2;
                    userSequence.add(2);
                    break;
                case R.id.redButton:
                    basicPlay(handler, 3, 0);
                    input = 3;
                    userSequence.add(3);
                    break;
                case R.id.blueButton:
                    basicPlay(handler, 4, 0);
                    input = 4;
                    userSequence.add(4);
                    break;
            }
            if (!sequence.get(userSequence.size() - 1).equals(input)) {
                acceptInput = false;
                openDialog(false);
                userSequence.clear();
            } else if (sequence.size() == userSequence.size() && sequence.equals(userSequence)) {
                acceptInput = false;
                openDialog(true);
                userSequence.clear();
            }
        }
    }

    public void openDialog(Boolean win) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        t1 = System.currentTimeMillis();
        final long points = (sequence.size() * 1000) - ((t1 - t0) / 7);
        sequence.clear();
        if (win) {
            playWinSound();
            final boolean topFive = entersRanking(points);
            final EditText input = new EditText(this);
            alertDialogBuilder.setMessage(!topFive ? "Great job!\n Got " + points + " points!"
                    : "Great job!\n Got " + points + " points!" + "\nInsert name for the rankings:(6 char max)");
            if (topFive) {
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Name");
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(6);
                input.setFilters(filterArray);
                alertDialogBuilder.setView(input);
            }
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (topFive) {
                                String name = input.getText().toString();
                                updateRanking(new Ranking((name.length() > 5) ? name.substring(0, 5).toUpperCase() : name.toUpperCase()
                                        , points));
                            }
                            arg0.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            alertDialogBuilder.setMessage("Wrong, try again.");
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
    }

    public void changeColorBright(int num) {
        if (index >= rawIds.size()) index = 0;

        playNote(rawIds.get(index++));
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
        if (timeEnd == base + increment * (sequence.size() - 1)) {
            acceptInput = true;
            final TextView go = findViewById(R.id.goText);
            go.setVisibility(View.VISIBLE);
            go.bringToFront();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    go.setVisibility(View.GONE);
                }
            }, 500);
        }

    }

    public void updateRanking(Ranking rank) {
        rankings.add(rank);
        Collections.sort(rankings);
        Collections.reverse(rankings);
        if (rankings.size() > 5)
            rankings = rankings.subList(0, 5);

        writeRank();
    }

    public void writeRank() {
        Gson gson = new Gson();
        String json = gson.toJson(rankings);
        prefsEditor.putString("ranking", json);
        prefsEditor.commit();
    }

    public List<Ranking> readRankings() {
        List<Ranking> total;
        Gson gson = new Gson();
        String json = mPrefs.getString("ranking", "");
        total = gson.fromJson(json, new TypeToken<List<Ranking>>() {
        }.getType());
        return (null != total) ? total : new ArrayList<Ranking>();
    }

    public boolean entersRanking(long points) {
        if (null != rankings && !rankings.isEmpty() && rankings.size() >= 5) {
            for (Ranking r : rankings) {
                if (r.getPoints() < points) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    public void onClickRankings(View view) {
        Intent intent = new Intent(MainActivity.this, RankingActivity.class);
        startActivity(intent);
    }


    public void onClickDificulty(View view) {
        final String[] levels = {"1", "2", "3"};
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Select Difficulty");
        alertDialogBuilder.setSingleChoiceItems(levels, difficulty - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                difficulty = Integer.parseInt(levels[i]);
            }
        });
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();
    }


    //============================================================================================
    //===                                           AUDIO
    //============================================================================================

    private void initializeAudioIds() {
        Collections.addAll(rawIds, audio.split(","));
    }

    private void playNote(String id) {
        final MediaPlayer mp = MediaPlayer.create(this, getResources().getIdentifier(id, "raw", getPackageName()));
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });
        mp.start();
    }

    private void playWinSound() {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.win);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });
        mp.start();
    }
}
