package br.com.condesales.tasks.venues;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.condesales.constants.FoursquareConstants;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.RequestListener;
import br.com.condesales.models.FoursquareError;
import br.com.condesales.models.Venue;

public class FoursquareVenuesNearbyRequest extends AsyncTask<String, Integer, ArrayList<Venue>> {

    private RequestListener<ArrayList<Venue>> mListener;
    private VenuesCriteria mCriteria;
    private boolean sslExp;
    private FoursquareError foursquareError;

    public FoursquareVenuesNearbyRequest(VenuesCriteria criteria, RequestListener<ArrayList<Venue>> listener) {
        mListener = listener;
        mCriteria = criteria;
    }

    public FoursquareVenuesNearbyRequest(VenuesCriteria criteria) {
        mCriteria = criteria;
    }

    @Override
    protected ArrayList<Venue> doInBackground(String... params) {

        String access_token = params[0];
        ArrayList<Venue> venues = new ArrayList<Venue>();

        try {

            //date required
            String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
            // Call Foursquare to get the Venues around
            String uri = "https://api.foursquare.com/v2/venues/search"
                    + "?v="
                    + apiDateVersion
                    + "&ll="
                    + mCriteria.getLocation().getLatitude()
                    + ","
                    + mCriteria.getLocation().getLongitude()
                    + "&llAcc="
                    + mCriteria.getLocation().getAccuracy()
                    + "&query="
                    + mCriteria.getQuery()
                    + "&limit="
                    + mCriteria.getQuantity()
                    + "&intent="
                    + mCriteria.getIntent().getValue()
                    + "&radius="
                    + mCriteria.getRadius();
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
                JSONArray json = venuesJson.getJSONObject("response")
                        .getJSONArray("venues");
                for (int i = 0; i < json.length(); i++) {
                    Venue venue = gson.fromJson(json.getJSONObject(i)
                            .toString(), Venue.class);
                    venues.add(venue);
                }
            } else {
                foursquareError = gson.fromJson(venuesJson.getJSONObject("meta").toString(), FoursquareError.class);
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            foursquareError = new FoursquareError();
            foursquareError.setErrorDetail(exp.getMessage());
        }
        return venues;
    }

    @Override
    protected void onPostExecute(ArrayList<Venue> venues) {
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
