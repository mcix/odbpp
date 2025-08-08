# odbpp
Just another ODB++ library

## ODB++
ODB++ is a CAD-to-CAM data exchange format used in the PCB (Printed Circuit Board) industry for transferring design and manufacturing data. ODB++ (Open Database++) is a proprietary, but widely adopted, format owned by Siemens.

# Goal 1
Extract basic part information such as location, component size (or shape) and pin1 location. And the board outline.
- Component data is located in `steps/{step_name}/layers/comp_+_top` for the top side and `steps/{step_name}/layers/comp_+_bot` for the bottom side of the board.
- The board outline is defined in the step profile located at `steps/{step_name}/profile`.

# Goal 2
General manufacturing and assembly metrics:
- Board size
- Number of layers
- Surface finish
- Minimum track space
- Minimum drill size
- Material and material thickness
- Copper layer thickness, inside and out
- Via's in pad

# Goal 3
Render the full pcb stackup in svg and overlaying the part layers. The svg should be interactive so the user is able to see information about parts.
