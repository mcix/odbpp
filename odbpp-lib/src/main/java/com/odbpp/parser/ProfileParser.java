package com.odbpp.parser;

import com.odbpp.model.Profile;
import com.odbpp.model.Surface;
import com.odbpp.model.Polarity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileParser {
    private static final Pattern SURFACE_PATTERN = Pattern.compile(
            "^S\\s+(P|N)\\s+(\\d+)(?:;(\\d+)=(.*?))?(?:;ID=(.*?))?\\s*$");
    
    private final SurfaceParser surfaceParser = new SurfaceParser();

    public Profile parse(Path profileFile) throws IOException {
        Profile profile = new Profile();
        List<String> lines = Files.readAllLines(profileFile, StandardCharsets.ISO_8859_1);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("S")) {
                Matcher matcher = SURFACE_PATTERN.matcher(line);
                if (matcher.find()) {
                    Surface surface = new Surface();
                    
                    // Parse polarity
                    String polarityStr = matcher.group(1);
                    surface.setPolarity(Polarity.fromString(polarityStr));
                    
                    // Parse dcode
                    int dcode = Integer.parseInt(matcher.group(2));
                    surface.setDcode(dcode);
                    
                    // Parse attributes if present
                    if (matcher.group(3) != null && matcher.group(4) != null) {
                        int attrNumber = Integer.parseInt(matcher.group(3));
                        String attrValue = matcher.group(4);
                        surface.getAttributes().put(attrNumber, attrValue);
                    }
                    
                    // Parse unique ID if present
                    if (matcher.group(5) != null) {
                        surface.setUniqueId(matcher.group(5));
                    }
                    
                    profile.getSurfaces().add(surface);
                    surfaceParser.parse(lines, i, surface);
                }
            }
        }
        return profile;
    }
}