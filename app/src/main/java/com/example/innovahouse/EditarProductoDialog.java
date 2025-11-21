package com.example.innovahouse;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EditarProductoDialog extends DialogFragment {

    private int productoId;
    private String nombreActual, descripcionActual, imagenActual;
    private double precioActual;

    DBHelper dbHelper;

    ImageView imgPreview;

    Uri nuevaImagenUri = null;

    ActivityResultLauncher<Intent> selector;

    public EditarProductoDialog(int id, String nombre, double precio, String descripcion, String imagen) {
        this.productoId = id;
        this.nombreActual = nombre;
        this.precioActual = precio;
        this.descripcionActual = descripcion;
        this.imagenActual = imagen;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dbHelper = new DBHelper(requireContext());

        selector = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null &&
                            result.getData().getData() != null) {

                        nuevaImagenUri = result.getData().getData();
                        imgPreview.setImageURI(nuevaImagenUri);
                    }
                }
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar producto");

        final View view = requireActivity()
                .getLayoutInflater()
                .inflate(R.layout.dialog_editar_producto, null);
        builder.setView(view);

        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etPrecio = view.findViewById(R.id.etPrecio);
        EditText etDescripcion = view.findViewById(R.id.etDescripcion);

        imgPreview = view.findViewById(R.id.imgPreview);
        Button btnSeleccionar = view.findViewById(R.id.btnSeleccionarImagen);

        etNombre.setText(nombreActual);
        etPrecio.setText(String.valueOf(precioActual));
        etDescripcion.setText(descripcionActual);

        if (imagenActual.startsWith("content://")) {
            imgPreview.setImageURI(Uri.parse(imagenActual));
        } else {
            int resId = requireContext().getResources().getIdentifier(
                    imagenActual, "drawable", requireContext().getPackageName());

            imgPreview.setImageResource(resId != 0 ? resId : R.drawable.noimage);
        }

        btnSeleccionar.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            selector.launch(i);
        });

        builder.setPositiveButton("Guardar", (dialog, which) -> {

            String nombre = etNombre.getText().toString().trim();
            String precioTxt = etPrecio.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (nombre.isEmpty() || precioTxt.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio = Double.parseDouble(precioTxt);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("nombre", nombre);
            values.put("precio", precio);
            values.put("descripcion", descripcion);

            if (nuevaImagenUri != null)
                values.put("imagen", nuevaImagenUri.toString());

            db.update("productos", values, "id=?", new String[]{String.valueOf(productoId)});
            db.close();

            Toast.makeText(getContext(), "Producto actualizado", Toast.LENGTH_SHORT).show();

            if (getActivity() instanceof ProductoDetalleActivity) {
                ((ProductoDetalleActivity) getActivity()).refrescarProducto();
            }
        });

        builder.setNegativeButton("Cancelar", null);

        return builder.create();
    }
}
