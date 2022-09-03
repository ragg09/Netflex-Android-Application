package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ActorAdpter_withMovie extends RecyclerView.Adapter<ActorAdpter_withMovie.MyViewHolder> {
    private Context mContext;
    private List<ActorGet_withMovie> list_data;
    private static final String urlString ="http://192.168.68.103:8000/" ;

    public ActorAdpter_withMovie(Context mContext, List<ActorGet_withMovie> list_data) {
        this.list_data = list_data;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView list_actor_iv_withMovie;
        private TextView list_actor_tv_title_withMovie, list_actor_tv_producer_name_withMovie, list_actor_tv_producer_website_withMovie;

        public MyViewHolder (final View view){
            super(view);
            list_actor_iv_withMovie = view.findViewById(R.id.list_actor_iv_withMovie);
            list_actor_tv_title_withMovie = view.findViewById(R.id.list_actor_tv_title_withMovie);
            list_actor_tv_producer_name_withMovie = view.findViewById(R.id.list_actor_tv_producer_name_withMovie);
            list_actor_tv_producer_website_withMovie = view.findViewById(R.id.list_actor_tv_producer_website_withMovie);
        }
    }

    @NonNull
    @Override
    public ActorAdpter_withMovie.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.list_actor_with_movie,parent,false);
        return new ActorAdpter_withMovie.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorAdpter_withMovie.MyViewHolder holder, int position) {
        final ActorGet_withMovie listData=list_data.get(position);


        if (listData.getImage().isEmpty()) {
            holder.list_actor_iv_withMovie.setImageResource(R.drawable.ic_android_default);
        } else{
            Picasso.get().load(urlString + listData.getImage()).into(holder.list_actor_iv_withMovie);
        }

        holder.list_actor_tv_title_withMovie.setText(listData.getTitle());
        holder.list_actor_tv_producer_name_withMovie.setText(listData.getProducer_name());
        holder.list_actor_tv_producer_website_withMovie.setText(listData.getProducer_website());

        holder.list_actor_iv_withMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,FilmActivity_ViewbyID.class);
                intent.putExtra("id", listData.getMovie_id()+"");// +"" This is to convert int to String type, otherwise it will report an error
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }


}
