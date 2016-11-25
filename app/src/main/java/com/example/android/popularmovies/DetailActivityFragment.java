package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String mMovieDetailsStr;
    private String originalTitle;
    private String userReview;
    private String overview;
    private String releaseDate;
    private String thumbnailUrl;
    double actualUserReview;
    private String baseUrl = "http://image.tmdb.org/t/p/w185/";

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mMovieDetailsStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            String[] movieData = mMovieDetailsStr.split("\\|");
            ImageView movieThumbnail = (ImageView) rootView.findViewById(R.id.movie_detail_thumbnail);
            thumbnailUrl = movieData[0];
            Picasso.with(getContext()).load(baseUrl + thumbnailUrl).placeholder(R.drawable.android_placeholder).error(R.drawable.android_placeholder).into(movieThumbnail);
            overview = movieData[1];
            releaseDate = movieData[2];
            originalTitle = movieData[3];
            userReview = movieData[4] + "/10";
            ((TextView) rootView.findViewById(R.id.original_title))
                    .setText(originalTitle);
            ((TextView) rootView.findViewById(R.id.release_date))
                    .setText(releaseDate);
            ((TextView) rootView.findViewById(R.id.user_reviews))
                    .setText(userReview);
            ((TextView) rootView.findViewById(R.id.overview))
                    .setText(overview);

        }
        return rootView;
    }
}
