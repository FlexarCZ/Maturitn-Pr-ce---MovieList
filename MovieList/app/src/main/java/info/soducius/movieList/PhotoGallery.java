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

public class PhotoGallery extends AppCompatActivity {

    String linkToImageJSONString;
    ArrayList<String>  linkToImages = new ArrayList<>();
    GridView gv;
    BaseAdapter PhotoGalleryGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

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

        linkToImageJSONString = getIntent().getStringExtra("link");

        new photoFetcher().execute();

        gv = (GridView) findViewById(R.id.gridView1);

        PhotoGalleryGridViewAdapter = new PhotoGalleryAdapter(PhotoGallery.this , linkToImages);
        gv.setAdapter(PhotoGalleryGridViewAdapter);

    }

    private class photoFetcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(linkToImageJSONString);
            try {
                JSONObject wholeJSON = new JSONObject(jsonStr);
                JSONArray backdropsJSONArray = wholeJSON.getJSONArray("backdrops");
                JSONObject oneBackdrop;
                for(int i=0;i<backdropsJSONArray.length();i++){
                    oneBackdrop = backdropsJSONArray.getJSONObject(i);
                    linkToImages.add("https://image.tmdb.org/t/p/w780/" + oneBackdrop.getString("file_path"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            PhotoGalleryGridViewAdapter.notifyDataSetChanged();
        }
    }

}
