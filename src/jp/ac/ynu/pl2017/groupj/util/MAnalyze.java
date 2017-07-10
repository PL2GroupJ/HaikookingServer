package jp.ac.ynu.pl2017.groupj.util;

import java.io.*;
import java.util.*;
import org.chasen.mecab.*;
/**
 * Created by haradakyouhei on 2017/06/29.
 */
public class MAnalyze {
    int kigo = 0, nonKigo = 1, jiAmari = 2, jiTarazu = 3, taigen = 4, kireji = 5;
    boolean[] skillFlags = {false, false, false, false, false, false};
    /*kigo, nonKigo, jiAmari, jiTarazu, taigen, kireJi*/

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

    public  String mAnalyze(String[] h){
        int i;
        Tagger tagger = new Tagger();
        String nameList = "";
        String speech = null;
        Node[] nodeSplit = new Node[4];

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
                //トークンの出力
                while (sta.hasMoreTokens()) {
                    speech = sta.nextToken();
                    if (speech.equals("名詞")) {
                        System.out.println(sf + "\t" + ft);
                        nameList = nameList + sf + ",";
                    }
                }
                if (i == 1 && sf.equals("や")){
                    System.out.println(sf +"...切れ字");
                    setFlags(kireji);
                }
            }
            /*
            if (speech.equals("名詞")){
                System.out.println("体言止め");
                setFlags(taigen);
            }
            */
        }
        nameList = nameList.substring(0, nameList.length()-1);
        return nameList;
    }

    void setFlags(int para){
        skillFlags[para] = true;
    }

    public boolean[] getSkillFlags(){
            return skillFlags;
    }

}
