package com.writingstar.autotypingandtextexpansion.NormalAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.writingstar.autotypingandtextexpansion.ClassActView.EditPharesActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.PhraseListActivity;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> mNames = new ArrayList<>();
    private Context mcontext;
    boolean isEditPhrase;

    public RecyclerViewAdapter(ArrayList<String> mNames, Context mcontext, boolean isEditPhrase) {
        this.mNames = mNames;
        this.mcontext = mcontext;
        this.isEditPhrase = isEditPhrase;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txt.setText(mNames.get(position));
        if (SharedPreferenceClass.getBoolean(mcontext, "isDark", false)) {
            holder.txt.setTextColor(mcontext.getResources().getColor(R.color.color_white));
            holder.txt.setTextColor(mcontext.getResources().getColor(R.color.color_white));
        } else {
            holder.txt.setTextColor(mcontext.getResources().getColor(R.color.color_1D1C21));
            holder.txt.setTextColor(mcontext.getResources().getColor(R.color.color_1D1C21));
        }
        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        RelativeLayout parent_layout;

        public ViewHolder(final View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.text_item);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public void showPopup(View itemView, final int position) {

        PopupMenu popup = new PopupMenu(mcontext, itemView);
        switch (position) {
            case 0:
                popup.inflate(R.menu.date_menu_item);
                break;

            case 1:
                popup.inflate(R.menu.time_menu_item);
                break;

            case 2:
                popup.inflate(R.menu.day_menu_item);
                break;

            case 3:
                popup.inflate(R.menu.month_menu_item);
                break;

            case 4:
                popup.inflate(R.menu.year_menu_item);
                break;

            case 5:
                popup.inflate(R.menu.hrs_menu_item);
                break;
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(isEditPhrase){
                    switch (position) {
                        case 0:
                            switch (item.getItemId()) {
                                case R.id.action_date1:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_dd_mm_yyyy), 0, 0);
                                    return true;

                                case R.id.action_date2:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_mm_dd_yyyy), 0, 1);
                                    return true;

                                case R.id.action_date3:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_dd_mm_yy), 0, 2);
                                    return true;

                                case R.id.action_date4:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_mm_dd_yy), 0, 3);
                                    return true;
                            }
                            break;

                        case 1:
                            switch (item.getItemId()) {
                                case R.id.action_time1:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_hh_mm), 1, 0);
                                    return true;

                                case R.id.action_time2:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_hh_mm_ss), 1, 1);
                                    return true;
                            }
                            break;

                        case 2:
                            switch (item.getItemId()) {
                                case R.id.action_day1:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_dayn), 2, 0);
                                    return true;

                                case R.id.action_day2:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_days), 2, 1);
                                    return true;

                                case R.id.action_day3:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_dayf), 2, 2);
                                    return true;
                            }
                            break;

                        case 3:
                            switch (item.getItemId()) {
                                case R.id.action_month1:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_monthn), 3, 0);
                                    return true;

                                case R.id.action_month2:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_months), 3, 1);
                                    return true;

                                case R.id.action_month3:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_monthf), 3, 2);
                                    return true;
                            }
                            break;

                        case 4:
                            switch (item.getItemId()) {
                                case R.id.action_year1:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_years), 4, 0);
                                    return true;

                                case R.id.action_year2:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_yearf), 4, 1);
                                    return true;
                            }
                            break;

                        case 5:
                            switch (item.getItemId()) {
                                case R.id.action_hrs1:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_hrs12), 5, 0);
                                    return true;

                                case R.id.action_hrs2:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_hrs24), 5, 1);
                                    return true;

                                case R.id.action_hrs3:
                                    ((EditPharesActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_am_pm), 5, 2);
                                    return true;
                            }
                            break;
                    }
                }else {
                    switch (position) {
                        case 0:
                            switch (item.getItemId()) {
                                case R.id.action_date1:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_dd_mm_yyyy), 0, 0);
                                    return true;

                                case R.id.action_date2:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_mm_dd_yyyy), 0, 1);
                                    return true;

                                case R.id.action_date3:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_dd_mm_yy), 0, 2);
                                    return true;

                                case R.id.action_date4:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_mm_dd_yy), 0, 3);
                                    return true;
                            }
                            break;

                        case 1:
                            switch (item.getItemId()) {
                                case R.id.action_time1:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_hh_mm), 1, 0);
                                    return true;

                                case R.id.action_time2:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_hh_mm_ss), 1, 1);
                                    return true;
                            }
                            break;

                        case 2:
                            switch (item.getItemId()) {
                                case R.id.action_day1:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_dayn), 2, 0);
                                    return true;

                                case R.id.action_day2:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_days), 2, 1);
                                    return true;

                                case R.id.action_day3:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_dayf), 2, 2);
                                    return true;
                            }
                            break;

                        case 3:
                            switch (item.getItemId()) {
                                case R.id.action_month1:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_monthn), 3, 0);
                                    return true;

                                case R.id.action_month2:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_months), 3, 1);
                                    return true;

                                case R.id.action_month3:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_monthf), 3, 2);
                                    return true;
                            }
                            break;

                        case 4:
                            switch (item.getItemId()) {
                                case R.id.action_year1:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_years), 4, 0);
                                    return true;

                                case R.id.action_year2:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_yearf), 4, 1);
                                    return true;
                            }
                            break;

                        case 5:
                            switch (item.getItemId()) {
                                case R.id.action_hrs1:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_hrs12), 5, 0);
                                    return true;

                                case R.id.action_hrs2:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_hrs24), 5, 1);
                                    return true;

                                case R.id.action_hrs3:
                                    ((PhraseListActivity) mcontext).changePhrase(mcontext.getResources().getString(R.string.format_am_pm), 5, 2);
                                    return true;
                            }
                            break;
                    }
                }

                return false;
            }
        });
        popup.show();
    }
}


