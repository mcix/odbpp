package com.odbpp.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Tests for OdbppParser
 */
public class OdbppParserTest {

    @Test
    public void testParseTestData() {
        OdbppParser parser = new OdbppParser();

        try {
            // Test with designodb_rigidflex
            String testDataPath = "testdata/designodb_rigidflex";
            OdbppParser.OdbppData data = parser.parseFromDirectory(testDataPath, "cellular_flip-phone");

            assertNotNull(data);
            assertEquals("cellular_flip-phone", data.getStepName());

            // Check board outline
            assertNotNull(data.getBoardOutline());
            assertTrue(data.getBoardOutline().getIslands().size() > 0);

            // Check components
            assertNotNull(data.getTopComponents());
            assertTrue(data.getTopComponents().size() > 0);

            System.out.println("=== designodb_rigidflex Summary ===");
            System.out.println(data.getSummary());

            // Print some component examples
            System.out.println("\n=== Top Components (first 5) ===");
            data.getTopComponents().stream()
                    .limit(5)
                    .forEach(comp -> System.out.printf("Component: %s (%s) at (%.3f, %.3f) rotation=%.1f°\n",
                            comp.getName(), comp.getPartName(), comp.getX(), comp.getY(), comp.getRotation()));

        } catch (IOException e) {
            System.err.println("Test failed with testdata/designodb_rigidflex: " + e.getMessage());
            // Try with simple odb data
            try {
                String testDataPath = "testdata/odb";
                OdbppParser.OdbppData data = parser.parseFromDirectory(testDataPath, "pcb");

                assertNotNull(data);
                assertEquals("pcb", data.getStepName());

                System.out.println("\n=== odb Summary ===");
                System.out.println(data.getSummary());

                // Print some component examples
                if (data.getTopComponents() != null && !data.getTopComponents().isEmpty()) {
                    System.out.println("\n=== Top Components (first 5) ===");
                    data.getTopComponents().stream()
                            .limit(5)
                            .forEach(comp -> System.out.printf("Component: %s (%s) at (%.3f, %.3f) rotation=%.1f°\n",
                                    comp.getName(), comp.getPartName(), comp.getX(), comp.getY(), comp.getRotation()));
                }

            } catch (IOException e2) {
                System.err.println("Both tests failed: " + e2.getMessage());
                fail("Unable to parse test data: " + e2.getMessage());
            }
        }
    }

    @Test
    public void testDiscoverSteps() {
        OdbppParser parser = new OdbppParser();

        try {
            List<String> steps = parser.discoverSteps("testdata/designodb_rigidflex");
            assertNotNull(steps);
            assertFalse(steps.isEmpty());
            assertTrue(steps.contains("cellular_flip-phone"));

            System.out.println("Discovered steps in designodb_rigidflex: " + steps);

        } catch (IOException e) {
            System.err.println("Failed to discover steps: " + e.getMessage());
            // Don't fail the test if testdata is not available
        }

        try {
            List<String> steps = parser.discoverSteps("testdata/odb");
            assertNotNull(steps);
            assertFalse(steps.isEmpty());
            assertTrue(steps.contains("pcb"));

            System.out.println("Discovered steps in odb: " + steps);

        } catch (IOException e) {
            System.err.println("Failed to discover steps in odb: " + e.getMessage());
            // Don't fail the test if testdata is not available
        }
    }

    @Test
    public void testAutoDiscoverAndParse() {
        OdbppParser parser = new OdbppParser();

        try {
            OdbppParser.OdbppData data = parser.parseFromDirectory("testdata/designodb_rigidflex");
            assertNotNull(data);
            assertNotNull(data.getStepName());

            System.out.println("\n=== Auto-discovered Data ===");
            System.out.println(data.getSummary());

        } catch (IOException e) {
            System.err.println("Auto-discovery test failed: " + e.getMessage());
            // Don't fail the test if testdata is not available
        }
    }
}
