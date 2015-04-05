import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

public class EntryMain {

    public EntryMain() {
        this.rFrame = -1;
        capitolo = "non impostato";
    }

    public int RFrame(String sPar) {
        String s = sPar.substring(2);
        this.rFrame = Integer.parseInt(s);
        return rFrame;
    }

    public String getCapitolo() {
        if (getRFrame() <= 56)
            return "01_0_kana_their_kanji";

        if (getRFrame() <= 195)
            return "02_1_pure_groups_easy";
        if (getRFrame() <= 336)
            return "02_2_pure_groups_3";
        if (getRFrame() <= 574)
            return "02_3_pure_groups_2";

        if (getRFrame() <= 623)
            return "03_1t_chinese_read";

        return "error";
    }

    public boolean isComplete() {
        return true;
    }

    public String toString() {
        return "r-frame=" + this.rFrame;
    }

    boolean processRiga1(Element riga1, String filename, int scanNr, int tablesCounter) {
        { // ------ kanji ----
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-frame-kanji > span"));
            Element kanji = Utils.find(riga1, sels);
            if (kanji != null) {
                // log.info(nr + " signal primitive: " + kanji.text());
            } else {
                log.error("");
                return false;
            }
        }
        { // signal primitive is missing I THINK
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".generated-style-override1", ".generated-style-2-override28"));
            Element signPrim = Utils.find(riga1, sels);
            if (signPrim != null) {
                // log.info(nr + " signal primitive: " + signPrim.text());
            } else {
                log.error("");
                return false;
            }
        }
        { // On reading
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-example-1-kanji span.no-style-override50", ".generated-style-override2"));
            Element OnE = Utils.find(riga1, sels);
            if (OnE != null) {
                // log.info(nr + " signal primitive: " + OnE.text());
            } else {
                log.error("");
                return false;
            }
        }
        { // link

            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".generated-style-override3", ".generated-style-2-override18"));
            Element link = Utils.find(riga1, sels);
            if (link != null) {
                // log.info(nr + " link: " + link.text());
            } else {
                log.warn("no link frame: " + tablesCounter);
            }
        }
        { // kanji frame number
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-vol-1-nr", ".generated-style-2-override1",
                    ".generated-style-2-override8",
                    ".generated-style-2-override28"));
            Element link = Utils.find(riga1, sels);
            // log.info(nr + " kanji frame: " + link1.text());
        }
        return true;
    }

    boolean processRiga2(Element riga2, String filename, int scanNr, int tablesCounter) {

        { // ------ frame nr ----
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-r-number"
                    // ,".generated-style-2-override2"
                    ));
            Element rktk2Frame = Utils.find(riga2, sels);
            if (rktk2Frame != null) {
                String rFrame = rktk2Frame.text();
                log.info(tablesCounter + " rktk2Frame: " + rFrame);
                RFrame(rFrame);
            } else {
                log.error(filename + " nr=" + scanNr + " rktk2 Frame not found\nhtml TRONCATO:\n" + riga2.html().substring(0, 80));
                EPUB_main.bigProblemHook();
                return false;
            }
        }
        return true;
    }

    
    boolean processRiga3(Element riga3, String filename, int scanNr, int tablesCounter) {
        log.error("funzione non implementata, esco");
        System.exit(1);
        return false;
    }

    
    
    public int getRFrame() {
        return this.rFrame;
    }

    String                                 capitolo;
    int                                    rFrame;
    private static org.apache.log4j.Logger log = Logger.getLogger(EntryMain.class);
}
