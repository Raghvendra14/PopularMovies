package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private MoviePosterAdapter posterAdapter;
    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    @Override
    public void onStart() {
        updateMovieDetails();
        super.onStart();
    }

    private void updateMovieDetails() {
        FetchMovieDetailsTask movieDetailsTask = new FetchMovieDetailsTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting_order = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
        movieDetailsTask.execute(sorting_order);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        posterAdapter = new MoviePosterAdapter(getActivity(), new ArrayList<String>());
        GridView gridView = (GridView) rootView.findViewById(R.id.home_page_grid);
        gridView.setAdapter(posterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieDescription = posterAdapter.getItem(position);
                Intent detailActivity = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movieDescription);
                startActivity(detailActivity);
            }
        });
        return rootView;
    }

    public class FetchMovieDetailsTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchMovieDetailsTask.class.getSimpleName();

        private String[] getMovieDetailsFromJson(String movieDetailsJsonStr) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMD_RESULTS = "results";
            final String TMD_POSTER_PATH = "poster_path";
            final String TMD_OVERVIEW = "overview";
            final String TMD_RELEASE_DATE = "release_date";
            final String TMD_TITLE = "original_title";
            final String TMD_USER_RATING = "vote_average";

            JSONObject movieDetails = new JSONObject(movieDetailsJsonStr);
            JSONArray moviesArray = movieDetails.getJSONArray(TMD_RESULTS);

            String[] resultStrs = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                String posterPath;
                String overview;
                String releaseDate;
                String originalTitle;
                double voteAverage;

                JSONObject movieData = moviesArray.getJSONObject(i);
                posterPath = movieData.getString(TMD_POSTER_PATH).substring(0);
                overview = movieData.getString(TMD_OVERVIEW);
                releaseDate = movieData.getString(TMD_RELEASE_DATE);
                originalTitle = movieData.getString(TMD_TITLE);
                voteAverage = movieData.getDouble(TMD_USER_RATING);

                resultStrs[i] = posterPath + "|" + overview + "|" + releaseDate + "|" + originalTitle + "|" + voteAverage;

            }
            return resultStrs;
        }
        protected String[] doInBackground(String... params) {

            if(params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Will contain the raw JSON response as a string.
            String movieDetailsJsonStr = null;
            try {
                // Construct the URL for the TheMovieDb query
                final String MOVIE_DETAILS_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] +"?";
                final String APP_KEY_PARAM = "api_key";
                Uri buildUri = Uri.parse(MOVIE_DETAILS_BASE_URL).buildUpon()
                        .appendQueryParameter(APP_KEY_PARAM, "ENTER YOUR API KEY HERE")
                        .build();
                URL url = new URL(buildUri.toString());

                // Create the request to TheMovieDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) {
                    // Stream is empty. No need in parsing.
                    return null;
                }

                movieDetailsJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDetailsFromJson(movieDetailsJsonStr);
            } catch (JSONException e) {
                Log.e (LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null) {
                posterAdapter.clear();
                for(String movieDetails : result) {
                    posterAdapter.add(movieDetails);
                }
            }
        }
    }
}


