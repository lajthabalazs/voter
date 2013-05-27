package hu.edudroid.quiz;

import hu.edudroid.quiz_engine.AnswerMessage;
import hu.edudroid.quiz_engine.PingMessage;
import hu.edudroid.quiz_engine.QuestionMessage;
import hu.edudroid.quiz_engine.QuizPeer;
import hu.edudroid.quiz_engine.QuizPeerListener;
import it.unipr.ce.dsg.s2p.sip.Address;

import java.util.HashSet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class QuizService extends Service implements QuizPeerListener {
	private static final int SERVICE_ID = 0;
	
	private QuizServiceBinder binder = new QuizServiceBinder();
	private QuizPeer peer;
	private HashSet<QuizPeerListener> listeners = new HashSet<QuizPeerListener>();
	private NotificationManager notificationManager;
	private Notification notification;
	private String question;
	private String questionId;
	private String[] answers;
	
		
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("ChatService", "Service created.");
		 // Set the icon, scrolling text and timestamp
		notification = new Notification(R.drawable.ic_launcher, "S2P chat is active", System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, QuizQuestionActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "S2P chat is active",
        		"S2P chat is active", contentIntent);
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(SERVICE_ID, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startForeground(SERVICE_ID, notification);
		return START_STICKY;
	}

	public int startPeer(final String peerName, final int peerPort) {
		// Get parameters from Intent
		if ((peerName == null) || (peerPort == 0)) {
			return -1;
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (peer != null) {
					peer.halt();
				}
				peer = new QuizPeer(peerName, peerName, peerPort);
				peer.registerListener(QuizService.this);
			}
		}).start();
		
		return 0;
	}
	
	public void stopPeer() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (peer != null) {
					peer.halt();
				}
			}
		}).start();
	}

	public void sendAnswer(final String address, final String code, final String questionId, final String answer) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				peer.sendAnswer(new Address(address), code, questionId, answer);
			}
		}).start();
	}

	public void sendPing(final String address, final String code) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				peer.sendPing(new Address(address), code);
			}
		}).start();

	}

	@Override
	public void onDestroy() {
		if (peer != null) {
			peer.halt();
		}
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void registerListener(QuizPeerListener listener) {
		listeners.add(listener);
	}
	
	public void unregisterListener(QuizPeerListener listener) {
		listeners.remove(listener);
	}

	public class QuizServiceBinder extends Binder {
		public QuizService getService() {
			return QuizService.this;
		}
	}
	
	public String getLocalAddress() {
		if (peer != null) {
			return peer.getLocalAddressString();
		} else {
			return "Not connected";
		}
	}

	@Override public void messageSendingError(String sentMessage, Address destination, String messageType) {}
	@Override public void messageSendingSuccess(String sentMessage, Address destination, String messageType) {}

	@Override
	public void answerReceived(Address source, AnswerMessage answer) {
	}

	@Override
	public void pingReceived(Address source, PingMessage ping) {
	}

	@Override
	public void questionReceived(Address source, QuestionMessage question) {
		this.question = question.getQuestion();
		this.answers = question.getAnswers();
		this.questionId = question.getQuestionId();
	}

	public String getQuestion() {
		return question;
	}

	public String[] getAnswers() {
		return answers;
	}

	public String getQuestionId() {
		return questionId;
	}

}