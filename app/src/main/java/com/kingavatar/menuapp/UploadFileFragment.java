package com.kingavatar.menuapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class UploadFileFragment extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Upload");
        final Button button = view.findViewById(R.id.button1);
        final int FILE_SELECT_CODE = 1;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (settings.contains("file_name")) {
            String name = "Tap to Upload \n\n" + settings.getString("file_name", "File Not Selected Tap to Upload");
            TextView text = view.findViewById(R.id.file_selecting);
            text.setText(name);
        }
        //Log.d("file_name",Boolean.toString(settings.contains("file_name")));
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onClick(View view) {
                Intent intent;// = new Intent(Intent.ACTION_GET_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("*/*");
                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(getActivity(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filechooser, container, false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    String PathHolder = Objects.requireNonNull(data.getData()).getPath();
                    final String Name = getFileName(uri);
                    //DashboardFragment.setPathHolder(PathHolder);
                    DashboardFragment.setExcelUri(uri);
                    TextView text = Objects.requireNonNull(getActivity()).findViewById(R.id.file_selecting);
                    String display = "Selected File : " + Name;
                    text.setText(display);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("file_name", Name);
                    editor.putString("file_uri", Objects.requireNonNull(uri).toString());
                    editor.apply();
                    new AsyncTask<Uri, Integer, String>() {
                        private String toast_msg;
                        private ProgressDialog dialog;

                        @Override
                        protected void onPreExecute() {
                            dialog = new ProgressDialog(getActivity());
                            dialog.setMessage("Please Wait");
                            dialog.setCancelable(false);
                            dialog.show();
                            super.onPreExecute();
                        }

                        @Override
                        protected String doInBackground(Uri... uris) {
                            DataBaseHelper mydatabase = new DataBaseHelper(getContext());
                            Sheet sheet = null;
                            try {
                                sheet = WorkbookFactory.create(Objects.requireNonNull(Objects.requireNonNull(getContext()).getContentResolver().openInputStream(uri))).getSheetAt(0);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                            } catch (InvalidFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            toast_msg = mydatabase.excelDatabase(sheet);
                            return toast_msg;
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            super.onProgressUpdate(values);
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            if (dialog.isShowing()) dialog.dismiss();
                            ((MainActivity) getActivity()).after_upload();
                            super.onPostExecute(s);
                            Toast.makeText(getContext(), toast_msg + " from " + Name, Toast.LENGTH_LONG).show();
                        }
                    }.execute(uri);
                }
                break;
        }
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            Context applicationContext = Objects.requireNonNull(getActivity()).getApplicationContext();
            try (Cursor cursor = applicationContext.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = Objects.requireNonNull(result).lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
