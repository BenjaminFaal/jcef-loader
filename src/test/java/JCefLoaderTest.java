import com.benjaminfaal.jcef.loader.JCefLoader;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class JCefLoaderTest {

    public static void main(String[] args) throws Exception {
        Path jcefPath = Paths.get(System.getProperty("user.home")).resolve("jcef");
        CefSettings settings = new CefSettings();
        // customize settings as needed...
        settings.windowless_rendering_enabled = false;
        settings.cache_path = jcefPath.resolve("cache").toString();
        settings.locale = Locale.getDefault().toString();
        // Pass options to chromium for example to enable camera/microphone
        CefApp app = JCefLoader.installAndLoad(jcefPath, settings, "--enable-media-stream");
        CefClient client = app.createClient();
        CefBrowser browser = client.createBrowser("https://google.com", false, false);

        JFrame frame = new JFrame("jcef-loader");
        frame.add(browser.getUIComponent());
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

}