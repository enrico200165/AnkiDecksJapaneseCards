import java.util.ArrayList;

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

    
}
