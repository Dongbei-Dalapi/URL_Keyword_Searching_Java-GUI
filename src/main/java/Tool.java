
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.lemmatizer.LemmatizerME;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.*;
import java.io.File;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;





import java.util.*;

public class Tool {
    private String key;
    private String url;


    /**
     * constructor
     */
    public Tool(String akey, String aurl) {
        key = akey;
        url = aurl;
    }

    /**
     * generate all the sentences contains word(exact form)
     *
     * @return ArrayList<Sentence>
     */
    public ArrayList<Sentence> exactForm() throws IOException {
        //create ana arrayList to return
        ArrayList<Sentence> result = new ArrayList<Sentence>();

        //extract text from webpage/file-->save to a String(text)
        String text = "";
        try {
            // Create a URL for the desired page
            URL aurl = new URL(url);

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(aurl.openStream()));
            String line ;
            while ((line = in.readLine()) != null) {
                line = in.readLine();
                text = text + line;
            }
            in.close();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        //replace all html tag
        text = text.replaceAll("</?[^>]+>", "");
        //text = text.replaceAll("&#91;/?[^>]+&#93","");

        //Array[String] = sentenceDetector(text)
        //Get file from resources folder(bin file)
        InputStream isent = WordSearcher.class.getResourceAsStream("en-sent.bin");
        SentenceModel msent = new SentenceModel(isent);
        SentenceDetectorME detector = new SentenceDetectorME(msent);
        String sentences[] = detector.sentDetect(text);


        for (int i = 0; i < sentences.length; i++) {
            //apply Tokenizer to each element
            InputStream itoken = WordSearcher.class.getResourceAsStream("en-token.bin");
            TokenizerModel mtoken = new TokenizerModel(itoken);
            TokenizerME tokenizer = new TokenizerME(mtoken);
            String[] tokens = tokenizer.tokenize(sentences[i]);


            //create a Sentence object
            Sentence s = new Sentence(tokens);


            //check if the sentence contain the keyword
            //record the index of occurrence of the keyword in the Sentence element index
            for (int j = 0; j < tokens.length; j++) {
                if (tokens[j].equalsIgnoreCase(key))
                    s.addIndex(j);
            }

            //check the index list,if there is index in it put it in the result Arraylist
            ArrayList<Integer> indexlist =s.getIndex();
            indexlist.trimToSize();
            if (indexlist.size()> 0)
                result.add(s);
        }
        //return this ArrayList<Sentence>
        return result;
    }//exactForm

    /**
     * generate all the sentences contains word(lemma)
     *
     * @return ArrayList<Sentence>
     */
    public ArrayList<Sentence> lemma() throws IOException {
        //create ana arrayList to return
        ArrayList<Sentence> result = new ArrayList<Sentence>();

        //extract text from webpage/file-->save to a String(text)
        String text = "";
        try {
            // Create a URL for the desired page
            URL aurl = new URL(url);

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(aurl.openStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                line = in.readLine();
                text = text + line;
            }
            in.close();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        //replace all html tag
        text = text.replaceAll("</?[^>]+>", "");

        //Array[String] = sentenceDetector(text)
        InputStream isent = WordSearcher.class.getResourceAsStream("en-sent.bin");
        SentenceModel msent = new SentenceModel(isent);
        SentenceDetectorME detector = new SentenceDetectorME(msent);
        String sentences[] = detector.sentDetect(text);


        for (int i = 0; i < sentences.length; i++) {
            //apply Tokenizer to each element
            InputStream itoken = WordSearcher.class.getResourceAsStream("en-token.bin");
            TokenizerModel mtoken = new TokenizerModel(itoken);
            TokenizerME tokenizer = new TokenizerME(mtoken);
            String[] tokens = tokenizer.tokenize(sentences[i]);

            //generate the postag
            String[] postag = posGenerator(tokens);

            //create a Sentence object
            Sentence s = new Sentence(tokens);

            String[] lemma = lemmaGenerator(tokens, postag);
            String[] keyword = {key};
            String[] keytag = posGenerator(keyword);
            String k_lemma = lemmaGenerator(keyword, keytag)[0];
            //check if the sentence contain the word which has the same lemma as the keyword
            //record the index of occurrence of the keyword in the Sentence element index
            for (int j = 0; j < lemma.length; j++) {
                if (lemma[j].equalsIgnoreCase(k_lemma)) {
                    s.addIndex(j);
                }
            }

            //check the index list,if there is index in it put it in the result Arraylist
            ArrayList<Integer> indexlist =s.getIndex();
            indexlist.trimToSize();
            if (indexlist.size() != 0)
                result.add(s);
        }

        //return this ArrayList<Sentence>
        return result;

    }//lemma

    /**
     * generate all the sentences contains word(postag)
     *
     * @return ArrayList<Sentence>
     */
    public ArrayList<Sentence> postag() throws IOException {
        //create ana arrayList to return
        ArrayList<Sentence> result = new ArrayList<Sentence>();

        //extract text from webpage/file-->save to a String(text)
        String text = "";
        try {
            // Create a URL for the desired page
            URL aurl = new URL(url);

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(aurl.openStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                line = in.readLine();
                text = text + line;
            }
            in.close();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        //replace all html tag
        text = text.replaceAll("</?[^>]+>", "");

        //Array[String] = sentenceDetector(text)
        InputStream isent = WordSearcher.class.getResourceAsStream("en-sent.bin");
        SentenceModel msent = new SentenceModel(isent);
        SentenceDetectorME detector = new SentenceDetectorME(msent);
        String sentences[] = detector.sentDetect(text);


        for (int i = 0; i < sentences.length; i++) {
            //apply Tokenizer to each element
            InputStream itoken = WordSearcher.class.getResourceAsStream("en-token.bin");
            TokenizerModel mtoken = new TokenizerModel(itoken);
            TokenizerME tokenizer = new TokenizerME(mtoken);
            String[] tokens = tokenizer.tokenize(sentences[i]);

            //generate the postag
            String[] pos = posGenerator(tokens);

            //create a Sentence object
            Sentence s = new Sentence(tokens);

            //check if the sentence contain the word which has the same lemma as the keyword
            //record the index of occurrence of the keyword in the Sentence element index
            for (int j = 0; j < pos.length; j++) {
                if (pos[j].equalsIgnoreCase(key)) {
                    s.addIndex(j);
                }
            }

            //check the index list,if there is index in it put it in the result Arraylist
            ArrayList<Integer> indexlist =s.getIndex();
            indexlist.trimToSize();
            if (indexlist.size() != 0)
                result.add(s);
        }
        //return this ArrayList<Sentence>
        return result;
    }//postag


    /**
     * generate  POS tags
     *
     * @param words
     * @return ArrayList<Sentence>
     */
    //might need a helping method
    public String[] posGenerator(String[] words) throws IOException {
        //apply nlp
        InputStream ipos = WordSearcher.class.getResourceAsStream("en-pos-maxent.bin");
        POSModel mpos = new POSModel(ipos);
        POSTaggerME postagger = new POSTaggerME(mpos);
        String[] tag = postagger.tag(words);
        return tag;

    }

    /**
     * generate lemma
     *
     * @param words,tags
     * @return ArrayList<Sentence>
     */
    public String[] lemmaGenerator(String[] words, String[] tags) throws IOException {
        //apply nlp
        InputStream ilemma = WordSearcher.class.getResourceAsStream("en-lemmatizer.bin");
        LemmatizerModel mlemma = new LemmatizerModel(ilemma);
        LemmatizerME lemmatizer = new LemmatizerME(mlemma);
        String[] lemma = lemmatizer.lemmatize(words, tags);
        return lemma;
    }

    /**
     * generate XML file
     * use DOM
     *
     * @param list,type
     */
    public void xmlGenerator(ArrayList<Sentence> list, String type) throws ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element searchresult = document.createElement("searchresult");
        Element from = document.createElement("from");
        from.setAttribute("url", url);
        Element searchTerm = document.createElement("searchTerm");
        searchTerm.setAttribute("value", key);
        searchTerm.setAttribute("type", type);
        ;
        Element results = document.createElement("results");

        //a for loop to iterate the sentence in the ArrayList
        for (int i = 0; i < list.size(); i++) {
            String tokens[] = list.get(i).getSentence();
            String postags[] = posGenerator(tokens);
            String lemmas[] = lemmaGenerator(tokens, postags);
            Element result = document.createElement("result");
            // a for loop to iterate words in a sentence
            for (int j = 0; j < tokens.length; j++) {
                Element t = document.createElement("t");
                t.setAttribute("word", tokens[j]);
                t.setAttribute("lemma", lemmas[j]);
                t.setAttribute("pos", postags[j]);

                result.appendChild(t);
            }//for
            results.appendChild(result);
        }//for
        searchresult.appendChild(from);
        searchresult.appendChild(searchTerm);
        searchresult.appendChild(results);
        document.appendChild(searchresult);

        //Transforming a DOM Tree to a String and save to file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        Result r = new StreamResult(new File(key + "_" + type));
        DOMSource source = new DOMSource(document);
        transformer.transform(source, r);


    }//xmlGenerator

    /**
     * calculate the position: invoke this method for eventlistener for the text area
     *
     * @param sentencelist : list of sentences
     * @return HashMap<String , Integer></> which contains 3 counts
     */
    public HashMap<String, Integer> positionCalculator(ArrayList<Sentence> sentencelist) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        //number
        int beginningcounter = 0;
        int middlecounter = 0;
        int endcounter = 0;

        for (int i = 0; i < sentencelist.size(); i++) {
            Sentence s = sentencelist.get(i);
            int len = s.getSentence().length;

            int beginning = len / 3;
            int middle = beginning * 2 + 1;

            ArrayList<Integer> in = s.getIndex();

            int num_index = in.size();
            for (int j = 0; j < num_index; j++) {
                int position = in.get(j).intValue();
                if (position <= beginning) {
                    beginningcounter++;
                } else if ((position > beginning) && (position <= middle)) {
                    middlecounter++;
                } else if (position > middle) {
                    endcounter++;
                }
            }
        }

        Integer beginning = new Integer(beginningcounter);
        Integer middle = new Integer(middlecounter);
        Integer end = new Integer(endcounter);

        result.put("beginning", beginning);
        result.put("middle", middle);
        result.put("end", end);
        return result;
    }//positionCalculator

    /**
     * calculate the category
     */
    public HashMap<String, Integer> categoryCalculator(ArrayList<Sentence> sentencelist) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        try{
            //iterate sentencelist, create postag array
            for(int i=0;i<sentencelist.size();i++){
                String tokens[] = sentencelist.get(i).getSentence();
                String postags[] = posGenerator(tokens);
                ArrayList<Integer> index = sentencelist.get(i).getIndex();
                //iterate index of the keyword in a sentence
                for(int j=0;j<index.size();j++){
                    if(postags[index.get(j).intValue()].equalsIgnoreCase("CC")){
                        if(result.containsKey("Coordinating conjunction"))
                            result.put("Coordinating conjunction",result.get("Coordinating conjunction")+1);
                        else
                            result.put("Coordinating conjunction",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("CD")){
                        if(result.containsKey("Cardinal number"))
                            result.put("Cardinal number",result.get("Cardinal number")+1);
                        else
                            result.put("Cardinal number",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("CC")){
                        if(result.containsKey("Coordinating conjunction"))
                            result.put("Coordinating conjunction",result.get("Coordinating conjunction")+1);
                        else
                            result.put("Coordinating conjunction",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("DT")||postags[index.get(j).intValue()].equalsIgnoreCase("PDT")||postags[index.get(j).intValue()].equalsIgnoreCase("WDT")){
                        if(result.containsKey("Determiner"))
                            result.put("Determiner",result.get("Determiner")+1);
                        else
                            result.put("Determiner",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("EX")){
                        if(result.containsKey("Existential there"))
                            result.put("Existential there",result.get("Existential there")+1);
                        else
                            result.put("Existential there",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("EW")){
                        if(result.containsKey("Foreign word"))
                            result.put("Foreign word",result.get("Foreign word")+1);
                        else
                            result.put("Foreign word",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("IN")){
                        if(result.containsKey("Preposition or subordinating conjunction"))
                            result.put("Preposition or subordinating conjunction",result.get("Preposition or subordinating conjunction")+1);
                        else
                            result.put("Preposition or subordinating conjunction",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("JJ")||postags[index.get(j).intValue()].equalsIgnoreCase("JJR")||postags[index.get(j).intValue()].equalsIgnoreCase("JJS")){
                        if(result.containsKey("Adjective"))
                            result.put("Adjective",result.get("Adjective")+1);
                        else
                            result.put("Adjective",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("LS")){
                        if(result.containsKey("List item marker"))
                            result.put("List item marker",result.get("List item marker")+1);
                        else
                            result.put("List item marker",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("MD")){
                        if(result.containsKey("Modal"))
                            result.put("Modal",result.get("Modal")+1);
                        else
                            result.put("Modal",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("NN")||postags[index.get(j).intValue()].equalsIgnoreCase("NNS")||postags[index.get(j).intValue()].equalsIgnoreCase("NNP")||postags[index.get(j).intValue()].equalsIgnoreCase("NNPS")){
                        if(result.containsKey("Noun"))
                            result.put("Noun",result.get("Noun")+1);
                        else
                            result.put("Noun",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("POS")){
                        if(result.containsKey("Possessive ending"))
                            result.put("Possessive ending",result.get("Possessive ending")+1);
                        else
                            result.put("Possessive ending",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("PRP")||postags[index.get(j).intValue()].equalsIgnoreCase("PRP$")||postags[index.get(j).intValue()].equalsIgnoreCase("WP")||postags[index.get(j).intValue()].equalsIgnoreCase("WP$")){
                        if(result.containsKey("Pronoun"))
                            result.put("Pronoun",result.get("Pronoun")+1);
                        else
                            result.put("Pronoun",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("RB")||postags[index.get(j).intValue()].equalsIgnoreCase("RBR")||postags[index.get(j).intValue()].equalsIgnoreCase("RBS")||postags[index.get(j).intValue()].equalsIgnoreCase("WRB")){
                        if(result.containsKey("Adverb"))
                            result.put("Adverb",result.get("Adverb")+1);
                        else
                            result.put("Adverb",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("VB")||postags[index.get(j).intValue()].equalsIgnoreCase("VBD")||postags[index.get(j).intValue()].equalsIgnoreCase("VBG")||postags[index.get(j).intValue()].equalsIgnoreCase("VBN")||postags[index.get(j).intValue()].equalsIgnoreCase("VBP")||postags[index.get(j).intValue()].equalsIgnoreCase("VBZ")){
                        if(result.containsKey("Verb"))
                            result.put("Verb",result.get("Verb")+1);
                        else
                            result.put("Verb",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("RP")){
                        if(result.containsKey("Particle"))
                            result.put("Particle",result.get("Particle")+1);
                        else
                            result.put("Particle",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("SYM")){
                        if(result.containsKey("Symbol"))
                            result.put("Symbol",result.get("Symbol")+1);
                        else
                            result.put("Symbol",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("TO")){
                        if(result.containsKey("To"))
                            result.put("To",result.get("To")+1);
                        else
                            result.put("To",1);
                    }
                    else if(postags[index.get(j).intValue()].equalsIgnoreCase("UH")){
                        if(result.containsKey("Interjection"))
                            result.put("Interjection",result.get("Interjection")+1);
                        else
                            result.put("Interjection",1);
                    }
                }
            }
        }
        catch(IOException e){e.getMessage();}
        return result;
    }//categoryCalculator



}