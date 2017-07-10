package jp.ac.ynu.pl2017.groupj.util;

/**
 * Created by haradakyouhei on 2017/07/10.
 */
public class MATest {
    public static void main(String args[]){
        int kigo = 0, nonKigo = 1, jiAmari = 2, jiTarazu = 3, taigen = 4, kireji = 5;


        MAnalyze ma = new MAnalyze();
        String[] haiku = {"", "アマガエル", "芭蕉に乗りて", "そよぎけり"};
        boolean[] flags;
        String nList;

        nList = ma.mAnalyze(haiku);
        System.out.println(nList);

        flags = ma.getSkillFlags();
        if(flags[kireji])
            System.out.println("切れ字");
        if (flags[taigen])
            System.out.println("体言止め");
    }
}
