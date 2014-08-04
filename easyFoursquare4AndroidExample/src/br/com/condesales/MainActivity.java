package br.com.condesales;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

import br.com.condesales.criterias.CheckInCriteria;
import br.com.condesales.criterias.TipsCriteria;
import br.com.condesales.listeners.RequestListener;
import br.com.condesales.models.Checkin;
import br.com.condesales.models.FoursquareError;
import br.com.condesales.models.Tip;
import br.com.condesales.models.User;
import br.com.condesales.tasks.users.UserImageRequest;

public class MainActivity extends Activity implements
        RequestListener<String> {

    private EasyFoursquareAsync async;
    private ImageView userImage;
    private ViewSwitcher viewSwitcher;
    private TextView userName;
    private RequestListener<Bitmap> userImageListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userImage = (ImageView) findViewById(R.id.imageView1);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
        userName = (TextView) findViewById(R.id.textView1);
        //ask for access
        async = new EasyFoursquareAsync(this);
        async.requestAccess(this);
    }

    private void requestTipsNearby() {
        Location loc = new Location("");
        loc.setLatitude(40.4363483);
        loc.setLongitude(-3.6815703);

        TipsCriteria criteria = new TipsCriteria();
        criteria.setLocation(loc);
        async.getTipsNearby(criteria, new RequestListener<ArrayList<Tip>>() {

            @Override
            public void onError(FoursquareError foursquareError) {
                Toast.makeText(MainActivity.this, foursquareError.getErrorDetail(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ArrayList<Tip> tips) {
                Toast.makeText(MainActivity.this, tips.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkin() {

        CheckInCriteria criteria = new CheckInCriteria();
        //setting to private, so you can test as many times as you want
        criteria.setBroadcast(CheckInCriteria.BroadCastType.PRIVATE);
        criteria.setVenueId("4c7063da9c6d6dcb9798d27a");

        async.checkIn(criteria, new RequestListener<Checkin>() {

            @Override
            public void onSuccess(Checkin response) {
                Toast.makeText(MainActivity.this, response.getVenue().getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FoursquareError error) {
                Toast.makeText(MainActivity.this, error.getErrorDetail(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void fetchUserInfo() {
        async.getUserInfo(new RequestListener<User>() {

            @Override
            public void onSuccess(User user) {
                userName.setText(user.getFirstName() + " " + user.getLastName());
                viewSwitcher.showNext();
                Toast.makeText(MainActivity.this, "Got it!", Toast.LENGTH_LONG).show();
                fetchUserImage(user);
            }

            @Override
            public void onError(FoursquareError error) {
                // Some error getting user info
                Toast.makeText(MainActivity.this, error.getErrorDetail(), Toast.LENGTH_LONG)
                        .show();
            }

        });
    }

    private void fetchUserImage(User user) {
        // OWww. did i already got user!?
        userImageListener = new RequestListener<Bitmap>() {
            @Override
            public void onSuccess(Bitmap userImage) {
                MainActivity.this.userImage.setImageBitmap(userImage);
            }

            @Override
            public void onError(FoursquareError error) {
                // Some error getting user image
                Toast.makeText(MainActivity.this, error.getErrorDetail(), Toast.LENGTH_LONG)
                        .show();
            }
        };

        UserImageRequest request = new UserImageRequest(userImageListener);
        request.execute(user.getPhoto());
    }

    @Override
    public void onSuccess(String accessToken) {
        // with the access token you can perform any request to foursquare.
        // example:
        fetchUserInfo();

        //for another examples uncomment lines below:
        //requestTipsNearby();
        checkin();
    }

    @Override
    public void onError(FoursquareError error) {
        // Some error trying to login
        Toast.makeText(MainActivity.this, error.getErrorDetail(), Toast.LENGTH_LONG)
                .show();
    }
}
