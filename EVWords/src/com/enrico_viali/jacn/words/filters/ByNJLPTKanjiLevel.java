package com.enrico_viali.jacn.words.filters;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.words.comparators.IEVStringFilter;

/**
 * @author enrico
 * Lascia dentro solo parole in cui TUTTI i kanji sono fra min e max livello in NJLPT
 */
public class ByNJLPTKanjiLevel implements IEVStringFilter {

    public ByNJLPTKanjiLevel(int min, int max) {
        minLev = min;
        maxLev = max;
    }

    @Override
    public boolean includeIt(String ePar) {
        log.error("funzione non implementata");
        System.exit(0);
        return true;
    }

    int  minLev;
    int  maxLev;

    private static org.apache.log4j.Logger log = Logger.getLogger(ByNJLPTKanjiLevel.class);
}
