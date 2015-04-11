package com.enricoviali.epub.ja.rtk2;

import org.jsoup.nodes.Element;

/*
 * Tables can span files so control variables for them cannot be local to a table
 * Just to make things readable (and maybe more manageable)
 * I group then in this controller class
 * 
 */
public class TProcVars {
    
    
    public String toString(boolean dumpTable) {
        String s = "";
                
        s += " kanji1Row: "+kanji1Row;
        s += " \nkanji2Row: "+kanji2Row;
        s += " \nR2FrameRow: "+RFrameRow;
        s += " \ncommentRow: "+kanji1Row;
        return s;
    }
    
    Element t;
    
    Element kanji1Row;
    Element kanji2Row;
    Element RFrameRow;
    Element CommentRow;
    
    int nrNonEmptyRows;
    int nrRcgndRows;
    int pass;
}
