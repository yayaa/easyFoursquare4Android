package br.com.condesales.tasks.venues;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import br.com.condesales.constants.FoursquareConstants;
import br.com.condesales.listeners.RequestListener;
import br.com.condesales.models.FoursquareError;
import br.com.condesales.models.Venue;

public class FoursquareVenueDetailsRequest extends AsyncTask<String, Integer, Venue> {

    private RequestListener<Venue> mListener;
    private String mVenueID;
    private FoursquareError foursquareError;

    public FoursquareVenueDetailsRequest(RequestListener<Venue> listener, String venueID) {
        mListener = listener;
        mVenueID = venueID;
    }

    public FoursquareVenueDetailsRequest(String venueID) {
        mVenueID = venueID;
    }


    @Override
    protected Venue doInBackground(String... params) {

        String access_token = params[0];
        Venue venue = null;

        try {

            //date required
            String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
            // Call Foursquare to get the Venues around
            String uri = "https://api.foursquare.com/v2/venues/" + mVenueID
                    + "?v="
                    + apiDateVersion;
            if (!access_token.equals("")) {
                uri = uri + "&oauth_token=" + access_token;
            } else {
                uri = uri + "&client_id=" + FoursquareConstants.CLIENT_ID + "&client_secret=" + FoursquareConstants.CLIENT_SECRET;
            }

            JSONObject venuesJson = executeHttpGet(uri);
            Gson gson = new Gson();

            // Get return code
            int returnCode = Integer.parseInt(venuesJson.getJSONObject("meta")
                    .getString("code"));
            // 200 = OK
            if (returnCode == HttpStatus.SC_OK) {
                JSONObject json = venuesJson.getJSONObject("response")
                        .getJSONObject("venue");
                venue = gson.fromJson(json.toString(), Venue.class);
            } else {
                foursquareError = gson.fromJson(venuesJson.getJSONObject("meta").toString(), FoursquareError.class);
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            foursquareError = new FoursquareError();
            foursquareError.setErrorDetail(exp.getMessage());
        }
        return venue;
    }

    @Override
    protected void onPostExecute(Venue venues) {
        if (mListener != null)
            if (foursquareError == null) {
                mListener.onSuccess(venues);
            } else {
                mListener.onError(foursquareError);
            }
        super.onPostExecute(venues);
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
