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

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.MyViewHolder>{
    private Context mContext;
    private List<FilmGetAll> list_data;
    private static final String urlString ="http://192.168.68.103:8000/" ;

    public FilmAdapter(Context mContext, List<FilmGetAll> list_data) {
        this.list_data = list_data;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView list_film_iv;
        private TextView list_film_title;


        public MyViewHolder (final View view){
            super(view);
            list_film_iv = view.findViewById(R.id.list_film_iv);
            list_film_title = view.findViewById(R.id.list_film_title);
        }
    }

    @NonNull
    @Override
    public FilmAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.list_film,parent,false);
        return new FilmAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmAdapter.MyViewHolder holder, int position) {
        final FilmGetAll listData=list_data.get(position);

        if (listData.getImage().isEmpty()) {
            holder.list_film_iv.setImageResource(R.drawable.ic_android_default);
        } else{
            Picasso.get().load(urlString + listData.getImage()).into(holder.list_film_iv);
        }

        holder.list_film_title.setText(listData.getTitle());

        holder.list_film_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,FilmActivity_ViewbyID.class);
                intent.putExtra("id", listData.getId()+"");// +"" This is to convert int to String type, otherwise it will report an error
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

}
