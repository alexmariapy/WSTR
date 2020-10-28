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
import com.writingstar.autotypingandtextexpansion.Model.MoreappModel;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.ArrayList;

public class MoreappAdapter extends RecyclerView.Adapter<MoreappAdapter.MoreappViewHolder> {
    Context context;
    ArrayList<MoreappModel> moreapp_list = new ArrayList<>();
    public MoreappAdapter(Context context, ArrayList<MoreappModel> moreapp_list) {
        this.context = context;
        this.moreapp_list = moreapp_list;
    }

    @NonNull
    @Override
    public MoreappViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.moreapp_list, parent, false);
        return new MoreappViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreappViewHolder holder, int position) {
            final MoreappModel data=moreapp_list.get(position);
            holder.txt_title.setText(data.getAppName());
            holder.txt_disc.setText(data.getAppDesc());
            if (data.getAppIconUrl()!=null){
                Glide.with(context).load(data.getAppIconUrl()).into(holder.image);
            }


            holder.lin_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getAppinstalled()==1){
                        context.startActivity(context.getPackageManager().getLaunchIntentForPackage(data.getAppPackageName()));
                    }else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getAppShortUrl()));
                        context.startActivity(browserIntent);
                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return moreapp_list.size();
    }

    class MoreappViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView txt_title,txt_disc;
        LinearLayout btn_install;
        LinearLayout lin_main;

        public MoreappViewHolder(View view) {
            super(view);
            image=(ImageView)view.findViewById(R.id.moreapp_imag);
            txt_title=(TextView)view.findViewById(R.id.txt_title);
            txt_disc=(TextView)view.findViewById(R.id.txt_disc);
            btn_install=(LinearLayout)view.findViewById(R.id.btn_install);
            lin_main=(LinearLayout)view.findViewById(R.id.lin_main);
        }
    }

}
