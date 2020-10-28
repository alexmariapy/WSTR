package com.writingstar.autotypingandtextexpansion.OtherClass;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.writingstar.autotypingandtextexpansion.ClassActView.AppearanceActivity;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.R;

public class BottomSheetSeekBar extends BottomSheetDialogFragment {
    Context context;
    int from;
    TextView title_bottom, txt_seekbar;
    SeekBar seekbar;

    public BottomSheetSeekBar(Context context, int from) {
        this.context = context;
        this.from = from;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.seekbar_bottom_sheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title_bottom = view.findViewById(R.id.title_bottom);
        txt_seekbar = view.findViewById(R.id.txt_seekbar);
        seekbar = view.findViewById(R.id.seekbar);
        Log.d("SH_VAL__", "" +
                SharedPreferenceClass.getInteger(context, "thresold", 0) + " :: " +
                SharedPreferenceClass.getInteger(context, "max_suggation", 0) + " :: " +
                SharedPreferenceClass.getInteger(context, "timeout", 0));
        switch (from) {
            case 1:
                txt_seekbar.setText("" + (SharedPreferenceClass.getInteger(context, "thresold", 0) + 2));
                seekbar.setProgress(SharedPreferenceClass.getInteger(context, "thresold", 0));
                title_bottom.setText(context.getResources().getString(R.string.sugg_thresold));
                break;
            case 2:
                txt_seekbar.setText("" + (SharedPreferenceClass.getInteger(context, "max_suggation", 0) + 3));
                seekbar.setProgress(SharedPreferenceClass.getInteger(context, "max_suggation", 0));
                title_bottom.setText(context.getResources().getString(R.string.max_sortcut_show));
                break;
            case 3:
                txt_seekbar.setText("" + (SharedPreferenceClass.getInteger(context, "timeout", 0) + 3));
                seekbar.setProgress(SharedPreferenceClass.getInteger(context, "timeout", 0));
                title_bottom.setText(context.getResources().getString(R.string.over_time_out));
                break;
            case 4:
                txt_seekbar.setText("" + (SharedPreferenceClass.getInteger(context, "opicity", 4) + 5));
                seekbar.setProgress(SharedPreferenceClass.getInteger(context, "opicity", 4));
                title_bottom.setText(context.getResources().getString(R.string.txt_opicity));
                break;
            case 5:
                txt_seekbar.setText("" + (SharedPreferenceClass.getInteger(context, "max_list", 0) + 3));
                seekbar.setProgress(SharedPreferenceClass.getInteger(context, "max_list", 0));
                title_bottom.setText(context.getResources().getString(R.string.txt_opicity));
                break;
        }

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("progress__", " :: " + progress);
                switch (from) {
                    case 1:
                        txt_seekbar.setText("" + (progress + 2));
                        SharedPreferenceClass.setInteger(context, "thresold", progress);
                        break;
                    case 2:
                        txt_seekbar.setText("" + (progress + 3));
                        SharedPreferenceClass.setInteger(context, "max_suggation", progress);
                        break;
                    case 3:
                        txt_seekbar.setText("" + (progress + 3));
                        SharedPreferenceClass.setInteger(context, "timeout", progress);
                        break;
                    case 4:
                        txt_seekbar.setText("" + (progress + 5));
                        SharedPreferenceClass.setInteger(context, "opicity", progress);
                        break;
                    case 5:
                        txt_seekbar.setText("" + (progress + 3));
                        SharedPreferenceClass.setInteger(context, "max_list", progress);
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((AppearanceActivity) context).changeVal();
    }
}
