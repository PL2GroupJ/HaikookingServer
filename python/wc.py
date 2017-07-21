# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""

import numpy as np
from PIL import Image
from wordcloud import WordCloud

if __name__ == '__main__':
    logo_mask = np.array(Image.open("mask/logo_mask.png"))
    spring_mask = np.array(Image.open("mask/spring-mask.png"))
    newyear_mask = np.array(Image.open("mask/newyear-mask.png"))
    summer_mask = np.array(Image.open("mask/summer-mask.png"))
    autumn_mask = np.array(Image.open("mask/autumn-mask.png"))
    winter_mask = np.array(Image.open("mask/winter-mask.png"))

    total_f = open("text/total_wordcloud.txt", encoding="utf-8")
    total_text = total_f.read()

    weekly_f = open("text/weekly_wordcloud.txt", encoding="utf-8")
    weekly_text = weekly_f.read()

    monthly_f = open("text/monthly_wordcloud.txt", encoding="utf-8")
    monthly_text = monthly_f.read()

    newyear_f = open("text/newyear_wordcloud.txt", encoding="utf-8")
    newyear_text = newyear_f.read()

    spring_f = open("text/spring_wordcloud.txt", encoding="utf-8")
    spring_text = spring_f.read()

    summer_f = open("text/summer_wordcloud.txt", encoding="utf-8")
    summer_text = summer_f.read()

    autumn_f = open("text/autumn_wordcloud.txt", encoding="utf-8")
    autumn_text = autumn_f.read()

    winter_f = open("text/winter_wordcloud.txt", encoding="utf-8")
    winter_text = winter_f.read()

    # today = datetime.date.today()
    wordcloud = WordCloud(background_color="lightcyan", mode="RGB", font_path="meiryo.ttc",
                          mask=logo_mask, width=600, height=600, colormap="gist_rainbow").generate(total_text)
    wordcloud.to_file("../image/total_wordcloud.png")
    print("total_wordcloud was generated.")

    wordcloud = WordCloud(background_color="lightcyan", mode="RGB", font_path="meiryo.ttc",
                          mask=logo_mask, width=600, height=600, colormap="gist_rainbow").generate(weekly_text)
    wordcloud.to_file("../image/weekly_wordcloud.png")
    print("weekly_wordcloud was generated.")

    wordcloud = WordCloud(background_color="lightcyan", mode="RGB", font_path="meiryo.ttc",
                          mask=logo_mask, width=600, height=600, colormap="gist_rainbow").generate(monthly_text)
    wordcloud.to_file("../image/monthly_wordcloud.png")
    print("monthly_wordcloud was generated.")

    wordcloud = WordCloud(background_color="moccasin", font_path="meiryo.ttc",
                          mask=newyear_mask, width=600, height=600, colormap="Reds").generate(newyear_text)
    wordcloud.to_file("../image/newyear_wordcloud.png")
    print("newyear_wordcloud was generated.")

    wordcloud = WordCloud(background_color="palegreen", font_path="meiryo.ttc",
                          mask=spring_mask, width=600, height=600, colormap="spring").generate(spring_text)
    wordcloud.to_file("../image/spring_wordcloud.png")
    print("spring_wordcloud was generated.")

    wordcloud = WordCloud(background_color="paleturquoise", font_path="meiryo.ttc",
                          mask=summer_mask, width=600, height=600, colormap="summer").generate(summer_text)
    wordcloud.to_file("../image/summer_wordcloud.png")
    print("summer_wordcloud was generated.")

    wordcloud = WordCloud(background_color="darkslategray", font_path="meiryo.ttc",
                          mask=autumn_mask, width=600, height=600, colormap="autumn").generate(autumn_text)
    wordcloud.to_file("../image/autumn_wordcloud.png")
    print("autumn_wordcloud was generated.")

    wordcloud = WordCloud(background_color="midnightblue", font_path="meiryo.ttc",
                          mask=winter_mask, width=600, height=600, colormap="PuBuGn").generate(winter_text)
    wordcloud.to_file("../image/winter_wordcloud.png")
    print("winter_wordcloud was generated.")