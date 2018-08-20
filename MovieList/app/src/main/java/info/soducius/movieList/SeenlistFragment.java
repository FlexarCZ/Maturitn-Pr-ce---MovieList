package info.soducius.movieList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class SeenlistFragment extends Fragment {

    static GridView gv;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_seenlist, container, false);
        gv = (GridView) rootView.findViewById(R.id.gridView1);

        //SeenlistGridViewAdapter = new SeenlistAdapter(getActivity(), seenlistMovies);
        gv.setAdapter(WatchlistFragment.SeenlistGridViewAdapter);

        return rootView;
    }
}
