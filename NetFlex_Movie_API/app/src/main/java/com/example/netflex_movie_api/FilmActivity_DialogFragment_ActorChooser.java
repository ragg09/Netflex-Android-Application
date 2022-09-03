package com.example.netflex_movie_api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmActivity_DialogFragment_ActorChooser extends AppCompatDialogFragment {
    Context mContext;
    private static final String actorURL ="http://192.168.68.103:8000/api/actor/" ;

    private List<ActorGetAll> actor_list_data;
    private List<String> actor_list_names;
    private RecyclerView rv;
    FilmAdapter_ActorChooser adapter;
    private URL myURL;

    SharedPreferences sp;
    String accessToken;

    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_choose_actor, null);

        //SETTING RV, actor_list_dat, and adapter
        rv=(RecyclerView) view.findViewById(R.id.layout_choose_actor_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        actor_list_data=new ArrayList<>();
        adapter=new FilmAdapter_ActorChooser(getActivity().getApplicationContext(),actor_list_data);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getActivity().getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();
        mContext = getActivity().getApplicationContext();

        builder.setView(view)
                .setTitle("Choose Actors")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
        .setPositiveButton("Get", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Getting Actors name from shared preference, this this it is a single string with commas
                //but Ill use split to separate each name
                String[] pangalan = sp.getString("Actors_to_movie","").split("\\,");

                //creating ang arralist, intended to be populated by names
                actor_list_names = new ArrayList<String>();
                //looping throurg names and append it to the array
                for(String name: pangalan){
                    //Toast.makeText(FilmActivity_Add.this, name, Toast.LENGTH_SHORT).show();
                    actor_list_names.add(name.toUpperCase() + " ");
                }
                listener.actor_names(actor_list_names.toString().replace("[", "").replace("]", ""));

            }
        });


        loadActorData();
        return  builder.create();


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener)context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() + "Must Implement Dialoglistener");
        }
    }

    public interface DialogListener{
        void actor_names(String names);
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
                    //JSONObject jsonObject=new JSONObject(response);
                    //JSONArray array=jsonObject.getJSONArray("data");
                    JSONArray array=response.getJSONArray("data");
                    for (int i=0; i<array.length(); i++ ){
                        JSONObject ob=array.getJSONObject(i);
                        ActorGetAll listData=new ActorGetAll(
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
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonRequest);


    }


}
