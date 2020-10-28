package com.writingstar.autotypingandtextexpansion.NormalAdapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.snackbar.Snackbar;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.writingstar.autotypingandtextexpansion.ClassActView.LocalBackupActivity;
import com.writingstar.autotypingandtextexpansion.ClassActView.MainActivity;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.R;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.fileViewHolder> {
    SQLiteHelper dbHelper;
    private Context context;
    public List<File> files;
    LinearLayout no_file;

    public FileAdapter(Context context, List<File> list, LinearLayout no_file) {
        this.context = context;
        this.files = list;
        this.no_file = no_file;
        dbHelper = new SQLiteHelper(context);
        dbHelper.open();

    }

    @NonNull
    @Override
    public FileAdapter.fileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list, parent, false);

        return new FileAdapter.fileViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapter.fileViewHolder holder, int position) {
        holder.tvName.setText(files.get(position).getName().replace(".csv", ""));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile(position);
            }
        });

        holder.btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.btnOption);
                popup.inflate(R.menu.manage_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_file:
                                alert(position, 1);
                                return true;

                            case R.id.share_file:
                                share(position);
                                return true;

                            case R.id.import_file:
                                alert(position, 2);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    private void alert(int position, int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        switch (i) {
            case 1:
                builder.setMessage(context.getResources().getString(R.string.sure_remove_cat));
                break;
            case 2:
                builder.setMessage(context.getResources().getString(R.string.sure_importfile));
                break;
        }
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (i) {
                    case 1:
                        delete(position);
                        break;
                    case 2:
                        importfile(position);
                        break;
                }
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
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
                alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
            }
        });
        alertDialog.show();

    }

    private void importfile(int position) {
        File file2 = new File(Environment.getExternalStorageDirectory(), "WritingStar/" + files.get(position).getName());
        try {
            dbHelper.truncateAll();
            FileReader filereader = new FileReader(file2);
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
                dbHelper.phraseInsertFromCSV(arrayList.get(1).toString(), arrayList.get(2).toString(),
                        arrayList.get(3).toString(), arrayList.get(4).toString(),
                        Integer.parseInt(arrayList.get(5).toString()), arrayList.get(6).toString(),
                        Integer.parseInt(arrayList.get(7).toString()), Integer.parseInt(arrayList.get(8).toString()),
                        Integer.parseInt(arrayList.get(9).toString()), Integer.parseInt(arrayList.get(10).toString()),
                        Integer.parseInt(arrayList.get(11).toString()));
                Snackbar.make( (((LocalBackupActivity) context).findViewById(android.R.id.content)),"Backup "+files.get(position).getName().replace(".csv","")+" successfully restored.",Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void delete(int position) {
        File file = new File(Environment.getExternalStorageDirectory(), "WritingStar/" + files.get(position).getName());
        Uri imageUriLcl = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(imageUriLcl, null, null);
        files.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, files.size());

        if (files.size() <= 0)
            no_file.setVisibility(View.VISIBLE);
        else
            no_file.setVisibility(View.GONE);
    }

    private void share(int position) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(Environment.getExternalStorageDirectory(), "WritingStar/" + files.get(position).getName());

        if (fileWithinMyDir.exists()) {
            intentShareFile.setType("text/*");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/WritingStar/" + files.get(position).getName()));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Share File...");
            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    private void openFile(int position) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "WritingStar/" + files.get(position).getName());
            Log.d("FILE___", "" + file);
            Intent csvIntent = new Intent(Intent.ACTION_VIEW);
            csvIntent.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file), "text/csv");
            csvIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            csvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(csvIntent);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.no_file_found), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class fileViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        RelativeLayout relativeLayout;
        ImageView btnOption;

        public fileViewHolder(@NonNull View convertView, int viewType) {
            super(convertView);

            tvName = (TextView) convertView.findViewById(R.id.filename);
            relativeLayout = (RelativeLayout) convertView.findViewById(R.id.lay_file);
            btnOption = (ImageView) convertView.findViewById(R.id.btnOption);
        }
    }
}
