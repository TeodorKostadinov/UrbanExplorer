package fmi.tkostadinov.urbanexplorer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class AnsweredQuestionFragment extends Fragment implements OnClickListener{

	private Boolean answered;
	public AnsweredQuestionFragment(String answered){
		if(answered == "t"){
			this.answered = true;
		}
		else{
			this.answered = false;
		}
	}
	public AnsweredQuestionFragment(){	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.answered_question_fragment, container, false);
		ImageView image = (ImageView) v.findViewById(R.id.message);
		image.setOnClickListener(this);
		if(this.answered){
			image.setImageResource(R.drawable.truepic);
		}
		else{
			image.setImageResource(R.drawable.wrongpic);
		}
		return v;
	}
	@Override
	public void onClick(View v) {
		getActivity().getFragmentManager().beginTransaction().remove(this).commit();
	}
	
	
}
