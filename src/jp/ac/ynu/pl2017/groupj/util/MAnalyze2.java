package jp.ac.ynu.pl2017.groupj.util;

import net.moraleboost.mecab.Lattice;
import net.moraleboost.mecab.Node;
import net.moraleboost.mecab.impl.StandardTagger;
//import org.chasen.mecab.Tagger;

import java.io.File;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

/**
 * Created by shiita on 2017/07/16.
 */
public class MAnalyze2 {

    int jiAmari = 0, jiTarazu = 1, taigen = 2, kireji = 3;
    // 字余り、字足らず、体言止め、切れ字のフラグ列
    boolean[] skillFlags = {false, false, false, false};

    public  String mAnalyze(String[] h){
        int i;
        StandardTagger tagger = new StandardTagger("");
        Lattice lattice = tagger.createLattice();
        String nameList = "";
        Node nodeSplit;
        String sf2 = "-1";

        for (i = 0;i < h.length;i++) {
            //切れ字の判定その１
            if(h[i].contains("かな") || h[i].contains("けり")){
                //System.out.println("切れ字");
                setFlags(kireji);
            }

            lattice.setSentence(h[i]);
            //解析
            tagger.parse(lattice);
            //System.out.println(lattice.toString());

            nodeSplit = lattice.bosNode();
            while (nodeSplit != null){
                String sf = nodeSplit.surface();
                String ft = nodeSplit.feature();
                StringTokenizer sta = new StringTokenizer(ft, ",");

                while (sta.hasMoreTokens()) {
                    if (sta.nextToken().equals("名詞")) {
                        sf2 = sf;
                        System.out.println(sf + "\t" + ft);

                        if (!(nameList.contains(sf))) {
                            //区切り文字":*:"
                            nameList = nameList + sf + ":*:";
                        }
                    }
                }
                //切れ字の判定その２
                if (i == 0 && sf.equals("や")){
                    //System.out.println(sf +"...切れ字");
                    setFlags(kireji);
                }
                nodeSplit = nodeSplit.next();
            }
            //体言止めの判定
            if (h[i].endsWith(sf2) ) {
                setFlags(taigen);
                //System.out.println("体言止め");
            }
        }

        //音数の評価
        soundAnalyze(h);
        //区切り文字付きの名詞で返す
        return nameList;
    }

    void soundAnalyze(String[] h){
        int i;
        StandardTagger tagger = new StandardTagger("");
        String kanaLeng = "";
        Node node;
        Lattice lattice = tagger.createLattice();

        for (i = 0;i < h.length;i++) {
            String kana = "";
            lattice.setSentence(h[i]);
            tagger.parse(lattice);
            node = lattice.bosNode();
            while (node != null){
                //音数にならない文字を削除
                kana = node.feature().split(",")[8];
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
                //System.out.println(kana);

                kanaLeng = kanaLeng + kana;
                kanaLeng = kanaLeng.replace("*","");
                node = node.next();
            }
            System.out.println(kanaLeng + "\t" + kanaLeng.length());
            if ((i == 0 || i == 2 ) && kanaLeng.length() != 5){
                if (kanaLeng.length() < 5){
                    setFlags(jiTarazu);
                }else {
                    setFlags(jiAmari);
                }
            }else if ((i == 1) && kanaLeng.length() != 7){
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



    /*public static void main(String[] args) {
        StandardTagger tagger = new StandardTagger("");
        System.out.println("MeCab version " + tagger.version());

        // Lattice（形態素解析に必要な実行時情報が格納されるオブジェクト）を構築
        Lattice lattice = tagger.createLattice();

        // 解析対象文字列をセット
        String text = "本日は晴天なり。";
        lattice.setSentence(text);

        // tagger.parse()を呼び出して、文字列を形態素解析する。
        tagger.parse(lattice);

        // 形態素解析結果を出力
        System.out.println(lattice.toString());

        // 一つずつ形態素をたどりながら、表層形と素性を出力
        Node node = lattice.bosNode();
        while (node != null) {
            String surface = node.surface();
            String feature = node.feature();
            System.out.println(surface + "\t" + feature);
            node = node.next();
        }

        // lattice, taggerを破壊
        lattice.destroy();
        tagger.destroy();
    }*/
}
