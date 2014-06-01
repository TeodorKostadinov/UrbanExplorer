package fmi.tkostadinov.urbanexplorer.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import fmi.tkostadinov.urbanexplorer.Profile;
import fmi.tkostadinov.urbanexplorer.Question;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DBAccess extends DBHelper{

	public DBAccess(Context context) {
		super(context);
		try {
			createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Profile getProfile(String nick, String pass){
		Cursor cursor = this.questionsDB.query("Users", new String[] {"username","password","points",
				"correctAnswers","wrongAnswers","visible","lastVisited", "_id"}, 
				"username = ?", new String[]{nick+""}, null, null, null);
		Profile profile = null;
		if(cursor.moveToFirst()){
			if (pass.equals(cursor.getString(cursor.getColumnIndex("password")))){
				int visited = Integer.parseInt(cursor.getString(cursor.getColumnIndex("lastVisited")));
				
				String correctsStr = cursor.getString(cursor.getColumnIndex("correctAnswers"));
				ArrayList<Integer>	rights = toIntList(correctsStr.split(" "));
				
				String wrongsStr = cursor.getString(cursor.getColumnIndex("wrongAnswers"));
				ArrayList<Integer>	wrongs = toIntList(wrongsStr.split(" "));
								
				int visible = Integer.parseInt(cursor.getString(cursor.getColumnIndex("visible")));
				int points = Integer.parseInt(cursor.getString(cursor.getColumnIndex("points")));
				
				int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
				profile = new Profile(nick, pass, rights, wrongs, visible, points, visited);	
				profile.id = id;
			}
		}
		return profile;
	}

	public void updateProfile(int id, int points, int visible, int lastVisited, 
			String wrongAnswers, String correctAnswers){
		ContentValues cv = new ContentValues();
		cv.put("lastVisited", lastVisited);
		cv.put("visible", visible);
		cv.put("correctAnswers", correctAnswers);
		cv.put("points", points);
		cv.put("wrongAnswers", wrongAnswers);
		questionsDB.update("Users", cv, "_id = "+id, null);
	}

	public void addProfile(String nick, String pass, int points, int visible, int lastVisited, 
			String wrongAnswers, String correctAnswers){
		ContentValues cv = new ContentValues();
		cv.put("password", pass);
		cv.put("username", nick);
		cv.put("points", points+"");
		cv.put("correctAnswers", correctAnswers);
		cv.put("wrongAnswers", wrongAnswers);
		cv.put("visible", visible+"");
		cv.put("lastVisited", lastVisited+"");
		this.questionsDB.insert("Users", null, cv);
	}

	private ArrayList<Integer> toIntList(String[] array) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(array[0]!=""){
			for (int i = 0; i < array.length; i++) {
				list.add(Integer.parseInt(array[i]));
			}
		}
		return list;
	}

	public Question getQuestion(int questionID){
		Cursor cursor = this.questionsDB.query("Questions", 
				new String[] {"name", "value", "answers", "correctAnswer", "lat", "long"}, 
				"_id = ?", new String[]{questionID+""}, null, null, null);
		cursor.moveToFirst();
		String name = cursor.getString(cursor.getColumnIndex("name"));
		String text = cursor.getString(cursor.getColumnIndex("value"));
		String answers = cursor.getString(cursor.getColumnIndex("answers"));
		int cor = Integer.parseInt(cursor.getString(cursor.getColumnIndex("correctAnswer")));
		double la =Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat")));
		double lo = Double.parseDouble(cursor.getString(cursor.getColumnIndex("long")));
		Question question = new Question(name, text, answers,cor, la, lo);
		cursor.close();
		return question;
	}

	public Cursor getQuestionLocations(int i){
		return this.questionsDB.query("Questions", new String[] {"lat","long", "_id"}, 
				null, null, null, null, "_id", i+"");
	}
}
