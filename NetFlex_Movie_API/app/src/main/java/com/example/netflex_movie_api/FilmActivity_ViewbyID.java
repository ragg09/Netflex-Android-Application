package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmActivity_ViewbyID extends AppCompatActivity {
    //Para sa Movie mismo
    ImageView film_viewbyid_iv;
    TextView film_viewbyid_tv_title, film_viewbyid_tv_duration, film_viewbyid_tv_date, film_viewbyid_tv_story;

    //Para sa Producer ng movie
    TextView film_viewbyid_tv_prod_name, film_viewbyid_tv_prod_email, film_viewbyid_tv_prod_website;

    //Genre
    TextView film_viewbyid_tv_genre;

    Button btn_open_actors; //baka palita ko ng imageview

    String urlFilm = "http://192.168.68.103:8000/api/film/";
    String urlString = "http://192.168.68.103:8000/";

    URL myURL;

    SharedPreferences sp;
    String accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_viewbyid);

        film_viewbyid_iv = (ImageView) findViewById(R.id.film_viewbyid_iv);
        film_viewbyid_tv_title = (TextView)findViewById(R.id.film_viewbyid_tv_title);
        film_viewbyid_tv_duration = (TextView)findViewById(R.id.film_viewbyid_tv_duration);
        film_viewbyid_tv_date = (TextView)findViewById(R.id.film_viewbyid_tv_date);
        film_viewbyid_tv_story = (TextView)findViewById(R.id.film_viewbyid_tv_story);

        film_viewbyid_tv_prod_name = (TextView)findViewById(R.id.film_viewbyid_tv_prod_name);
        film_viewbyid_tv_prod_email = (TextView)findViewById(R.id.film_viewbyid_tv_prod_email);
        film_viewbyid_tv_prod_website = (TextView)findViewById(R.id.film_viewbyid_tv_prod_website);

        film_viewbyid_tv_genre = (TextView)findViewById(R.id.film_viewbyid_tv_genre);

        btn_open_actors = (Button)findViewById(R.id.btn_open_actors);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");

        //GETTING DATA FROM INTENT
        Intent i = getIntent();
        String id = i.getStringExtra("id");


        //OPEN ACTOR FRAGMENTS, LIST EVERY ACTOR IN A FILM
        btn_open_actors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilmActivity_DialogFragment filmActivity_dialogFragment = new FilmActivity_DialogFragment();
                filmActivity_dialogFragment.show(getSupportFragmentManager(), "ACTORS IN A FILM");
            }
        });

        LoadMovieDetails(id);

    }//END of onCreate

    public void LoadMovieDetails(String id){
        //creating new url from base URL and ID
        String searchURL = urlFilm+id;

        //checking url
        try {
            myURL = new URL(searchURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //initialize a new request queue instance
        //to prevent multiple request
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,     //kung anong type of request
                searchURL,              //url na pupuntahan nung request
                null,        //since get method ito, walang data na pinapasa kaya null lang
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //getting the movie object || PS it is not the array object so no need to loop through it
                            JSONObject movie_data = response.getJSONObject("data_movie");
                            //SETTING DATA INTO VIEW
                            Picasso.get().load(urlString + movie_data.getString("image")).into(film_viewbyid_iv);
                            film_viewbyid_tv_title.setText(movie_data.getString("title"));
                            film_viewbyid_tv_duration.setText(movie_data.getInt("duration")+" Hour Runtime");
                            film_viewbyid_tv_date.setText(movie_data.getString("date_released"));
                            film_viewbyid_tv_story.setText(movie_data.getString("story"));

                            //getting the producer object || PS it is not the array object so no need to loop through it
                            JSONObject producer_data = response.getJSONObject("producer");
                            //SETTING DATA INTO VIEW
                            film_viewbyid_tv_prod_name.setText(producer_data.getString("name"));
                            film_viewbyid_tv_prod_email.setText(producer_data.getString("email"));
                            film_viewbyid_tv_prod_website.setText(producer_data.getString("website"));

                            //getting the producer object || PS it is not the array object so no need to loop through it
                            JSONObject genre_data = response.getJSONObject("genre");
                            film_viewbyid_tv_genre.setText(genre_data.getString("genre").toUpperCase());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "MAY MALING NANGYAYARE!!", Toast.LENGTH_LONG).show();
                    }
                }
        ){
            //ito na ung pag si-set ng authorization token
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer "+accessToken);
                return params;
            }
        };

        //adding
        //sending the request
        requestQueue.add(jsonObjectRequest);
    }


}
