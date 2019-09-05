package norman.bunch.of.things.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.bunch.of.things.Application;
import norman.bunch.of.things.DataType;
import norman.bunch.of.things.LoggingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainFrame extends JFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
    private ResourceBundle bundle;
    private Properties appProps;
    private JDesktopPane desktop;
    private JMenuItem optionsFileItem;
    private JMenuItem exitFileItem;
    private JMenuItem newCharacterItem;
    private JMenuItem importBookGameItem;
    private JMenuItem importSupplementGameItem;

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

        JMenu characterMenu = new JMenu(bundle.getString("menu.character"));
        menuBar.add(characterMenu);
        newCharacterItem = new JMenuItem(bundle.getString("menu.character.new"));
        characterMenu.add(newCharacterItem);
        newCharacterItem.addActionListener(this);

        JMenu gameMenu = new JMenu(bundle.getString("menu.game"));
        menuBar.add(gameMenu);
        importBookGameItem = new JMenuItem(bundle.getString("menu.game.import.book"));
        gameMenu.add(importBookGameItem);
        importBookGameItem.addActionListener(this);
        importSupplementGameItem = new JMenuItem(bundle.getString("menu.game.import.supplement"));
        gameMenu.add(importSupplementGameItem);
        importSupplementGameItem.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(exitFileItem)) {
            LOGGER.debug("Processing exit menu item.");
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (actionEvent.getSource().equals(optionsFileItem)) {
            options();
        } else if (actionEvent.getSource().equals(newCharacterItem)) {
            newCharacter();
        } else if (actionEvent.getSource().equals(importBookGameItem)) {
            importGameBook();
        } else if (actionEvent.getSource().equals(importSupplementGameItem)) {
            importGameSupplement();
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
        LocaleWrapper[] locales = {new LocaleWrapper(Locale.ENGLISH), new LocaleWrapper(Locale.FRENCH)};
        LocaleWrapper newLang = (LocaleWrapper) JOptionPane
                .showInputDialog(this, bundle.getString("options.language"), bundle.getString("options.title"),
                        JOptionPane.PLAIN_MESSAGE, null, locales, new LocaleWrapper());
        if (newLang != null) {
            appProps.setProperty("main.frame.language", newLang.getLocale().toLanguageTag());
            try {
                Application.storeProps(appProps);
            } catch (LoggingException e) {
                JOptionPane.showMessageDialog(this, bundle.getString("error.message.saving.window.size.and.location"),
                        bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
            }
            Locale.setDefault(newLang.getLocale());
            initComponents();
        }
    }

    private void newCharacter() {
        // Build list of campaign names.
        // FIXME For now, we're using game books instead of campaigns.
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, File> gameBooks = new HashMap<>();
        for (File file : Application.getAppDir().listFiles()) {
            try {
                JsonNode fileJson = objectMapper.readTree(file);
                JsonNode type = fileJson.get("type");
                JsonNode name = fileJson.get("name");
                if (type != null && type.asText().equals(DataType.GAME_BOOK.name()) && name != null &&
                        name.asText().length() > 0) {
                    gameBooks.put(name.asText(), file);
                }
            } catch (IOException ignored) {
                // We are ignoring files which are not valid JSON.
            }
        }
        String[] gameBookNames = gameBooks.keySet().toArray(new String[gameBooks.keySet().size()]);
        Arrays.sort(gameBookNames);

        // Select a campaign.
        // FIXME For now, we're using game books instead of campaigns.
        Object selectedGameBookName = JOptionPane.showInputDialog(this, bundle.getString("character.select.campaign"),
                bundle.getString("character.title"), JOptionPane.PLAIN_MESSAGE, null, gameBookNames, null);
        if (selectedGameBookName != null) {
            File selectedGameBookFile = gameBooks.get(selectedGameBookName);
            try {
                JsonNode gameBookJson = objectMapper.readTree(selectedGameBookFile);
                CharacterFrame frame = new CharacterFrame(bundle.getString("character.title"), gameBookJson);
                frame.setVisible(true);
                desktop.add(frame);
                frame.setSelected(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, bundle.getString("error.message.unexpected"),
                        bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
            } catch (PropertyVetoException ignored) {
                LOGGER.warn("PropertyVetoException ignored while opening New Character frame.", ignored);
            }
        }
    }

    private void importGameBook() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("game.book.extension"), "gamebook"));
        int rtnVal = fileChooser.showOpenDialog(this);
        if (rtnVal == JFileChooser.APPROVE_OPTION) {
            File importFile = fileChooser.getSelectedFile();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode importJson = objectMapper.readTree(importFile);
                JsonNode type = importJson.get("type");
                JsonNode name = importJson.get("name");
                if (type != null && type.asText().equals(DataType.GAME_BOOK.name()) && name != null &&
                        name.asText().length() > 0) {
                    String fileName = "gb_" + name.asText().toLowerCase().replaceAll("[^a-z0-9\\.]", "_") + ".json";
                    File file = new File(Application.getAppDir(), fileName);
                    try {
                        objectMapper.writeValue(file, importJson);
                        JOptionPane.showMessageDialog(this, bundle.getString("game.book.import.success"));
                    } catch (IOException e) {
                        LOGGER.error("Error writing Game Book JSON file.", e);
                        JOptionPane.showMessageDialog(this, bundle.getString("error.message.unexpected"),
                                bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, bundle.getString("error.message.import.game.book"),
                            bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, bundle.getString("error.message.import.game.book"),
                        bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importGameSupplement() {
    }
}
