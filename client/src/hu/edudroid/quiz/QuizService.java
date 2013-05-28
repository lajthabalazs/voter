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

	private static final int QUIZ_PEER_PORT = 56221;
	
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
		startPeer("QuizPeer", QUIZ_PEER_PORT);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startForeground(SERVICE_ID, notification);
		return START_STICKY;
	}

	private int startPeer(final String peerName, final int peerPort) {
		// Get parameters from Intent
		if ((peerName == null) || (peerPort == 0)) {
			return -1;
		}
		Log.e("ChatService", "Starting peer");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (peer != null) {
					peer.halt();
				}
				peer = new QuizPeer(peerName, peerName, peerPort, new Base64CoderAndroid());
				peer.registerListener(QuizService.this);
				Log.e("ChatService", "Peer started");
			}
		}).start();
		
		return 0;
	}
	
	public void sendAnswer(final String address, final String code, final String questionId, final String answer) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				peer.sendAnswer(new Address(address, QuizPeer.SERVER_PORT), code, questionId, answer);
			}
		}).start();
	}

	public void sendPing(final String address, final String code) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 5; i++) {
					if (peer != null) {
						peer.sendPing(new Address(address, QuizPeer.SERVER_PORT), code);
						Log.e("Ping message sent", "Code " + code);
						break;
					} else {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
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

	@Override public void messageSendingError(String sentMessage, Address destination, String messageType) {
		Log.e("Message sending error", sentMessage + " " + messageType);
	}
	@Override public void messageSendingSuccess(String sentMessage, Address destination, String messageType) {
		Log.i("Message sending success", sentMessage);		
	}

	@Override
	public void answerReceived(Address source, AnswerMessage answer) {
	}

	@Override
	public void pingReceived(Address source, PingMessage ping) {
	}

	@Override
	public void questionReceived(Address source, QuestionMessage question) {
		this.questionId = question.getQuestionId();
		this.question = question.getQuestion();
		this.answers = question.getAnswers();
		Log.e("Question received", question.getQuestion());
		for (QuizPeerListener listener : listeners) {
			listener.questionReceived(source, question);
		}
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