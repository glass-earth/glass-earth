# WMAPI v1.0 (World Map API)

[Glass Earth API](https://github.com/glass-earth/glass-earth/tree/master/gibs-data) is an API that provides imagery of the Earth from NASA's satellites. Having been processed to filter noise and reduce missed data, the imagery is displayed in a more correct and simpler way for users and scientists to access.

We make the data available for public at [glassearth.net/wmapi/](http://glassearth.net/wmapi/). The public API is called WMAPI.

**Note: All data is get from [GIBS](https://github.com/glass-earth/glass-earth/tree/master/gibs-data) service. We do not own the data.**

![compare result][1]

## Usage

Send HTTP GET request to WMAPI service

```
GET http://glassearth.net/wmapi/v1/[graph_name]?day=[day]&level=1
```

Example

```
GET http://glassearth.net/wmapi/v1/land_temp?day=2013-12-02&level=1
```

### Response

Data is reponsed as an image for the requested day. If the requested day is not in database, it will response upper nearest day. If the requested day is out of range or graph name is invalid, it will reponse 400 Bad Request.

### Graph Name

These graphs are currently supported:

* `aerosol`
* `chlorophylla`
* `cloud_top_temp`
* `dust_score`
* `land_temp`
* `ozone`
* `precipitation`
* `sea_temp`
* `snow_cover`
* `so2`
* `water_vapor`

### Day

Day format is `yyyy-MM-dd`. For example `2012-12-20`. Day must be between `2012-05-08` and `2014-04-08`. We will support latest data in future.

### Level

We currently only support level 1. We will add more level in future.

## Future Works

1. Support more types of data
2. Support more data level
3. Update latest data everyday from GIBS


[1]: https://dl.dropboxusercontent.com/u/36585213/OpenSource%20Resource/nasaisac2014/comparing.png (403KB)
