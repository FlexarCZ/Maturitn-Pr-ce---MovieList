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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoGalleryAdapter extends BaseAdapter{

    Context context;

    ArrayList<String> linkToImages = new ArrayList<>();

    ImageView photoImageView;


    private static LayoutInflater inflater=null;
    public PhotoGalleryAdapter(Activity activity, ArrayList<String> linksToImages) {
        // TODO Auto-generated constructor stub
        context = activity;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        linkToImages = linksToImages;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return linkToImages.size();
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
        final View photo_cell;
        photo_cell = inflater.inflate(R.layout.photo_cell, null);

        photoImageView = (ImageView) photo_cell.findViewById(R.id.photoImageView);

            String url = linkToImages.get(position);

            Picasso.with(context).
                    load(url).
                    fit().
                    centerCrop().
                    placeholder(R.drawable.photo_placeholder).
                    into(photoImageView);


        photoImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = linkToImages.get(position).replace("w780","original");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        return photo_cell;
    }
}
