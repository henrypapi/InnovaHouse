package com.example.innovahouse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    RadioGroup rgMetodoPago;
    TextView tvTotal;
    Button btnConfirmarPago, btnCancelarPago;
    double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        rgMetodoPago = findViewById(R.id.rgMetodoPago);
        tvTotal = findViewById(R.id.tvTotal);
        btnConfirmarPago = findViewById(R.id.btnConfirmarPago);
        btnCancelarPago = findViewById(R.id.btnCancelarPago);

        // Obtener total del intent
        total = getIntent().getDoubleExtra("total", 0.0);
        tvTotal.setText("Total a pagar: S/. " + String.format("%.2f", total));

        btnConfirmarPago.setOnClickListener(v -> {
            int selectedId = rgMetodoPago.getCheckedRadioButtonId();
            
            if (selectedId == -1) {
                Toast.makeText(this, "Selecciona un método de pago", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton rbSeleccionado = findViewById(selectedId);
            String metodo = rbSeleccionado.getText().toString();

            Toast.makeText(this, "Procesando pago con " + metodo, Toast.LENGTH_SHORT).show();

            // Aquí iría la lógica de procesamiento del pago
            // Por ahora, simplemente guardamos el pedido

            Intent resultIntent = new Intent();
            resultIntent.putExtra("metodoPago", metodo);
            resultIntent.putExtra("total", total);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnCancelarPago.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
