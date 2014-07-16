package br.com.condesales.tasks.users;

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
import br.com.condesales.models.User;

public class SelfInfoRequest extends AsyncTask<String, Integer, User> {

    private RequestListener<User> mListener;
    private FoursquareError foursquareError;

    public SelfInfoRequest(RequestListener<User> listener) {
        mListener = listener;
    }

    public SelfInfoRequest() {
    }

    @Override
    protected User doInBackground(String... params) {

        String token = params[0];
        User user = null;
        Gson gson = new Gson();
        // Check if there is a parameter called "code"
        try {
            //date required
            String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
            // Get userdata of myself
            JSONObject jsonResponse = executeHttpGet("https://api.foursquare.com/v2/"
                    + "users/self"
                    + "?v=" + apiDateVersion
                    + "&oauth_token=" + token);
            // Get return code
            int returnCode = jsonResponse.getJSONObject("meta").getInt("code");
            // 200 = OK
            if (returnCode == HttpStatus.SC_OK) {
                String json = jsonResponse.getJSONObject("response")
                        .getJSONObject("user").toString();
                user = gson.fromJson(json, User.class);
            } else {
                foursquareError = gson.fromJson(jsonResponse.getJSONObject("meta").toString(), FoursquareError.class);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            foursquareError = new FoursquareError();
            foursquareError.setErrorDetail(exp.getMessage());
        }

        return user;
    }

    @Override
    protected void onPostExecute(User result) {
        if (mListener != null) {
            if (foursquareError == null) {
                mListener.onSuccess(result);
            } else {
                mListener.onError(foursquareError);
            }
        }
        super.onPostExecute(result);
    }

    /**
     * Calls a URI and returns the answer as a JSON object.
     *
     * @param uri the uri to make the request
     * @return The JSONObject containing the information
     * @throws Exception general exception
     */
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
