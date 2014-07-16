package br.com.condesales.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import br.com.condesales.constants.FoursquareConstants;
import br.com.condesales.listeners.RequestListener;
import br.com.condesales.models.FoursquareError;

public class AccessTokenRequest extends AsyncTask<String, Integer, String> {

    private final Activity mActivity;
    private RequestListener<String> mListener;
    private FoursquareError foursquareError;

    public AccessTokenRequest(Activity activity, RequestListener<String> listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... params) {

        String code = params[0];
        String token = "";
        // Check if there is a parameter called "code"
        if (code != null) {
            try {
                // Call Foursquare again to get the access token
                JSONObject tokenJson = executeHttpGet("https://foursquare.com/oauth2/access_token"
                        + "?client_id="
                        + FoursquareConstants.CLIENT_ID
                        + "&client_secret="
                        + FoursquareConstants.CLIENT_SECRET
                        + "&grant_type=authorization_code"
                        + "&redirect_uri=http://localhost:8888"
                        + "&code="
                        + code);

                token = tokenJson.getString("access_token");
                //saving token
                Log.i("Access Token", token);
                SharedPreferences settings = mActivity.getSharedPreferences(FoursquareConstants.SHARED_PREF_FILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(FoursquareConstants.ACCESS_TOKEN, token);
                // Commit the edits!
                editor.commit();

            } catch (Exception exp) {
                exp.printStackTrace();
                foursquareError = new FoursquareError();
                foursquareError.setErrorDetail(exp.getMessage());
            }
        } else {
            foursquareError.setErrorDetail("Unknown login error");
        }
        return token;
    }

    @Override
    protected void onPostExecute(String accessToken) {
        if (mListener != null) {
            if (foursquareError == null) {
                mListener.onSuccess(accessToken);
            } else {
                mListener.onError(foursquareError);
            }
        }
        super.onPostExecute(accessToken);
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
