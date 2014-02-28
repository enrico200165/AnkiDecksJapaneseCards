package com.enrico_viali.jacn.kanjidic;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.enrico_viali.jacn.common.*;
import com.enrico_viali.utils.Utl;

public class KanjidicEntry extends EVJPCNEntry {

	public KanjidicEntry(String kanji) {
		super();
		this.separator = Cfg.SEP_EVKANJI_OUT;
		this.kanji = kanji;

		this.JISencoding = "0";
		this.onReadings = new ArrayList<String>();
		this.kunReadings = new ArrayList<String>();
		this.nanori = "indefinito";
		this.meanings = new ArrayList<String>();
		this.bushu = "-1";
		this.histRadical = "-1";
		this.frequency = 0;
		this.jlpt = Utl.NOT_INITIALIZED_INT;
		this.jouyou = Utl.NOT_INITIALIZED_INT;
		this.alpern = 0;
		this.nelson = 0;
		this.haigh = 0;
		this.jaBusyPeople = 0;
		this.kanjiWay = 0;
		this.kodansha = 0;
		this.henshall = 0;
		this.kanjiInContest = 0;
		this.halpern = 0;
		this.oNeill = 0;
		this.deRo = 0;
		this.sadake = 0;
		this.kask = 0;
		this.skip = 0;
		this.stroke = 0;
		this.unicode = 0;
		this.spanAdamitsky = 0;
		this.morohashiDaikanwajiten = 0;
		this.henshallGuideToRem = 0;
		this.gakken = 0;
		this.heisigNr = 0;
		this.oNeillJaNames = 0;
		this.koreanRead = "indefinito";
		this.pinyin = "indefinito";
		this.xRefs = null;
		this.misClasification = 0;
	}

	
    @Override
    public String toString() {
        return this.getKanji();
    }

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		/*
		result = prime * result + ((JISencoding == null) ? 0 : JISencoding.hashCode());
		result = prime * result + alpern;
		result = prime * result + ((bushu == null) ? 0 : bushu.hashCode());
		result = prime * result + ((csvString == null) ? 0 : csvString.hashCode());
		result = prime * result + deRo;
		result = prime * result + Float.floatToIntBits(frequency);
		result = prime * result + gakken;
		result = prime * result + haigh;
		result = prime * result + halpern;
		result = prime * result + heisigNr;
		result = prime * result + henshall;
		result = prime * result + henshallGuideToRem;
		result = prime * result + ((histRadical == null) ? 0 : histRadical.hashCode());
		result = prime * result + jaBusyPeople;
		result = prime * result + jlpt;
		result = prime * result + jouyou;
		*/
		result = prime * result + ((kanji == null) ? 0 : kanji.hashCode());
		/*
		result = prime * result + kanjiInContest;
		result = prime * result + kanjiWay;
		result = prime * result + kask;
		result = prime * result + kodansha;
		result = prime * result + ((koreanRead == null) ? 0 : koreanRead.hashCode());
		result = prime * result + ((kunReadings == null) ? 0 : kunReadings.hashCode());
		result = prime * result + ((meanings == null) ? 0 : meanings.hashCode());
		result = prime * result + misClasification;
		result = prime * result + morohashiDaikanwajiten;
		result = prime * result + ((nanori == null) ? 0 : nanori.hashCode());
		result = prime * result + nelson;
		result = prime * result + oNeill;
		result = prime * result + oNeillJaNames;
		result = prime * result + ((onReadings == null) ? 0 : onReadings.hashCode());
		result = prime * result + ((pinyin == null) ? 0 : pinyin.hashCode());
		result = prime * result + sadake;
		result = prime * result + ((separator == null) ? 0 : separator.hashCode());
		result = prime * result + skip;
		result = prime * result + spanAdamitsky;
		result = prime * result + stroke;
		result = prime * result + (int) (unicode ^ (unicode >>> 32));
		result = prime * result + ((xRefs == null) ? 0 : xRefs.hashCode());
		*/
		return result;
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof KanjidicEntry))
			return false;
		KanjidicEntry other = (KanjidicEntry) obj;
		if (JISencoding == null) {
			if (other.JISencoding != null)
				return false;
		} else if (!JISencoding.equals(other.JISencoding))
			return false;
		if (alpern != other.alpern)
			return false;
		if (bushu == null) {
			if (other.bushu != null)
				return false;
		} else if (!bushu.equals(other.bushu))
			return false;
		if (csvString == null) {
			if (other.csvString != null)
				return false;
		} else if (!csvString.equals(other.csvString))
			return false;
		if (deRo != other.deRo)
			return false;
		if (Float.floatToIntBits(frequency) != Float.floatToIntBits(other.frequency))
			return false;
		if (gakken != other.gakken)
			return false;
		if (haigh != other.haigh)
			return false;
		if (halpern != other.halpern)
			return false;
		if (heisigNr != other.heisigNr)
			return false;
		if (henshall != other.henshall)
			return false;
		if (henshallGuideToRem != other.henshallGuideToRem)
			return false;
		if (histRadical == null) {
			if (other.histRadical != null)
				return false;
		} else if (!histRadical.equals(other.histRadical))
			return false;
		if (jaBusyPeople != other.jaBusyPeople)
			return false;
		if (jlpt != other.jlpt)
			return false;
		if (jouyou != other.jouyou)
			return false;
		if (kanji == null) {
			if (other.kanji != null)
				return false;
		} else if (!kanji.equals(other.kanji))
			return false;
		if (kanjiInContest != other.kanjiInContest)
			return false;
		if (kanjiWay != other.kanjiWay)
			return false;
		if (kask != other.kask)
			return false;
		if (kodansha != other.kodansha)
			return false;
		if (koreanRead == null) {
			if (other.koreanRead != null)
				return false;
		} else if (!koreanRead.equals(other.koreanRead))
			return false;
		if (kunReadings == null) {
			if (other.kunReadings != null)
				return false;
		} else if (!kunReadings.equals(other.kunReadings))
			return false;
		if (meanings == null) {
			if (other.meanings != null)
				return false;
		} else if (!meanings.equals(other.meanings))
			return false;
		if (misClasification != other.misClasification)
			return false;
		if (morohashiDaikanwajiten != other.morohashiDaikanwajiten)
			return false;
		if (nanori == null) {
			if (other.nanori != null)
				return false;
		} else if (!nanori.equals(other.nanori))
			return false;
		if (nelson != other.nelson)
			return false;
		if (oNeill != other.oNeill)
			return false;
		if (oNeillJaNames != other.oNeillJaNames)
			return false;
		if (onReadings == null) {
			if (other.onReadings != null)
				return false;
		} else if (!onReadings.equals(other.onReadings))
			return false;
		if (pinyin == null) {
			if (other.pinyin != null)
				return false;
		} else if (!pinyin.equals(other.pinyin))
			return false;
		if (sadake != other.sadake)
			return false;
		if (separator == null) {
			if (other.separator != null)
				return false;
		} else if (!separator.equals(other.separator))
			return false;
		if (skip != other.skip)
			return false;
		if (spanAdamitsky != other.spanAdamitsky)
			return false;
		if (stroke != other.stroke)
			return false;
		if (unicode != other.unicode)
			return false;
		if (xRefs == null) {
			if (other.xRefs != null)
				return false;
		} else if (!xRefs.equals(other.xRefs))
			return false;
		return true;
	}



	public boolean keyEquals(Object obj) {
		if (this == obj)
			return true;
		KanjidicEntry other = (KanjidicEntry) obj;
		if (kanji == null) {
			if (other.kanji != null)
				return false;
		} else if (!kanji.equals(other.kanji))
			return false;
		return true;
	}
		

	@Override
	public String getKeyExpression() {
		return getKanji();
	}

	public String toString(String sep) {
		String s = "";
		s += this.kanji;
		s += sep + this.heisigNr;
		return s;
	}

	boolean fillValuesVector(String[] v) {
		log.error("funzione non implementata");
		System.exit(1);
		return false;
	}

	public void addMeaning(String meanPar) {
		meanings.add(meanPar);
	}

	
	
	public String getAsCSVLine(long scanned,long included,
	String sep, String sepReplacement) {
		log.warn("dummy/partial implementation check it!!!");
		return getCSVLine(sep,sepReplacement);
	}

	
	/**
	 * crea la linea del file csv
	 * 
	 * @return
	 */
	public String getCSVLine(String sep, String sepReplacement) {
		String tmp;

		this.csvString = "";

		addCSVElement("" + this.kanji);
		addCSVElement("pinyin=" + this.pinyin);
		tmp = "on= ";
		for (String s : this.onReadings) {
			tmp += (s + " ");
		}
		addCSVElement(tmp);
		tmp = "kun= ";
		for (String s : this.kunReadings) {
			tmp += (s + " ");
		}
		addCSVElement(tmp);
		tmp = "mean= ";
		for (String s : this.meanings) {
			tmp += (s + " ");
		}
		addCSVElement(tmp);
		addCSVElement("heisigNr=" + this.heisigNr);
		addCSVElement("jouyou=" + this.jouyou);
		addCSVElement("strokes=" + this.stroke);
		addCSVElement("frequency=" + this.frequency);

		return csvString;
	}

	
	void addCSVElement(String s) {
		csvString += (s + separator);
	}

	
	public static boolean buildFromLine(String lineaKanjidic, final String kanjidicLineSep, KanjidicEntry entry) {
		int i = 0;
		boolean onReadingFound = false;
		// splitta la riga nei singoli elementi
		String[] elementi = new String[100];

		if (!manageSpaceInMeanings(elementi, lineaKanjidic, "" + Cfg.SEP_KANJIDIC)) {
			log.error("manageSpaceInMeanings returned false");
			return false;
		}

		// The first part of each line is of a fixed format,
		// indicating which character the line is for, while the rest is more
		// free-format.

		// The first two bytes are the kanji itself. There is then a space
		entry.setKanji(elementi[i].charAt(0) + "");

		// the 4-byte ASCII representation of the hexadecimal coding of the
		// two-byte JIS encoding, and another space
		entry.setJISencoding(elementi[1]);

		// The rest of the line is composed of a combination of three kinds of
		// fields (which may be in any order and interspersed):
		for (i = 2; (i < elementi.length) && (elementi[i] != null); i++) {
			String curElement = elementi[i];
			switch (curElement.charAt(0)) {
			case '{': {
				// remove curly braces
				String mean = curElement.substring(1, curElement.length() - 1);
				entry.addMeaning(mean);
				break;
			}
			case 'T': {
				break;
			}
			case 'G': // Jouyou grade level. At most one per line. G1 through
			{
				entry.setJouyou(Utl.intFromStringAt1(curElement));
				// debug
				// if (curElement.equals("G10"))
				// /if (curElement.length() > 2 && !curElement.equals("G10"))
				if (entry.getJouyou() == 1 && !curElement.equals("G1"))
					log.debug("solo per breakpoint");
				break;
			}
			case 'J': {
				entry.jlpt = Utl.intFromStringAt1(curElement);
				break;
			}
			case 'L': { // index in Heisig in remembering the kanjis
				entry.setHeisig(Utl.intFromStringAt1(curElement));
				break;
			}
			case 'S': { // stroke count
				entry.setStroke(Utl.intFromStringAt1(curElement));
				break;
			}
			case 'Y': // pinyin
			{
				entry.setPinyin(curElement.substring(1));
				break;
			}
			case 'F': {
				entry.setFrequency(Utl.intFromStringAt1(curElement));
				break;
			}
				// informazioni ignorate
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'H':
			case 'I':
			case 'K':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Z': {
				break;
			}
			default: {
				if (CJKUtils.containsHiragana(curElement)) {
					entry.addKunReading(curElement);
				} else if (CJKUtils.containsKatakana(curElement)) {
					entry.addOnReading(curElement);
					onReadingFound = true;
				} else {
					log.error("riga: " + i + " non gestito elemento: " + curElement);
					return false;
				}
			}
			}
		}

		return true;
	}

	static boolean manageSpaceInMeanings(String[] entries, String csvLine, String inputDelimiter) {

		String pre_entries[] = csvLine.split(inputDelimiter);
		if (pre_entries.length <= 3) { // aspetto almeno kanji reading e 1
			// significato
			log.error("pre_entries[i].length() <=0");
			return false;
		}

		// ora possiamo avere 1 significato con più parole splittato su pi�
		// stringhe

		int j = 0;
		int i = 0;
		while ((i < pre_entries.length) && (pre_entries[i] != null) && (i < entries.length) && (j < entries.length)) {
			if (pre_entries[i].length() <= 0) {
				log.debug("skipping pre_entries[" + i + "].length() <= 0\nthe line is:" + "\n" + csvLine);
				i++;
				continue;
			}
			log.trace("i,j=" + i + ", " + j);
			entries[j] = pre_entries[i];
			if ((pre_entries[i].charAt(0) == '{') && (pre_entries[i].charAt(pre_entries[i].length() - 1) != '}')) {
				// � cominciato un significato, primo char '{' ma non finisce
				// nello stesso token, ultimo char != }
				// dobbiamo concatenare nello stesso token desinazione fino a }
				do {
					i++; // elemnto corrente gia assegnato, esaminiamo e
					// assegniamo il successivo
					if ((i >= pre_entries.length) || (pre_entries[i] == null)) {
						System.err.println("pre_entries[i] == null || i >= pre_entries.length, i = " + i);
						break;
					} else {
						entries[j] += Cfg.SEP_KANJIDIC + pre_entries[i];
					}
				} while (pre_entries[i].charAt(pre_entries[i].length() - 1) != '}');
			}
			j++;
			i++; // i � gia stato incrementato
		}

		return true;
	}

	public boolean hasOnReading() {
		return this.onReadings.size() > 0;
	}

	public String getKanji() {
		return kanji;
	}

	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	public String getJISencoding() {
		return JISencoding;
	}

	public void setJISencoding(String sencoding) {
		JISencoding = sencoding;
	}

	public ArrayList<String> getOnReadings() {
		return this.onReadings;
	}

	public String getOnReadingsStr(String sep) {
		String s = "";
		for (String e : this.onReadings) {
			s += e + sep;
		}
		return s;
	}

	public void addOnReading(String on) {
		this.onReadings.add(on);
	}

	public ArrayList<String> getKunReadings() {
		return this.kunReadings;
	}

	public String getKunReadingsStr(String sep) {
		String s = "";
		for (String e : this.kunReadings) {
			s += e + sep;
		}
		return s;
	}

	public void addKunReading(String kun) {
		this.kunReadings.add(kun);
	}

	public String getNanori() {
		return nanori;
	}

	public void setNanori(String nanori) {
		this.nanori = nanori;
	}

	public ArrayList<String> getMeanings() {
		return meanings;
	}

	public String getMeaningsStr(String sep) {
		String s = "";
		for (String m : meanings) {
			s += m + sep + " ";
		}

		// remove ending ,space
		if (s.length() > 2)
			s = s.substring(0, s.length() - 2);
		return s;
	}

	public void setMeanings(ArrayList<String> meanings) {
		this.meanings = meanings;
	}

	public String getBushu() {
		return bushu;
	}

	public void setBushu(String bushu) {
		this.bushu = bushu;
	}

	public String getHistRadical() {
		return histRadical;
	}

	public void setHistRadical(String histRadical) {
		this.histRadical = histRadical;
	}

	public float getFrequency() {
		return frequency;
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
	}

	public int getJouyou() {
		return jouyou;
	}

	public int getOldJlpt() {
		return this.jlpt; // per ora non sembra essere inclusa questa informazione
	}

	public void setJouyou(int jouyou) {
		this.jouyou = jouyou;
	}

	public int getAlpern() {
		return alpern;
	}

	public void setAlpern(int alpern) {
		this.alpern = alpern;
	}

	public int getNelson() {
		return nelson;
	}

	public void setNelson(int nelson) {
		this.nelson = nelson;
	}

	public int getHaigh() {
		return haigh;
	}

	public void setHaigh(int haigh) {
		this.haigh = haigh;
	}

	public int getJaBusyPeople() {
		return jaBusyPeople;
	}

	public void setJaBusyPeople(int jaBusyPeople) {
		this.jaBusyPeople = jaBusyPeople;
	}

	public int getKanjiWay() {
		return kanjiWay;
	}

	public void setKanjiWay(int kanjiWay) {
		this.kanjiWay = kanjiWay;
	}

	public int getKodansha() {
		return kodansha;
	}

	public void setKodansha(int kodansha) {
		this.kodansha = kodansha;
	}

	public int getHenshall() {
		return henshall;
	}

	public void setHenshall(int henshall) {
		this.henshall = henshall;
	}

	public int getKanjiInContest() {
		return kanjiInContest;
	}

	public void setKanjiInContest(int kanjiInContest) {
		this.kanjiInContest = kanjiInContest;
	}

	public int getHalpern() {
		return halpern;
	}

	public void setHalpern(int halpern) {
		this.halpern = halpern;
	}

	public int getONeill() {
		return oNeill;
	}

	public void setONeill(int neill) {
		oNeill = neill;
	}

	public int getDeRo() {
		return deRo;
	}

	public void setDeRo(int deRo) {
		this.deRo = deRo;
	}

	public int getSadake() {
		return sadake;
	}

	public void setSadake(int sadake) {
		this.sadake = sadake;
	}

	public int getKask() {
		return kask;
	}

	public void setKask(int kask) {
		this.kask = kask;
	}

	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	public int getStroke() {
		return stroke;
	}

	public void setStroke(int stroke) {
		this.stroke = stroke;
	}

	public long getUnicode() {
		return unicode;
	}

	public void setUnicode(long unicode) {
		this.unicode = unicode;
	}

	public int getSpanAdamitsky() {
		return spanAdamitsky;
	}

	public void setSpanAdamitsky(int spanAdamitsky) {
		this.spanAdamitsky = spanAdamitsky;
	}

	public int getMorohashiDaikanwajiten() {
		return morohashiDaikanwajiten;
	}

	public void setMorohashiDaikanwajiten(int morohashiDaikanwajiten) {
		this.morohashiDaikanwajiten = morohashiDaikanwajiten;
	}

	public int getHenshallGuideToRem() {
		return henshallGuideToRem;
	}

	public void setHenshallGuideToRem(int henshallGuideToRem) {
		this.henshallGuideToRem = henshallGuideToRem;
	}

	public int getGakken() {
		return gakken;
	}

	public void setGakken(int gakken) {
		this.gakken = gakken;
	}

	public int getHeisig() {
		return heisigNr;
	}

	public void setHeisig(int heisig) {
		this.heisigNr = heisig;
	}

	public int getONeillJaNames() {
		return oNeillJaNames;
	}

	public void setONeillJaNames(int neillJaNames) {
		oNeillJaNames = neillJaNames;
	}

	public String getKoreanRead() {
		return koreanRead;
	}

	public void setKoreanRead(String koreanRead) {
		this.koreanRead = koreanRead;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public ArrayList<String> getXRefs() {
		return xRefs;
	}

	public void setXRefs(ArrayList<String> refs) {
		xRefs = refs;
	}

	public int getMisClasification() {
		return misClasification;
	}

	public void setMisClasification(int misClasification) {
		this.misClasification = misClasification;
	}

	private static org.apache.log4j.Logger log = Logger.getLogger(KanjidicEntry.class);

	String csvString = "";
	String separator;

	// campi di kanjdic
	public String kanji;
	String JISencoding;
	ArrayList<String> onReadings;
	ArrayList<String> kunReadings;
	String nanori;
	ArrayList<String> meanings;
	String bushu;
	String histRadical;
	float frequency;
	int jouyou;
	int alpern;
	int nelson;
	int haigh;
	public int jlpt;

	// D<code> -- the "D" codes will be progressively used for dictionary based
	// codes.
	// DBnnn - the index numbers used in "Japanese For Busy People" vols I-III,
	// published by the AJLT. The codes are the volume.chapter.
	int jaBusyPeople;

	// DCnnnn - the index numbers used in "The Kanji Way to Japanese Language
	// Power" by Dale Crowley
	int kanjiWay;

	// DGnnn - the index numbers used in the "Kodansha Compact Kanji Guide".
	int kodansha;

	// DHnnnn - the index numbers used in the 3rd edition of "A Guide To Reading
	// and Writing Japanese" edited by Ken Hensall et al.
	int henshall;

	// DJnnnn - the index numbers used in the "Kanji in Context" by Nishiguchi
	// and Kono.
	int kanjiInContest;

	// DKnnnn - the index numbers used by Jack Halpern in his Kanji Learners
	// Dictionary, published by Kodansha in 1999. The numbers have been provided
	// by Mr Halpern.
	int halpern;

	// DOnnnn - the index numbers used in P.G. O'Neill's Essential Kanji The
	// numbers have been provided by Glenn Rosenthal.
	int oNeill;

	// DRnnnn - these are the codes developed by Father Joseph De Roo, and
	// published in his book "2001 Kanji" (Bojinsha).
	int deRo;

	// DSnnnn - the index numbers used in the early editions of "A Guide To
	// Reading and Writing Japanese" edited by Florence Sakade.
	int sadake;

	// DTnnn - the index numbers used in the Tuttle Kanji Cards, compiled by
	// Alexander Kask.
	int kask;

	// P<code> -- the SKIP pattern code. The <code> is of the form
	// "P<num>-<num>-<num>".
	int skip;

	// S<num> -- the stroke count. At least one per line. If more than one, the
	// first is considered the accepted count, while subsequent ones are common
	// miscounts.
	int stroke;

	// U<hexnum> -- the Unicode encoding of the kanji.
	long unicode;

	// I<code> -- the index codes in the reference books by Spahn & Hadamitzky.
	int spanAdamitsky;

	// MNnnnnnnn and MPnn.nnnn -- the index number and volume.page respectively
	// of the kanji in the 13-volume Morohashi Daikanwajiten.
	int morohashiDaikanwajiten;

	// Ennnn -- the index number used in "A Guide To Remembering Japanese
	// Characters" by Kenneth G. Henshall.
	int henshallGuideToRem;

	// Knnnn -- the index number in the Gakken Kanji Dictionary ("A New
	// Dictionary of Kanji Usage"). Some of the numbers relate to the list at
	// the back of the book, ..
	int gakken;

	// Lnnnn -- the index number used in "Remembering The Kanji" by James
	// Heisig.
	int heisigNr;

	// Onnnn -- the index number in "Japanese Names", by P.G. O'Neill.
	int oNeillJaNames;

	// Wxxxx -- the romanized form of the Korean reading(s) of the kanji.
	String koreanRead;

	// Yxxxxx -- the "Pinyin" of each kanji, i.e. the (Mandarin or Beijing)
	// Chinese romanization.
	String pinyin;

	// Xxxxxxx -- a cross-reference code. An entry of, say, XN1234
	ArrayList<String> xRefs;

	// Zxxxxxx -- a mis-classification code. It means that this kanji is
	// sometimes mis-classified as having the xxxxxx coding.
	int misClasification;

	@Override
	public long getNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getAsHTMLLine(long scanned, long included, int level) {
		// TODO Auto-generated method stub
		return null;
	}

}
