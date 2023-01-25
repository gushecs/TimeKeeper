package chronos;

import org.apache.commons.lang3.StringUtils;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Objects;

public class TK_Timer extends TimeKeeper{

    private final JTextField hoursToSet, minutesToSet, secondsToSet;

    private long timerTotalTime;

    private final Timer timesUp;

    private final Color dmFontColor = new Color(230,230,230);

    private boolean silence;

    public TK_Timer(String title, JPanel parentPanel, int tkIndex, boolean silence, boolean toggleDarkMode) {

        super(StringUtils.defaultIfBlank(title, "New Timer"), parentPanel, tkIndex);

        //------------------------------------------------------------------------- Elements creation

        this.silence = silence;
        this.setToggleDarkMode(toggleDarkMode);

        this.type.setForeground(new Color(11, 201, 87));
        this.type.setText("timer");

        Dimension setTimeTextsDimension = new Dimension(20,20);

        hoursToSet = new JTextField();
        hoursToSet.setPreferredSize(setTimeTextsDimension);
        PlainDocument hoursToSetDoc = (PlainDocument) hoursToSet.getDocument();
        hoursToSetDoc.setDocumentFilter(new TimeFilter());

        minutesToSet = new JTextField();
        minutesToSet.setPreferredSize(setTimeTextsDimension);
        PlainDocument minutesToSetDoc = (PlainDocument) minutesToSet.getDocument();
        minutesToSetDoc.setDocumentFilter(new TimeFilter());

        secondsToSet = new JTextField();
        secondsToSet.setPreferredSize(setTimeTextsDimension);
        PlainDocument secondsToSetDoc = (PlainDocument) secondsToSet.getDocument();
        secondsToSetDoc.setDocumentFilter(new TimeFilter());

        Font separateManipulationFieldsFont = new Font("Arial", Font.BOLD, 16);

        JLabel separateHoursAndMinutes = new JLabel(":");
        separateHoursAndMinutes.setFont(separateManipulationFieldsFont);
        JLabel separateMinutesAndSeconds = new JLabel(":");
        separateMinutesAndSeconds.setFont(separateManipulationFieldsFont);
        
        Font setTimeButtonFont = new Font("Arial", Font.PLAIN, 12);
        Insets setTimeButtonMargins = new Insets(2,2,2,2);
        Dimension setTimeButtonDimensions = new Dimension(60,20);

        JButton setTime = new JButton("Set time");
        setTime.setFont(setTimeButtonFont);
        setTime.setPreferredSize(setTimeButtonDimensions);
        setTime.setMargin(setTimeButtonMargins);
        setTime.setMaximumSize(setTimeButtonDimensions);
        setTime.setBorderPainted(false);

        this.start.setEnabled(false);

        //------------------------------------------------------------------------- Timer

        timesUp = new Timer(1000, (e) -> {
            if (time.getForeground().equals(Color.black) ||
                    time.getForeground().equals(dmFontColor))
                time.setForeground(Color.red);
            else
                if (isToggleDarkMode())
                    time.setForeground(dmFontColor);
                else
                    time.setForeground(Color.black);
        });

        //------------------------------------------------------------------------- Action listeners
        
        setTime.addActionListener((e) -> {
            long calcTime;
            if ((calcTime = calculateTime(hoursToSet.getText(), minutesToSet.getText(), secondsToSet.getText())) != 0) {
                if (timesUp.isRunning()) {
                    timesUp.stop();
                    if (isToggleDarkMode())
                        time.setForeground(dmFontColor);
                    else
                        time.setForeground(Color.BLACK);
                }
                long timeSnapshot = System.currentTimeMillis();
                setStartTime(timeSnapshot + calcTime);
                timerTotalTime = calcTime;
                setStopTime(timeSnapshot);
                printTime((getStopTime() != 0) ? getStartTime() - getStopTime() : getStartTime() - System.currentTimeMillis());
                type.setText("timer of " + time.getText());
                reset.setEnabled(Math.abs(getStartTime() - getStopTime()) == 0);
                start.setEnabled(true);
                log.add(new LogElement(LogType.SETTIME, new Date(System.currentTimeMillis()), "Time set to " + time.getText().replace(" ","") + "."));
            }
        });

        save.addActionListener((e) -> {
            long snapshotTime = System.currentTimeMillis();
            String timeSnapshot = this.getTime();
            JFileChooser saveTimeKeeper = new JFileChooser();
            saveTimeKeeper.setFileFilter(new FileNameExtensionFilter(".tkm", "tkm"));
            saveTimeKeeper.setAcceptAllFileFilterUsed(false);
            try {
                String path = TimeKeeperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                path = path.substring(0, path.lastIndexOf("/") + 1)+"mem/";
                File pathFile = new File(path);
                if (!pathFile.exists())
                    pathFile.mkdir();
                saveTimeKeeper.setCurrentDirectory(new File(path));
                saveTimeKeeper.setSelectedFile(new File(path+this.getTitle().trim().replace(" ","_")+".tkm"));
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            int fileFlag = saveTimeKeeper.showSaveDialog(this);
            if (fileFlag == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = saveTimeKeeper.getSelectedFile().getAbsolutePath();
                    if (!path.contains(".tkm"))
                        path = path + ".tkm";
                    FileOutputStream file = new FileOutputStream(path);
                    new ObjectOutputStream(file).writeObject(
                            new TK_TimerMem(
                                    this.getTitle(),
                                    timeSnapshot,
                                    this.getTimerTotalTime(),
                                    this.getStartTime(),
                                    (getStopTime() == 0) ? snapshotTime:getStopTime(),
                                    this.getLog()
                            ));
                    file.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //------------------------------------------------------------------------- Montage

        JPanel setTimePanel = new JPanel();
        setTimePanel.setLayout(new BoxLayout(setTimePanel, BoxLayout.X_AXIS));
        setTimePanel.setPreferredSize(new Dimension(135,20));
        setTimePanel.add(this.hoursToSet);
        setTimePanel.add(separateHoursAndMinutes);
        setTimePanel.add(this.minutesToSet);
        setTimePanel.add(separateMinutesAndSeconds);
        setTimePanel.add(this.secondsToSet);
        setTimePanel.add(setTime);
        this.centerPanel.add(setTimePanel);

        this.log.add(new LogElement(LogType.CREATION, new Date(System.currentTimeMillis()), "Timer " + title + " created."));

        Chronos.toggleDarkMode(this, this.isToggleDarkMode());
    }

    @Override
    protected final ActionListener timerActionListener() {
        return new TimerActionListener();
    }

    private class TimerActionListener implements ActionListener, Serializable {
        @Override
        public void actionPerformed(ActionEvent e) {
            printTime(Math.round((float)(getStartTime() - System.currentTimeMillis())/1000) * 1000L);
        }
    }

    @Override
    protected ActionListener resetActionListener() {
        return new ResetActionListener();
    }

    private class ResetActionListener implements ActionListener, Serializable {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timesUp.isRunning()) {
                timesUp.stop();
                if (isToggleDarkMode())
                    time.setForeground(dmFontColor);
                else
                    time.setForeground(Color.BLACK);
            }
            long timeSnapshot = System.currentTimeMillis();
            setStopTime(timeSnapshot);
            setStartTime(timeSnapshot + timerTotalTime);
            timer.stop();
            printTime(getStartTime() - getStopTime());
            start.setEnabled(true);
            stop.setEnabled(false);
            reset.setEnabled(false);
            log.add(new LogElement(LogType.RESET, new Date(System.currentTimeMillis()), "Timer reseted to " + time.getText().replace(" ","") + "."));
        }
    }

    private void printTime(long timeToPrint) {
        if (timeToPrint==0) {
            this.stop.setEnabled(false);
            this.timer.stop();
            this.timesUp.start();
            if (!this.silence) {
                try {
                    Clip clip = AudioSystem.getClip();
                    String path = TimeKeeperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                    path = path.substring(0, path.lastIndexOf("/") + 1)+"mem/timeExpired.wav";
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            new File(path).getAbsoluteFile());
                    clip.open(inputStream);
                    clip.start();
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | URISyntaxException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (!((JFrame) SwingUtilities.getRoot(this)).isFocused())
                SwingUtilities.getRoot(this).requestFocus();
            this.log.add(new LogElement(LogType.TIMEREXPIRED, new Date(System.currentTimeMillis()), "Timer expired."));
        }
        long hours = timeToPrint/3_600_000;
        long minutes = ( timeToPrint % 3_600_000 ) / 60_000;
        long seconds = ( timeToPrint % 3_600_000 % 60_000 ) / 1000;
        this.time.setText(String.format("%02d : %02d : %02d", hours, minutes, seconds));
    }

    public void setSilence(boolean silence) {this.silence = silence;}

    public long getTimerTotalTime() {
        return timerTotalTime;
    }

    public void loadTimerMem (TK_TimerMem memory, boolean loadSingleObject) {
        title.setText(memory.title());
        timerTotalTime = memory.timerTotalTime();
        setStartTime(memory.startTime());
        setStopTime(memory.stopTime());
        printTime(timerTotalTime);
        timesUp.stop();
        type.setText("timer of " + time.getText());
        time.setText(memory.time());
        log = memory.log();
        if (memory.startTime() - memory.stopTime() > 0) {
            start.setEnabled(true);
            if (loadSingleObject)
                log.add(new LogElement(LogType.TIMERLOAD, new Date(System.currentTimeMillis()), "Timer loaded from disk with " + memory.time() + " left."));
        } else if (loadSingleObject) {
            log.add(new LogElement(LogType.TIMERLOAD, new Date(System.currentTimeMillis()), "Timer loaded from disk with no time left."));
        }
        if (timerTotalTime != 0 && timerTotalTime != getStartTime() - getStopTime())
            reset.setEnabled(true);
    }

}