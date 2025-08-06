package com.odbpp.model;

import lombok.Data;
import java.util.List;

/**
 * Represents a BOM (Bill of Materials) item in the ODB++ format.
 * The BOM DATA section of a component file contains BOM information for components.
 * 
 * According to the ODB++ specification, BOM Description Records contain the following parameters:
 * - CPN: Customer part number
 * - PKG: Package name
 * - IPN: Internal part number
 * - DSC: Unlimited number of descriptions
 * - VPL_VND: Manufacturer from an external vendor part library corresponding to original vendor
 * - VPL_MPN: MPN from an external vendor part library corresponding to original MPN
 * - VND: Manufacturer (vendor) name
 * - MPN: Manufacturer part number with qualification and chosen status
 */
@Data
public class BomItem {
    /**
     * Customer Part Number (CPN)
     * The customer's part number for this component.
     */
    private String cpn;
    
    /**
     * Package name (PKG)
     * The package type or form factor of the component.
     */
    private String pkg;
    
    /**
     * Internal Part Number (IPN)
     * The internal part number used by the organization.
     */
    private String ipn;
    
    /**
     * Description (DSC)
     * Unlimited number of descriptions for the component.
     */
    private List<String> descriptions;
    
    /**
     * Vendor Part Library Vendor (VPL_VND)
     * Manufacturer from an external vendor part library corresponding to original vendor
     * (as determined in BOM Validation).
     */
    private String vplVnd;
    
    /**
     * Vendor Part Library MPN (VPL_MPN)
     * MPN from an external vendor part library corresponding to original MPN
     * (as determined in BOM Validation).
     */
    private String vplMpn;
    
    /**
     * Vendor/Manufacturer name (VND)
     * The manufacturer (vendor) name.
     */
    private String vnd;
    
    /**
     * Manufacturer Part Number (MPN)
     * The manufacturer part number.
     */
    private String mpn;
    
    /**
     * Qualification status of the part (vendor+mpn) for production.
     * - -1: Not qualified
     * - 0: Unknown
     * - 1: Qualified
     */
    private QualificationStatus qualificationStatus;
    
    /**
     * Whether the part is chosen from among the alternate parts for the CPN.
     * Only one part can be a chosen part.
     * - 0: Not chosen
     * - 1: Chosen
     */
    private ChosenStatus chosenStatus;
    
    /**
     * Quantity of this component in the BOM.
     */
    private int quantity;
    
    /**
     * Item number for ordering in the BOM.
     */
    private int itemNumber;
    
    /**
     * Priority of this part among alternates.
     * Lower numbers indicate higher priority.
     */
    private int priority;
    
    /**
     * Parses an MPN line according to the ODB++ specification.
     * MPN lines contain these parameters separated by spaces:
     * - qualify status (whether the part is qualified for production)
     * - chosen status (whether the part is chosen from among alternates)
     * - MPN (the manufacturer part number)
     * 
     * Example: "0 Y 4N35S" for a CPN component whose qualification is unknown (0),
     * that is the chosen component (1), with a manufacturer part number of 4N35S.
     * 
     * @param mpnLine the MPN line to parse
     * @return this BomItem instance for chaining
     */
    public BomItem parseMpnLine(String mpnLine) {
        if (mpnLine == null || mpnLine.trim().isEmpty()) {
            throw new IllegalArgumentException("MPN line cannot be null or empty");
        }
        
        String[] parts = mpnLine.trim().split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("MPN line must contain at least 3 parts: " + mpnLine);
        }
        
        try {
            int qualifyValue = Integer.parseInt(parts[0]);
            this.qualificationStatus = QualificationStatus.fromValue(qualifyValue);
            
            int chosenValue = Integer.parseInt(parts[1]);
            this.chosenStatus = ChosenStatus.fromValue(chosenValue);
            
            this.mpn = parts[2];
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric values in MPN line: " + mpnLine, e);
        }
        
        return this;
    }
    
    /**
     * Formats the MPN line according to the ODB++ specification.
     * 
     * @return the formatted MPN line
     */
    public String formatMpnLine() {
        if (qualificationStatus == null || chosenStatus == null || mpn == null) {
            throw new IllegalStateException("Qualification status, chosen status, and MPN must be set");
        }
        
        return String.format("%d %d %s", 
            qualificationStatus.getValue(), 
            chosenStatus.getValue(), 
            mpn);
    }
}
