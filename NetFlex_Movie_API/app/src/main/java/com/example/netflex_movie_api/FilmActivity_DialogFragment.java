package com.example.netflex_movie_api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
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

public class FilmActivity_DialogFragment extends AppCompatDialogFragment {

    Context mContext;
    private static final String urlFilm ="http://192.168.68.103:8000/api/film/" ;

    private List<FilmGet_withActor> actor_list_data;
    private RecyclerView rv;
    private FilmAdapter_withActor adapter;
    private URL myURL;

    SharedPreferences sp;
    String accessToken;




    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_actors_in_film, null);

        //I USE .getActivity to reference everything from the activity to this fragment

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getActivity().getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();
        mContext = getActivity().getApplicationContext();

        //SETTING RV, actor_list_dat, and adapter
        rv=(RecyclerView)view.findViewById(R.id.layout_actors_in_film_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        actor_list_data=new ArrayList<>();
        adapter=new FilmAdapter_withActor(mContext,actor_list_data);

        //GETTING DATA FROM INTENT using the Activity where this fragment is called
        String id = getActivity().getIntent().getExtras().getString("id");

        //Toast.makeText(getActivity().getApplicationContext(), id, Toast.LENGTH_SHORT).show();


        builder.setView(view)
                .setTitle("ACTORS IN THIS FILM")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });



        loadActorData(id);
        return  builder.create();
    }

    public void loadActorData(String id){
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,     //kung anong type of request
                searchURL,              //url na pupuntahan nung request
                null,        //since get method ito, walang data na pinapasa kaya null lang
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //getting object from film response
                            JSONArray array = response.getJSONArray("data_actors");
                            for (int i=0; i<array.length(); i++ ){
                                JSONObject ob=array.getJSONObject(i);
                                FilmGet_withActor listData=new FilmGet_withActor(
                                        ob.getInt("id"),
                                        ob.getString("name"),
                                        ob.getString("note"),
                                        ob.getString("image")
                                );
                                actor_list_data.add(listData);

                            }
                            rv.setAdapter(adapter);
                            //JSONObject obj=array.getJSONObject(0);
                            //Toast.makeText(ActorActivity.this, list_data.toString(), Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "MAY MALING NANGYAYARE!!", Toast.LENGTH_LONG).show();
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
