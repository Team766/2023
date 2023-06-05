package com.team766.robot.mechanisms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.*;
import com.team766.logging.*;

public class jFrame extends JFrame implements ActionListener {
	public januaryTag JanuaryTag;
	public JFrame frame;
	JButton click;
	public jFrame(){
		super("MotorController");
		loggerCategory = Category.MECHANISMS;
		JanuaryTag = new januaryTag();
		frame = new JFrame();
		click =  new JButton("Play Again");
		frame.add(click);

		click.addActionListener(this);
		frame.getRootPane().setDefaultButton(click); // sets default button
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		JanuaryTag.debugLogs();
		ArrayList<Double> arr = new ArrayList<Double>();
		arr = JanuaryTag.getTransform3dData();
		for(int i = 0; i < arr.size(); i++){
			log("" + arr.get(i));
		}
	}
	
}
