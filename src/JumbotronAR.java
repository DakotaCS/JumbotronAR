import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;



public class JumbotronAR
{
	public class serialinput implements SerialPortEventListener {
		SerialPort serialPort;
	        /** The port we're normally going to use. */
		private final String PORT_NAMES[] = { 
				"/dev/tty.usbserial-A9007UX1", // Mac OS X
	                        "/dev/ttyACM0", // Raspberry Pi
				"/dev/ttyUSB0", // Linux
				"COM3", // Windows
		};
		/**
		* A BufferedReader which will be fed by a InputStreamReader 
		* converting the bytes into characters 
		* making the displayed results codepage independent
		*/
		private BufferedReader input;
		/** The output stream to the port */
		@SuppressWarnings("unused")
		private OutputStream output;
		/** Milliseconds to block while waiting for port open */
		private static final int TIME_OUT = 2000;
		/** Default bits per second for COM port. */
		private static final int DATA_RATE = 115200;
		
		public String serialinput = ""; 

		public void initialize() {
	                // the next line is for Raspberry Pi and 
	                // gets us into the while loop and was suggested here:  https://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
	                //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

			CommPortIdentifier portId = null;
			@SuppressWarnings("rawtypes")
			Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

			//First, Find an instance of serial port as set in PORT_NAMES.
			while (portEnum.hasMoreElements()) {
				CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
				for (String portName : PORT_NAMES) {
					if (currPortId.getName().equals(portName)) {
						portId = currPortId;
						break;
					}
				}
			}
			if (portId == null) {
				System.out.println("Could not find COM port.");
				return;
			}

			try {
				// open serial port, and use class name for the appName.
				serialPort = (SerialPort) portId.open(this.getClass().getName(),
						TIME_OUT);

				// set port parameters
				serialPort.setSerialPortParams(DATA_RATE,
						SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				// open the streams
				input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
				output = serialPort.getOutputStream();

				// add event listeners
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}

		/**
		 * This should be called when you stop using the port.
		 * This will prevent port locking on platforms like Linux.
		 */
		public synchronized void close() {
			if (serialPort != null) {
				serialPort.removeEventListener();
				serialPort.close();
			}
		}

		/**
		 * Handle an event on the serial port. Read the data and print it.
		 */
		public synchronized void serialEvent(SerialPortEvent oEvent) {
			if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				try {
					serialinput=input.readLine();
					System.out.println(serialinput);
					
					if (serialinput.equals("t"))
					{
						incrementOneMin(); 
					}
					else if (serialinput.equals("o"))
					{
						decrementOneMin(); 
					}
					else if (serialinput.equals("e"))
					{
						incrementOneSec(); 
					}
					else if (serialinput.equals("n"))
					{
						decrementOneSec(); 
					}
					else if (serialinput.equals("r"))
					{
						reset();  
					}
					else if (serialinput.equals("s"))
					{
						pause(); 
					}
					else if (serialinput.equals("a"))
					{
						start(); 
					}
					
					else if (serialinput.equals("c"))
					{
						increaseHome(); 
					}
					else if (serialinput.equals("g"))
					{
						decreaseHome(); 
					}
					else if (serialinput.equals("i"))
					{
						increaseGuest(); 
					}
					else if (serialinput.equals("d"))
					{
						decreaseGuest(); 
					}
					else if (serialinput.equals("z")) 
					{
						resetScore(); 
					}
					else if (serialinput.equals("h"))
					{
						toggleHorn(); 
					}
					
					
				} catch (Exception e) {
					System.err.println(e.toString());
				}
			}
		}
		
		public void pause() 
		{
			twoSecondTimer.pause();
		}

		private void toggleHorn() 
		{
			 if(!isClicked)
			   {
			         isClicked = true;
					   toronto = "src/tgh.wav"; 
						tampa = "src/tbgh.wav"; 
						eop = "src/nhl_eop.wav"; 
			         
			   }
			   else 
			   {
					toronto = ""; 
					tampa = ""; 
					eop = ""; 
			   }
			
		}
		
		public void reset()
		{
			twoSecondTimer.cancel();
			twoSecondTimer.duration = 0; 
			timer1.setText("00 : 00"); 
		}

		private void resetScore() 
		{
			scoreGuest.setText("0");
			scoreHome.setText("0");	
		}

		private void decreaseGuest() 
		{
			decrementGuestScore(); 
			
		}

		private void increaseGuest() 
		{
			incrementGuestScore(); 
			
		}

		private void decreaseHome() 
		{
			decrementHomeScore(); 
		}

		private void increaseHome() 
		{
			incrementHomeScore(); 
		}

		private void start() 
		{
			if(twoSecondTimer.duration <= 0)
			{
				twoSecondTimer.cancel();
			}
			else
			{
				twoSecondTimer.start();
			}
			
		}

		private void decrementOneSec() 
		{
			if (twoSecondTimer.duration > 5000)
			{
			twoSecondTimer.pause();
			twoSecondTimer.duration -= 1000; 
			timer1.setText(twoSecondTimer.currentTime(twoSecondTimer.getRemainingTime()));
			}
		}

		private void incrementOneSec() 
		{
			twoSecondTimer.pause();
			twoSecondTimer.duration += 1000; 
			timer1.setText(twoSecondTimer.currentTime(twoSecondTimer.getRemainingTime()));
			
		}

		private void decrementOneMin() 
		{
			if (twoSecondTimer.duration > 60000)
			{
				twoSecondTimer.pause();
				twoSecondTimer.duration -= 60000; 
				timer1.setText(twoSecondTimer.currentTime(twoSecondTimer.getRemainingTime()));
			}
			
		}

		private void incrementOneMin() 
		{
			twoSecondTimer.pause();
			twoSecondTimer.duration += 60000; 
			timer1.setText(twoSecondTimer.currentTime(twoSecondTimer.getRemainingTime())); 
			
		}
	 
	}

	private JFrame frame;
	long lastUpdate;
	long interval = 1000; 
	long d = 0; 
	//This creates a default timer which ticks every 1 seconds, and runs for 20 minutes.
		Timer1 twoSecondTimer = new ExampleTimer(interval, d);
		
	String toronto = ""; 
	String tampa = ""; 
	String eop = "src/nhl_eop.wav"; 
	boolean isClicked = false;
	
     JLabel timer1 = new JLabel("00 : 00");
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
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				try {
					JumbotronAR window = new JumbotronAR();
					JumbotronAR.serialinput main = window.new serialinput(); 
					
					
					//window.menuBar.hide();
					main.initialize();
				      GraphicsEnvironment graphics =
				    	      GraphicsEnvironment.getLocalGraphicsEnvironment();
				    	      GraphicsDevice device = graphics.getDefaultScreenDevice();
				    	      
				    window.frame.setVisible(true);
				    window.frame.setResizable(true);
				    device.setFullScreenWindow(window.frame);
					
					Thread t=new Thread() {
						public void run() {
							//the following line will keep this app alive for 1000 seconds,
							//waiting for events to occur and responding to them (printing incoming messages to console).
							try 
							{
								Thread.sleep(1000000);
								System.out.println(main.serialinput + "From jumbotron"); 
								
							} 
							catch (InterruptedException ie) 
							{
								
							}
						}
					};
					t.start();
					System.out.println("Started");
					
					
					
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
		twoSecondTimer.duration =  0;
		initialize();
		
	    
	}	
	
	//increment guest score 
	public void incrementGuestScore()
	{
		String score = scoreGuest.getText(); 
		int score1 = 0; 
		try
		{
			{
				try 
				{
					AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(toronto).getAbsoluteFile());
					Clip clip = AudioSystem.getClip();
					clip.open(audioInputStream);
					clip.start();
				} 
				catch(Exception ex) 
				{
					System.out.println("Error with playing sound. (Toronto)");
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
				if (score1 <= 0 )
				{
					throw new Exception(); 
				}
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
		try
		{
			{
					try 
					{
						AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(tampa).getAbsoluteFile());
						Clip clip = AudioSystem.getClip();
						clip.open(audioInputStream);
						clip.start();
					} 
					catch(Exception ex) 
					{
						System.out.println("Error playing sound. (Tampa)");
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
			if (score1 <= 0 )
			{
				throw new Exception(); 
			}
			score1--; 
			score= String.valueOf(score1); 
			scoreHome.setText(score);
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		//original 1800 x 1000
		frame.setBounds(100, 100, 720, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{182, 552, 443, 0};
		gridBagLayout.rowHeights = new int[]{125, 56, 97, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblGuest = new JLabel("HOME");
		lblGuest.setForeground(Color.WHITE);
		lblGuest.setBackground(Color.WHITE);
		lblGuest.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 40));
		GridBagConstraints gbc_lblGuest = new GridBagConstraints();
		gbc_lblGuest.insets = new Insets(0, 0, 5, 5);
		gbc_lblGuest.gridx = 0;
		gbc_lblGuest.gridy = 0;
		frame.getContentPane().add(lblGuest, gbc_lblGuest);
		
		JLabel lblNewLabel = new JLabel("GUEST");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 40));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 0;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setForeground(Color.WHITE);
		panel.setBackground(Color.DARK_GRAY);
		panel.setBorder(new LineBorder(Color.WHITE, 10, true));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
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
		timer1.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 62));
		
		scoreHome.setVerticalAlignment(SwingConstants.TOP);
		scoreHome.setForeground(Color.RED);
		scoreHome.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 125));
		scoreHome.setBackground(Color.WHITE);
		GridBagConstraints gbc_lblGuest_1 = new GridBagConstraints();
		gbc_lblGuest_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblGuest_1.gridx = 0;
		gbc_lblGuest_1.gridy = 2;
		frame.getContentPane().add(scoreHome, gbc_lblGuest_1);
		
		scoreGuest.setVerticalAlignment(SwingConstants.TOP);
		scoreGuest.setForeground(Color.RED); 
		scoreGuest.setFont(new Font("Tw Cen MT Condensed", Font.BOLD, 125));
		scoreGuest.setBackground(Color.WHITE);
		GridBagConstraints gbc_lblGuest_1_1 = new GridBagConstraints();
		gbc_lblGuest_1_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblGuest_1_1.gridx = 2;
		gbc_lblGuest_1_1.gridy = 2;
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
		

		//start the clock
		menuBar.add(btnStartTimer);
		btnStartTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(twoSecondTimer.duration <= 0)
				{
					twoSecondTimer.cancel();
				}
				else
				{
					twoSecondTimer.start();
				}
			}
		});
		
		//pause the clock
		menuBar.add(btnPauseTimer);
		btnPauseTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				twoSecondTimer.pause();
			}
		});
		
		//reset the clock
		menuBar.add(btnResetTimer);
		menuBar.add(btnReset);
		btnResetTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				twoSecondTimer.cancel();
				twoSecondTimer.duration = 0; 
				timer1.setText("00 : 00"); 
			}
		});
		
		//increment and decrement by 1 minute
		menuBar.add(btnmin_1);
		menuBar.add(btnmin);
		btnmin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				twoSecondTimer.pause();
				twoSecondTimer.duration += 60000; 
				timer1.setText(twoSecondTimer.currentTime(twoSecondTimer.getRemainingTime())); 
				
			}
		});
		btnmin_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if (twoSecondTimer.duration > 60000)
				{
					twoSecondTimer.pause();
					twoSecondTimer.duration -= 60000; 
					timer1.setText(twoSecondTimer.currentTime(twoSecondTimer.getRemainingTime()));
				}
			}
		});
		
		//increment and decrement 5 seconds: 
		menuBar.add(btnsec_1);
		menuBar.add(btnsec);
		btnsec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{

				twoSecondTimer.pause();
				twoSecondTimer.duration += 1000; 
				timer1.setText(twoSecondTimer.currentTime(twoSecondTimer.getRemainingTime()));
			}
		});
		btnsec_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if (twoSecondTimer.duration > 5000)
				{
				twoSecondTimer.pause();
				twoSecondTimer.duration -= 1000; 
				timer1.setText(twoSecondTimer.currentTime(twoSecondTimer.getRemainingTime()));
				}
			}
		});
		
		//mute/unmute the goal horns
		menuBar.add(btnMuteunmute);
		
		btnMuteunmute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				
				   if(!isClicked)
				   {
				         isClicked = true;
						   toronto = "src/tgh.wav"; 
							tampa = "src/tbgh.wav"; 
							eop = "src/nhl_eop.wav"; 
				         
				   }
				   else 
				   {
						toronto = ""; 
						tampa = ""; 
						eop = ""; 
				   }
			}
		});
		
		//exit the application 
		menuBar.add(btnExit);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				System.exit(0);
			}
		});
		
	
			
	}
	public class ExampleTimer extends Timer1
	{
		
		public ExampleTimer() 
		{
			super();
		}
		
		public ExampleTimer(long interval, long duration){
			super(interval, duration);
		}

		@Override
		protected void onTick() 
		{	
			//Duration d = ....
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
		
		protected String currentTime(long d)
		{
	        long minutes = (d / 1000) / 60;
	        long seconds = (d / 1000) % 60;
			 
			String str = String.format("%02d", minutes); 
			String str2 = String.format("%02d", seconds);
			timer1.setText(str + " : " + str2);
			return str + " : " + str2; 
		}
		
		
		@Override
		protected void onFinish() 
		{
			
			try 
			{
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(eop).getAbsoluteFile());
				Clip clip = AudioSystem.getClip();
				clip.open(audioInputStream);
				clip.start();
			} 
			catch(Exception ex) 
			{
				System.out.println("Error with playing sound.");
				ex.printStackTrace();
			}
			twoSecondTimer.cancel();
			twoSecondTimer.duration = 0; 
			timer1.setText("00 : 00"); 
			System.out.println("done"); 
		
			
		}
	}
	 
	
}

