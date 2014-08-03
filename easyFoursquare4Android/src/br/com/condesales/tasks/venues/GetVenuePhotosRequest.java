package br.com.condesales.tasks.venues;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.condesales.constants.FoursquareConstants;
import br.com.condesales.listeners.VenuePhotosListener;
import br.com.condesales.models.PhotoItem;
import br.com.condesales.models.PhotosGroup;
import br.com.condesales.models.Venue;

/**
 * Created by dionysis_lorentzos on 2/8/14.
 * All rights reserved by the Author.
 * Use with your own responsibility.
 */

public class GetVenuePhotosRequest extends AsyncTask<String, Integer, PhotosGroup> {

    private VenuePhotosListener mListener;
    private String mVenueID;

    public GetVenuePhotosRequest(Activity activity, VenuePhotosListener listener, String venueID) {
        mListener = listener;
        mVenueID = venueID;
    }


    @Override
    protected PhotosGroup doInBackground(String... params) {

        String access_token = params[0];
        ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();
        PhotosGroup photosGroup = new PhotosGroup();

        try {

            //date required
            String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
            // Call Foursquare to get the Venues around
            String uri = "https://api.foursquare.com/v2/venues/" + mVenueID
                    + "/photos?v="
                    + apiDateVersion;
            if (!access_token.equals("")) {
                uri = uri + "&oauth_token=" + access_token;
            } else {
                uri = uri + "&client_id=" + FoursquareConstants.CLIENT_ID + "&client_secret=" + FoursquareConstants.CLIENT_SECRET;
            }

            JSONObject photosJson = executeHttpGet(uri);

            // Get return code
            int returnCode = Integer.parseInt(photosJson.getJSONObject("meta")
                    .getString("code"));
            // 200 = OK
            if (returnCode == 200) {
                Gson gson = new Gson();
                JSONObject json = photosJson.getJSONObject("response").getJSONObject("photos");
                photosGroup = gson.fromJson(json.toString(), PhotosGroup.class);
            } else {
                if (mListener != null)
                    mListener.onError(photosJson.getJSONObject("meta").getString("errorDetail"));
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            if (mListener != null)
                mListener.onError(exp.toString());
        }

        return photosGroup;
    }


    @Override
    protected void onPostExecute(PhotosGroup photosGroup) {
        if (mListener != null)
            mListener.onGotVenuePhotos(photosGroup);
        super.onPostExecute(photosGroup);
    }


    // Calls a URI and returns the answer as a JSON object
    private JSONObject executeHttpGet(String uri) throws Exception {
        HttpGet req = new HttpGet(uri);

        HttpClient client = new DefaultHttpClient();
        HttpResponse resLogin = client.execute(req);
        BufferedReader r = new BufferedReader(new InputStreamReader(resLogin
                .getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = r.readLine()) != null) {
            sb.append(s);
        }

        return new JSONObject(sb.toString());
    }

}
