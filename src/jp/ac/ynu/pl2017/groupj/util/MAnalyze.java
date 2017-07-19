package jp.ac.ynu.pl2017.groupj.util;

import net.moraleboost.mecab.Lattice;
import net.moraleboost.mecab.Node;
import net.moraleboost.mecab.impl.StandardTagger;

import java.util.StringTokenizer;

/**
 * Created by shiita on 2017/07/16.
 */
public class MAnalyze {

    int jiAmari = 0, jiTarazu = 1, taigen = 2, kireji = 3;
    // 字余り、字足らず、体言止め、切れ字のフラグ列
    boolean[] skillFlags = {false, false, false, false};
    String[] advice = {
            "　字余り・字たらずは、言葉のとおり、定型の5・7・5の17字より多かったり、少なかったりすることです。" +
                    "それにより得られる効果は、目立つことです。リズムを壊すことで、少し異端な印象を与えることができます。" +
                    "それにより、感動をも際立たせるのです。",

            "　俳句の下五音を名詞や代名詞の体言で締め括ることを「体言止め」と言います。" +
                    "体言が俳句の最後にあることで、それ以降の動詞や形容詞などの用言が省略されています。" +
                    "そのため、省略された部分を想像することになり、情趣を生み出します。",

            "　切れ字というのは、強く言い切る働きをする語で、切れを生み出すのに使われます。" +
                "現代の俳句では、「や」「かな」「けり」の三つの切れ字が使われています。"  +
                "いかに良い切れを作り出すかが、俳句作りの醍醐味で、これが作品の善し悪しを決めます。"};

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

        }
        //体言止めの判定
        if (h[2].endsWith(sf2) && !(h[2].endsWith("かな"))) {
            setFlags(taigen);
            //System.out.println("体言止め");
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

        try {
            for (i = 0; i < h.length; i++) {
                String kana = "";
                lattice.setSentence(h[i]);
                tagger.parse(lattice);
                node = lattice.bosNode();
                while (node != null) {
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
                    kanaLeng = kanaLeng.replace("*", "");
                    node = node.next();
                }
                System.out.println(kanaLeng + "\t" + kanaLeng.length());
                if ((i == 0 || i == 2) && kanaLeng.length() != 5) {
                    if (kanaLeng.length() < 5) {
                        setFlags(jiTarazu);
                    } else {
                        setFlags(jiAmari);
                    }
                } else if ((i == 1) && kanaLeng.length() != 7) {
                    if (kanaLeng.length() < 7) {
                        setFlags(jiTarazu);
                    } else {
                        setFlags(jiAmari);
                    }
                }
                kanaLeng = "";
            }
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("ふりがなの振られない文字がありました");
        }

    }

    void setFlags(int para){
        skillFlags[para] = true;
    }

    public boolean[] getSkillFlags(){
        return skillFlags;
    }

    public String[] getAdvice() { return advice; }
}
