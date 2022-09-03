package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
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

public class FilmAdapter_withActor extends RecyclerView.Adapter<FilmAdapter_withActor.MyViewHolder> {
    private Context mContext;
    private List<FilmGet_withActor> list_data;
    private static final String urlString ="http://192.168.68.103:8000/" ;

    public FilmAdapter_withActor(Context mContext, List<FilmGet_withActor> list_data) {
        this.list_data = list_data;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView list_actor_in_movie_iv;
        private TextView list_actor_in_movie_name, list_actor_in_movie_note;

        public MyViewHolder (final View view){
            super(view);
            list_actor_in_movie_iv = view.findViewById(R.id.list_actor_in_movie_iv);
            list_actor_in_movie_name = view.findViewById(R.id.list_actor_in_movie_name);
            list_actor_in_movie_note = view.findViewById(R.id.list_actor_in_movie_note);
        }
    }

    @NonNull
    @Override
    public FilmAdapter_withActor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.list_actor_in_movie,parent,false);
        return new FilmAdapter_withActor.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmAdapter_withActor.MyViewHolder holder, int position) {
        final FilmGet_withActor listData=list_data.get(position);


        if (listData.getImage().isEmpty()) {
            holder.list_actor_in_movie_iv.setImageResource(R.drawable.ic_android_default);
        } else{
            Picasso.get().load(urlString + listData.getImage()).into(holder.list_actor_in_movie_iv);
        }

        holder.list_actor_in_movie_name.setText(listData.getName());
        holder.list_actor_in_movie_note.setText(listData.getNote());

    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }



}
