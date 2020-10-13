package mediaplayer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class TestProp {

    public static void main(String[] args) {
        String loc = "";
        String duration = "";

        try (InputStream input = TestProp.class.getClassLoader().getResourceAsStream("data.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            loc = prop.getProperty("last.location");
            duration = prop.getProperty("last.duration");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (OutputStream output = new FileOutputStream("data.properties")) {
            Properties prop = new Properties();
            if (loc.isEmpty()) {
                prop.setProperty("last.location", "22222");
                prop.setProperty("last.duration", "aaaaa");
            } else {
                System.out.println("existing :" + loc);
            }
            if (duration.isEmpty()) {
                prop.setProperty("last.duration", "aaaaa");
            } else {
                System.out.println("existing :" + duration);
            }
            prop.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /*
     * Parameters params = new Parameters();
     * FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new
     * FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration
     * .class) .configure(params.properties() .setFileName("file.properties"));
     * Configuration config = builder.getConfiguration();
     * config.setProperty("somekey", "somevalue"); builder.save();
     */

}
