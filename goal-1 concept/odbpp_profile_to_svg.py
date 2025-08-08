#!/usr/bin/env python3
"""
ODB++ Profile to SVG Converter

This script converts an ODB++ profile file to SVG format.
The profile defines the outline shape of a PCB step including islands and holes.
"""

import re
import math
import sys
from typing import List, Tuple, Optional
from xml.dom.minidom import Document


class ProfileParser:
    """Parser for ODB++ profile files"""
    
    def __init__(self):
        self.units = "MM"
        self.features = []
        self.current_surface = None
        self.current_polygon = None


class ComponentParser:
    """Parser for ODB++ component files"""
    
    def __init__(self):
        self.units = "MM"
        self.components = []
    
    def parse_file(self, filename: str):
        """Parse an ODB++ component file"""
        with open(filename, 'r') as f:
            lines = f.readlines()
        
        for line in lines:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
                
            self._parse_line(line)
    
    def _parse_line(self, line: str):
        """Parse a single line from the component file"""
        if line.startswith('UNITS='):
            self.units = line.split('=')[1]
        elif line.startswith('CMP '):
            self._parse_component(line)
    
    def _parse_component(self, line: str):
        """Parse component record: CMP <pkg_ref> <x> <y> <rot> <mirror> <comp_name> <part_name>; <attributes>;ID=<id>"""
        # Split by semicolon to separate main part from attributes
        parts = line.split(';')
        main_part = parts[0].strip()
        
        # Parse the main CMP record
        tokens = main_part.split()
        if len(tokens) >= 7:
            pkg_ref = tokens[1]
            x = float(tokens[2])
            y = float(tokens[3])
            rot = float(tokens[4])
            mirror = tokens[5]
            comp_name = tokens[6]
            part_name = tokens[7] if len(tokens) > 7 else "???"
            
            component = {
                'pkg_ref': pkg_ref,
                'x': x,
                'y': y,
                'rotation': rot,
                'mirror': mirror,
                'comp_name': comp_name,
                'part_name': part_name
            }
            
            self.components.append(component)


class ProfileParser:
    """Parser for ODB++ profile files"""
    
    def __init__(self):
        self.units = "MM"
        self.features = []
        self.current_surface = None
        self.current_polygon = None
        
    def parse_file(self, filename: str):
        """Parse an ODB++ profile file"""
        with open(filename, 'r') as f:
            lines = f.readlines()
        
        for line in lines:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
                
            self._parse_line(line)
    
    def _parse_line(self, line: str):
        """Parse a single line from the profile file"""
        if line.startswith('UNITS='):
            self.units = line.split('=')[1]
        elif line.startswith('S '):
            self._parse_surface_start(line)
        elif line.startswith('OB '):
            self._parse_outline_begin(line)
        elif line.startswith('OS '):
            self._parse_outline_segment(line)
        elif line.startswith('OC '):
            self._parse_outline_curve(line)
        elif line == 'OE':
            self._parse_outline_end()
        elif line == 'SE':
            self._parse_surface_end()
    
    def _parse_surface_start(self, line: str):
        """Parse surface start record: S P 0;;ID=16732"""
        parts = line.split()
        polarity = parts[1]  # P or N
        dcode = parts[2].split(';')[0]
        
        self.current_surface = {
            'polarity': polarity,
            'dcode': dcode,
            'polygons': []
        }
    
    def _parse_outline_begin(self, line: str):
        """Parse outline begin: OB x y type"""
        parts = line.split()
        x, y = float(parts[1]), float(parts[2])
        poly_type = parts[3]  # I=island, H=hole
        
        self.current_polygon = {
            'type': poly_type,
            'start_point': (x, y),
            'segments': [],
            'closed': False
        }
    
    def _parse_outline_segment(self, line: str):
        """Parse outline segment: OS x y"""
        parts = line.split()
        x, y = float(parts[1]), float(parts[2])
        
        if self.current_polygon:
            self.current_polygon['segments'].append({
                'type': 'line',
                'end': (x, y)
            })
    
    def _parse_outline_curve(self, line: str):
        """Parse outline curve: OC end_x end_y center_x center_y cw"""
        parts = line.split()
        end_x, end_y = float(parts[1]), float(parts[2])
        center_x, center_y = float(parts[3]), float(parts[4])
        clockwise = parts[5] == 'Y'
        
        if self.current_polygon:
            self.current_polygon['segments'].append({
                'type': 'arc',
                'end': (end_x, end_y),
                'center': (center_x, center_y),
                'clockwise': clockwise
            })
    
    def _parse_outline_end(self):
        """Parse outline end: OE"""
        if self.current_polygon and self.current_surface:
            self.current_polygon['closed'] = True
            self.current_surface['polygons'].append(self.current_polygon)
            self.current_polygon = None
    
    def _parse_surface_end(self):
        """Parse surface end: SE"""
        if self.current_surface:
            self.features.append(self.current_surface)
            self.current_surface = None


class SVGGenerator:
    """Generate SVG from parsed profile data"""
    
    def __init__(self, profile_parser: ProfileParser, component_parser: ComponentParser = None):
        self.profile_parser = profile_parser
        self.component_parser = component_parser
        self.doc = Document()
        
    def generate_svg(self, output_filename: str):
        """Generate SVG file from parsed profile data"""
        # Calculate bounding box
        min_x, min_y, max_x, max_y = self._calculate_bounds()
        
        # Add some padding
        padding = 5
        width = max_x - min_x + 2 * padding
        height = max_y - min_y + 2 * padding
        
        # Create SVG root element
        svg = self.doc.createElement('svg')
        svg.setAttribute('xmlns', 'http://www.w3.org/2000/svg')
        svg.setAttribute('width', f'{width}')
        svg.setAttribute('height', f'{height}')
        svg.setAttribute('viewBox', f'{min_x - padding} {min_y - padding} {width} {height}')
        self.doc.appendChild(svg)
        
        # Add styles
        style = self.doc.createElement('style')
        style.appendChild(self.doc.createTextNode("""
            .board-outline { fill: #1A4B1A; stroke: black; stroke-width: 0.1; }
            .board-hole { fill: white; stroke: black; stroke-width: 0.1; }
            .component { fill: orange; stroke: darkorange; stroke-width: 0.1; }
            .component-label { font-family: Arial, sans-serif; font-size: 1px; text-anchor: middle; fill: black; }
        """))
        svg.appendChild(style)
        
        # Process each surface feature
        for feature in self.profile_parser.features:
            self._add_surface_to_svg(svg, feature, min_y, max_y)
        
        # Add components if available
        if self.component_parser:
            self._add_components_to_svg(svg, min_y, max_y)
        
        # Write to file
        with open(output_filename, 'w') as f:
            f.write(self.doc.toprettyxml(indent='  '))
        
        print(f"SVG generated: {output_filename}")
        print(f"Bounds: ({min_x:.3f}, {min_y:.3f}) to ({max_x:.3f}, {max_y:.3f})")
        print(f"Units: {self.profile_parser.units}")
        if self.component_parser:
            print(f"Components: {len(self.component_parser.components)}")
    
    def _calculate_bounds(self) -> Tuple[float, float, float, float]:
        """Calculate the bounding box of all features including components"""
        min_x = min_y = float('inf')
        max_x = max_y = float('-inf')
        
        # Check profile features
        for feature in self.profile_parser.features:
            for polygon in feature['polygons']:
                # Check start point
                x, y = polygon['start_point']
                min_x, max_x = min(min_x, x), max(max_x, x)
                min_y, max_y = min(min_y, y), max(max_y, y)
                
                # Check all segment endpoints
                for segment in polygon['segments']:
                    x, y = segment['end']
                    min_x, max_x = min(min_x, x), max(max_x, x)
                    min_y, max_y = min(min_y, y), max(max_y, y)
        
        # Check component positions
        if self.component_parser:
            for component in self.component_parser.components:
                x, y = component['x'], component['y']
                min_x, max_x = min(min_x, x), max(max_x, x)
                min_y, max_y = min(min_y, y), max(max_y, y)
        
        return min_x, min_y, max_x, max_y
    
    def _add_surface_to_svg(self, svg_root, feature: dict, min_y: float, max_y: float):
        """Add a surface feature to the SVG"""
        # Create a group for this surface
        group = self.doc.createElement('g')
        svg_root.appendChild(group)
        
        # Separate islands and holes
        islands = [p for p in feature['polygons'] if p['type'] == 'I']
        holes = [p for p in feature['polygons'] if p['type'] == 'H']
        
        # Create path for islands
        for island in islands:
            path = self.doc.createElement('path')
            path_data = self._polygon_to_path(island, min_y, max_y)
            
            # Add holes to the island path
            for hole in holes:
                hole_data = self._polygon_to_path(hole, min_y, max_y)
                path_data += ' ' + hole_data
            
            path.setAttribute('d', path_data)
            path.setAttribute('class', 'board-outline')
            path.setAttribute('fill-rule', 'evenodd')  # For proper hole rendering
            group.appendChild(path)
    
    def _polygon_to_path(self, polygon: dict, min_y: float, max_y: float) -> str:
        """Convert a polygon to SVG path data"""
        if not polygon['segments']:
            return ""
        
        start_x, start_y = polygon['start_point']
        # Flip Y coordinate for SVG (SVG has origin at top-left)
        start_y = max_y - start_y + min_y
        
        path_data = f"M {start_x:.3f} {start_y:.3f}"
        
        current_x, current_y = start_x, start_y
        
        for segment in polygon['segments']:
            if segment['type'] == 'line':
                end_x, end_y = segment['end']
                end_y = max_y - end_y + min_y  # Flip Y
                path_data += f" L {end_x:.3f} {end_y:.3f}"
                current_x, current_y = end_x, end_y
                
            elif segment['type'] == 'arc':
                end_x, end_y = segment['end']
                end_y = max_y - end_y + min_y  # Flip Y
                center_x, center_y = segment['center']
                center_y = max_y - center_y + min_y  # Flip Y
                clockwise = segment['clockwise']
                
                # Calculate radius and arc parameters
                radius = math.sqrt((current_x - center_x)**2 + (current_y - center_y)**2)
                
                # Calculate the angle span of the arc
                start_angle = math.atan2(current_y - center_y, current_x - center_x)
                end_angle = math.atan2(end_y - center_y, end_x - center_x)
                
                # Calculate the angle difference
                angle_diff = end_angle - start_angle
                
                # Normalize to [-π, π]
                while angle_diff > math.pi:
                    angle_diff -= 2 * math.pi
                while angle_diff < -math.pi:
                    angle_diff += 2 * math.pi
                
                # Determine if it's a large arc (>180 degrees) BEFORE adjusting for direction
                large_arc_flag = 1 if abs(angle_diff) > math.pi else 0
                
                # In ODB++: Y=clockwise, N=counter-clockwise
                # In SVG with Y-flipped coordinates:
                # - ODB++ clockwise (Y) becomes SVG counter-clockwise (sweep-flag=0)
                # - ODB++ counter-clockwise (N) becomes SVG clockwise (sweep-flag=1)
                
                if clockwise:  # Y in ODB++
                    sweep_flag = 1  # counter-clockwise in SVG due to Y-flip
                else:  # N in ODB++
                    sweep_flag = 0  # clockwise in SVG due to Y-flip
                
                path_data += f" A {radius:.3f} {radius:.3f} 0 {large_arc_flag} {sweep_flag} {end_x:.3f} {end_y:.3f}"
                current_x, current_y = end_x, end_y
        
        if polygon['closed']:
            path_data += " Z"
        
        return path_data
    
    def _add_components_to_svg(self, svg_root, min_y: float, max_y: float):
        """Add components to the SVG"""
        components_group = self.doc.createElement('g')
        components_group.setAttribute('class', 'components')
        svg_root.appendChild(components_group)
        
        for component in self.component_parser.components:
            # Get component position and flip Y coordinate
            comp_x = component['x']
            comp_y = max_y - component['y'] + min_y
            
            # Create a small rectangle to represent the component
            rect = self.doc.createElement('rect')
            rect.setAttribute('x', f'{comp_x - 0.5:.3f}')
            rect.setAttribute('y', f'{comp_y - 0.5:.3f}')
            rect.setAttribute('width', '1.0')
            rect.setAttribute('height', '1.0')
            rect.setAttribute('class', 'component')
            
            # Add rotation if needed
            if component['rotation'] != 0.0:
                rect.setAttribute('transform', f'rotate({component["rotation"]} {comp_x:.3f} {comp_y:.3f})')
            
            components_group.appendChild(rect)
            
            # Add component label
            text = self.doc.createElement('text')
            text.setAttribute('x', f'{comp_x:.3f}')
            text.setAttribute('y', f'{comp_y + 1.5:.3f}')  # Slightly below the component
            text.setAttribute('class', 'component-label')
            text.appendChild(self.doc.createTextNode(component['comp_name']))
            components_group.appendChild(text)


def main():
    """Main function"""
    if len(sys.argv) < 3 or len(sys.argv) > 4:
        print("Usage: python odbpp_profile_to_svg.py <input_profile> <output_svg> [components_file]")
        print("Example: python odbpp_profile_to_svg.py testdata/qorvpr71odbpp/steps/stp/profile output.svg")
        print("Example: python odbpp_profile_to_svg.py testdata/qorvpr71odbpp/steps/stp/profile output.svg testdata/qorvpr71odbpp/steps/stp/layers/comp_+_top/components")
        sys.exit(1)
    
    input_file = sys.argv[1]
    output_file = sys.argv[2]
    components_file = sys.argv[3] if len(sys.argv) == 4 else None
    
    try:
        # Parse the profile file
        profile_parser = ProfileParser()
        profile_parser.parse_file(input_file)
        
        if not profile_parser.features:
            print("No features found in the profile file")
            sys.exit(1)
        
        # Parse components file if provided
        component_parser = None
        if components_file:
            component_parser = ComponentParser()
            component_parser.parse_file(components_file)
        
        # Generate SVG
        generator = SVGGenerator(profile_parser, component_parser)
        generator.generate_svg(output_file)
        
    except FileNotFoundError as e:
        print(f"Error: Could not find input file: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()