package fmi.tkostadinov.urbanexplorer;

import java.util.ArrayList;
import java.util.List;

public class Profile {
	public String nickname;
	public String password;
	public int points;
	public List<Integer> correctAnswers;
	public List<Integer> wrongAnswers;
	public int visible;
	public int lastVisited;
	public int id = -1;
	
	public Profile(String name, String password, ArrayList<Integer> correct,
			ArrayList<Integer> wrong, int visible, int points, int lastVisited){
		this.nickname = name;
		this.password = password;
		this.correctAnswers = correct;
		this.wrongAnswers = wrong;
		this.visible = visible;
		this.points = points;
		this.lastVisited = lastVisited;
	}
	
	public void answeredRight(int questionNumber){
		this.points+=100;
		this.correctAnswers.add(questionNumber);
	}
	
	public void answeredWrong(int questionNumber){
		this.points-=10;
		this.wrongAnswers.add(questionNumber);
	}

	@Override
	public String toString() {
		String correct = "correct: ";
		for (int i : correctAnswers) {
			correct = correct + i + " ";
		}
		String wrong = "wrong: ";
		for (int i : wrongAnswers) {
			wrong = wrong + i + " ";
		}
		String info = this.nickname + ":"+ this.visible + " "+correct + " "+ wrong;
		
		return info;
	}

	
}
