package chronos;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;

public class Chronos extends JFrame {

    private final JTextField title;

    private final Container container;

    private final JRadioButton chronometerType;

    private final JScrollPane chronosScrollPane;

    private final JCheckBox silence, toggleDarkMode;

    private static Color color, dmMainColor, dmElementColor, fontColor, dmFontColor, buttonColor, dmButtonColor;

    public Chronos() {

        super("Chronos");

        JLabel titleLabel, typeLabel;
        JButton add, load;

        color = new Color(255, 255, 255);
        dmMainColor = new Color(20,20,20);
        dmElementColor = new Color(30, 30, 30);
        fontColor = new Color(0,0,0);
        dmFontColor = new Color(230,230,230);
        buttonColor = new Color(200, 200, 200);
        dmButtonColor = new Color(50,50,50);

        /*------------   Main Window   ------------*/

        setSize(new Dimension(570, 440));
        setResizable(false);
        setLocationRelativeTo(null);
        setOpacity(1);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new ClosingAction());
        container = getContentPane();

        /*<<------------   Superior menu - open   ------------>>*/

        JPanel chronosSuperiorMenu = new JPanel();
        chronosSuperiorMenu.setLayout(new BorderLayout());

        /*------------   Left side elements   ------------*/

        JPanel chronosSuperiorMenuOptions = new JPanel();
        chronosSuperiorMenuOptions.setLayout(new GridLayout(2, 1));

        JPanel leftSidePanel = new JPanel();

        Dimension labelDimension = new Dimension(50,30);
        Dimension elementDimension = new Dimension(300, 30);

        JPanel titlePanel = new JPanel();

        titleLabel = new JLabel("Title: ");
        titleLabel.setPreferredSize(labelDimension);
        titlePanel.add(titleLabel);

        title = new JTextField();
        title.setPreferredSize(elementDimension);
        titlePanel.add(title);

        chronosSuperiorMenuOptions.add(titlePanel);

        JPanel typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));

        typeLabel = new JLabel("  Type:       ");
        typeLabel.setPreferredSize(labelDimension);
        typePanel.add(typeLabel);

        ButtonGroup types = new ButtonGroup();
        JRadioButton timerType = new JRadioButton();
        timerType.setSelected(true);
        timerType.setText("Timer");
        types.add(timerType);
        chronometerType = new JRadioButton();
        chronometerType.setText("Chronometer");
        types.add(chronometerType);
        typePanel.add(timerType);
        typePanel.add(chronometerType);

        chronosSuperiorMenuOptions.add(typePanel);

        leftSidePanel.add(chronosSuperiorMenuOptions);

        chronosSuperiorMenu.add(leftSidePanel, BorderLayout.WEST);

        /*------------   Right side elements   ------------*/

        JPanel rightSidePanel = new JPanel();
        rightSidePanel.setPreferredSize(new Dimension(140, 90));
        rightSidePanel.setLayout(new GridBagLayout());

        JPanel rightSideButtonsPanel = new JPanel();
        rightSideButtonsPanel.setLayout(new FlowLayout());

        add = new JButton("add");
        add.setBorderPainted(false);

        load = new JButton("load");
        load.setBorderPainted(false);

        rightSideButtonsPanel.add(add);
        rightSideButtonsPanel.add(load);

        rightSidePanel.add(rightSideButtonsPanel);
        chronosSuperiorMenu.add(rightSidePanel, BorderLayout.EAST);

        /*<<------------   Superior menu - close   ------------>>*/

        container.add(chronosSuperiorMenu, BorderLayout.NORTH);

        /*<<------------   TimeKeeper objects list   ------------>>*/

        JPanel centralPanel = new JPanel();

        JPanel scrollView = new JPanel();
        scrollView.setLayout(new BoxLayout(scrollView, BoxLayout.Y_AXIS));

        chronosScrollPane = new JScrollPane(scrollView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chronosScrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        chronosScrollPane.setPreferredSize(new Dimension(540, 280));

        centralPanel.add(chronosScrollPane);

        container.add(centralPanel, BorderLayout.CENTER);

        /*<<------------   Inferior part options   ------------>>*/

        JPanel inferiorSidePanel = new JPanel();
        inferiorSidePanel.setLayout(new BoxLayout(inferiorSidePanel, BoxLayout.X_AXIS));

        silence = new JCheckBox("Silence application");
        inferiorSidePanel.add(silence);

        toggleDarkMode  = new JCheckBox("Dark mode");
        inferiorSidePanel.add(toggleDarkMode);

        container.add(inferiorSidePanel, BorderLayout.SOUTH);

        toggleDarkMode(container, toggleDarkMode.isSelected());

        /*<<------------   Action Listeners   ------------>>*/

        add.addActionListener((e) -> {
            if (chronometerType.isSelected())
                addChronometer();
            else
                addTimer();
            chronosScrollPane.revalidate();
        });

        silence.addActionListener((e) -> {
            for (Component tk : ((JPanel) chronosScrollPane.getViewport().getView()).getComponents())
                if (tk instanceof TK_Timer)
                    ((TK_Timer) tk).setSilence(silence.isSelected());
        });

        load.addActionListener((e) -> {
            JFileChooser loadTimeKeeper = new JFileChooser();
            loadTimeKeeper.setFileFilter(new FileNameExtensionFilter(".tkm", "tkm"));
            loadTimeKeeper.setAcceptAllFileFilterUsed(false);
            loadTimeKeeper.setCurrentDirectory(new File(System.getProperty("user.dir")+"\\src\\main\\java\\chronos\\mem\\"));
            int fileFlag = loadTimeKeeper.showOpenDialog(this);
            if (fileFlag == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = loadTimeKeeper.getSelectedFile().getAbsolutePath();
                    FileInputStream file = new FileInputStream(path);
                    loadTimeKeeperObject(new ObjectInputStream(file).readObject(), true);
                    chronosScrollPane.revalidate();
                    file.close();
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        toggleDarkMode.addActionListener((e) -> Chronos.toggleDarkMode(container, toggleDarkMode.isSelected()));
    }

    private static class ClosingAction extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            TimeKeeperMain.saveChronos();
        }
    }

    private TK_Chronometer addChronometer () {
        TK_Chronometer chronometer;
        ((JPanel) chronosScrollPane.getViewport().getView())
                .add(chronometer = new TK_Chronometer(
                        title.getText(),
                        (JPanel) chronosScrollPane.getViewport().getView(),
                        ((JPanel) chronosScrollPane.getViewport().getView()).getComponentCount(),
                        toggleDarkMode.isSelected()));
        return chronometer;
    }

    private TK_Timer addTimer () {
        TK_Timer timer;
        ((JPanel) chronosScrollPane.getViewport().getView())
                .add(timer = new TK_Timer(
                        title.getText(),
                        (JPanel) chronosScrollPane.getViewport().getView(),
                        ((JPanel) chronosScrollPane.getViewport().getView()).getComponentCount(),
                        silence.isSelected(),
                        toggleDarkMode.isSelected()));
        return timer;
    }

    public static void toggleDarkMode(Component mainComponent, boolean isDarkMode) {
        if (mainComponent instanceof Container)
            for (Component component : ((Container) mainComponent).getComponents())
                toggleDarkMode(component, isDarkMode);

        toggleDarkModeText(mainComponent, isDarkMode);
        toggleDarkModeBackground(mainComponent, isDarkMode);

        if (mainComponent instanceof TimeKeeper) {
            ((TimeKeeper) mainComponent).setToggleDarkMode(isDarkMode);
            if (((TimeKeeper) mainComponent).getLogWindow() != null)
                toggleDarkMode(((TimeKeeper) mainComponent).getLogWindow().getContentPane(), isDarkMode);
        }
    }

    public static void toggleDarkModeText (Component component, boolean isDarkMode) {
        if (isDarkMode && component.getForeground().equals(fontColor)) {
            component.setForeground(dmFontColor);
            if (component instanceof JTextField)
                ((JTextField) component).setCaretColor(dmFontColor);
        } else if (!isDarkMode && component.getForeground().equals(dmFontColor)) {
            component.setForeground(fontColor);
            if (component instanceof JTextField)
                ((JTextField) component).setCaretColor(fontColor);
        } else if (component.getName() != null && component.getName().contains("LogWindowLabelElement")) {
            LogWindow.toggleDarkModeText((JLabel) component, isDarkMode);
        }
    }

    public static void toggleDarkModeBackground (Component component, boolean isDarkMode) {
        if (isDarkMode) {
            if (component instanceof JButton)
                component.setBackground(dmButtonColor);
            else if (component instanceof JRadioButton ||
                    component instanceof JCheckBox ||
                    (component instanceof JPanel &&
                    !(component.getParent() instanceof JViewport)))
                component.setBackground(dmMainColor);
            else if (component instanceof JScrollBar)
                ((JScrollBar) component).setUI(new CustomScrollBarUI(isDarkMode));
            else
                component.setBackground(dmElementColor);
            if (component.getName() != null && component.getName().equals("LogWindowScrollView"))
                LogWindow.toggleToDarkModeBackground((Container) component);
        } else {
            if (component instanceof JButton)
                component.setBackground(buttonColor);
            else if (component instanceof JScrollBar)
                ((JScrollBar) component).setUI(new CustomScrollBarUI(isDarkMode));
            else
                component.setBackground(color);
        }
    }

    protected static class CustomScrollBarUI extends BasicScrollBarUI implements Serializable {

        private final boolean isDarkMode;

        public CustomScrollBarUI(boolean isDarkMode) {this.isDarkMode = isDarkMode;}

        @Override
        protected void configureScrollBarColors() {
            if (isDarkMode) {
                thumbHighlightColor = dmButtonColor;
                thumbLightShadowColor = dmButtonColor;
                thumbDarkShadowColor = dmButtonColor;
                thumbColor = dmButtonColor;
                trackColor = dmElementColor;
            } else {
                thumbHighlightColor = buttonColor;
                thumbLightShadowColor = buttonColor;
                thumbDarkShadowColor = buttonColor;
                thumbColor = buttonColor;
                trackColor = color;
            }
        }

        @Override
        protected JButton createDecreaseButton(int orientation)  {return createButton("⮝");}

        @Override
        protected JButton createIncreaseButton(int orientation)  {return createButton("⮟");}

        private JButton createButton(String string) {
            JButton arrowButton = new JButton(string);
            arrowButton.setBorderPainted(false);
            arrowButton.setMargin(new Insets(0,0,0,0));
            arrowButton.setFont(new Font(null,Font.BOLD,15));
            arrowButton.setPreferredSize(new Dimension(20,20));

            if (isDarkMode) {
                arrowButton.setBackground(dmButtonColor);
                arrowButton.setForeground(dmElementColor);
            } else {
                arrowButton.setBackground(buttonColor);
                arrowButton.setForeground(dmButtonColor);
            }
            return arrowButton;
        }
    }

    public ChronosMem createMemory() {
        ChronosMem memory = new ChronosMem(silence.isSelected(), toggleDarkMode.isSelected(), new ArrayList<>());
        for (Component timeKeeper : ((JPanel) chronosScrollPane.getViewport().getView()).getComponents())
            if (timeKeeper instanceof TK_Chronometer chronometer)
                memory.timeKeeperObjects().add(
                        new TK_ChronometerMem(
                                chronometer.getTitle(),
                                chronometer.getTime(),
                                chronometer.getStartTime(),
                                (chronometer.getStopTime() == 0) ? System.currentTimeMillis():chronometer.getStopTime(),
                                chronometer.getLog()));
            else if (timeKeeper instanceof TK_Timer timer)
                memory.timeKeeperObjects().add(
                        new TK_TimerMem(
                                timer.getTitle(),
                                timer.getTime(),
                                timer.getTimerTotalTime(),
                                timer.getStartTime(),
                                (timer.getStopTime() == 0) ? System.currentTimeMillis():timer.getStopTime(),
                                timer.getLog()));
        return memory;
    }

    public void loadMemory(ChronosMem memory) {
        silence.setSelected(memory.silence());
        toggleDarkMode.setSelected(memory.toggleDarkMode());
        chronosScrollPane.getVerticalScrollBar().setUI(new Chronos.CustomScrollBarUI(toggleDarkMode.isSelected()));
        Chronos.toggleDarkMode(this.getContentPane(), toggleDarkMode.isSelected());
        for (Object timeKeeperObject : memory.timeKeeperObjects())
            loadTimeKeeperObject(timeKeeperObject, false);
    }

    private void loadTimeKeeperObject (Object timeKeeperObject, boolean loadSingleObject) {
        if (timeKeeperObject instanceof TK_ChronometerMem) {
            TK_Chronometer chronometer = addChronometer();
            chronometer.loadChronometerMem((TK_ChronometerMem) timeKeeperObject, loadSingleObject);
        } else if (timeKeeperObject instanceof TK_TimerMem) {
            TK_Timer timer = addTimer();
            timer.loadTimerMem((TK_TimerMem) timeKeeperObject, loadSingleObject);
        }
    }
}