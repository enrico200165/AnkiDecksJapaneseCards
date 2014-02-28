package com.enrico_viali.gtranslate;


import com.google.api.translate.Language;

public class Main {
	public static void main(String[] args) throws Exception {
		
		GTranslate trsl = new GTranslateDB();
		
		RespTransl translatedText = new RespTransl();
		translatedText = trsl.translate("house", Language.ENGLISH, Language.ITALIAN);
		translatedText = trsl.translate("hello", Language.ENGLISH, Language.ITALIAN);
		translatedText = trsl.translate("sunset", Language.ENGLISH, Language.ITALIAN);
		
		System.out.println(translatedText.translation);
	}
}