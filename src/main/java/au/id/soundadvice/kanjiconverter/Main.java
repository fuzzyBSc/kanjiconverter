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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class Main {
    private static final CSVFormat format = CSVFormat.DEFAULT
            .withFirstRecordAsHeader();

    public static void main(String[] argv) throws IOException {
        Path input = Paths.get(argv[0]);
        Path output = Paths.get(argv[1]);
        try (
                BufferedReader reader = Files.newBufferedReader(input);
                CSVParser parser = CSVParser.parse(reader, format);
                BufferedWriter writer = Files.newBufferedWriter(output, StandardOpenOption.TRUNCATE_EXISTING);
                CSVPrinter printer = new CSVPrinter(writer, format)
        ) {
            printer.printRecord(CoreModel.getCSVHeader());
            Map<String, Integer> header = parser.getHeaderMap();
            StreamSupport.stream(parser.spliterator(), false)
                    .map(it -> JoyoWikipedia.fromCSV(header, it))
                    .flatMap(CoreModel::fromWikipedia)
                    .sorted()
                    .map(CoreModel::toCSVRow)
                    .forEach(it -> print(printer, it));
        }
    }

    private static void print(CSVPrinter printer, List<String> it) {
        try {
            printer.printRecord(it);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
