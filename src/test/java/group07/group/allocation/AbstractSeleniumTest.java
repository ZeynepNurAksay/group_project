package group07.group.allocation;

import java.io.File;

public abstract class AbstractSeleniumTest {

    static {
        System.setProperty("webdriver.gecko.driver", findFile());
    }

    static private String findFile() {
        String[] paths = {"", "bin/", "target/classes"};
        for (String path : paths) {
            if (new File(path + "chromedriver.exe").exists()) return path + "chromedriver.exe";
        }
        return "";
    }

}
