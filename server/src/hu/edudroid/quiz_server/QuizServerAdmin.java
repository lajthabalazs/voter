package hu.edudroid.quiz_server;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class QuizServerAdmin extends JFrame implements ActionListener {
	
	public static void main(String[] args) {
		new QuizServerAdmin();
	}

	private static final long serialVersionUID = 937448270379393937L;
	private static final String BROWSE = "BROWSE";
	private static final String START_QUIZ = "START_QUIZ";
	private static final Object STOP_QUIZ = "STOP_QUIZ";
	
	private static final int MAX_LOG_LENGTH = 20000;
	private JTextField fileNameField;
	private JTextArea logArea;
	private JFileChooser fileChooser = new JFileChooser();
	private boolean running = false;
	private JButton peerButton;
	
	public QuizServerAdmin() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		Container contentPane = getContentPane();
		contentPane.setLayout(springLayout);
		JLabel fileNameLabel = new JLabel("Config file");
		fileNameField = new JTextField("",15);
		JButton browseButton = new JButton("Browse");
		
		peerButton = new JButton("Start quiz");
		logArea = new JTextArea(20,40);
		logArea.setEditable(false);
		JScrollPane logScroll = new JScrollPane(logArea);
		contentPane.add(fileNameLabel);
		contentPane.add(fileNameField);
		contentPane.add(browseButton);
		browseButton.setActionCommand(BROWSE);
		browseButton.addActionListener(this);
		contentPane.add(peerButton);
		peerButton.setActionCommand(START_QUIZ);
		peerButton.addActionListener(this);
		contentPane.add(logScroll);
		springLayout.putConstraint(SpringLayout.WEST, fileNameLabel, 5, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, fileNameField, 5, SpringLayout.EAST, fileNameLabel);		
		springLayout.putConstraint(SpringLayout.WEST, browseButton, 5, SpringLayout.EAST, fileNameField);

		springLayout.putConstraint(SpringLayout.WEST, peerButton, 5, SpringLayout.EAST, browseButton);
		
		springLayout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.EAST, peerButton);

		springLayout.putConstraint(SpringLayout.WEST, logScroll, 5, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, logScroll, -5, SpringLayout.EAST, contentPane);
		
		springLayout.putConstraint(SpringLayout.NORTH, browseButton, 5, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, peerButton, 5, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, logScroll, 5, SpringLayout.SOUTH, peerButton);
		springLayout.putConstraint(SpringLayout.SOUTH, contentPane, 5, SpringLayout.SOUTH, logScroll);
		springLayout.putConstraint(SpringLayout.BASELINE, fileNameLabel, 0, SpringLayout.BASELINE, browseButton);
		springLayout.putConstraint(SpringLayout.BASELINE, fileNameField, 0, SpringLayout.BASELINE, browseButton);
		
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (actionCommand.equals(BROWSE)) {
			int result = fileChooser.showOpenDialog(getContentPane());
			if (result == JFileChooser.APPROVE_OPTION) {
				File configFile = fileChooser.getSelectedFile();
				fileNameField.setText(configFile.getAbsolutePath());				
			}
		} else if (actionCommand.equals(START_QUIZ)){
			if (!running) {
				try{
					running = true;
					
					QuizGame model = new QuizGame(fileNameField.getText());
					QuizServer server = new QuizServer(model);
					new QuizLiveFrame(server, model);
					log("Peer created ");
				} catch(Exception e) {
					log("Error creating peer with config " + fileNameField.getText() + " : " + e);
				}
			}
		} else if (actionCommand.equals(STOP_QUIZ)){
			
		}
	}
	
	private void log(String message){
		String text = logArea.getText().substring(0, Math.min(MAX_LOG_LENGTH, logArea.getText().length()));
		logArea.setText(message + "\n" + text);
	}
}
