package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Frame extends JFrame
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Container c; 
	
	Container c1;
	Container c2;
	Container c3;
	
	//First area
	JLabel linkLabel;
	JTextField link; 
	
	//Second area
	
	Container c21;
	Container c22;
	
	JLabel veryStandardLabel;
	JCheckBox checkLink;
	JCheckBox checkActors;
	JCheckBox checkStates;
	JCheckBox checkEdges;
	JCheckBox checkMessages;
	
	//TODO potentially further to be added
	
	JLabel veryExpertLabel;
	JCheckBox checkCycle;
	JTextField cycleNumber;
	JCheckBox checkMultiplicity;
	JCheckBox checkPlaceholder;
	
	//Thrird arrea
	
	JButton run;
	
	
	
	public Frame ()
	{
		c = this.getContentPane();
		c.setLayout(new GridLayout(3,1,0,5));
		
		
		c1 = new Container();
		c2 = new Container();
		c3 = new Container();
		
		//First area
		
		c1.setLayout(new GridLayout(2,1,20,20));
		
		JLabel linkLabel = new JLabel("Please insert the XML file of the S-BPM model (both abstract and implemented");
		link = new JTextField("Insert XML file containing abstract + implemented model",2);
		
		c1.add(linkLabel);
		c1.add(link);
		
		//Second area
		
		c2.setLayout(new GridLayout(1,2));
		
		Container c21 = new Container();
		Container c22 = new Container();
		
		c21.setLayout(new GridLayout(6,1)); //TODO 6 adjust
		
		this.veryStandardLabel = new JLabel("Standard Verification: ");
		this.checkLink = new JCheckBox("Link of .owl file?");
		this.checkActors = new JCheckBox("Actors correctly implemented?");
		this.checkStates = new JCheckBox("States correctly implemented?");
		this.checkEdges = new JCheckBox("Edges correctly implemented?");
		this.checkMessages = new JCheckBox("Messages correctly implemented?");
		
		c21.add(veryStandardLabel);
		c21.add(checkLink);
		c21.add(checkActors);
		c21.add(checkStates);
		c21.add(checkEdges);
		c21.add(checkMessages);
		
		
		c22.setLayout(new GridLayout(6,1)); //TODO 6 adjust
		
		c22.setLayout(new GridLayout(6,1)); //TODO 6 adjust
		
		this.veryExpertLabel = new JLabel("Expert Verification: ");
		this.checkCycle = new JCheckBox("Cycle verification on?");
		this.cycleNumber = new JTextField("Type here number of cycles allowed");
		this.checkMultiplicity = new JCheckBox("Multiplicity verification on?");
		this.checkPlaceholder = new JCheckBox("Placeholder verification on?");
		
		c22.add(veryExpertLabel);
		c22.add(checkCycle);
		c22.add(cycleNumber);
		c22.add(checkMultiplicity);
		c22.add(checkPlaceholder);
	
		
		c2.add(c21);
		c2.add(c22);
		
		//Third area
		
		c3.setLayout(new GridLayout(1,1));
		
		run = new JButton("RUN VERIFICATION");
		
		c3.add(run);
		
		
		c.add(c1);
		c.add(c2);
		c.add(c3);
		
		
		
		
		
	}
	
	public class RunListener implements ActionListener {

		public void actionPerformed (ActionEvent e)
		{
			
			//Standard verification
			
			if(checkLink.isSelected())
			{
				//TODO insert method
			}
			
			if(checkActors.isSelected())
			{
				//TODO insert method
			}
			
			if(checkStates.isSelected())
			{
				//TODO insert method
			}
			
			if(checkEdges.isSelected())
			{
				//TODO insert method
			}
			
			if(checkMessages.isSelected())
			{
				//TODO insert method
			}
			
			//Expert verification
			
			if(checkCycle.isSelected())
			{
				//TODO insert method
			}
			
			if(checkMultiplicity.isSelected())
			{
				//TODO insert method
			}
			
			if(checkPlaceholder.isSelected())
			{
				//TODO insert method
			}
			
		}
		
	}

}
