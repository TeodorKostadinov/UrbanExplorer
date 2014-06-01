package fmi.tkostadinov.urbanexplorer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class QuestionFragment extends Fragment implements OnClickListener{
	private RadioButton but1;
	private RadioButton but2;
	private RadioButton but3;
	private RadioButton but4;
	private RadioButton rightButton;
	private Button btnExit;
	private Question question;
	private int id;
	private double distance;
	private OnQuestionAnsweredListener listener;
	public static double MINIMAL_DISTANCE = 50;
	
	public QuestionFragment(Question question, int id, double distance){
		this.question = question;
		this.distance = distance;
		this.id = id;
	}
	public QuestionFragment(){ }
	//creates the view, text, radios, exit btn, sets listeners for the buttons - onClick
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.question_fragment, container, false);
		TextView questionText = (TextView) v.findViewById(R.id.question);

		but1 = (RadioButton) v.findViewById(R.id.radio_first);
		but2 = (RadioButton) v.findViewById(R.id.radio_second);
		but3 = (RadioButton) v.findViewById(R.id.radio_third);
		but4 = (RadioButton) v.findViewById(R.id.radio_forth);
		btnExit = (Button) v.findViewById(R.id.btn_exit);
		btnExit.setOnClickListener(this);
		but1.setOnClickListener(this);
		but2.setOnClickListener(this);
		but3.setOnClickListener(this);
		but4.setOnClickListener(this);
		rightButton = getRightButton();
		Log.e("user", "actual "+distance);
		Log.e("user", "minimaj "+MINIMAL_DISTANCE);
		if(distance < MINIMAL_DISTANCE){
			questionText.setText(this.question.question);
			but1.setText(this.question.possibilities[0]);
			but2.setText(this.question.possibilities[1]);
			but3.setText(this.question.possibilities[2]);
			but4.setText(this.question.possibilities[3]);
		}
		else{
			questionText.setText("Too far from question");
			questionText.setTextSize(20);
			RadioGroup rg = (RadioGroup) v.findViewById(R.id.radios);
			rg.removeAllViews();
		}
		return v;
	}
	//returns the right radio
	private RadioButton getRightButton() {
		int right = this.question.answer;
		switch (right) {
		case 0: return but1;
		case 1: return but2;
		case 2: return but3;
		case 3: return but4;
		}
		return null;
	}
	//manages the exit of the view and calls the answer methods
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_exit){
			getActivity().getFragmentManager().beginTransaction().remove(this).commit();
		}
		else{
			if (v == rightButton) {
				answerCorrect();
			}
			else{
				answerWrong();
			}
			getActivity().getFragmentManager().beginTransaction().remove(this).commit();
			Log.e("user", "exitFragment");
		}

	}

	//calls the listener and transports true or false to the activity/ changes the question to ANSWERD
	private void answerWrong() {
		listener.onQuestionAnswered(false,this.id);
	}

	private void answerCorrect() {
		listener.onQuestionAnswered(true,this.id);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnQuestionAnsweredListener) {
			listener = (OnQuestionAnsweredListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MyListFragment.OnItemSelectedListener");
		}
	}

	public interface OnQuestionAnsweredListener {
		public void onQuestionAnswered(Boolean correctAnswer, int questionNumber);
	}

}
