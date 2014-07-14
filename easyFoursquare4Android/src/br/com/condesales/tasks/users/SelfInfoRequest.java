package br.com.condesales.tasks.users;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import br.com.condesales.listeners.UserInfoRequestListener;
import br.com.condesales.models.User;

public class SelfInfoRequest extends AsyncTask<String, Integer, User> {

    private Activity mActivity;
    private ProgressDialog mProgress;
    private UserInfoRequestListener mListener;
    private Exception error;

    public SelfInfoRequest(Activity activity, UserInfoRequestListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    public SelfInfoRequest(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        mProgress = new ProgressDialog(mActivity);
        mProgress.setCancelable(false);
        mProgress.setMessage("Getting user info ...");
        mProgress.show();
        super.onPreExecute();
    }

    @Override
    protected User doInBackground(String... params) {

        String token = params[0];
        User user = null;
        Gson gson = new Gson();
        // Check if there is a parameter called "code"
        if (token != null && retrieveUserInfo().equals("")) {
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
                    saveUserInfo(json);
                    user = gson.fromJson(json, User.class);
                } else {
                    if (mListener != null) {
                        error = new Exception(jsonResponse.getJSONObject("meta").getString("errorDetail"));
                        return null;
                    }
                }
            } catch (Exception exp) {
                if (mListener != null) {
                    error = exp;
                    return null;
                }

            }
        } else {
            user = gson.fromJson(retrieveUserInfo(), User.class);
        }

        UserImageRequest request = new UserImageRequest(mActivity);
        Bitmap bitmap = request.getFileInCache();
        user.setBitmapPhoto(bitmap);
        return user;
    }

    @Override
    protected void onPostExecute(User result) {
        mProgress.dismiss();
        if (mListener != null) {
            if (error != null) {
                mListener.onError(error.getMessage());
                return;
            }
            mListener.onUserInfoFetched(result);
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

    private void saveUserInfo(String userJson) {
        SharedPreferences settings = mActivity.getSharedPreferences(
                FoursquareConstants.SHARED_PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(FoursquareConstants.USER_INFO, userJson);
        // Commit the edits!
        editor.commit();
    }

    private String retrieveUserInfo() {
        SharedPreferences settings = mActivity.getSharedPreferences(
                FoursquareConstants.SHARED_PREF_FILE, 0);
        return settings.getString(FoursquareConstants.USER_INFO, "");
    }

}
