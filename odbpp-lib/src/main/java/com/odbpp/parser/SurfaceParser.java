package com.odbpp.parser;

import com.odbpp.model.ContourPolygon;
import com.odbpp.model.Surface;

import java.io.IOException;
import java.util.List;

public class SurfaceParser {
    public void parse(List<String> lines, int i, Surface surface) throws IOException {
        i++; // Move to the next line, which should be the start of the first polygon
        while (i < lines.size() && !lines.get(i).startsWith("SE")) {
            String line = lines.get(i);
            
            if (line.startsWith("OB")) {
                // Start of a new polygon
                ContourPolygon polygon = parsePolygon(lines, i);
                surface.getPolygons().add(polygon);
                i = findPolygonEnd(lines, i);
            }
            i++;
        }
    }
    
    private ContourPolygon parsePolygon(List<String> lines, int startIndex) throws IOException {
        String obLine = lines.get(startIndex);
        String[] parts = obLine.split("\\s+");
        
        if (parts.length < 4) {
            throw new IOException("Invalid OB line format: " + obLine);
        }
        
        ContourPolygon polygon = new ContourPolygon();
        polygon.setXStart(Double.parseDouble(parts[1]));
        polygon.setYStart(Double.parseDouble(parts[2]));
        polygon.setType(ContourPolygon.Type.fromString(parts[3]));
        
        // Parse polygon parts (OS and OC commands)
        int i = startIndex + 1;
        while (i < lines.size() && !lines.get(i).startsWith("OE")) {
            String line = lines.get(i);
            
            if (line.startsWith("OS")) {
                // Segment command
                ContourPolygon.PolygonPart segment = parseSegment(line);
                polygon.getPolygonParts().add(segment);
            } else if (line.startsWith("OC")) {
                // Arc command
                ContourPolygon.PolygonPart arc = parseArc(line);
                polygon.getPolygonParts().add(arc);
            }
            i++;
        }
        
        return polygon;
    }
    
    private ContourPolygon.PolygonPart parseSegment(String line) throws IOException {
        String[] parts = line.split("\\s+");
        
        if (parts.length < 3) {
            throw new IOException("Invalid OS line format: " + line);
        }
        
        ContourPolygon.PolygonPart segment = new ContourPolygon.PolygonPart();
        segment.setType(ContourPolygon.PolygonPart.Type.SEGMENT);
        segment.setEndX(Double.parseDouble(parts[1]));
        segment.setEndY(Double.parseDouble(parts[2]));
        
        return segment;
    }
    
    private ContourPolygon.PolygonPart parseArc(String line) throws IOException {
        String[] parts = line.split("\\s+");
        
        if (parts.length < 6) {
            throw new IOException("Invalid OC line format: " + line);
        }
        
        ContourPolygon.PolygonPart arc = new ContourPolygon.PolygonPart();
        arc.setType(ContourPolygon.PolygonPart.Type.ARC);
        arc.setEndX(Double.parseDouble(parts[1]));
        arc.setEndY(Double.parseDouble(parts[2]));
        arc.setXCenter(Double.parseDouble(parts[3]));
        arc.setYCenter(Double.parseDouble(parts[4]));
        arc.setClockwise("Y".equalsIgnoreCase(parts[5]) || "y".equals(parts[5]));
        
        return arc;
    }
    
    private int findPolygonEnd(List<String> lines, int startIndex) {
        int i = startIndex;
        while (i < lines.size() && !lines.get(i).startsWith("OE")) {
            i++;
        }
        return i;
    }
}

