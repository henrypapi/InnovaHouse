package com.example.innovahouse;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    TextView tvNombre, tvCorreo, tvRol, tvTelefono;
    Button btnCerrarSesion, btnEditar, btnEliminar;
    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;
    String rol, email;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        dbHelper = new DBHelper(this);

        // DATOS DE SESIÓN
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String nombre = prefs.getString("userName", "Usuario");
        String apellido = prefs.getString("apellido", "");
        email = prefs.getString("userEmail", "");
        rol = prefs.getString("rol", "usuario");
        
        // Obtener teléfono de la base de datos
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursorTel = db.rawQuery("SELECT telefono FROM usuarios WHERE email=?", new String[]{email});
        String telefono = "";
        if (cursorTel.moveToFirst()) {
            telefono = cursorTel.getString(0);
        }
        cursorTel.close();
        db.close();

        // UI
        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        tvRol = findViewById(R.id.tvRol);
        tvTelefono = findViewById(R.id.tvTelefono);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);

        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);

        // Colocar datos reales
        tvNombre.setText(nombre + " " + apellido);
        tvCorreo.setText(email);
        tvRol.setText("Rol: " + (rol.equals("admin") ? "Administrador" : "Cliente"));
        tvTelefono.setText("Teléfono: " + telefono);

        // Configurar botones según rol
        if (rol.equals("admin")) {
            btnEditar.setText("Editar Datos de Admin");
            btnEliminar.setText("Eliminar Cliente");
            btnEliminar.setVisibility(Button.VISIBLE);
        } else {
            btnEditar.setText("Editar Mis Datos");
            btnEliminar.setVisibility(Button.GONE);
        }

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        // Editar perfil
        btnEditar.setOnClickListener(v -> abrirDialogoEditar());

        // Eliminar cliente (solo admin)
        if (rol.equals("admin")) {
            btnEliminar.setOnClickListener(v -> abrirDialogoEliminarCliente());
        }

        // BOTONES SUPERIORES
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        btnIconos.setOnClickListener(v ->
                Toast.makeText(this, "Ya estás en Perfil", Toast.LENGTH_SHORT).show()
        );

        btnPerfil.setOnClickListener(v -> {}); // ya está en perfil

        btnCarrito.setOnClickListener(v -> {
            if (rol.equals("admin")) {
                startActivity(new Intent(this, AgregarProductoActivity.class));
            } else {
                startActivity(new Intent(this, CarritoActivity.class));
            }
        });
    }

    private void abrirDialogoEditar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(rol.equals("admin") ? "Editar Datos de Administrador" : "Editar Mis Datos");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Correo
        EditText etCorreo = new EditText(this);
        etCorreo.setHint("Correo electrónico");
        etCorreo.setText(email);
        etCorreo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etCorreo.setPadding(12, 12, 12, 12);
        layout.addView(etCorreo);

        // Contraseña actual (solo si no es admin o si quiere cambiar)
        EditText etClaveActual = new EditText(this);
        etClaveActual.setHint("Contraseña actual");
        etClaveActual.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etClaveActual.setPadding(12, 12, 12, 12);
        layout.addView(etClaveActual);

        // Nueva contraseña
        EditText etClaveNueva = new EditText(this);
        etClaveNueva.setHint("Nueva contraseña (dejar en blanco para no cambiar)");
        etClaveNueva.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etClaveNueva.setPadding(12, 12, 12, 12);
        layout.addView(etClaveNueva);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoCorreo = etCorreo.getText().toString().trim();
            String claveActual = etClaveActual.getText().toString().trim();
            String claveNueva = etClaveNueva.getText().toString().trim();

            if (nuevoCorreo.isEmpty()) {
                Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (claveActual.isEmpty()) {
                Toast.makeText(this, "Ingresa tu contraseña actual para confirmar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar contraseña actual
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT clave FROM usuarios WHERE email=?",
                    new String[]{email}
            );

            if (!cursor.moveToFirst()) {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                cursor.close();
                db.close();
                return;
            }

            String claveGuardada = cursor.getString(0);
            cursor.close();
            db.close();

            if (!claveGuardada.equals(claveActual)) {
                Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualizar
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("email", nuevoCorreo);

            if (!claveNueva.isEmpty()) {
                if (claveNueva.length() < 6) {
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    db.close();
                    return;
                }
                values.put("clave", claveNueva);
            }

            int filas = db.update("usuarios", values, "email=?", new String[]{email});
            db.close();

            if (filas > 0) {
                // Actualizar SharedPreferences
                SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userEmail", nuevoCorreo);
                editor.apply();

                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            } else {
                Toast.makeText(this, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void abrirDialogoEliminarCliente() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Cliente");

        EditText etEmailCliente = new EditText(this);
        etEmailCliente.setHint("Correo del cliente a eliminar");
        etEmailCliente.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etEmailCliente.setPadding(16, 16, 16, 16);

        builder.setView(etEmailCliente);

        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            String emailCliente = etEmailCliente.getText().toString().trim();

            if (emailCliente.isEmpty()) {
                Toast.makeText(this, "Ingresa un correo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar que no intente eliminarse a sí mismo
            if (emailCliente.equals(email)) {
                Toast.makeText(this, "No puedes eliminar tu propia cuenta desde aquí", Toast.LENGTH_SHORT).show();
                return;
            }

            // Confirmar eliminación
            AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
            confirmBuilder.setTitle("Confirmar eliminación");
            confirmBuilder.setMessage("¿Estás seguro de que deseas eliminar a este cliente?");
            confirmBuilder.setPositiveButton("Sí, eliminar", (dialog2, which2) -> {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int filas = db.delete("usuarios", "email=? AND rol!=?", new String[]{emailCliente, "admin"});
                db.close();

                if (filas > 0) {
                    Toast.makeText(this, "Cliente eliminado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cliente no encontrado o es administrador", Toast.LENGTH_SHORT).show();
                }
            });
            confirmBuilder.setNegativeButton("Cancelar", (dialog2, which2) -> dialog2.cancel());
            confirmBuilder.show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
