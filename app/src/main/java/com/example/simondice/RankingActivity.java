package com.example.simondice;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class RankingActivity extends AppCompatActivity {

    private TextView first,second,third,fourth,fifth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranquing);
        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);
        fifth = findViewById(R.id.fifth);

        setRankings(MainActivity.rankings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRankings(MainActivity.rankings);
    }

    public void setRankings(List<Ranking> lrank){
        Collections.sort(lrank);
        Collections.reverse(lrank);
        String aux;
        for(int i = 0; i<lrank.size();i++){
            switch (i){
                case 0:
                    aux = "1st "+lrank.get(i).getName()+"\t\t\t"+lrank.get(i).getPoints();
                    first.setText(aux);
                    break;
                case 1:
                    aux = "2nd "+lrank.get(i).getName()+"\t\t\t"+lrank.get(i).getPoints();
                    second.setText(aux);
                    break;
                case 2:
                    aux = "3rd "+lrank.get(i).getName()+"\t\t\t"+lrank.get(i).getPoints();
                    third.setText(aux);
                    break;
                case 3:
                    aux = "4th "+lrank.get(i).getName()+"\t\t\t\t"+lrank.get(i).getPoints();
                    fourth.setText(aux);
                    break;
                case 4:
                    aux = "5th "+lrank.get(i).getName()+"\t\t\t\t"+lrank.get(i).getPoints();
                    fifth.setText(aux);
                    break;

            }
        }
    }
    public void onClickReturn(View view) {
        this.onBackPressed();
    }
}
