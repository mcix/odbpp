package com.odbpp.parser;

import com.odbpp.model.StandardFont;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class StandardFontParser {
    public StandardFont parse(Path fontFile) throws IOException {
        StandardFont font = new StandardFont();
        font.setCharacters(new ArrayList<>());

        try (BufferedReader reader = Files.newBufferedReader(fontFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("XSIZE")) {
                    font.setXSize(Double.parseDouble(line.split(" ")[1]));
                } else if (line.startsWith("YSIZE")) {
                    font.setYSize(Double.parseDouble(line.split(" ")[1]));
                } else if (line.startsWith("OFFSET")) {
                    font.setOffset(Double.parseDouble(line.split(" ")[1]));
                } else if (line.startsWith("CHAR")) {
                    font.getCharacters().add(parseCharBlock(reader, line.split(" ")[1].charAt(0)));
                }
            }
        }
        return font;
    }

    private StandardFont.CharacterDefinition parseCharBlock(BufferedReader reader, char c) throws IOException {
        StandardFont.CharacterDefinition charDef = new StandardFont.CharacterDefinition();
        charDef.setCharacter(c);
        charDef.setLines(new ArrayList<>());
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.equals("ECHAR")) {
                break;
            }
            if (line.startsWith("LINE")) {
                charDef.getLines().add(parseLine(line));
            }
        }
        return charDef;
    }

    private StandardFont.LineDefinition parseLine(String line) {
        String[] parts = line.split(" ");
        StandardFont.LineDefinition lineDef = new StandardFont.LineDefinition();
        lineDef.setXs(Double.parseDouble(parts[1]));
        lineDef.setYs(Double.parseDouble(parts[2]));
        lineDef.setXe(Double.parseDouble(parts[3]));
        lineDef.setYe(Double.parseDouble(parts[4]));
        lineDef.setPolarity(parts[5].charAt(0));
        lineDef.setShape(parts[6].charAt(0));
        lineDef.setWidth(Double.parseDouble(parts[7]));
        return lineDef;
    }
}
