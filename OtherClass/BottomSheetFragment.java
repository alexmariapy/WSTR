package com.writingstar.autotypingandtextexpansion.OtherClass;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.writingstar.autotypingandtextexpansion.ClassActView.EditPharesActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.PhraseListActivity;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.ArrayList;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener{

    SwitchCompat back_space_undo, disable_smart_case, dont_append_space, space_for_expansion, expand_within_word;
    Context context;
    int from;
    SQLiteHelper dbHelper;
    ArrayList<TxpGetSet> phrase_list = new ArrayList<>();
    int id;
    View v0,v1,v2,v3,v4,v5,v6;
    int bsu = 0, dsc = 0, das = 0, sfe = 0, eww = 0;
    RelativeLayout lay_expand_within_word,lay_back_undo,lay_smartcase,lay_uppend_space,lay_space_expansion;
    TextView txt_use_count,txt_modified,txt_title;
    boolean isFromEdit;
    public BottomSheetFragment(Context context, int from, int id,boolean isFromEdit) {
        this.context = context;
        this.from = from;
        this.id = id;
        this.isFromEdit = isFromEdit;
        dbHelper = new SQLiteHelper(context);
        dbHelper.open();
        phrase_list = dbHelper.getAllphraseList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
    }

    private void initialize(View view) {
        txt_title = view.findViewById(R.id.txt_title);
        back_space_undo = view.findViewById(R.id.back_space_undo);
        disable_smart_case = view.findViewById(R.id.disable_smart_case);
        dont_append_space = view.findViewById(R.id.dont_append_space);
        space_for_expansion = view.findViewById(R.id.space_for_expansion);
        expand_within_word = view.findViewById(R.id.expand_within_word);
        txt_modified = view.findViewById(R.id.txt_modified);
        txt_use_count = view.findViewById(R.id.txt_use_count);
        v0 = view.findViewById(R.id.v0);
        v1 = view.findViewById(R.id.v1);
        v2 = view.findViewById(R.id.v2);
        v3 = view.findViewById(R.id.v3);
        v4 = view.findViewById(R.id.v4);
        v5 = view.findViewById(R.id.v5);
        v6 = view.findViewById(R.id.v6);


        lay_expand_within_word = view.findViewById(R.id.lay_expand_within_word);
        lay_back_undo = view.findViewById(R.id.lay_back_undo);
        lay_smartcase = view.findViewById(R.id.lay_smartcase);
        lay_uppend_space = view.findViewById(R.id.lay_uppend_space);
        lay_space_expansion = view.findViewById(R.id.lay_space_expansion);

        lay_expand_within_word.setOnClickListener(this);
        lay_back_undo.setOnClickListener(this);
        lay_smartcase.setOnClickListener(this);
        lay_uppend_space.setOnClickListener(this);
        lay_space_expansion.setOnClickListener(this);

        for (int i = 0; i < phrase_list.size(); i++) {
            if (phrase_list.get(i).getPhrase_id() == id) {
                bsu = phrase_list.get(i).backspace_undo;
                dsc = phrase_list.get(i).smart_case;
                das = phrase_list.get(i).append_case;
                sfe = phrase_list.get(i).space_for_expansion;
                eww = phrase_list.get(i).within_words;
                txt_modified.setText(phrase_list.get(i).phrase_modified_time);
                txt_use_count.setText(""+phrase_list.get(i).phrase_usage_count);
            }
        }

        if (SharedPreferenceClass.getBoolean(context, "change_toggle", false)) {
            Log.d("chk_sp_val_",
                      "\n1 :: " + SharedPreferenceClass.getInteger(context, "bsu", 0) +
                            "\n2 :: " + SharedPreferenceClass.getInteger(context, "dsc", 0) +
                            "\n3 :: " + SharedPreferenceClass.getInteger(context, "das", 0) +
                            "\n4 :: " + SharedPreferenceClass.getInteger(context, "sfe", 0) +
                            "\n5 :: " + SharedPreferenceClass.getInteger(context, "eww", 0));

            if (SharedPreferenceClass.getInteger(context, "bsu", 0) == 1) {
                back_space_undo.setChecked(true);
            } else {
                back_space_undo.setChecked(false);
            }

            if (SharedPreferenceClass.getInteger(context, "dsc", 0) == 1) {
                disable_smart_case.setChecked(true);
            } else {
                disable_smart_case.setChecked(false);
            }

            if (SharedPreferenceClass.getInteger(context, "das", 0) == 1) {
                dont_append_space.setChecked(true);
            } else {
                dont_append_space.setChecked(false);
            }

            if (SharedPreferenceClass.getInteger(context, "sfe", 0) == 1) {
                space_for_expansion.setChecked(true);
            } else {
                space_for_expansion.setChecked(false);
            }

            if (SharedPreferenceClass.getInteger(context, "eww", 0) == 1) {
                expand_within_word.setChecked(true);
            } else {
                expand_within_word.setChecked(false);
            }
        } else {
            if (from == 1) {
                Log.d("chk_sp_val_",
                        " \n1 :: " + bsu +
                                "\n2 :: " + dsc +
                                "\n3 :: " + das +
                                "\n4 :: " + sfe +
                                "\n5 :: " + eww);
                if (bsu == 1) {
                    back_space_undo.setChecked(true);
                    SharedPreferenceClass.setInteger(context, "bsu", 1);
                } else {
                    back_space_undo.setChecked(false);
                    SharedPreferenceClass.setInteger(context, "bsu", 0);
                }

                if (dsc == 1) {
                    SharedPreferenceClass.setInteger(context, "dsc", 1);
                    disable_smart_case.setChecked(true);
                } else {
                    SharedPreferenceClass.setInteger(context, "dsc", 0);
                    disable_smart_case.setChecked(false);
                }

                if (das == 1) {
                    SharedPreferenceClass.setInteger(context, "das", 1);
                    dont_append_space.setChecked(true);
                } else {
                    SharedPreferenceClass.setInteger(context, "das", 0);
                    dont_append_space.setChecked(false);
                }

                if (sfe == 1) {
                    SharedPreferenceClass.setInteger(context, "sfe", 1);
                    space_for_expansion.setChecked(true);
                } else {
                    SharedPreferenceClass.setInteger(context, "sfe", 0);
                    space_for_expansion.setChecked(false);
                }

                if (eww == 1) {
                    SharedPreferenceClass.setInteger(context, "eww", 1);
                    expand_within_word.setChecked(true);
                } else {
                    SharedPreferenceClass.setInteger(context, "eww", 0);
                    expand_within_word.setChecked(false);
                }
            }
        }


        dbHelper.close();

        if (SharedPreferenceClass.getBoolean(context, "isDark", false)) {
            v0.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_night));
            v1.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_night));
            v2.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_night));
            v3.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_night));
            v4.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_night));
            v5.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_night));
            v6.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_night));
        } else {
            v0.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            v1.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            v2.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            v3.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            v4.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            v5.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            v6.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }


        if(!isFromEdit){
            txt_title.setText(context.getResources().getString(R.string.info));
            lay_back_undo.setVisibility(View.GONE);
            lay_expand_within_word.setVisibility(View.GONE);
            lay_smartcase.setVisibility(View.GONE);
            lay_uppend_space.setVisibility(View.GONE);
            lay_space_expansion.setVisibility(View.GONE);
        }else {
            txt_title.setText(context.getResources().getString(R.string.settings));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.lay_expand_within_word:
                    if (!expand_within_word.isChecked()) {
                        expand_within_word.setChecked(true);
                        ((EditPharesActivity) context).changeVal(Constant.expand_within_word, 1);
                    } else {
                        expand_within_word.setChecked(false);
                        ((EditPharesActivity) context).changeVal(Constant.expand_within_word, 0);
                    }
                    break;
                case R.id.lay_back_undo:
                    if (!back_space_undo.isChecked()) {
                        back_space_undo.setChecked(true);
                        ((EditPharesActivity) context).changeVal(Constant.back_space_undo, 1);
                    } else {
                        back_space_undo.setChecked(false);
                        ((EditPharesActivity) context).changeVal(Constant.back_space_undo, 0);
                    }
                    break;
                case R.id.lay_smartcase:
                    if (!disable_smart_case.isChecked()) {
                        disable_smart_case.setChecked(true);
                        ((EditPharesActivity) context).changeVal(Constant.disable_smart_case, 1);
                    } else {
                        disable_smart_case.setChecked(false);
                        ((EditPharesActivity) context).changeVal(Constant.disable_smart_case, 0);
                    }
                    break;
                case R.id.lay_uppend_space:
                    if (!dont_append_space.isChecked()) {
                        dont_append_space.setChecked(true);
                        ((EditPharesActivity) context).changeVal(Constant.dont_append_space, 1);
                    } else {
                        dont_append_space.setChecked(false);
                        ((EditPharesActivity) context).changeVal(Constant.dont_append_space, 0);
                    }
                    break;
                case R.id.lay_space_expansion:
                    if (!space_for_expansion.isChecked()) {
                        space_for_expansion.setChecked(true);
                        ((EditPharesActivity) context).changeVal(Constant.space_for_expansion, 1);
                    } else {
                        space_for_expansion.setChecked(false);
                        ((EditPharesActivity) context).changeVal(Constant.space_for_expansion, 0);
                    }
                    break;

            }
    }
}
