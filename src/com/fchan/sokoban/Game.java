package com.fchan.sokoban;
import java.awt.FlowLayout;

import javax.swing.JFrame;


public class Game {
	
	public static void main(String[] args) {
		JFrame f = new JFrame("CCPS109 Final Project Sokoban Game ");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		f.setLayout(new FlowLayout());

		f.add(new Sokoban("m1.txt"));
		f.pack();
		f.setVisible(true);
		
		
	}

}
