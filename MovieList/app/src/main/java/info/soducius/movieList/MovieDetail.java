package info.soducius.movieList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MovieDetail extends AppCompatActivity {

    Context context;

    String linkToJson;
    Integer isFromWatchlistOrSeenList;
    Integer id;
    String youtubeTrailerLink;

    JSONObject movieInfoJSONObject;
    JSONObject movieVideosJSON;

    ImageView movieImage;
    ImageView backdropImageView;
    ImageView backdropGalleryButton;
    ImageView backdropVideosButton;
    ImageView backdropPlayButton;

    TextView movieName;
    TextView movieGenreTextView;
    TextView movieOrigin;
    TextView movieYear;
    TextView movieLength;
    TextView movieRating;

    TextView moviePlot;

    TextView toolbarTitle;
    TextView toolbarQuote;

    ImageView toolbarWatchlistImageView;
    ImageView toolbarSeenlistImageView;

    GridView similiarMoviesGridView;
    TextView similiarMoviesNoInternetConnection;

    ArrayList<String> similiarMovieArrayList = new ArrayList<>();
    BaseAdapter SimiliarMoviesGridViewAdapter;

    ImageView addToCalendar;

    public static ScrollView movieDetailScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        movieDetailScrollView = (ScrollView) findViewById(R.id.movieDetailScrollView);

        similiarMoviesGridView = (GridView) findViewById(R.id.gridView1);
        similiarMoviesNoInternetConnection = (TextView) findViewById(R.id.similiarMoviesNoInternetConnection);

        backdropImageView = (ImageView) findViewById(R.id.backdropImageView);
        backdropGalleryButton = (ImageView) findViewById(R.id.backdropGalleryButton);
        backdropVideosButton = (ImageView) findViewById(R.id.backdropVideosButton);
        backdropPlayButton = (ImageView) findViewById(R.id.backdropPlayButton);

        isFromWatchlistOrSeenList = getIntent().getIntExtra("isFromWatchlistOrSeenlist", 0);
        id = getIntent().getIntExtra("id", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbarWatchlistImageView = (ImageView) toolbar.findViewById(R.id.toolbarWatchlistButton);
        toolbarSeenlistImageView = (ImageView) toolbar.findViewById(R.id.toolbarSeenlistButton);

        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbarName);
        toolbarQuote = (TextView)toolbar.findViewById(R.id.toolbarQuote);

        movieName = (TextView) findViewById(R.id.movieName);
        movieGenreTextView = (TextView) findViewById(R.id.movieGenre);
        movieOrigin = (TextView) findViewById(R.id.movieOrigin);
        movieYear = (TextView) findViewById(R.id.movieYear);
        movieLength = (TextView) findViewById(R.id.movieLength);
        movieRating = (TextView) findViewById(R.id.movieRating);

        moviePlot = (TextView) findViewById(R.id.moviePlot);

        movieImage = (ImageView) findViewById(R.id.movieImage);

        addToCalendar = (ImageView) findViewById(R.id.addToCalendar);
        try {
        if (MainActivity.isOnline()){
            linkToJson = getIntent().getStringExtra("linkToSite");
            new videoJSONFetcher().execute();
        } else if (isFromWatchlistOrSeenList == 1) {
            movieInfoJSONObject = new JSONObject(MainActivity.watchlistMovies.get(id));
            setAllInfoWithoutInternet();
        } else if (isFromWatchlistOrSeenList == 2){
            movieInfoJSONObject = new JSONObject(MainActivity.seenlistMovies.get(id));
            setAllInfoWithoutInternet();
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class videoJSONFetcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(linkToJson);
            try {
                movieInfoJSONObject = new JSONObject(jsonStr);

                String vidJSONStr = sh.makeServiceCall("https://api.themoviedb.org/3/movie/"+movieInfoJSONObject.getString("id")+"/videos?api_key=95148fc363ff244cab388010dfbfa949&language=en-US");
                movieVideosJSON = new JSONObject(vidJSONStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            setAllInfoWithInternet();
        }
    }

    protected void setAllInfoWithInternet(){
        try {
            new SimiliarMoviesJSONparser(movieInfoJSONObject.getInt("id")).execute();

            if (!movieVideosJSON.getJSONArray("results").isNull(0)) {
                youtubeTrailerLink = "http://www.youtube.com/watch?v=" + movieVideosJSON.getJSONArray("results").getJSONObject(0).getString("key");
                backdropPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeTrailerLink)));
                    }
                });
            }
            backdropGalleryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent toPhotoGallery = new Intent(MovieDetail.this, PhotoGallery.class);
                        toPhotoGallery.putExtra("link","https://api.themoviedb.org/3/movie/"+movieInfoJSONObject.getString("id")+"/images?api_key=95148fc363ff244cab388010dfbfa949");
                        startActivity(toPhotoGallery);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            backdropVideosButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent toVideoGallery = new Intent(MovieDetail.this, VideoGallery.class);
                        toVideoGallery.putExtra("link", "https://api.themoviedb.org/3/movie/"+movieInfoJSONObject.getString("id")+"/videos?api_key=95148fc363ff244cab388010dfbfa949");
                        startActivity(toVideoGallery);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            SimiliarMoviesGridViewAdapter = new SimiliarMoviesAdapter(MovieDetail.this, similiarMovieArrayList);
            similiarMoviesGridView.setAdapter(SimiliarMoviesGridViewAdapter);

            Picasso.with(context).
                    load("https://image.tmdb.org/t/p/w342"+movieInfoJSONObject.getString("poster_path")).
                    placeholder(R.drawable.placeholder_film_image).
                    error(R.drawable.error_film_image).
                    fit().
                    into(movieImage);

            Picasso.with(context).
                    load("https://image.tmdb.org/t/p/w780"+movieInfoJSONObject.getString("backdrop_path")).
                    placeholder(R.drawable.backdrop_placeholder).
                    fit().
                    into(backdropImageView);

            toolbarTitle.append(movieInfoJSONObject.getString("title"));
            if (movieInfoJSONObject.getString("tagline").equals("null")){
                toolbarQuote.append("");
            } else toolbarQuote.append(movieInfoJSONObject.getString("tagline"));
            movieName.setText(movieInfoJSONObject.getString("title"));
            String originalDate = String.valueOf(movieInfoJSONObject.getString("release_date").replaceFirst("-"," ").replaceFirst("-",".")+".");
            final String month = originalDate.substring(8,originalDate.length());
            final String day = originalDate.substring(5,originalDate.length()-3);
            final String year = originalDate.substring(0,4);
            movieYear.setText(month+day+" "+year);
            Calendar c = Calendar.getInstance();
            int actualYear = c.get(Calendar.YEAR);
            int actualDay = c.get(Calendar.DAY_OF_MONTH);
            int actualMonth = c.get(Calendar.MONTH);
            try{
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String str1 = String.valueOf(actualDay)+"/"+String.valueOf(actualMonth)+"/"+String.valueOf(actualYear);
                Date date1 = formatter.parse(str1);
                String str2 = month.substring(0,month.length()-1)+"/"+day.substring(0,day.length()-1)+"/"+year;
                Date date2 = formatter.parse(str2);
                if (date1.compareTo(date2)<0)
                {
                    //date2 is Greater than my date1
                    addToCalendar.setVisibility(View.VISIBLE);
                } else addToCalendar.setVisibility(View.GONE);
            }catch (ParseException e1){
                e1.printStackTrace();
            }
            Double vote = movieInfoJSONObject.getDouble("vote_average");
            movieRating.setText(String.valueOf(vote)+"/10");
            movieLength.setText(String.valueOf(movieInfoJSONObject.getInt("runtime"))+" min.");
            String movieOrigins = "";
            if (!movieInfoJSONObject.getJSONArray("production_countries").isNull(0)) {
                for (int i = 0; i < movieInfoJSONObject.getJSONArray("production_countries").length(); i++) {
                    movieOrigins = movieOrigins + movieInfoJSONObject.getJSONArray("production_countries").getJSONObject(i).getString("name") + ", ";
                }
                movieOrigin.append(movieOrigins.substring(0, movieOrigins.length() - 2));
            }
            if (!movieInfoJSONObject.getJSONArray("genres").isNull(0)) {
                String movieGenres = "";
                for (int i = 0; i < movieInfoJSONObject.getJSONArray("genres").length(); i++) {
                    movieGenres = movieGenres + movieInfoJSONObject.getJSONArray("genres").getJSONObject(i).getString("name") + ", ";
                }
                movieGenreTextView.append(movieGenres.substring(0, movieGenres.length() - 2));
            }
            moviePlot.setText(movieInfoJSONObject.getString("overview"));

            toolbarWatchlistImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new AddToSeenOrWatchListAsyncTask("watchlist.txt", "https://api.themoviedb.org/3/movie/" + String.valueOf(movieInfoJSONObject.getInt("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US").execute();
                        Toast.makeText(MovieDetail.this,movieInfoJSONObject.getString("title")+" was added to watchlist", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            toolbarSeenlistImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new AddToSeenOrWatchListAsyncTask("seenlist.txt", "https://api.themoviedb.org/3/movie/" + String.valueOf(movieInfoJSONObject.getString("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US").execute();
                        Toast.makeText(MovieDetail.this,movieInfoJSONObject.getString("title")+"was added to seenlist", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            addToCalendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent calIntent = new Intent(Intent.ACTION_INSERT);
                        calIntent.setType("vnd.android.cursor.item/event");
                        calIntent.putExtra(CalendarContract.Events.TITLE, movieInfoJSONObject.getString("title")+" has premiere");
                        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My local cinema");
                        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "I don't want to miss this awesome movie");
                        GregorianCalendar calDate = new GregorianCalendar(Integer.valueOf(year),Integer.valueOf(day.substring(0,day.length()-1))-1,Integer.valueOf(month.substring(0,month.length()-1)),9,0);
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calDate.getTimeInMillis());
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calDate.getTimeInMillis());
                        startActivity(calIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {e.printStackTrace();}
    }

    protected void setAllInfoWithoutInternet(){
        try {
            backdropPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MovieDetail.this,"No internet connecction", Toast.LENGTH_SHORT).show();
                }
            });
            backdropGalleryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MovieDetail.this,"No internet connecction", Toast.LENGTH_SHORT).show();
                }
            });
            backdropVideosButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MovieDetail.this,"No internet connecction", Toast.LENGTH_SHORT).show();
                }
            });

            similiarMoviesGridView.setVisibility(View.GONE);
            similiarMoviesNoInternetConnection.setVisibility(View.VISIBLE);

            Picasso.with(context).
                    load("https://image.tmdb.org/t/p/w342"+movieInfoJSONObject.getString("poster_path")).
                    error(R.drawable.error_film_image).
                    fit().
                    into(movieImage);
            toolbarTitle.append(movieInfoJSONObject.getString("title"));
            if (movieInfoJSONObject.getString("tagline").equals("null")){
                toolbarQuote.append("");
            } else toolbarQuote.append(movieInfoJSONObject.getString("tagline"));
            movieName.setText(movieInfoJSONObject.getString("title"));
            String originalDate = String.valueOf(movieInfoJSONObject.getString("release_date").replaceFirst("-"," ").replaceFirst("-",".")+".");
            final String month = originalDate.substring(8,originalDate.length());
            final String day = originalDate.substring(5,originalDate.length()-3);
            final String year = originalDate.substring(0,4);
            movieYear.setText(month+day+" "+year);
            Calendar c = Calendar.getInstance();
            int actualYear = c.get(Calendar.YEAR);
            int actualDay = c.get(Calendar.DAY_OF_MONTH);
            int actualMonth = c.get(Calendar.MONTH);
            try{
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String str1 = String.valueOf(actualDay)+"/"+String.valueOf(actualMonth)+"/"+String.valueOf(actualYear);
                Date date1 = formatter.parse(str1);
                String str2 = month.substring(0,month.length()-1)+"/"+day.substring(0,day.length()-1)+"/"+year;
                Date date2 = formatter.parse(str2);
                if (date1.compareTo(date2)<0)
                {
                    //date2 is Greater than my date1
                    addToCalendar.setVisibility(View.VISIBLE);
                }
            }catch (ParseException e1){
                e1.printStackTrace();
            }
            Double vote = movieInfoJSONObject.getDouble("vote_average");
            movieRating.setText(String.valueOf(vote)+"/10");
            movieLength.setText(String.valueOf(movieInfoJSONObject.getInt("runtime"))+" min.");
            String movieOrigins = "";
            if (!movieInfoJSONObject.getJSONArray("production_countries").isNull(0)) {
                for (int i = 0; i < movieInfoJSONObject.getJSONArray("production_countries").length(); i++) {
                    movieOrigins = movieOrigins + movieInfoJSONObject.getJSONArray("production_countries").getJSONObject(i).getString("name") + ", ";
                }
                movieOrigin.append(movieOrigins.substring(0, movieOrigins.length() - 1));
            }
            if (!movieInfoJSONObject.getJSONArray("genres").isNull(0)) {
                String movieGenres = "";
                for (int i = 0; i < movieInfoJSONObject.getJSONArray("genres").length(); i++) {
                    movieGenres = movieGenres + movieInfoJSONObject.getJSONArray("genres").getJSONObject(i).getString("name") + ", ";
                }
                movieGenreTextView.append(movieGenres.substring(0, movieGenres.length() - 1));
            }
            moviePlot.setText(movieInfoJSONObject.getString("overview"));

            toolbarWatchlistImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new AddToSeenOrWatchListWithoutInternetAsyncTask("watchlist.txt").execute();
                        Toast.makeText(MovieDetail.this,movieInfoJSONObject.getString("title")+" was added to watchlist", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            toolbarSeenlistImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new AddToSeenOrWatchListWithoutInternetAsyncTask("seenlist.txt").execute();
                        Toast.makeText(MovieDetail.this,movieInfoJSONObject.getString("title")+"was added to seenlist", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            addToCalendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent calIntent = new Intent(Intent.ACTION_INSERT);
                        calIntent.setType("vnd.android.cursor.item/event");
                        calIntent.putExtra(CalendarContract.Events.TITLE, movieInfoJSONObject.getString("title")+" has premiere");
                        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My local cinema");
                        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "I don't want to miss this awesome movie");
                        GregorianCalendar calDate = new GregorianCalendar(Integer.valueOf(year),Integer.valueOf(day.substring(0,day.length()-1))-1,Integer.valueOf(month.substring(0,month.length()-1)),9,0);
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calDate.getTimeInMillis());
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calDate.getTimeInMillis());
                        startActivity(calIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {e.printStackTrace();}
    }

    private void writeToWatchlistOrSeenlistFile(String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MovieDetail.this.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String fileName) {

        String ret = "";
        try {
            FileInputStream inputStream = MovieDetail.this.openFileInput(fileName);

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

    private class AddToSeenOrWatchListAsyncTask extends AsyncTask<String, Void, String> {
        String fileName;
        String linkToData;
        AddToSeenOrWatchListAsyncTask(String fileName,String linkToData){
            this.fileName = fileName;
            this.linkToData = linkToData;
        }
        @Override
        protected String doInBackground(String... params) {

            String fileString = readFromFile(fileName);

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(linkToData);
            try {
                JSONObject wholeJSON = new JSONObject();
                Boolean doesContain = false;
                if (!fileString.equals("")) {
                    wholeJSON = new JSONObject(fileString);

                    for (int i=0;i<wholeJSON.length();i++){
                        JSONObject JSONStr = new JSONObject(jsonStr);
                        if (String.valueOf(JSONStr.getString("title")).equals(wholeJSON.getJSONArray(String.valueOf(i)).getJSONObject(0).getString("title"))){
                            doesContain = true;
                        }
                    }
                }

                if (!doesContain) {
                    JSONObject oneMovieObject = new JSONObject(jsonStr);

                    JSONArray oneMovieArray = new JSONArray();
                    oneMovieArray.put(oneMovieObject);

                    wholeJSON.put(String.valueOf(wholeJSON.length()), oneMovieArray);

                    fileString = wholeJSON.toString();
                    if (fileName.equals("watchlist.txt")) {
                        MainActivity.watchlistMovies.add(String.valueOf(oneMovieObject));
                    } else if (fileName.equals("seenlist.txt")) {
                        MainActivity.seenlistMovies.add(String.valueOf(oneMovieObject));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            writeToWatchlistOrSeenlistFile(fileName,fileString);

            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            if (fileName.equals("watchlist.txt")) {WatchlistFragment.WatchlistGridViewAdapter.notifyDataSetChanged();}
            if (fileName.equals("seenlist.txt")) {WatchlistFragment.SeenlistGridViewAdapter.notifyDataSetChanged();}
        }
    }

    private class AddToSeenOrWatchListWithoutInternetAsyncTask extends AsyncTask<String, Void, String> {
        String fileName;
        AddToSeenOrWatchListWithoutInternetAsyncTask(String fileName){
            this.fileName = fileName;
        }
        @Override
        protected String doInBackground(String... params) {

            String fileString = readFromFile(fileName);

            try {
                JSONObject wholeJSON = new JSONObject();
                Boolean doesContain = false;
                if (!fileString.equals("")) {
                    wholeJSON = new JSONObject(fileString);

                    for (int i=0;i<wholeJSON.length();i++){
                        if (String.valueOf(movieInfoJSONObject.getString("title")).equals(wholeJSON.getJSONArray(String.valueOf(i)).getJSONObject(0).getString("title"))){
                            doesContain = true;
                        }
                    }
                }

                if (!doesContain) {
                    JSONArray oneMovieArray = new JSONArray();
                    oneMovieArray.put(movieInfoJSONObject);

                    wholeJSON.put(String.valueOf(wholeJSON.length()), oneMovieArray);

                    fileString = wholeJSON.toString();
                    if (fileName.equals("watchlist.txt")) {
                        MainActivity.watchlistMovies.add(String.valueOf(movieInfoJSONObject));
                    } else if (fileName.equals("seenlist.txt")) {
                        MainActivity.seenlistMovies.add(String.valueOf(movieInfoJSONObject));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            writeToWatchlistOrSeenlistFile(fileName,fileString);

            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            if (fileName.equals("watchlist.txt")) {WatchlistFragment.WatchlistGridViewAdapter.notifyDataSetChanged();}
            if (fileName.equals("seenlist.txt")) {WatchlistFragment.SeenlistGridViewAdapter.notifyDataSetChanged();}

        }
    }


    private class SimiliarMoviesJSONparser extends AsyncTask<String, Void, String> {
        int id;
        SimiliarMoviesJSONparser(int id){
            this.id = id;
        }
        @Override
        protected String doInBackground(String... params) {

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("https://api.themoviedb.org/3/movie/"+String.valueOf(id)+"/similar?api_key=95148fc363ff244cab388010dfbfa949&language=en-US&page=1");

            try {
                JSONObject wholeJSON = new JSONObject(jsonStr);
                JSONArray resultsArray = wholeJSON.getJSONArray("results");

                for (int i=0; i<3; i++){
                    JSONObject oneMovie = resultsArray.getJSONObject(i);
                    similiarMovieArrayList.add(String.valueOf(oneMovie));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String i = "";
            return i;
        }
        @Override
        protected void onPostExecute (String i) {
            SimiliarMoviesGridViewAdapter.notifyDataSetChanged();
        }

    }

}
