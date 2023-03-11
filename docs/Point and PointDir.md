# Point and PointDir

## What are Point and PointDir?

Point and PointDir are two classes which make it easy to represent a position with or without a heading. PointDir inherits from Point.

## How to use Point and PointDir

Points have an x-coordinate and a y-coordinate, which can be accessed via `getX()` and `getY()`. PointDir additionally has a heading, which can be accessed via `getHeading()`.

Point has several useful methods:

* `distance(Point a)` returns the distance between the current point and `a`.
* `slope(Point a)` returns the slope of the line between the current point and `a`. It has a maximum and minimum of ±1000.
* `add(Point a)` returns a point with an whose coordinates are the sums of the corresponding coordinates of the current point and `a`.
* `scaleVector(Point inputPoint, double scale)` returns a point whose coordinates are the coordinates of a vector between the current point and `inputPoint`, scaled to have a length of `scale`.
