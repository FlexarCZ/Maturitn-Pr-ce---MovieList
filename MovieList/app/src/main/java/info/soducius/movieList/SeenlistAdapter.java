package info.soducius.movieList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

public class SeenlistAdapter extends BaseAdapter{

    Context context;

    ArrayList<String> seenlistMoviesArrayList = new ArrayList<>();

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
    public SeenlistAdapter(Activity mainActivity, ArrayList<String> seenlistMovies) {
        // TODO Auto-generated constructor stub
        context = mainActivity;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        seenlistMoviesArrayList = seenlistMovies;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return seenlistMoviesArrayList.size();
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


        cellView.findViewById(R.id.popularSeenButton).setVisibility(View.GONE);
        cellView.findViewById(R.id.clearSeenButton).setVisibility(View.VISIBLE);

        ratingFirstStar = (ImageView) cellView.findViewById(R.id.ratingFirstStar);
        ratingSecondStar = (ImageView) cellView.findViewById(R.id.ratingSecondStar);
        ratingThirdStar = (ImageView) cellView.findViewById(R.id.ratingThirdStar);
        ratingFourthStar = (ImageView) cellView.findViewById(R.id.ratingFourthStar);
        ratingFifthStar = (ImageView) cellView.findViewById(R.id.ratingFifthStar);

        String JSONString = readFromFile("seenlist.txt");
        if (!seenlistMoviesArrayList.isEmpty()) {

            //String JSONString = readFromFile("seenlist.txt");
            try {
                wholeJSON = new JSONObject(JSONString);
                oneMovieJSONArray = wholeJSON.getJSONArray(String.valueOf(position));
                oneMovieJSONObject = oneMovieJSONArray.getJSONObject(0);
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

            goldStripe = (ImageView) cellView.findViewById(R.id.goldStripe);
            if(rating > 7.5) {
                goldStripe.setVisibility(View.VISIBLE);
            }

            TextView tv;
            ImageView img;


            tv = (TextView) cellView.findViewById(R.id.textView1);
            img = (ImageView) cellView.findViewById(R.id.filmImage);

            //Log.i("HEEEEEJ", "" + result);

//*******************************
            //String url = "https://image.tmdb.org/t/p/w185" + imagesURL.get(position);
            String url = null;
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



            //tv.setText(result.get(position));
            try {
                tv.setText(oneMovieJSONObject.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            cellView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (cellView.findViewById(R.id.infoBar).getVisibility() == View.VISIBLE) {

                        String link = null;
                        Intent toFilmDetailActivityIntent = null;
                        try {
                            link = "https://api.themoviedb.org/3/movie/" + String.valueOf(wholeJSON.getJSONArray(String.valueOf(position)).getJSONObject(0).getInt("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US";
                            toFilmDetailActivityIntent = new Intent(context, MovieDetail.class);
                            toFilmDetailActivityIntent.putExtra("isFromWatchlistOrSeenlist", 2);
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


            cellView.findViewById(R.id.clearSeenButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject JSONObject = new JSONObject(seenlistMoviesArrayList.get(position));
                        Toast.makeText(context,JSONObject.getString("title")+" was removed from seenlist", Toast.LENGTH_SHORT).show();
                        new removeFromSeenlist(position).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            cellView.findViewById(R.id.bookMarkButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject JSONObject = new JSONObject(seenlistMoviesArrayList.get(position));
                        Toast.makeText(context, "You bookmarked "+ JSONObject.getString("title"), Toast.LENGTH_SHORT).show();
                        new AddToSeenOrWatchListAsyncTask("watchlist.txt", "https://api.themoviedb.org/3/movie/" + String.valueOf(JSONObject.getString("id")) + "?api_key=95148fc363ff244cab388010dfbfa949&language=en-US").execute();
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

    private class removeFromSeenlist extends AsyncTask<String, Void, String> {
        int id;
        removeFromSeenlist(int id){
            this.id = id;
        }
        @Override
        protected String doInBackground(String... params) {

            ArrayList<String> allOldMovies = new ArrayList<>();
            ArrayList<String> newMovies = new ArrayList<>();
            String finalString;
            JSONObject newJSONObject = new JSONObject();

            allOldMovies = MainActivity.seenlistMovies;
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


            MainActivity.seenlistMovies = newMovies;

            finalString = newJSONObject.toString();
            finalString = finalString.replaceAll("\\\\" ,"");
            finalString = finalString.replaceAll("\\}\"\\]" ,"}]");
            finalString = finalString.replaceAll(":\\[\"\\{" ,":[{");
            Log.i("All replaced: ", finalString);
            writeToWatchlistOrSeenlistFile("seenlist.txt", finalString);

            return finalString;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            WatchlistFragment.SeenlistGridViewAdapter.notifyDataSetChanged();
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
