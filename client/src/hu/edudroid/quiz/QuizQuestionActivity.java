package hu.edudroid.quiz;

import hu.edudroid.quiz.QuizService.QuizServiceBinder;
import hu.edudroid.quiz_engine.AnswerMessage;
import hu.edudroid.quiz_engine.PingMessage;
import hu.edudroid.quiz_engine.QuestionMessage;
import hu.edudroid.quiz_engine.QuizPeerListener;
import hu.edudroid.quiz_engine.TimeoutMessage;
import it.unipr.ce.dsg.s2p.sip.Address;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class QuizQuestionActivity extends Activity implements ServiceConnection, OnItemClickListener, QuizPeerListener {

	private TextView questionText;
	private TextView codeText;
	private ListView answerList;
	private QuizService service;
	private SharedPreferences prefs;
	
	private String code;
	private String serverAddress;
	
	private String questionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz_question);
		questionText = (TextView)findViewById(R.id.questionText);
		codeText = (TextView)findViewById(R.id.codeDisplay);
		answerList = (ListView)findViewById(R.id.answerList);
		answerList.setOnItemClickListener(this);
		prefs = getSharedPreferences(EnterCodeActivity.SHARED_PREFS_NAME, MODE_PRIVATE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		code = prefs.getString(EnterCodeActivity.CODE_KEY, null);
		serverAddress = prefs.getString(EnterCodeActivity.SERVER_ADDRESS_KEY, null);
		answerList.setVisibility(View.INVISIBLE);
		questionText.setText(R.string.noQuestion);		
		boolean result = bindService(new Intent(this, QuizService.class), this, Context.BIND_AUTO_CREATE);
		if (!result) {
			Builder builder = new Builder(this);
			builder.setTitle("Internal error");
			builder.setMessage("Unable to connect to service. Please restart manually!");
			builder.show();
		}
		if (code != null) {
			codeText.setText(code + " " + serverAddress);
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
		Log.d("Service connected", "We have a service!");		
		service = ((QuizServiceBinder)binder).getService();
		service.registerListener(this);
		service.sendPing(serverAddress, code);
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
	public void onItemClick(AdapterView<?> adapter, View view, final int itemPosition, long itemId) {
		if (service != null) {
			
			Builder builder = new Builder(this);
			LayoutInflater inflater = getLayoutInflater();
			View dialogView = inflater.inflate(R.layout.send_dialog_layout, null);
			builder.setView(dialogView);
			final AlertDialog dialog = builder.create();
			final CheckBox doubleBox = (CheckBox)dialogView.findViewById(R.id.doubleCheckbox);
			final CheckBox doubleOrNothingBox = (CheckBox)dialogView.findViewById(R.id.doubleOrNothingCheckbox);
			Button cancelButton = (Button)dialogView.findViewById(R.id.cancelButton);
			cancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
			Button sendButton = (Button)dialogView.findViewById(R.id.sendButton);
			sendButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String address = prefs.getString(EnterCodeActivity.SERVER_ADDRESS_KEY, null);
					String code = prefs.getString(EnterCodeActivity.CODE_KEY, null);
					if (address == null) {
						Toast.makeText(QuizQuestionActivity.this, "No server address, restart application!", Toast.LENGTH_LONG).show();
						return;
					}
					service.sendAnswer(address, code, questionId, "" + itemPosition, doubleBox.isChecked(), doubleOrNothingBox.isChecked());
					dialog.cancel();
				}
			});
			dialog.show();
		}
	}

	@Override public void messageSendingError(String sentMessage, Address destination, String messageType) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(QuizQuestionActivity.this, "Error sending message!", Toast.LENGTH_LONG).show();
			}
		});
	}
	@Override public void messageSendingSuccess(String sentMessage, Address destination, String messageType) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(QuizQuestionActivity.this, "Message sent succesfully!", Toast.LENGTH_LONG).show();
			}
		});
	}

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
			questionText.setText(question);
			if (answers != null) {
				answerList.setVisibility(View.VISIBLE);
				answerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, answers));
			} else {
				answerList.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void pingReceived(Address source, PingMessage ping) {}

	@Override
	public void timeoutReceived(Address sender, TimeoutMessage timeout) {
		questionId = null;
		final String questionString = "Time's up";
		final String[] answers = null;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(QuizQuestionActivity.this, "Response time is over", Toast.LENGTH_LONG).show();
				updateUi(questionString, answers);
			}
		});
	}
}