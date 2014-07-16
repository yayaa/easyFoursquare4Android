package br.com.condesales.tasks.users;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import br.com.condesales.listeners.RequestListener;
import br.com.condesales.models.FoursquareError;

public class UserImageRequest extends AsyncTask<String, Integer, Bitmap> {

    private final String FILE_NAME = "foursquareUser";
    private RequestListener<Bitmap> mListener;
    private FoursquareError foursquareError;

    public UserImageRequest(RequestListener<Bitmap> listener) {
        mListener = listener;
    }

    public UserImageRequest() {
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        String userPhoto = params[0];
        Bitmap bmp = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URL url = new URL(userPhoto); // you can write here any link
            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();
            /*
             * Define InputStreams to read from the URLConnection.
			 */
            is = ucon.getInputStream();
            bis = new BufferedInputStream(is);
            bmp = BitmapFactory.decodeStream(bis);
        } catch (Exception exp) {
            exp.printStackTrace();
            foursquareError = new FoursquareError();
            foursquareError.setErrorDetail(exp.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
        if (mListener != null)
            if (foursquareError == null) {
                mListener.onSuccess(bmp);
            } else {
                mListener.onError(foursquareError);
            }
        super.onPostExecute(bmp);
    }
}
