package hu.edudroid.quiz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class EnterCodeActivity extends Activity implements OnClickListener {
	public static final String CODE_KEY = "code";
	public static final String SERVER_ADDRESS_KEY = "server address";
	public static String SHARED_PREFS_NAME = "prefs";
	private SharedPreferences prefs;
	private EditText codeEdit; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_code);
		findViewById(R.id.confirmCodeButton).setOnClickListener(this);
		 codeEdit = ((EditText)findViewById(R.id.codeEdit));
		prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String code = prefs.getString(CODE_KEY, null);
		if (code != null) {
			codeEdit.setText(code);	
		}
	}
	
	@Override
	protected void onPause() {
		String code = codeEdit.getEditableText().toString();
		prefs.edit().putString(CODE_KEY, code).commit();
		super.onPause();
	}

	@Override
	public void onClick(View arg0) {
		String code = codeEdit.getEditableText().toString();
		prefs.edit().putString(CODE_KEY, code).commit();
		startActivity(new Intent(this, ConnectActivity.class));
		finish();
	}
}
