package br.com.condesales;

import android.text.TextUtils;

/**
 * Created by Yahya Bayramoglu on 23/02/16.
 */
public class FoursquareConfig {

    private static boolean displayProgress = true;

    private static String CLIENT_ID;
    private static String CLIENT_SECRET;

    // Do not leave this field as default value
    private static String CALLBACK_URL = "http://localhost:8888";

    public static final String SHARED_PREF_FILE = "shared_pref";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_INFO = "user_info";
    public static final String API_DATE_VERSION = "20140714";

    public static void setClient(String clientId, String clientSecret) {
        FoursquareConfig.CLIENT_ID = clientId;
        FoursquareConfig.CLIENT_SECRET = clientSecret;
    }

    public static void setCallbackUrl(String callbackUrl) {
        FoursquareConfig.CALLBACK_URL = callbackUrl;
    }

    public static void setDisplayProgress(boolean shouldDisplay) {
        FoursquareConfig.displayProgress = shouldDisplay;
    }

    public static String getClientId() {
        if (TextUtils.isEmpty(FoursquareConfig.CLIENT_ID)) {
            throw new RuntimeException("Use FoursquareConfig class to set your ClientId first.");
        }

        return FoursquareConfig.CLIENT_ID;
    }

    public static String getClientSecret() {
        if (TextUtils.isEmpty(FoursquareConfig.CLIENT_SECRET)) {
            throw new RuntimeException("Use FoursquareConfig class to set your ClientSecret first.");
        }

        return FoursquareConfig.CLIENT_SECRET;
    }

    public static String getCallbackUrl() {
        return FoursquareConfig.CALLBACK_URL;
    }

    public static boolean shouldDisplayProgress() {
        return displayProgress;
    }

}