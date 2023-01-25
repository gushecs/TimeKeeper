package chronos;

import javax.swing.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TimeKeeperMain {

    private static Chronos chronos;
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        String path = TimeKeeperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        File[] memFiles = new File(path.substring(0, path.lastIndexOf("/") + 1)+"mem/").listFiles();

        chronos = new Chronos();

        if (memFiles != null)
            for (File memFile : memFiles)
                if (memFile.getName().contains("chronos"))
                    try {
                        FileInputStream file = new FileInputStream(memFile.getAbsolutePath());
                        chronos.loadMemory((ChronosMem) new ObjectInputStream(file).readObject());
                        file.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

        SwingUtilities.invokeLater(() -> chronos.setVisible(true));
    }

    public static void saveChronos() {
        try {
            String path = TimeKeeperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path = path.substring(0, path.lastIndexOf("/") + 1)+"mem/";
            File pathFile = new File(path);
            if (!pathFile.exists())
                pathFile.mkdir();
            new ObjectOutputStream(new FileOutputStream(path + "chronos.mem")).writeObject(chronos.createMemory());
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
}
