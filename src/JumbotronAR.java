/**
 * ***********************************************************************
 * Jumbotron (Scoreboard) for Backyard/Indoor Rinks 
 * 
 * Class: JumbotronAR.java
 * 
 * Purpose: Creates an object of type JumbotronAR and its GUI. 
 * Note: Errors on the standard output stream will NOT be visible in the GUI. 
 * 
 * @author: Dakota Soares and Ben Martens 
 * 
 * @date: December 29/21
 */

//for the GUI
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

//for the GUI - Swing
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.JMenuBar;
import javax.imageio.ImageIO;
//for the sound
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.io.File;
import java.io.IOException;
//for the timing sequence
import java.time.Duration;
//file imports
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
//enumerator util
import java.util.Enumeration;

public class JumbotronAR
{
	//Nested class that implements the Serial Port Event Listener
	public class serialinput implements SerialPortEventListener 
	{
		//create a serial port
		SerialPort serialPort;
	    //the port is OS dependent
		private final String PORT_NAMES[] = 
			{ 
				"/dev/tty.usbserial-A9007UX1", //MacOS
	            "/dev/ttyACM0", //Raspberry PI
				"/dev/ttyUSB0", //Linux/UNIX
				"COM3", //Windows
			};
		//buffered reader wrapper class
		private BufferedReader input;
		//outputstream
		@SuppressWarnings("unused")
		private OutputStream output;
		//block for x milliseconds while the port is open
		private static final int TIME_OUT = 2000;
		//bps that can come through
		private static final int DATA_RATE = 115200;
		//initialize serial input string
		public String serialinput = ""; 

		public void initialize() 
		{
	        //the next line is for Raspberry Pi and 
	        // gets us into the while loop and was suggested here:  https://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
	        //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
			CommPortIdentifier portId = null;
			@SuppressWarnings("rawtypes")
			Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
			//Find an instance of serial port as set in PORT_NAMES.
			while (portEnum.hasMoreElements()) 
			{
				CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
				for (String portName : PORT_NAMES) 
				{
					if (currPortId.getName().equals(portName)) 
					{
						portId = currPortId;
						break;
					}
				}
			}
			//if the port is null let the user know
			if (portId == null) {
				System.out.println("Could not find COM port.");
				return;
			}
			try 
			{
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
			} 
			catch (Exception e) 
			{
				System.err.println(e.toString());
			}
		}

		/**
		 * This should be called when you stop using the port.
		 * This will prevent port locking on platforms like Linux.
		 * Note: In this application because the board is started automatically
		 * and will be running constantly, close() is not necessary
		 */
		public synchronized void close() 
		{
			if (serialPort != null) 
			{
				serialPort.removeEventListener();
				serialPort.close();
			}
		}

		/**
		 * Handle an event on the serial port. Read the data and print it.
		 */
		public synchronized void serialEvent(SerialPortEvent oEvent) 
		{
			//if the data is available - fire the correct function
			//when a specified button on the remote controller is pressed
			if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) 
			{
				try 
				{
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
					
					else if (serialinput.equals("i"))
					{
						increaseHome(); 
					}
					else if (serialinput.equals("d"))
					{
						decreaseHome(); 
					}
					else if (serialinput.equals("c"))
					{
						increaseGuest(); 
					}
					else if (serialinput.equals("g"))
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
						
				} 
				catch (Exception e) 
				{
					System.err.println(e.toString());
				}
			}
		}
		
		/**
		 * Pause the timer
		 */
		public void pause() 
		{
			twoSecondTimer.pause();
		}

		/**
		 * This mutes/unmutes the horn. 
		 * isClicked default = false
		 */
		private void toggleHorn() 
		{
			 if(!isClicked)
			   {
			         isClicked = true;
					   toronto = "src/tgh.wav"; 
						tampa = "src/tbgh.wav";     
			   }
			   else 
			   {
					toronto = "src/silence.wav"; 
					tampa = "src/silence.wav"; 
			   }
		}
		
		/**
		 * Reset the timer only. 
		 */
		public void reset()
		{
			twoSecondTimer.cancel();
			twoSecondTimer.duration = 0; 
			timer1.setText("00 : 00"); 
		}

		/**
		 * Reset the score only. 
		 */
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

		/**
		 * Start the timer. If the timer is less then/equal to zero
		 * don't start it. 
		 */
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

	//create the JFrame
	private JFrame frame;
	long lastUpdate;
	long interval = 1000; 
	long d = 0; 
	//This creates a default timer which ticks every 1 seconds, and runs for 20 minutes.
	Timer1 twoSecondTimer = new ExampleTimer(interval, d);
	//the wav file strings are initially not set
	String toronto = ""; 
	String tampa = ""; 
	//end of period horn will always be set and never mutable
	String eop = "src/nhl_eop.wav"; 
	//isClicked is false by default
	boolean isClicked = false;
	//Jlabels for GUI
    JLabel timer1 = new JLabel("00 : 00");
	JLabel scoreHome = new JLabel("0");
	JLabel scoreGuest = new JLabel("0"); 
	Border border = BorderFactory.createLineBorder(Color.white, 5); 
	
	//auto-generated code 
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
	private final JPanel NHL = new JPanel();
	private final JPanel homePanel = new JPanel();
	private final JPanel guestPanel = new JPanel();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			@SuppressWarnings("deprecation")
			//run a new thread for the GUI
			public void run() 
			{
				try 
				{
					System.out.println("Started: Initializing GUI");
					JumbotronAR window = new JumbotronAR();
					JumbotronAR.serialinput main = window.new serialinput(); 
					
					window.menuBar.hide();
					main.initialize();
				    GraphicsEnvironment graphics =
				    GraphicsEnvironment.getLocalGraphicsEnvironment();
				    GraphicsDevice device = graphics.getDefaultScreenDevice();
				    	      
				    window.frame.setVisible(true);
				    window.frame.setResizable(true);
				    device.setFullScreenWindow(window.frame);
					//run a new thread for the serial input
					Thread t=new Thread() 
					{
						public void run() 
						{
							//the following line will keep this app alive for x seconds,
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
					System.out.println("Started: Capturing Serial Input");
					
				} catch (Exception e) 
				{
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
	 * auto-generated code 
	 */
	private void initialize() 
	{
		
		frame = new JFrame();
		frame.setResizable(false);
		Color darkG = new Color(32, 32, 32);
		Color darkY = new Color(255,204,0); 
		frame.getContentPane().setBackground(darkG);
		//original 1800 x 1000
		frame.setBounds(100, 100, 720, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 178, 0, 552, 0, 178, 0, 0};
		gridBagLayout.rowHeights = new int[]{125, 0, 97, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblGuest = new JLabel("HOME");
		lblGuest.setForeground(Color.WHITE);
		lblGuest.setBackground(Color.WHITE);
		lblGuest.setFont(new Font("Tw Cen MT Condensed", Font.BOLD | Font.ITALIC, 45));
		GridBagConstraints gbc_lblGuest = new GridBagConstraints();
		gbc_lblGuest.fill = GridBagConstraints.VERTICAL;
		gbc_lblGuest.insets = new Insets(0, 0, 5, 5);
		gbc_lblGuest.gridx = 1;
		gbc_lblGuest.gridy = 0;
		frame.getContentPane().add(lblGuest, gbc_lblGuest);
		
		JPanel panel = new JPanel();
		panel.setForeground(Color.WHITE);
		panel.setBackground(Color.BLACK);
		//default white
		panel.setBorder(new LineBorder(Color.WHITE, 5, true));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 3;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{552, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		timer1.setBackground(Color.DARK_GRAY);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		panel.add(timer1, gbc_lblNewLabel_1);
		timer1.setForeground(Color.RED);
		//default "Tw Cen MT Condensed"
		timer1.setFont(new Font("Digital-7", Font.BOLD, 90));
		
		
		JLabel lblNewLabel = new JLabel("GUEST");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tw Cen MT Condensed", Font.BOLD | Font.ITALIC, 45));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 5;
		gbc_lblNewLabel.gridy = 0;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		GridBagConstraints gbc_homePanel = new GridBagConstraints();
		gbc_homePanel.fill = GridBagConstraints.VERTICAL;
		gbc_homePanel.insets = new Insets(0, 0, 5, 5);
		gbc_homePanel.gridx = 1;
		gbc_homePanel.gridy = 2;
		frame.getContentPane().add(homePanel, gbc_homePanel);
		GridBagLayout gbl_homePanel = new GridBagLayout();
		gbl_homePanel.columnWidths = new int[]{178, 0};
		gbl_homePanel.rowHeights = new int[]{97, 0};
		gbl_homePanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_homePanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		homePanel.setLayout(gbl_homePanel);
		GridBagConstraints gbc_scoreHome = new GridBagConstraints();
		gbc_scoreHome.fill = GridBagConstraints.VERTICAL;
		gbc_scoreHome.gridx = 0;
		gbc_scoreHome.gridy = 0;
		homePanel.add(scoreHome, gbc_scoreHome);
		scoreHome.setForeground(darkY);
		scoreHome.setFont(new Font("Digital-7", Font.BOLD, 150));
		scoreHome.setBackground(Color.WHITE);
		
		GridBagConstraints gbc_NHL = new GridBagConstraints();
		gbc_NHL.insets = new Insets(0, 0, 5, 5);
		gbc_NHL.fill = GridBagConstraints.HORIZONTAL;
		gbc_NHL.gridx = 3;
		gbc_NHL.gridy = 2;
		frame.getContentPane().add(NHL, gbc_NHL);
		
		GridBagConstraints gbc_guestPanel = new GridBagConstraints();
		gbc_guestPanel.insets = new Insets(0, 0, 5, 5);
		gbc_guestPanel.fill = GridBagConstraints.BOTH;
		gbc_guestPanel.gridx = 5;
		gbc_guestPanel.gridy = 2;
		frame.getContentPane().add(guestPanel, gbc_guestPanel);
		GridBagLayout gbl_guestPanel = new GridBagLayout();
		gbl_guestPanel.columnWidths = new int[]{178, 0};
		gbl_guestPanel.rowHeights = new int[]{97, 0};
		gbl_guestPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_guestPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		guestPanel.setLayout(gbl_guestPanel);
		GridBagConstraints gbc_scoreGuest = new GridBagConstraints();
		gbc_scoreGuest.fill = GridBagConstraints.VERTICAL;
		gbc_scoreGuest.gridx = 0;
		gbc_scoreGuest.gridy = 0;
		guestPanel.add(scoreGuest, gbc_scoreGuest);
		scoreGuest.setForeground(darkY); 
		scoreGuest.setFont(new Font("Digital-7", Font.BOLD, 150));
		scoreGuest.setBackground(Color.WHITE);
		
		homePanel.setBackground(Color.BLACK);
		guestPanel.setBackground(Color.BLACK);
		
		
		NHL.setOpaque(false);
		try {
			 BufferedImage nhl = ImageIO.read(new File("nhl.png"));
				NHL.add(new JLabel(new ImageIcon(nhl)));
			 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//scoreHome.setBorder(border);
		//scoreGuest.setBorder(border);
		
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
//end of class JumbotronAR.java