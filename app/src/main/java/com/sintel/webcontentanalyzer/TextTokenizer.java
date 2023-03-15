/* 
 * Copyright (C) 2014 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sintel.webcontentanalyzer;

import com.sintel.webcontentanalyzer.Document;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TextTokenizer {
    
    /**
     * Preprocess the text by removing punctuation, duplicate spaces and 
     * lowercasing it.
     * 
     * @param text
     * @return 
     */
    public static String preprocess(String text) {
        return text.replaceAll("\\p{P}", " ").replaceAll("\\s+", " ").toLowerCase(Locale.getDefault());
    }

    public static  String cleanTexte(String text){
        //text = text.replaceAll("[éèê]+","e");
        //text = text.replaceAll("[àâ]+","a");
        //text = text.replaceAll("î","i");
        //text = text.replaceAll("[ûù]+","u");
        //text = text.replaceAll("ô","o");
        //text=text.replaceAll("[ɗƊ]","d");
        //text=text.replaceAll("[ɓƁ]","b");
        //text=text.replaceAll("ŋ","n");
        //text=text.replaceAll("ɗ","d");
        text = text.replaceAll("[^a-zA-Zéêèàâçûùôî]+"," ");
        //text = text.replaceAll("[()?:!.'<>*=/\\,;{}]+"," ");
        return  text.trim();
    }
    
    /**
     * A simple method to extract the keywords from the text. For real world 
     * applications it is necessary to extract also keyword combinations.
     * 
     * @param text
     * @return 
     */
    public static String[] extractKeywords(String text) {
        return text.split(" ");
    }
    
    /**
     * Counts the number of occurrences of the keywords inside the text.
     * 
     * @param keywordArray
     * @return 
     */
    public static Map<String, Integer> getKeywordCounts(String[] keywordArray) {
        Map<String, Integer> counts = new HashMap<>();
        
        Integer counter;
        for(int i=0;i<keywordArray.length;++i) {
            counter = counts.get(keywordArray[i]);
            if(counter==null) {
                counter=0;
            }
            counts.put(keywordArray[i], ++counter); //increase counter for the keyword
        }
        
        return counts;
    }
    
    /**
     * Tokenizes the document and returns a Document Object.
     * 
     * @param text
     * @return 
     */
    public static Document tokenize(String text) {
        text=cleanTexte(text);
        //text=AnalyseActivity.remove_stop_word(text);
        String preprocessedText = preprocess(text);
        String[] keywordArray = extractKeywords(preprocessedText);
        Document doc = new Document();
        doc.tokens = getKeywordCounts(keywordArray);
        return doc;
    }
}
