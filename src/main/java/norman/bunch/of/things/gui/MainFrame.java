package norman.bunch.of.things.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.bunch.of.things.Application;
import norman.bunch.of.things.BunchType;
import norman.bunch.of.things.LocaleWrapper;
import norman.bunch.of.things.LoggingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainFrame extends JFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private ResourceBundle bundle;
    private Properties appProps;
    private JDesktopPane desktop;
    private JMenuItem optionsFileItem;
    private JMenuItem exitFileItem;
    private JMenuItem importRuleBookItem;

    public MainFrame(Properties appProps) throws HeadlessException {
        super();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.appProps = appProps;
        Locale.setDefault(Locale.forLanguageTag(appProps.getProperty("main.frame.language")));

        initComponents();
        int width = Integer.parseInt(appProps.getProperty("main.frame.width"));
        int height = Integer.parseInt(appProps.getProperty("main.frame.height"));
        setSize(width, height);
        int x = Integer.parseInt(appProps.getProperty("main.frame.location.x"));
        int y = Integer.parseInt(appProps.getProperty("main.frame.location.y"));
        setLocation(x, y);
    }

    private void initComponents() {
        LOGGER.debug("Initializing window components. Locale = " + Locale.getDefault());
        bundle = ResourceBundle.getBundle("norman.bunch.of.things.gui.MainFrame");
        setTitle(bundle.getString("title"));
        desktop = new JDesktopPane();
        setContentPane(desktop);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu(bundle.getString("menu.file"));
        menuBar.add(fileMenu);
        optionsFileItem = new JMenuItem(bundle.getString("menu.file.options"));
        fileMenu.add(optionsFileItem);
        optionsFileItem.addActionListener(this);
        fileMenu.add(new JSeparator());
        exitFileItem = new JMenuItem(bundle.getString("menu.file.exit"));
        fileMenu.add(exitFileItem);
        exitFileItem.addActionListener(this);

        JMenu ruleBookMenu = new JMenu(bundle.getString("menu.rule.book"));
        menuBar.add(ruleBookMenu);
        importRuleBookItem = new JMenuItem(bundle.getString("menu.rule.book.import"));
        ruleBookMenu.add(importRuleBookItem);
        importRuleBookItem.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(exitFileItem)) {
            LOGGER.debug("Processing exit menu item.");
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (actionEvent.getSource().equals(optionsFileItem)) {
            options();
        } else if (actionEvent.getSource().equals(importRuleBookItem)) {
            importRuleBook();
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent windowEvent) {
        if (windowEvent.getSource() == this && windowEvent.getID() == WindowEvent.WINDOW_CLOSING) {

            // Get current size and position of UI and remember it.
            appProps.setProperty("main.frame.width", Integer.toString(getWidth()));
            appProps.setProperty("main.frame.height", Integer.toString(getHeight()));
            Point location = getLocation();
            appProps.setProperty("main.frame.location.x", Integer.toString(location.x));
            appProps.setProperty("main.frame.location.y", Integer.toString(location.y));

            // Save the properties file to a local file.
            try {
                Application.storeProps(appProps);
            } catch (LoggingException e) {
                JOptionPane.showInternalMessageDialog(this,
                        bundle.getString("error.message.saving.window.size.and.location"),
                        bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
        super.processWindowEvent(windowEvent);
    }

    private void options() {
        JFrame optionsFrame = new JFrame();
        optionsFrame.setTitle(bundle.getString("options.title"));
        optionsFrame.setResizable(false);

        JPanel optionsPanel = new JPanel();
        optionsFrame.add(optionsPanel);
        optionsPanel.setOpaque(false);

        JLabel langLabel = new JLabel(bundle.getString("options.language"));
        optionsPanel.add(langLabel);

        LocaleWrapper[] locales = {new LocaleWrapper(Locale.ENGLISH), new LocaleWrapper(Locale.FRENCH)};
        JComboBox langComboBox = new JComboBox(locales);
        optionsPanel.add(langComboBox);
        langComboBox.setSelectedItem(new LocaleWrapper());
        MainFrame mainFrame = this;
        langComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                LocaleWrapper newLang = (LocaleWrapper) langComboBox.getSelectedItem();
                appProps.setProperty("main.frame.language", newLang.getLocale().toLanguageTag());
                try {
                    Application.storeProps(appProps);
                } catch (LoggingException e) {
                    JOptionPane.showMessageDialog(mainFrame,
                            bundle.getString("error.message.saving.window.size.and.location"),
                            bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
                }

                Locale.setDefault(newLang.getLocale());
                initComponents();
                optionsFrame.dispose();
            }
        });
        optionsFrame.pack();

        int frameWidth = getWidth();
        int frameHeight = getHeight();
        Point location = getLocation();
        int frameLocationX = location.x;
        int frameLocationY = location.y;
        int optionsWidth = optionsFrame.getWidth();
        int optionsHeight = optionsFrame.getHeight();
        int optionsLocationX = frameLocationX + (frameWidth - optionsWidth) / 2;
        int optionsLocationY = frameLocationY + (frameHeight - optionsHeight) / 2;
        optionsFrame.setLocation(optionsLocationX, optionsLocationY);
        optionsFrame.setVisible(true);
    }

    private void importRuleBook() {
        JFileChooser fileChooser = new JFileChooser();
        int rtnVal = fileChooser.showOpenDialog(this);
        if (rtnVal == JFileChooser.APPROVE_OPTION) {
            File importFile = fileChooser.getSelectedFile();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode importJson = objectMapper.readTree(importFile);
                JsonNode type = importJson.get("type");
                JsonNode name = importJson.get("name");

                if (type != null && type.asText().equals(BunchType.RULE_BOOK.name()) && name != null &&
                        name.asText().length() > 0) {
                    String fileName = name.asText().toLowerCase().replaceAll("[^a-z]", "_") + ".json";
                    File file = new File(Application.getAppDir(), fileName);
                    objectMapper.writeValue(file, importJson);
                    JOptionPane.showMessageDialog(this, bundle.getString("success.message.import.rule.book"));
                } else {
                    JOptionPane.showMessageDialog(this, bundle.getString("error.message.import.rule.book"),
                            bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, bundle.getString("error.message.import.rule.book"),
                        bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
