package com.writingstar.autotypingandtextexpansion.Libservice;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.writingstar.autotypingandtextexpansion.BuildConfig;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Interface.OnRecyclerItemClickListenerValueChange;
import com.writingstar.autotypingandtextexpansion.Model.LoadAppModel;
import com.writingstar.autotypingandtextexpansion.Model.PhraseListGetSet;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.PopupListAdapter;
import com.writingstar.autotypingandtextexpansion.PopupPhraseListAdapter;
import com.writingstar.autotypingandtextexpansion.R;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccessService extends AccessibilityService implements OnPrimaryClipChangedListener, OnLoadCompleteListener<Cursor>, Callback, View.OnTouchListener {
    SQLiteHelper dbHelper;
    ArrayList<TxpGetSet> phrase_list = new ArrayList<>();
    public List<LoadAppModel> appList = new ArrayList();
    String phares, strBefore = "", strBackmatch = "";
    boolean global_bsu, global_sc, global_as, global_sp;
    AccessibilityNodeInfo source;
    Bundle arguments;
    String pattern = "", target = "";
    boolean isMore = false, isEnter = false, isChange = false, isBWList = true;
    int oldLength = 0, newLength = 0;
    AccessibilityEvent acv;
    ImageView cancel;
    WindowManager windowManager;
    FrameLayout mLayout;
    RelativeLayout rel, parent_layout_recpopup;
    Handler h = new Handler();

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        acv = accessibilityEvent;
        switch (accessibilityEvent.getEventType()) {

            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                String str = accessibilityEvent.getText().toString().replace("[", "").replace("]", "");
                String str2 = accessibilityEvent.getText().toString().replace("[", "").replace("]", "");
                dbHelper = new SQLiteHelper(getApplicationContext());
                dbHelper.open();
                appList = dbHelper.getAppList();
                isBWList = true;
                if (appList != null && appList.size() > 0) {
                    for (int i = 0; i < appList.size(); i++) {
                        if (SharedPreferenceClass.getBoolean(this, "isBlackList", true)) {
                            if (appList.get(i).app_package.equals(accessibilityEvent.getPackageName().toString())) {
                                isBWList = false;
                            } else {
                                isBWList = true;
                            }
                        } else {
                            if (appList.get(i).app_package.equals(accessibilityEvent.getPackageName().toString())) {
                                isBWList = true;
                            } else {
                                isBWList = false;
                            }
                        }
                    }
                }

                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                Gson gson = new Gson();

                if (!accessibilityEvent.getPackageName().toString().equals(BuildConfig.APPLICATION_ID) && str.length() > 0 && isBWList) {
                    global_bsu = SharedPreferenceClass.getBoolean(getApplicationContext(), "global_bsu", true);
                    global_sp = SharedPreferenceClass.getBoolean(getApplicationContext(), "global_sp", true);
                    global_as = SharedPreferenceClass.getBoolean(getApplicationContext(), "global_as", true);
                    global_sc = SharedPreferenceClass.getBoolean(getApplicationContext(), "global_sc", false);

                    if (phrase_list != null)
                        phrase_list.clear();

                    phrase_list = dbHelper.getAllphraseList();
                    newLength = str.length();
                    //Log.e("TXT__", "::  size : " + phrase_list.size());
                    Log.d("TXT__str9", "::  size : " + str);
                    isMore = false;
                    isEnter = false;
                    String[] tststr = str.split(" ");
                    if (tststr.length > 1) {
                        Log.d("txt_call_ie", "::: if");
                        strBefore = "";
                        StringBuilder strBuilder = new StringBuilder(strBefore);
                        String[] newStr = str.split(" ");
                        for (int i = 0; i < newStr.length; i++) {
                            strBefore = newStr[i];
                            if (i != newStr.length - 1) {
                                if (i != 0) {
                                    strBefore = " " + strBefore;
                                }

                                strBuilder.append(strBefore);
                            }
                        }
                        strBefore = strBuilder.toString();
                        Log.d("TXT__str", "::: " + strBefore);

                        if (Character.isWhitespace(str.charAt(str.length() - 1)))
                            str = newStr[newStr.length - 1] + " ";
                        else
                            str = newStr[newStr.length - 1];

                        isMore = true;
                    } else {
                        Log.d("txt_call_ie", "::: else");
                    }

                    String[] entrstr = str2.split("\n");
                    Log.d("txt_call_newStr2", "::: entrstr:: " + entrstr.length + " :: st2 : " + str2);
                    if (entrstr.length > 1) {
                        Log.d("txt_call_ie", "::: if");
                        strBefore = "";
                        StringBuilder strBuilder = new StringBuilder(strBefore);
                        String[] newStr = str2.split("\n");
                        Log.d("txt_call_newStr", "::: newStr " + newStr.length);

                        for (int i = 0; i < newStr.length; i++) {
                            strBefore = newStr[i];
                            if (i != newStr.length - 1) {
                                if (i != 0)
                                    strBefore = "\n" + strBefore;

                                strBuilder.append(strBefore);
                            }
                        }
                        strBefore = strBuilder.toString() + "\n";
                        Log.d("TXT__str_br1", "::: " + strBefore);
                        isEnter = true;
                        str = newStr[newStr.length - 1];
                        Log.d("TXT__str_lng_13", "::: " + str);

                        String[] tststr2 = str.split(" ");
                        if (tststr2.length > 1) {
                            if (Character.isWhitespace(tststr2.length - 1))
                                str = tststr2[tststr2.length - 1] + " ";
                            else
                                str = tststr2[tststr2.length - 1];

                            String stt = "";
                            StringBuilder sBuilder = new StringBuilder(stt);
                            for (int k = 0; k < tststr2.length - 1; k++) {
                                stt = tststr2[k] + " ";
                                Log.d("TXT__str_br2", "::: " + tststr2[k]);
                                sBuilder.append(stt);
                            }

                            strBefore = strBefore + sBuilder.toString();
                            Log.d("TXT__str_br3", "::: " + strBefore);
                        }

                        Log.d("TXT__str_lng_2", "::: " + tststr2.length + " :: " + str);
                        isMore = true;
                    }
                    Log.d("TXT__str99", "::: " + strBefore);
                    for (int i = 0; i < phrase_list.size(); i++) {
                        Log.e("TXT__list_", "::  " + str + " :: size : " + phrase_list.get(i).getPhrase_title());
                    }

                    source = accessibilityEvent.getSource();
                    arguments = new Bundle();
                    backSpaceUndo();
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayListdummy = new ArrayList();

                    if (arrayListdummy != null) {
                        arrayListdummy.clear();
                    }
                    if (arrayList != null) {
                        arrayList.clear();
                    }
                    for (int i = 0; i < phrase_list.size(); i++) {
                        String phraseTitle = phrase_list.get(i).getPhrase_title();
                        if (phraseTitle.toLowerCase().contains(str.toLowerCase()) && source != null && str.length() >= 2) {
                            PhraseListGetSet phraseListGetSet = new PhraseListGetSet();
                            phraseListGetSet.phrase_id = phrase_list.get(i).phrase_id;
                            arrayListdummy.add(phraseListGetSet);
                        }
                    }

                    int Scnt = 0;
                    for (int i = 0; i < phrase_list.size(); i++) {
                        ArrayList<String> finalOutputString = gson.fromJson(phrase_list.get(i).phrase_detail, type);
                        if (finalOutputString.size() == 1) {
                            Scnt = Scnt + 1;
                            Log.d("chk_ip_Scnt", " Scnt : " + Scnt);
                        }
                    }
                    for (int i = 0; i < phrase_list.size(); i++) {
                        Log.d("chk_str_", " :: call title :: " + phrase_list.get(i).getPhrase_title());
                        String phraseTitle = phrase_list.get(i).getPhrase_title();
                        String lstStr = str.substring(str.length() - 1);
                        Log.d("chk_spc_", ":" + lstStr + ":     --  " + lstStr.length());

                        ArrayList<String> finalOutputString = gson.fromJson(phrase_list.get(i).phrase_detail, type);

                        int thresold = SharedPreferenceClass.getInteger(getApplicationContext(), "thresold", 0);

                        if (phraseTitle.toLowerCase().equals(str.toLowerCase())) {
                            if (finalOutputString.size() > 1) {
                                Log.d("chk_ip_str", " call : lst");

                                if (phraseTitle.toLowerCase().equals(str.toLowerCase()) && source != null && str.length() >= (2 + thresold)) {
                                    PhraseListGetSet phraseListGetSet = new PhraseListGetSet();
                                    phraseListGetSet.phrase_id = phrase_list.get(i).phrase_id;
                                    phraseListGetSet.phrase_title = phrase_list.get(i).phrase_title;
                                    phraseListGetSet.phrase_detail = finalOutputString.get(0);
                                    arrayList.add(phraseListGetSet);
                                    Log.d("arr_sz_ss", " :: " + i + " :: " + (phrase_list.size() - 1));
                                }

                                if (arrayList != null && arrayList.size() > 0) {
                                    if (SharedPreferenceClass.getBoolean(getApplicationContext(), "tog_sort_sugg", true)) {
                                        Log.d("chk_spc_as_ifc", ": asdum:: " + arrayListdummy.size() + " i ::" + i + " Scnt:: " + (Scnt - 1) + " strL:: " + str.length() + " thrs:: " + (2 + thresold));
                                        Log.d("chk_spc_as_ifc", ": phrsTT:: " + phrase_list.get(i).getPhrase_title().toLowerCase() + " strLc ::" + str.toLowerCase());

                                        if (arrayListdummy.size() == 1 && phrase_list != null && phrase_list.get(i).getPhrase_title().toLowerCase().equals(str.toLowerCase()) && source != null && str.length() >= (2 + thresold)) {
                                            ArrayList<String> listArr = gson.fromJson(phrase_list.get(i).phrase_detail, type);
                                            showListPopupMenu(str, i, listArr);
                                        } else {
                                            Log.d("sp_call_pop", " call : else");
                                        }
                                        Log.d("chk_spc_as_dm", ": asdum:: " + arrayListdummy.size() + ": as:: " + arrayList.size() + " pt:: " + phraseTitle.toLowerCase() + " str:: " + str.toLowerCase());
                                    }
                                } else {
                                    Log.d("call_view_", "else__");
                                    removeWView();
                                }

                            } else {
                                Log.d("chk_ip_str", " call : else");

                                if (phraseTitle.toLowerCase().contains(str.toLowerCase()) && source != null && str.length() >= (2 + thresold)) {
                                    PhraseListGetSet phraseListGetSet = new PhraseListGetSet();
                                    phraseListGetSet.phrase_id = phrase_list.get(i).phrase_id;
                                    phraseListGetSet.phrase_title = phrase_list.get(i).phrase_title;
                                    phraseListGetSet.phrase_detail = finalOutputString.get(0);
                                    arrayList.add(phraseListGetSet);
                                    Log.d("arr_sz_ss", " :: " + i + " :: " + (phrase_list.size() - 1));
                                }

                                if (global_sp && !(lstStr.length() <= 0) && phrase_list.get(i).space_for_expansion == 0) {
                                    if (phrase_list.get(i).within_words == 1) {
                                        expandWithinWords(str, phraseTitle, i);
                                        return;
                                    }
                                    phraseTitle = phraseTitle + lstStr;
                                    Log.d("chk_spc_", " :: call ::" + phraseTitle + ": -- :" + str.trim() + ":");
                                    if (phraseTitle.equalsIgnoreCase(str) && source != null) {
                                        switch (lstStr) {
                                            case ",":
                                                phares = finalOutputString.get(0) + ",";
                                                changeAuto(str, i);
                                                break;
                                            case ".":
                                                phares = finalOutputString.get(0) + ".";
                                                changeAuto(str, i);
                                                break;
                                            case ":":
                                                phares = finalOutputString.get(0) + ":";
                                                changeAuto(str, i);
                                                break;
                                            case ";":
                                                phares = finalOutputString.get(0) + ";";
                                                changeAuto(str, i);
                                                break;
                                            case "?":
                                                phares = finalOutputString.get(0) + "?";
                                                changeAuto(str, i);
                                                break;
                                            case " ":
                                                phares = finalOutputString.get(0) + "";
                                                changeAuto(str, i);
                                                break;
                                        }
                                    }
                                } else if (!(lstStr.length() <= 0) && phrase_list.get(i).within_words == 1) {
                                    expandWithinWords(str, phraseTitle, i);
                                } else {
                                    Log.d("chk_str_", " :: main : " + phraseTitle + " -- " + str);
                                    if (phraseTitle.equalsIgnoreCase(str) && source != null) {
                                        Log.d("chk_str_", " :: call ");
                                        phares = finalOutputString.get(0);
                                        changeAuto(str, i);
                                    }
                                }

                                if (arrayList != null && arrayList.size() > 0) {
                                    if (SharedPreferenceClass.getBoolean(getApplicationContext(), "tog_sort_sugg", true)) {
                                        Log.d("chk_spc_as_ifc_s", ": asdum:: " + arrayListdummy.size() + " i ::" + i + " Scnt:: " + (Scnt - 1) + " strL:: " + str.length() + " thrs:: " + (2 + thresold));
                                        Log.d("chk_spc_as_ifc_s", ": phrsTT:: " + phrase_list.get(i).getPhrase_title().toLowerCase() + " strLc ::" + str.toLowerCase());

                                        if (arrayListdummy.size() > 1 && phrase_list != null && i == Scnt - 1 && str.length() >= (2 + thresold)) {
                                            showCustomPopupMenu(str, i, arrayList);
                                            Log.d("arr_sz_sssm", " :: call if");
                                        } else if (arrayListdummy.size() == 1 && phrase_list != null && phrase_list.get(i).getPhrase_title().toLowerCase().contains(str.toLowerCase()) && source != null && str.length() >= (2 + thresold)) {
                                            phares = finalOutputString.get(0);
                                            showCustomPopupMenu(str, i, arrayList);
                                            Log.d("arr_sz_sssm", " :: call elif");
                                        } else {
                                            Log.d("arr_sz_sssm", " :: call els");
                                        }
                                        Log.d("chk_spc_as_dm", ": asdum:: " + arrayListdummy.size() + ": as:: " + arrayList.size() + " pt:: " + phraseTitle.toLowerCase() + " str:: " + str.toLowerCase());
                                    }
                                } else {
                                    Log.d("call_view_", "else__");
                                    removeWView();
                                }
                            }
                        } else {
                            Log.d("chk_ip_str", " call : arr " + finalOutputString);
                            if (finalOutputString.size() == 1) {
                                Log.d("chk_ip_str", " call : sng");

                                if (phraseTitle.toLowerCase().contains(str.toLowerCase()) && source != null && str.length() >= (2 + thresold)) {
                                    PhraseListGetSet phraseListGetSet = new PhraseListGetSet();
                                    phraseListGetSet.phrase_id = phrase_list.get(i).phrase_id;
                                    phraseListGetSet.phrase_title = phrase_list.get(i).phrase_title;
                                    phraseListGetSet.phrase_detail = finalOutputString.get(0);
                                    arrayList.add(phraseListGetSet);
                                    Log.d("arr_sz_ss", " :: " + i + " :: " + (phrase_list.size() - 1));
                                }

                                if (global_sp && !(lstStr.length() <= 0) && phrase_list.get(i).space_for_expansion == 0) {
                                    if (phrase_list.get(i).within_words == 1) {
                                        expandWithinWords(str, phraseTitle, i);
                                        return;
                                    }
                                    phraseTitle = phraseTitle + lstStr;
                                    Log.d("chk_spc_", " :: call ::" + phraseTitle + ": -- :" + str.trim() + ":");
                                    if (phraseTitle.equalsIgnoreCase(str) && source != null) {
                                        switch (lstStr) {
                                            case ",":
                                                phares = finalOutputString.get(0) + ",";
                                                changeAuto(str, i);
                                                break;
                                            case ".":
                                                phares = finalOutputString.get(0) + ".";
                                                changeAuto(str, i);
                                                break;
                                            case ":":
                                                phares = finalOutputString.get(0) + ":";
                                                changeAuto(str, i);
                                                break;
                                            case ";":
                                                phares = finalOutputString.get(0) + ";";
                                                changeAuto(str, i);
                                                break;
                                            case "?":
                                                phares = finalOutputString.get(0) + "?";
                                                changeAuto(str, i);
                                                break;
                                            case " ":
                                                phares = finalOutputString.get(0) + "";
                                                changeAuto(str, i);
                                                break;
                                        }
                                    }
                                } else if (!(lstStr.length() <= 0) && phrase_list.get(i).within_words == 1) {
                                    expandWithinWords(str, phraseTitle, i);
                                } else {
                                    Log.d("chk_str_", " :: main : " + phraseTitle + " -- " + str);
                                    if (phraseTitle.equalsIgnoreCase(str) && source != null) {
                                        Log.d("chk_str_", " :: call ");
                                        phares = finalOutputString.get(0);
                                        changeAuto(str, i);
                                    }
                                }

                                if (arrayList != null && arrayList.size() > 0) {
                                    if (SharedPreferenceClass.getBoolean(getApplicationContext(), "tog_sort_sugg", true)) {
                                        Log.d("chk_spc_as_ifc", ": asdum:: " + arrayListdummy.size() + " i ::" + i + " Scnt:: " + (Scnt - 1) + " strL:: " + str.length() + " thrs:: " + (2 + thresold));
                                        Log.d("chk_spc_as_ifc", ": phrsTT:: " + phrase_list.get(i).getPhrase_title().toLowerCase() + " strLc ::" + str.toLowerCase());

                                        if (arrayListdummy.size() > 1 && phrase_list != null && i == Scnt - 1 && str.length() >= (2 + thresold)) {
                                            showCustomPopupMenu(str, i, arrayList);
                                            Log.d("arr_sz_sssm", " :: call if");
                                        } else if (arrayListdummy.size() == 1 && phrase_list != null && phrase_list.get(i).getPhrase_title().toLowerCase().contains(str.toLowerCase()) && source != null && str.length() >= (2 + thresold)) {
                                            phares = finalOutputString.get(0);
                                            showCustomPopupMenu(str, i, arrayList);
                                            Log.d("arr_sz_sssm", " :: call elif");
                                        } else {
                                            Log.d("arr_sz_sssm", " :: call els");
                                        }
                                        Log.d("chk_spc_as_dm", ": asdum:: " + arrayListdummy.size() + ": as:: " + arrayList.size() + " pt:: " + phraseTitle.toLowerCase() + " str:: " + str.toLowerCase());
                                    }
                                } else {
                                    Log.d("call_view_", "else__");
                                    removeWView();
                                }
                            }
                        }
                    }
                    break;
                }
        }
    }

    private void expandWithinWords(String str, String phraseTitle, int i) {
        if (str.toLowerCase().contains(phraseTitle.toLowerCase()) && source != null) {
            Log.d("within_words_", " :: call");
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<String> finalOutputString = gson.fromJson(phrase_list.get(i).phrase_detail, type);
            phares = str.toLowerCase().replace(phrase_list.get(i).phrase_title.toLowerCase(), finalOutputString.get(0));
            changeAuto(str, i);
        }
    }

    private void changeAuto(String str, int pos) {
        removeWView();
        setValueOfChange(str, pos);
        String lngth;
        Log.d("CHK_BCK_str", ":: " + str);
        Log.d("CHK_BCK_str_val", ":: " + strBefore + " " + phares);
        strBackmatch = "";
        if (isMore) {
            if (isEnter) {
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, strBefore + phares);
                lngth = strBefore + phares;
                SharedPreferenceClass.setString(getApplicationContext(), "bckString", strBefore + str + "");
                strBackmatch = strBefore + phares;
            } else {
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, strBefore + " " + phares);
                lngth = strBefore + " " + phares;
                SharedPreferenceClass.setString(getApplicationContext(), "bckString", strBefore + " " + str + "");
                strBackmatch = strBefore + " " + phares;
            }
            isMore = false;
            isEnter = false;
        } else {
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, phares);
            lngth = phares;
            SharedPreferenceClass.setString(getApplicationContext(), "bckString", str + "");
            strBackmatch = phares;
        }
        source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        dbHelper.lastUseCount(phrase_list.get(pos).phrase_id, phrase_list.get(pos).phrase_usage_count + 1);
        dbHelper.lastUseTime(phrase_list.get(pos).phrase_id, "" + System.currentTimeMillis());
        isChange = true;
        oldLength = lngth.length();
        dbHelper.close();
    }

    private void setValueOfChange(String str, int pos) {
        while (phares.contains(getApplicationContext().getResources().getString(R.string.format_dd_mm_yyyy)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_mm_dd_yyyy)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_dd_mm_yy)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_mm_dd_yy)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_hh_mm)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_hh_mm_ss)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_dayn)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_days)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_dayf)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_monthn)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_months)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_monthf)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_years)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_yearf)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_hrs12)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_hrs24)) ||
                phares.contains(getApplicationContext().getResources().getString(R.string.format_am_pm))) {

            if (phares.contains(getApplicationContext().getResources().getString(R.string.format_dd_mm_yyyy))) {
                pattern = "dd/MM/yyyy";
                target = getApplicationContext().getResources().getString(R.string.format_dd_mm_yyyy);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_mm_dd_yyyy))) {
                pattern = "MM/dd/yyyy";
                target = getApplicationContext().getResources().getString(R.string.format_mm_dd_yyyy);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_dd_mm_yy))) {
                pattern = "dd/MM/yy";
                target = getApplicationContext().getResources().getString(R.string.format_dd_mm_yy);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_dd_mm_yyyy))) {
                pattern = "dd/MM/yyyy";
                target = getApplicationContext().getResources().getString(R.string.format_dd_mm_yyyy);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_mm_dd_yy))) {
                pattern = "MM/dd/yy";
                target = getApplicationContext().getResources().getString(R.string.format_mm_dd_yy);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_hh_mm))) {
                pattern = "hh:mm";
                target = getApplicationContext().getResources().getString(R.string.format_hh_mm);
            } else if (phares.contains(
                    getApplicationContext().getResources().getString(R.string.format_hh_mm_ss))) {
                pattern = "hh:mm:ss";
                target = getApplicationContext().getResources().getString(R.string.format_hh_mm_ss);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_dayn))) {
                pattern = "dd";
                target = getApplicationContext().getResources().getString(R.string.format_dayn);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_days))) {
                pattern = "E";
                target = getApplicationContext().getResources().getString(R.string.format_days);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_dayf))) {
                pattern = "EEEE";
                target = getApplicationContext().getResources().getString(R.string.format_dayf);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_monthn))) {
                pattern = "MM";
                target = getApplicationContext().getResources().getString(R.string.format_monthn);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_months))) {
                pattern = "MMM";
                target = getApplicationContext().getResources().getString(R.string.format_months);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_monthf))) {
                pattern = "MMMM";
                target = getApplicationContext().getResources().getString(R.string.format_monthf);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_years))) {
                pattern = "yy";
                target = getApplicationContext().getResources().getString(R.string.format_years);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_yearf))) {
                pattern = "yyyy";
                target = getApplicationContext().getResources().getString(R.string.format_yearf);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_hrs12))) {
                pattern = "hh";
                target = getApplicationContext().getResources().getString(R.string.format_hrs12);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_hrs24))) {
                pattern = "HH";
                target = getApplicationContext().getResources().getString(R.string.format_hrs24);
            } else if (phares.contains(getApplicationContext().getResources().getString(R.string.format_am_pm))) {
                pattern = "a";
                target = getApplicationContext().getResources().getString(R.string.format_am_pm);
            }
            String currentDate = new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
            phares = phares.replace(target, currentDate);
        }

        if (global_sc && phrase_list.get(pos).smart_case == 0) {
            Log.d("chk_str_", " :: call 0 ");
            if (phares.contains(" ")) {
                Log.d("chk_str_", " :: call 1 ");
                String strng = "";
                String[] parts = phares.split(" ");
                Log.d("chk_str_", " :: call lngth " + parts.length);
                StringBuilder strBuilder = new StringBuilder(strng);
                for (int i = 0; i < parts.length; i++) {
                    String cutStr = parts[i];
                    if (str.substring(0, 1).toUpperCase().equals(str.substring(0, 1)) && str.substring(1).toLowerCase().equals(str.substring(1)))
                        strng = cutStr.substring(0, 1).toUpperCase() + cutStr.substring(1).toLowerCase();
                    else if (str.substring(0, 1).toUpperCase().equals(str.substring(0, 1)) && str.substring(1).toUpperCase().equals(str.substring(1)))
                        strng = cutStr.substring(0, 1).toUpperCase() + cutStr.substring(1).toUpperCase();
                    else
                        strng = cutStr.substring(0, 1).toLowerCase() + cutStr.substring(1).toLowerCase();

                    if (i != 0)
                        strng = " " + strng;

                    strBuilder.append(strng);
                }
                Log.d("chk_str_", " :: " + strBuilder.toString());
                phares = strBuilder.toString();
            } else {
                if (str.substring(0, 1).toUpperCase().equals(str.substring(0, 1)) && str.substring(1).toLowerCase().equals(str.substring(1)))
                    phares = phares.substring(0, 1).toUpperCase() + phares.substring(1).toLowerCase();
                else if (str.substring(0, 1).toUpperCase().equals(str.substring(0, 1)) && str.substring(1).toUpperCase().equals(str.substring(1)))
                    phares = phares.substring(0, 1).toUpperCase() + phares.substring(1).toUpperCase();
                else
                    phares = phares.substring(0, 1).toLowerCase() + phares.substring(1).toLowerCase();
            }
        }
        if (global_as && phrase_list.get(pos).append_case == 0) {
            phares = phares + " ";
        }
    }

    private void backSpaceUndo() {
        Log.d("CHK_BCK_", ":: " + isChange + " ol :: " + oldLength + " nl :: " + newLength);
        if (isChange) {
            if (newLength == (oldLength - 1)) {
                Log.d("CHK_BCK_", "bck__");
                for (int i = 0; i < phrase_list.size(); i++) {
                    Log.d("CHK_BCK_phr", "bck__ :: " + strBackmatch + " -- " + phrase_list.get(i).phrase_detail);

                    boolean isDT = false;
                    if (phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_dd_mm_yyyy)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_mm_dd_yyyy)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_dd_mm_yy)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_mm_dd_yy)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_hh_mm)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_hh_mm_ss)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_dayn)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_days)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_dayf)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_monthn)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_months)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_monthf)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_years)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_yearf)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_hrs12)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_hrs24)) ||
                            phrase_list.get(i).phrase_detail.contains(getApplicationContext().getResources().getString(R.string.format_am_pm))) {
                        isDT = true;
                    }

                    String bsStr = phrase_list.get(i).phrase_detail.toLowerCase();
                    Type type = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    Gson gson = new Gson();
                    ArrayList<String> finalOutputString = gson.fromJson(bsStr, type);
                    if (finalOutputString.get(0) != null) {
                        for (int k = 0; k < finalOutputString.size(); k++) {
                            if ((strBackmatch.toLowerCase().contains(finalOutputString.get(k)))) {
                                bsStr = finalOutputString.get(k);
                            }
                        }
                    }
                    if (phrase_list.get(i).backspace_undo == 0 && (strBackmatch.toLowerCase().contains(bsStr) || isDT)) {
                        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, SharedPreferenceClass.getString(getApplicationContext(), "bckString", ""));
                        source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    }
                }
            }
            isChange = false;
        }
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onPrimaryClipChanged() {
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void showCustomPopupMenu(String str, int i, ArrayList arrayList) {
        windowManager = null;
        windowManager = (WindowManager) getBaseContext().getSystemService(WINDOW_SERVICE);
        int opicity = SharedPreferenceClass.getInteger(getApplicationContext(), "opicity", 4);// 0 to 255
        Log.d("opc__t_", " :: " + opicity);

        switch (opicity) {
            case 0:
                opicity = 205;
                break;
            case 1:
                opicity = 221;
                break;
            case 2:
                opicity = 230;
                break;
            case 3:
                opicity = 241;
                break;
            case 4:
                opicity = 255;
                break;
        }


        if (arrayList != null && arrayList.size() > 0) {

            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            if (mLayout == null) {
                mLayout = new FrameLayout(getApplicationContext());
                inflater.inflate(R.layout.recycler_popup_window, mLayout);
            }
            rel = mLayout.findViewById(R.id.rec);
            parent_layout_recpopup = mLayout.findViewById(R.id.parent_layout_recpopup);
            if (SharedPreferenceClass.getBoolean(getApplicationContext(), "isDark", false)) {
                rel.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.bg_popup_night));
            } else {
                rel.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.bg_popup_day));
            }

            if (windowManager != null) {
                if (mLayout.isAttachedToWindow()) {
                    windowManager.removeView(mLayout);
                }
            }

            cancel = mLayout.findViewById(R.id.cancel);
            RecyclerView recyclerView = mLayout.findViewById(R.id.recycler_popup);
            PopupListAdapter popupListAdapter = new PopupListAdapter(getApplicationContext(), arrayList, new OnRecyclerItemClickListenerValueChange() {
                @Override
                public void OnClick(int pos, View view, String detail) {
                    phares = detail;
                    changeAuto(str, pos);
                }
            });

            if (arrayList.size() > 3) {
                int sz = 0;
                Log.d("Sugg__", ":: " + SharedPreferenceClass.getInteger(getApplicationContext(), "max_suggation", 3));
                switch (SharedPreferenceClass.getInteger(getApplicationContext(), "max_suggation", 0) + 3) {
                    case 3:
                        sz = 235;
                        break;
                    case 4:
                        sz = 310;
                        break;
                    case 5:
                        sz = 395;
                        break;
                    case 6:
                        sz = 480;
                        break;
                    case 7:
                        sz = 560;
                        break;
                }
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = dpToPx(sz);
                recyclerView.setLayoutParams(params);
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(popupListAdapter);
            rel.getBackground().setAlpha(opicity);
        }

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = 0;
        params.y = 300;

        try {
            if (windowManager != null) {
                // Log.d("LL__y"," :: "+ mLayout.getWindowToken() + " :: "+mLayout.getParent()+" :: "+mLayout.isAttachedToWindow()+ " :: "+mLayout.getRootView());
                if (mLayout.isAttachedToWindow()) {
                    windowManager.removeView(mLayout);
                }
                windowManager.addView(mLayout, params);
            }
        } catch (Exception e) {
        }


        mLayout.setOnTouchListener(this);

        parent_layout_recpopup.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;


                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();


                        return true;
                    case MotionEvent.ACTION_UP:

                        //Add code for launching application and positioning the widget to nearest edge.


                        return true;
                    case MotionEvent.ACTION_MOVE:


                        float Xdiff = Math.round(event.getRawX() - initialTouchX);
                        float Ydiff = Math.round(event.getRawY() - initialTouchY);


                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) Xdiff;
                        params.y = initialY + (int) Ydiff;

                        //Update the layout with new X & Y coordinates
                        if (windowManager != null) {
                            if (mLayout != null) {
                                if (mLayout.getWindowToken() != null) {
                                    windowManager.updateViewLayout(mLayout, params);
                                }
                            }
                        }


                        return true;
                }
                return false;
            }
        });

        int tou = SharedPreferenceClass.getInteger(getApplicationContext(), "timeout", 0);

        if (h != null)
            h.removeCallbacksAndMessages(null);

        if (SharedPreferenceClass.getBoolean(getApplicationContext(), "tog_timeout", true)) {
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeWView();
                    Log.d("call_view_", "Handler");
                }
            }, (tou + 3) * 1000);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("call_view_", "cancel");
                removeWView();
            }
        });
    }

    private void showListPopupMenu(String str, int i, ArrayList arrayList) {
        windowManager = null;
        windowManager = (WindowManager) getBaseContext().getSystemService(WINDOW_SERVICE);
        int opicity = SharedPreferenceClass.getInteger(getApplicationContext(), "opicity", 4);// 0 to 255
        Log.d("opc__t_", " :: " + opicity);
        switch (opicity) {
            case 0:
                opicity = 205;
                break;
            case 1:
                opicity = 221;
                break;
            case 2:
                opicity = 230;
                break;
            case 3:
                opicity = 241;
                break;
            case 4:
                opicity = 255;
                break;
        }

        if (arrayList != null && arrayList.size() > 0) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            if (mLayout == null) {
                mLayout = new FrameLayout(getApplicationContext());
                inflater.inflate(R.layout.recycler_popup_window, mLayout);
            }
            rel = mLayout.findViewById(R.id.rec);
            parent_layout_recpopup = mLayout.findViewById(R.id.parent_layout_recpopup);
            if (SharedPreferenceClass.getBoolean(getApplicationContext(), "isDark", false)) {
                rel.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.bg_popup_night));
            } else {
                rel.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.bg_popup_day));
            }
            cancel = mLayout.findViewById(R.id.cancel);
            RecyclerView recyclerView = mLayout.findViewById(R.id.recycler_popup);
            PopupPhraseListAdapter popupListAdapter = new PopupPhraseListAdapter(getApplicationContext(), arrayList, new OnRecyclerItemClickListenerValueChange() {
                @Override
                public void OnClick(int pos, View view, String detail) {
                    phares = detail;
                    Log.d("call_view_", "onClick");
                    changeAuto(str, i);
                }
            });

            if (arrayList.size() > 3) {
                int sz = 0;
                Log.d("Sugg__", ":: " + SharedPreferenceClass.getInteger(getApplicationContext(), "max_list", 3));
                switch (SharedPreferenceClass.getInteger(getApplicationContext(), "max_list", 0) + 3) {
                    case 3:
                        sz = 235;
                        break;
                    case 4:
                        sz = 310;
                        break;
                    case 5:
                        sz = 395;
                        break;
                    case 6:
                        sz = 480;
                        break;
                    case 7:
                        sz = 560;
                        break;
                }
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = dpToPx(sz);
                recyclerView.setLayoutParams(params);
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(popupListAdapter);
            rel.getBackground().setAlpha(opicity);
        }

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = 0;
        params.y = 300;

        try {
            if (windowManager != null) {
                // Log.d("LL__y"," :: "+ mLayout.getWindowToken() + " :: "+mLayout.getParent()+" :: "+mLayout.isAttachedToWindow()+ " :: "+mLayout.getRootView());
                if (mLayout.isAttachedToWindow()) {
                    windowManager.removeView(mLayout);
                }
                windowManager.addView(mLayout, params);
            }
        } catch (Exception e) {
        }

        mLayout.setOnTouchListener(this);

        parent_layout_recpopup.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;


                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();


                        return true;
                    case MotionEvent.ACTION_UP:

                        //Add code for launching application and positioning the widget to nearest edge.


                        return true;
                    case MotionEvent.ACTION_MOVE:


                        float Xdiff = Math.round(event.getRawX() - initialTouchX);
                        float Ydiff = Math.round(event.getRawY() - initialTouchY);


                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) Xdiff;
                        params.y = initialY + (int) Ydiff;

                        //Update the layout with new X & Y coordinates
                        if (windowManager != null) {
                            if (mLayout != null) {
                                if (mLayout.getWindowToken() != null) {
                                    windowManager.updateViewLayout(mLayout, params);
                                }
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        int tou = SharedPreferenceClass.getInteger(getApplicationContext(), "timeout", 0);

        if (h != null)
            h.removeCallbacksAndMessages(null);

        if (SharedPreferenceClass.getBoolean(getApplicationContext(), "tog_timeout", true)) {
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("call_view_", "Handler");
                    removeWView();
                }
            }, (tou + 3) * 1000);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("call_view_", "cancel");
                removeWView();
            }
        });

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            Log.d("call_view_", "onTouch");
            removeWView();
            return true;
        }
        return false;
    }

    private void removeWView() {
        if (mLayout != null && mLayout.isAttachedToWindow()) {
            windowManager.removeView(mLayout);
            mLayout = null;
        }
    }

    @Override
    public void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }

        removeWView();
        super.onDestroy();
    }


}
