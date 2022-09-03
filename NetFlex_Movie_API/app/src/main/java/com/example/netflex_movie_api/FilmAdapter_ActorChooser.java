package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.List;

public class FilmAdapter_ActorChooser extends RecyclerView.Adapter<FilmAdapter_ActorChooser.MyViewHolder>{
    private Context mContext;
    private List<ActorGetAll> list_data;
    private static final String urlString ="http://192.168.68.103:8000/" ;

    public FilmAdapter_ActorChooser(Context mContext, List<ActorGetAll> list_data) {
        this.list_data = list_data;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView list_actor_chooser_iv;
        private TextView list_actor_chooser_name;
        private CheckBox list_actor_chooser_cb;
        SharedPreferences sp;

        public MyViewHolder (final View view){
            super(view);
            list_actor_chooser_iv = view.findViewById(R.id.list_actor_chooser_iv);
            list_actor_chooser_name = view.findViewById(R.id.list_actor_chooser_name);
            list_actor_chooser_cb = view.findViewById(R.id.list_actor_chooser_cb);
            sp = view.getContext().getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);


        }
    }

    @NonNull
    @Override
    public FilmAdapter_ActorChooser.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.list_actor_chooser,parent,false);
        return new FilmAdapter_ActorChooser.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmAdapter_ActorChooser.MyViewHolder holder, int position) {
        final ActorGetAll listData = list_data.get(position);


        if (listData.getImage().isEmpty()) {
            holder.list_actor_chooser_iv.setImageResource(R.drawable.ic_android_default);
        } else {
            Picasso.get().load(urlString + listData.getImage()).into(holder.list_actor_chooser_iv);
        }

        holder.list_actor_chooser_name.setText(listData.getName());

        holder.list_actor_chooser_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Toast.makeText(mContext, "TRY", Toast.LENGTH_SHORT).show();
//                    JSONArray Actors = new JSONArray();
//                    String actors = holder.sp.getString("Actors_to_movie", "");
//                    Actors.put(actors+ ","+listData.getName());
//                    SharedPreferences.Editor editor = holder.sp.edit();
//                    editor.putString("Actors_to_movie", Actors.toString());
//                    editor.commit();

                    SharedPreferences.Editor editor = holder.sp.edit();
                    holder.sp.getString("Actors_to_movie", "");
                    holder.sp.getString("Actors_to_movie_id", "");

                    editor.putString("Actors_to_movie", holder.sp.getString("Actors_to_movie", "")+listData.getName()+ ",");
                    editor.putString("Actors_to_movie_id", holder.sp.getString("Actors_to_movie_id", "")+listData.getId()+ ",");
                    editor.commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }


}
