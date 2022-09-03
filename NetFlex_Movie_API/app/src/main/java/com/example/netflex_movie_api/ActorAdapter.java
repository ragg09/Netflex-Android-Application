package com.example.netflex_movie_api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.MyViewHolder> {
    private Context mContext;
    private List<ActorGetAll> list_data;
    private static final String urlString ="http://192.168.68.103:8000/" ;

    public ActorAdapter(Context mContext, List<ActorGetAll> list_data) {
        this.list_data = list_data;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageSwitcher list_actor_iv_withMovie;
        private TextView list_actor_name;
        private ImageView list_actor_iv, list_actor_iv_opt;
        private Button list_actor_btn;

        public MyViewHolder (final View view){
            super(view);
            list_actor_name = view.findViewById(R.id.list_actor_name);
            list_actor_iv = view.findViewById(R.id.list_actor_iv);
            list_actor_iv_opt = view.findViewById(R.id.list_actor_iv_opt);


        }
    }


    @NonNull
    @Override
    public ActorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.list_actor,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorAdapter.MyViewHolder holder, int position) {
        final ActorGetAll listData=list_data.get(position);


        if (listData.getImage().isEmpty()) {
            holder.list_actor_iv.setImageResource(R.drawable.ic_android_default);
        } else{
            Picasso.get().load(urlString + listData.getImage()).into(holder.list_actor_iv);
        }

        holder.list_actor_name.setText(listData.getName());

        //VIEW ACTOR AND ALL MOVIE INVOLVEMENTS
        holder.list_actor_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActorActivity_withMovie.class);
                // +"" This is to convert int to String type, otherwise it will report an error
                i.putExtra("actor_id", listData.getId()+"");
                mContext.startActivity(i);
            }
        });

        //ACTOR EDIT UPDATE ACTIVITY
        holder.list_actor_iv_opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ActorActivity_ViewbyID.class);
                intent.putExtra("actor_id", listData.getId()+"");// +"" This is to convert int to String type, otherwise it will report an error
                intent.putExtra("name", listData.getName());
                intent.putExtra("note", listData.getNote());
                intent.putExtra("image", listData.getImage());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }


}
