package com.example.netflex_movie_api;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Context mContext;
    private static final String filmURL ="http://192.168.68.103:8000/api/film/" ;

    private List<FilmGetAll> film_list_data;
    private RecyclerView rv;
    private FilmAdapter adapter;
    private URL myURL;

    SwipeRefreshLayout refreshLayout;

    SharedPreferences sp;
    String accessToken;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                SharedPreferences.Editor editor = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_actor:
                Intent intent1 = new Intent(MainActivity.this, ActorActivity.class);
                startActivity(intent1);
                return true;
            case R.id.menu_film:
                Intent intent2 = new Intent(MainActivity.this, FilmActivity_CUD.class);
                startActivity(intent2);
                return true;
            case R.id.menu_producer:
                Intent intent3 = new Intent(MainActivity.this, ProducerActivity.class);
                startActivity(intent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();

        //SETTING RV, actor_list_dat, and adapter
        rv=(RecyclerView)findViewById(R.id.film_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(divider);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.film_refresh);

        film_list_data = new ArrayList<>();
        adapter = new FilmAdapter(this, film_list_data);

        loadAllMovies();

        //REFRESH RV    REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                film_list_data.clear(); //clearing existing array
                loadAllMovies();
                refreshLayout.setRefreshing(false);
            }
        });
        //^^^REFRESH RV    REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV
    }//END of onCreate

    public void loadAllMovies(){
        try {
            myURL = new URL(filmURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                filmURL,
                null,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array=response.getJSONArray("data");
                    for (int i=0; i<array.length(); i++ ){
                        JSONObject obj = array.getJSONObject(i);
                        FilmGetAll listData = new FilmGetAll(
                                obj.getInt("id"),
                                obj.getString("title"),
                                obj.getString("image")
                        );
                        film_list_data.add(listData);

                    }
                    rv.setAdapter(adapter);
                    //JSONObject obj=array.getJSONObject(0);
                    //Toast.makeText(ActorActivity.this, list_data.toString(), Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ accessToken);
                return params;
            }


        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);
    }

}