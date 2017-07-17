package jp.ac.ynu.pl2017.groupj.util;


/**
 * Created by haradakyouhei on 2017/07/10.
 */
public class MATest {
    public static void main(String args[]){
        //技法フラグのインデックス
        int jiAmari = 0, jiTarazu = 1, taigen = 2, kireji = 3;

        MAnalyze ma = new MAnalyze();
        String[] haiku = {"赤い椿", "白い椿と", "落ちにけり"};
        boolean[] flags;
        String nList;

        //解析後の名詞の取得
        nList = ma.mAnalyze(haiku);
        System.out.println(nList);

        //技法フラグの取得
        flags = ma.getSkillFlags();
        if (flags[kireji])
            System.out.println("切れ字");
        if (flags[taigen])
            System.out.println("体言止め");
        if (flags[jiAmari])
            System.out.println("字余り");
        if (flags[jiTarazu])
            System.out.println("字足らず");
    }
}
