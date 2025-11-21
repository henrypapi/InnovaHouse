package com.example.innovahouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Producto> listaProductos = new ArrayList<>();
    ArrayList<Producto> listaProductosFiltrada = new ArrayList<>();
    ArrayList<Producto> listaProductosPaginada = new ArrayList<>();
    ProductoAdapter adapter;
    TextView tvBienvenida, tvPaginacion;
    EditText etBuscar;

    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;
    FloatingActionButton btnAgregar;
    ImageView btnPrevious, btnNext;
    String rol;
    
    private static final int PRODUCTOS_POR_PAGINA = 5;
    private int paginaActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // REFERENCIAS A LA INTERFAZ
        recyclerView = findViewById(R.id.recyclerProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductoAdapter(listaProductosPaginada);
        recyclerView.setAdapter(adapter);

        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvPaginacion = findViewById(R.id.tvPaginacion);
        etBuscar = findViewById(R.id.etBuscar);

        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);
        btnAgregar = findViewById(R.id.btnAgregar);

        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        // OBTENER DATOS DE SESIÓN
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String nombreUsuario = prefs.getString("userName", "Usuario");
        rol = prefs.getString("rol", "usuario");

        // MENSAJE DE BIENVENIDA
        if (rol.equals("admin")) {
            tvBienvenida.setText("Bienvenido Administrador");
        } else {
            tvBienvenida.setText("Bienvenido, " + nombreUsuario);
        }

        // OCULTAR BOTÓN AGREGAR SI NO ES ADMIN
        if (!rol.equals("admin")) {
            btnAgregar.setVisibility(FloatingActionButton.GONE);
        }

        // BOTONES SUPERIOR
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnIconos.setOnClickListener(v -> {
            if (rol.equals("admin")) {
                startActivity(new Intent(MainActivity.this, PedidosActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, HistorialActivity.class));
            }
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnCarrito.setOnClickListener(v -> {
            if (rol.equals("admin")) {
                Intent intent = new Intent(MainActivity.this, AgregarProductoActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        // BOTÓN AGREGAR PRODUCTO
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarProductoActivity.class);
            startActivity(intent);
        });

        // BÚSQUEDA Y FILTRO
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                paginaActual = 0;
                filtrarProductos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // BOTONES DE PAGINACIÓN
        btnPrevious.setOnClickListener(v -> {
            if (paginaActual > 0) {
                paginaActual--;
                actualizarPaginacion();
            }
        });

        btnNext.setOnClickListener(v -> {
            int totalPaginas = (int) Math.ceil((double) listaProductosFiltrada.size() / PRODUCTOS_POR_PAGINA);
            if (paginaActual < totalPaginas - 1) {
                paginaActual++;
                actualizarPaginacion();
            }
        });

        // CARGAR PRODUCTOS
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
            db.execSQL("INSERT INTO productos (nombre, precio, descripcion, imagen) VALUES ('Refrigeradora', 1800, 'Refrigeradora LG 400L Premium', 'refrigeradora')");
            db.execSQL("INSERT INTO productos (nombre, precio, descripcion, imagen) VALUES ('Lavadora', 1500, 'Lavadora Samsung 12kg Automática', 'lavadora')");
            db.execSQL("INSERT INTO productos (nombre, precio, descripcion, imagen) VALUES ('Microondas', 450, 'Microondas Panasonic 30L Digital', 'microondas')");
            db.execSQL("INSERT INTO productos (nombre, precio, descripcion, imagen) VALUES ('Cocina', 1200, 'Cocina Whirlpool 6 quemadores', 'cocina')");
            db.execSQL("INSERT INTO productos (nombre, precio, descripcion, imagen) VALUES ('Cafetera', 250, 'Cafetera Electrolux 12 tazas', 'cafetera')");
            db.execSQL("INSERT INTO productos (nombre, precio, descripcion, imagen) VALUES ('Licuadora', 150, 'Licuadora Oster 6 velocidades', 'licuadora')");

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

        filtrarProductos(etBuscar.getText().toString());
    }

    private void filtrarProductos(String query) {
        listaProductosFiltrada.clear();

        if (query.isEmpty()) {
            listaProductosFiltrada.addAll(listaProductos);
        } else {
            String queryLower = query.toLowerCase();
            for (Producto p : listaProductos) {
                if (p.getNombre().toLowerCase().contains(queryLower) ||
                    p.getDescripcion().toLowerCase().contains(queryLower)) {
                    listaProductosFiltrada.add(p);
                }
            }
        }

        paginaActual = 0;
        actualizarPaginacion();
    }

    private void actualizarPaginacion() {
        listaProductosPaginada.clear();

        int inicio = paginaActual * PRODUCTOS_POR_PAGINA;
        int fin = Math.min(inicio + PRODUCTOS_POR_PAGINA, listaProductosFiltrada.size());

        for (int i = inicio; i < fin; i++) {
            listaProductosPaginada.add(listaProductosFiltrada.get(i));
        }

        adapter.notifyDataSetChanged();

        // Actualizar indicador de paginación
        int totalPaginas = (int) Math.ceil((double) listaProductosFiltrada.size() / PRODUCTOS_POR_PAGINA);
        tvPaginacion.setText("Página " + (paginaActual + 1) + " de " + totalPaginas);

        // Habilitar/deshabilitar botones
        btnPrevious.setEnabled(paginaActual > 0);
        btnNext.setEnabled(paginaActual < totalPaginas - 1);

        btnPrevious.setAlpha(paginaActual > 0 ? 1f : 0.5f);
        btnNext.setAlpha(paginaActual < totalPaginas - 1 ? 1f : 0.5f);
    }
}
