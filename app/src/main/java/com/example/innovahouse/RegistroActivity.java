package com.example.innovahouse;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegistroActivity extends AppCompatActivity {

    EditText etApellido, etNombre, etCorreo, etUsuario, etClave;
    Button btnRegistrar, btnBuscar, btnEditar, btnEliminar;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = new DBHelper(this);

        etApellido = findViewById(R.id.et_apellido);
        etNombre = findViewById(R.id.et_nombre);
        etCorreo = findViewById(R.id.et_correo);
        etUsuario = findViewById(R.id.et_usuario);
        etClave = findViewById(R.id.et_clave);

        btnRegistrar = findViewById(R.id.btn_registrar);
        btnBuscar = findViewById(R.id.btn_buscar);
        btnEditar = findViewById(R.id.btn_editar);
        btnEliminar = findViewById(R.id.btn_eliminar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
        btnBuscar.setOnClickListener(v -> buscarUsuario());
        btnEditar.setOnClickListener(v -> editarUsuario());
        btnEliminar.setOnClickListener(v -> eliminarUsuario());

        // 🔹 Texto para volver al login
        TextView tvLogin = findViewById(R.id.tvLogin);
        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> {
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void registrarUsuario() {
        String apellido = etApellido.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String clave = etClave.getText().toString().trim();

        if (apellido.isEmpty() || nombre.isEmpty() || correo.isEmpty() || usuario.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("apellido", apellido);
        values.put("nombre", nombre);
        values.put("email", correo);
        values.put("usuario", usuario);
        values.put("clave", clave);

        long result = db.insert("usuarios", null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();

            // 🔹 Ir automáticamente al login
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            intent.putExtra("mensaje", "Usuario registrado correctamente");
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar. Es posible que el usuario ya exista.", Toast.LENGTH_LONG).show();
        }
    }

    private void buscarUsuario() {
        String usuario = etUsuario.getText().toString().trim();

        if (usuario.isEmpty()) {
            Toast.makeText(this, "Ingrese el usuario a buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario=?", new String[]{usuario});

        if (cursor.moveToFirst()) {
            etApellido.setText(cursor.getString(1));
            etNombre.setText(cursor.getString(2));
            etCorreo.setText(cursor.getString(3));
            etClave.setText(cursor.getString(5));
            Toast.makeText(this, "Usuario encontrado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    private void editarUsuario() {
        String usuario = etUsuario.getText().toString().trim();
        String clave = etClave.getText().toString().trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y clave", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("apellido", etApellido.getText().toString());
        values.put("nombre", etNombre.getText().toString());
        values.put("email", etCorreo.getText().toString());
        values.put("clave", clave);

        int filas = db.update("usuarios", values, "usuario=?", new String[]{usuario});
        db.close();

        if (filas > 0) {
            Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarUsuario() {
        String usuario = etUsuario.getText().toString().trim();

        if (usuario.isEmpty()) {
            Toast.makeText(this, "Ingrese el usuario a eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete("usuarios", "usuario=?", new String[]{usuario});
        db.close();

        if (filas > 0) {
            Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        etApellido.setText("");
        etNombre.setText("");
        etCorreo.setText("");
        etUsuario.setText("");
        etClave.setText("");
    }
}