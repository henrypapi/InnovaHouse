package com.example.innovahouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    TextView tvNombre, tvCorreo, tvRol;
    Button btnCerrarSesion;

    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // DATOS DE SESIÓN
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String nombre = prefs.getString("userName", "Usuario");
        String apellido = prefs.getString("apellido", "");
        String correo = prefs.getString("userEmail", "");
        String rol = prefs.getString("rol", "usuario");

        // UI
        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        tvRol = findViewById(R.id.tvRol);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);

        // Colocar datos reales
        tvNombre.setText(nombre + " " + apellido);
        tvCorreo.setText(correo);
        tvRol.setText("Rol: " + rol);

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        // BOTONES SUPERIORES
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        btnIconos.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "Función no implementada", android.widget.Toast.LENGTH_SHORT).show()
        );

        btnPerfil.setOnClickListener(v -> {}); // ya está en perfil

        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, CarritoActivity.class))
        );
    }
}
