package info.soducius.movieList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import java.util.ArrayList;

public class SimiliarMoviesAdapter extends BaseAdapter{

    Context context;

    ArrayList<String> moviesArrayList = new ArrayList<>();

    ImageView movieImageView;

    JSONObject oneMovieJSONObject;

    ImageView ratingFirstStar;
    ImageView ratingSecondStar;
    ImageView ratingThirdStar;
    ImageView ratingFourthStar;
    ImageView ratingFifthStar;

    ImageView goldStripe;

    private static LayoutInflater inflater=null;
    public SimiliarMoviesAdapter(Activity activity, ArrayList<String> similiarMovieArrayList) {
        // TODO Auto-generated constructor stub
        context = activity;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        moviesArrayList = similiarMovieArrayList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return moviesArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        final View film_cell;
        film_cell = inflater.inflate(R.layout.film_cell, null);

        ratingFirstStar = (ImageView) film_cell.findViewById(R.id.ratingFirstStar);
        ratingSecondStar = (ImageView) film_cell.findViewById(R.id.ratingSecondStar);
        ratingThirdStar = (ImageView) film_cell.findViewById(R.id.ratingThirdStar);
        ratingFourthStar = (ImageView) film_cell.findViewById(R.id.ratingFourthStar);
        ratingFifthStar = (ImageView) film_cell.findViewById(R.id.ratingFifthStar);

        String url = null;

        try {
            oneMovieJSONObject = new JSONObject(moviesArrayList.get(position));
            url ="https://image.tmdb.org/t/p/w185"+ oneMovieJSONObject.getString("poster_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Double rating = 0d;
        try {
            rating = oneMovieJSONObject.getDouble("vote_average");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setRatingStars(rating);

        goldStripe = (ImageView) film_cell.findViewById(R.id.goldStripe);
        if(rating > 7.5) {
            goldStripe.setVisibility(View.VISIBLE);
        }

        TextView tv;

        tv = (TextView) film_cell.findViewById(R.id.textView1);

        try {
            tv.setText(oneMovieJSONObject.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        movieImageView = (ImageView) film_cell.findViewById(R.id.filmImage);

            Picasso.with(context).
                    load(url).
                    fit().
                    placeholder(R.drawable.placeholder_film_image).
                    into(movieImageView);

        MovieDetail.movieDetailScrollView.setScrollY(-500);

        film_cell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (film_cell.findViewById(R.id.infoBar).getVisibility() == View.VISIBLE) {

                    String link = null;
                    Intent toFilmDetailActivityIntent = null;
                    try {
                        JSONObject ObjectForLink = new JSONObject(moviesArrayList.get(position));
                        link = "https://api.themoviedb.org/3/movie/" + String.valueOf(ObjectForLink.getInt("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US";
                        toFilmDetailActivityIntent = new Intent(context, MovieDetail.class);
                        toFilmDetailActivityIntent.putExtra("isFromWatchlistOrSeenlist", 2);
                        toFilmDetailActivityIntent.putExtra("id", position);
                        toFilmDetailActivityIntent.putExtra("linkToSite", link);
                        toFilmDetailActivityIntent.putExtra("linkToImage", String.valueOf(ObjectForLink.getString("poster_path")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(toFilmDetailActivityIntent);

                } else {
                    for (int i=0;i<parent.getChildCount();i++){
                        parent.getChildAt(i).findViewById(R.id.infoBar).setVisibility(View.GONE);
                        parent.getChildAt(i).findViewById(R.id.movieCellOverlay).setVisibility(View.GONE);
                    }
                    film_cell.findViewById(R.id.infoBar).setVisibility(View.VISIBLE);
                    film_cell.findViewById(R.id.movieCellOverlay).setVisibility(View.VISIBLE);
                }
            }
        });


        film_cell.findViewById(R.id.popularSeenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject JSONObject = new JSONObject(moviesArrayList.get(position));
                    Toast.makeText(context,JSONObject.getString("title")+" was removed from seenlist", Toast.LENGTH_SHORT).show();
                    new AddToSeenOrWatchListAsyncTask("seenlist.txt", "https://api.themoviedb.org/3/movie/" + String.valueOf(JSONObject.getString("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US").execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        film_cell.findViewById(R.id.bookMarkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject JSONObject = new JSONObject(moviesArrayList.get(position));
                    Toast.makeText(context, "You bookmarked "+ JSONObject.getString("title"), Toast.LENGTH_SHORT).show();
                    new AddToSeenOrWatchListAsyncTask("watchlist.txt", "https://api.themoviedb.org/3/movie/" + String.valueOf(JSONObject.getString("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US").execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return film_cell;
    }

    private void setRatingStars(Double rating) {
        if (rating < 0.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else if (rating < 1.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_half_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else if (rating < 2.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else if (rating < 3.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_half_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else if (rating < 4.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else if (rating < 5.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_half_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else if (rating < 6.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else if (rating < 7.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_half_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else if (rating < 8.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else if (rating < 9.5) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_half_black_24dp);
        } else if (rating < 10) {
            ratingFirstStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingSecondStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingThirdStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingFourthStar.setImageResource(R.drawable.ic_star_black_24dp);
            ratingFifthStar.setImageResource(R.drawable.ic_star_black_24dp);
        }
    }

    private class AddToSeenOrWatchListAsyncTask extends AsyncTask<String, Void, String> {
        String fileName;
        String linkToData;

        AddToSeenOrWatchListAsyncTask(String fileName, String linkToData) {
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

                    for (int i = 0; i < wholeJSON.length(); i++) {
                        JSONObject JSONStr = new JSONObject(jsonStr);
                        if (String.valueOf(JSONStr.getString("title")).equals(wholeJSON.getJSONArray(String.valueOf(i)).getJSONObject(0).getString("title"))) {
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
            writeToWatchlistOrSeenlistFile(fileName, fileString);

            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            if (fileName.equals("watchlist.txt")) {
                WatchlistFragment.WatchlistGridViewAdapter.notifyDataSetChanged();
            }
            if (fileName.equals("seenlist.txt")) {
                WatchlistFragment.SeenlistGridViewAdapter.notifyDataSetChanged();
            }

        }
    }

    private void writeToWatchlistOrSeenlistFile(String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
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
            FileInputStream inputStream = context.openFileInput(fileName);

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
}
