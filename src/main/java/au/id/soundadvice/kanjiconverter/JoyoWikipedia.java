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

import org.apache.commons.csv.CSVRecord;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoyoWikipedia {
    public final int joyoNumber;
    public final String kanji;
    public final String radical;
    public final int strokes;
    public final String grade;
    public final String readings;

    private JoyoWikipedia(int joyoNumber, String kanji, String radical, int strokes, String grade, String readings) {
        this.joyoNumber = joyoNumber;
        this.kanji = kanji;
        this.radical = radical;
        this.strokes = strokes;
        this.grade = grade;
        this.readings = readings;
    }

    public static JoyoWikipedia fromCSV(Map<String, Integer> header, CSVRecord record) {
        return new JoyoWikipedia(
                Integer.parseInt(record.get(header.get("#"))),
                removeFootnotes(record.get(header.get("New"))),
                removeFootnotes(record.get(header.get("Radical"))),
                Integer.parseInt(record.get(header.get("Strokes"))),
                record.get(header.get("Grade")),
                removeFootnotes(record.get(header.get("Readings")))
        );
    }

    private static final Pattern footnote = Pattern.compile("[\\u00A0\\s]*\\[[0-9]+]");

    private static String removeFootnotes(String string) {
        Matcher matcher = footnote.matcher(string);
        if (matcher.find()) {
            StringBuffer result = new StringBuffer();
            do {
                matcher.appendReplacement(result, "");
            } while (matcher.find());
            matcher.appendTail(result);
            return result.toString();
        } else {
            return string;
        }
    }
}
