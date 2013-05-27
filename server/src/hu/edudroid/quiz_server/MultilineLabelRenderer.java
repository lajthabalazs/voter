package hu.edudroid.quiz_server;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class MultilineLabelRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 5229385569943296946L;
	
	final JLabel label = new JLabel();

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean hasFocus) {
		final String text = (String) value;
		label.setFont(new Font(Font.SERIF, Font.PLAIN, 20));	
		//label.setText("<html><body style='width: 200px;'>" + text + "</body></html>");
		label.setText("<html><body>" + text + "</body></html>");
		//label.setText("<html><body>" + text + "</body></html>");
		return label;
	}
}