package info.soducius.movieList;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoviesTopRated extends AppCompatActivity {

    GridView gv;
    static BaseAdapter TopRatedMoviesGridViewAdapter;
    public ArrayList<String> topRatedMoviesArrayList = new ArrayList<>();

    int gridViewLoadLittleHelper = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_top_rated);


        TextView noInterneTextView = (TextView) findViewById(R.id.noInternettextView);

        gv = (GridView) findViewById(R.id.gridView1);

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

        if (MainActivity.isOnline()) {
            if (topRatedMoviesArrayList.isEmpty()) {
                new TopRatedMoviesJSONParser().execute(String.valueOf(1));
            }
            TopRatedMoviesGridViewAdapter = new TopRatedMoviesAdapter(MoviesTopRated.this, topRatedMoviesArrayList);
            gv.setAdapter(TopRatedMoviesGridViewAdapter);

            gv.setVisibility(View.VISIBLE);
            noInterneTextView.setVisibility(View.GONE);
        } else {
            gv.setVisibility(View.GONE);
            noInterneTextView.setVisibility(View.VISIBLE);
        }
        gv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onScroll(final AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem == 0) {
                    View v = gv.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top
                        //sv.setVisibility(View.VISIBLE);
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = gv.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0 && gridViewLoadLittleHelper != totalItemCount / 20) {
                        // reached the bottom
                        gridViewLoadLittleHelper = totalItemCount / 20;
                        Log.i("IIIIIIIIIIIIIIIIII", String.valueOf(gridViewLoadLittleHelper));
                        new TopRatedMoviesJSONParser().execute(String.valueOf(gridViewLoadLittleHelper + 1));
                    }
                } else {
                    // not top
                    //sv.setVisibility(View.GONE);
                }

            }
        });

    }

    private class TopRatedMoviesJSONParser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("https://api.themoviedb.org/3/movie/top_rated?api_key=95148fc363ff244cab388010dfbfa949&language=en-US&page=" + params[0]);

            try {
                JSONObject wholeJSON = new JSONObject(jsonStr);
                JSONArray oneArray = wholeJSON.getJSONArray("results");

                for (int i = 0; i < oneArray.length(); i++) {
                    JSONObject oneMovie = oneArray.getJSONObject(i);
                    topRatedMoviesArrayList.add(String.valueOf(oneMovie));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String i = "";
            return i;
        }

        @Override
        protected void onPostExecute(String i) {
            TopRatedMoviesGridViewAdapter.notifyDataSetChanged();
        }


    }
}
