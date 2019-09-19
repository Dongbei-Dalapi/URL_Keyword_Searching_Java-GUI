
import java.io.IOException;


import javax.xml.parsers.ParserConfigurationException;


import javax.xml.transform.*;



import java.util.*;


import javax.swing.*;
import java.awt.event.*;
import java.awt.FlowLayout;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.awt.event.*;     // import window adapter
import java.awt.FlowLayout;  // import a FlowLayout layout manager
import java.awt.Dimension;  // import Dimension

//package for highlighter
import java.awt.Color;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;


public class WordSearcher {
    private JFrame frame;
    private JTextField enter;
    private JTextField entering_url;
    private JButton search;
    private JList list_of_choices;
    private JTextArea result;
    private JTextArea pnc;
    private JButton clear;
    private JButton save;

    private JLabel aLabel;

    //search result sentence
    private ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();
    //keyword and url
    private String enter_key;
    private String enter_url;

    WordSearcher() {

        frame = new JFrame("Search");
        frame.setSize(300, 200);
        /**
         * Override JFrames default layout manager.
         * Components are left aligned and have a horizontal space of
         */


        FlowLayout aFlowLayout = new FlowLayout(FlowLayout.LEFT, 10, 20);
        frame.getContentPane().setLayout(aFlowLayout);

         /*
         create and add components
         */

        //JTextField: enter the item to be searched
        enter = new JTextField(15);
        //JTextFiled: entering url
        entering_url = new JTextField(15);
        //JButton:Search
        search = new JButton("Search");
        //JList: make your choice

        String[] choices = {"exact from", "lemma", "entering pos"};

        DefaultListModel aListModel = new DefaultListModel();
        for (int i = 0; i < choices.length; i++)
            aListModel.addElement(choices[i]);

        list_of_choices = new JList(aListModel);
        JScrollPane listPane = new JScrollPane(list_of_choices);

        //JTextArea: display search result
        result = new JTextArea(30, 110);
        JScrollPane sp = new JScrollPane(result, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //JTextArea: display position and category
        pnc = new JTextArea(20, 50);
        //JButton: clear all
        clear = new JButton("Clear All");
        //JButton: save to file
        save = new JButton("save to file");
        //JLabel
        aLabel = new JLabel("Make your choice:");


        /**
         * add components
         */
        frame.getContentPane().add(enter);
        frame.getContentPane().add(entering_url);
        frame.getContentPane().add(search);
        frame.getContentPane().add(aLabel);
        frame.getContentPane().add(listPane);
        frame.getContentPane().add(list_of_choices);
        //frame.getContentPane().add(result);
        frame.getContentPane().add(sp);
        frame.getContentPane().add(pnc);
        frame.getContentPane().add(save);
        frame.getContentPane().add(clear);

        /**
         * add event listener
         */
        search.addActionListener(new searchButtonHandler());
        list_of_choices.addListSelectionListener(new ValueReporter());
        save.addActionListener(new saveButtonHandler());
        clear.addActionListener(new clearButtonHandler());


        frame.addWindowListener(new MyWindowListener());

        frame.setVisible(true);

        /**
         * initial set up
         */
        enter.setText("Enter a word");
        entering_url.setText("Enter URL");
        result.setText("Search result will be displayed here");
        pnc.setText("category and position information");
        clear.setToolTipText("Double Click");
    }

    private class MyWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    /**
     * event listeners for button search
     */
    private class searchButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            enter_key = enter.getText();
            enter_url = entering_url.getText();

            String text = "";
            Tool t = new Tool(enter_key, enter_url);
            int chosen = list_of_choices.getSelectedIndex();
            ArrayList<Integer> indexHighLight = new ArrayList<Integer>();
            int start = 0;
            int end = 0;

            //get the search result sentence from Tool private class
            try {
                if (chosen == 0) {
                    //exact form
                    sentenceList = t.exactForm();
                } else if (chosen == 1) {
                    //lemma
                    sentenceList = t.lemma();
                } else if (chosen == 2) {
                    //pos
                    sentenceList = t.postag();
                }
            } catch (IOException ex) {
                System.out.println("cannot find the file");//ex.getMessage();
            }
            //get whole string text for display at textarea and the index for highlighting
            for (int i = 0; i < sentenceList.size(); i++) {
                for (int j = 0; j < sentenceList.get(i).getSentence().length; j++) {
                    String temp = sentenceList.get(i).getSentence()[j];
                    if (i == 0 && j == 0) {
                        text = temp;
                        end = temp.length();
                    } else if (j == 0 || temp.equals(".") || temp.equals(",") || temp.equals("?") || temp.equals("!") || temp.equals(";") || temp.equals(":")) {
                        text = text + temp;
                        start = end;
                        end = end + temp.length();
                    } else {
                        text = text + " " + temp;
                        start = end + 1;
                        end = end + 1 + temp.length();
                    }
                    if (j == (sentenceList.get(i).getSentence().length - 1)) {
                        text = text + "\n";
                        end = end + 1;
                    }
                    if (sentenceList.get(i).getIndex().contains(new Integer(j))) {
                        indexHighLight.add(start);
                        indexHighLight.add(end);
                    }


                }
            }//for

            result.setText(text);
            result.setLineWrap(true);
            //highligt the words
            Highlighter highlighter = result.getHighlighter();
            HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.green);
            try {
                for (int i = 0; i < (indexHighLight.size()); i = i + 2) {
                    int first = indexHighLight.get(i).intValue();
                    int last = indexHighLight.get(i + 1).intValue();
                    highlighter.addHighlight(first, last, painter);
                }
            } catch (BadLocationException ex) {
                ex.getMessage();
            }

            // display the percentage of the position
            String position = "Position with in a sentence:\n";
            int be = 0, mi = 0, en = 0, sum = 0;
            //caculate the percentage of three position and display it on textarea
            be = t.positionCalculator(sentenceList).get("beginning").intValue();
            mi = t.positionCalculator(sentenceList).get("middle").intValue();
            en = t.positionCalculator(sentenceList).get("end").intValue();
            sum = be + mi + en;
            if (sum == 0)
                position = position + "Beginning : " + be + "%\n" + "Middle : " + mi + "%\n" + "End : " + en + "%\n\n";
            else
                position = position + "Beginning : " + be * 100 / sum + "%\n" + "Middle : " + mi * 100 / sum + "%\n" + "End : " + en * 100 / sum + "%\n\n";

            // display the percentage of the category
            String category = "Categories :\n";
            HashMap<String,Integer> cat = t.categoryCalculator(sentenceList);
            int total =0;
            for (String key : cat.keySet()) {
                total = total + cat.get(key).intValue();
            }
            for (String key : cat.keySet()) {
                int value = cat.get(key).intValue();
                category = category + key + " : " + value*100/total + "%\n";
            }

            pnc.setText(position+category);

        }
    }//searchButtonHandler

    /**
     * event listener for listSelectioner
     */
    private class ValueReporter implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()) {
                JList l = (JList) event.getSource();
            }
        }
    }

    /**
     * event listener for button save to file
     */
    private class saveButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int chosen = list_of_choices.getSelectedIndex();
            String type = "";
            if (chosen == 0)
                type = "word";
            else if (chosen == 1)
                type = "lemma";
            else if (chosen == 2)
                type = "pos";
            Tool t = new Tool(enter_key, enter_url);
            try {
                t.xmlGenerator(sentenceList, type);
            } catch (ParserConfigurationException ex) {
                ex.getMessage();
            } catch (IOException ex) {
                ex.getMessage();
            } catch (TransformerException ex) {
                ex.getMessage();
            }

        }
    }

    /**
     * event listener for button
     */
    private class clearButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            enter.setText("Enter a word");
            entering_url.setText("Enter URL");

            result.setText("Search result will be displayed here");
            pnc.setText("category and position infprmation");
            list_of_choices.clearSelection();
        }
    }




    /**
     * open GUI
     */
    public static void main(String[] args) {
        WordSearcher wordSearcher = new WordSearcher();
    }
}