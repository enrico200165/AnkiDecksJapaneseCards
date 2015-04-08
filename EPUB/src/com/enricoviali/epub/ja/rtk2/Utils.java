package com.enricoviali.epub.ja.rtk2;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.enrico_viali.jacn.common.CJKUtils;

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

    static int nrFromFName(String fname) {
        int ret = -1;
        String high = fname.substring(4, 8);
        String low = fname.substring(15, 18);

        ret = 100 * Integer.parseInt(high) + Integer.parseInt(low);

        return ret;
    }

    static boolean isKanjiRow(Element e) {
        Element td = e.select("td").first();
        String sub = td.text().trim();
        if (sub.length() != 1)
            return false;
        sub = sub.substring(0, 1);
        return CJKUtils.isAllKanji(sub, true);
    }

    static boolean isFrameRow(Element e) {
        String sub = e.text();
        boolean ret = sub.matches(" *r-[0-9]+.*");
        return ret;
    }

    static boolean isComment(Element ePar) {
        Elements els = ePar.select("td");
        int withContent = 0;

        for (Element e : els) {
            if (e.text().length() > 0)
                withContent++;
        }
        return (withContent == 1);
    }


    private static org.apache.log4j.Logger log = Logger.getLogger(Utils.class);

}
