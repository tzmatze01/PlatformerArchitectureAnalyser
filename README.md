# Platformer Architecture Analyser

This project is meant to analyse the architecture of platformer games.
These maps are represented as png files, which are placed at _src/main/images_. \
To find maps, one can visit www.vgmaps.com. \
The Tool displays the game maps, and applies a raster (8x8, 16x16, ...) to the image. \
With clicks on the raster cells, the user can mark the different 'sprites' of the image.

The output of a analysed map will be a text file with characters. \
Each character is corresponding to a tile. Thus the raster and the textfile have the same shape. 
