package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ProducerAdapter extends RecyclerView.Adapter<ProducerAdapter.MyViewHolder> {
    private Context mContext;
    private List<ProducerGetAll> list_data;

    SharedPreferences sp;
    String accessToken;

    public ProducerAdapter(Context mContext, List<ProducerGetAll> list_data) {
        this.list_data = list_data;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView list_producer_name;
        private ImageView list_producer_iv;

        public MyViewHolder (final View view){
            super(view);
            list_producer_iv = view.findViewById(R.id.list_producer_iv);
            list_producer_name = view.findViewById(R.id.list_producer_name);


        }
    }

    @NonNull
    @Override
    public ProducerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.list_producer,parent,false);
        return new ProducerAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ProducerAdapter.MyViewHolder holder, int position) {
        final ProducerGetAll listData=list_data.get(position);

        holder.list_producer_iv.setImageResource(R.drawable.ic_producer);
        holder.list_producer_name.setText(listData.getName());


        holder.list_producer_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,ProducerActivity_ViewbyID.class);
                intent.putExtra("producer_id", listData.getId()+"");// +"" This is to convert int to String type, otherwise it will report an error
                intent.putExtra("name", listData.getName());
                intent.putExtra("email", listData.getEmail());
                intent.putExtra("website", listData.getWebsite());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }



}
