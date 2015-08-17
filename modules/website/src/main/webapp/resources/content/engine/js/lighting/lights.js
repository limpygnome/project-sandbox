projectSandbox.lights =
{
    // This constant must also be set in the shader
    LIGHTS_LIMIT: 512,

    lights: null,

    reset: function(gl, shaderProgram)
    {
        this.lights = new Array();

        // Setup new lights
        var light;
        for (var i = 0; i < LIGHTS_LIMIT; i++)
        {
            light = new Light(gl, shaderProgram, i);
            lights.push(light);
        }

        console.debug("engine/lights - lights reset");
    },

    fetch: function(light)
    {
        // Fetch first light available
        if (lights.length > 0)
        {
            var light = lights.splice(0, 1);

            console.debug("engine/lights - light taken from pool - index: " + light.index);

            return light;
        }
        else
        {
            return null;
        }
    },

    dispose: function(light)
    {
        // Add back to pool
        light.reset();
        lights.push(light);

        console.debug("engine/lights - light added to pool - index: " + light.index);
    }

}
