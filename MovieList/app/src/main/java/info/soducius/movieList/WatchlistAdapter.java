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

public class WatchlistAdapter extends BaseAdapter{

    public static Context context;

    public ArrayList<String> watchlistMoviesArrayList = new ArrayList<>();

    JSONObject wholeJSON = new JSONObject();
    JSONArray oneMovieJSONArray = new JSONArray();
    JSONObject oneMovieJSONObject = new JSONObject();

    ImageView ratingFirstStar;
    ImageView ratingSecondStar;
    ImageView ratingThirdStar;
    ImageView ratingFourthStar;
    ImageView ratingFifthStar;

    ImageView goldStripe;

    private static LayoutInflater inflater=null;


    public WatchlistAdapter(Activity mainActivity,ArrayList<String> watchlistMovies) {
        context = mainActivity; //nastaví kontext
        watchlistMoviesArrayList = watchlistMovies; //předá list z argumentu do listu adaptéru
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {return watchlistMoviesArrayList.size();} //funkce getCount vrací celkový počet prvků v mřížce
    @Override
    public Object getItem(int position) {return position;} //funkce getItem vrací pozici tázaného prvku
    @Override
    public long getItemId(int position) {return position;}
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final View cellView;
        cellView = inflater.inflate(R.layout.film_cell, null);
        //inicializování všech potřebných prvků v buňce mřížce
        cellView.findViewById(R.id.bookMarkButton).setVisibility(View.GONE);
        cellView.findViewById(R.id.clearBookmarkButton).setVisibility(View.VISIBLE);
        cellView.findViewById(R.id.popularSeenButton).setVisibility(View.GONE);
        cellView.findViewById(R.id.watchlistSeenButton).setVisibility(View.VISIBLE);
        ratingFirstStar = (ImageView) cellView.findViewById(R.id.ratingFirstStar);
        ratingSecondStar = (ImageView) cellView.findViewById(R.id.ratingSecondStar);
        ratingThirdStar = (ImageView) cellView.findViewById(R.id.ratingThirdStar);
        ratingFourthStar = (ImageView) cellView.findViewById(R.id.ratingFourthStar);
        ratingFifthStar = (ImageView) cellView.findViewById(R.id.ratingFifthStar);
        //pokud seznam uložených filmů do watchlist není prázdný
        if (!watchlistMoviesArrayList.isEmpty()) {
                String JSONString = readFromFile("watchlist.txt"); //přečtení offline databáze
                try {
                    wholeJSON = new JSONObject(JSONString);
                    oneMovieJSONArray = wholeJSON.getJSONArray(String.valueOf(position)); //následné získání filmu podle pozice
                    oneMovieJSONObject = oneMovieJSONArray.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Double rating = 0d;
                try {
                    rating = oneMovieJSONObject.getDouble("vote_average"); //získání hodnocení z databáze
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            setRatingStars(rating); //nastavení hodnocení (malé hvězdičky u filmu)
            goldStripe = (ImageView) cellView.findViewById(R.id.goldStripe);
            if(rating > 7.5) {
                goldStripe.setVisibility(View.VISIBLE); //pokud má film lepší hodnocení než 7.5 dostane zlatou stuhu
            }
                TextView tv;
                ImageView img;
                tv = (TextView) cellView.findViewById(R.id.textView1);  //inicializování TextView - pole pro název filmu
                img = (ImageView) cellView.findViewById(R.id.filmImage);//inicializace ImageView - pole pro obrázek (plakát)

                String url = null;
                try {
                    url ="https://image.tmdb.org/t/p/w185" + oneMovieJSONObject.getString("poster_path"); //získání URL obrázku
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Picasso.with(context). //knihovna Picasso, která se stará o načítání obrázků
                        load(url).
                        fit().
                        placeholder(R.drawable.placeholder_film_image).//případná náhrada za obrázek při nepovedeném načtení
                        into(img); //cíl do kterého se má obrázek načíst
                try {
                    tv.setText(oneMovieJSONObject.getString("title")); //získání a nastavení textu do pole tv (název  filmu)
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            cellView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cellView.findViewById(R.id.infoBar).getVisibility() == View.VISIBLE) {

                        String link = null;
                        Intent toFilmDetailActivityIntent = null;
                        try {
                            link = "https://api.themoviedb.org/3/movie/" + String.valueOf(wholeJSON.getJSONArray(String.valueOf(position)).getJSONObject(0).getInt("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US";
                            toFilmDetailActivityIntent = new Intent(context, MovieDetail.class);
                            toFilmDetailActivityIntent.putExtra("isFromWatchlistOrSeenlist", 1);
                            toFilmDetailActivityIntent.putExtra("id", position);
                            toFilmDetailActivityIntent.putExtra("linkToSite", link);
                            toFilmDetailActivityIntent.putExtra("linkToImage", wholeJSON.getJSONArray(String.valueOf(position)).getJSONObject(0).getString("poster_path"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        context.startActivity(toFilmDetailActivityIntent);

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

            cellView.findViewById(R.id.watchlistSeenButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject JSONObject = new JSONObject(watchlistMoviesArrayList.get(position));
                        Toast.makeText(context,JSONObject.getString("title")+" Was added to seen", Toast.LENGTH_SHORT).show();
                        new AddToSeenOrWatchListAsyncTask("seenlist.txt", "https://api.themoviedb.org/3/movie/" + String.valueOf(JSONObject.getString("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US").execute();
                        new removeFromWatchlist(position).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            cellView.findViewById(R.id.clearBookmarkButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject JSONObject = new JSONObject(watchlistMoviesArrayList.get(position));
                        Toast.makeText(context, JSONObject.getString("title")+" was removed from watchlist", Toast.LENGTH_SHORT).show();
                        new removeFromWatchlist(position).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return cellView;
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

    private class removeFromWatchlist extends AsyncTask<String, Void, String> {
        int id;
        removeFromWatchlist(int id){
            this.id = id;
        }
        @Override
        protected String doInBackground(String... params) {

            ArrayList<String> allOldMovies = new ArrayList<>();
            ArrayList<String> newMovies = new ArrayList<>();
            String finalString;
            JSONObject newJSONObject = new JSONObject();

            allOldMovies = MainActivity.watchlistMovies;
            allOldMovies.remove(id);
            newMovies = allOldMovies;
            try {
                for (int i = 0;i<newMovies.size();i++){
                    JSONArray newJSONArray = new JSONArray();
                    newJSONArray.put(newMovies.get(i));
                    newJSONObject.put(String.valueOf(i),newJSONArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            MainActivity.watchlistMovies = newMovies;

            finalString = newJSONObject.toString();
            finalString = finalString.replaceAll("\\\\" ,"");
            finalString = finalString.replaceAll("\\}\"\\]" ,"}]");
            finalString = finalString.replaceAll(":\\[\"\\{" ,":[{");
            Log.i("All replaced: ", finalString);
            writeToWatchlistOrSeenlistFile("watchlist.txt", finalString);

            return finalString;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            WatchlistFragment.WatchlistGridViewAdapter.notifyDataSetChanged();
        }
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
