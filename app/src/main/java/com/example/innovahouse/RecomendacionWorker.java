package com.example.innovahouse;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RecomendacionWorker extends Worker {

    private static final String CHANNEL_ID = "recomendaciones_channel";

    public RecomendacionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 1. Obtener un producto aleatorio de la BD
        Producto producto = obtenerProductoAleatorio();

        // 2. Si encontramos un producto, mostramos la notificación
        if (producto != null) {
            mostrarNotificacion(producto);
        }

        return Result.success();
    }

    private Producto obtenerProductoAleatorio() {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Producto prod = null;

        // Query para obtener 1 solo producto al azar
        Cursor cursor = db.rawQuery("SELECT * FROM productos ORDER BY RANDOM() LIMIT 1", null);

        if (cursor.moveToFirst()) {
            // Asumiendo el orden de tus columnas: 0:id, 1:nombre, 2:precio, 3:desc, 4:img
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            double precio = cursor.getDouble(2);
            String descripcion = cursor.getString(3);
            String imagen = cursor.getString(4);

            prod = new Producto(id, nombre, precio, descripcion, imagen);
        }
        cursor.close();
        db.close();
        return prod;
    }

    private void mostrarNotificacion(Producto producto) {
        Context context = getApplicationContext();

        // Crear el canal de notificaciones (Obligatorio para Android 8.0+)
        createNotificationChannel(context);

        // Intent para abrir la app al tocar la notificación
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // CAMBIAR POR TU LOGO: R.drawable.ic_tu_logo
                .setContentTitle("¡Sugerencia Innovahouse!")
                .setContentText("Mira nuestro " + producto.getNombre() + " a solo S/ " + producto.getPrecio())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("No te pierdas la oferta: " + producto.getDescripcion()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Mostrarla
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            // Usamos el ID del producto como ID de notificación para que no se reemplacen si se acumulan
            notificationManager.notify(producto.getId(), builder.build());
        } catch (SecurityException e) {
            // Manejar falta de permisos en Android 13+
            e.printStackTrace();
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Recomendaciones";
            String description = "Canal para sugerencias de productos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}