package jp.ac.ynu.pl2017.groupj.util;

import net.moraleboost.mecab.Lattice;
import net.moraleboost.mecab.Node;
import net.moraleboost.mecab.impl.StandardTagger;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by shiita on 2017/07/16.
 */
public class MAnalyze2 {
    public static void main(String[] args) {
        StandardTagger tagger = new StandardTagger("", Charset.defaultCharset());
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
//        Node node = lattice.bosNode();
//        while (node != null) {
//            String surface = node.surface();
//            String feature = node.feature();
//            System.out.println(surface + "\t" + feature);
//            node = node.next();
//        }

        // lattice, taggerを破壊
        lattice.destroy();
        tagger.destroy();
    }
}
