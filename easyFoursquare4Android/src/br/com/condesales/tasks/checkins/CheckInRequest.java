package br.com.condesales.tasks.checkins;

import android.location.Location;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.com.condesales.constants.FoursquareConstants;
import br.com.condesales.criterias.CheckInCriteria;
import br.com.condesales.listeners.RequestListener;
import br.com.condesales.models.Checkin;
import br.com.condesales.models.FoursquareError;

public class CheckInRequest extends AsyncTask<String, Integer, Checkin> {

    private RequestListener<Checkin> mListener;
    private CheckInCriteria mCriteria;
    private FoursquareError foursquareError;

    /**
     * Async constructor
     *
     * @param listener the listener where the async request should respond to
     * @param criteria the object containing all the params from check in
     */
    public CheckInRequest(RequestListener<Checkin> listener,
                          CheckInCriteria criteria) {
        mListener = listener;
        mCriteria = criteria;
    }

    /**
     * Sync constructor
     *
     * @param criteria the object containing all the params from check in
     */
    public CheckInRequest(CheckInCriteria criteria) {
        mCriteria = criteria;
    }

    @Override
    protected Checkin doInBackground(String... params) {

        String access_token = params[0];
        Checkin checkin = null;
        Gson gson = new Gson();
        try {
            // Call Foursquare to post checkin
            JSONObject venuesJson = executeHttpPost(
                    FoursquareConstants.CHECKINS_ADD, access_token, mCriteria);

            // Get return code
            int returnCode = Integer.parseInt(venuesJson.getJSONObject("meta")
                    .getString("code"));
            // 200 = OK
            if (returnCode == HttpStatus.SC_OK) {

                JSONObject json = venuesJson.getJSONObject("response").getJSONObject("checkin");
                checkin = gson.fromJson(json.toString(), Checkin.class);
            } else {
                String errorString = venuesJson.getJSONObject("meta")
                        .getString("errorDetail");
                foursquareError = gson.fromJson(errorString, FoursquareError.class);
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            foursquareError = new FoursquareError();
            foursquareError.setErrorDetail(exp.getMessage());
        }
        return checkin;
    }

    @Override
    protected void onPostExecute(Checkin checkin) {
        if (mListener != null)
            if (foursquareError == null) {
                mListener.onSuccess(checkin);
            } else {
                mListener.onError(foursquareError);
            }
        super.onPostExecute(checkin);
    }

    /**
     * Calls a URI and returns the answer as a JSON object
     *
     * @param uri         the uri to request
     * @param accessToken the access token
     * @param criteria    the object that contains all the params
     * @return JSONObject containing the result
     * @throws Exception some exceptions :P
     */
    private JSONObject executeHttpPost(String uri, String accessToken,
                                       CheckInCriteria criteria) throws Exception {
        HttpPost req = new HttpPost(uri);
        HttpClient client = new DefaultHttpClient();

        //date required
        String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
        // create the params lists, an add some info on it..
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("v", apiDateVersion));
        params.add(new BasicNameValuePair("venueId", criteria.getVenueId()));
        if (criteria.getEventId() != null && !criteria.getEventId().equals(""))
            params.add(new BasicNameValuePair("eventId", criteria.getEventId()));
        // only shout if you have something...
        if (criteria.getShout() != null && !criteria.getShout().equals(""))
            params.add(new BasicNameValuePair("shout", criteria.getShout()));
        params.add(new BasicNameValuePair("broadcast", criteria.getBroadcast()
                .getType()));
        // just send location when you have it...
        if (criteria.getLocation() != null) {
            Location location = criteria.getLocation();
            params.add(new BasicNameValuePair("ll", location.getLatitude()
                    + "," + location.getLongitude()));
            params.add(new BasicNameValuePair("llAcc", location.getAccuracy()
                    + ""));
        }
        params.add(new BasicNameValuePair("oauth_token", accessToken));
        // putting the params to the post request
        req.setEntity(new UrlEncodedFormEntity(params));

        HttpResponse resCheckin = client.execute(req);
        BufferedReader r = new BufferedReader(new InputStreamReader(resCheckin
                .getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = r.readLine()) != null) {
            sb.append(s);
        }
        return new JSONObject(sb.toString());
    }
}
