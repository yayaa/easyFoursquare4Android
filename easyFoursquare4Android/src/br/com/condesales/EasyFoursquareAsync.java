package br.com.condesales;


import android.app.Activity;
import android.content.SharedPreferences;

import java.util.ArrayList;

import br.com.condesales.constants.FoursquareConstants;
import br.com.condesales.criterias.CheckInCriteria;
import br.com.condesales.criterias.TipsCriteria;
import br.com.condesales.criterias.TrendingVenuesCriteria;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.RequestListener;
import br.com.condesales.models.Checkin;
import br.com.condesales.models.Tip;
import br.com.condesales.models.User;
import br.com.condesales.models.Venue;
import br.com.condesales.models.Venues;
import br.com.condesales.tasks.checkins.CheckInRequest;
import br.com.condesales.tasks.tips.TipsNearbyRequest;
import br.com.condesales.tasks.users.GetCheckInsRequest;
import br.com.condesales.tasks.users.GetFriendsRequest;
import br.com.condesales.tasks.users.GetUserVenuesHistoryRequest;
import br.com.condesales.tasks.users.SelfInfoRequest;
import br.com.condesales.tasks.venues.FoursquareTrendingVenuesNearbyRequest;
import br.com.condesales.tasks.venues.FoursquareVenueDetailsRequest;
import br.com.condesales.tasks.venues.FoursquareVenuesNearbyRequest;

/**
 * Class to handle methods used to perform requests to FoursquareAPI and respond
 * ASYNChronously.
 *
 * @author Felipe Conde <condesales@gmail.com>
 */
public class EasyFoursquareAsync {

    private Activity mActivity;
    private String mAccessToken = "";

    public EasyFoursquareAsync(Activity activity) {
        mActivity = activity;
    }

    /**
     * Requests the access to API
     */
    public void requestAccess(RequestListener<String> listener) {
        if (!hasAccessToken()) {
            loginDialog(listener);
        } else {
            listener.onSuccess(getAccessToken());
        }
    }

    /**
     * Requests logged user information asynchronously.
     *
     * @param listener As the request is asynchronous, listener used to retrieve the
     *                 User object, containing the information.
     */
    public void getUserInfo(RequestListener<User> listener) {
        SelfInfoRequest request = new SelfInfoRequest(listener);
        request.execute(getAccessToken());
    }

    /**
     * Requests the nearby Venues.
     *
     * @param criteria The criteria to your search request
     * @param listener As the request is asynchronous, listener used to retrieve the
     *                 User object, containing the information.
     */
    public void getVenuesNearby(VenuesCriteria criteria, RequestListener<ArrayList<Venue>> listener) {
        FoursquareVenuesNearbyRequest request = new FoursquareVenuesNearbyRequest(criteria, listener);
        request.execute(getAccessToken());
    }

    /**
     * Requests the nearby Tips.
     *
     * @param criteria The criteria to your search request
     * @param listener As the request is asynchronous, listener used to retrieve the
     *                 User object, containing the information.
     */
    public void getTipsNearby(TipsCriteria criteria, RequestListener<ArrayList<Tip>> listener) {
        TipsNearbyRequest request = new TipsNearbyRequest(criteria, listener);
        request.execute(getAccessToken());
    }

    /**
     * Requests the nearby Venus that are trending.
     *
     * @param listener As the request is asynchronous, listener used to retrieve the
     *                 User object, containing the information.
     * @param criteria The criteria to your search request
     */
    public void getTrendingVenuesNearby(TrendingVenuesCriteria criteria, RequestListener<ArrayList<Venue>> listener) {
        FoursquareTrendingVenuesNearbyRequest request = new FoursquareTrendingVenuesNearbyRequest(criteria, listener);
        request.execute(getAccessToken());

    }

    public void getVenueDetail(String venueID, RequestListener<Venue> listener) {
        FoursquareVenueDetailsRequest request = new FoursquareVenueDetailsRequest(listener, venueID);
        request.execute(getAccessToken());
    }

    /**
     * Checks in at a venue.
     *
     * @param listener As the request is asynchronous, listener used to retrieve the
     *                 User object, containing the information about the check in.
     * @param criteria The criteria to your search request
     */
    public void checkIn(CheckInCriteria criteria, RequestListener<Checkin> listener) {
        CheckInRequest request = new CheckInRequest(criteria, listener);
        request.execute(getAccessToken());
    }

    public void getCheckIns(RequestListener<ArrayList<Checkin>> listener) {
        GetCheckInsRequest request = new GetCheckInsRequest(listener);
        request.execute(getAccessToken());
    }

    public void getCheckIns(String userID, RequestListener<ArrayList<Checkin>> listener) {
        GetCheckInsRequest request = new GetCheckInsRequest(userID, listener);
        request.execute(getAccessToken());
    }

    public void getFriends(RequestListener<ArrayList<User>> listener) {
        GetFriendsRequest request = new GetFriendsRequest(listener);
        request.execute(mAccessToken);
    }

    public void getFriends(String userID, RequestListener<ArrayList<User>> listener) {
        GetFriendsRequest request = new GetFriendsRequest(userID, listener);
        request.execute(getAccessToken());
    }

    public void getVenuesHistory(RequestListener<ArrayList<Venues>> listener) {
        GetUserVenuesHistoryRequest request = new GetUserVenuesHistoryRequest(listener);
        request.execute(getAccessToken());
    }

    public void getVenuesHistory(String userID, RequestListener<ArrayList<Venues>> listener) {
        GetUserVenuesHistoryRequest request = new GetUserVenuesHistoryRequest(userID, listener);
        request.execute(getAccessToken());
    }

    private boolean hasAccessToken() {
        String token = getAccessToken();
        return !token.equals("");
    }

    private String getAccessToken() {
        if (mAccessToken.equals("")) {
            SharedPreferences settings = mActivity.getSharedPreferences(
                    FoursquareConstants.SHARED_PREF_FILE, 0);
            mAccessToken = settings.getString(FoursquareConstants.ACCESS_TOKEN,
                    "");
        }
        return mAccessToken;
    }

    /**
     * Requests the Foursquare login though a dialog.
     */
    private void loginDialog(RequestListener<String> listener) {
        String url = "https://foursquare.com/oauth2/authenticate"
                + "?client_id=" + FoursquareConstants.CLIENT_ID
                + "&response_type=code" + "&redirect_uri="
                + FoursquareConstants.CALLBACK_URL;

        FoursquareDialog mDialog = new FoursquareDialog(mActivity, url, listener);
        mDialog.show();
    }

}
