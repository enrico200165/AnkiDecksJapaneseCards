package com.enricoviali.epub.ja.rtk2;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public interface IPage {

    public abstract void parsePage(int fileNr, Document page, String filename);

    public abstract boolean processTable(int fileNr, Element table, String filename, int scanNr);

}