package com.example.innovahouse;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ImageUtils {

    public static String saveDrawableToInternal(Context context, int drawableId, String fileName) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);

            File file = PathsManager.createImageFile(context, fileName);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveUriImageToInternal(Context context, Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream input = resolver.openInputStream(uri);

            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();

            String fileName = "img_" + System.currentTimeMillis() + ".png";
            File file = PathsManager.createImageFile(context, fileName);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
