package com.example.innovahouse;

import android.content.Context;
import java.io.File;

public class PathsManager {

    public static File getImagesDir(Context context) {
        File dir = new File(context.getFilesDir(), "imagenes");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File createImageFile(Context context, String fileName) {
        return new File(getImagesDir(context), fileName);
    }

    public static String getImagePath(Context context, String fileName) {
        return new File(getImagesDir(context), fileName).getAbsolutePath();
    }
}
