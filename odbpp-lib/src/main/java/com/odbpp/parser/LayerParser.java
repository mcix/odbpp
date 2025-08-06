package com.odbpp.parser;

import com.odbpp.model.Layer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LayerParser {
    private final AttrListParser attrListParser = new AttrListParser();
    private final FeaturesFileParser featuresFileParser = new FeaturesFileParser();
    private final ComponentsParser componentsParser = new ComponentsParser();
    private final ProfileParser profileParser = new ProfileParser();

    public Layer parse(Path layerDir) throws IOException {
        Layer layer = new Layer();
        layer.setName(layerDir.getFileName().toString());

        Path attrlistFile = layerDir.resolve("attrlist");
        if (Files.exists(attrlistFile)) {
            layer.setAttrList(attrListParser.parse(attrlistFile));
        }

        Path featuresFile = layerDir.resolve("features");
        if (Files.exists(featuresFile)) {
            layer.setFeatures(featuresFileParser.parse(featuresFile));
        }

        Path componentsFile = layerDir.resolve("components");
        if (Files.exists(componentsFile)) {
            layer.setComponents(componentsParser.parse(componentsFile));
        }

        Path profileFile = layerDir.resolve("profile");
        if (Files.exists(profileFile)) {
            layer.setProfile(profileParser.parse(profileFile));
        }
        
        // TODO: dimensions, notes, tools
        return layer;
    }
}
