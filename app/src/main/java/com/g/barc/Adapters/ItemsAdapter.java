package com.g.barc.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.g.barc.Classes.Items;
import com.g.barc.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder>{

    List<Items> lists;
    Context context;

    public ItemsAdapter(List<Items> lists, Context context) {

        // generate constructors to initialise the List and Context objects

        this.lists = lists;
        this.context = context;


    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, final int position) {
        final Items myList = lists.get(position);
        System.out.println(myList.getItemname());

        holder.name.setText(myList.getItemname());
        holder.price.setText(myList.getPrice());

//        if (!myList.getPhoto().equals("")) {
//            Picasso.with(context)
//                    .load(myList.getPhoto())
//                    .into(holder.avatar_url);
//        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, price;
        public ImageView avatar_url;
        public CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.list_name);
            price = itemView.findViewById(R.id.list_price);
        }
    }
}
