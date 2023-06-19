package com.standalone.droid.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
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

    public static File getRemovableStorage(Context context) {
        String appName = getApplicationName(context);
        File storage = null;
        List<String> dictPaths = Arrays.asList("external_sd", "ext_sd", "external", "extSdCard", "sdcard2");

        for (String desiredPath : dictPaths) {
            File sdCard = new File("/mnt/", desiredPath);
            if (sdCard.isDirectory() && sdCard.canWrite()) {
                storage = new File(sdCard, appName);
                if (!storage.mkdir()) {
                    Log.e("WRITE_EXTERNAL_STORAGE", "Cannot create a new folder");
                }
                break;
            }
        }
        return storage;
    }

    public static boolean isRemovableStorage(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }
}
