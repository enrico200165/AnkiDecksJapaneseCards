package com.enrico_viali.jacn.words.renderers;

//import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.enrico_viali.html.*;
import com.enrico_viali.jacn.edict.EdictUtils;
import com.enrico_viali.jacn.evkanji.core.EVKanjiEntry;
import com.enrico_viali.utils.*;

/**
 * @author enrico
 * RISCRIVERE TOTALMENTE  portata qui in cut&paste
 */

public class NJLPTWordRenderer implements ITexLineRenderer {

    public NJLPTWordRenderer(String cssClassKanjiPar, String cssKanjiIDPar,
            String cssClassElementsPar) {
        super();
        cssClassKanji = cssClassKanjiPar;
        cssKanjiID = cssKanjiIDPar;
        cssElementsClass = cssClassElementsPar;
    }

    
    @Override
    public String render(IRenderableAsTextLine ePar, long scanned, long included, int level) {
  
        log.error("va riscritta totalmente, portata qui in cut&paste");
        System.exit(1);
        
        if (!(ePar instanceof EVKanjiEntry)) {
            log.error("tipo non atteso, trovato: " + ePar.getClass().getName());
            return "error";
        }

        EVKanjiEntry e = (EVKanjiEntry) ePar;

        TableRow tr = new TableRow();
        SPAN sp = null;
        tr.addClass("kanjiListElem");
        TableDataHelper tdh = new TableDataHelper();
        tdh.setClass(null);

        // tdh.setWidth(100);
        sp = SPAN.buildElement("kanji", "kanji", null, e.getKanji());
        sp.addAttribute("style", "color:blue;font-size:300%");
        tr.addChild(tdh.cTD(null).addChild(sp));
        // tdh.setWidth(150);
        tr.addChild(tdh.cTD(null).addChild(SPAN.buildElement("onReading", "onReading", null, EdictUtils.commaSpace(e.getOn()))));
        sp = SPAN.buildElement("kunReading", "kunReading", null, EdictUtils.commaSpace(e.getKun()));
        // sp.addAttribute("width", "10px");
        tdh.setWidthPx(50);
        tr.addChild(tdh.cTD(null).addChild(sp));
        tdh.setWidthPx(400);
        tr.addChild(tdh.cTD(null).addChild(SPAN.buildElement("meaning_IT", "meaning", null, "" + EdictUtils.noP(e.getKanjidicMeaningIT()))));
        tdh.setWidthPx(400);
        tr.addChild(tdh.cTD(null).addChild(SPAN.buildElement("meaning", "meaning", null, "" + EdictUtils.noP(e.getKanjidicMeaning()))));

        tr.addChild(tdh.cTD(null).addChild(SPAN.buildElement("strokesNr", "strokesNr", null, "" + e.getStrokes())));

        for (int i = 0; i < 5; i++) {
            if (e.getCompKanji(i) == null || e.getCompKanji(i).length() > 0) {
                /*
                 * // tdh.setWidth(250);
                 * tr.addChild(tdh.cTD(null).addChild(SPAN.buildElement("comp",
                 * "comp", null, e.getCompKanji(i)))); // tdh.setWidth(300);
                 * tr.addChild
                 * (tdh.cTD(null).addChild(SPAN.buildElement("compReading",
                 * "compReading", null, e.getCompKana(i)))); //
                 * tdh.setWidth(400);
                 * tr.addChild(tdh.cTD(null).addChild(SPAN.buildElement
                 * ("compMean", "compMean", null,
                 * EdictUtils.noP(e.getCompMeaning(i)))));
                 */

                String a = SPAN.buildElement("comp", "comp", null, e.getCompKanji(i)).getHTMLMarkUp(level)
                        + "<br />" + SPAN.buildElement("compReading", "compReading", null, e.getCompKana(i)).getHTMLMarkUp(level)
                        + "<br />" + SPAN.buildElement("compMean", "compMean", null, EdictUtils.noP(e.getCompMeaning(i))).getHTMLMarkUp(level);
                TableData td = new TableData(a);
                tr.addChild(td);
                // tdh.setWidth(300);
                // tr.addChild(tdh.cTD(null).addChild(SPAN.buildElement("compReading",
                // "compReading", null, e.getCompKana(i))));
                // tdh.setWidth(400);
                // tr.addChild(tdh.cTD(null).addChild(SPAN.buildElement("compMean",
                // "compMean", null, EdictUtils.noP(e.getCompMeaning(i)))));

            } else {
                String a = SPAN.buildElement("comp", "comp", null, "").getHTMLMarkUp(level)
                        + "<br />" + SPAN.buildElement("compReading", "compReading", null, "").getHTMLMarkUp(level)
                        + "<br />" + SPAN.buildElement("compMean", "compMean", null, EdictUtils.noP("")).getHTMLMarkUp(level);
                TableData td = new TableData(a);
                tr.addChild(td);
            }
        }
        // log.info(tr.getHTMLMarkUp(level));
        return tr.getHTMLMarkUp(level);
    }

    String                                 cssClassKanji;
    String                                 cssKanjiID;
    String                                 cssElementsClass;

    private static org.apache.log4j.Logger log = Logger.getLogger(NJLPTWordRenderer.class);
}
