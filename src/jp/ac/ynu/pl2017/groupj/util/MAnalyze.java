package jp.ac.ynu.pl2017.groupj.util;

import java.io.*;
import java.util.*;
import org.chasen.mecab.*;
/**
 * Created by haradakyouhei on 2017/06/29.
 */
public class MAnalyze {
    int jiAmari = 0, jiTarazu = 1, taigen = 2, kireji = 3;
    boolean[] skillFlags = {false, false, false, false};
    /*kigo, nonKigo, jiAmari, jiTarazu, taigen, kireJi*/

    static {
        try {
            String dir = System.getProperty("user.dir");
            File f = new File(dir + "/res/libMeCab.so"); // Select libMeCab.so path
            System.load(f.toString());
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Cannot load the example native code.\nMake sure your LD_LIBRARY_PATH contains \'.\'\n" + e);
            System.exit(1);
        }
    }

    public  String mAnalyze(String[] h){
        int i;
        Tagger tagger = new Tagger();
        String nameList = "";
        Node[] nodeSplit = new Node[4];
        String sf2 = "-1";

        for (i = 0;i < h.length;i++) {
            if(h[i].contains("かな") || h[i].contains("けり")){
                System.out.println("切れ字");
                setFlags(kireji);
            }

            nodeSplit[i] = tagger.parseToNode(h[i]);
            for (; nodeSplit[i] != null; nodeSplit[i] = nodeSplit[i].getNext()) {
                String sf = nodeSplit[i].getSurface();
                String ft = nodeSplit[i].getFeature();
                StringTokenizer sta = new StringTokenizer(ft, ",");

                while (sta.hasMoreTokens()) {
                    if (sta.nextToken().equals("名詞")) {
                        sf2 = sf;
                        System.out.println(sf + "\t" + ft);
                        nameList = nameList + sf + ":*:";
                    }
                }
                if (i == 1 && sf.equals("や")){
                    System.out.println(sf +"...切れ字");
                    setFlags(kireji);
                }
            }
            if (i > 0 && h[i].endsWith(sf2) ) {
                setFlags(taigen);
                System.out.println("体言止め");
            }
        }
        nameList = nameList.substring(0, nameList.length()-3);
        soundAnalyze(h);
        return nameList;
    }

    void soundAnalyze(String[] h){
        int i;
        Tagger tagger = new Tagger();
        String kanaLeng = "";
        Node[] node = new Node[4];

        for (i = 0;i < h.length;i++) {
            String kana = "";
            node[i] = tagger.parseToNode(h[i]);
            for (; node[i] != null; node[i] = node[i].getNext()) {
                if (i > 0) {
                    //String sf = node[i].getSurface();
                    kana = node[i].getFeature().split(",")[8];
                    kana = kana.replace("ゃ", "");
                    kana = kana.replace("ゅ", "");
                    kana = kana.replace("ょ", "");
                    kana = kana.replace("ャ", "");
                    kana = kana.replace("ュ", "");
                    kana = kana.replace("ョ", "");
                    kana = kana.replace("ぁ", "");
                    kana = kana.replace("ぃ", "");
                    kana = kana.replace("ぅ", "");
                    kana = kana.replace("ぇ", "");
                    kana = kana.replace("ぉ", "");
                    kana = kana.replace("ァ", "");
                    kana = kana.replace("ィ", "");
                    kana = kana.replace("ゥ", "");
                    kana = kana.replace("ェ", "");
                    kana = kana.replace("ォ", "");
                    kana = kana.replace("ゎ", "");
                    kana = kana.replace("ヮ", "");
                    System.out.println(kana);
                }
                kanaLeng = kanaLeng + kana;
                kanaLeng = kanaLeng.replace("*","");
            }
            System.out.println(kanaLeng);
            if ((i == 1 || i == 3 ) && kanaLeng.length() != 5){
                if (kanaLeng.length() < 5){
                    setFlags(jiTarazu);
                }else {
                    setFlags(jiAmari);
                }
            }else if ((i == 2) && kanaLeng.length() != 7){
                if (kanaLeng.length() < 7){
                    setFlags(jiTarazu);
                }else {
                    setFlags(jiAmari);
                }
            }
            kanaLeng = "";
        }

    }

    void setFlags(int para){
        skillFlags[para] = true;
    }

    public boolean[] getSkillFlags(){
            return skillFlags;
    }

}
