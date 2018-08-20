package info.soducius.movieList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

public class PopularMoviesAdapter extends BaseAdapter{

    Context context;
    ArrayList<String> popularMoviesArrayList = new ArrayList<>();

    JSONObject oneMovieJSONObject = new JSONObject();

    ImageView ratingFirstStar;
    ImageView ratingSecondStar;
    ImageView ratingThirdStar;
    ImageView ratingFourthStar;
    ImageView ratingFifthStar;

    ImageView goldStripe;

    private static LayoutInflater inflater=null;
    public PopularMoviesAdapter(Activity mainActivity, ArrayList<String> popularMoviesArrayListParam) {
        // TODO Auto-generated constructor stub
        context = mainActivity;

        popularMoviesArrayList = popularMoviesArrayListParam;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return popularMoviesArrayList.size();
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
        final View cellView;
        cellView = inflater.inflate(R.layout.film_cell, null);

        ratingFirstStar = (ImageView) cellView.findViewById(R.id.ratingFirstStar);
        ratingSecondStar = (ImageView) cellView.findViewById(R.id.ratingSecondStar);
        ratingThirdStar = (ImageView) cellView.findViewById(R.id.ratingThirdStar);
        ratingFourthStar = (ImageView) cellView.findViewById(R.id.ratingFourthStar);
        ratingFifthStar = (ImageView) cellView.findViewById(R.id.ratingFifthStar);

        try {
            oneMovieJSONObject = new JSONObject(popularMoviesArrayList.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView tv;
        ImageView img;

        Double rating = 0d;
        try {
            rating = oneMovieJSONObject.getDouble("vote_average");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setRatingStars(rating);
        goldStripe = (ImageView) cellView.findViewById(R.id.goldStripe);
        if(rating > 7.5) {
            goldStripe.setVisibility(View.VISIBLE);
        }

        tv = (TextView) cellView.findViewById(R.id.textView1);
        img = (ImageView) cellView.findViewById(R.id.filmImage);
//*******************************
        String url = "";
            try {
                url ="https://image.tmdb.org/t/p/w185" + oneMovieJSONObject.getString("poster_path");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Picasso.with(context).
                    load(url).
                    fit().
                    placeholder(R.drawable.placeholder_film_image).
                    into(img);
//*******************************

            try {
                tv.setText(oneMovieJSONObject.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        cellView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cellView.findViewById(R.id.infoBar).getVisibility() == View.VISIBLE) {
                    try {
                        JSONObject JSONObject = new JSONObject(popularMoviesArrayList.get(position));
                    Intent toFilmDetailActivityIntent = new Intent(context, MovieDetail.class);
                    toFilmDetailActivityIntent.putExtra("linkToSite", "https://api.themoviedb.org/3/movie/" + String.valueOf(JSONObject.getString("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US");
                    context.startActivity(toFilmDetailActivityIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int i=0;i<parent.getChildCount();i++){
                        parent.getChildAt(i).findViewById(R.id.infoBar).setVisibility(View.GONE);
                        parent.getChildAt(i).findViewById(R.id.movieCellOverlay).setVisibility(View.GONE);
                    }
                    cellView.findViewById(R.id.infoBar).setVisibility(View.VISIBLE);
                    cellView.findViewById(R.id.movieCellOverlay).setVisibility(View.VISIBLE);
                }
            }
        });

        cellView.findViewById(R.id.popularSeenButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject JSONObject = new JSONObject(popularMoviesArrayList.get(position));
                    final Toast toast = Toast.makeText(context, JSONObject.getString("title")+"was added to Seenlist", Toast.LENGTH_SHORT);
                    toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 750);
                    //Toast.makeText(context,JSONObject.getString("title")+" Was added to seen", Toast.LENGTH_SHORT).show();
                new AddToSeenOrWatchListAsyncTask("seenlist.txt", "https://api.themoviedb.org/3/movie/" + String.valueOf(JSONObject.getString("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US").execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        cellView.findViewById(R.id.bookMarkButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject JSONObject = new JSONObject(popularMoviesArrayList.get(position));
                    final Toast toast = Toast.makeText(context, JSONObject.getString("title")+"was added to Watchlist", Toast.LENGTH_SHORT);
                    toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 750);
                    // /Toast.makeText(context, "You bookmarked "+ JSONObject.getString("title"), Toast.LENGTH_SHORT).show();
                    new AddToSeenOrWatchListAsyncTask("watchlist.txt", "https://api.themoviedb.org/3/movie/" + String.valueOf(JSONObject.getString("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US").execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return cellView;
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

    private class AddToSeenOrWatchListAsyncTask extends AsyncTask<String, Void, String> {
        String fileName;
        String linkToData;
        AddToSeenOrWatchListAsyncTask(String fileName,String linkToData){
            this.fileName = fileName;
            this.linkToData = linkToData;
        }
        @Override
        protected String doInBackground(String... params) {
            //přečtení původního souboru
            String fileString = readFromFile(fileName);
            HttpHandler sh = new HttpHandler();
            //provedení dotazu - adresa dotazu je paramtr při volání této funkce
            String jsonStr = sh.makeServiceCall(linkToData);
            try {
                JSONObject wholeJSON = new JSONObject();
                //Ověření zda seznam již obsahuje přidávaný film
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
                    //pokud jej neobsahuje
                    JSONObject oneMovieObject = new JSONObject(jsonStr);

                    JSONArray oneMovieArray = new JSONArray();
                    oneMovieArray.put(oneMovieObject);
                    //film se přidá jako další položka do databáze
                    wholeJSON.put(String.valueOf(wholeJSON.length()), oneMovieArray);
                    fileString = wholeJSON.toString();
                    //přidání do seznamů pro adaptéry
                    if (fileName.equals("watchlist.txt")) {
                        MainActivity.watchlistMovies.add(String.valueOf(oneMovieObject));
                    } else if (fileName.equals("seenlist.txt")) {
                        MainActivity.seenlistMovies.add(String.valueOf(oneMovieObject));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //přepsání nového seznamu starým
            writeToWatchlistOrSeenlistFile(fileName,fileString);
            return fileName;
        }
        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            //po dokončení celé funkce se upozorní adaptéry o změně v jejich datech
            if (fileName.equals("watchlist.txt")) {WatchlistFragment.WatchlistGridViewAdapter.notifyDataSetChanged();}
            if (fileName.equals("seenlist.txt")) {WatchlistFragment.SeenlistGridViewAdapter.notifyDataSetChanged();}
        }
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
