package edu.uci.ics.fabflixmobile.ui.singlemovie;

import com.google.gson.JsonArray;
import edu.uci.ics.fabflixmobile.R;
import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.moviesearch.MovieSearchActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SingleMovieActivity extends AppCompatActivity {

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "fabflix";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    private TextView movieTitle;
    private TextView director;
    private TextView movieGenres;
    private TextView movieStars;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemovie);
        Intent intent = getIntent();
        movieTitle = findViewById(R.id.movieTitle);
        director = findViewById(R.id.director);
        movieGenres = findViewById(R.id.movieGenres);
        movieStars = findViewById(R.id.movieStars);
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest singleMovieRequest= new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?id=" + intent.getStringExtra("id"),
                response -> {
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        movieTitle.setText(jsonObject.getString("movie_title") + " (" + jsonObject.getString("movie_year") + ")");
                        director.setText("Director\n" + jsonObject.getString("movie_director"));
                        movieGenres.setText("Genres\n" + jsonObject.getString("movie_genre"));
                        movieStars.setText("Stars\n" + jsonObject.getString("movie_starNames"));

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }



                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                });
        queue.add(singleMovieRequest);

    }

}
