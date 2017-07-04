package jp.ac.ynu.pl2017.groupj.util;

import java.io.*;
import java.util.*;
import org.chasen.mecab.*;
/**
 * Created by haradakyouhei on 2017/06/29.
 */
public class MAnalyze {

    //String[] haiku;
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

    public static String mAnalyze(String[] h){
        int i, j = 0;
        Tagger tagger = new Tagger();
        String nameList = new String();

        Node[] nodeSplit = new Node[4];
        for (i = 0;i < h.length;i++) {
            nodeSplit[i] = tagger.parseToNode(h[i]);
            for (; nodeSplit[i] != null; nodeSplit[i] = nodeSplit[i].getNext()) {
                String sf = nodeSplit[i].getSurface();
                String ft = nodeSplit[i].getFeature();
                StringTokenizer sta = new StringTokenizer(ft, ",");
                //トークンの出力
                while (sta.hasMoreTokens()) {
                    if (sta.nextToken().equals("名詞")) {
                        System.out.println(sf + "\t" + ft);
                        nameList = nameList + sf + ",";
                    }
                }

            }
        }

        return nameList;
    }

    /*使い方*/
    public static void main(String args[]){
        String[] haiku = {"", "古池や", "蛙飛び込む", "水の音"};
        String nList;
        int i=0;

        nList = mAnalyze(haiku);
        System.out.println(nList);
    }
}
