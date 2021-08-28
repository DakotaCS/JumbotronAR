import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.JMenuBar;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.Duration;
import java.awt.event.ActionEvent;

public class JumbotronAR
{
	

	private JFrame frame;
	long lastUpdate;
	long interval = 1001; 
	boolean isMuted = false; 
	long d; 
	//This creates a default timer which ticks every 1 seconds, and runs for 20 minutes.
		Timer1 twoSecondTimer = new ExampleTimer(interval, d);
	
     JLabel timer1 = new JLabel("20 : 00");
		JLabel scoreHome = new JLabel("0");
		JLabel scoreGuest = new JLabel("0"); 
		private final JMenuBar menuBar = new JMenuBar();
		private final JButton btnNewButton = new JButton("Increment Home");
		private final JButton btnNewButton_1 = new JButton("Decrement Home");
		private final JButton btnNewButton_2 = new JButton("Increment Guest");
		private final JButton btnDecrementGuest = new JButton("Decrement Guest");
		private final JButton btnReset = new JButton("Reset Scores");
		private final JButton btnStartTimer = new JButton("Start Timer");
		private final JButton btnPauseTimer = new JButton("Pause Timer");
		private final JButton btnResetTimer = new JButton("Reset Timer");
		private final JButton btnmin = new JButton("+1min");
		private final JButton btnmin_1 = new JButton("-1min");
		private final JButton btnsec = new JButton("+5sec");
		private final JButton btnsec_1 = new JButton("-5sec");
		private final JButton btnMuteunmute = new JButton("Mute/Unmute");
		private final JButton btnExit = new JButton("Exit");
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JumbotronAR window = new JumbotronAR();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JumbotronAR() 
	{
		twoSecondTimer.duration =  1200000l;
		initialize();
		
	    
	}	
	
	public void reset()
	{
		scoreGuest.setText("0");
		scoreHome.setText("0");
	}
	
	
	//increment guest score 
	public void incrementGuestScore()
	{
		String score = scoreGuest.getText(); 
		int score1 = 0; 
		//twoSecondTimer.pause();
		try
		{
			if(isMuted = false)
			{
				try 
				{
					AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/toronto_goal_horn_clip.wav").getAbsoluteFile());
					Clip clip = AudioSystem.getClip();
					clip.open(audioInputStream);
					clip.start();
				} 
				catch(Exception ex) 
				{
					System.out.println("Error with playing sound.");
					ex.printStackTrace();
				}
			}
			
			score1 = Integer.parseInt(score); 
			score1++; 
			score= String.valueOf(score1); 
			scoreGuest.setText(score);
		
		}
		catch(Exception e)
		{
			System.out.println("That didn't work");
		}
	}
	//decrement guest score 
		public void decrementGuestScore()
		{
			String score = scoreGuest.getText(); 
			int score1 = 0; 
			try
			{
				score1 = Integer.parseInt(score); 
				score1--; 
				score= String.valueOf(score1); 
				scoreGuest.setText(score);
			}
			catch(Exception e)
			{
				System.out.println("That didn't work");
			}
		}
	//increment home score 
	public void incrementHomeScore()
	{
		String score = scoreHome.getText(); 
		int score1 = 0; 
		//twoSecondTimer.pause();
		try
		{
			if(isMuted = false)
			{
					try 
					{
						AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/tampa_goal_horn_clip.wav").getAbsoluteFile());
						Clip clip = AudioSystem.getClip();
						clip.open(audioInputStream);
						clip.start();
					} 
					catch(Exception ex) 
					{
						System.out.println("Error with playing sound.");
						ex.printStackTrace();
					}
			}
			
			score1 = Integer.parseInt(score); 
			score1++; 
			score= String.valueOf(score1); 
			scoreHome.setText(score);
		}
		
		catch(Exception e)
		{
			System.out.println("That didn't work");
		}
	}
	//decrement home score 
	public void decrementHomeScore()
	{
		String score = scoreHome.getText(); 
		int score1 = 0; 
		try
		{
			score1 = Integer.parseInt(score); 
			score1--; 
			score= String.valueOf(score1); 
			scoreHome.setText(score);
		}
		catch(Exception e)
		{
			System.out.println("That didn't work");
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setBounds(100, 100, 1800, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{412, 552, 443, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 225, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblGuest = new JLabel("HOME");
		lblGuest.setForeground(Color.WHITE);
		lblGuest.setBackground(Color.WHITE);
		lblGuest.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 200));
		GridBagConstraints gbc_lblGuest = new GridBagConstraints();
		gbc_lblGuest.anchor = GridBagConstraints.NORTH;
		gbc_lblGuest.insets = new Insets(0, 0, 5, 5);
		gbc_lblGuest.gridx = 0;
		gbc_lblGuest.gridy = 5;
		frame.getContentPane().add(lblGuest, gbc_lblGuest);
		
		JLabel lblNewLabel = new JLabel("GUEST");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 200));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 5;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setForeground(Color.WHITE);
		panel.setBackground(Color.DARK_GRAY);
		panel.setBorder(new LineBorder(Color.WHITE, 10, true));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 6;
		frame.getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{552, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		timer1.setBackground(Color.DARK_GRAY);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		panel.add(timer1, gbc_lblNewLabel_1);
		timer1.setVerticalAlignment(SwingConstants.TOP);
		timer1.setForeground(Color.WHITE);
		timer1.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 250));
		
		scoreHome.setVerticalAlignment(SwingConstants.TOP);
		scoreHome.setForeground(Color.RED);
		scoreHome.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 500));
		scoreHome.setBackground(Color.WHITE);
		GridBagConstraints gbc_lblGuest_1 = new GridBagConstraints();
		gbc_lblGuest_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblGuest_1.gridx = 0;
		gbc_lblGuest_1.gridy = 7;
		frame.getContentPane().add(scoreHome, gbc_lblGuest_1);
		
		scoreGuest.setVerticalAlignment(SwingConstants.TOP);
		scoreGuest.setForeground(Color.RED); 
		scoreGuest.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 500));
		scoreGuest.setBackground(Color.WHITE);
		GridBagConstraints gbc_lblGuest_1_1 = new GridBagConstraints();
		gbc_lblGuest_1_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblGuest_1_1.gridx = 2;
		gbc_lblGuest_1_1.gridy = 7;
		frame.getContentPane().add(scoreGuest, gbc_lblGuest_1_1);
		
		frame.setJMenuBar(menuBar);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				incrementHomeScore(); 
			}
		});
		menuBar.add(btnNewButton);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				decrementHomeScore(); 
			}
		});
		
		menuBar.add(btnNewButton_1);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				incrementGuestScore(); 
			}
		});
		
		menuBar.add(btnNewButton_2);
		btnDecrementGuest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				decrementGuestScore(); 
			}
		});
		
		menuBar.add(btnDecrementGuest);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				scoreHome.setText("0");
				scoreGuest.setText("0");
			}
		});
		
		menuBar.add(btnReset);
		btnStartTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				twoSecondTimer.start();
			}
		});
		
		menuBar.add(btnStartTimer);
		btnPauseTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				twoSecondTimer.pause();
			}
		});
		
		menuBar.add(btnPauseTimer);
		btnResetTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				twoSecondTimer.cancel();
				timer1.setText("20 : 00");
				d = 1200000l; 
			}
		});
		
		menuBar.add(btnResetTimer);
		
		menuBar.add(btnmin);
		
		menuBar.add(btnmin_1);
		
		menuBar.add(btnsec);
		
		menuBar.add(btnsec_1);
		btnMuteunmute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(isMuted = false)
				{
					isMuted = true; 
				}
				else if (isMuted = true)
				{
					isMuted = false; 
				}
			}
		});
		
		menuBar.add(btnMuteunmute);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				System.exit(0);
			}
		});
		
		menuBar.add(btnExit);
			
	}
	
	@SuppressWarnings("serial")
	public class ExampleTimer extends Timer1{
		
		public ExampleTimer() {
			super();
		}
		
		public ExampleTimer(long interval, long duration){
			super(interval, duration);
		}

		@Override
		protected void onTick() 
		{	
			Duration d = Duration.ofMillis(getRemainingTime());
			currentTime(d); 
			
		}

		protected String currentTime(Duration d)
		{
			long minutesPart = d.toMinutes(); 
			long secondsPart = d.minusMinutes( minutesPart ).getSeconds() ;
			 
			String str = String.format("%02d", minutesPart); 
			String str2 = String.format("%02d", secondsPart);
			timer1.setText(str + " : " + str2);
			return str + " : " + str2; 
		}
		
		@Override
		protected void onFinish() 
		{
		}
	}
	 
	
}

