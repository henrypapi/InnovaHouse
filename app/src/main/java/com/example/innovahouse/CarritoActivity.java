package com.example.innovahouse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarritoActivity extends AppCompatActivity {

    // UI
    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;
    RecyclerView recyclerCarrito;
    TextView tvTotal, btnPagar;

    // Datos
    CarritoAdapter adapter;
    ArrayList<Producto> listaCarrito;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        // Inicializar BD
        dbHelper = new DBHelper(this);

        // Referencias UI (IDs de activity_carrito.xml)
        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);

        recyclerCarrito = findViewById(R.id.recyclerCarrito);
        tvTotal = findViewById(R.id.tvTotal);
        btnPagar = findViewById(R.id.btnPagar);

        // Configuración del RecyclerView
        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));

        // Cargar datos
        cargarDatosDelCarrito();

        // Botón Pagar (Botón verde grande)
        btnPagar.setOnClickListener(v -> {
            if (listaCarrito == null || listaCarrito.isEmpty()) {
                Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Procesando compra...", Toast.LENGTH_SHORT).show();
                // Aquí podrías vaciar el carrito despues de pagar:
                // dbHelper.vaciarCarrito();
                // cargarDatosDelCarrito(); // recargar para ver vacio
            }
        });

        // Navegación Barra Inferior
        configurarNavegacion();
    }

    private void cargarDatosDelCarrito() {
        // 1. Pedir lista a la base de datos
        listaCarrito = dbHelper.obtenerCarrito();

        // 2. Configurar adaptador
        adapter = new CarritoAdapter(listaCarrito, this);
        recyclerCarrito.setAdapter(adapter);

        // 3. Calcular Total
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = 0;
        if (listaCarrito != null) {
            for (Producto p : listaCarrito) {
                total += p.getPrecio();
            }
        }
        // Mostrar total con formato de moneda
        tvTotal.setText("Total: S/. " + String.format("%.2f", total));
    }

    private void configurarNavegacion() {
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpiar pila para no acumular
            startActivity(intent);
            finish();
        });

        btnPerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, PerfilActivity.class));
        });

        btnIconos.setOnClickListener(v ->
                Toast.makeText(this, "Categorías (Próximamente)", Toast.LENGTH_SHORT).show()
        );

        // btnCarrito no hace nada porque ya estamos aquí
    }

    // Método opcional: Si sales y vuelves, refrescar datos
    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosDelCarrito();
    }
}