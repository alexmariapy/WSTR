package com.writingstar.autotypingandtextexpansion.AWritingStar;

import android.content.IntentSender;
import android.database.Cursor;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.writingstar.autotypingandtextexpansion.ClassActView.MainActivity;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.OtherClass.CSVWriter;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.drive.Drive.*;
import static com.writingstar.autotypingandtextexpansion.ClassActView.MainActivity.REQUEST_CODE_CREATION;
import static com.writingstar.autotypingandtextexpansion.ClassActView.MainActivity.REQUEST_CODE_OPENING;
import static com.writingstar.autotypingandtextexpansion.ClassActView.MainActivity.REQUEST_CODE_SIGN_IN;
import static com.writingstar.autotypingandtextexpansion.KotlinData.SQLiteHandler.DATABASE_NAME;


public class RemoteBackup {

    private static final String TAG = "Google Drive Activity";

    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    public TaskCompletionSource<DriveId> mOpenItemTaskSource;

    private MainActivity activity;

    public RemoteBackup(MainActivity activity) {
        this.activity = activity;
    }

    public void connectToDrive(boolean backup) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account == null) {
            signIn();
        } else {
            //Initialize the drive api
            mDriveClient = getDriveClient(activity, account);
            // Build a drive resource client.
            mDriveResourceClient = getDriveResourceClient(activity, account);
            if (backup)
                startDriveBackup();
            else
                startDriveRestore();
        }
    }

    private void signIn() {
        Log.i(TAG, "Start sign in");
        GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient();
        activity.startActivityForResult(GoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(activity, signInOptions);
    }


    private void startDriveBackup() {
        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        task -> createFileIntentSender(task.getResult()))
                .addOnFailureListener(
                        e -> Log.w(TAG, "Failed to create new contents.", e));
    }

    private Task<Void> createFileIntentSender(DriveContents driveContents) {

        try {
            File exportDir = null;
            exportDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, "writingstar.csv");
            FileInputStream fis = new FileInputStream(file);
            OutputStream outputStream = driveContents.getOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                .setTitle("writingstar.csv")
                .setMimeType("application/csv")
                .build();

        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        task -> {
                            activity.startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATION, null, 0, 0, 0);
                            return null;
                        });
    }

    private void startDriveRestore() {
        pickFile().addOnSuccessListener(activity,
                driveId -> retrieveContents(driveId.asDriveFile()))
                .addOnFailureListener(activity, e -> {
                    Log.e(TAG, "No file selected", e);
                });
    }

    private void retrieveContents(DriveFile file) {
      /*  SQLiteHelper dbHelper;
        dbHelper = new SQLiteHelper(activity);
        dbHelper.open();
        try {
            FileReader filereader = new FileReader(String.valueOf(file));

            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();

            ArrayList arrayList = null;
            for (String[] row : allData) {
                if (arrayList != null)
                    arrayList.clear();
                arrayList = new ArrayList();
                for (String cell : row) {
                    Log.d("QRY__", "\n:: " + cell);
                    arrayList.add(cell);
                }
                dbHelper.phraseInsert(arrayList.get(1).toString(), arrayList.get(2).toString(),
                        arrayList.get(3).toString(), arrayList.get(4).toString(),
                        Integer.parseInt(arrayList.get(5).toString()), Integer.parseInt(arrayList.get(6).toString()),
                        Integer.parseInt(arrayList.get(7).toString()), Integer.parseInt(arrayList.get(8).toString()),
                        Integer.parseInt(arrayList.get(9).toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        final String inFileName = "writingstar.csv";
        try {
            File exportDir = null;
            exportDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
        } catch (Exception e) {
        }

        Task<DriveContents> openFileTask = mDriveResourceClient.openFile(file, DriveFile.MODE_READ_WRITE);

        openFileTask.continueWithTask(task -> {
            DriveContents contents = task.getResult();

            BufferedReader reader = new BufferedReader(new InputStreamReader(task.getResult().getInputStream(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(s.toString());
            String link = jsonObject.getString("Result");

            BufferedOutputStream bos = new BufferedOutputStream(contents.getOutputStream());
            try {
                File imageDirectory = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
                FileInputStream is = new FileInputStream(new File(imageDirectory, inFileName));
                BufferedInputStream bis = new BufferedInputStream(is);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = bis.read(buffer)) > 0) {
                    //Log.d("call_bite_", " :: " + length + " :: " + fileInputStream.read(buffer));
                    bos.write(buffer, 0, length);
                    Toast.makeText(activity, "call_", Toast.LENGTH_SHORT).show();
                }
                bos.flush();
                bos.close();

                Log.d("import_show_remote", "call_");
                SharedPreferenceClass.setBoolean(activity, "importdata", true);
                Toast.makeText(activity, "Import completed", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show();
            }
            return mDriveResourceClient.discardContents(contents);

        })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Unable to read contents", e);
                    Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show();
                });
    }

    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        mDriveClient
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith((Continuation<IntentSender, Void>) task -> {
                    activity.startIntentSenderForResult(
                            task.getResult(), REQUEST_CODE_OPENING, null, 0, 0, 0);
                    return null;
                });
        return mOpenItemTaskSource.getTask();
    }

    private Task<DriveId> pickFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/csv"))
                        .setActivityTitle("Select writingstar.csv File")
                        .build();
        return pickItem(openOptions);
    }
}
