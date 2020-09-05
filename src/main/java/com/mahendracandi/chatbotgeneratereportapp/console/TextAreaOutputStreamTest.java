package com.mahendracandi.chatbotgeneratereportapp.console;

import java.awt.BorderLayout;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class TextAreaOutputStreamTest extends JPanel{
	private JTextArea textArea = new JTextArea(25, 50);
	private TextAreaOutputStream taOutputStream = new TextAreaOutputStream(
	        textArea, "> ");

	public TextAreaOutputStreamTest() {
	    setLayout(new BorderLayout());
	    add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
	    System.setOut(new PrintStream(taOutputStream));

	}

	private static void createAndShowGui() {
	    JFrame frame = new JFrame("Output");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add(new TextAreaOutputStreamTest());
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}

	public static void mainRunner(String[] args) {
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            createAndShowGui();
	        }
	    });
	}
}
