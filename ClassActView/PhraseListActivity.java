package com.writingstar.autotypingandtextexpansion.ClassActView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.RecyclerViewAdapter;
import com.writingstar.autotypingandtextexpansion.OtherClass.BottomSheetFragment;
import com.writingstar.autotypingandtextexpansion.R;
import com.writingstar.autotypingandtextexpansion.RecyclerClass.EditItemTouchHelperCallback;
import com.writingstar.autotypingandtextexpansion.RecyclerClass.ItemAdapter;
import com.writingstar.autotypingandtextexpansion.RecyclerClass.OnStartDragListener;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;

public class PhraseListActivity extends AppCompatActivity implements View.OnClickListener, OnStartDragListener {
    int id = -1, from = 0;// new phrase
    String title, desc, note;
    EditText txt_sortcut, txt_addnote, txt_phrase;
    ArrayList<TxpGetSet> phrase_list = new ArrayList<>();
    SQLiteHelper dbHelper;
    int back_space_undo = 0, disable_smart_case = 0, dont_append_space = 0, space_for_expansion = 0, expand_within_word = 0;
    private ArrayList<String> recItemNames = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    RelativeLayout recycler_main;
    ImageView imgBtnDelete, imgBtnSetting, imgBtnDone, imgClose, add_view;
    TextView tv_actionbar_title;
    private LinearLayout adLinLay;
    AdView adView;
    InterstitialAd mInterstitial;
    RecyclerView mRecyclerView;
    ItemTouchHelper mItemTouchHelper;
    ItemAdapter mAdapter;
    List<TxpGetSet> mPhraseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferenceClass.getBoolean(this, "isDark", false)) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.AppTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        setContentView(R.layout.activity_phrase_list);
        txt_sortcut = findViewById(R.id.txt_sortcut);
        txt_phrase = findViewById(R.id.txt_phrase);
        txt_addnote = findViewById(R.id.txt_addnote);
        imgBtnDelete = findViewById(R.id.imgBtnDelete);
        add_view = findViewById(R.id.add_view);
        imgBtnSetting = findViewById(R.id.imgBtnSetting);
        imgClose = findViewById(R.id.imgClose);
        imgBtnDone = findViewById(R.id.imgBtnDone);
        recycler_main = findViewById(R.id.recycler_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        dbHelper = new SQLiteHelper(PhraseListActivity.this);
        dbHelper.open();
        phrase_list = dbHelper.getAllphraseList();

        imgBtnDelete.setOnClickListener(this);
        imgBtnSetting.setOnClickListener(this);
        imgBtnDone.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        add_view.setOnClickListener(this);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (getIntent() != null && getIntent().hasExtra("id")) {
            tv_actionbar_title.setText(getResources().getString(R.string.edit_phrase));
            id = getIntent().getIntExtra("id", 9999);
            title = getIntent().getStringExtra("title");
            desc = getIntent().getStringExtra("desc");

            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<String> finalOutputString = gson.fromJson(desc, type);
            Log.d("call_txt__", " :: " + finalOutputString);
            for (int i = 0; i < finalOutputString.size(); i++) {
                mPhraseList.add(new TxpGetSet(finalOutputString.get(i)));
            }


            note = getIntent().getStringExtra("note");
            from = 1;
            txt_sortcut.setText(title);
            txt_addnote.setText(note);
            txt_sortcut.setSelection(title.length());
            for (int i = 0; i < phrase_list.size(); i++) {
                Log.d("chk_id", " :: " + phrase_list.get(i).phrase_id + " -- " + id);
                if (phrase_list.get(i).phrase_id == id) {
                    back_space_undo = phrase_list.get(i).backspace_undo;
                    disable_smart_case = phrase_list.get(i).smart_case;
                    dont_append_space = phrase_list.get(i).append_case;
                    space_for_expansion = phrase_list.get(i).space_for_expansion;
                    expand_within_word = phrase_list.get(i).within_words;
                }
            }
            mAdapter = new ItemAdapter(this, mPhraseList, this);
        } else {
            from = 0;
            tv_actionbar_title.setText(getResources().getString(R.string.add_phrase));
            imgBtnDelete.setVisibility(View.GONE);
            mAdapter = new ItemAdapter(this, mPhraseList, this);
        }


        Log.d("Sql___", "From :: " + from);

        recItemNames.add("Date");
        recItemNames.add("Time");
        recItemNames.add("Day");
        recItemNames.add("Month");
        recItemNames.add("Year");
        recItemNames.add("Hour");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new RecyclerViewAdapter(recItemNames, this, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        txt_phrase.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    recycler_main.setVisibility(View.VISIBLE);
                } else {
                    recycler_main.setVisibility(View.GONE);
                }
            }
        });
        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();


        ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        if (mPhraseList == null || mPhraseList.size() == 0)
            mRecyclerView.setVisibility(View.GONE);

    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setMessage(getResources().getString(R.string.sure_exit_save));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.phraseDelete(id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
        if (alertDialog != null && !alertDialog.isShowing())
            alertDialog.show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    public void showRecycler() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void hideRecycler() {
        mRecyclerView.setVisibility(View.GONE);
    }

    public void changeVal(String str, int b) {
        SharedPreferenceClass.setBoolean(PhraseListActivity.this, "change_toggle", true);
        Log.d("chk_sp_val_edtp_", "0 :: " + str + " :: " + b);

        switch (str) {
            case "back_space_undo":
                Log.d("chk_sp_val_edtp_", "1");
                back_space_undo = b;
                SharedPreferenceClass.setInteger(PhraseListActivity.this, "bsu", b);
                break;

            case "disable_smart_case":
                Log.d("chk_sp_val_edtp_", "2");
                disable_smart_case = b;
                SharedPreferenceClass.setInteger(PhraseListActivity.this, "dsc", b);
                break;

            case "dont_append_space":
                Log.d("chk_sp_val_edtp_", "3");
                dont_append_space = b;
                SharedPreferenceClass.setInteger(PhraseListActivity.this, "das", b);
                break;

            case "space_for_expansion":
                Log.d("chk_sp_val_edtp_", "4");
                space_for_expansion = b;
                SharedPreferenceClass.setInteger(PhraseListActivity.this, "sfe", b);
                break;

            case "expand_within_word":
                Log.d("chk_sp_val_edtp_", "5");
                expand_within_word = b;
                SharedPreferenceClass.setInteger(PhraseListActivity.this, "eww", b);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferenceClass.setBoolean(this, "change_toggle", false);
        SharedPreferenceClass.setInteger(this, "eww", 0);
        SharedPreferenceClass.setInteger(this, "sfe", 0);
        SharedPreferenceClass.setInteger(this, "das", 0);
        SharedPreferenceClass.setInteger(this, "bsu", 0);
        SharedPreferenceClass.setInteger(this, "dsc", 0);

        hideKeyboard();
        finish();
    }

    public void changePhrase(String insValue, int mainCase, int subCase) {
        txt_phrase.getText().insert(txt_phrase.getSelectionStart(), insValue);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnDone:
                savePhrase();
                break;

            case R.id.imgBtnDelete:
                hideKeyboard();
                deleteDialog();
                break;

            case R.id.imgBtnSetting:
                hideKeyboard();
                BottomSheetFragment fragment = new BottomSheetFragment(PhraseListActivity.this, from, id, false);
                fragment.show(getSupportFragmentManager(), "bottom_sheet");
                break;

            case R.id.imgClose:
                finish();
                break;

            case R.id.add_view:
                if (!txt_phrase.getText().toString().trim().isEmpty()) {
                    mAdapter.getPhraseList().add(new TxpGetSet(txt_phrase.getText().toString().trim()));
                    mAdapter.refreshAdapter(mAdapter.getPhraseList());
                    txt_phrase.setText("");
                }
                break;
        }
    }

    private void savePhrase() {
        hideKeyboard();
        if (txt_sortcut.getText().toString().trim().contains(" "))
            Snackbar.make(findViewById(R.id.main_layout), "" + getResources().getString(R.string.space_not_allowed), Snackbar.LENGTH_LONG).show();
        else if (txt_sortcut.getText().toString().trim().isEmpty())
            Snackbar.make(findViewById(R.id.main_layout), "" + getResources().getString(R.string.empty_category), Snackbar.LENGTH_LONG).show();
        else {
            String newCat = txt_sortcut.getText().toString().trim();
            String newPhr = txt_phrase.getText().toString().trim();
            boolean val = false;
            Log.d("tst_cat_tst", "sz :: --  " + phrase_list.size());

            for (int i = 0; i < phrase_list.size(); i++) {
                Log.d("tst_cat_tst", " --  " + phrase_list.get(i).getPhrase_title() + " -- " + newCat);
                if (phrase_list.get(i).getPhrase_title().equals(newCat)) {
                    val = true;
                    Log.d("tst_cat_tst", "newCat --  " + val);
                }

                if (id == phrase_list.get(i).phrase_id && phrase_list.get(i).getPhrase_title().equals(newCat) && !phrase_list.get(i).getPhrase_title().equals(newPhr)) {
                    val = false;
                    Log.d("tst_cat_tst", "id --  " + val);
                }

                Log.d("tog_val_sat_", " :: " + phrase_list.get(i).backspace_undo + " :: " + back_space_undo);
                Log.d("tog_val_sat_", " :: " + phrase_list.get(i).append_case + " :: " + dont_append_space);
                Log.d("tog_val_sat_", " :: " + phrase_list.get(i).smart_case + " :: " + disable_smart_case);
                Log.d("tog_val_sat_", " :: " + phrase_list.get(i).space_for_expansion + " :: " + space_for_expansion);
                Log.d("tog_val_sat_", " :: " + phrase_list.get(i).within_words + " :: " + expand_within_word);

                if ((phrase_list.get(i).backspace_undo != back_space_undo ||
                        phrase_list.get(i).append_case != dont_append_space ||
                        phrase_list.get(i).smart_case != disable_smart_case) ||
                        phrase_list.get(i).space_for_expansion != space_for_expansion ||
                        phrase_list.get(i).within_words != expand_within_word) {
                    val = false;
                    Log.d("tst_cat_tst", "tog_val_sat_ --  " + val);
                }
            }

            if (val) {
                Snackbar.make(findViewById(R.id.main_layout), "" + getResources().getString(R.string.exist_category), Snackbar.LENGTH_SHORT).show();
            } else {

                if (mAdapter.getPhraseList().size() < 1) {
                    Snackbar.make(findViewById(R.id.main_layout), "" + getResources().getString(R.string.empty_category), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> inputArray = new ArrayList<>();
                for (int i = 0; i < mAdapter.getPhraseList().size(); i++) {
                    Log.d("chk_val_", " :: " + mAdapter.getPhraseList().get(i).phrase_detail);
                    if (mAdapter.getPhraseList().get(i).phrase_detail.trim().isEmpty()) {
                        Snackbar.make(findViewById(R.id.main_layout), "" + getResources().getString(R.string.empty_category), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (inputArray.contains(mAdapter.getPhraseList().get(i).phrase_detail.toString())) {
                        Snackbar.make(findViewById(R.id.main_layout), "" + getResources().getString(R.string.exist_phrasel), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    inputArray.add(mAdapter.getPhraseList().get(i).phrase_detail.toString());
                }

                Gson gson = new Gson();
                String inputString = gson.toJson(inputArray);

                String useDate = new SimpleDateFormat("d MMM, HH:mm a", Locale.getDefault()).format(new Date());
                Log.d("dt_view_", "- : " + useDate);
                if (from == 0)
                    dbHelper.phraseInsert(txt_sortcut.getText().toString().trim(), inputString, "" + useDate, txt_addnote.getText().toString().trim(), back_space_undo, disable_smart_case, dont_append_space, space_for_expansion, expand_within_word);
                else
                    dbHelper.phraseUpdate(id, txt_sortcut.getText().toString().trim(), inputString, "" + useDate, txt_addnote.getText().toString().trim(), back_space_undo, disable_smart_case, dont_append_space, space_for_expansion, expand_within_word);

                SharedPreferenceClass.setBoolean(this, "change_toggle", false);
                Toast.makeText(PhraseListActivity.this,"Phraselist added successfully",Toast.LENGTH_SHORT).show();
                if (HelperClass.check_internet(this) && SharedPreferenceClass.getBoolean(this, HelperClass.IS_FULLPRO, IS_TRUE))
                    inrequestadd();
                else
                    finish();
            }
        }
    }

    private void loadDataAds() {
        if (HelperClass.check_internet(this) && SharedPreferenceClass.getBoolean(this, HelperClass.IS_FULLPRO, IS_TRUE)) {
            adView = new AdView(this);
            adView.setAdUnitId(getResources().getString(R.string.banner_home_footer));
            adLinLay.setVisibility(View.VISIBLE);
            adLinLay.removeAllViews();
            adLinLay.addView(adView);
            loadBanner();
        } else {
            adLinLay.setVisibility(View.GONE);
        }
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    public void inrequestadd() {
        HelperClass.showProgressDialog(PhraseListActivity.this, getResources().getString(R.string.txt_load_database));
        mInterstitial = new InterstitialAd(PhraseListActivity.this);
        mInterstitial.setAdUnitId(getResources().getString(R.string.interstitial_full_screen_ph_back));
        mInterstitial.loadAd(new AdRequest.Builder().build());
        mInterstitial.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                HelperClass.dismissProgressDialog();
                finish();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                HelperClass.dismissProgressDialog();
                finish();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitial.isLoaded()) {
                    HelperClass.dismissProgressDialog();
                    mInterstitial.show();
                }
            }

        });
    }
}