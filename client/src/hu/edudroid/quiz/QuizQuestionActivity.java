package hu.edudroid.quiz;

import hu.edudroid.quiz.QuizService.QuizServiceBinder;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class QuizQuestionActivity extends Activity implements ServiceConnection, OnItemClickListener {

	private EditText questionText;
	private EditText codeText;
	private ListView answerList;
	private QuizService service;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		questionText = (EditText)findViewById(R.id.questionText);
		codeText = (EditText)findViewById(R.id.codeDisplay);
		answerList = (ListView)findViewById(R.id.answerList);
		answerList.setOnItemClickListener(this);
		prefs = getSharedPreferences(EnterCodeActivity.SHARED_PREFS_NAME, MODE_PRIVATE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean result = bindService(new Intent(this, QuizService.class), this, 0);
		if (!result) {
			Builder builder = new Builder(this);
			builder.setTitle("Internal error");
			builder.setMessage("Unable to connect to service. Please restart manually!");
			builder.show();
		}
		String codeString = prefs.getString(EnterCodeActivity.CODE_KEY, null);
		if (codeString != null) {
			codeText.setText(codeString);
		}
		
	}
	
	@Override
	protected void onPause() {
		unbindService(this);
		super.onPause();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		service = ((QuizServiceBinder)binder).getService();
		String questionString = service.getQuestion();
		if (questionString == null) {
			answerList.setVisibility(View.INVISIBLE);
			questionText.setText(R.string.noQuestion);
		} else {
			questionText.setText(questionString);
			String[] answers = service.getAnswers();
			answerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, answers));
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		service = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}