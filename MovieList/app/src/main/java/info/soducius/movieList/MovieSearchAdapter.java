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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MovieSearchAdapter extends BaseAdapter{

    Context context;

    ArrayList<String> movieTitlesArrayList = new ArrayList<>();
    ArrayList<String> movieIDsArrayList = new ArrayList<>();
    ArrayList<String> movieURLsArrayList = new ArrayList<>();
    ArrayList<Double> movieRatingArrayList = new ArrayList<>();

    ImageView ratingFirstStar;
    ImageView ratingSecondStar;
    ImageView ratingThirdStar;
    ImageView ratingFourthStar;
    ImageView ratingFifthStar;

    ImageView goldStripe;

    private static LayoutInflater inflater=null;
    public MovieSearchAdapter(Activity mainActivity, ArrayList<String> movieTitles, ArrayList<String> movieIds, ArrayList<String> movieImageURLs, ArrayList<Double> movieRatings) {
        // TODO Auto-generated constructor stub
        context = mainActivity;

        movieTitlesArrayList = movieTitles;
        movieIDsArrayList = movieIds;
        movieURLsArrayList = movieImageURLs;
        movieRatingArrayList = movieRatings;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return movieTitlesArrayList.size();
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
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        final View cellView;
        cellView = inflater.inflate(R.layout.film_cell, null);

            TextView tv;
            ImageView img;

            tv = (TextView) cellView.findViewById(R.id.textView1);
            img = (ImageView) cellView.findViewById(R.id.filmImage);

        ratingFirstStar = (ImageView) cellView.findViewById(R.id.ratingFirstStar);
        ratingSecondStar = (ImageView) cellView.findViewById(R.id.ratingSecondStar);
        ratingThirdStar = (ImageView) cellView.findViewById(R.id.ratingThirdStar);
        ratingFourthStar = (ImageView) cellView.findViewById(R.id.ratingFourthStar);
        ratingFifthStar = (ImageView) cellView.findViewById(R.id.ratingFifthStar);

//**********************************************************
            String url = "https://image.tmdb.org/t/p/w185"+movieURLsArrayList.get(position);

            Picasso.with(context).
                    load(url).
                    fit().
                    error(R.drawable.error_film_image).
                    placeholder(R.drawable.placeholder_film_image).
                    into(img);
//***********************************************************

        Double rating = movieRatingArrayList.get(position);

        setRatingStars(rating);

        goldStripe = (ImageView) cellView.findViewById(R.id.goldStripe);
        if(rating > 7.5) {
            goldStripe.setVisibility(View.VISIBLE);
        }

            tv.setText(movieTitlesArrayList.get(position));


        cellView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cellView.findViewById(R.id.infoBar).getVisibility() == View.VISIBLE) {

                    String link = null;
                    link = "https://api.themoviedb.org/3/movie/" + movieIDsArrayList.get(position) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US";
                    Intent toFilmDetailActivityIntent = new Intent(context, MovieDetail.class);
                    toFilmDetailActivityIntent.putExtra("linkToSite", link);
                    toFilmDetailActivityIntent.putExtra("linkToImage", movieURLsArrayList.get(position));
                    context.startActivity(toFilmDetailActivityIntent);

                } else {
                    for (int i=0;i<parent.getChildCount();i++){
                        Log.i("PARANT ",String.valueOf(parent.getChildCount()));
                        parent.getChildAt(i).findViewById(R.id.infoBar).setVisibility(View.GONE);
                        parent.getChildAt(i).findViewById(R.id.movieCellOverlay).setVisibility(View.GONE);
                    }
                    cellView.findViewById(R.id.infoBar).setVisibility(View.VISIBLE);
                    cellView.findViewById(R.id.movieCellOverlay).setVisibility(View.VISIBLE);

                }
            }
        });

        cellView.findViewById(R.id.popularSeenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,movieTitlesArrayList.get(position)+" Was added to seen", Toast.LENGTH_SHORT).show();

                String linkToData = "https://api.themoviedb.org/3/movie/"+movieIDsArrayList.get(position)+"?api_key=95148fc363ff244cab388010dfbfa949&language=en-US";
                new AddToSeenOrWatchListAsyncTask("seenlist.txt", linkToData).execute();

                WatchlistFragment.SeenlistGridViewAdapter.notifyDataSetChanged();
            }
        });

        cellView.findViewById(R.id.bookMarkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You bookmarked "+ movieTitlesArrayList.get(position), Toast.LENGTH_SHORT).show();

                String linkToData = "https://api.themoviedb.org/3/movie/"+movieIDsArrayList.get(position)+"?api_key=95148fc363ff244cab388010dfbfa949&language=en-US";
                new AddToSeenOrWatchListAsyncTask("watchlist.txt", linkToData).execute();

                WatchlistFragment.WatchlistGridViewAdapter.notifyDataSetChanged();
            }
        });

        return cellView;
    }

    private class AddToSeenOrWatchListAsyncTask extends AsyncTask<String,Void,Void> {
        String fileName;
        String linkToData;
        AddToSeenOrWatchListAsyncTask(String fileName,String linkToData){
            this.fileName = fileName;
            this.linkToData = linkToData;
        }
        @Override
        protected Void doInBackground(String... params) {

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

                if(!doesContain) {
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            WatchlistFragment.WatchlistGridViewAdapter.notifyDataSetChanged();
            WatchlistFragment.SeenlistGridViewAdapter.notifyDataSetChanged();
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

}
