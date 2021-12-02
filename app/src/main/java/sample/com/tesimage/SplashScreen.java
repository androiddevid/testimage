package sample.com.tesimage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashScreen extends Activity  {

	private static final int SPLASH_TIME = 1 * 1000;
	protected static final String TAG = null;

	@Override
	protected void onStart() {
		super.onStart();

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);

		 new Handler().postDelayed(() -> {

		   Intent intent = new Intent(SplashScreen.this, TesPhoto.class);
		   startActivity(intent);

		   finish();
		 }, SPLASH_TIME);
	}


	@Override
	public void onDestroy() {


		super.onDestroy();

	}


	
	

}
