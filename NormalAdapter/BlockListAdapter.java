package com.writingstar.autotypingandtextexpansion.NormalAdapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.writingstar.autotypingandtextexpansion.ClassActView.BlockListApp;
import com.writingstar.autotypingandtextexpansion.ClassActView.LoadAppList;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Interface.MyOnItemClickListener;
import com.writingstar.autotypingandtextexpansion.Model.LoadAppModel;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.ArrayList;
import java.util.List;

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.ViewHolder> {
    public List<LoadAppModel> appInfo1s = new ArrayList();
    public Context context;
    public MyOnItemClickListener onItemClickListener;
    SQLiteHelper dbHelper;


    public BlockListAdapter(Context context2, List<LoadAppModel> list, MyOnItemClickListener onItemClickListener2) {
        context = context2;
        appInfo1s = list;
        onItemClickListener = onItemClickListener2;
        dbHelper = new SQLiteHelper(context2);
        dbHelper.open();
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_item, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        if (SharedPreferenceClass.getBoolean(context, "isDark", false)) {
            viewHolder.lay_applist.setBackground(context.getResources().getDrawable(R.drawable.border_day_night));
        } else {
            viewHolder.lay_applist.setBackground(context.getResources().getDrawable(R.drawable.border_day));
        }
        if (i == appInfo1s.size() - 1)
            setMargins(viewHolder.lay_applist, 16, 16, 16, 16);
        else
            setMargins(viewHolder.lay_applist, 16, 16, 16, 0);

        final LoadAppModel item = getItem(i);
        try {
            PackageManager packageManager= context.getPackageManager();
            String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(item.app_package, PackageManager.GET_META_DATA));
            viewHolder.appsName.setText(appName);
             viewHolder.img.setImageDrawable(context.getPackageManager().getApplicationIcon(item.app_package));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.appDelete(item.app_id);
                ((BlockListApp)context).callRefresh();
            }
        });



    }

    public LoadAppModel getItem(int i) {
        return (LoadAppModel) this.appInfo1s.get(i);
    }

    public int getItemCount() {
        return appInfo1s.size();
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TextView appsName;
        ImageView img,remove;
        RelativeLayout lay_applist;

        public ViewHolder(View view) {
            super(view);
            appsName = (TextView) view.findViewById(R.id.appsName);
            img = (ImageView) view.findViewById(R.id.img);
            remove = (ImageView) view.findViewById(R.id.remove);
            lay_applist = (RelativeLayout) view.findViewById(R.id.lay_applist);
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = context.getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l = (int) (left * scale + 0.5f);
            int r = (int) (right * scale + 0.5f);
            int t = (int) (top * scale + 0.5f);
            int b = (int) (bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }
}
