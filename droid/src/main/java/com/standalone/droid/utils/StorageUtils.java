package com.standalone.droid.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.List;

/**
 * Requirement:
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 **/
public class StorageUtils {
    public static File getDefaultStorage(Context context) {
        String appName = getApplicationName(context);
        File storage = new File(Environment.getExternalStorageDirectory(), appName);
        Log.i("INFO_EXTERNAL", "getStorage: " + storage.getAbsolutePath());
        if (!storage.exists()) {
            if (!storage.mkdir()) {
                Log.e("WRITE_EXTERNAL_STORAGE", "Cannot create a new folder");
            }
        }

        return storage;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static File getRemovableStorage(Context context) {
        String appName = getApplicationName(context);
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();
        StorageVolume storageVolume = storageVolumeList.get(1);
        return new File(storageVolume.getDirectory(), appName);
    }

    public static boolean hasSDCard() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }
}
