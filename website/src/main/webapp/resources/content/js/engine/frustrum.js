projectSandbox.frustrum =
{
    FRUSTRUM_VERTICAL_FOV: 45.0,
    FRUSTRUM_DISTANCE_NEAR: 1.0,
    FRUSTRUM_DISTANCE_FAR: 1000.0,

    planeVerts: null,

    /*
        Should be invoked when the camera changes, so the frustrum can be recomputed.
    */
	update: function()
	{
	    var ratio = 800.0/600.0;

	    // Camera variables
	    var camearaPos =
	    [
	        projectSandbox.camera.x,
	        projectSandbox.camera.y,
	        projectSandbox.camera.z
	    ];

	    var cameraDirection =
	    [
	        0.0, 0.0, 1.0
	    ];

	    // Calculate size of near plane
	    var nearHeight = 2 * Math.tan(this.FRUSTRUM_VERTICAL_FOV / 2.0) * this.FRUSTRUM_DISTANCE_NEAR;
	    var nearWidth = nearHeight * ratio;

        // Calculate size of far plane
	    var farHeight = 2 * Math.tan(this.FRUSTRUM_VERTICAL_FOV / 2.0) * this.FRUSTRUM_DISTANCE_FAR;
	    var farWidth = farHeight * ratio;

	    // Calculate center points of far and near
	    var farCenter =
	    [
	        camearaPos[0] + cameraDirection[0] * this.FRUSTRUM_DISTANCE_FAR,
	        camearaPos[1] + cameraDirection[1] * this.FRUSTRUM_DISTANCE_FAR,
	        camearaPos[2] + cameraDirection[2] * this.FRUSTRUM_DISTANCE_FAR
	    ];

	    var nearCenter =
	    [
	        camearaPos[0] + cameraDirection[0] * this.FRUSTRUM_DISTANCE_NEAR,
            camearaPos[1] + cameraDirection[1] * this.FRUSTRUM_DISTANCE_NEAR,
            camearaPos[2] + cameraDirection[2] * this.FRUSTRUM_DISTANCE_NEAR
	    ];

        // Build frustrum vert
        planeVerts = new Array();

	    // Far top left
        planeVerts[0] =
        [
            farCenter[0] - (farWidth / 2.0),
            farCenter[1] + (farHeight / 2.0),
            farCenter[2]
        ];

	    // Far top right
        planeVerts[1] =
        [
            farCenter[0] + (farWidth / 2.0),
            farCenter[1] + (farHeight / 2.0),
            farCenter[2]
        ];

	    // Far bottom left
        planeVerts[2] =
        [
            farCenter[0] - (farWidth / 2.0),
            farCenter[1] - (farHeight / 2.0),
            farCenter[2]
        ];

	    // Far bottom right
        planeVerts[3] =
        [
            farCenter[0] + (farWidth / 2.0),
            farCenter[1] - (farHeight / 2.0),
            farCenter[2]
        ];

	    // Near top left
        planeVerts[4] =
        [
            nearCenter[0] - (nearWidth / 2.0),
            nearCenter[1] + (nearHeight / 2.0),
            nearCenter[2]
        ];

	    // Near top right
        planeVerts[5] =
        [
            nearCenter[0] + (nearWidth / 2.0),
            nearCenter[1] + (nearHeight / 2.0),
            nearCenter[2]
        ];

	    // Near bottom left
        planeVerts[6] =
        [
            nearCenter[0] - (nearWidth / 2.0),
            nearCenter[1] - (nearHeight / 2.0),
            nearCenter[2]
        ];

	    // Near bottom right
        planeVerts[7] =
        [
            nearCenter[0] + (nearWidth / 2.0),
            nearCenter[1] - (nearHeight / 2.0),
            nearCenter[2]
        ];

        // Update frustrum computation
        this.planeVerts = planeVerts;

        this.nearWidth = nearWidth;
        this.nearHeight = nearHeight;
        this.farWidth = farWidth;
        this.farHeight = farHeight;
	},

	/*
	    Returns array of length 4:

            0 - start index X
            1 - start index Y
            2 - end index X
            3 - end index y
	*/
	mapRegionToRender: function(tileSize)
	{
	    // Check initial frustrum built
	    if (this.planeVerts == null)
	    {
	        return null;
	    }

	    // Determine z-level of map relative to camera
	    var mapZ = (projectSandbox.map.renderZ * -1.0) + projectSandbox.camera.z + projectSandbox.camera.zoom;

	    // Determine frustrum width/height
	    var ratio = 800.0/600.0;
	    var frustrumHeight = 2 * Math.tan(this.FRUSTRUM_VERTICAL_FOV / 2.0) * mapZ;
        var frustrumWidth = frustrumHeight * ratio;

	    // Generate initial indexes from frustrum
	    var mapIndexes =
	    [
	        Math.floor((projectSandbox.camera.x - (frustrumWidth / 2.0)) / tileSize),
	        Math.floor((projectSandbox.camera.y + (frustrumHeight / 2.0)) / tileSize),

	        Math.floor((projectSandbox.camera.x + (frustrumWidth / 2.0)) / tileSize),
	        Math.floor((projectSandbox.camera.y - (frustrumHeight / 2.0)) / tileSize),
	    ];

	    // Swap and invert y
        mapIndexes[1] = (projectSandbox.map.height - 1) - mapIndexes[1];
        mapIndexes[3] = (projectSandbox.map.height - 1) - mapIndexes[3];

	    // Clamp to size of map
	    mapIndexes[0] = this.clampIndexes(mapIndexes[0], 0, projectSandbox.map.width - 1);
	    mapIndexes[1] = this.clampIndexes(mapIndexes[1], 0, projectSandbox.map.height - 1);
	    mapIndexes[2] = this.clampIndexes(mapIndexes[2], 0, projectSandbox.map.width - 1);
	    mapIndexes[3] = this.clampIndexes(mapIndexes[3], 0, projectSandbox.map.height - 1);

        console.log("y: " + projectSandbox.map.height  +" ->" +mapIndexes[1]);

	    console.debug(mapIndexes);

	    return mapIndexes;
	},

	clampIndexes: function(value, min, max)
	{
        if (value < min)
	    {
	        return min;
	    }
	    else if (value > max)
	    {
	        return max;
	    }
	    else
	    {
	        return value;
	    }
	},

	intersects: function()
	{
	}
}