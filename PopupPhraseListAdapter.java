package com.writingstar.autotypingandtextexpansion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Interface.OnRecyclerItemClickListenerValueChange;
import com.writingstar.autotypingandtextexpansion.Model.PhraseListGetSet;

import java.util.ArrayList;
import java.util.List;

public class PopupPhraseListAdapter extends RecyclerView.Adapter<PopupPhraseListAdapter.ViewHolder> {
    public ArrayList<String> phraselist = new ArrayList();
    public Context context;
    SQLiteHelper dbHelper;
    OnRecyclerItemClickListenerValueChange onRecyclerItemClickListener;


    public PopupPhraseListAdapter(Context context2, ArrayList<String> list, OnRecyclerItemClickListenerValueChange onRecyclerItemClickListener) {
        context = context2;
        phraselist = list;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
        dbHelper = new SQLiteHelper(context2);
        dbHelper.open();

    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.popup_phrase_list_item, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.text_acess.setText(phraselist.get(i));
        if (SharedPreferenceClass.getBoolean(context, "isDark", false)) {
            viewHolder.text_acess.setTextColor(context.getResources().getColor(R.color.color_white));
        } else {
            viewHolder.text_acess.setTextColor(context.getResources().getColor(R.color.color_1D1C21));
        }
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRecyclerItemClickListener != null)
                    onRecyclerItemClickListener.OnClick(i, v,phraselist.get(i));
            }
        });
    }


    public int getItemCount() {
        Log.d("SZ___"," sz : "+phraselist.size());
        return phraselist.size();
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TextView text_acess;
        RelativeLayout parent_layout;

        public ViewHolder(View view) {
            super(view);
            text_acess = (TextView) view.findViewById(R.id.text_acess);
            parent_layout = (RelativeLayout) view.findViewById(R.id.parent_layout);
        }
    }


}
