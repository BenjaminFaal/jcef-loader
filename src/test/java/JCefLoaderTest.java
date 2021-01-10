import com.benjaminfaal.jcef.loader.JCefLoader;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JCefLoaderTest {

    private static final JFrame frame = new JFrame("jcef-loader");

    private static final JTabbedPane tabs = new JTabbedPane();

    private static final JTextField urlTextField = new JTextField();

    private static final List<CefBrowser> browsers = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Path jcefPath = Paths.get(System.getProperty("user.home")).resolve("jcef");
        CefSettings settings = new CefSettings();
        // customize settings as needed...
        settings.windowless_rendering_enabled = false;
        settings.cache_path = jcefPath.resolve("cache").toString();
        settings.locale = Locale.getDefault().toString();
        // Pass options to chromium for example to enable camera/microphone
        CefApp app = JCefLoader.installAndLoad(jcefPath, settings, "--enable-media-stream");

        addTab("https://google.com");
        addTab("https://github.com/BenjaminFaal/jcef-loader");

        JButton createTabButton = new JButton("+");
        createTabButton.addActionListener(e -> addTab("https://google.com"));
        frame.add(createTabButton, BorderLayout.LINE_END);

        urlTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    urlTextField.selectAll();
                }
            }
        });
        urlTextField.addActionListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            if (selectedIndex != -1) {
                CefBrowser browser = browsers.get(selectedIndex);
                browser.loadURL(urlTextField.getText());
            } else {
                addTab(urlTextField.getText());
            }
        });
        frame.add(urlTextField, BorderLayout.NORTH);
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            if (selectedIndex != -1) {
                CefBrowser browser = browsers.get(selectedIndex);
                urlTextField.setText(browser.getURL());
                frame.setTitle(tabs.getTitleAt(selectedIndex) + " - jcef-loader");
            } else {
                urlTextField.setText(null);
                frame.setTitle("jcef-loader");
            }
        });

        tabs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = tabs.indexAtLocation(e.getX(), e.getY());
                if (index != -1) {
                    if (e.getButton() == MouseEvent.BUTTON2) {
                        CefBrowser browser = browsers.get(index);
                        browser.getClient().dispose();
                        browsers.remove(browser);
                        tabs.removeTabAt(index);
                    }
                }
            }
        });
        frame.add(tabs, BorderLayout.CENTER);

        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.dispose();
                System.exit(0);
            }
        });
    }

    private static void addTab(String url) {
        CefClient client = JCefLoader.getInstance().createClient();
        CefBrowser browser = client.createBrowser(url, false, false);
        browsers.add(browser);
        Component component = browser.getUIComponent();
        tabs.addTab(url, component);

        int index = tabs.indexOfComponent(component);
        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onTitleChange(CefBrowser browser, String title) {
                if (index != -1) {
                    tabs.setTitleAt(index, title);
                    frame.setTitle(title + " - jcef-loader");
                }
            }

            @Override
            public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
                int index = tabs.indexOfComponent(component);
                if (index == tabs.getSelectedIndex()) {
                    urlTextField.setText(url);
                }
            }
        });
        tabs.setSelectedComponent(component);
    }

}