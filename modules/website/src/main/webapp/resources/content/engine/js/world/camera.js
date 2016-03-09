projectSandbox.camera =
{
    // Zoom settings
    zoomFactor: 10,
    zoomMin: 1,
    zoomMax: 1000,
    
    // Co-ordinates of the camera
    x: 0,
    y: 0,
    z: 0,
    
    // Rotation of the camera
    rotationX: 0.0,
    rotationY: 0.0,
    rotationZ: 0.0,
    
    // Zoom of the camera from the co-ordinates
    zoom: 400,
    
    // The entity id to chase; if not null, the xyz of the camera is the entity
    chaseEntityId: null,

    // The limits of the camera
    // - 0,1 - XY lower limit
    // - 2,3 - XY upper limit
    limits: null,
    
    // Last co-ordinates of mouse
    mouseX: 0,
    mouseY: 0,
    mouseMove: false,
    mouseRotationFactor: 0.1,

    setup: function()
    {
        // Build camera limits
        this.buildLimits();

        // Update position to check it's within limits
        this.setPosition(this.x, this.y, this.z);
    },

    buildCameraView: function()
    {
        // Translate to center of camera
        //mat4.translate(projectSandbox.cameraView, projectSandbox.cameraView, [-this.x, -this.y, -this.z]);

        // Apply rotations
//        mat4.rotateX(projectSandbox.cameraView, projectSandbox.cameraView, this.rotationX);
//        mat4.rotateY(projectSandbox.cameraView, projectSandbox.cameraView, this.rotationY);
//        mat4.rotateZ(projectSandbox.cameraView, projectSandbox.cameraView, this.rotationZ);

        // Translate by zoom
        mat4.translate(projectSandbox.cameraView, projectSandbox.cameraView, [0, 0, -this.zoom]);
        mat4.translate(projectSandbox.cameraView, projectSandbox.cameraView, [-this.x, -this.y, 0.0]);
    },
    
    renderLogic: function()
    {
        // Update chase co-ordinates
        if(this.chaseEntityId != null)
        {
            var ent = projectSandbox.entities.get(this.chaseEntityId);
            
            if(ent != null)
            {
                if (this.x != ent.x || this.y != ent.y || this.z != ent.z)
                {
                    // Update camera
                    this.setPosition(ent.x, ent.y, ent.z);
                }
            }
            else
            {
                console.warn("engine/camera - chase entity " + this.chaseEntityId + " not found");
            }
        }
        else if (!projectSandbox.network.closed)
        {
            console.warn("engine/camera - no chase entity");
        }

        // Build camera view perspective
        this.buildCameraView();
    },
    
    logic: function()
    {
        var mouse = projectSandbox.interaction.mouse;

        // Update zoom level
        var delta = mouse.scrollDelta;
        
        if(delta != 0)
        {
            var oldZoom = this.zoom;

            // Adjust zoom
            if(delta < 0)
            {
                this.zoom += this.zoomFactor;
            }
            else
            {
                this.zoom -= this.zoomFactor;
            }
            
            // Bound zoom
            if(this.zoom < this.zoomMin)
            {
                this.zoom = this.zoomMin;
            }
            else if(this.zoom > this.zoomMax)
            {
                this.zoom = this.zoomMax;
            }

            // Check if zoom changed
            if (this.zoom != oldZoom)
            {
                // Rebuild camera limits
                this.buildLimits();
            }
            
            // Reset delta
            mouse.scrollDelta = 0;
        }
        
        // Update rotation
        if(mouse.left)
        {
            // Check if this is the first loop with mouse down
            if(!this.mouseMove)
            {
                this.mouseMove = true;
                this.mouseX = mouse.x;
                this.mouseY = mouse.y;
            }
            else
            {
                // Move rotation based on amount of movement
                var diffX = (mouse.x - this.mouseX)/projectSandbox.canvas.width;
                var diffY = (mouse.y - this.mouseY)/projectSandbox.canvas.height;
                this.rotationY += diffX * this.mouseRotationFactor;
                this.rotationX += diffY * this.mouseRotationFactor;
            }
        }
        else if(this.mouseMove)
        {
            // Reset ready for next loop
            this.mouseMove = false;
        }
    },

    setPosition: function(x, y, z)
    {
        var limits = this.limits;

        // Check limits have been built yet
        if (limits == null)
        {
            console.warn("engine/camera - limits not built yet, no restrictions on position");
        }
        else
        {
            // Limit position
            // -- X
            if (x < limits[0])
            {
                x = limits[0];
            }
            else if (x > limits[2])
            {
                x = limits[2];
            }
            // -- Y
            if (y < limits[1])
            {
                y = limits[1];
            }
            else if (y > limits[3])
            {
                y = limits[3];
            }
        }

        // Update camera
        this.x = x;
        this.y = y;
        this.z = z;

        // Update frustrum
        projectSandbox.frustrum.update();

        // Update shader
        var gl = projectSandbox.gl;
        if (projectSandbox.shaderProgram != null)
        {
            gl.vertexAttrib3f(projectSandbox.shaderProgram.cameraPosition, this.x, this.y, this.z + this.zoom);
        }
    },

    buildLimits: function()
    {
        // Check map is ready
        if (!projectSandbox.map.isSetup)
        {
            console.warn("engine/camera - unable to build limits, map not setup");
            return;
        }

        var cameraZ = this.z + this.zoom;

        var mapTileSize = projectSandbox.map.tileSize;
        var mapWidth = projectSandbox.map.width * mapTileSize;
        var mapHeight = projectSandbox.map.height * mapTileSize;

        var frustrumSize = projectSandbox.frustrum.computeFrustrumSize(cameraZ);
        var frustumWidthHalf = frustrumSize[0] / 2.0;
        var frustumHeightHalf = frustrumSize[1] / 2.0;

        this.limits =
        [
            Math.ceil(frustumWidthHalf),
            Math.ceil(frustumHeightHalf),
            Math.floor(mapWidth - frustumWidthHalf),
            Math.floor(mapHeight - frustumHeightHalf)
        ];

        console.debug(
            "engine/camera - limits rebuilt - " +
            this.limits[0] + "," + this.limits[1] + "," + this.limits[2] + "," + this.limits[3]
        );
    }
}