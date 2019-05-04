package com.sub48.climaactual;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NuevaCiudadActivity extends Activity {
    public void onAgregarPorNombre(View view) {
        EditText editText = findViewById(R.id.nombreEditText);
        String ciudad = editText.getText().toString().trim();

        if (MainActivity.esCadenaValida(ciudad)) {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.INTENT_NOMBRE_CIUDAD_A_AGREGAR, ciudad);
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            Toast.makeText(this, R.string.texto_alerta_nombre_vacio, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_ciudad);
        setTitle(R.string.menu_agregar_ciudad);
    }
}
