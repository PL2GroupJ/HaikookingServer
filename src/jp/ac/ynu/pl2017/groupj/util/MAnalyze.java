package jp.ac.ynu.pl2017.groupj.util;

import java.io.*;
import java.util.*;
import org.chasen.mecab.*;
/**
 * Created by haradakyouhei on 2017/06/29.
 */
public class MAnalyze {

    String[] haiku;
    static {
        try {
            String dir = System.getProperty("user.dir");
            File f = new File(dir + "/lib/libMeCab.so"); // Select libMeCab.so path
            System.load(f.toString());
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Cannot load the example native code.\nMake sure your LD_LIBRARY_PATH contains \'.\'\n" + e);
            System.exit(1);
        }
    }

    public static String[] mAnalyze(String[] h){
        int i, j = 0;
        Tagger tagger = new Tagger();
        String[] nameList = new String[10];

        for(i = 0;i < h.length;i++) {
            /*名詞の抽出*/
            Node nodeSplit = tagger.parseToNode(h[i]);
            for (; nodeSplit != null; nodeSplit = nodeSplit.getNext()) {
                String sf = nodeSplit.getSurface();
                String ft = nodeSplit.getFeature();
                StringTokenizer sta = new StringTokenizer(ft, ",");
                while (sta.hasMoreTokens()) {
                    if (sta.nextToken().equals("名詞") && j < 10) {
                        System.out.println(sf + "を格納します");
                        nameList[j++] = sf;
                    }
                }

            }
        }
        return nameList;
    }
/*使い方*/
    public static void main(String args[]){
        String[] haiku = {"古池や", "蛙飛び込む", "水の音"};
        int i=0;

        do{
            System.out.println(mAnalyze(haiku)[i++]);
        }while (i<4);
    }
}
