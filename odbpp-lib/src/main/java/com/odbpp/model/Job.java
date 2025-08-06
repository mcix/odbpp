package com.odbpp.model;

import lombok.Data;
import java.util.Map;

/**
 * Top-level class representing the entire ODB++ job/product model.
 * 
 * According to ODB++ specification (page 23), the following files are REQUIRED:
 * - <product_model_name>/matrix/matrix
 * - <product_model_name>/misc/info  
 * - <product_model_name>/fonts/standard
 * - <product_model_name>/steps/<step_name>/stephdr
 * - <product_model_name>/steps/<step_name>/layers/<layer_name>/features (or features.Z)
 * 
 * Entity naming rules (spec pg. 23):
 * - Entity names must not exceed 64 characters
 * - Entity names may contain only: lowercase letters (a-z), digits (0-9), 
 *   punctuation: dash(-), underscore(_), dot(.) and plus(+)
 * - Entity names must not start with dot(.), hyphen(-), or plus(+)
 *   (Exception: system attribute names start with a dot)
 * - Entity names must not end with a dot(.)
 * 
 * Default units of measurement are defined in the UNITS directive in misc/info.
 * If not defined, the default is imperial.
 */
@Data
public class Job {
    /**
     * Basic information from misc/info. 
     * REQUIRED - Contains product model metadata and UNITS directive.
     */
    private MiscInfo miscInfo;

    /**
     * Product model attributes from misc/attrlist. 
     * OPTIONAL - Contains user-defined attributes for the product model.
     */
    private AttrList productModelAttributes;

    /**
     * Timestamp from misc/last_save. 
     * OPTIONAL - Records when the job was last saved.
     */
    private String lastSave;

    /**
     * Data from misc/metadata.xml. 
     * OPTIONAL - Additional metadata in XML format.
     */
    private Metadata metadata;

    /**
     * The layer and step matrix from matrix/matrix. 
     * REQUIRED - Defines the relationship between steps and layers.
     */
    private Matrix matrix;

    /**
     * Data from matrix/stackup.xml. 
     * OPTIONAL - Layer stackup information in XML format.
     */
    private com.odbpp.model.stackup.StackupFile stackup;

    /**
     * Standard font definition from fonts/standard. 
     * REQUIRED - Defines the standard font used throughout the design.
     */
    private StandardFont standardFont;

    /**
     * User-defined symbols from the symbols directory. 
     * OPTIONAL - Custom symbols used in the design.
     * 
     * Symbol features are stored in: symbols/<symbol_name>/features
     * See specification page 97 for Symbol Features details.
     */
    private Map<String, Symbol> symbols;

    /**
     * Wheels (aperture tables) from the wheels directory. 
     * OPTIONAL - Aperture definitions for different tools/machines.
     */
    private Map<String, Wheel> wheels;

    /**
     * System attributes from misc/sysattr.* files. 
     * OPTIONAL - System-defined attributes (names start with a dot).
     */
    private Map<String, AttributeDefinition> systemAttributes;

    /**
     * User attributes from misc/userattr file. 
     * OPTIONAL - User-defined attributes (names must not start with a dot).
     */
    private Map<String, AttributeDefinition> userAttributes;

    /**
     * Steps from the steps directory. 
     * REQUIRED - Each step contains layers with features and components.
     * 
     * Required step structure:
     * - steps/<step_name>/stephdr (step header)
     * - steps/<step_name>/layers/<layer_name>/features (or features.Z)
     * 
     * Additional step files:
     * - steps/<step_name>/eda/data (EDA data, see spec page 111)
     * - steps/<step_name>/layers/<layer_name>/components (Components, see spec page 155)
     * - steps/<step_name>/layers/<layer_name>/features (Graphic Features, see spec page 172)
     */
    private Map<String, Step> steps;
}
