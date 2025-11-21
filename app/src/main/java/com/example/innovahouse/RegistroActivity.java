package com.example.innovahouse;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    EditText etNombre, etApellido, etTelefono, etCorreo, etClave;
    Button btnRegistrar, btnCancelar;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = new DBHelper(this);

        etNombre = findViewById(R.id.et_nombre);
        etApellido = findViewById(R.id.et_apellido);
        etTelefono = findViewById(R.id.et_telefono);
        etCorreo = findViewById(R.id.et_correo);
        etClave = findViewById(R.id.et_clave);

        btnRegistrar = findViewById(R.id.btn_registrar);
        btnCancelar = findViewById(R.id.btn_cancelar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
        btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Texto para volver al login
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
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String clave = etClave.getText().toString().trim();

        // Validaciones
        if (!validarCampos(nombre, apellido, telefono, correo, clave)) {
            return;
        }

        // Verificar si el email ya existe
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE email=?", new String[]{correo});
        
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_LONG).show();
            cursor.close();
            db.close();
            return;
        }
        cursor.close();
        db.close();

        // Guardar usuario
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("apellido", apellido);
        values.put("email", correo);
        values.put("usuario", correo); // Usar email como usuario
        values.put("clave", clave);
        values.put("rol", "usuario"); // Por defecto usuario, no admin
        values.put("telefono", telefono);

        long result = db.insert("usuarios", null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, "¡Registro exitoso! Inicia sesión", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            intent.putExtra("mensaje", "Cuenta creada correctamente");
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar. Intenta nuevamente.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarCampos(String nombre, String apellido, String telefono, String correo, String clave) {
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("El nombre es requerido");
            etNombre.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError("El apellido es requerido");
            etApellido.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError("El teléfono es requerido");
            etTelefono.requestFocus();
            return false;
        }

        if (telefono.length() < 9) {
            etTelefono.setError("El teléfono debe tener al menos 9 dígitos");
            etTelefono.requestFocus();
            return false;
        }

        if (!telefono.matches("\\d+")) {
            etTelefono.setError("El teléfono solo debe contener números");
            etTelefono.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(correo)) {
            etCorreo.setError("El correo es requerido");
            etCorreo.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Ingresa un correo válido");
            etCorreo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(clave)) {
            etClave.setError("La contraseña es requerida");
            etClave.requestFocus();
            return false;
        }

        if (clave.length() < 6) {
            etClave.setError("La contraseña debe tener al menos 6 caracteres");
            etClave.requestFocus();
            return false;
        }

        return true;
    }
}