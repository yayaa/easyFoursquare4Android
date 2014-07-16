package br.com.condesales.tasks.users;

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
import br.com.condesales.listeners.RequestListener;
import br.com.condesales.models.FoursquareError;
import br.com.condesales.models.Venues;

public class GetUserVenuesHistoryRequest extends AsyncTask<String, Integer, ArrayList<Venues>> {

    private RequestListener<ArrayList<Venues>> mListener;
    private String mUserID = "self";// default value
    private FoursquareError foursquareError;

    /**
     * Async constructor (userID gonna be self)
     *
     * @param listener
     */
    public GetUserVenuesHistoryRequest(RequestListener<ArrayList<Venues>> listener) {
        mListener = listener;
    }

    /**
     * Async constructor
     *
     * @param listener the listener where the async request shoud respont to
     * @param userID
     */
    public GetUserVenuesHistoryRequest(String userID, RequestListener<ArrayList<Venues>> listener) {
        mListener = listener;
        mUserID = userID;
    }

    /**
     * Sync constructor (userID gonna be self)
     */
    public GetUserVenuesHistoryRequest() {
    }

    /**
     * Sync constructor
     *
     * @param userID The id from user to get information
     */
    public GetUserVenuesHistoryRequest(String userID) {
        mUserID = userID;
    }

    @Override
    protected ArrayList<Venues> doInBackground(String... params) {

        String access_token = params[0];
        Venues venue = null;
        ArrayList<Venues> list = new ArrayList<Venues>();
        try {
            // date required
            String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
            // Call Foursquare to post checkin
            JSONObject venuesJson = executeHttpGet("https://api.foursquare.com/v2/users/"
                    + mUserID
                    + "/venuehistory"
                    + "?v="
                    + apiDateVersion
                    + "&oauth_token=" + access_token);
            Gson gson = new Gson();

            // Get return code
            int returnCode = Integer.parseInt(venuesJson.getJSONObject("meta")
                    .getString("code"));
            // 200 = OK
            if (returnCode == HttpStatus.SC_OK) {

                JSONArray json = venuesJson.getJSONObject("response")
                        .getJSONObject("venues").getJSONArray("items");
                for (int i = 0; i < json.length(); i++) {
                    venue = gson.fromJson(json.get(i).toString(),
                            Venues.class);
                    list.add(venue);
                }
            } else {
                foursquareError = gson.fromJson(venuesJson.getJSONObject("meta").toString(), FoursquareError.class);
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            foursquareError = new FoursquareError();
            foursquareError.setErrorDetail(exp.getMessage());
        }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<Venues> friendsList) {
        if (mListener != null)
            if (foursquareError == null) {
                mListener.onSuccess(friendsList);
            } else {
                mListener.onError(foursquareError);
            }
        super.onPostExecute(friendsList);
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
