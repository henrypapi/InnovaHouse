package com.example.innovahouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {

    RecyclerView recyclerHistorial;
    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;
    DBHelper dbHelper;
    TextView tvSinHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        dbHelper = new DBHelper(this);

        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);
        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        tvSinHistorial = findViewById(R.id.tvSinHistorial);

        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Verificar rol
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String rol = prefs.getString("rol", "usuario");
        int usuarioId = prefs.getInt("usuarioId", -1);

        if (rol.equals("admin")) {
            Toast.makeText(this, "Sección solo para clientes", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar botones
        configurarBarraSuperior();

        // Cargar historial
        cargarHistorial(usuarioId);
    }

    private void cargarHistorial(int usuarioId) {
        ArrayList<Pedido> listaHistorial = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, fecha_pedido, total, estado FROM pedidos WHERE usuario_id=? ORDER BY fecha_pedido DESC",
                new String[]{String.valueOf(usuarioId)}
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String fecha = cursor.getString(1);
            double total = cursor.getDouble(2);
            String estado = cursor.getString(3);

            listaHistorial.add(new Pedido(id, "Pedido #" + id, fecha, total, estado));
        }

        cursor.close();
        db.close();

        if (listaHistorial.isEmpty()) {
            tvSinHistorial.setVisibility(TextView.VISIBLE);
            recyclerHistorial.setVisibility(RecyclerView.GONE);
        } else {
            tvSinHistorial.setVisibility(TextView.GONE);
            recyclerHistorial.setVisibility(RecyclerView.VISIBLE);
            PedidosAdapter adapter = new PedidosAdapter(listaHistorial);
            recyclerHistorial.setAdapter(adapter);
        }
    }

    private void configurarBarraSuperior() {
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnIconos.setOnClickListener(v ->
                Toast.makeText(this, "Ya estás en Historial", Toast.LENGTH_SHORT).show()
        );

        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class))
        );

        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, CarritoActivity.class))
        );
    }
}
