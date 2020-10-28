package com.writingstar.autotypingandtextexpansion.OtherClass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.writingstar.autotypingandtextexpansion.ClassActView.EditPharesActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.MainActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.PhraseListActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.PremiumScreenActivity;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.ArrayList;

public class BottomSheetChooseActivity extends BottomSheetDialogFragment {

    Context context;
    LinearLayout act_phrase_list, act_phrase;
    TextView txt_rem;
    ImageView vp_tag;
    boolean prem;
    int size;

    public BottomSheetChooseActivity(Context context, boolean prem, int size) {
        this.context = context;
        this.prem = prem;
        this.size = size;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_act_bottom_sheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act_phrase = view.findViewById(R.id.act_phrase);
        act_phrase_list = view.findViewById(R.id.act_phrase_list);
        txt_rem = view.findViewById(R.id.txt_rem);
        vp_tag = view.findViewById(R.id.vp_tag);

        if (prem) {
            txt_rem.setVisibility(View.VISIBLE);
            vp_tag.setVisibility(View.VISIBLE);
            txt_rem.setText((15 - size) + " Remaining");
        } else {
            txt_rem.setVisibility(View.GONE);
            vp_tag.setVisibility(View.GONE);
        }
        act_phrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).dismissDialog();
                if (prem && size > 14) {
                    context.startActivity(new Intent(context, PremiumScreenActivity.class));
                } else
                    context.startActivity(new Intent(context, EditPharesActivity.class));
            }
        });
        act_phrase_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).dismissDialog();
                if (prem) {
                    context.startActivity(new Intent(context, PremiumScreenActivity.class));
                } else
                    context.startActivity(new Intent(context, PhraseListActivity.class));
            }
        });
    }

}
