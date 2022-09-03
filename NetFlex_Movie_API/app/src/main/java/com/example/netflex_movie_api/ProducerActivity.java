package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

public class ProducerActivity extends AppCompatActivity {
    private FloatingActionButton btnadd_producer;
    Context mContext;
    private static final String producerURL ="http://192.168.68.103:8000/api/producer/" ;

    private List<ProducerGetAll> producer_list_data;
    private RecyclerView rv;
    private ProducerAdapter adapter;
    private URL myURL;

    SwipeRefreshLayout refreshLayout;

    SharedPreferences sp;

    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();
        mContext = getApplicationContext();

        //SETTING RV, producer_list_dat, and adapter
        rv=(RecyclerView)findViewById(R.id.producer_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        producer_list_data=new ArrayList<>();
        adapter=new ProducerAdapter(this, producer_list_data);

        btnadd_producer = (FloatingActionButton)findViewById(R.id.btnadd_producer);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.producer_refresh);


        btnadd_producer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProducerActivity.this, ProducerActivity_Add.class);
                startActivity(i);
            }
        });

        //REFRESH RV    REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                producer_list_data.clear(); //clearing existing array
                loadProducerData();
                refreshLayout.setRefreshing(false);
            }
        });
        //^^^REFRESH RV    REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV  REFRESH RV

        loadProducerData();

    }//END of ONCREATE

    public void loadProducerData(){

        try {
            myURL = new URL(producerURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                producerURL,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array=response.getJSONArray("data");
                    for (int i=0; i<array.length(); i++ ){
                        JSONObject ob=array.getJSONObject(i);
                        ProducerGetAll listData = new ProducerGetAll(
                                ob.getInt("id"),
                                ob.getString("name"),
                                ob.getString("email"),
                                ob.getString("website")
                        );
                        producer_list_data.add(listData);
                        //Toast.makeText(mContext, "PATI TO!", Toast.LENGTH_SHORT).show();
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
