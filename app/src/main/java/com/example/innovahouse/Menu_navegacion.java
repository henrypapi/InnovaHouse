package com.example.innovahouse;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
public class Menu_navegacion {
    public static void setupMenu(Activity activity) {
        ImageView btnMenu = activity.findViewById(R.id.btnmenu);
        ImageView btnIconos = activity.findViewById(R.id.btniconos);
        ImageView btnPerfil = activity.findViewById(R.id.btnperfil);
        ImageView btnCarrito = activity.findViewById(R.id.btncarrito);

        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, MainActivity.class));
            });
        }

        if (btnIconos != null) {
            btnIconos.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            });
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, menu_perfil.class));
            });
        }

        if (btnCarrito != null) {
            btnCarrito.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, ProductoDetalleActivity.class));
            });
        }
    }
}
