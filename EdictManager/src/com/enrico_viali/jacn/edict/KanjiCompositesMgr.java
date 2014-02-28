package com.enrico_viali.jacn.edict;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;

import org.apache.log4j.Logger;

public class KanjiCompositesMgr {

    public KanjiCompositesMgr() {
        _composites = new Hashtable<String, List<Edict2K_Ele>>();
    }

    public boolean loadFromRam(List<Edict2K_Ele> headwordsList) {
        int headwScanned = 0;
        int singleKanjis = 0;
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        try {
            for (Edict2K_Ele headwordObj : headwordsList) {
                headwScanned++;
                
                // if (!headwordObj.getIsPBoth() || !headwordObj.getIsPMotherEntry())
                //    continue;
                
                String headword = headwordObj.getKanjiWord();
                /*
                if (headword.matches(".*崚.*") 
                        || headword.matches(".*揖.*")
                        || headword.matches(".*彊.*")
                        || headword.matches(".*泪.*")
                        || headword.matches(".*滉.*")
                        )
                    System.out.print("<"+headword+">"); // debug
                  */
                
                if (headword.length() == 1) {
                    singleKanjis++;
                    // log.info("found single kanji: " + headword);
                    continue;
                }
                for (int i = 0; i < headword.length(); i++) {
                    char kanji = headword.charAt(i);
                    addKanjiAndKomposites("" + kanji, headwordObj);
                }
            }
            log.info("composites table nr entries:" + _composites.size()
                    + "single kanjs in headwords: " + singleKanjis);
        } catch (OutOfMemoryError e) {
            int MEGABYTE = (1024 * 1024);
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            long maxMemory = heapUsage.getMax() / MEGABYTE;
            long usedMemory = heapUsage.getUsed() / MEGABYTE;
            System.out.println("Memory Use :" + usedMemory + "M/" + maxMemory + "M");
            System.exit(1);
        }
        String msg = "KanjiCompositeMgr, nr kanjis: " + _composites.size();
        msg += " headwords: " + headwScanned;
        log.info(msg);
        return true;
    }

    void addKanjiAndKomposites(String kanji, Edict2K_Ele newEl) {

        List<Edict2K_Ele> list = _composites.get(kanji);

        if (list == null) { // no previous List of entries create and put
                            // list
            list = new ArrayList<Edict2K_Ele>();
            _composites.put(kanji, list);
        }
        // here we have the list and can add the entry to it
        list.add(newEl);
    }

    public ArrayList<Edict2K_Ele> getUnsortedComposites(String kanji, boolean onlyGoodEx) {
        if (_composites.get(kanji) == null) {
            log.warn("not found composites for kanji: " + kanji);
        }

        ArrayList<Edict2K_Ele> ret = new ArrayList<Edict2K_Ele>();
        for (Edict2K_Ele comp : _composites.get(kanji)) {
            if (onlyGoodEx && comp.isBadExample()) {
                log.info("discarded bad composite: " + comp);
                continue;
            }
            ret.add(comp);
        }
        return ret;
    }

    public List<Edict2K_Ele> getSortedComposites(String kanji, boolean onlyGoodEx, int maxNr) {
        ArrayList<Edict2K_Ele> tempList = new ArrayList<Edict2K_Ele>();

        if (_composites.get(kanji) == null) {
            log.debug("not found composites for kanji: " + kanji);
            return tempList;
        }

        Set<String> alreadyFeteched = new HashSet<String>();
        for (Edict2K_Ele comp : _composites.get(kanji)) {
            if (onlyGoodEx && comp.isBadExample()) {
                log.debug("discarded bad composite: " + comp);
                continue;
            }
            // forse sarebbe meglio scegliere l'headword migliore nel composite
            // manager
            if (alreadyFeteched.contains(comp.getEntSeq())) {
                // headwords pointing to the same entry already fetched
                // don't insert to same meanings
                continue;
            }

            tempList.add(comp);
            alreadyFeteched.add(comp.getEntSeq());
        }
        Collections.sort(tempList);

        // da implementare:
        // prendere in modo bilanciato con il kanji
        // a sinistra, destra, centro

        // ritorniamo il nr di elementi richiesto, non di più
        if (maxNr > 0) {
            if (maxNr > tempList.size())
                maxNr = tempList.size();
            return tempList.subList(0, maxNr);
        }
        else
            return tempList;
    }

    Hashtable<String, List<Edict2K_Ele>>   _composites;

    private static org.apache.log4j.Logger log = Logger.getLogger(KanjiCompositesMgr.class);

}
