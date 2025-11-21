package com.example.innovahouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarritoActivity extends AppCompatActivity {

    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;
    RecyclerView recyclerCarrito;
    CarritoAdapter adapter;

    TextView tvTotal, btnPagar;

    ArrayList<Producto> listaCarrito = new ArrayList<>(); // TEMPORAL (luego será la BD carrito)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);


        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);

        recyclerCarrito = findViewById(R.id.recyclerCarrito);
        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));

        tvTotal = findViewById(R.id.tvTotal);
        btnPagar = findViewById(R.id.btnPagar);

        // -------------------------
        // CARGAR CARRITO (TEMPORAL)

        adapter = new CarritoAdapter(listaCarrito, this);
        recyclerCarrito.setAdapter(adapter);

        actualizarTotal();

        // -------------------------
        // BOTÓN PAGAR

                btnPagar.setOnClickListener(v ->
                        android.widget.Toast.makeText(this, "Pasarela de pago próximamente", android.widget.Toast.LENGTH_SHORT).show()
                );

        // -------------------------
        // BOTONES SUPERIORES

                configurarBarraSuperior();
    }

    private void actualizarTotal() {
        double total = 0;
        for (Producto p : listaCarrito) total += p.getPrecio();
        tvTotal.setText("Total: S/. " + total);
    }

    private void configurarBarraSuperior() {

        btnHome.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class))
        );

        btnIconos.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "Función no implementada", android.widget.Toast.LENGTH_SHORT).show()
        );

        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class))
        );

        btnCarrito.setOnClickListener(v -> {}); // ya estás en carrito
    }
}
