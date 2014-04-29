# Glass Earth API v1.0

Glass Earth API is an API that provides imagery of the Earth from NASA's satellites. Having been processed to filter noise and reduce missed data, the imagery is displayed in a more correct and simpler way for users and scientists to access.

![compare result][1]

## Download
JAR: 
> [WMGlassEarthAPI.v1.jar](https://dl.dropboxusercontent.com/u/36585213/OpenSource%20Resource/nasaisac2014/WMGlassEarthAPI.v1.jar)

## Setup

* Download JAR
* Put the JAR in the libs sub-folder of your Java project

## Usage

    GEInfo info = JWMGlassEarthAPI.getInfo(GEType type, Date date, int level)

**Parameters**

* `type` : the type of data which you want to get from API. Currently, we support 11 types of unique data, including: Land Surface Temperature, Sea Surface Temperature, Aerosol Optical Depth, Chlorophyll A, Cloud Top Temperature, Dust Score, Ozone, Snow Cover, Sulfur Dioxide, Water Vapor, and Precipitation.
* `date` : the date which you want to get data. Our database is now support date from May 08, 2012 to present.
* `level` : the quality of image you wish to get. Now we just support level 1 which means the size of graph is 640x360 pixels.

**Result**
The `getInfo` function returns a `GEInfo` instance. `GEInfo` contains several read-only information of data.

* `version` : the version of the API
* `level` : similar to `level` of parameter
* `group` : the group of the `type` of data
* `url` : the `url` by which you can get imagery from Glass Earth Server.

## Future Works
1. Support more types of data
2. Support more data level


[1]: https://dl.dropboxusercontent.com/u/36585213/OpenSource%20Resource/nasaisac2014/comparing.png (403KB)