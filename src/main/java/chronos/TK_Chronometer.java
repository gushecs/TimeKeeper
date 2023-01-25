package chronos;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Date;

public class TK_Chronometer extends TimeKeeper{
    private final JTextField hoursToAdd, minutesToAdd, secondsToAdd;

    public TK_Chronometer(String title, JPanel parentPanel, int tkIndex, boolean toggleDarkMode) {
        
        super(StringUtils.defaultIfBlank(title, "New Chronometer"), parentPanel, tkIndex);

        //------------------------------------------------------------------------- Elements creation

        this.setToggleDarkMode(toggleDarkMode);

        this.type.setForeground(new Color(162, 139, 15));
        this.type.setText("chronometer");

        Dimension timeManipulationDimensions = new Dimension(20,20);
        Font timeManipulationButtonsFont = new Font("Arial", Font.BOLD, 15);
        Insets timeManipulationButtonsMargins = new Insets(2,2,2,2);

        JButton addTime = new JButton("+");
        addTime.setFont(timeManipulationButtonsFont);
        addTime.setPreferredSize(timeManipulationDimensions);
        addTime.setMargin(timeManipulationButtonsMargins);
        addTime.setMaximumSize(timeManipulationDimensions);
        addTime.setBorderPainted(false);

        JButton subtractTime = new JButton("-");
        subtractTime.setFont(timeManipulationButtonsFont);
        subtractTime.setPreferredSize(timeManipulationDimensions);
        subtractTime.setMargin(timeManipulationButtonsMargins);
        subtractTime.setMaximumSize(timeManipulationDimensions);
        subtractTime.setBorderPainted(false);

        hoursToAdd = new JTextField();
        hoursToAdd.setMaximumSize(timeManipulationDimensions);
        PlainDocument hoursToAddDoc = (PlainDocument) hoursToAdd.getDocument();
        hoursToAddDoc.setDocumentFilter(new TimeFilter());

        minutesToAdd = new JTextField();
        minutesToAdd.setMaximumSize(timeManipulationDimensions);
        PlainDocument minutesToAddDoc = (PlainDocument) minutesToAdd.getDocument();
        minutesToAddDoc.setDocumentFilter(new TimeFilter());

        secondsToAdd = new JTextField();
        secondsToAdd.setMaximumSize(timeManipulationDimensions);
        PlainDocument secondsToAddDoc = (PlainDocument) secondsToAdd.getDocument();
        secondsToAddDoc.setDocumentFilter(new TimeFilter());

        Font separateManipulationFieldsFont = new Font("Arial", Font.BOLD, 16);

        JLabel separateHoursAndMinutes = new JLabel(":");
        separateHoursAndMinutes.setFont(separateManipulationFieldsFont);
        JLabel separateMinutesAndSeconds = new JLabel(":");
        separateMinutesAndSeconds.setFont(separateManipulationFieldsFont);


        //------------------------------------------------------------------------- Action listeners

        addTime.addActionListener((e) -> {
            long calcTime;
            if ((calcTime = calculateTime(hoursToAdd.getText(), minutesToAdd.getText(), secondsToAdd.getText())) != 0) {
                if (getStartTime() == 0) {
                    long timeSnapshot = System.currentTimeMillis();
                    setStartTime(timeSnapshot - calcTime);
                    setStopTime(timeSnapshot);
                } else
                    setStartTime(getStartTime() - calcTime);
                time.setText(printTime((getStopTime() != 0) ? getStopTime() - getStartTime() : System.currentTimeMillis() - getStartTime()));
                reset.setEnabled(Math.abs(getStartTime() - getStopTime()) != 0);
                log.add(new LogElement(LogType.TIMEADD, new Date(System.currentTimeMillis()), printTime(calcTime).replace(" ","") + " added to the chronometer."));
            }
        });

        subtractTime.addActionListener((e) -> {
            long calcTime;
            if ((calcTime = calculateTime(hoursToAdd.getText(), minutesToAdd.getText(), secondsToAdd.getText())) != 0) {
                if (getStartTime() == 0) {
                    long timeSnapshot = System.currentTimeMillis();
                    setStartTime(timeSnapshot + calcTime);
                    setStopTime(timeSnapshot);
                } else
                    setStartTime(getStartTime() + calcTime);
                time.setText(printTime((getStopTime() != 0) ? getStopTime() - getStartTime() : System.currentTimeMillis() - getStartTime()));
                reset.setEnabled(Math.abs(getStartTime() - getStopTime()) != 0);
                log.add(new LogElement(LogType.TIMESUBTRACT, new Date(System.currentTimeMillis()), printTime(calcTime).replace(" ","") + " withdrawn from the chronometer."));
            }
        });

        save.addActionListener((e) -> {
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
                            new TK_ChronometerMem(
                                    this.getTitle(),
                                    this.getTime(),
                                    this.getStartTime(),
                                    this.getStopTime(),
                                    this.getLog()
                            ));
                    file.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //------------------------------------------------------------------------- Montage

        JPanel timeManipulation = new JPanel();
        timeManipulation.setLayout(new BoxLayout(timeManipulation, BoxLayout.X_AXIS));
        timeManipulation.setPreferredSize(new Dimension(130, 20));
        timeManipulation.add(subtractTime);
        timeManipulation.add(this.hoursToAdd);
        timeManipulation.add(separateHoursAndMinutes);
        timeManipulation.add(this.minutesToAdd);
        timeManipulation.add(separateMinutesAndSeconds);
        timeManipulation.add(this.secondsToAdd);
        timeManipulation.add(addTime);
        this.centerPanel.add(timeManipulation);

        this.log.add(new LogElement(LogType.CREATION, new Date(System.currentTimeMillis()), "Chronometer " + title + " created."));

        Chronos.toggleDarkMode(this, this.isToggleDarkMode());
    }

    @Override
    protected final ActionListener timerActionListener() {
        return new TimerActionListener();
    }
    
    private class TimerActionListener implements ActionListener, Serializable {
        @Override
        public void actionPerformed(ActionEvent e) {
            time.setText(printTime(System.currentTimeMillis() - getStartTime()));
        }
    }

    @Override
    protected ActionListener resetActionListener() {
        return new ResetActionListener();
    }
    
    private class ResetActionListener implements ActionListener, Serializable {
        @Override
        public void actionPerformed(ActionEvent e) {
            setStopTime(0L);
            setStartTime(0L);
            timer.stop();
            time.setText("00 : 00 : 00");
            start.setEnabled(true);
            stop.setEnabled(false);
            reset.setEnabled(false);
            log.add(new LogElement(LogType.RESET, new Date(System.currentTimeMillis()) , "Chronometer reseted."));
        }
    }

    private String printTime(long timeToPrint) {
        long timeToPrintAbs = Math.abs(timeToPrint);
        long hours = timeToPrintAbs/3_600_000;
        long minutes = ( timeToPrintAbs % 3_600_000 ) / 60_000;
        long seconds = ( timeToPrintAbs % 3_600_000 % 60_000 ) / 1000;
        return String.format((timeToPrint < 0) ? "-%02d : %02d : %02d" : "%02d : %02d : %02d", hours, minutes, seconds);
    }

    public void loadChronometerMem(TK_ChronometerMem memory, boolean loadSingleObject) {
        title.setText(memory.title());
        time.setText(memory.time());
        setStartTime(memory.startTime());
        setStopTime(memory.stopTime());
        log = memory.log();
        if (loadSingleObject)
            log.add(new LogElement(LogType.CHRONOMETERLOAD, new Date(System.currentTimeMillis()), "Chronometer loaded from disk with time " + memory.time()));
    }
}
