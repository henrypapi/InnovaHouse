package com.example.innovahouse;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Producto> listaProductos = new ArrayList<>();
    ProductoAdapter adapter;
    TextView tvBienvenida;

    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;
    FloatingActionButton btnAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ---------------------------
        // REFERENCIAS A LA INTERFAZ
        // ---------------------------
        recyclerView = findViewById(R.id.recyclerProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductoAdapter(listaProductos);
        recyclerView.setAdapter(adapter);

        tvBienvenida = findViewById(R.id.tvBienvenida);

        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);
        btnAgregar = findViewById(R.id.btnAgregar);


        // ---------------------------
        // OBTENER DATOS DE SESIÓN
        // ---------------------------
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String nombreUsuario = prefs.getString("userName", "Usuario");
        String rol = prefs.getString("rol", "usuario");

        // ---------------------------
        // MENSAJE DE BIENVENIDA
        // ---------------------------
        if (rol.equals("admin")) {
            tvBienvenida.setText("Bienvenido Administrador");
        } else {
            tvBienvenida.setText("Bienvenido, " + nombreUsuario);
        }


        // ---------------------------
        // OCULTAR BOTÓN AGREGAR SI NO ES ADMIN
        // ---------------------------
        if (!rol.equals("admin")) {
            btnAgregar.setVisibility(FloatingActionButton.GONE);
        }


        // ---------------------------
        // BOTONES SUPERIOR
        // ---------------------------
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnIconos.setOnClickListener(v ->
                Toast.makeText(this, "Función no implementada", Toast.LENGTH_SHORT).show()
        );

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnCarrito.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CarritoActivity.class);
            startActivity(intent);
        });

        // ---------------------------
        // BOTÓN AGREGAR PRODUCTO
        // ---------------------------
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarProductoActivity.class);
            startActivity(intent);
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        PeriodicWorkRequest recomendacionRequest =
                new PeriodicWorkRequest.Builder(RecomendacionWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "trabajo_recomendacion",
                ExistingPeriodicWorkPolicy.KEEP, // Si ya existe, no lo reemplaza (mantiene el tiempo)
                recomendacionRequest
        );

        // Para prueba inmediata (solo una vez)
        OneTimeWorkRequest testRequest =
                new OneTimeWorkRequest.Builder(RecomendacionWorker.class)
                        .build();

        WorkManager.getInstance(this).enqueue(testRequest);


        // ---------------------------
        // CARGAR PRODUCTOS
        // ---------------------------
        cargarProductos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
    }

    private void cargarProductos() {
        listaProductos.clear();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM productos", null);

        // Si no hay productos, agregar productos iniciales
        if (cursor.getCount() == 0) {

            db.execSQL("INSERT INTO productos (nombre, precio, descripcion, imagen) VALUES ('Refrigeradora', 1800, 'Refrigeradora LG 400L', 'refrigeradora')");
            db.execSQL("INSERT INTO productos (nombre, precio, descripcion, imagen) VALUES ('Lavadora', 1500, 'Lavadora Samsung 12kg', 'lavadora')");
            db.execSQL("INSERT INTO productos (nombre, precio, descripcion, imagen) VALUES ('Microondas', 450, 'Microondas Panasonic 30L', 'microondas')");

            cursor.close();
            cursor = db.rawQuery("SELECT * FROM productos", null);
            Toast.makeText(this, "Productos de prueba agregados", Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            double precio = cursor.getDouble(2);
            String descripcion = cursor.getString(3);
            String imagen = cursor.getString(4);

            listaProductos.add(new Producto(id, nombre, precio, descripcion, imagen));
        }

        cursor.close();
        db.close();

        adapter.notifyDataSetChanged();
    }
}
