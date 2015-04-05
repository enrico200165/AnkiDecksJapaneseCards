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

    boolean processRiga2(Element riga2, String filename, int scanNr, int tablesCounter) {

        { // ------ frame nr ----
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-r-number"
                    // ,".generated-style-2-override2"
                    ));
            Element rktk2Frame = EPUB_main.find(riga2, sels);
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

    public int getRFrame() {
        return this.rFrame;
    }

    String                                 capitolo;
    int                                    rFrame;
    private static org.apache.log4j.Logger log = Logger.getLogger(EntryMain.class);
}
