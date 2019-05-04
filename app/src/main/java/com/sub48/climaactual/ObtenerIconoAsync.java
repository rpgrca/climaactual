package com.sub48.climaactual;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.InputStream;
import java.net.URL;

class ObtenerIconoAsync extends AsyncTask<Void, Void, Bitmap> {
    interface ObtenerIconoRespuesta {
        void doRespuesta(@NonNull Ciudad ciudad, Bitmap bitmap);
    }

    private Ciudad mCiudad;
    private ObtenerIconoRespuesta mObtenerIconoRespuesta;

    ObtenerIconoAsync(@NonNull Ciudad ciudad, @NonNull ObtenerIconoRespuesta obtenerIconoRespuesta) {
        mCiudad = ciudad;
        mObtenerIconoRespuesta = obtenerIconoRespuesta;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        String urlText = mCiudad.getIconoUrl();
        Bitmap icon = null;

        try {
            URL url = new URL(urlText);
            InputStream inputStream = url.openStream();
            if (inputStream != null) {
                icon = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return icon;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        try {
            mObtenerIconoRespuesta.doRespuesta(mCiudad, bitmap);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
