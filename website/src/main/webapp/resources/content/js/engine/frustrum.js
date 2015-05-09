projectSandbox.frustrum =
{
    FRUSTRUM_VERTICAL_FOV: 45.0,
    FRUSTRUM_DISTANCE_NEAR: 1.0,
    FRUSTRUM_DISTANCE_FAR: 1000.0,

    planeVerts: new Array(),

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

	    // Far top left
        this.planeVerts[0] =
        [
            farCenter[0] - (farWidth / 2.0),
            farCenter[1] + (farHeight / 2.0),
            farCenter[2]
        ];

	    // Far top right
        this.planeVerts[1] =
        [
            farCenter[0] + (farWidth / 2.0),
            farCenter[1] + (farHeight / 2.0),
            farCenter[2]
        ];

	    // Far bottom left
        this.planeVerts[2] =
        [
            farCenter[0] - (farWidth / 2.0),
            farCenter[1] - (farHeight / 2.0),
            farCenter[2]
        ];

	    // Far bottom right
        this.planeVerts[3] =
        [
            farCenter[0] + (farWidth / 2.0),
            farCenter[1] - (farHeight / 2.0),
            farCenter[2]
        ];

	    // Near top left
        this.planeVerts[4] =
        [
            nearCenter[0] - (nearWidth / 2.0),
            nearCenter[1] + (nearHeight / 2.0),
            nearCenter[2]
        ];

	    // Near top right
        this.planeVerts[5] =
        [
            nearCenter[0] + (nearWidth / 2.0),
            nearCenter[1] + (nearHeight / 2.0),
            nearCenter[2]
        ];

	    // Near bottom left
        this.planeVerts[6] =
        [
            nearCenter[0] - (nearWidth / 2.0),
            nearCenter[1] - (nearHeight / 2.0),
            nearCenter[2]
        ];

	    // Near bottom right
        this.planeVerts[7] =
        [
            nearCenter[0] + (nearWidth / 2.0),
            nearCenter[1] - (nearHeight / 2.0),
            nearCenter[2]
        ];
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
	    // Generate initial indexes from frustrum
	    var mapIndexes =
	    [
	        Math.floor(this.planeVerts[6][0] / tileSize),
	        Math.floor(this.planeVerts[6][1] / tileSize),

	        Math.ceil(this.planeVerts[5][0] / tileSize),
	        Math.ceil(this.planeVerts[5][1] / tileSize)
	    ];

	    // Clamp to size of map
	    mapIndexes[0] = this.clampIndexes(mapIndexes[0], 0, projectSandbox.map.width - 1);
	    mapIndexes[1] = this.clampIndexes(mapIndexes[1], 0, projectSandbox.map.height - 1);
	    mapIndexes[2] = this.clampIndexes(mapIndexes[2], 0, projectSandbox.map.width - 1);
	    mapIndexes[3] = this.clampIndexes(mapIndexes[3], 0, projectSandbox.map.height - 1);

        console.debug(this.planeVerts[6][0]);
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