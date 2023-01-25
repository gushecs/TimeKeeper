package chronos;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.text.Document;

public abstract class TimeKeeper extends JPanel {

    protected final JLabel time, type, title;
    
    private JTextField logText;

    private final JButton moveUp, moveDown, logButton;
    protected final JButton reset, start, stop;
    protected final JButton save;

    private long startTime, stopTime;

    private boolean toggleDarkMode;

    protected Timer timer;

    private int tkIndex;

    private final JPanel parentPanel;
    protected JPanel centerPanel;

    protected java.util.List<LogElement> log;

    private LogWindow logWindow;
    
    private TimeKeeper selfReference = this;

    protected TimeKeeper(String titleText, JPanel parentPanel, int tkIndex) {

        this.parentPanel = parentPanel;
        this.tkIndex = tkIndex;

        //------------------------------------------------------------------------- Elements creation

        log = new ArrayList<>();

        Insets noMargin = new Insets(0,0,0,0);

        Font upAndDownFont = new Font("Arial", Font.BOLD, 8);
        Dimension upAndDownDimension = new Dimension(15,25);

        moveUp = new JButton("▲");
        moveUp.setFont(upAndDownFont);
        moveUp.setMargin(noMargin);
        moveUp.setPreferredSize(upAndDownDimension);
        moveUp.setBorderPainted(false);

        moveDown = new JButton("▼");
        moveDown.setFont(upAndDownFont);
        moveDown.setMargin(noMargin);
        moveDown.setPreferredSize(upAndDownDimension);
        moveDown.setBorderPainted(false);

        if (tkIndex == 0) {
            this.moveUp.setEnabled(false);
            this.moveDown.setEnabled(false);
        } else {
            ((TimeKeeper) parentPanel.getComponent(tkIndex - 1)).getMoveDown().setEnabled(true);
            this.moveDown.setEnabled(false);
        }

        Font typeAndTitleFont = new Font("Arial", Font.BOLD, 11);
        
        title = new JLabel(" " + titleText, JLabel.LEFT);
        title.setFont(typeAndTitleFont);

        this.type = new JLabel("",JLabel.RIGHT);
        this.type.setFont(typeAndTitleFont);

        this.time = new JLabel("00 : 00 : 00", JLabel.RIGHT);
        this.time.setForeground(Color.BLACK);

        Dimension timeButtons = new Dimension(50, 25);
        Font timeButtonsFont = new Font(null, Font.PLAIN, 20);
        Insets timeButtonsMargins = new Insets(1,12,1,12);
        
        start = new JButton();
        start.setText("⏵");
        start.setFont(timeButtonsFont);
        start.setMargin(timeButtonsMargins);
        start.setPreferredSize(timeButtons);
        start.setBorderPainted(false);
        
        stop = new JButton();
        stop.setText("⏸");
        stop.setFont(timeButtonsFont);
        stop.setEnabled(false);
        stop.setMargin(timeButtonsMargins);
        stop.setPreferredSize(timeButtons);
        stop.setBorderPainted(false);
        
        reset = new JButton();
        reset.setText("⏮");
        reset.setFont(timeButtonsFont);
        reset.setEnabled(false);
        reset.setMargin(timeButtonsMargins);
        reset.setPreferredSize(timeButtons);
        reset.setBorderPainted(false);

        Dimension rightMenuButtonsDimension = new Dimension(25, 25);
        Font rightMenuButtonsFont = new Font(null, Font.PLAIN, 20);

        logButton = new JButton("\uD83D\uDDB9");
        logButton.setPreferredSize(rightMenuButtonsDimension);
        logButton.setFont(rightMenuButtonsFont);
        logButton.setMargin(noMargin);
        logButton.setBorderPainted(false);

        save = new JButton("\uD83D\uDDAB");
        save.setPreferredSize(rightMenuButtonsDimension);
        save.setFont(rightMenuButtonsFont);
        save.setMargin(noMargin);
        save.setBorderPainted(false);

        JButton delete = new JButton("\uD83D\uDDD9");
        delete.setPreferredSize(rightMenuButtonsDimension);
        delete.setFont(rightMenuButtonsFont);
        delete.setMargin(noMargin);
        delete.setBorderPainted(false);
        delete.setForeground(Color.RED);

        logText = new JTextField();
        logText.setPreferredSize(new Dimension(465,20));
        logText.setFont(new Font("Arial",Font.PLAIN,12));
        logText.setForeground(Color.GRAY);
        logText.setText("Enter text to print in the Log.");

        JButton logTextButton = new JButton("Log⌲");
        logTextButton.setPreferredSize(new Dimension(40,15));
        logTextButton.setFont(new Font(null, Font.BOLD, 12));
        logTextButton.setMargin(noMargin);
        logTextButton.setBorderPainted(false);

        //------------------------------------------------------------------------- Timer

        timer = new Timer(0, timerActionListener());

        //------------------------------------------------------------------------- Action listeners
        
        moveUp.addActionListener((e) -> {
            if (this.tkIndex != 0) {
                TimeKeeper tkUp = (TimeKeeper) this.parentPanel.getComponent(this.tkIndex-1);
                this.parentPanel.add(this, this.tkIndex-1);
                this.parentPanel.add(tkUp, this.tkIndex);
                if (this.tkIndex - 1 == 0) {
                    this.moveUp.setEnabled(false);
                    tkUp.getMoveUp().setEnabled(true);
                }
                if (this.parentPanel.getComponentCount() - 1 == this.tkIndex) {
                    tkUp.getMoveDown().setEnabled(false);
                    this.moveDown.setEnabled(true);
                }
                tkUp.setTkIndex(this.tkIndex);
                this.tkIndex = this.tkIndex - 1;
                this.parentPanel.revalidate();
            }
        });

        moveDown.addActionListener((e) -> {
            if (this.tkIndex != this.parentPanel.getComponentCount() - 1) {
                TimeKeeper tkDown = (TimeKeeper) this.parentPanel.getComponent(this.tkIndex + 1);
                this.parentPanel.add(this, this.tkIndex+1);
                this.parentPanel.add(tkDown, this.tkIndex);
                if (this.tkIndex + 1 == this.parentPanel.getComponentCount() - 1) {
                    this.moveDown.setEnabled(false);
                    tkDown.getMoveDown().setEnabled(true);
                }
                if (this.tkIndex == 0) {
                    tkDown.getMoveUp().setEnabled(false);
                    this.moveUp.setEnabled(true);
                }
                tkDown.setTkIndex(this.tkIndex);
                this.tkIndex = this.tkIndex + 1;
                this.parentPanel.revalidate();
            }
        });

        start.addActionListener((e) -> {
            timer.start();
            startTime = (stopTime == 0L) ? System.currentTimeMillis() : startTime + System.currentTimeMillis() - stopTime;
            stopTime = 0L;
            stop.setEnabled(true);
            reset.setEnabled(true);
            start.setEnabled(false);
            log.add(new LogElement(LogType.START, new Date(System.currentTimeMillis()) , type.getText() + " started."));
        });
        
        stop.addActionListener((e) -> {
            stopTime = System.currentTimeMillis();
            timer.stop();
            start.setEnabled(true);
            stop.setEnabled(false);
            reset.setEnabled(Math.abs(startTime - stopTime) != 0);
            log.add(new LogElement(LogType.PAUSE, new Date(System.currentTimeMillis()), type.getText() + " paused at" + time.getText() + "."));
        });
        
        reset.addActionListener(resetActionListener());

        logButton.addActionListener((e) -> SwingUtilities.invokeLater(() -> {
                if (logWindow != null && logWindow.isShowing())
                    logWindow.dispose();
                logWindow = new LogWindow(selfReference);
                logWindow.setVisible(true);
        }));

        delete.addActionListener((e) -> {
            if (this.tkIndex != this.parentPanel.getComponentCount() - 1)
                for (int i = this.tkIndex + 1; i < this.parentPanel.getComponentCount(); i++)
                    ((TimeKeeper) this.parentPanel.getComponent(i)).setTkIndex(i-1);
            this.parentPanel.remove(this.tkIndex);
            this.parentPanel.revalidate();
            this.parentPanel.repaint();
        });

        logTextButton.addActionListener((e) -> {
            if (StringUtils.isNotBlank(logText.getText()) && !logText.getForeground().equals(Color.GRAY)) {
                log.add(new LogElement(LogType.LOG, new Date(System.currentTimeMillis()) ,logText.getText()));
                logText.setForeground(Color.GRAY);
                logText.setText("Enter text to print in the Log.");
            }
        });

        logText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (logText.getForeground() == Color.GRAY) {
                    logText.setText("");
                    if (toggleDarkMode) {
                        logText.setForeground(Color.WHITE);
                        logText.setCaretColor(Color.WHITE);
                    } else {
                        logText.setForeground(Color.BLACK);
                        logText.setCaretColor(Color.BLACK);
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (logText.getText().equals("")) {
                    logText.setForeground(Color.GRAY);
                    logText.setText("Enter text to print in the Log.");
                }
            }
        });

        //------------------------------------------------------------------------- Montage

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setPreferredSize(new Dimension(520, 65));
        setMaximumSize(new Dimension(520, 65));

        JPanel upAndDownPanel = new JPanel();
        upAndDownPanel.setLayout(new GridLayout(2,1));
        upAndDownPanel.add(moveUp);
        upAndDownPanel.add(moveDown);
        this.add(upAndDownPanel);

        JPanel tkMainPanel = new JPanel();
        tkMainPanel.setLayout(new BorderLayout());
        this.add(tkMainPanel);

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());

        JPanel titleAndTypePanel = new JPanel();
        titleAndTypePanel.setLayout(new BorderLayout());
        titleAndTypePanel.setPreferredSize(new Dimension(505, 10));
        titleAndTypePanel.add(title, BorderLayout.WEST);
        titleAndTypePanel.add(this.type, BorderLayout.EAST);

        tkMainPanel.add(titleAndTypePanel, BorderLayout.NORTH);

        JPanel time = new JPanel();
        time.setPreferredSize(new Dimension(90,30));
        time.add(this.time);
        centerPanel.add(time);

        JPanel timeControlButtons = new JPanel();
        timeControlButtons.setSize(new Dimension(165,30));
        timeControlButtons.setLayout(new BoxLayout(timeControlButtons, BoxLayout.X_AXIS));
        timeControlButtons.add(this.start);
        timeControlButtons.add(stop);
        timeControlButtons.add(this.reset);
        centerPanel.add(timeControlButtons);

        tkMainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel rightSideMenuPanel = new JPanel();
        rightSideMenuPanel.setLayout(new FlowLayout());
        rightSideMenuPanel.setMaximumSize(new Dimension(80,30));
        rightSideMenuPanel.add(logButton);
        rightSideMenuPanel.add(save);
        rightSideMenuPanel.add(delete);

        tkMainPanel.add(rightSideMenuPanel, BorderLayout.EAST);

        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());
        logPanel.setPreferredSize(new Dimension(505,17));
        logPanel.add(logText, BorderLayout.CENTER);
        logPanel.add(logTextButton, BorderLayout.EAST);

        tkMainPanel.add(logPanel, BorderLayout.SOUTH);
    }

    protected long calculateTime(String hours, String minutes, String seconds) {
        if ((StringUtils.isBlank(hours) || StringUtils.isNumeric(hours)) &&
                (StringUtils.isBlank(minutes) || StringUtils.isNumeric(minutes)) &&
                (StringUtils.isBlank(seconds) || StringUtils.isNumeric(seconds)))
            return 3_600_000 * Long.parseLong(StringUtils.defaultIfBlank(hours, "0")) +
                    60_000 * Long.parseLong(StringUtils.defaultIfBlank(minutes, "0")) +
                    1000 * Long.parseLong(StringUtils.defaultIfBlank(seconds, "0"));
        return 0;
    }

    protected class TimeFilter extends DocumentFilter implements Serializable {

        @Override
        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, string);

            if (test(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            } else {
                // warn the user and don't allow the insert
            }
        }

        private boolean test(String text) {
            try {
                if (StringUtils.isBlank(text))
                    return true;
                int number = Integer.parseInt(text);
                return number < 60 && text.length() < 3;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            if (test(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                // warn the user and don't allow the insert
            }

        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.delete(offset, offset + length);

            if (test(sb.toString())) {
                super.remove(fb, offset, length);
            } else {
                // warn the user and don't allow the insert
            }

        }
    }

    protected abstract ActionListener timerActionListener();

    protected abstract ActionListener resetActionListener();

    protected long getStartTime() {
        return startTime;
    }

    protected void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    protected long getStopTime() {
        return stopTime;
    }

    protected void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public void setTkIndex(int tkIndex) {
        this.tkIndex = tkIndex;
    }

    public JButton getMoveUp() {
        return moveUp;
    }

    public JButton getMoveDown() {
        return moveDown;
    }

    public java.util.List<LogElement> getLog() {
        return log;
    }

    public String getTitle() {return title.getText();}

    public String getTime() {return time.getText();}

    public boolean isToggleDarkMode() {
        return toggleDarkMode;
    }

    public void setToggleDarkMode(boolean toggleDarkMode) {
        this.toggleDarkMode = toggleDarkMode;
        if (logWindow != null)
            logWindow.setDarkMode(toggleDarkMode);
    }

    public LogWindow getLogWindow() {
        return logWindow;
    }
}
