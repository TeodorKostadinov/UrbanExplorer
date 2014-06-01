package fmi.tkostadinov.urbanexplorer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import fmi.tkostadinov.urbanexplorer.QuestionFragment.OnQuestionAnsweredListener;
import fmi.tkostadinov.urbanexplorer.database.DBAccess;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.TextView;

public class MapActivity extends Activity implements OnMarkerClickListener, 
OnMapClickListener, OnQuestionAnsweredListener, LocationListener{
	private static int INITIAL_QUESTIONS_VISIBLE = 3;
	private static int ALL_QUESTIONS_COUNT = 10;
	private GoogleMap map;
	private HashMap<Integer, Marker> markersList = new HashMap<Integer, Marker>();
	private Fragment qf;
	private FragmentTransaction ft;
	public TextView tvPoints, tvPlayer;
	private Profile profile;
	private Location currentLocation;
	private LocationManager lm;
	private DBAccess db;
	private int today = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		Log.e("user","whaat");
		Calendar calendar = Calendar.getInstance();
		today = calendar.get(Calendar.DAY_OF_WEEK); 
		Log.e("user", "today is "+today);
		initialize();
		Log.e("user","1");
		if(profile != null){
			setQuestionsOnMap(profile.visible);
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(
					lm.getLastKnownLocation(lm.NETWORK_PROVIDER).getLatitude(),
					lm.getLastKnownLocation(lm.NETWORK_PROVIDER).getLongitude()), 12);
			map.animateCamera(update);
			Log.e("user","2");
			updatePointsOnScreen();
		}
		Log.e("user","6");
	}

	private void initialize() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		tvPoints = (TextView) findViewById(R.id.player_points);
		tvPlayer = (TextView) findViewById(R.id.player_name);
		map.setOnMarkerClickListener(this);
		map.setOnMapClickListener(this);
		map.setMyLocationEnabled(true);
		db = new DBAccess(this);
		profile = getUser();
	}

	private void updatePointsOnScreen(){
		tvPlayer.setText(profile.nickname.toString());
		tvPoints.setText(profile.points+"");
	}
	
	private Profile getUser() {
		String userName = getIntent().getStringExtra("User Name").toString();
		String userPass = getIntent().getStringExtra("User Password").toString();
		String loginType = getIntent().getStringExtra("Login Type").toString();
		Profile pr;
		if(loginType.equals("new user")){
			//Log.e("user","getUser - new User");
			pr = new Profile(userName, userPass, new ArrayList<Integer>(), new ArrayList<Integer>(), 
					INITIAL_QUESTIONS_VISIBLE, 0, 0);
		}
		else{
			db.openDataBase();
			pr = db.getProfile(userName, userPass);
			db.close();
			if(pr == null){
				//Inform user for Incorrect pass/username
				Intent i = new Intent(this, LoginActivity.class);
				i.putExtra("user", "wrong");
				this.startActivity(i);
				finish();
			}
		}
		return pr;
	}

	//sets the first I questions on the map, coloring them appropriately
	private void setQuestionsOnMap(int i) {

		Boolean isItStillToday = this.today == profile.lastVisited;
		if(!isItStillToday){
			profile.wrongAnswers.clear();
		}

		db.openDataBase();
		Cursor c = db.getQuestionLocations(i);
		if(c.moveToFirst()){
			do {
				double lat = c.getDouble(c.getColumnIndex("lat"));
				double lng = c.getDouble(c.getColumnIndex("long"));
				int id = c.getInt((c.getColumnIndex("_id")));
				if (profile.correctAnswers.contains(id)) {
					Marker marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(lat,lng))
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
					.snippet(new String(""+id)));
					//markersList.put(id,marker);
				}
				else if (isItStillToday && profile.wrongAnswers.contains(id)) {
					Marker marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(lat,lng))
					.snippet(new String(""+id)));
					markersList.put(id,marker);
				}
				else{
					Marker marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(lat,lng))
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
					.snippet(new String(""+id)));
					markersList.put(id,marker);
				}
			} while (c.moveToNext());
		}
		c.close();
		db.close();
	}

	//creates the fragment
	@Override
	public boolean onMarkerClick(Marker arg0) {
		//int qNum = markersList.
		int qNum = Integer.parseInt(arg0.getSnippet());
		ft = getFragmentManager().beginTransaction();
		db.openDataBase();
		Question question = db.getQuestion(qNum);
		if(profile.wrongAnswers.contains(qNum)){

			qf = new AnsweredQuestionFragment("f");
		}
		else if (profile.correctAnswers.contains(qNum)){
			qf = new AnsweredQuestionFragment("t");
		}
		else{
			if(this.currentLocation!= null){
				Location questLoc = new Location("quest");
				questLoc.setLatitude(question.coordinates.latitude);
				questLoc.setLongitude(question.coordinates.longitude);
				double distance = currentLocation.distanceTo(questLoc);
				qf = new QuestionFragment(question, qNum, distance);	
			}
			else{
				tvPlayer.setText("Can't get current location");
			}
		}
		ft.replace(R.id.fragment_container, qf);
		ft.commit();
		return false;
	}

	//the callback from the fragment
	@Override
	public void onQuestionAnswered(Boolean correctAnswer, int questionNumber) {
		//Log.e("user", "callback1");
		//change points in profile, reveal next
		if(correctAnswer){
			profile.answeredRight(questionNumber);
			if(profile.visible<ALL_QUESTIONS_COUNT){
				Log.e("user", "visible++");
				profile.visible++;
				revealNextQuestion(profile.visible);
			}
		}
		else{
			profile.answeredWrong(questionNumber);
		}
		//update map with changed question and points
		changeMarker(questionNumber, correctAnswer);
		updatePointsOnScreen();
	}

	private void revealNextQuestion(int questionID) {
		db.openDataBase();
		//Log.e("user", "next1");
		Question q = db.getQuestion(questionID);
		//Log.e("user", "callback2");
		Marker marker = map.addMarker(new MarkerOptions()
		.position(q.coordinates)
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
		.snippet(new String(""+questionID)));
		markersList.put(questionID,marker);
	}

	public void changeMarker(int questionNumber, Boolean correct){
		Marker marker = this.markersList.get(questionNumber);
		if(correct){
			marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		}
		else {
			marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//TODO the user won't be able to answer yesterday's wrong questions
		//if he launches twice the app
		profile.lastVisited = this.today;
		StringBuilder wrongString = new StringBuilder();
		for (int num : profile.wrongAnswers) {
			wrongString.append(num);
			wrongString.append(" ");
		}
		StringBuilder correctString = new StringBuilder();
		for (int num : profile.correctAnswers) {
			correctString.append(num);
			correctString.append(" ");
		}
		db.openDataBase();
		if(profile.id != -1){
			db.updateProfile(profile.id, profile.points, profile.visible, profile.lastVisited,
					wrongString.toString(), correctString.toString());
		}
		else{
			//Log.e("user","new user - addProfile");
			db.addProfile(profile.nickname, profile.password, 
					profile.points, profile.visible, profile.lastVisited,
					wrongString.toString(), correctString.toString());
		}
		db.close();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, this);
	}

	//closes question when map touched
	@Override
	public void onMapClick(LatLng point) {
		if(qf!=null){
			ft = getFragmentManager().beginTransaction();
			ft.remove(qf);
			ft.commit();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		this.currentLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}