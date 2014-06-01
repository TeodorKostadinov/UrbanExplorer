package fmi.tkostadinov.urbanexplorer;

import java.util.Date;

import android.text.format.Time;

import com.google.android.gms.maps.model.LatLng;

public class Question {

	public static int questionCounter = 0;
	public String name;
	public String question;
	public String[] possibilities;
	public int answer;
	public LatLng coordinates;
	public int uniqueNumber;
	public Question(String name, String question, String possibleAns, 
			int answer, double latitude, double longtitude){
		this.name = name;
		this.question = question;
		this.possibilities = possibleAns.split("-");
		this.answer = answer - 1;
		this.coordinates = new LatLng(latitude,longtitude);
		this.uniqueNumber = questionCounter;
		questionCounter++;
	}

}
