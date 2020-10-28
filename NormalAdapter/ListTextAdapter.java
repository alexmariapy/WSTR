package com.writingstar.autotypingandtextexpansion.NormalAdapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.ArrayList;
import java.util.List;

public class ListTextAdapter extends RecyclerView.Adapter<ListTextAdapter.MoreappViewHolder> {
    Context context;
    List<TxpGetSet> txt_list = new ArrayList<>();

    public ListTextAdapter(Context context, List<TxpGetSet> txt_list) {
        this.context = context;
        this.txt_list = txt_list;
    }

    @NonNull
    @Override
    public MoreappViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.txt_list, parent, false);
        return new MoreappViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreappViewHolder holder, int position) {
        final TxpGetSet data = txt_list.get(position);
        holder.txt_lst.setText(data.getPhrase_detail());
        if (txt_list.size() == 1)
            holder.bool.setVisibility(View.GONE);
        else
            holder.bool.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return txt_list.size();
    }

    class MoreappViewHolder extends RecyclerView.ViewHolder {
        TextView txt_lst, bool;

        public MoreappViewHolder(View view) {
            super(view);
            txt_lst = (TextView) view.findViewById(R.id.txt_lst);
            bool = (TextView) view.findViewById(R.id.bool);
        }
    }

}
