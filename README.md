# odbpp
Just another ODB++ library

## ODB++
ODB++ is a CAD-to-CAM data exchange format used in the PCB (Printed Circuit Board) industry for transferring design and manufacturing data. ODB++ (Open Database++) is a proprietary, but widely adopted, format owned by Siemens.

# Goal 1
The first goal of this project is the parse and extract information from ODB++ files. Starting with general manufacturing and assembly metrics:
- Board size
- Number of layers
- Surface finish
- Minimum track space
- Minimum drill size
- Material and material thickness
- Copper layer thickness, inside and out
- Via's in pad
- Bill of Materials
- Centroid

# Goal 2
Extract basic part information such as location and component size.

# Goal 3
Render the full pcb stackup in svg and overlaying the part layers. The svg should be interactive so the user is able to see information about parts.
