projectSandbox.lights =
{
    // This constant must also be set in the shader, in
    // two places: size of uniform array, limit in for-loop
    LIGHTS_LIMIT: 16,

    lights: null,

    init: function(gl, shaderProgram)
    {
        console.debug("engine/lighting/lights - initializing...");

        // Setup lights array
        this.lights = new Array();

        shaderProgram.lights = { };

        // Create each light
        var light;
        for (var i = 0; i < this.LIGHTS_LIMIT; i++)
        {
            light = new Light(gl, shaderProgram, i);
            this.lights.push(light);
        }

        console.debug("engine/lighting/lights - setup complete - " + this.LIGHTS_LIMIT + " lights");
    },

    reset: function()
    {
        console.debug("engine/lighting/lights - resetting...");

        // Setup new lights
        var light;
        for (var i = 0; i < this.LIGHTS_LIMIT; i++)
        {
            light = this.lights[i];
            light.reset();
        }

        console.debug("engine/lighting/lights - lights reset");
    },

    fetch: function(light)
    {
        // Fetch first light available
        if (lights.length > 0)
        {
            var light = lights.splice(0, 1);

            console.debug("engine/lighting/lights - light taken from pool - index: " + light.index);

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

        console.debug("engine/lighting/lights - light added to pool - index: " + light.index);
    }

}
