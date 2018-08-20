package info.soducius.movieList;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static ArrayList<String> watchlistMovies = new ArrayList<>();
    public static ArrayList<String> seenlistMovies = new ArrayList<>();

    private ViewPager viewPager;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private String[] pageTitle = {"Popular", "Watchlist", "Seenlist"};

    private Preferences mPrefs;
    public static MainActivity mn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager = (ViewPager)findViewById(R.id.view_pager);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SearchView toolbarSearchView = (SearchView)toolbar.findViewById(R.id.toolbarSearchView);
        toolbarSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intentToMovieSearchActivity = new Intent(MainActivity.mn, MovieSearch.class);
                intentToMovieSearchActivity.putExtra("query", query);
                startActivity(intentToMovieSearchActivity);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        mPrefs = new Preferences(this);
        mn = MainActivity.this;

        if (mPrefs.getFirstRun()){
            try {
                OutputStreamWriter outputStreamWriterwatchlist = new OutputStreamWriter(openFileOutput("watchlist.txt", Context.MODE_PRIVATE));
                outputStreamWriterwatchlist.write("");
                outputStreamWriterwatchlist.close();
                //****************
                OutputStreamWriter outputStreamWriterseenlist = new OutputStreamWriter(openFileOutput("seenlist.txt", Context.MODE_PRIVATE));
                outputStreamWriterseenlist.write("");
                outputStreamWriterseenlist.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(MainActivity.this,UserManual.class));
        }

        String watchlistString = readFromFile("watchlist.txt");
        if(watchlistString.equals("") && watchlistMovies.isEmpty()) {
            // idn...
        } else {
            try {
                JSONObject wholeJSON = new JSONObject(watchlistString);
                for(int i=0; i < wholeJSON.length(); i++){
                    JSONArray oneMovieArray = wholeJSON.getJSONArray(String.valueOf(i));
                    JSONObject oneMovieObject = oneMovieArray.getJSONObject(0);
                    watchlistMovies.add(String.valueOf(oneMovieObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String seenlistString = readFromFile("seenlist.txt");
        if(seenlistString.equals("")&& seenlistMovies.isEmpty()) {
            // idn...
        } else {
            try {
                JSONObject wholeJSON = new JSONObject(seenlistString);
                for(int i=0; i < wholeJSON.length(); i++){
                    JSONArray oneMovieArray = wholeJSON.getJSONArray(String.valueOf(i));
                    JSONObject oneMovieObject = oneMovieArray.getJSONObject(0);
                    seenlistMovies.add(String.valueOf(oneMovieObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //create default navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //setting Tab layout (number of Tabs = number of ViewPager pages)
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for (int i = 0; i < 3; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(pageTitle[i]));
        }

        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //handling navigation view item event
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        //set viewpager adapter
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //change Tab selection when swipe ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //change ViewPager page when tab selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.fr1) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.fr2) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.fr3) {
            viewPager.setCurrentItem(2);
        } else if (id == R.id.top_rated) {
            Intent intent = new Intent(this, MoviesTopRated.class);
            startActivity(intent);
        }else if (id == R.id.user_manual){
            Intent intent = new Intent(this, UserManual.class);
            startActivity(intent);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String readFromFile(String fileName) {

        String ret = "";
        try {
            FileInputStream inputStream = openFileInput(fileName);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();

        } catch (IOException ignored) {
        }
        return ret;
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mn.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
