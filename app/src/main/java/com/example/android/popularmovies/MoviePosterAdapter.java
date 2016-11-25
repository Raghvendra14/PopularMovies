package com.example.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviePosterAdapter extends ArrayAdapter<String> {
    private String baseUrl = "http://image.tmdb.org/t/p/w185/";

    MoviePosterAdapter(Activity context, List<String> movieDetails ) {
        super(context, 0, movieDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String movieDetails = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.popular_movies, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.movie_poster);

            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        String[] movieData = movieDetails.split("\\|");
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        int height = getContext().getResources().getDisplayMetrics().heightPixels;
        Picasso.with(getContext()).load(baseUrl + movieData[0]).placeholder(R.drawable.android_placeholder).error(R.drawable.android_placeholder).resize(width/2,height/2).into(holder.icon);

        return convertView;
    }

}
