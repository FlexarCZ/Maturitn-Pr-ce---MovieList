package info.soducius.movieList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class WatchlistFragment extends Fragment {

    public static BaseAdapter WatchlistGridViewAdapter;
    public static BaseAdapter SeenlistGridViewAdapter;

    static GridView gv;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_watchlist, container, false);
        gv = (GridView) rootView.findViewById(R.id.gridView1);

        WatchlistGridViewAdapter = new WatchlistAdapter(getActivity(),MainActivity.watchlistMovies);
        gv.setAdapter(WatchlistGridViewAdapter);

        //seenlistAdapter is here because viewpager loads only views right next to him so seenlist adapter wouldn't we initialized => null exception when adding to seenList right from the first screen
        SeenlistGridViewAdapter = new SeenlistAdapter(getActivity(), MainActivity.seenlistMovies);
        return rootView;

    }
}
