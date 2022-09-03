package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorActivity_withMovie extends AppCompatActivity {

    ImageView iv_actor_image_wm;
    TextView tv_actor_name_wm, tv_actor_note_wm;

    String urlActor = "http://192.168.68.103:8000/api/actor/";
    String urlString = "http://192.168.68.103:8000/";

    URL myURL;

    private List<ActorGet_withMovie> actor_list_data_wm;
    private RecyclerView rv;
    private ActorAdpter_withMovie adapter;

    SharedPreferences sp;
    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_with_movie);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();

        //SETTING RV, actor_list_dat, and adapter
        rv=(RecyclerView)findViewById(R.id.actor_rv_wm);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(divider);

        actor_list_data_wm = new ArrayList<>();
        adapter = new ActorAdpter_withMovie(this, actor_list_data_wm);

        iv_actor_image_wm = (ImageView)findViewById(R.id.iv_actor_image_wm);
        tv_actor_name_wm = (TextView)findViewById(R.id.tv_actor_name_wm);
        tv_actor_note_wm = (TextView)findViewById(R.id.tv_actor_note_wm);

        //GETTING DATA FROM INTENT
        Intent i = getIntent();
        String id = i.getStringExtra("actor_id");

        loadActorwithMovie(id);

    }//END of onCREATE


    //LOAD ACTOR AND ITS MOVIES
    public void loadActorwithMovie(String id){
        //creating new url from base URL and ID
        String searchURL = urlActor+id;

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
                searchURL,               //url na pupuntahan nung request
                null,        //since get method ito, walang data na pinapasa kaya null lang
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //getting the actor object || PS it is not the array object
                            JSONObject actor_data = response.getJSONObject("data_actor");
                            //SETTING DATA INTO VIEW
                            Picasso.get().load(urlString + actor_data.getString("image")).into(iv_actor_image_wm);
                            tv_actor_name_wm.setText(actor_data.getString("name"));
                            tv_actor_note_wm.setText(actor_data.getString("note"));

                            if(response.getInt("count") > 0){
                                JSONArray array_movie = response.getJSONArray("movies");
                                JSONArray array_producer = response.getJSONArray("producers");
                                for (int i=0; i<array_movie.length(); i++ ){
                                    JSONObject movie = array_movie.getJSONObject(i);
                                    JSONObject producer = array_producer.getJSONObject(i);

                                    ActorGet_withMovie listData = new ActorGet_withMovie(
                                            movie.getInt("id"),
                                            movie.getString("title"),
                                            movie.getString("image"),
                                            producer.getString("name"),
                                            producer.getString("website")
                                    );
                                    actor_list_data_wm.add(listData);
                                }
                                rv.setAdapter(adapter);
                            }else {
                                //Toast.makeText(ActorActivity_withMovie.this, "WALA KA PANG MOVIE KAWAWA KA NAMAN", Toast.LENGTH_SHORT).show();
                                showToast("WALA KA PANG MOVIE KAWAWA KA NAMAN", R.drawable.ic_film);
                            }

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

    //==========================================================================================
    //CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST
    public void showToast(String message, int icon) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_custom_toast, (ViewGroup) findViewById(R.id.toast_root));
        TextView toastText = layout.findViewById(R.id.toast_text);
        ImageView toastImage = layout.findViewById(R.id.toast_image);

        toastText.setText(message);
        toastImage.setImageResource(icon);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    //^^^CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST
}