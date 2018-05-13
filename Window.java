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
		//ui();
	}

	public void blockchain(ArrayList<Block> blocks)
	{
		this.setTitle("Client Blockchain");
		this.setSize(820, 550);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
/*
		menu_color= new Color(107, 107, 107);
		txt_color= new Color(255, 255, 255);
*/

		JPanel content = new JPanel();
		content.setBackground(Color.WHITE);
		content.setPreferredSize(new Dimension(800, 500));

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
				i++;
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
		        	blockContent.add(new JLabel(hash.substring(0, 5)+"..."+hash.substring(hash.length()-8, hash.length())),blockGBC);
				gbc.gridx = i%5;
				gbc.gridy = i/5;
				content.add(blockContent, gbc);
			}
		}


		this.setContentPane(content);
		this.setVisible(true);
	}


	public void ui()
	{  
		this.setTitle("Client Blockchain");
		this.setSize(820, 550);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Color menu_color= new Color(107, 107, 107);
		Color txt_color= new Color(255, 255, 255);

		/**************************************
		 *
		 *		  GESTION DES BOUTONS
		 *
		***************************************/

		JButton server_btn = new JButton("Connect to Server");
		JButton tran_btn = new JButton("New Transaction");

		server_btn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				
				System.out.println("<Connect></Connect>");
			}
		});

		tran_btn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				System.out.println("New Transaction");
			}
		});

		JPanel cmd = new JPanel();
		cmd.setBackground(menu_color);
		cmd.setPreferredSize(new Dimension(250, 500));

		/**************************************
		 *
		 *		  GESTION DE L'AFFICHAGE
		 *
		***************************************/


		JPanel content = new JPanel();
		content.setBackground(Color.WHITE);
		content.setPreferredSize(new Dimension(800, 500));

		GridBagConstraints gbc = new GridBagConstraints();
		content.setLayout(new GridBagLayout());
		cmd.setLayout(new GridBagLayout());

		/*
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		content.add(cmd, gbc);
		gbc.gridx = 1;
		content.add(log, gbc);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipady = 10;
		cmd.add(server_btn, gbc);
		gbc.gridy = 1;
		cmd.add( Box.createVerticalGlue(),gbc);
		gbc.gridy = 2;
		cmd.add(tran_btn, gbc);*/


		this.setContentPane(content);
		this.setVisible(true);
	}  

}
