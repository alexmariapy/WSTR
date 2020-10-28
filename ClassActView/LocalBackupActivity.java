package com.writingstar.autotypingandtextexpansion.ClassActView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.material.snackbar.Snackbar;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.FileAdapter;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.LoadApplistAdapter;
import com.writingstar.autotypingandtextexpansion.OtherClass.CSVWriter;
import com.writingstar.autotypingandtextexpansion.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;

public class LocalBackupActivity extends AppCompatActivity {
    TextView tv_actionbar_title, btnBackup;
    private LinearLayout adLinLay;
    AdView adView;
    ImageView imgClose, imgBtnDone;
    FileAdapter fileAdapter;
    SQLiteHelper dbHelper;
    LinearLayout no_file;
    RelativeLayout parent_lay;
    private androidx.appcompat.app.AlertDialog alertSimpleDialog;
    RecyclerView recyclerView;

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
        setContentView(R.layout.activity_local_backup);

        imgClose = findViewById(R.id.imgClose);
        imgBtnDone = findViewById(R.id.imgBtnDone);
        btnBackup = findViewById(R.id.btnBackup);
        parent_lay = findViewById(R.id.parent_lay);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        imgBtnDone.setVisibility(View.GONE);
        no_file = findViewById(R.id.no_files);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_backuplist);
        tv_actionbar_title.setText(getResources().getString(R.string.txt_backup));
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dbHelper = new SQLiteHelper(LocalBackupActivity.this);
        dbHelper.open();

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chekStoragePermission())
                    backupFile();
                else
                    storagepermissiononly(202);

            }
        });

        if (!chekStoragePermission())
            storagepermissiononly(200);

        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getFiles("") == null || getFiles("").size() <= 0) {
            no_file.setVisibility(View.VISIBLE);
        } else {
            no_file.setVisibility(View.GONE);
            fileAdapter = new FileAdapter(LocalBackupActivity.this, getFiles(""), no_file);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(fileAdapter);
        }

    }

    boolean chekStoragePermission() {
        boolean allow = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            allow = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            allow = false;
        }
        return allow;
    }

    void storagepermissiononly(int reqcode) {
        List<String> listPermissionsNeeded = new ArrayList<>();
        listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
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
                switch (requestCode) {
                    case 202:
                        backupFile();
                        break;
                    case 200:
                        onResume();
                        break;
                }
            }
        }
    }

    public void showSimpleDialog() {
        try {
            if (alertSimpleDialog != null && !isFinishing()) {
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
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                alertSimpleDialog = builder.create();
                alertSimpleDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        alertSimpleDialog.getButton(alertSimpleDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                });
                alertSimpleDialog.show();
            }
        } catch (Exception e) {
        }
    }


    private void backupFile() {
        if (dbHelper.getAllphraseList().size() > 0) {
            String currentDate = new SimpleDateFormat("d_M_yy_hmmss", Locale.getDefault()).format(new Date());
            File exportDir = null;
            exportDir = new File(Environment.getExternalStorageDirectory(), "WritingStar");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            Log.d("f_name__ :: ", "ws_backup_" + currentDate + ".csv");
            File file = new File(exportDir, "ws_backup_" + currentDate + ".csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                Cursor curCSV = dbHelper.getTableCursur();
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {
                    String arrStr[] = {
                            String.valueOf(curCSV.getInt(0)),
                            curCSV.getString(1),
                            curCSV.getString(2),
                            curCSV.getString(3),
                            curCSV.getString(4),
                            String.valueOf(curCSV.getInt(5)),
                            curCSV.getString(6),
                            String.valueOf(curCSV.getInt(7)),
                            String.valueOf(curCSV.getInt(8)),
                            String.valueOf(curCSV.getInt(9)),
                            String.valueOf(curCSV.getInt(10)),
                            String.valueOf(curCSV.getInt(11))
                    };
                    Log.d("QRY__rnd_", "\n :: " + String.valueOf(curCSV.getInt(0))
                            + "\n :: " + curCSV.getString(1)
                            + "\n :: " + curCSV.getString(2)
                            + "\n :: " + curCSV.getString(3)
                            + "\n :: " + curCSV.getString(4)
                            + "\n :: " + String.valueOf(curCSV.getInt(5))
                            + "\n :: " + curCSV.getString(6)
                            + "\n :: " + String.valueOf(curCSV.getInt(7))
                            + "\n :: " + String.valueOf(curCSV.getInt(8))
                            + "\n :: " + String.valueOf(curCSV.getInt(9))
                            + "\n :: " + String.valueOf(curCSV.getInt(10))
                            + "\n :: " + String.valueOf(curCSV.getInt(11)));

                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();
                Snackbar.make(parent_lay,exportDir.getPath().toString() + "/ws_backup_" + currentDate,Snackbar.LENGTH_LONG).show();
                onResume();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Snackbar.make(parent_lay,getResources().getString(R.string.no_phrase_for_backup),Snackbar.LENGTH_LONG).show();
        }

    }

    public List<File> getFiles(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append("/");
        sb.append("WritingStar");
        File[] listFiles = new File(sb.toString()).listFiles();
        ArrayList arrayList = new ArrayList();

        if (listFiles != null) {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].getName().contains(str)) {
                    arrayList.add(listFiles[i]);
                }
            }
        }
        return arrayList;
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