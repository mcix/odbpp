package com.odbpp.model;

import lombok.Data;

/**
 * Represents a component attribute assignment.
 * According to the ODB++ specification, attributes are comma-separated assignments:
 * - BOOLEAN: n (attribute n is set)
 * - OPTION: n=m (attribute n has value m)
 * - INTEGER: n=i (attribute n has value i)
 * - FLOAT: n=f (attribute n has value f)
 * - TEXT: n=s (attribute n has the value associated with index s in the attribute text string lookup table)
 */
@Data
public class ComponentAttribute {
    /**
     * Attribute index in the attribute name lookup table
     */
    private int attributeIndex;
    
    /**
     * Type of the attribute
     */
    private AttributeType type;
    
    /**
     * Value of the attribute (depends on type)
     */
    private String value;
    
    /**
     * Creates a boolean attribute (just the index, no value needed)
     * 
     * @param attributeIndex the attribute index
     * @return the component attribute
     */
    public static ComponentAttribute createBoolean(int attributeIndex) {
        ComponentAttribute attr = new ComponentAttribute();
        attr.setAttributeIndex(attributeIndex);
        attr.setType(AttributeType.BOOLEAN);
        attr.setValue(null); // Boolean attributes don't need a value
        return attr;
    }
    
    /**
     * Creates an option attribute
     * 
     * @param attributeIndex the attribute index
     * @param optionValue the option value
     * @return the component attribute
     */
    public static ComponentAttribute createOption(int attributeIndex, String optionValue) {
        ComponentAttribute attr = new ComponentAttribute();
        attr.setAttributeIndex(attributeIndex);
        attr.setType(AttributeType.OPTION);
        attr.setValue(optionValue);
        return attr;
    }
    
    /**
     * Creates an integer attribute
     * 
     * @param attributeIndex the attribute index
     * @param intValue the integer value
     * @return the component attribute
     */
    public static ComponentAttribute createInteger(int attributeIndex, int intValue) {
        ComponentAttribute attr = new ComponentAttribute();
        attr.setAttributeIndex(attributeIndex);
        attr.setType(AttributeType.INTEGER);
        attr.setValue(String.valueOf(intValue));
        return attr;
    }
    
    /**
     * Creates a float attribute
     * 
     * @param attributeIndex the attribute index
     * @param floatValue the float value
     * @return the component attribute
     */
    public static ComponentAttribute createFloat(int attributeIndex, double floatValue) {
        ComponentAttribute attr = new ComponentAttribute();
        attr.setAttributeIndex(attributeIndex);
        attr.setType(AttributeType.FLOAT);
        attr.setValue(String.valueOf(floatValue));
        return attr;
    }
    
    /**
     * Creates a text attribute
     * 
     * @param attributeIndex the attribute index
     * @param textIndex the text index in the attribute text string lookup table
     * @return the component attribute
     */
    public static ComponentAttribute createText(int attributeIndex, int textIndex) {
        ComponentAttribute attr = new ComponentAttribute();
        attr.setAttributeIndex(attributeIndex);
        attr.setType(AttributeType.TEXT);
        attr.setValue(String.valueOf(textIndex));
        return attr;
    }
} 