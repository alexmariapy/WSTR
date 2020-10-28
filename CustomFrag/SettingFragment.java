package com.writingstar.autotypingandtextexpansion.CustomFrag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.writingstar.autotypingandtextexpansion.ClassActView.AppearanceActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.InternalBrowserActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.LocalBackupActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.MainActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.MoreAppActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.PremiumScreenActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.SendErrorActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.TextExpansionSetting;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.R;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.check_internet;

public class SettingFragment extends Fragment implements View.OnClickListener {
    public static Context context;
    RelativeLayout lay_vip, lay_night_mode, lay_backup_restore, lay_text_expansion, lay_text_appearance, lay_more_apps, lay_send_error, lay_rate, lay_share, lay_privacy;
    SwitchCompat toggle_nightmode;

    public static SettingFragment newInstance(Context c) {
        SettingFragment settingFragment = new SettingFragment();
        context = c;
        return settingFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lay_night_mode = view.findViewById(R.id.lay_night_mode);
        lay_text_expansion = view.findViewById(R.id.lay_text_expansion);
        lay_text_appearance = view.findViewById(R.id.lay_text_appearance);
        lay_more_apps = view.findViewById(R.id.lay_more_apps);
        lay_send_error = view.findViewById(R.id.lay_send_error);
        lay_rate = view.findViewById(R.id.lay_rate);
        lay_share = view.findViewById(R.id.lay_share);
        lay_privacy = view.findViewById(R.id.lay_privacy);
        lay_backup_restore = view.findViewById(R.id.lay_backup_restore);
        lay_vip = view.findViewById(R.id.lay_vip);
        toggle_nightmode = view.findViewById(R.id.toggle_nightmode);

        lay_night_mode.setOnClickListener(this);
        lay_text_expansion.setOnClickListener(this);
        lay_text_appearance.setOnClickListener(this);
        lay_more_apps.setOnClickListener(this);
        lay_send_error.setOnClickListener(this);
        lay_rate.setOnClickListener(this);
        lay_share.setOnClickListener(this);
        lay_privacy.setOnClickListener(this);
        lay_backup_restore.setOnClickListener(this);

        if (!SharedPreferenceClass.getBoolean(context, HelperClass.IS_FULLPRO, IS_TRUE))
            lay_vip.setVisibility(View.GONE);

        if (SharedPreferenceClass.getBoolean(context, "isDark", false)) {
            toggle_nightmode.setChecked(true);
        } else {
            toggle_nightmode.setChecked(false);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_night_mode:
                if (toggle_nightmode.isChecked()) {
                    toggle_nightmode.setChecked(false);
                    SharedPreferenceClass.setBoolean(context, "isDark", false);
                    ((MainActivity) context).recreateAct();
                } else {
                    toggle_nightmode.setChecked(true);
                    SharedPreferenceClass.setBoolean(context, "isDark", true);
                    ((MainActivity) context).recreateAct();
                }
                break;
            case R.id.lay_backup_restore:
                if (SharedPreferenceClass.getBoolean(context, HelperClass.IS_FULLPRO, IS_TRUE)) {
                    Intent br = new Intent(context, PremiumScreenActivity.class);
                    context.startActivity(br);
                } else {
                    Intent br = new Intent(context, LocalBackupActivity.class);
                    context.startActivity(br);
                }
                break;
            case R.id.lay_text_expansion:
                Intent Nxt = new Intent(context, TextExpansionSetting.class);
                context.startActivity(Nxt);
                break;
            case R.id.lay_text_appearance:
                Intent app = new Intent(context, AppearanceActivity.class);
                context.startActivity(app);
                break;
            case R.id.lay_more_apps:
                if (check_internet(context)) {
                    Intent ma = new Intent(context, MoreAppActivity.class);
                    context.startActivity(ma);
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), context.getResources().getString(R.string.offline_message), Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.lay_send_error:
                Intent se = new Intent(context, SendErrorActivity.class);
                context.startActivity(se);
                break;
            case R.id.lay_rate:
                HelperClass.rate(context);
                break;
            case R.id.lay_share:
                HelperClass.shareApp(context);
                break;
            case R.id.lay_privacy:
                if (check_internet(context)) {
                    Intent pp = new Intent(context, InternalBrowserActivity.class);
                    context.startActivity(pp);
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), context.getResources().getString(R.string.offline_message), Snackbar.LENGTH_SHORT).show();
                }

                break;

        }
    }


}