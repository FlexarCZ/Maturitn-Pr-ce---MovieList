package info.soducius.movieList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TutorialActivity extends AppCompatActivity {

    ImageView nextButton1;
    ImageView nextButton2;
    ImageView nextButton3;

    RelativeLayout tutorialLayout1;
    RelativeLayout tutorialLayout2;
    RelativeLayout tutorialLayout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        //******************************************
        // Tímto se onlouvám komukoliv kdo čte tento kód. Je to neskutečně zprasené. Nebyl již čas udělat pořádný tutoriál.
        //******************************************
        nextButton1 = (ImageView) findViewById(R.id.buttonNext);
        nextButton2 = (ImageView) findViewById(R.id.buttonNext2);
        nextButton3 = (ImageView) findViewById(R.id.buttonNext3);

        tutorialLayout1 = (RelativeLayout) findViewById(R.id.tutorialLayout1);
        tutorialLayout2 = (RelativeLayout) findViewById(R.id.tutorialLayout2);
        tutorialLayout3 = (RelativeLayout) findViewById(R.id.tutorialLayout3);

        nextButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialLayout1.setVisibility(View.GONE);
                tutorialLayout2.setVisibility(View.VISIBLE);
            }
        });

        nextButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialLayout2.setVisibility(View.GONE);
                tutorialLayout3.setVisibility(View.VISIBLE);
            }
        });

        nextButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialActivity.this,MainActivity.class));
            }
        });
    }
}
