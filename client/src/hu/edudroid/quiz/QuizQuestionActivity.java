package hu.edudroid.quiz;

import it.unipr.ce.dsg.s2p.sip.Address;
import hu.edudroid.quiz.QuizService.QuizServiceBinder;
import hu.edudroid.quiz_engine.AnswerMessage;
import hu.edudroid.quiz_engine.PingMessage;
import hu.edudroid.quiz_engine.QuestionMessage;
import hu.edudroid.quiz_engine.QuizPeerListener;
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
import android.widget.Toast;

public class QuizQuestionActivity extends Activity implements ServiceConnection, OnItemClickListener, QuizPeerListener {

	private EditText questionText;
	private EditText codeText;
	private ListView answerList;
	private QuizService service;
	private SharedPreferences prefs;
	
	private String questionId;

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
		answerList.setVisibility(View.INVISIBLE);
		questionText.setText(R.string.noQuestion);		
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
		if (service != null) {
			service.unregisterListener(this);
		}
		unbindService(this);
		super.onPause();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		service = ((QuizServiceBinder)binder).getService();
		service.registerListener(this);
		String question = service.getQuestion();
		String[] answers = service.getAnswers();
		questionId = service.getQuestionId();
		updateUi(question, answers);		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		service = null;
		answerList.setVisibility(View.INVISIBLE);
		questionText.setText(R.string.noQuestion);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int itemPosition, long itemId) {
		if (service != null) {
			String address = prefs.getString(EnterCodeActivity.SERVER_ADDRESS_KEY, null);
			String code = prefs.getString(EnterCodeActivity.CODE_KEY, null);
			if (address == null) {
				Toast.makeText(this, "No server address, restart application!", Toast.LENGTH_LONG).show();
				return;
			}
			service.sendAnswer(address, code, questionId, "" + itemPosition);
		}
	}

	@Override
	public void messageSendingError() {}

	@Override
	public void messageSendingSuccess() {}

	@Override
	public void answerReceived(Address source, AnswerMessage answer) {}

	@Override
	public void questionReceived(Address source, QuestionMessage question) {
		questionId = question.getQuestionId();
		final String questionString = question.getQuestion();
		final String[] answers = question.getAnswers();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateUi(questionString, answers);
			}
		});
	}
	
	private void updateUi(String question, String[] answers) {
		if (question == null) {
			answerList.setVisibility(View.INVISIBLE);
			questionText.setText(R.string.noQuestion);
		} else {
			answerList.setVisibility(View.VISIBLE);
			questionText.setText(question);
			answerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, answers));
		}
	}

	@Override
	public void pingReceived(Address source, PingMessage ping) {}
}