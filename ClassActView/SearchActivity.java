package com.writingstar.autotypingandtextexpansion.ClassActView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.writingstar.autotypingandtextexpansion.BuildConfig;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Interface.OnRecyclerItemClickListener;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.SearchAdapter;
import com.writingstar.autotypingandtextexpansion.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;

public class SearchActivity extends AppCompatActivity {
    ImageView imgBack,imgMicro;
    EditText et_search;
    NestedScrollView no_search;
    TextView srch_hint_txt;
    RecyclerView search_recyl;
    ActionMode mActionMode;
    SearchAdapter searchAdapter;
    boolean isMultiSelect = false;
    ArrayList<TxpGetSet> multisearch_list = new ArrayList<>();
    ArrayList<TxpGetSet> phrase_list = new ArrayList<>();
    SQLiteHelper dbHelper;
    private final int REQ_CODE_SPEECH = 100;
    private final int PER_CODE_SPEECH = 200;
    private AlertDialog alertSimpleDialog;
    Intent na;
    private LinearLayout adLinLay;
    AdView adView;
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
        setContentView(R.layout.activity_search);
        et_search = findViewById(R.id.et_search);
        imgBack = findViewById(R.id.imgBack);
        imgMicro = findViewById(R.id.imgMicro);
        no_search = findViewById(R.id.no_search);
        search_recyl = findViewById(R.id.search_recyl);
        srch_hint_txt = findViewById(R.id.srch_hint_txt);

        dbHelper = new SQLiteHelper(this);
        dbHelper.open();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chekSpeechPermission()) {
                    startNormalSTT(Locale.ENGLISH);
                } else {
                    speechPermissiononly(PER_CODE_SPEECH);
                }
            }
        });
        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();
    }

    private void startNormalSTT(Locale language) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        startActivityForResult(intent, REQ_CODE_SPEECH);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode == REQ_CODE_SPEECH) {
                if(resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    et_search.setText(result.get(0));
                    et_search.setSelection(et_search.getText().toString().trim().length());
                }
            }
        }catch (Exception e){}
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multisearch_list.contains(phrase_list.get(position)))
                multisearch_list.remove(phrase_list.get(position));
            else
                multisearch_list.add(phrase_list.get(position));

            if (multisearch_list.size() > 0)
                mActionMode.setTitle("" + multisearch_list.size());
            else
                mActionMode.finish();

            refreshAdapter();
        }
    }

    public void refreshAdapter() {
        searchAdapter.selected_usersList = multisearch_list;
        searchAdapter.phraseList = phrase_list;
        searchAdapter.notifyDataSetChanged();
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
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SearchActivity.this, R.style.AlertDialogCustom);
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
            multisearch_list = new ArrayList<TxpGetSet>();
            refreshAdapter();
        }
    };

    private void deleteItems(int from) {
        if (from == 1) {
            if (multisearch_list.size() > 0) {
                for (int i = 0; i < multisearch_list.size(); i++) {
                    dbHelper.phraseDelete(multisearch_list.get(i).phrase_id);
                    phrase_list.remove(multisearch_list.get(i));
                }

                searchAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
                onResume();
               // Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (et_search.getText().toString().trim().length() > 1)
            checkValueSearch(et_search.getText().toString().trim());
        else{
            no_search.setVisibility(View.VISIBLE);
            srch_hint_txt.setText(R.string.no_search_string);
        }


        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (String.valueOf(s).length() > 1)
                    checkValueSearch(String.valueOf(s));
                else{
                    no_search.setVisibility(View.VISIBLE);
                    srch_hint_txt.setText(R.string.no_search_string);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    boolean chekSpeechPermission() {
        boolean allow = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            allow = false;
        }
        return allow;
    }

    void speechPermissiononly(int reqcode) {
        List<String> listPermissionsNeeded = new ArrayList<>();
        listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), reqcode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showSimpleDialog();
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == PER_CODE_SPEECH) {
                        startNormalSTT(Locale.ENGLISH);
                    }
                }
            }
    }

    public void showSimpleDialog() {
        hideKeyboard();
        try {
            if (alertSimpleDialog != null) {
                if (!alertSimpleDialog.isShowing())
                    alertSimpleDialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setMessage(getResources().getString(R.string.allow_for_smooth));
                builder.setPositiveButton(getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        alertSimpleDialog.dismiss();
                        alertSimpleDialog = null;
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                });
                if (alertDialog != null && !alertDialog.isShowing())
                    alertDialog.show();
            }
        } catch (Exception e) {
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void checkValueSearch(String text) {
        if (phrase_list != null)
            phrase_list.clear();

        phrase_list = dbHelper.getSearchItem(text);

        if (phrase_list.size() > 0)
            no_search.setVisibility(View.GONE);
        else{
            no_search.setVisibility(View.VISIBLE);
            srch_hint_txt.setText(R.string.no_result);
        }

        searchAdapter = new SearchAdapter(this, phrase_list, multisearch_list, new OnRecyclerItemClickListener() {
            @Override
            public void OnClick(int position, View view) {
                hideKeyboard();
                if (isMultiSelect)
                    multi_select(position);
                else {
                    Type type = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    Gson gson = new Gson();
                    ArrayList<String> finalOutputString = gson.fromJson(phrase_list.get(position).phrase_detail, type);
                    Log.d("call_txt__", " :: " + finalOutputString);
                    if (finalOutputString.size() > 1)
                        na = new Intent(SearchActivity.this, PhraseListActivity.class);
                    else
                        na = new Intent(SearchActivity.this, EditPharesActivity.class);

                    na.putExtra("id", phrase_list.get(position).phrase_id);
                    na.putExtra("title", phrase_list.get(position).phrase_title);
                    na.putExtra("desc", phrase_list.get(position).phrase_detail);
                    na.putExtra("note", phrase_list.get(position).phrase_note);
                    startActivity(na);
                }
            }

            @Override
            public void OnLongClick(int position, View view) {
                hideKeyboard();
                if (!isMultiSelect) {
                    multisearch_list = new ArrayList<TxpGetSet>();
                    isMultiSelect = true;
                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }
                multi_select(position);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        search_recyl.setLayoutManager(mLayoutManager);
        search_recyl.setItemAnimator(new DefaultItemAnimator());
        search_recyl.setAdapter(searchAdapter);
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
}