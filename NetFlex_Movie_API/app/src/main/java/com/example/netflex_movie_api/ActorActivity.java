package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorActivity extends AppCompatActivity {
    private FloatingActionButton btnadd_actor;
    Context mContext;
    private static final String actorURL ="http://192.168.68.103:8000/api/actor/" ;

    private List<ActorGetAll> actor_list_data;
    private RecyclerView rv;
    private ActorAdapter adapter;
    private URL myURL;

    SwipeRefreshLayout refreshLayout;

    SharedPreferences sp;

    String accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();
        mContext = getApplicationContext();
        
        //SETTING RV, actor_list_dat, and adapter
        rv=(RecyclerView)findViewById(R.id.actor_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        actor_list_data=new ArrayList<>();
        adapter=new ActorAdapter(this,actor_list_data);

        btnadd_actor = (FloatingActionButton)findViewById(R.id.btnadd_actor);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.actor_refresh);



        //REFRESH RV    REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                actor_list_data.clear(); //clearing existing array
                loadActorData();
                refreshLayout.setRefreshing(false);
            }
        });
        //^^^REFRESH RV    REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV

        //ADD ACTORT ACTIVITY
        btnadd_actor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActorActivity.this, ActorActivity_Add.class);
                intent.putExtra("access_token", accessToken);
                startActivity(intent);
            }
        });

        loadActorData(); //populate data on create
    }

    public void loadActorData(){

        try {
            myURL = new URL(actorURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //Log.i("url","url"+ myURL);
        //RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        // stringRequest=new StringRequest(Request.Method.GET,
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                actorURL,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                String token = accessToken;
                try {
                    JSONArray array = response.getJSONArray("data");
                    for (int i=0; i<array.length(); i++ ){
                        JSONObject ob = array.getJSONObject(i);
                        ActorGetAll listData = new ActorGetAll(
                                ob.getInt("id"),
                                ob.getString("name"),
                                ob.getString("note"),
                                ob.getString("image")
                        );
                        actor_list_data.add(listData);

                    }

                    rv.setAdapter(adapter);


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
