package com.sub48.climaactual;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

class ConsultaClimaAsync extends AsyncTask<Void, Void, String> {
    interface ConsultaClimaRespuesta {
        void doRespuesta(@NonNull Ciudad ciudad, @NonNull Configuracion configuracion, String s);
    }

    private Ciudad mCiudad;
    private Configuracion mConfiguracion;
    private ConsultaClimaRespuesta mConsultaClimaRespuesta;

    ConsultaClimaAsync(@NonNull Ciudad ciudad, @NonNull Configuracion configuracion, @NonNull ConsultaClimaRespuesta consultaClimaRespuesta) {
        mCiudad = ciudad;
        mConfiguracion = configuracion;
        mConsultaClimaRespuesta = consultaClimaRespuesta;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String urlText = getUrl(mCiudad);
        String jsonText = "";

        try {
            URL url = new URL(urlText);
            InputStream inputStream = url.openStream();
            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                inputStream.close();
                jsonText = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return jsonText;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            mConsultaClimaRespuesta.doRespuesta(mCiudad, mConfiguracion, s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea la URL del query para consultar el clima
     *
     * @param ciudad La ciudad a buscar ya sea por nombre o coordenadas
     * @return La URL a usar
     */
    private String getUrl(@NonNull Ciudad ciudad) {
        String unidad = mConfiguracion.getUnidad() == Configuracion.Unidad.Celsius? "metric" :
                mConfiguracion.getUnidad() == Configuracion.Unidad.Fahrenheit? "imperial" :
                        "";

        String lenguaje = mConfiguracion.getLenguaje() == Configuracion.Lenguaje.Castellano? "es" :
                mConfiguracion.getLenguaje() == Configuracion.Lenguaje.Ingles? "en" : "";


        Uri resultado = Uri.parse(mConfiguracion.getApiUrl()).buildUpon()
                .appendQueryParameter("appid", mConfiguracion.getApiKey())
                .appendQueryParameter("q", ciudad.getNombre())
                .appendQueryParameter("units", unidad)
                .appendQueryParameter("lang", lenguaje)
                .build();

        return resultado.toString();
    }
}
