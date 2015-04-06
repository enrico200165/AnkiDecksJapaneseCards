import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Utils {
    public static Element find(Element e, ArrayList<String> selectors) {
        Element ret = null;
        Elements els;
        for (String sel : selectors) {
            els = e.select(sel);
            if (els.size() > 0) {
                return els.get(0);
            }
        }
        return ret;
    }

    static int tableNr(String ID) {
        int ret;
        String s = ID.substring(7);
        ret = Integer.parseInt(s);
        return ret;
    }

    static void esco(String msg) {
        log.error("esco: " + msg);
        System.exit(1);
    }
    private static org.apache.log4j.Logger log = Logger.getLogger(Utils.class);

}
