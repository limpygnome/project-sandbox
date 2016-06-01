projectSandbox.rendering.core = function()
{
    // Rendering
    var canvas = null;
    var gl = null;
    var shaderProgram = null;

    // Matrices
    var modelView = mat4.create();
    var perspective = mat4.create();
    var cameraView = mat4.create();

    // FPS calculation
    var fps = 0;
    var fpsTime = (new Date).getTime();
    var fpsFrames = 0;

    var init = function(_canvas)
    {
        // Check we have canvas element
        canvas = _canvas;

        if (canvas == null)
        {
            console.error("engine/rendering/core - no canvas available");
            return false;
        }

        // Reset instance of webgl
        gl = null;

        try
        {
            // Setup WebGL
            gl = canvas.getContext("webgl", {alpha: true}) || canvas.getContext("experimental-webgl", {alpha: true});

            if (gl == null)
            {
                console.error("engine/rendering/core - failed to setup WebGL context (critical)");
                return false;
            }

            // Set default size to use canvas
            gl.viewportWidth = canvas.width;
            gl.viewportHeight = canvas.height;
        }
        catch(e)
        {
            console.error("engine/rendering/core - failed to setup WebGL context: " + e);
        }

        // Setup buffer cache
        projectSandbox.bufferCache.setup(gl);

        return true;
    };

    var postResourcesInit = function()
    {
        // Setup scene
        sceneSetup();

        // Setup shader program
        shaderProgram = projectSandbox.shaders.createDefaultTextureProgram(gl);

        // Setup texture manager
        projectSandbox.textures.setup(gl);

        // Setup lights
        projectSandbox.lights.init(gl, shaderProgram);
    };

    var sceneSetup = function()
    {
        gl.clearColor(0.0, 0.0, 0.0, 1.0);

        // Setup blending/alpha transparency
        gl.enable(gl.BLEND);
        gl.blendEquation(gl.FUNC_ADD);
        gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);

        // Disable depth buffer
        gl.depthMask(false);

        // Setup camera
        projectSandbox.camera.setup(canvas, gl);
    };

    var render = function()
    {
        var map = projectSandbox.map;
        var effects = projectSandbox.effects;
        var entities = projectSandbox.entities;
        var game = projectSandbox.game;

        // Reset scene
        // -- WARNING: if width/height changes, update frustrum culling since it uses it for calculating aspect ratio
        gl.viewport(0, 0, projectSandbox.camera.getRenderWidth(), projectSandbox.camera.getRenderHeight());
        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

        // Reset perspective matrix
        // -- fovy (vertical field of view)
        // -- aspect ratio
        // -- near bound of frustrum
        // -- far bound of frustrum
        var aspectRatio = projectSandbox.camera.getRenderRatio();

        mat4.perspective(
            perspective,

            projectSandbox.frustrum.FRUSTRUM_VERTICAL_FOV,
            aspectRatio,
            projectSandbox.frustrum.FRUSTRUM_DISTANCE_NEAR,
            projectSandbox.frustrum.FRUSTRUM_DISTANCE_FAR
        );

        // Reset matrices
        mat4.identity(modelView);
        mat4.identity(cameraView);

        // Perform camera render logic
        projectSandbox.camera.renderLogic(cameraView);

        // Update camera view matrix
        gl.uniformMatrix4fv(shaderProgram.uniformCameraViewMatrix, false, cameraView);

        // Render map
        if (map != null && map.isSetup())
        {
            map.render(gl, shaderProgram, modelView, perspective);
        }

        // Render effects
        var effect;
        for (var i = effects.length - 1; i >= 0; i--)
        {
            effect = effects[i];
            if (projectSandbox.frustrum.intersects(effect))
            {
                effect.render(gl, shaderProgram, modelView, perspective);
            }
        }

        // Render ents
        var ent;
        for (var kv of entities)
        {
            ent = kv[1];
            if (projectSandbox.frustrum.intersects(ent))
            {
                // Render entity
                ent.render(gl, shaderProgram, modelView, perspective);
            }
        }

        // Update FPS
        var currentTime = (new Date).getTime();
        if(currentTime - fpsTime >= 1000)
        {
            // Update counters
            fps = fpsFrames;
            fpsTime = currentTime;
            fpsFrames = 1;
        }
        else
        {
            // Update total frames for current second
            fpsFrames++;
        }

        // Render UI
        game.ui.controller.render(gl, shaderProgram, modelView, perspective);
    };

    var getGl = function()
    {
        return gl;
    };

    var getCanvas = function()
    {
        return canvas;
    };

    return {

        // Exposed functions
        init                : init,
        postResourcesInit   : postResourcesInit,
        render              : render,

        // Accessors
        getGl               : getGl,
        getCanvas           : getCanvas

    };

}();