package com.sub48.climaactual;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends ListActivity implements ConsultaClimaAsync.ConsultaClimaRespuesta, ObtenerIconoAsync.ObtenerIconoRespuesta {
    public static String INTENT_NOMBRE_CIUDAD_A_AGREGAR = "INTENT_CIUDAD";
    private int AGREGAR_CIUDAD_REQUEST_CODE = 1234;
    private CiudadController mCiudadController;
    private ConfiguracionController mConfiguracionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView mListView = findViewById(android.R.id.list);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.ciudadesLayout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                for (Ciudad ciudad: mCiudadController.getCiudades()) {
                    refrescarClima(ciudad);
                }

                pullToRefresh.setRefreshing(false);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ciudad ciudad = mCiudadController.obtener(position);
                refrescarClima(ciudad);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Ciudad ciudad = mCiudadController.obtener(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.titulo_alerta_borrar)
                        .setMessage(getString(R.string.pregunta_mensaje_borrar, ciudad.getNombre()))
                        .setPositiveButton(R.string.texto_boton_borrar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCiudadController.borrar(ciudad.getNombre());
                                mCiudadController.grabar();
                                mCiudadController.refrescar();
                            }
                        })
                        .setNegativeButton(R.string.texto_boton_cancelar, null)
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .show();

                return true;
            }
        });

        mConfiguracionController = new ConfiguracionController();
        mCiudadController = new CiudadController(this);

        if (savedInstanceState != null) {
            mConfiguracionController.cargar(savedInstanceState);
            mCiudadController.cargar(savedInstanceState);
        }
        else {
            mConfiguracionController.cargar(this);
            mCiudadController.cargar();
        }

        mListView.setAdapter(mCiudadController.getAdapter());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mCiudadController.grabar(outState);
        mConfiguracionController.grabar(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        switch (mConfiguracionController.getConfiguration().getUnidad()) {
            case Celsius:
                menu.findItem(R.id.MenuUnidadCelsius).setChecked(true);
                break;

            case Fahrenheit:
                menu.findItem(R.id.MenuUnidadFahrenheit).setChecked(true);
                break;

            default: // case Kelvin:
                menu.findItem(R.id.MenuUnidadKelvin).setChecked(true);
                break;
        }

        switch (mConfiguracionController.getConfiguration().getLenguaje()) {
            case Castellano:
                menu.findItem(R.id.MenuCastellano).setChecked(true);
                break;

            default: // case Ingles:
                menu.findItem(R.id.MenuIngles).setChecked(true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.MenuAgregarCiudad:
                agregarCiudad();
                break;

            case R.id.MenuUnidadCelsius:
                mConfiguracionController.getConfiguration().setUnidad(Configuracion.Unidad.Celsius);
                break;

            case R.id.MenuUnidadFahrenheit:
                mConfiguracionController.getConfiguration().setUnidad(Configuracion.Unidad.Fahrenheit);
                break;

            case R.id.MenuUnidadKelvin:
                mConfiguracionController.getConfiguration().setUnidad(Configuracion.Unidad.Kelvin);
                break;

            case R.id.MenuCastellano:
                mConfiguracionController.getConfiguration().setLenguaje(Configuracion.Lenguaje.Castellano);
                break;

            case R.id.MenuIngles:
                mConfiguracionController.getConfiguration().setLenguaje(Configuracion.Lenguaje.Ingles);
                break;
        }

        if (id != R.id.MenuAgregarCiudad && !item.isChecked()) {
            item.setChecked(true);
            mConfiguracionController.grabar(this);

            if (mCiudadController.getCiudades().size() > 0) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.titulo_alerta_refrescar)
                        .setMessage(R.string.pregunta_mensaje_refrescar)
                        .setPositiveButton(R.string.texto_boton_refrescar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (Ciudad ciudad : mCiudadController.getCiudades()) {
                                    if (ciudad.getUltimaActualizacion() != null) {
                                        refrescarClima(ciudad);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.texto_boton_cancelar, null)
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == AGREGAR_CIUDAD_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            String ciudad = data.getStringExtra(INTENT_NOMBRE_CIUDAD_A_AGREGAR);

            if (MainActivity.esCadenaValida(ciudad)) {
                if (mCiudadController.agregar(ciudad)) {
                    mCiudadController.grabar();
                    mCiudadController.refrescar();
                }
                else {
                    SwipeRefreshLayout layout = findViewById(R.id.ciudadesLayout);
                    Snackbar snackbar = Snackbar.make(layout, R.string.texto_alerta_ya_existe, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.texto_boton_reintentar, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            agregarCiudad();
                        }
                    });
                    snackbar.show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void doRespuesta(@NonNull Ciudad ciudad, @NonNull Configuracion configuracion, String s) {
        try {
            if (esCadenaValida(s)) {
                JSONObject jsonObject = new JSONObject(s);
                // Solo el código 200 es una respuesta válida
                if (jsonObject.getInt("cod") == 200) {
                    ciudad.setClima(s, mConfiguracionController.getConfiguration().getUnidad());
                    mCiudadController.refrescar();

                    Toast.makeText(this, String.format(Locale.getDefault(),
                            "%s: %s (%d %s, Hum. %d%%)",
                            ciudad.getNombre(),
                            ciudad.getDescripcion(),
                            ciudad.getTemperatura(),
                            ciudad.getAbreviaturaUnidad(),
                            ciudad.getHumedad()),
                            Toast.LENGTH_LONG)
                            .show();

                    ObtenerIconoAsync obtenerIconoAsync = new ObtenerIconoAsync(ciudad, this);
                    obtenerIconoAsync.execute();
                }
                else {
                    String message = jsonObject.getString("message");
                    if (MainActivity.esCadenaValida(message)) {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(this, R.string.texto_respuesta_vacia, Toast.LENGTH_LONG).show();
                    }
                }
            }
            else {
                Toast.makeText(this, R.string.texto_respuesta_vacia, Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void doRespuesta(@NonNull Ciudad ciudad, Bitmap bitmap) {
        if (bitmap != null) {
            ciudad.setIcono(bitmap);
            mCiudadController.refrescar();
        }
    }

    /**
     * Consulta el clima de la ciudad especificada
     * @param ciudad La ciudad a refrescar
     */
    private void refrescarClima(@NonNull Ciudad ciudad) {
        ConsultaClimaAsync consultaClimaAsync = new ConsultaClimaAsync(ciudad, mConfiguracionController.getConfiguration(), this);
        consultaClimaAsync.execute();
    }

    /**
     * Levanta la actividad para agregar ciudades.
     */
    private void agregarCiudad() {
        Intent intent = new Intent(this, NuevaCiudadActivity.class);
        startActivityForResult(intent, AGREGAR_CIUDAD_REQUEST_CODE);
    }

    /**
     * Verifica que el String es válido, no es nulo ni está vació
     * @param value El String a verificar
     * @return true si es válido.
     */
    public static boolean esCadenaValida(String value) {
        return value != null && ! "".equals(value);
    }
}

