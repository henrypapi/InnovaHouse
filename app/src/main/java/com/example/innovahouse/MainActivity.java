package com.example.innovahouse;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Producto> listaProductos = new ArrayList<>();
    ProductoAdapter adapter;
    TextView tvBienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductoAdapter(listaProductos);
        recyclerView.setAdapter(adapter);

        tvBienvenida = findViewById(R.id.tvBienvenida);

        // 🔹 Obtener datos del usuario desde el Login
        String nombreUsuario = getIntent().getStringExtra("userName");
        String correoUsuario = getIntent().getStringExtra("userEmail");

        if (nombreUsuario != null && nombreUsuario.equalsIgnoreCase("Admin")) {
            tvBienvenida.setText("Bienvenido Administrador");
        } else if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            tvBienvenida.setText("Bienvenido, " + nombreUsuario);
        } else {
            tvBienvenida.setText("Bienvenido");
        }

        // 🔹 Botón para agregar nuevo producto
        FloatingActionButton btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarProductoActivity.class);
            startActivity(intent);
        });

        // 🔹 Cargar productos
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