package jp.ac.ynu.pl2017.groupj.util;

/**
 * Created by haradakyouhei on 2017/07/10.
 */
public class MATest {
    public static void main(String args[]){
        MAnalyze ma = new MAnalyze();
        String[] haiku = {"", "古池や", "蛙飛び込む", "水の音"};
        boolean[] flags;
        String nList;

        nList = ma.mAnalyze(haiku);
        System.out.println(nList);

        flags = ma.getSkillFlags();
    }
}
