#!/bin/bash

# Create temporary iconset directory
ICONSET="baby.iconset"
mkdir -p "$ICONSET"

# Use magick instead of convert for newer ImageMagick versions
# Convert PNG to various sizes
magick "../../resources/icons/baby.png" -resize 16x16 "$ICONSET/icon_16x16.png"
magick "../../resources/icons/baby.png" -resize 32x32 "$ICONSET/icon_16x16@2x.png"
magick "../../resources/icons/baby.png" -resize 32x32 "$ICONSET/icon_32x32.png"
magick "../../resources/icons/baby.png" -resize 64x64 "$ICONSET/icon_32x32@2x.png"
magick "../../resources/icons/baby.png" -resize 128x128 "$ICONSET/icon_128x128.png"
magick "../../resources/icons/baby.png" -resize 256x256 "$ICONSET/icon_128x128@2x.png"
magick "../../resources/icons/baby.png" -resize 256x256 "$ICONSET/icon_256x256.png"
magick "../../resources/icons/baby.png" -resize 512x512 "$ICONSET/icon_256x256@2x.png"
magick "../../resources/icons/baby.png" -resize 512x512 "$ICONSET/icon_512x512.png"
magick "../../resources/icons/baby.png" -resize 1024x1024 "$ICONSET/icon_512x512@2x.png"

# Create .icns file
iconutil -c icns "$ICONSET"

# Clean up
rm -rf "$ICONSET"
