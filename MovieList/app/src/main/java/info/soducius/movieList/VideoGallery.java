package info.soducius.movieList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoGallery extends AppCompatActivity {

    String linkToVideoJSONString;
    ArrayList<String>  linkToVideos = new ArrayList<>();
    ArrayList<String> linkToThumbnails = new ArrayList<>();
    ArrayList<String> titleArrayList = new ArrayList<>();
    GridView gv;
    BaseAdapter VideoGalleryGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linkToVideoJSONString = getIntent().getStringExtra("link");

        new photoFetcher().execute();

        gv = (GridView) findViewById(R.id.gridView1);

        VideoGalleryGridViewAdapter = new VideoGalleryAdapter(VideoGallery.this , linkToVideos, linkToThumbnails, titleArrayList);
        gv.setAdapter(VideoGalleryGridViewAdapter);

    }

    private class photoFetcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(linkToVideoJSONString);
            try {
                JSONObject wholeJSON = new JSONObject(jsonStr);
                JSONArray videosJSONArray = wholeJSON.getJSONArray("results");
                JSONObject oneVideo;
                for(int i=0;i<videosJSONArray.length();i++){
                    oneVideo = videosJSONArray.getJSONObject(i);
                    linkToVideos.add("https://www.youtube.com/watch?v=" + oneVideo.getString("key"));
                    linkToThumbnails.add("http://img.youtube.com/vi/"+oneVideo.getString("key")+"/0.jpg");
                    titleArrayList.add(oneVideo.getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            VideoGalleryGridViewAdapter.notifyDataSetChanged();
        }
    }

}
