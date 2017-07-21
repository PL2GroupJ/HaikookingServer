# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""

import sys
import os
from os import path
import numpy as np
from PIL import Image
import datetime
import matplotlib.pyplot as plt
import time

sys.path.append(os.getcwd() + "/lib/wordcloud")
from wordcloud import WordCloud

text = "初鳩,初花,初針,初春,初日,初日影,初飛行,初披講,初日の出,初雲雀,初富士,初冬,初風呂,初箒,初蛍,初参り,初松風,初神籤,初彌撒,春小袖," \
       "春炬燵,春寒,春雨,春芝居,春近し,春隣,春の馬,春の海,春の蚊,春の風,春の川,春の草,春の雲,春の暮,春の蟬,春の空,春の田,春の月,春の虹," \
       "春の野,春の日,春の服,春,2,3,春の星,はるのほし,春,2,3,春の水,はるのみず,春,2,3,春の山,はるのやま,春,2,3,春の闇,はるのやみ,春," \
       "2,3,春の雪,はるのゆき,春,2,3,春の夜,はるのよ,春,2,春の宵,はるのよい,2,3,春日傘,はるひがさ,春,2,3,春深し,はるふかし,春,2,3,春待つ," \
       "はるまつ,冬,2,春祭,はるまつり,春,2,春めく,はるめく,春,2,春休み,はるやすみ,春,2,春夕焼,はるゆうやけ,春,2,バレンタインデー," \
       "ばれんたいんでー,春,晩夏,ばんか,夏,晩菊,ばんぎく,秋,万愚節,ばんぐせつ,春,半夏,はんげ,夏,半夏生,はんげしょう,夏,パンジー,ぱんじー," \
       "春,晩秋,ばんしゅう,秋,晩春,ばんしゅん,春,半仙戯,はんせんぎ,春,晩霜,ばんそう,春,斑猫,はんみょう,夏,晩涼,ばんりょう,夏,万緑," \
       "ばんりょく,夏,日脚伸ぶ,ひあしのぶ,ビーチパラソル,びーちぱらそる,夏,ひひな,ひいな,春,柊,ひいらぎ,冬,麦酒,びーる,夏".encode('utf-8')
d = path.dirname(__file__)

start_day = datetime.date.today()

logo_mask = np.array(Image.open(
    path.join(d, os.getcwd() + "/python/mask/logo_mask.png")))
spring_mask = np.array(Image.open(
    path.join(d, os.getcwd() + "/python/mask/spring-mask.png")))
newyear_mask = np.array(Image.open(
    path.join(d, os.getcwd() + "/python/mask/newyear-mask.png")))
summer_mask = np.array(Image.open(
    path.join(d, os.getcwd() + "/python/mask/summer-mask.png")))
autumn_mask = np.array(Image.open(
    path.join(d, os.getcwd() + "/python/mask/autumn-mask.png")))
winter_mask = np.array(Image.open(
    path.join(d, os.getcwd() + "/python/mask/winter-mask.png")))

total_f = open("./python/text/total_wordcloud.txt")
total_text = total_f.read()

weekly_f = open("./python/text/weekly_wordcloud.txt")
weekly_text = weekly_f.read()

monthly_f = open("./python/text/monthly_wordcloud.txt")
monthly_text = monthly_f.read()

newyear_f = open("./python/text/newyear_wordcloud.txt")
newyear_text = newyear_f.read()

spring_f = open("./python/text/spring_wordcloud.txt")
spring_text = spring_f.read()

summer_f = open("./python/text/summer_wordcloud.txt")
summer_text = summer_f.read()

autumn_f = open("./python/text/autumn_wordcloud.txt")
autumn_text = autumn_f.read()

winter_f = open("./python/text/winter_wordcloud.txt")
winter_text = winter_f.read()

while True:
    today = datetime.date.today()
    wordcloud = WordCloud(background_color="lightcyan", mode="RGB", font_path="./ヒラギノ角ゴシック W5.ttc",
                          mask=logo_mask, width=600, height=600, colormap="gist_rainbow").generate(total_text)
    wordcloud.to_file("./image/total_wordcloud.png")
    print("total_wordcloud was generated.")

    wordcloud = WordCloud(background_color="lightcyan", mode="RGB", font_path="./ヒラギノ角ゴシック W5.ttc",
                          mask=logo_mask, width=600, height=600, colormap="gist_rainbow").generate(weekly_text)
    wordcloud.to_file("./image/weekly_wordcloud.png")
    print("weekly_wordcloud was generated.")

    wordcloud = WordCloud(background_color="lightcyan", mode="RGB", font_path="./ヒラギノ角ゴシック W5.ttc",
                          mask=logo_mask, width=600, height=600, colormap="gist_rainbow").generate(monthly_text)
    wordcloud.to_file("./image/monthly_wordcloud.png")
    print("monthly_wordcloud was generated.")

    wordcloud = WordCloud(background_color="moccasin", font_path="./ヒラギノ角ゴシック W5.ttc",
                          mask=newyear_mask, width=600, height=600, colormap="Reds").generate(newyear_text)
    wordcloud.to_file("./image/newyear_wordcloud.png")
    print("newyear_wordcloud was generated.")

    wordcloud = WordCloud(background_color="palegreen", font_path="./ヒラギノ角ゴシック W5.ttc",
                          mask=spring_mask, width=600, height=600, colormap="spring").generate(spring_text)
    wordcloud.to_file("./image/spring_wordcloud.png")
    print("spring_wordcloud was generated.")

    wordcloud = WordCloud(background_color="paleturquoise", font_path="./ヒラギノ角ゴシック W5.ttc",
                          mask=summer_mask, width=600, height=600, colormap="summer").generate(summer_text)
    wordcloud.to_file("./image/summer_wordcloud.png")
    print("summer_wordcloud was generated.")

    wordcloud = WordCloud(background_color="darkslategray", font_path="./ヒラギノ角ゴシック W5.ttc",
                          mask=autumn_mask, width=600, height=600, colormap="autumn").generate(autumn_text)
    wordcloud.to_file("./image/autumn_wordcloud.png")
    print("autumn_wordcloud was generated.")

    wordcloud = WordCloud(background_color="midnightblue", font_path="./ヒラギノ角ゴシック W5.ttc",
                          mask=winter_mask, width=600, height=600, colormap="PuBuGn").generate(winter_text)
    wordcloud.to_file("./image/winter_wordcloud.png")
    print("winter_wordcloud was generated.")

    # time.sleep(86400)
