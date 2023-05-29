package edu.uci.ics.fabflixmobile.ui.moviesearch;
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

import java.util.HashMap;
import java.util.Map;

public class MovieSearchActivity extends AppCompatActivity {

    private EditText movieTitle;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_search);
        movieTitle = findViewById(R.id.movietitle);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    public void search(){
        finish();
        Intent MovieListPage = new Intent(MovieSearchActivity.this, MovieListActivity.class);
        MovieListPage.putExtra("fulltext", movieTitle.getText().toString());
        MovieListPage.putExtra("pageOffset", "0");
        startActivity(MovieListPage);


    }
}
