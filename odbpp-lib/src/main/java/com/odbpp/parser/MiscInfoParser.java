package com.odbpp.parser;

import com.odbpp.model.MiscInfo;
import com.odbpp.StructuredTextParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class MiscInfoParser {
    private final StructuredTextParser structuredTextParser = new StructuredTextParser();

    public MiscInfo parse(Path miscInfoFile) throws IOException {
        Map<String, String> data = structuredTextParser.parse(miscInfoFile);
        MiscInfo miscInfo = new MiscInfo();
        miscInfo.setProductModelName(data.get("PRODUCT_MODEL_NAME"));
        miscInfo.setOdbVersionMajor(Integer.parseInt(data.get("ODB_VERSION_MAJOR")));
        miscInfo.setOdbVersionMinor(Integer.parseInt(data.get("ODB_VERSION_MINOR")));
        miscInfo.setOdbSource(data.get("ODB_SOURCE"));
        miscInfo.setCreationDate(data.get("CREATION_DATE"));
        miscInfo.setSaveDate(data.get("SAVE_DATE"));
        miscInfo.setSaveApp(data.get("SAVE_APP"));
        miscInfo.setSaveUser(data.get("SAVE_USER"));
        miscInfo.setUnits(data.get("UNITS"));
        if (data.containsKey("MAX_UID")) {
            miscInfo.setMaxUid(Long.parseLong(data.get("MAX_UID")));
        }
        return miscInfo;
    }
}
