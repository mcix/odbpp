package com.odbpp.parser;

import com.odbpp.model.Profile;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

class ProfileParserTest {

    @Test
    void testParse() throws Exception {
        Path path = Paths.get("..", "testdata", "designodb_rigidflex", "steps", "cellular_flip-phone", "layers", "signal_1", "profile");
        ProfileParser parser = new ProfileParser();
        Profile profile = parser.parse(path);
        assertNotNull(profile);
        assertFalse(profile.getSurfaces().isEmpty());
    }
}