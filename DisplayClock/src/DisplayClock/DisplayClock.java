/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DisplayClock;

/**
 *
 * @author javi2_000
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DisplayClock implements ActionListener, KeyListener {

    private JFrame frame;
    private JPanel panel;
    private JLabel timeLabel = new JLabel();

    private JButton startBtn = new JButton("Start");
    private JButton pauseBtn = new JButton("Pause");
    private JButton resumeBtn = new JButton("Resume");

    private CountTimer countdown;


    public DisplayClock() {
        setText("       ");
        clockGUI();
    }

    private void clockGUI() {
        frame = new JFrame();
        panel = new JPanel();

        panel.setLayout(new BorderLayout());
        timeLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        panel.add(timeLabel, BorderLayout.NORTH);

        startBtn.addActionListener(this);
        pauseBtn.addActionListener(this);
        resumeBtn.addActionListener(this);

        JPanel cmdPanel = new JPanel();
        cmdPanel.setLayout(new GridLayout());

        cmdPanel.add(startBtn);
        cmdPanel.add(pauseBtn);
        cmdPanel.add(resumeBtn);

        panel.add(cmdPanel, BorderLayout.SOUTH);

        JPanel clrPanel = new JPanel();
        clrPanel.setLayout(new GridLayout(0,1));


        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();

        countdown = new CountTimer();

    }

    private void setText(String sTime) {
        timeLabel.setText(sTime);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

        JButton b = (JButton) e.getSource();
        if (b.equals(startBtn)){
            countdown.start(); 
        }
        else if (b.equals(pauseBtn)){
            countdown.pause(); 
        }
        else if (b.equals(resumeBtn)){
            //countdown.resume(); 
        }
    }


    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
             public void run() {
               new DisplayClock();
             }
          });

    }

    boolean running = false;
    @Override
    public void keyTyped(KeyEvent e){
            char key = e.getKeyChar();
            /*
            if (key == ' '){
                running = !running;
                
                if (running == false){
                    countdown.pause();
                }
                else{
                    countdown.resume();
                }
            }*/
        }

    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public class CountTimer implements ActionListener, KeyListener {

        private static final int second = 1000;
        private int counter = 0;
        private boolean isRunning = false;
        private Timer myTimer = new Timer(second, this);

        public CountTimer() {
            counter=0;
            setText(TimeFormat(counter));
        }

        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isRunning == true) {
                counter++;
                setText(TimeFormat(counter));
            }
        }
        
        
        @Override
        public void keyTyped(KeyEvent e){
            char key = e.getKeyChar();
            if (key == ' '){
                isRunning = !isRunning;
            }
        }

        public void start() {
            counter = 0; 
            isRunning = true;
            myTimer.start();
        }
        
        public void resume() {
            isRunning = true;
            //myTimer.restart();
        }
        
        public void pause() {
            isRunning = false;
        }
        

        @Override
        public void keyPressed(KeyEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyReleased(KeyEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private String TimeFormat(int count) {
        int hours = count / 3600;
        int minutes = (count-hours*3600)/60;
        int seconds = count-minutes*60;
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }
}