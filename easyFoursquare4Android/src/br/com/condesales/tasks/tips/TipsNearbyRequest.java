package br.com.condesales.tasks.tips;

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
import br.com.condesales.criterias.TipsCriteria;
import br.com.condesales.listeners.RequestListener;
import br.com.condesales.models.FoursquareError;
import br.com.condesales.models.Tip;

public class TipsNearbyRequest extends AsyncTask<String, Integer, ArrayList<Tip>> {

    private RequestListener<ArrayList<Tip>> mListener;
    private TipsCriteria mCriteria;
    private FoursquareError foursquareError;

    public TipsNearbyRequest(TipsCriteria criteria, RequestListener<ArrayList<Tip>> listener) {
        mListener = listener;
        mCriteria = criteria;
    }

    public TipsNearbyRequest(TipsCriteria criteria) {
        mCriteria = criteria;
    }

    @Override
    protected ArrayList<Tip> doInBackground(String... params) {

        String access_token = params[0];
        ArrayList<Tip> tips = new ArrayList<Tip>();

        try {

            //date required

            String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
            // Call Foursquare to get the Tips around
            String uri = "https://api.foursquare.com/v2/tips/search"
                    + "?v="
                    + apiDateVersion
                    + "&ll="
                    + mCriteria.getLocation().getLatitude()
                    + ","
                    + mCriteria.getLocation().getLongitude()
                    + "&query="
                    + mCriteria.getQuery()
                    + "&limit="
                    + mCriteria.getQuantity()
                    + "&offset="
                    + mCriteria.getOffset();
            if (!access_token.equals("")) {
                uri = uri + "&oauth_token=" + access_token;
            } else {
                uri = uri + "&client_id=" + FoursquareConstants.CLIENT_ID + "&client_secret=" + FoursquareConstants.CLIENT_SECRET;
            }

            JSONObject tipsJson = executeHttpGet(uri);
            Gson gson = new Gson();

            // Get return code
            int returnCode = Integer.parseInt(tipsJson.getJSONObject("meta")
                    .getString("code"));
            // 200 = OK
            if (returnCode == HttpStatus.SC_OK) {
                JSONArray json = tipsJson.getJSONObject("response")
                        .getJSONArray("tips");
                for (int i = 0; i < json.length(); i++) {
                    Tip tip = gson.fromJson(json.getJSONObject(i)
                            .toString(), Tip.class);
                    tips.add(tip);
                }
            } else {
                String errorString = tipsJson.getJSONObject("meta").toString();
                foursquareError = gson.fromJson(errorString, FoursquareError.class);
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            foursquareError = new FoursquareError();
            foursquareError.setErrorDetail(exp.getMessage());
        }
        return tips;
    }

    @Override
    protected void onPostExecute(ArrayList<Tip> tips) {
        if (mListener != null)
            if (foursquareError == null) {
                mListener.onSuccess(tips);
            } else {
                mListener.onError(foursquareError);
            }
        super.onPostExecute(tips);
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
