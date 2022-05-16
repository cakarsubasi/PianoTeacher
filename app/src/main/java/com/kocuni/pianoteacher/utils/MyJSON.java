package com.kocuni.pianoteacher.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class MyJSON {
    static String fileName = "songfromserver.json";
    static String fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();

    public static void saveData( String serverResponse) throws IOException {
        FileWriter file = new FileWriter(fileDirectory + "/" + fileName);
        try {
            file.write(serverResponse);
            file.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }

    }

    public static String getData(){
        File f = new File(fileDirectory+ "/" + fileName);
        try {
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }


    }
}
