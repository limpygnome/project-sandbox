projectSandbox.camera =
{
    // Zoom settings
    zoomFactor: 10,
    zoomMin: 1,
    zoomMax: 1000,
    
    // Co-ordinates of the camera
    x: 0.0,
    y: 0.0,
    z: 0.0,
    
    // Rotation of the camera
    rotationX: 0.0,
    rotationY: 0.0,
    rotationZ: 0.0,
    
    // Zoom of the camera from the co-ordinates
    zoom: 800,
    
    // The entity id to chase; if not null, the xyz of the camera is the entity
    chaseEntityId: null,

    // The limits of the camera
    // - 0,1 - XY lower limit
    // - 2,3 - XY upper limit
    limits: null,

    // The game time at which the limits were last built
    limitsLastBuiltTime: 0,
    
    // Last co-ordinates of mouse
    mouseX: 0,
    mouseY: 0,
    mouseMove: false,
    mouseRotationFactor: 0.1,

    setup: function(canvas, gl)
    {
        // Update position to check it's within limits
        this.setPosition(this.x, this.y, this.z);

        // Output useful debug information
        console.debug(  "engine/camera - render width: " + this.getRenderWidth() +
                        ", render height: " + this.getRenderHeight() +
                        ", render ratio: " + this.getRenderRatio() +
                        ", canvas width: " + canvas.clientWidth +
                        ", canvas height: " + canvas.clientHeight +
                        ", viewport width: " + gl.viewportWidth +
                        ", viewport height: " + gl.viewportHeight
        );
    },

    renderLogic: function(cameraView)
    {
        // Build camera view perspective
        this.buildCameraView(cameraView);
    },

    buildCameraView: function(cameraView)
    {
        // Translate by zoom
        mat4.translate(cameraView, cameraView, [0, 0, -this.zoom]);
        mat4.translate(cameraView, cameraView, [-this.x, -this.y, 0.0]);
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
        if (mouse.left)
        {
            // Check if this is the first loop with mouse down
            if (!this.mouseMove)
            {
                this.mouseMove = true;
                this.mouseX = mouse.x;
                this.mouseY = mouse.y;
            }
            else
            {
                // Move rotation based on amount of movement
                var diffX = (mouse.x - this.mouseX)/projectSandbox.rendering.core.getCanvas().width;
                var diffY = (mouse.y - this.mouseY)/projectSandbox.rendering.core.getCanvas().height;
                this.rotationY += diffX * this.mouseRotationFactor;
                this.rotationX += diffY * this.mouseRotationFactor;
            }
        }
        else if (this.mouseMove)
        {
            // Reset ready for next loop
            this.mouseMove = false;
        }

        // Update chase co-ordinates
        if (this.chaseEntityId != null)
        {
            var ent = projectSandbox.entities.get(this.chaseEntityId);

            if (ent != null)
            {
                if (this.x != ent.x || this.y != ent.y || this.z != ent.z)
                {
                    // Update camera
                    this.setPosition(ent.x, ent.y, ent.z);
                }
            }
            else
            {
                console.warn("engine/camera - chase entity not found");
            }
        }
        else
        {
            console.warn("engine/camera - no chase entity setup");
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

        // Rebuild camera limits
        this.buildLimits();
    },

    buildLimits: function()
    {
        // Check if already built for current logic cycle
        var currentTime = projectSandbox.currentTime;

        if (this.limitsLastBuiltTime != currentTime)
        {
            this.limitsLastBuiltTime = currentTime;
        }

        var map = projectSandbox.map;

        // Check map is ready
        if (map == null || !map.isSetup())
        {
            console.warn("engine/camera - unable to build limits, map not setup");
            return;
        }

        var cameraZ = this.z + this.zoom;

        var mapTileSize = projectSandbox.map.tileSize;
        var mapWidth = map.getWidth();
        var mapHeight = map.getHeight();

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
    },

    /*
        Rendering uses the actual width/height of the canvas as the viewport size and ratio.

        The following were used in the past:
        - projectSandbox.gl.viewportWidth;
        - projectSandbox.gl.viewportHeight;
        - projectSandbox.gl.viewportWidth / projectSandbox.gl.viewportHeight;

        But this doesn't work for a dynamically sized canvas, and looked blurry. Although this does gain a few FPS,
        due to a smaller rendering area.
    */

    getRenderWidth: function()
    {
        return projectSandbox.rendering.core.getGl().drawingBufferWidth;
    },

    getRenderHeight: function()
    {
        return projectSandbox.rendering.core.getGl().drawingBufferHeight;
    },

    getRenderRatio: function()
    {
        return projectSandbox.rendering.core.getCanvas().clientWidth / projectSandbox.rendering.core.getCanvas().clientHeight;
    }

}
