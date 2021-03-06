package hu.edudroid.quiz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ConnectActivity extends Activity implements OnClickListener {

	private SharedPreferences prefs;
	private EditText addressEdit;
	private TextView codeText;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_layout);
		findViewById(R.id.connectButton).setOnClickListener(this);
		findViewById(R.id.discoverButton).setOnClickListener(this);
		findViewById(R.id.discoverButton).setEnabled(false);
		addressEdit = (EditText)findViewById(R.id.serverAddressEdit);
		codeText = (TextView)findViewById(R.id.codeDisplay);
		prefs = getSharedPreferences(EnterCodeActivity.SHARED_PREFS_NAME, MODE_PRIVATE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String address = prefs.getString(EnterCodeActivity.SERVER_ADDRESS_KEY, null);
		if (address != null) {
			addressEdit.setText(address);
		}
		String code = prefs.getString(EnterCodeActivity.CODE_KEY, null);
		if (address != null) {
			codeText.setText(code);
		}
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.connectButton) {
			String address = addressEdit.getEditableText().toString();
			prefs.edit().putString(EnterCodeActivity.SERVER_ADDRESS_KEY, address).commit();
			startActivity(new Intent(this, QuizQuestionActivity.class));
		} else if (arg0.getId() == R.id.discoverButton) {
		}
	}
}
