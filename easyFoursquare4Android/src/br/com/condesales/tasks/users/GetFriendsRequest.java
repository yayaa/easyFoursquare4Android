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
import br.com.condesales.models.User;

public class GetFriendsRequest extends AsyncTask<String, Integer, ArrayList<User>> {

    private RequestListener<ArrayList<User>> mListener;
    private String mUserID = "self";// default value
    private FoursquareError foursquareError;

    /**
     * Async constructor (userID gonna be self)
     *
     * @param listener the listener where the async request shoud respont to
     */
    public GetFriendsRequest(RequestListener<ArrayList<User>> listener) {
        mListener = listener;
    }

    /**
     * Async constructor
     *
     * @param userID   The id from user to get information
     * @param listener the listener where the async request shoud respont to
     */
    public GetFriendsRequest(
            String userID, RequestListener<ArrayList<User>> listener) {
        mListener = listener;
        mUserID = userID;
    }

    /**
     * Sync constructor (userID gonna be self)
     */
    public GetFriendsRequest() {

    }

    /**
     * Sync constructor
     *
     * @param userID The id from user to get information
     */
    public GetFriendsRequest(String userID) {
        mUserID = userID;
    }

    @Override
    protected ArrayList<User> doInBackground(String... params) {

        String access_token = params[0];
        User user = null;
        ArrayList<User> list = new ArrayList<User>();
        try {
            // date required
            String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
            // Call Foursquare to post checkin
            JSONObject venuesJson = executeHttpGet("https://api.foursquare.com/v2/users/"
                    + mUserID
                    + "/friends"
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
                        .getJSONObject("friends").getJSONArray("items");
                for (int i = 0; i < json.length(); i++) {
                    user = gson.fromJson(json.get(i).toString(),
                            User.class);
                    list.add(user);
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
    protected void onPostExecute(ArrayList<User> friendsList) {
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
