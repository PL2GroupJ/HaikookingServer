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
    weekly_mask = np.array(Image.open("mask/weekly-mask.png"))
    monthly_mask = np.array(Image.open("mask/monthly-mask.png"))
    spring_mask = np.array(Image.open("mask/spring-mask.png"))
    newyear_mask = np.array(Image.open("mask/newyear-mask.png"))
    summer_mask = np.array(Image.open("mask/summer-mask.png"))
    autumn_mask = np.array(Image.open("mask/autumn-mask.png"))
    winter_mask = np.array(Image.open("mask/winter-mask.png"))

    # Access.Flagの順番で受け取る
    textList = input().split(":*:")

    wordcloud = WordCloud(background_color="lightcyan", mode="RGB", font_path="meiryo.ttc",
                          mask=logo_mask, width=1000, height=1000, colormap="gist_rainbow").generate(textList[0])
    wordcloud.to_file("image/total_wordcloud.png")

    wordcloud = WordCloud(background_color="lightcyan", mode="RGB", font_path="meiryo.ttc",
                          mask=weekly_mask, width=1000, height=1000, colormap="gist_rainbow").generate(textList[1])
    wordcloud.to_file("image/weekly_wordcloud.png")

    wordcloud = WordCloud(background_color="midnightblue", mode="RGB", font_path="meiryo.ttc",
                          mask=monthly_mask, width=1000, height=1000, colormap="Wistia").generate(textList[2])
    wordcloud.to_file("image/monthly_wordcloud.png")

    wordcloud = WordCloud(background_color="palegreen", font_path="meiryo.ttc",
                          mask=spring_mask, width=1000, height=1000, colormap="spring").generate(textList[3])
    wordcloud.to_file("image/spring_wordcloud.png")

    wordcloud = WordCloud(background_color="paleturquoise", font_path="meiryo.ttc",
                          mask=summer_mask, width=1000, height=1000, colormap="summer").generate(textList[4])
    wordcloud.to_file("image/summer_wordcloud.png")

    wordcloud = WordCloud(background_color="darkslategray", font_path="meiryo.ttc",
                          mask=autumn_mask, width=1000, height=1000, colormap="autumn").generate(textList[5])
    wordcloud.to_file("image/autumn_wordcloud.png")

    wordcloud = WordCloud(background_color="midnightblue", font_path="meiryo.ttc",
                          mask=winter_mask, width=1000, height=1000, colormap="PuBuGn").generate(textList[6])
    wordcloud.to_file("image/winter_wordcloud.png")

    wordcloud = WordCloud(background_color="moccasin", font_path="meiryo.ttc",
                          mask=newyear_mask, width=1000, height=1000, colormap="Reds").generate(textList[7])
    wordcloud.to_file("image/newyear_wordcloud.png")

    print("wordcloud was generated.")