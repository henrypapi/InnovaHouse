package com.example.innovahouse;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_menu);

        // 🔹 Mostrar mensaje si viene desde registro
        String mensaje = getIntent().getStringExtra("mensaje");
        if (mensaje != null) {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.contraseña);
        btnLogin = findViewById(R.id.btnIniciar);
        dbHelper = new DBHelper(this);

        // 🔹 Crear cuenta admin si no existe
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            db.execSQL("INSERT OR IGNORE INTO usuarios (email, clave, nombre, apellido) VALUES ('admin@innova.com', '1234', 'Admin', 'Sistema')");
        }

        btnLogin.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPass = password.getText().toString().trim();

            if (userEmail.isEmpty() || userPass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE email=? AND clave=?",
                    new String[]{userEmail, userPass});

            if (cursor.moveToFirst()) {
                String nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));

                // 🔹 Mensaje de bienvenida
                if (nombreUsuario.equalsIgnoreCase("Admin")) {
                    Toast.makeText(this, "Bienvenido Administrador", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Bienvenido, " + nombreUsuario, Toast.LENGTH_SHORT).show();
                }

                // 🔹 Enviar datos al Dashboard
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("userEmail", userEmail);
                i.putExtra("userName", nombreUsuario);
                startActivity(i);
                finish(); // evita volver al login
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
            db.close();
        });

        // 🔹 Texto para ir al registro
        TextView tvRegistro = findViewById(R.id.tvRegistro);
        tvRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

}
