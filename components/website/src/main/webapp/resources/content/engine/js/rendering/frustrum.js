projectSandbox.frustrum =
{
    FRUSTRUM_VERTICAL_FOV: 45.0,
    FRUSTRUM_DISTANCE_NEAR: 1.0,
    FRUSTRUM_DISTANCE_FAR: 1000.0,

    ratio: null,
    planeVerts: null,

    frustrumDistanceBoxes: new Map(),

    /*
        Should be invoked when the camera changes, so the frustrum can be recomputed.
    */
    update: function()
    {
        // Compute ratio
        this.ratio = projectSandbox.camera.getRenderWidth() / projectSandbox.camera.getRenderHeight();

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

        // Calculate size of near and far plane
        var nearSize = this.computeFrustrumSize(this.FRUSTRUM_DISTANCE_NEAR);
        var nearWidth = nearSize[0];
        var nearHeight = nearSize[1];

        var farSize = this.computeFrustrumSize(this.FRUSTRUM_DISTANCE_FAR);
        var farWidth = farSize[0];
        var farHeight = farSize[1];

        // Calculate center points of far and near
        var farCenter =
        [
            camearaPos[0] + (cameraDirection[0] * this.FRUSTRUM_DISTANCE_FAR),
            camearaPos[1] + (cameraDirection[1] * this.FRUSTRUM_DISTANCE_FAR),
            camearaPos[2] + (cameraDirection[2] * this.FRUSTRUM_DISTANCE_FAR)
        ];

        var nearCenter =
        [
            camearaPos[0] + (cameraDirection[0] * this.FRUSTRUM_DISTANCE_NEAR),
            camearaPos[1] + (cameraDirection[1] * this.FRUSTRUM_DISTANCE_NEAR),
            camearaPos[2] + (cameraDirection[2] * this.FRUSTRUM_DISTANCE_NEAR)
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

        // Update sizes
        this.nearWidth = nearWidth;
        this.nearHeight = nearHeight;
        this.farWidth = farWidth;
        this.farHeight = farHeight;

        // Reset boxes cache
        this.frustrumDistanceBoxes.clear();
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
            console.warn("engine/rendering/frustrum - no verts, cannot build map region to render");
            return null;
        }

        // Determine z-level of map relative to camera
        var mapZ = (projectSandbox.map.renderZ * -1.0) + projectSandbox.camera.z + projectSandbox.camera.zoom;

        // Determine frustrum size
        var frustrumSize = this.computeFrustrumSize(mapZ);

        var frustrumWidthHalf = frustrumSize[0] / 2.0;
        var frustrumHeightHalf = frustrumSize[1] / 2.0;

        // Generate initial indexes from frustrum
        // Note: y is inverted since we subtract it from the map height (invert it)
        var mapIndexes =
        [
            Math.floor((projectSandbox.camera.x - frustrumWidthHalf) / tileSize),
            Math.floor((projectSandbox.camera.y + frustrumHeightHalf) / tileSize),

            Math.floor((projectSandbox.camera.x + frustrumWidthHalf) / tileSize),
            Math.floor((projectSandbox.camera.y - frustrumHeightHalf) / tileSize),
        ];

        var tilesWidth = projectSandbox.map.tilesWidth;
        var tilesHeight = projectSandbox.map.tilesHeight;

        // Invert y
        mapIndexes[1] = (tilesHeight - 1) - mapIndexes[1];
        mapIndexes[3] = (tilesHeight - 1) - mapIndexes[3];

        // Clamp to size of map
        mapIndexes[0] = projectSandbox.utils.clamp(mapIndexes[0], 0, tilesWidth - 1);
        mapIndexes[1] = projectSandbox.utils.clamp(mapIndexes[1], 0, tilesHeight - 1);
        mapIndexes[2] = projectSandbox.utils.clamp(mapIndexes[2], 0, tilesWidth - 1);
        mapIndexes[3] = projectSandbox.utils.clamp(mapIndexes[3], 0, tilesHeight - 1);

        return mapIndexes;
    },

    computeFrustrumSize: function (distance)
    {
        var frustrumHeight = 2 * Math.tan(this.FRUSTRUM_VERTICAL_FOV / 2.0) * distance;
        var frustrumWidth = frustrumHeight * this.ratio;

        return [frustrumWidth, frustrumHeight];
    },

    intersects: function(primitive)
    {
        // Fetcb/generate box for Z level
        var z = primitive.z + projectSandbox.camera.z + projectSandbox.camera.zoom;
        var box = this.frustrumDistanceBoxes.get(z);

        // TODO: change to use box, looks like mistake
        if (boxSize == null)
        {
            // Cache for speed
            var boxSize = this.computeFrustrumSize(z);
            var frustrumWidthHalf = boxSize[0] / 2.0;
            var frustrumHeightHalf = boxSize[1] / 2.0;

            var box =
            [
                projectSandbox.camera.x - frustrumWidthHalf,
                projectSandbox.camera.y - frustrumHeightHalf,
                projectSandbox.camera.x + frustrumWidthHalf,    // (+ w)
                projectSandbox.camera.y + frustrumHeightHalf,    // (+ h)
            ];

            this.frustrumDistanceBoxes.set(z, box);
        }

        var intersects =    primitive.x                     <     box[2]     &&
                            primitive.x + primitive.radius    >    box[0]    &&
                            primitive.y                        <    box[3]    &&
                            primitive.y + primitive.radius    >    box[1]    ;

        return intersects;
    }
}
