package chronos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class LogWindow extends JFrame {

    private boolean darkMode;

    public LogWindow (TimeKeeper timeKeeper) {

        darkMode = timeKeeper.isToggleDarkMode();

        setSize(new Dimension(505, 615));
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container container = getContentPane();
        setTitle(timeKeeper.getTitle() + "'s Log");

        JPanel logPanel = new JPanel();

        JPanel scrollView = new JPanel();
        scrollView.setName("LogWindowScrollView");
        scrollView.setLayout(new BoxLayout(scrollView, BoxLayout.Y_AXIS));

        JScrollPane logScrollPane = new JScrollPane(scrollView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        logScrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        logScrollPane.setPreferredSize(new Dimension(480, 565));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss - ");

        Dimension logImageDimension = new Dimension(40,40);
        EmptyBorder emptyBorder = new EmptyBorder(0,0,0,0);
        Font logTextFont = new Font(null, Font.PLAIN, 12);

        Map<String,Font> logImageSizes = new HashMap<>();
        logImageSizes.put("small", new Font(null, Font.PLAIN, 19));
        logImageSizes.put("medium", new Font(null, Font.PLAIN, 25));
        logImageSizes.put("big", new Font(null, Font.PLAIN, 32));

        Map<LogType,Color> logColorMap = new HashMap<>();

        for (LogElement loggedInfo : timeKeeper.getLog()) {

            String text = "<html><p style=\"width:275px\"><b>" + simpleDateFormat.format(loggedInfo.logTime()) +
                    "</b>" + loggedInfo.logText() + "</p></html>";

            if (!logColorMap.containsKey(loggedInfo.logType()))
                addKeyToLogColorMap(loggedInfo.logType(), logColorMap);

            JLabel logText = new JLabel(text);
            logText.setFont(logTextFont);
            logText.setForeground(logColorMap.get(loggedInfo.logType()));
            logText.setBorder(emptyBorder);
            logText.setName("LogWindowLabelElement_text");

            int textHeight = Math.max((int)Math.ceil((logText.getPreferredSize().height*logText.getPreferredSize().width)/275.0),70);

            JPanel logElementPanel = new JPanel();
            logElementPanel.setLayout(new GridBagLayout());
            logElementPanel.setMaximumSize(new Dimension(490, textHeight));
            logElementPanel.setBorder(emptyBorder);

            JLabel logImage = new JLabel(loggedInfo.logType().toString(), JLabel.CENTER);
            logImage.setFont(logImageSizes.get(returnLogImageSize(loggedInfo.logType())));
            logImage.setForeground(logColorMap.get(loggedInfo.logType()));
            if (darkMode)
                logImage.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            else
                logImage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            logImage.setBackground(Color.WHITE);
            logImage.setOpaque(true);
            logImage.setPreferredSize(logImageDimension);
            logImage.setMaximumSize(logImageDimension);
            logImage.setName("LogWindowLabelElement_image");

            LogLeftJPanel logLeftSidePanel = new LogLeftJPanel();
            logLeftSidePanel.setPreferredSize(new Dimension(70, textHeight));
            logLeftSidePanel.setLayout(new GridBagLayout());
            logLeftSidePanel.setBorder(emptyBorder);

            logLeftSidePanel.add(logImage);

            logElementPanel.add(logLeftSidePanel);
            logElementPanel.add(logText);

            scrollView.add(logElementPanel);
        }

        String emptySpace = "<html><div style=\"width:275px\"><b></b></div></html>";

        for (int i=0; i<10; i++){

            JPanel logElementPanel = new JPanel();
            logElementPanel.setLayout(new GridBagLayout());
            logElementPanel.setMaximumSize(new Dimension(490, 4));
            logElementPanel.setBorder(emptyBorder);

            LogLeftJPanel logLeftSidePanel = new LogLeftJPanel();
            logLeftSidePanel.setPreferredSize(new Dimension(70, 4));
            logLeftSidePanel.setLayout(new GridBagLayout());
            logLeftSidePanel.setBorder(emptyBorder);

            JLabel logRightSidePanel = new JLabel(emptySpace);
            logRightSidePanel.setBorder(emptyBorder);

            logElementPanel.add(logLeftSidePanel);
            logElementPanel.add(logRightSidePanel);
            scrollView.add(logElementPanel);

        }

        JPanel logFinalElementPanel = new JPanel();
        logFinalElementPanel.setLayout(new GridBagLayout());
        logFinalElementPanel.setMaximumSize(new Dimension(490, 8));
        logFinalElementPanel.setBorder(emptyBorder);

        LogLeftJPanel logFinalLeftSidePanel = new LogLeftJPanel();
        logFinalLeftSidePanel.setMinimumSize(new Dimension(70, 8));
        logFinalLeftSidePanel.setBorder(emptyBorder);

        JLabel logFinalRightSidePanel = new JLabel(emptySpace);
        logFinalRightSidePanel.setBorder(emptyBorder);

        logFinalElementPanel.add(logFinalLeftSidePanel);
        logFinalElementPanel.add(logFinalRightSidePanel);
        scrollView.add(logFinalElementPanel);

        logPanel.add(logScrollPane);

        container.add(logPanel, BorderLayout.CENTER);

        Chronos.toggleDarkMode(container, darkMode);
    }

    private class LogLeftJPanel extends JPanel {
        private void doDrawing(Graphics g) {

            Graphics2D g2d = (Graphics2D) g;
            int height = getHeight();

            if (darkMode)
                g2d.setColor(Color.white);
            else
                g2d.setColor(Color.black);

            int x = getWidth() / 2, y1 = 0;

            if (height != 8) {

                if (height > 45 && ((JLabel) getComponent(0)).getText().equals(LogType.CREATION.toString()))
                    y1 = height / 2;
                g2d.drawLine(x, y1, x, height);

            } else {

                g2d.drawOval(x-2,y1,height/2,height/2);
                g2d.fillOval(x-2,y1,height/2,height/2);

            }
        }

        @Override
        public void paintComponent(Graphics g) {

            super.paintComponent(g);
            doDrawing(g);
        }
    }

    private String returnLogImageSize(LogType logType) {
        return (logType.equals(LogType.LOG) ||
                logType.equals(LogType.CREATION)) ? "big" :
                (logType.equals(LogType.RESET) ||
                logType.equals(LogType.SETTIME) ||
                logType.equals(LogType.TIMEREXPIRED)) ? "medium" : "small";
    }

    private void addKeyToLogColorMap(LogType logType, Map<LogType, Color> logColorMap) {
        if (darkMode)
            switch (logType) {
                case LOG -> logColorMap.put(logType,new Color(50, 141, 187, 255));
                case CREATION -> logColorMap.put(logType,new Color(252, 102, 49));
                case PAUSE, RESET, START -> logColorMap.put(logType,new Color(0,0,0));
                case SETTIME, TIMEREXPIRED, TIMERLOAD -> logColorMap.put(logType,new Color(11, 201, 87));
                case TIMEADD, TIMESUBTRACT, CHRONOMETERLOAD -> logColorMap.put(logType,new Color(162, 139, 15));
            }
        else
            switch (logType) {
                case LOG -> logColorMap.put(logType,new Color(16, 46, 61));
                case CREATION -> logColorMap.put(logType,new Color(136, 55, 26));
                case PAUSE, RESET, START -> logColorMap.put(logType,new Color(0,0,0));
                case SETTIME, TIMEREXPIRED, TIMERLOAD -> logColorMap.put(logType,new Color(11, 201, 87));
                case TIMEADD, TIMESUBTRACT, CHRONOMETERLOAD -> logColorMap.put(logType,new Color(162, 139, 15));
            }
    }

    public static void toggleToDarkModeBackground(Container container) {

        int numberOfElements = container.getComponentCount();

        int colorChangeStart = numberOfElements - 11;

        for (int i = numberOfElements - 11; i < numberOfElements ; i++) {

            int colorFactor = i - colorChangeStart;
            Color bgColor = new Color(20+colorFactor,20+colorFactor,20+colorFactor);
            ((JPanel) container.getComponent(i)).getComponent(0).setBackground(bgColor);
            if (((JPanel) container.getComponent(i)).getComponent(0).getGraphics() != null)
                ((JPanel) container.getComponent(i)).getComponent(0).paint(((JPanel) container.getComponent(i)).getComponent(0).getGraphics());
            container.getComponent(i).setBackground(bgColor);
        }
    }

    public static void toggleDarkModeText(JLabel label, boolean isDarkMode) {
        if (isDarkMode) {
            if (label.getForeground().getRed() == 16)
                label.setForeground(new Color(50, 141, 187, 255));
            else if (label.getForeground().getRed() == 136)
                label.setForeground(new Color(252, 102, 49));
            if (label.getName().contains("image"))
                label.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        } else {
            if (label.getForeground().getRed() == 50)
                label.setForeground(new Color(16, 46, 61));
            else if (label.getForeground().getRed() == 252)
                label.setForeground(new Color(136, 55, 26));
            if (label.getName().contains("image"))
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }
}
