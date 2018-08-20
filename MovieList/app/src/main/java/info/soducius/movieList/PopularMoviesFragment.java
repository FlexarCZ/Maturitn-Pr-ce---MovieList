package info.soducius.movieList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PopularMoviesFragment extends Fragment {

    static GridView gv;
    static BaseAdapter PopularMoviesGridViewAdapter;
    public ArrayList<String> popularMoviesArrayList = new ArrayList<>();

    int gridViewLoadLittleHelper = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_content, container, false);
        final View rootView = inflater.inflate(R.layout.fragment_section_popular_movies, container, false);


        TextView noInterneTextView = (TextView) rootView.findViewById(R.id.noInternettextView);

        gv = (GridView) rootView.findViewById(R.id.gridView1);

        //final SearchView sv = (SearchView) rootView.findViewById(R.id.searchView);
        //sv.setIconifiedByDefault(false);

        if (MainActivity.isOnline()) {
            if (popularMoviesArrayList.isEmpty()) {
                new PopularMoviesJSONParser().execute(String.valueOf(1));
            }
            PopularMoviesGridViewAdapter = new PopularMoviesAdapter(getActivity(), popularMoviesArrayList);
            gv.setAdapter(PopularMoviesGridViewAdapter);

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
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = gv.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0 && gridViewLoadLittleHelper != totalItemCount/20) {
                        // reached the bottom
                        gridViewLoadLittleHelper = totalItemCount/20;
                        Log.i("IIIIIIIIIIIIIIIIII", String.valueOf(gridViewLoadLittleHelper));
                        new PopularMoviesJSONParser().execute(String.valueOf(gridViewLoadLittleHelper + 1));
                    }
                } else {
                    // not top
                }
            }
        });
        return rootView;
    }

    private class PopularMoviesJSONParser extends AsyncTask<String, Void, String> {
    //Asynchroní funkce, která je zpracována na nezávislém vlákně
        @Override
        protected String doInBackground(String... params) {
            //Volání vytvořené funkce HttpHandler
            HttpHandler sh = new HttpHandler();
            //základní podoba dotazu (tento API patří pouze mně)
            String basicLink = "https://api.themoviedb.org/3/movie/popular?api_key=95148fc363ff244cab388010dfbfa949&language=en-US&page=";
            //params[0] obsahuje číslo stránky, jenž je potřeba načíst
            String jsonStr = sh.makeServiceCall(basicLink+params[0]);
            try {
                //vytvoření JSON objektu, který obsahuje seznam získaných populárních filmů
                JSONObject wholeJSON = new JSONObject(jsonStr);
                //získání pole "results" - výsledky dotazu
                JSONArray oneArray = wholeJSON.getJSONArray("results");
                //čtení a následné přidání filmů do vlastnímu seznamu populárních filmů
                for (int i=0; i<oneArray.length(); i++){
                    JSONObject oneMovie = oneArray.getJSONObject(i);
                    popularMoviesArrayList.add(String.valueOf(oneMovie));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonStr;
        }
        @Override
        protected void onPostExecute (String i) {
            //Upozornění adaptéru, jenž se stará o vykreslování populárních filmů, že jeho data se změnila
            PopularMoviesGridViewAdapter.notifyDataSetChanged();
        }
    }
}
