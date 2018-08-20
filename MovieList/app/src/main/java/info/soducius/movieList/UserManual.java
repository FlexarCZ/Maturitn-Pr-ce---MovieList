package info.soducius.movieList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserManual extends AppCompatActivity {

    TextView welcomeTextView;
    TextView tutorialWelcome;
    Integer easterCount;
    ImageView easterEggImageView;

    ImageView toAppButton;
    ImageView toTutorialButton;

    private Preferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);
        mPrefs = new Preferences(this);

        easterCount = 0;

        welcomeTextView = (TextView)findViewById(R.id.welcomeTextView);
        easterEggImageView = (ImageView) findViewById(R.id.easterEggImageView);
        welcomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easterEgg();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (mPrefs.getFirstRun()){
            mPrefs.setFirstRun(false);
            toAppButton = (ImageView) findViewById(R.id.toAppButton);
            toTutorialButton = (ImageView) findViewById(R.id.tutorialButton);
            tutorialWelcome = (TextView) findViewById(R.id.textView3);

            toTutorialButton.setVisibility(View.VISIBLE);
            toAppButton.setVisibility(View.VISIBLE);
            toAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {startActivity(new Intent(UserManual.this,MainActivity.class));}
            });

            toTutorialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserManual.this,TutorialActivity.class));
                }
            });

        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }



    }

    private void easterEgg() {
        easterCount++;
        if (easterCount == 5){
            easterEggImageView.setVisibility(View.VISIBLE);
            easterEggImageView.animate().rotationBy(360f).alpha(1f).setDuration(500);
            easterEggImageView.clearAnimation();
            easterCount = 0;
        }
    }
}
