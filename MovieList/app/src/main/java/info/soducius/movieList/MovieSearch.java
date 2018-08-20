package info.soducius.movieList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MovieSearch extends AppCompatActivity {

    BaseAdapter movieSearchGridViewAdapter;

    ArrayList<String> movieTitlesArrayList = new ArrayList<>();
    ArrayList<String> movieIDsArrayList = new ArrayList<>();
    ArrayList<String> movieURLsArrayList = new ArrayList<>();
    ArrayList<Double> movieRatingArrayList = new ArrayList<>();

    String query;

    GridView gv;
    TextView nothingFoundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);

        query = getIntent().getStringExtra("query");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //startActivity(new Intent(MovieSearch.this,MainActivity.class));
            }
        });

        SearchView toolbarSearchView = (SearchView)toolbar.findViewById(R.id.toolbarSearchView);
        toolbarSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.mn, "Vyhledal si: "+query, Toast.LENGTH_SHORT).show();
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

        toolbarSearchView.setIconifiedByDefault(false);
        toolbarSearchView.setQuery(query, false);

        nothingFoundTextView = (TextView) findViewById(R.id.nothingFoundTextView);

        query = query.replaceAll(" ","%20");
        String queryURL = "https://api.themoviedb.org/3/search/movie?api_key=95148fc363ff244cab388010dfbfa949&language=en-US&query="+query+"&page=1&include_adult=false";
        Log.i("QUERY ZAČÁTEK: ", query);
        if (MainActivity.isOnline()) {
            new resultsParserAsyncTask().execute(queryURL);
        }
        gv = (GridView) findViewById(R.id.gridView1);

        movieSearchGridViewAdapter = new MovieSearchAdapter(this, movieTitlesArrayList, movieIDsArrayList, movieURLsArrayList, movieRatingArrayList);
        gv.setAdapter(movieSearchGridViewAdapter);

        //Hides keyboard on start (fkn searchView...)
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private class resultsParserAsyncTask extends AsyncTask<String,Void, String>{

        @Override
        protected String doInBackground(String... params) {

            String URL = params[0];

            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(URL);

            try {
                JSONObject wholeJSON = new JSONObject(jsonStr);
                JSONArray results = wholeJSON.getJSONArray("results");

                Log.i("LEEEENGTH", String.valueOf(results.length()));

                for (int i=0; i<results.length(); i++){
                    JSONObject oneMovie = results.getJSONObject(i);
                    String title = oneMovie.getString("title");
                    movieTitlesArrayList.add(title);

                    String imageURL = oneMovie.getString("poster_path");
                    movieURLsArrayList.add(imageURL);

                    String detailLink = oneMovie.getString("id");
                    movieIDsArrayList.add(detailLink);

                    Double rating = oneMovie.getDouble("vote_average");
                    movieRatingArrayList.add(rating);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            movieSearchGridViewAdapter.notifyDataSetChanged();

            if (movieTitlesArrayList.isEmpty()) {
                gv.setVisibility(View.GONE);
                nothingFoundTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}


