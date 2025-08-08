package com.odbpp.parser;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main ODB++ parser that combines profile and component parsing functionality.
 * Extracts basic part information such as location, component size (or shape) and pin1 location,
 * and the board outline from ODB++ files.
 */
@Slf4j
public class OdbppParser {

    @Getter
    private ProfileParser profileParser;

    @Getter
    private ComponentsParser topComponentParser;

    @Getter
    private ComponentsParser bottomComponentParser;

    /**
     * Parse ODB++ data from a directory structure
     *
     * @param odbppRootPath Path to the ODB++ root directory
     * @param stepName Name of the step to parse
     * @return Parsed ODB++ data
     * @throws IOException if files cannot be read
     */
    public OdbppData parseFromDirectory(String odbppRootPath, String stepName) throws IOException {
        Path rootPath = Paths.get(odbppRootPath);
        Path stepPath = rootPath.resolve("steps").resolve(stepName);

        if (!Files.exists(stepPath)) {
            throw new IOException("Step directory not found: " + stepPath);
        }

        OdbppData data = new OdbppData();
        data.setStepName(stepName);

        // Parse profile (board outline)
        Path profilePath = stepPath.resolve("profile");
        if (Files.exists(profilePath)) {
            profileParser = new ProfileParser();
            profileParser.parseFile(profilePath.toString());
            data.setBoardOutline(profileParser.getBoardOutlineForSVG());
            log.info("Parsed board outline from: {}", profilePath);
        } else {
            log.warn("Profile file not found: {}", profilePath);
        }

        // Parse top components
        Path topComponentsPath = stepPath.resolve("layers/comp_+_top/components");
        if (Files.exists(topComponentsPath)) {
            topComponentParser = new ComponentsParser();
            topComponentParser.parseFile(topComponentsPath.toString());
            data.setTopComponents(topComponentParser.getComponentInfoForSVG());
            log.info("Parsed {} top components from: {}",
                    data.getTopComponents().size(), topComponentsPath);
        } else {
            log.warn("Top components file not found: {}", topComponentsPath);
        }

        // Parse bottom components
        Path bottomComponentsPath = stepPath.resolve("layers/comp_+_bot/components");
        if (Files.exists(bottomComponentsPath)) {
            bottomComponentParser = new ComponentsParser();
            bottomComponentParser.parseFile(bottomComponentsPath.toString());
            data.setBottomComponents(bottomComponentParser.getComponentInfoForSVG());
            log.info("Parsed {} bottom components from: {}",
                    data.getBottomComponents().size(), bottomComponentsPath);
        } else {
            log.warn("Bottom components file not found: {}", bottomComponentsPath);
        }

        return data;
    }

    /**
     * Parse ODB++ data and find the first available step
     *
     * @param odbppRootPath Path to the ODB++ root directory
     * @return Parsed ODB++ data
     * @throws IOException if files cannot be read or no steps found
     */
    public OdbppData parseFromDirectory(String odbppRootPath) throws IOException {
        Path rootPath = Paths.get(odbppRootPath);
        Path stepsPath = rootPath.resolve("steps");

        if (!Files.exists(stepsPath)) {
            throw new IOException("Steps directory not found: " + stepsPath);
        }

        // Find first available step
        try {
            String firstStep = Files.list(stepsPath)
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .findFirst()
                    .orElseThrow(() -> new IOException("No steps found in: " + stepsPath));

            log.info("Found step: {}", firstStep);
            return parseFromDirectory(odbppRootPath, firstStep);

        } catch (IOException e) {
            throw new IOException("Error reading steps directory: " + stepsPath, e);
        }
    }

    /**
     * Discover available steps in an ODB++ directory
     *
     * @param odbppRootPath Path to the ODB++ root directory
     * @return List of available step names
     * @throws IOException if directory cannot be read
     */
    public List<String> discoverSteps(String odbppRootPath) throws IOException {
        Path rootPath = Paths.get(odbppRootPath);
        Path stepsPath = rootPath.resolve("steps");

        if (!Files.exists(stepsPath)) {
            throw new IOException("Steps directory not found: " + stepsPath);
        }

        try {
            return Files.list(stepsPath)
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            throw new IOException("Error reading steps directory: " + stepsPath, e);
        }
    }

    /**
     * Container for parsed ODB++ data
     */
    @Getter
    public static class OdbppData {
        private String stepName;
        private ProfileParser.BoardOutline boardOutline;
        private List<ComponentsParser.ComponentInfo> topComponents;
        private List<ComponentsParser.ComponentInfo> bottomComponents;

        // Setters
        public void setStepName(String stepName) {
            this.stepName = stepName;
        }

        public void setBoardOutline(ProfileParser.BoardOutline boardOutline) {
            this.boardOutline = boardOutline;
        }

        public void setTopComponents(List<ComponentsParser.ComponentInfo> topComponents) {
            this.topComponents = topComponents;
        }

        public void setBottomComponents(List<ComponentsParser.ComponentInfo> bottomComponents) {
            this.bottomComponents = bottomComponents;
        }

        /**
         * Get summary information about the parsed data
         */
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append(String.format("Step: %s\n", stepName));

            if (boardOutline != null) {
                summary.append(String.format("Board outline: %d islands, %d holes\n",
                        boardOutline.getIslands().size(), boardOutline.getHoles().size()));
            } else {
                summary.append("Board outline: not available\n");
            }

            if (topComponents != null) {
                summary.append(String.format("Top components: %d\n", topComponents.size()));
            } else {
                summary.append("Top components: not available\n");
            }

            if (bottomComponents != null) {
                summary.append(String.format("Bottom components: %d\n", bottomComponents.size()));
            } else {
                summary.append("Bottom components: not available\n");
            }

            return summary.toString();
        }
    }
}
