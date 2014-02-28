package com.enrico_viali.jacn.words;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.words.comparators.IEVStringFilter;
import com.enrico_viali.utils.FileHelper;

public class WordList {

    public WordList() {
        super();
        loaded = false;
        _wordsList = new ArrayList<String>();
    }

    public WordList addAll(ArrayList<String> al) {
        _wordsList.addAll(al);
        return this;
    }

    void removeDuplicates() {
        int before, after;
        before = this._wordsList.size();
        HashSet<String> hs = new HashSet<String>();
        hs.addAll(this._wordsList);
        this._wordsList = null; // SPERO che non distrugga gli elementi
        this._wordsList = new ArrayList<String>();
        this._wordsList.addAll(hs);
        after = _wordsList.size();
        log.info("elements from " + before + " to " + after);
    }

    public WordList load(ArrayList<String> al) {
        _wordsList.clear();
        _wordsList.addAll(al);
        return this;
    }

    WordList addWord(String word) {
        this._wordsList.add(word);
        return this;
    }

    public WordList filter(IEVStringFilter filter) {
        ArrayList<String> wl2 = new ArrayList<String>();

        for (String s : this._wordsList) {
            if (filter.includeIt(s)) {
                wl2.add(s);
            }
        }
        this._wordsList = wl2;
        return this;
    }

    public WordList intersect(List<String> l2) {
        this._wordsList.retainAll(l2);
        return this;
    }

    public WordList sort(Comparator<String> comparator) {
        Collections.sort(this._wordsList, comparator);
        return this;
    }

    public WordList loadFromFile(String filepath) {

        if (loaded == true) {
            log.error("request to load WordListBuilder already loaded");
            System.exit(1);
        }
        FileHelper fh = new FileHelper("", filepath, null);
        if (!fh.readAll(true, null)) {
            log.error("lettura fallita");
            System.exit(1);
        }
        this._wordsList.addAll(fh.getLines());

        return this;
    }


    public void dump() {
        int i = 0;
        log.info("dumping");
        for (String s : this._wordsList) {
            log.info((i++)+ ": " + s);
        }
    }
    
    
    private static org.apache.log4j.Logger log = Logger.getLogger(WordList.class);

    ArrayList<String>                      _wordsList;
    boolean                                loaded;
}
