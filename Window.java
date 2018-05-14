import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.util.*;

import java.awt.*;
import javax.swing.*;

import java.awt.Dimension;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.*;
import java.io.*;

import java.util.Hashtable;

public class Window extends JFrame
{
	public Window()
	{
		this.setTitle("Blockchain");
		this.setSize(820, 550);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setTitle(String addr, String port)
	{
		this.setTitle("Server: "+ addr +":"+port);
	}

	public void blockchain(ArrayList<Block> blocks, int wt)
	{
/*
		menu_color= new Color(107, 107, 107);
		txt_color= new Color(255, 255, 255);
*/

		JPanel server = new JPanel();
		server.setPreferredSize(new Dimension(800, 500));
		server.setLayout(new BorderLayout());


   		JPanel waiting = new JPanel();
   		String str = "Wainting transaction" + ((wt>1)?"s":"") + ": " + Integer.toString(wt);
    	JLabel text = new JLabel(str);
  		text.setFont(new Font("Ariam",1,17));
  		text.setForeground(Color.WHITE);
		text.setPreferredSize(new Dimension(800, 50));
		waiting.setBackground(new Color(107, 107, 107));
		waiting.add(text);

		server.add("North", waiting);


		JPanel content = new JPanel();
		content.setBackground(Color.WHITE);
		content.setPreferredSize(new Dimension(800, 450));

		server.add("Center", content);

		GridBagConstraints gbc = new GridBagConstraints();
		content.setLayout(new GridBagLayout());
		if (blocks!=null)
		{
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.ipadx = 15;
			gbc.ipady = 15;
			int i=0;
			for (Block block : blocks) 
			{
				JPanel blockContent = new JPanel();        
				blockContent.setBackground(new Color(107, 107, 107));
				GridBagConstraints blockGBC = new GridBagConstraints();
				blockContent.setLayout(new GridBagLayout());
				blockGBC.gridheight = 1;
				blockGBC.gridwidth = 1;
				blockGBC.gridx = 0;
				blockGBC.gridy = 0;
		        blockContent.add(new JLabel("Block :"),blockGBC);
				blockGBC.gridx = 1;
				blockContent.add(Box.createRigidArea(new Dimension(5,0)));
				blockGBC.gridx = 2;
		        blockContent.add(new JLabel(""+i),blockGBC);    
				blockGBC.gridy = 1;
		        blockContent.add(new JLabel(""+block.nbTransactions()),blockGBC);
				blockGBC.gridx = 1;
				blockContent.add(Box.createRigidArea(new Dimension(5,0)));
				blockGBC.gridx = 0;
		        blockContent.add(new JLabel("transactions :"),blockGBC);
				blockGBC.gridy = 2;
				String hash =block.getHash();
				if(hash.equals(""))
		        	blockContent.add(new JLabel(""),blockGBC);
				else
		        	blockContent.add(new JLabel(hash.substring(0, 13)),blockGBC	);
				gbc.gridx = i%5;
				gbc.gridy = i/5;
				content.add(blockContent, gbc);
				i++;
			}
		}


		this.setContentPane(server);
		this.setVisible(true);
	}


}
