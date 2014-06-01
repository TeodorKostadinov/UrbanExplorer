package fmi.tkostadinov.urbanexplorer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LoginActivity extends Activity implements OnClickListener{

	private EditText nick;
	private EditText pass;
	private Button login;
	private Button newUser;
	private TextView warning;
	private ToggleButton tglBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		this.setTitle(getResources().getString(R.string.title_activity_login));
		
		nick = (EditText) findViewById(R.id.nickname);
		pass = (EditText) findViewById(R.id.pass);
		login = (Button) findViewById(R.id.btn_login);
		login.setOnClickListener(this);
		newUser = (Button) findViewById(R.id.btn_new_user);
		newUser.setOnClickListener(this);
		
		//Inform for wrong username/pass
		if(getIntent().hasExtra("user")){
			warning = (TextView) findViewById(R.id.warning);
			warning.setVisibility(View.VISIBLE);
			warning.setText("Wrong username/password");
		}
		
		//Set last used nickname, if available
		SharedPreferences pref = getPreferences(0);
		SharedPreferences.Editor editor = pref.edit();
		String lastUsedName = pref.getString("username", "noname");
		if(!lastUsedName.equals("noname")){
			nick.setText(lastUsedName);
		}
		
		//TODO Remove upon distribution
		tglBtn = (ToggleButton) findViewById(R.id.toggle_gps);
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent(this, MapActivity.class);
		if(v.getId()==R.id.btn_new_user) {
			i.putExtra("Login Type", "new user");
		}
		else {
			i.putExtra("Login Type", "old user");			
		}
		i.putExtra("User Name", nick.getText().toString());
		i.putExtra("User Password", pass.getText().toString());
		//Saving last used user name in SP
		SharedPreferences pref = getPreferences(0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("username", nick.getText().toString());
		editor.commit();
		this.startActivity(i);
		finish();
	}

	//TODO Remove upon distribution
	public void onDistanceCheckToggle(View v){
		//Log.e("user", "toggled ");
		if(tglBtn.isChecked()){
			QuestionFragment.MINIMAL_DISTANCE = Double.MAX_VALUE;
		}
		else{
			QuestionFragment.MINIMAL_DISTANCE = 50;
		}
	}
}
