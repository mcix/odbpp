package com.odbpp.parser;

import com.odbpp.model.*;
import com.odbpp.XmlParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Stream;

public class OdbParser {

    private final MiscInfoParser miscInfoParser = new MiscInfoParser();
    private final AttrListParser attrListParser = new AttrListParser();
    private final MatrixParser matrixParser = new MatrixParser();
    private final XmlParser xmlParser = new XmlParser();
    private final AttributeDefinitionParser attributeDefinitionParser = new AttributeDefinitionParser();
    private final StandardFontParser fontParser = new StandardFontParser();
    private final FeaturesFileParser featuresFileParser = new FeaturesFileParser();
    private final StackupParser stackupParser = new StackupParser();
    private final DCodeParser dCodeParser = new DCodeParser();
    private final StepParser stepParser = new StepParser();

    public Job parse(Path odbRootPath) throws IOException {
        Job job = new Job();

        // misc directory
        Path miscDir = odbRootPath.resolve("misc");
        if (Files.exists(miscDir)) {
            job.setMiscInfo(miscInfoParser.parse(miscDir.resolve("info")));
            Path attrlistFile = miscDir.resolve("attrlist");
            if(Files.exists(attrlistFile)) {
                job.setProductModelAttributes(attrListParser.parse(attrlistFile));
            }
            Path lastSaveFile = miscDir.resolve("last_save");
            if (Files.exists(lastSaveFile)) {
                try (Stream<String> lines = Files.lines(lastSaveFile)) {
                    job.setLastSave(lines.findFirst().orElse(null));
                }
            }
            Path metadataFile = miscDir.resolve("metadata.xml");
            if (Files.exists(metadataFile)) {
                Metadata metadata = new Metadata();
                metadata.setData(xmlParser.parse(metadataFile));
                job.setMetadata(metadata);
            }

            // Parse sysattr.* and userattr files
            job.setSystemAttributes(new HashMap<>());
            try (Stream<Path> miscFiles = Files.list(miscDir)) {
                miscFiles.filter(p -> p.getFileName().toString().startsWith("sysattr"))
                         .forEach(p -> {
                             try {
                                 job.getSystemAttributes().putAll(attributeDefinitionParser.parse(p));
                             } catch (IOException e) {
                                 // Handle exception
                             }
                         });
            }
            Path userAttrFile = miscDir.resolve("userattr");
            if (Files.exists(userAttrFile)) {
                job.setUserAttributes(attributeDefinitionParser.parse(userAttrFile));
            }
        }

        // matrix directory
        Path matrixDir = odbRootPath.resolve("matrix");
        if (Files.exists(matrixDir)) {
            job.setMatrix(matrixParser.parse(matrixDir.resolve("matrix")));
            Path stackupFile = matrixDir.resolve("stackup.xml");
            if (Files.exists(stackupFile)) {
                job.setStackup(stackupParser.parse(stackupFile));
            }
        }

        // fonts directory
        Path fontsDir = odbRootPath.resolve("fonts");
        if (Files.exists(fontsDir)) {
            Path standardFontFile = fontsDir.resolve("standard");
            if (Files.exists(standardFontFile)) {
                job.setStandardFont(fontParser.parse(standardFontFile));
            }
        }

        // symbols directory
        Path symbolsDir = odbRootPath.resolve("symbols");
        if (Files.exists(symbolsDir)) {
            job.setSymbols(new HashMap<>());
            try (Stream<Path> symbolDirs = Files.list(symbolsDir)) {
                symbolDirs.filter(Files::isDirectory).forEach(symbolDir -> {
                    try {
                        Symbol symbol = new Symbol();
                        symbol.setName(symbolDir.getFileName().toString());
                        Path attrlistFile = symbolDir.resolve("attrlist");
                        if (Files.exists(attrlistFile)) {
                            symbol.setAttrList(attrListParser.parse(attrlistFile));
                        }
                        Path featuresFile = symbolDir.resolve("features");
                        if (Files.exists(featuresFile)) {
                            symbol.setFeatures(featuresFileParser.parse(featuresFile));
                        }
                        job.getSymbols().put(symbol.getName(), symbol);
                    } catch (IOException e) {
                        // Handle exception
                    }
                });
            }
        }
        
        // wheels directory
        Path wheelsDir = odbRootPath.resolve("wheels");
        if (Files.exists(wheelsDir)) {
            job.setWheels(new HashMap<>());
            try (Stream<Path> wheelDirs = Files.list(wheelsDir)) {
                wheelDirs.filter(Files::isDirectory).forEach(wheelDir -> {
                    try {
                        Wheel wheel = new Wheel();
                        wheel.setName(wheelDir.getFileName().toString());
                        Path attrlistFile = wheelDir.resolve("attrlist");
                        if (Files.exists(attrlistFile)) {
                            wheel.setAttrList(attrListParser.parse(attrlistFile));
                        }
                        Path dcodesFile = wheelDir.resolve("dcodes");
                        if (Files.exists(dcodesFile)) {
                            wheel.setDcodes(dCodeParser.parse(dcodesFile));
                        }
                        job.getWheels().put(wheel.getName(), wheel);
                    } catch (IOException e) {
                        // Handle exception
                    }
                });
            }
        }

        // steps directory
        Path stepsDir = odbRootPath.resolve("steps");
        if (Files.exists(stepsDir)) {
            job.setSteps(new HashMap<>());
            try (Stream<Path> stepDirs = Files.list(stepsDir)) {
                stepDirs.filter(Files::isDirectory).forEach(stepDir -> {
                    try {
                        job.getSteps().put(stepDir.getFileName().toString(), stepParser.parse(stepDir));
                    } catch (IOException e) {
                        // Handle exception
                    }
                });
            }
        }

        return job;
    }
}