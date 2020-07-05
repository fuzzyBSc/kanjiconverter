/*
 * Copyright (c) 2020.
 *
 *     This file is part of kanjiconverter.
 *
 *     kanjiconverter is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     kanjiconverter is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with kanjiconverter.  If not, see <https://www.gnu.org/licenses/>.
 */

package au.id.soundadvice.kanjiconverter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoreModel {
    public final int joyoNumber;
    public final String kanji;
    public final String radical;
    public final int strokes;
    public final String grade;
    public final List<String> onyomi;
    public final List<String> kunyomi;

    private CoreModel(int joyoNumber, String kanji, String radical, int strokes, String grade, List<String> onyomi, List<String> kunyomi) {
        this.joyoNumber = joyoNumber;
        this.kanji = kanji;
        this.radical = radical;
        this.strokes = strokes;
        this.grade = grade;
        this.onyomi = onyomi;
        this.kunyomi = kunyomi;
    }

    public static CoreModel fromWikipedia(JoyoWikipedia wikipedia) {
        List<String> onyomi = extractReading(wikipedia.readings, Character.UnicodeBlock.KATAKANA)
                .collect(Collectors.toList());
        List<String> kunyomi = extractReading(wikipedia.readings, Character.UnicodeBlock.HIRAGANA)
                .collect(Collectors.toList());
        return new CoreModel(
                wikipedia.joyoNumber,
                wikipedia.kanji,
                wikipedia.radical,
                wikipedia.strokes,
                wikipedia.grade,
                onyomi, kunyomi
        );
    }

    private static final Pattern separator = Pattern.compile("[,，、 \\u00A0\\s]+");

    private static Stream<String> extractReading(String readings, Character.UnicodeBlock match) {
        return separator.splitAsStream(readings)
                .filter(it -> contains(it, match))
                .map(CoreModel::removeOkurigana)
                .distinct();
    }

    private static String removeOkurigana(String string) {
        int dashPos = string.indexOf('-');
        if (dashPos >= 0) {
            return string.substring(0, dashPos);
        } else {
            return string;
        }
    }

    private static boolean contains(String string, Character.UnicodeBlock match) {
        for (int ii = 0; ii < string.length(); ++ii) {
            char cc = string.charAt(ii);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(cc);
            if (block == match) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getCSVHeader() {
        return Arrays.asList("#", "Kanji", "Onyomi", "Kunyomi", "Tags");
    }

    public List<String> toCSVRow() {
        Map<String, String> tags = new TreeMap<>();
        tags.put("grade", grade);
        tags.put("radical", radical);
        tags.put("strokes", Integer.toString(strokes));
        return Arrays.asList(
                Integer.toString(joyoNumber),
                kanji,
                readingToString(onyomi),
                readingToString(kunyomi),
                tags.entrySet().stream()
                        .map(it -> it.getKey() + ":" + it.getValue())
                        .collect(Collectors.joining(" ")));
    }

    private String readingToString(List<String> reading) {
        return String.join(", ", reading);
    }
}
