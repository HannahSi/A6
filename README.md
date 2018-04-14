# A6
Assignment 6 for CS 2110

Clara Song (cs2274) and Hannah Si (hs649)

Known issues:
When a large tool size is selected, curved lines drawn by the pencil and eraser tools  have jagged edges. Dragging the airbrush rapidly leaves discrete spots instead of a continuous airbrushed streak.
Also, on only one of our computers, the cursor often fails to change back from the default cursor to the active tool after exiting the color chooser dialog. It does display the correct tool, however, after leaving and reentering the program window. On few occasions, the cursor does behave correctly, but we have not been able to determine a consistent cause.

Other notes:
Wherever the mouse position is set using a MouseEvent, we add 0.5 to the X and Y coordinates before casting to an int as a method of rounding to the nearest integer.

We implement the airbrush tool by iterating through the pixels contained in an approximate circle of radius toolSize centered at the mouse position (approximate due to integer coordinates). At each pixel, a random decimal is generated between 0.0 and 1.0, and if the decimal is less than a given percentage (10%), a rectangle of length and width 1 is drawn at the pixel’s coordinates. The result is a circle that is randomly filled to 10% with the foreground color.