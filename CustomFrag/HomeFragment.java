package com.writingstar.autotypingandtextexpansion.CustomFrag;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.writingstar.autotypingandtextexpansion.ClassActView.EditPharesActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.MainActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.PhraseListActivity;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.Interface.OnRecyclerItemClickListener;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.MultiSelectAdapter;
import com.writingstar.autotypingandtextexpansion.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    ActionMode mActionMode;
    RecyclerView recyclerView;
    MultiSelectAdapter multiSelectAdapter;
    boolean isMultiSelect = false;
    ArrayList<TxpGetSet> multiselect_list = new ArrayList<>();
    ArrayList<TxpGetSet> phrase_list = new ArrayList<>();
    SQLiteHelper dbHelper;
    public static Context context;
    RelativeLayout layout_nodata;
    Intent na;

    public static HomeFragment newInstance(Context c) {
        HomeFragment homeFragment = new HomeFragment();
        context = c;
        return homeFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layout_nodata = view.findViewById(R.id.layout_nodata);
    }


    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(phrase_list.get(position)))
                multiselect_list.remove(phrase_list.get(position));
            else
                multiselect_list.add(phrase_list.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.finish();

            refreshAdapter();
        }
    }

    public void refreshAdapter() {
        multiSelectAdapter.selected_usersList = multiselect_list;
        multiSelectAdapter.phraseList = phrase_list;
        multiSelectAdapter.notifyDataSetChanged();
    }

    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
                    builder1.setMessage(getResources().getString(R.string.delete_phrase));
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    deleteItems(1);
                                }
                            });
                    builder1.setNegativeButton(
                            getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            alert11.getButton(alert11.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                            alert11.getButton(alert11.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    });
                    alert11.show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<TxpGetSet>();
            refreshAdapter();
        }
    };

    private void deleteItems(int from) {
        if (from == 1) {
            if (multiselect_list.size() > 0) {
                for (int i = 0; i < multiselect_list.size(); i++) {
                    dbHelper.phraseDelete(multiselect_list.get(i).phrase_id);
                    phrase_list.remove(multiselect_list.get(i));
                }

                multiSelectAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
                onResume();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (phrase_list != null)
            phrase_list.clear();

        dbHelper = new SQLiteHelper(context);
        dbHelper.open();
        phrase_list = dbHelper.getAllphraseList();

        if (phrase_list.size() > 0)
            layout_nodata.setVisibility(View.GONE);
        else
            layout_nodata.setVisibility(View.VISIBLE);


        multiSelectAdapter = new MultiSelectAdapter(context, phrase_list, multiselect_list, new OnRecyclerItemClickListener() {
            @Override
            public void OnClick(int position, View view) {
                if (isMultiSelect)
                    multi_select(position);
                else {
                    Type type = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    Gson gson = new Gson();
                    ArrayList<String> finalOutputString = gson.fromJson(phrase_list.get(position).phrase_detail, type);
                    Log.d("call_txt__", " :: " + finalOutputString);
                    if (finalOutputString.size() > 1)
                        na = new Intent(context, PhraseListActivity.class);
                    else
                        na = new Intent(context, EditPharesActivity.class);

                    na.putExtra("id", phrase_list.get(position).phrase_id);
                    na.putExtra("title", phrase_list.get(position).phrase_title);
                    na.putExtra("desc", phrase_list.get(position).phrase_detail);
                    na.putExtra("note", phrase_list.get(position).phrase_note);
                    startActivity(na);
                }
            }

            @Override
            public void OnLongClick(int position, View view) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<TxpGetSet>();
                    isMultiSelect = true;
                    if (mActionMode == null) {
                        mActionMode = ((MainActivity) context).startActionMode(mActionModeCallback);
                    }
                }
                multi_select(position);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(multiSelectAdapter);
    }

    public void removeActionmode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }
}