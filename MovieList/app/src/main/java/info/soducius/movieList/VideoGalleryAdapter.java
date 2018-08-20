package info.soducius.movieList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoGalleryAdapter extends BaseAdapter{

    Context context;

    ArrayList<String>  linkToVideos = new ArrayList<>();
    ArrayList<String> linkToThumbnails = new ArrayList<>();
    ArrayList<String> titleArrayList = new ArrayList<>();

    ImageView videoImageView;
    TextView titleTextView;


    private static LayoutInflater inflater=null;
    public VideoGalleryAdapter(Activity activity, ArrayList<String> linksToVideos, ArrayList<String> ThumbnailLinks, ArrayList<String> titlesArrayList) {
        // TODO Auto-generated constructor stub
        context = activity;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        linkToVideos = linksToVideos;
        linkToThumbnails = ThumbnailLinks;
        titleArrayList = titlesArrayList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return linkToVideos.size();
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
        final View video_cell;
        video_cell = inflater.inflate(R.layout.video_cell, null);

        videoImageView = (ImageView) video_cell.findViewById(R.id.videoImageView);
        titleTextView = (TextView) video_cell.findViewById(R.id.titleTextView);

            String url = linkToThumbnails.get(position);

            Picasso.with(context).
                    load(url).
                    placeholder(R.drawable.photo_placeholder).
                    into(videoImageView);

        titleTextView.setText(titleArrayList.get(position));

        videoImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_cell.findViewById(R.id.titleBar).getVisibility() == View.VISIBLE){
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkToVideos.get(position))));
                } else {
                    video_cell.findViewById(R.id.titleBar).setVisibility(View.VISIBLE);
                }
            }
        });

        return video_cell;
    }
}
