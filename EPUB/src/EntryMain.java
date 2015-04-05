import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

public class EntryMain {

    public EntryMain() {
        selectors = new Selectors();
        reset();
    }

    void reset() {
        tableID = "-";
        RTK2Frame = -1;
        capitolo = "-";
        tableID = "-";
        // --- da riga 1
        kanji = "知";
        signPrim = "-";
        On = "-";
        link = "-";
        RTK1Frame = "-";
        // --- da riga 2
        RTK2Frame = -1;
    }

    public int RFrame(String sPar) {
        String s = sPar.substring(2);
        this.RTK2Frame = Integer.parseInt(s);
        return RTK2Frame;
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

    
    void setComment(String com) {
        this.comment = com;
    }
    
    public String toString() {
        String sep = "  ";
        String eq = "=";
        String s = "";

        s += sep + "table" + eq + getTableID();
        s += sep + "capitolo" + eq + getCapitolo();
        s += sep + "kanji" + eq + kanji;
        s += sep + "signal primitive" + eq + signPrim;
        s += sep + "On" + eq + On;
        s += sep + "link" + eq + link;
        s += sep + "RTK1Frame" + eq + RTK1Frame;
        // da riga 2
        s += sep + "RTK2Frame" + eq + RTK2Frame;

        return s;
    }

    boolean processRiga1(Element riga1, String filename, String tableID,int tableNr, int scanNr, int tablesCounter) {
       
     
        { // ------ kanji ----
            ArrayList<String> sels = selectors.selsKanji(tableNr);
            Element kanjiE = Utils.find(riga1, sels);
            if (kanjiE != null) {
                // log.info(nr + " signal primitive: " + kanji.text());
                kanji = kanjiE.text();
            } else {
                log.error(filename+" kanji, table ID: "+tableID);
                return false;
            }
        }
        { // signal primitive is missing I THINK
            ArrayList<String> sels = selectors.selsSignalPrim(tableNr);
            Element signPrimE = Utils.find(riga1, sels);
            if (signPrimE != null) {
                signPrim = signPrimE.text();
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
                this.On = OnE.text();
            } else {
                log.error("");
                return false;
            }
        }
        { // link

            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".generated-style-override3", ".generated-style-2-override18"));
            Element linkE = Utils.find(riga1, sels);
            if (linkE != null) {
                // log.info(nr + " link: " + link.text());
                link = linkE.text();
            } else {
                log.warn("no link frame: " + tablesCounter);
            }
        }
        { // kanji frame number
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-vol-1-nr", ".generated-style-2-override1",
                    ".generated-style-2-override8",
                    ".generated-style-2-override28"));
            Element RTK1FrameE = Utils.find(riga1, sels);
            this.RTK1Frame = RTK1FrameE.text();
            // log.info(nr + " kanji frame: " + link1.text());
        }
        return true;
    }

    boolean processRiga2(Element riga2, String filename, String tableID, int tableNr,int scanNr, int tablesCounter) {

        { // ------ frame nr ----
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-r-number"
                    // ,".generated-style-2-override2"
                    ));
            Element rktk2FrameE = Utils.find(riga2, sels);
            if (rktk2FrameE != null) {
                String rFrame = rktk2FrameE.text();
                RFrame(rFrame);
            } else {
                log.error(filename + " tableID: " + tableID + " nr=" + scanNr + " rktk2 Frame not found\nhtml TRONCATO:\n" + riga2.html().substring(0, 80));
                EPUB_main.bigProblemHook();
                return false;
            }
        }

        return true;
    }

    boolean processRiga3(Element riga3, String filename, String tableID, int tableNr,int scanNr, int tablesCounter) {

        { // ------ frame nr ----
            ArrayList<String> sels = new ArrayList<String>(Arrays.asList(".x2-example-1-comment"));
            Element commentE = Utils.find(riga3, sels);
            if (commentE != null) {
                this.comment = commentE.text();
            } else {
                log.error(filename + " tableID: "+ tableID +" nr=" + scanNr + " comment found\nhtml TRONCATO:\n" + riga3.html().substring(0, 240));
                return false;
            }
        }
        return true;
    }

    public int getRFrame() {
        return this.RTK2Frame;
    }

    public int getRTK1Frame() {
        int ret;
        ret = Integer.parseInt(this.RTK1Frame);
        return ret;
    }

    public void setTableID(String id) {
        this.tableID = id;
    }

    public String getTableID() {
        return this.tableID;
    }

    String                                 capitolo;
    String                                 tableID;
    Selectors selectors;
    
    
    // --- da riga 1
    String                                 kanji;
    String                                 signPrim;
    String                                 On;
    String                                 link;
    String                                 RTK1Frame;
    // --- da riga 2
    int                                    RTK2Frame;
    // --- da riga 3
    String                                 comment;

    private static org.apache.log4j.Logger log = Logger.getLogger(EntryMain.class);
}
