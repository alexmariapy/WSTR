package com.writingstar.autotypingandtextexpansion.NormalAdapter;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Interface.OnRecyclerItemClickListener;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;
import com.writingstar.autotypingandtextexpansion.R;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    public ArrayList<TxpGetSet> phraseList = new ArrayList<>();
    public ArrayList<TxpGetSet> selected_usersList = new ArrayList<>();
    Context mContext;
    OnRecyclerItemClickListener onRecyclerItemClickListener;
    public ArrayList<TxpGetSet> categoryArray = new ArrayList<TxpGetSet>();
    ListTextAdapter listTextAdapter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc, time;
        public LinearLayout ll_listitem;
        RecyclerView recycler_txt;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tv_title);
            //desc = (TextView) view.findViewById(R.id.tv_desc);
            time = (TextView) view.findViewById(R.id.tv_time);
            ll_listitem = (LinearLayout) view.findViewById(R.id.ll_listitem);
            recycler_txt = (RecyclerView) view.findViewById(R.id.recycler_txt);
        }
    }


    public SearchAdapter(Context context, ArrayList<TxpGetSet> phraseList, ArrayList<TxpGetSet> selectedList, OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.mContext = context;
        this.phraseList = phraseList;
        this.selected_usersList = selectedList;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        TxpGetSet item = phraseList.get(position);
        holder.title.setText(item.getPhrase_title());
        holder.time.setText("Not used yet");
        List<TxpGetSet> mPhraseList= new ArrayList<>();

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        ArrayList<String> finalOutputString = gson.fromJson(item.getPhrase_detail(), type);
        Log.d("call_txt__", " :: " + finalOutputString);
        for(int i=0;i < finalOutputString.size();i++){
            mPhraseList.add(new TxpGetSet(finalOutputString.get(i)));
        }


        holder.recycler_txt.setLayoutManager(new LinearLayoutManager(mContext));
        listTextAdapter=new ListTextAdapter(mContext,mPhraseList);
        holder.recycler_txt.setAdapter(listTextAdapter);
        holder.recycler_txt.setLayoutFrozen(true);


        if (!item.getPhrase_use_time().contains("Not used yet")) {
            String time = "";
            long[] converter = new long[6];
            long ms = Long.parseLong(item.getPhrase_use_time());
            converter[0] = (System.currentTimeMillis() - ms) / (1000 * 60 * 60 * 24 * 365); // years
            converter[1] = (System.currentTimeMillis() - ms) / (1000 * 60 * 60 * 24 * 30); // months
            converter[2] = (System.currentTimeMillis() - ms) / (1000 * 60 * 60 * 24); // days
            converter[3] = (System.currentTimeMillis() - ms) / (1000 * 60 * 60); // hours
            converter[4] = (System.currentTimeMillis() - ms) / (1000 * 60); // minutes
            converter[5] = (System.currentTimeMillis() - ms) / 1000; // seconds

            boolean dt = false;
            Log.d("date_time :: ", "" + converter[0] + " years, " + converter[1] + " months, " + converter[2] + " days, " + converter[3] + " hours, " + converter[4] + " minutes, " + converter[5] + " seconds.");
            if (converter[5] < 60)
                time = converter[5] + " sec";
            else if (converter[4] < 60)
                time = converter[4] + " minute";
            else if (converter[3] < 24)
                time = converter[3] + " hour";
            else if (converter[2] < 16) {
                time = converter[2] + " Day";
            } else {
                time = getDate(ms, "d MMM yy");
                dt = true;
            }

            if (dt)
                holder.time.setText(time + "");
            else
                holder.time.setText(time + " ago");


        }


        if (position == phraseList.size() - 1)
            setMargins(holder.ll_listitem, 16, 16, 16, 16);
        else
            setMargins(holder.ll_listitem, 16, 16, 16, 0);


        if (SharedPreferenceClass.getBoolean(mContext, "isDark", false)) {
            if (selected_usersList.contains(phraseList.get(position)))
                holder.ll_listitem.setBackground(mContext.getResources().getDrawable(R.drawable.border_selected));
            else
                holder.ll_listitem.setBackground(mContext.getResources().getDrawable(R.drawable.border_day_night));
        } else {
            if (selected_usersList.contains(phraseList.get(position)))
                holder.ll_listitem.setBackground(mContext.getResources().getDrawable(R.drawable.border_selected));
            else
                holder.ll_listitem.setBackground(mContext.getResources().getDrawable(R.drawable.border_day));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRecyclerItemClickListener != null)
                    onRecyclerItemClickListener.OnClick(position, v);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerItemClickListener != null)
                    onRecyclerItemClickListener.OnLongClick(position, v);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return phraseList.size();
    }


    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = mContext.getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l = (int) (left * scale + 0.5f);
            int r = (int) (right * scale + 0.5f);
            int t = (int) (top * scale + 0.5f);
            int b = (int) (bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }


    public String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}

