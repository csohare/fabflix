package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.uci.ics.fabflixmobile.R;
import android.content.Intent;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.os.Bundle;
import com.google.gson.Gson;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.moviesearch.MovieSearchActivity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MovieListActivity extends AppCompatActivity {

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "fabflix";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_movielist);
        if(intent.getStringExtra("pageOffset").equals("0")) {
            Button button = findViewById(R.id.prev);
            button.setEnabled(false);
        }
        final ArrayList<Movie> movies = new ArrayList<>();

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        String encodedFullText = "";
        String pageOffset = "";
        try{
            encodedFullText = URLEncoder.encode(intent.getStringExtra("fulltext"), "UTF-8");
            pageOffset = URLEncoder.encode(intent.getStringExtra("pageOffset"), "UTF-8");
            Log.d("PAGE OFFSET", intent.getStringExtra("pageOffset"));
        }catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // request type is POST
        final StringRequest fulltextSearch = new StringRequest(
                Request.Method.GET,

                baseURL + "/api/MovieList?fulltext=" + encodedFullText + "&pageSize=11&pageOffset=" + pageOffset + "&sort=1",

                response -> {
                    Log.d("search success", response);
                    try{
                        JSONArray jsonArray  = new JSONArray(response);
                        JSONObject jsonObject;
                        if(jsonArray.length() != 11) {
                            Button nextButton = findViewById(R.id.next);
                            nextButton.setEnabled(false);
                        }
                        ListView listView = findViewById(R.id.list);
                        for(int i = 0; i < jsonArray.length(); i++) {
                            String item = jsonArray.getString(i);
                            jsonObject = new JSONObject(item);
                            Movie movie = new Movie(jsonObject.getString("movieId"), jsonObject.getString("title"), jsonObject.getString("year"),jsonObject.getString("rating"), jsonObject.getString("genreNames"), jsonObject.getString("starNames"), jsonObject.getString("director"));
                            movies.add(movie);
                            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
                            listView.setAdapter(adapter);

                        }
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Movie movie = movies.get(position);
                            finish();
                            Intent nextIntent = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                            nextIntent.putExtra("id", movie.getId());
                            startActivity(nextIntent);
                        });
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                });
        queue.add(fulltextSearch);
        Button nextButton = findViewById(R.id.next);
        Button prevButton = findViewById(R.id.prev);
        prevButton.setOnClickListener(view -> prev());
        nextButton.setOnClickListener(view -> next());
    }
    @SuppressLint("SetTextI18n")
    public void next() {
        finish();
        Intent curr = getIntent();
        Intent intent = new Intent(MovieListActivity.this, MovieListActivity.class);
        intent.putExtra("fulltext", curr.getStringExtra("fulltext"));
        String newPageOffset = Integer.toString(Integer.parseInt(curr.getStringExtra("pageOffset")) + 10);
        intent.putExtra("pageOffset", newPageOffset);
        startActivity(intent);
    }

    public void prev() {
        finish();
        Intent curr = getIntent();
        Intent intent = new Intent(MovieListActivity.this, MovieListActivity.class);
        intent.putExtra("fulltext", curr.getStringExtra("fulltext"));
        String newPageOffset = Integer.toString(Integer.parseInt(curr.getStringExtra("pageOffset")) - 10);
        intent.putExtra("pageOffset", newPageOffset);
        startActivity(intent);

    }
}