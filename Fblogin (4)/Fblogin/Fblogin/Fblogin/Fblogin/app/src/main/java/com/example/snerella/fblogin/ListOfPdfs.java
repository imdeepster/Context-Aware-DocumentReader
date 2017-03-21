package com.example.snerella.fblogin;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FilenameFilter;

public class ListOfPdfs extends ListActivity {

        public File[] imagelist;
        public static String FILE_PATH = "file_path";
        public static final int READ_EXTERNAL_STORAGE = 1;
        public String TAG = "ListActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list);

        //Checking if read permission has been obtained

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.e("TAG", "Permission is granted");
            enumeratePdf();
        } else {

            Log.e("TAG", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);

        }
    }

//Method to list all files from downloads directory - Downloads directory only
    public void enumeratePdf() {
        File images = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        imagelist = images.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return ((name.endsWith(".pdf")));
            }
        });
        String[] pdflist = new String[imagelist.length];
        for (int i = 0; i < imagelist.length; i++) {
            pdflist[i] = imagelist[i].getName();
        }

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, pdflist));
    }
//If permission not obtained this method explicitly seeks permissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                    Log.e("TAG", "Permission is granted");
                    enumeratePdf();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e("TAG", "Permission is revoked");
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }


    }


//Method to call Display pdf on the list item selection (any pdf file)

    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        String path = imagelist[(int) id].getAbsolutePath();

        Intent intent=new Intent(this,com.example.snerella.fblogin.Pdf.class);
        intent.putExtra(FILE_PATH,path);
        startActivity(intent);




    }
}