package com.rishi.frendzapp.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

import com.rishi.frendzapp_core.utils.ConstsCore;
import com.rishi.frendzapp_core.utils.ErrorUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {

    private File filesFolder;
    private static final String folderName = "/Frendzapp";
    private static final String fileType = ".jpeg";

    public FileHelper() {
        initFilesFolder();
    }

    private void initFilesFolder() {
        filesFolder = new File(Environment.getExternalStorageDirectory() + folderName);
        if (!filesFolder.exists()) {
            filesFolder.mkdirs();
        }
    }

    public void checkExsistFile(String fileUrlString, Bitmap bitmap) {
        File file = createFileIfNotExist(fileUrlString);
        if (!file.exists()) {
            saveFile(file, bitmap);
        }
    }

    public File createFileIfNotExist(String fileUrlString) {
        String fileName = fileUrlString.substring(fileUrlString.lastIndexOf('/') + 1) + fileType;
        return new File(filesFolder, fileName);
    }

    private void saveFile(File file, Bitmap bitmap) {
        // starting new Async Task
        new SavingFileTask().execute(file, bitmap);
    }

    private class SavingFileTask extends AsyncTask<Object, String, Object> {

        @Override
        protected Object doInBackground(Object... objects) {
            File file = (File) objects[0];
            Bitmap bitmap = (Bitmap) objects[1];

            FileOutputStream fileOutputStream;

            try {
                fileOutputStream = new FileOutputStream(file);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, ConstsCore.FULL_QUALITY, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                fileOutputStream.write(byteArray);

                fileOutputStream.flush();
                fileOutputStream.close();

                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();

            } catch (FileNotFoundException e) {
                ErrorUtils.logError(e);
            } catch (IOException e) {
                ErrorUtils.logError(e);
            }

            return null;
        }
    }
}