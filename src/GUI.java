import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
public class GUI extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 250;
	private static final int HEIGHT = 100;
	private JLabel nameL;
	private JLabel versionL;
	private JTextField nameF;
	private JButton undemo;
	private ChangeButtonHandler Handler;
	private JComboBox version;
	private JLabel msg;
	@SuppressWarnings({ })
	public GUI()
	{
		setTitle("Minecraft UnDemo");
		Container pane = getContentPane();
		pane.setLayout(new GridLayout(3, 2));
		nameL = new JLabel("Enter Username:", SwingConstants.RIGHT);
		versionL = new JLabel("Select Version:", SwingConstants.RIGHT);
		msg = new JLabel("");
		nameF = new JTextField(10);
		undemo = new JButton("UnDemo!");
		Handler = new ChangeButtonHandler();
		undemo.addActionListener(Handler);
		version = new JComboBox(Main.getVersions());
		pane.add(nameL);
		pane.add(nameF);
		pane.add(versionL);
		pane.add(version);
		pane.add(msg);
		pane.add(undemo);
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	private class ChangeButtonHandler implements ActionListener
	    {
	       public void actionPerformed(ActionEvent e)
	        {
	    	   if(Main.check((String)version.getSelectedItem(), nameF.getText())){
	    		   msg.setText("ERROR INVALID");
	    	   }else{
	    	   		try {
						Main.patchJSON((String)version.getSelectedItem(), nameF.getText());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	    	   		try {
						Main.copyJar((String)version.getSelectedItem());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	    		   msg.setText("Done!");
	    	   
	        }}}}
	    

