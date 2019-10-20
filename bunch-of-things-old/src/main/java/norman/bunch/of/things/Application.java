package norman.bunch.of.things;

import norman.bunch.of.things.gui.MainFrame;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("application");
    private static final String APP_DIR_NAME = BUNDLE.getString("app.dir.name");
    private static final String APP_PROPS_FILE_NAME = BUNDLE.getString("app.props.file.name");
    private static final String APP_PROPS_FILE_COMMENTS = BUNDLE.getString("app.props.file.comments");

    public static void main(String args[]) {
        LOGGER.debug("Starting Application");
        final Properties appProps = new Properties();

        // Load properties and set Look and Feel. If there's an error, there's not much I an do about it except rethrow it as a run time exception.
        try {
            appProps.putAll(loadProps());
            setLookAndFeel();
        } catch (LoggingException e) {
            throw new RuntimeException(e);
        }

        EventQueue.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(appProps);
            mainFrame.setVisible(true);
        });
    }

    private static Properties loadProps() throws LoggingException {
        // Create properties object with default values.
        Properties appProps = new Properties();
        appProps.setProperty("main.frame.width", BUNDLE.getString("main.frame.width.default"));
        appProps.setProperty("main.frame.height", BUNDLE.getString("main.frame.height.default"));
        appProps.setProperty("main.frame.location.x", BUNDLE.getString("main.frame.location.x.default"));
        appProps.setProperty("main.frame.location.y", BUNDLE.getString("main.frame.location.y.default"));
        appProps.setProperty("main.frame.language", Locale.getDefault().toLanguageTag());
        appProps.setProperty("main.frame.game.directory", SystemUtils.USER_HOME);

        // Load properties file. Create it if it does not already exist.
        File appPropsFile = new File(getAppDir(), APP_PROPS_FILE_NAME);
        if (appPropsFile.exists()) {
            LOGGER.debug("Loading window size and location properties.");
            try (InputStream inputStream = new FileInputStream(appPropsFile)) {
                appProps.load(inputStream);
            } catch (IOException e) {
                throw new LoggingException(LOGGER, "Error loading properties file from " + appPropsFile + ".", e);
            }
        } else {
            LOGGER.debug("Using default window size and location properties.");
            storeProps(appProps);
        }
        return appProps;
    }

    public static void storeProps(Properties appProps) throws LoggingException {
        LOGGER.debug("Storing window size and location properties.");

        // Create home directory if it does not exist.
        File appDir = getAppDir();
        if (!appDir.exists()) {
            if (!appDir.mkdir()) {
                throw new LoggingException(LOGGER, "Error creating directory " + appDir + ".");
            }
        }

        File appPropsFile = new File(appDir, APP_PROPS_FILE_NAME);
        try (FileOutputStream outputStream = new FileOutputStream(appPropsFile)) {
            appProps.store(outputStream, APP_PROPS_FILE_COMMENTS);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error storing properties file to " + appPropsFile + ".", e);
        }
    }

    private static void setLookAndFeel() throws LoggingException {
        // Set Look and Feel to be whatever system this app is running on.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            throw new LoggingException(LOGGER, "Error setting system Look and Feel.", e);
        }
    }

    public static File getAppDir() {
        return new File(SystemUtils.USER_HOME, APP_DIR_NAME);
    }
}
