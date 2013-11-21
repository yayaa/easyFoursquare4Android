package br.com.condesales;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.listeners.UserInfoRequestListener;
import br.com.condesales.models.User;
import br.com.condesales.tasks.users.UserImageRequest;

public class MainActivity extends Activity implements
		AccessTokenRequestListener, ImageRequestListener {

	private EasyFoursquareAsync async;
	private ImageView userImage;
	private ViewSwitcher viewSwitcher;
	private TextView userName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		userImage = (ImageView) findViewById(R.id.imageView1);
		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
		userName = (TextView) findViewById(R.id.textView1);
		async = new EasyFoursquareAsync(this);
		async.requestAccess(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onError(String errorMsg) {
		// TODO Do something with the error message
		Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onAccessGrant(String accessToken) {
		// TODO with the access token you can perform any request to foursquare.
		// example:
		async.getUserInfo(new UserInfoRequestListener() {

			@Override
			public void onError(String errorMsg) {
				// TODO Some error getting user info
				Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG)
						.show();
			}

			@Override
			public void onUserInfoFetched(User user) {
				// TODO OWww. did i already got user!?
				if (user.getBitmapPhoto() == null) {
					UserImageRequest request = new UserImageRequest(
							MainActivity.this, MainActivity.this);
					request.execute(user.getPhoto());
				} else {
					userImage.setImageBitmap(user.getBitmapPhoto());
				}
				userName.setText(user.getFirstName() + " " + user.getLastName());
				viewSwitcher.showNext();
				Toast.makeText(MainActivity.this, "Got it!", Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	@Override
	public void onImageFetched(Bitmap bmp) {
		userImage.setImageBitmap(bmp);
	}

}
