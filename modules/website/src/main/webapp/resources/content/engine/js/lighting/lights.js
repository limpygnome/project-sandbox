projectSandbox.lights =
{
    // This constant must also be set in the shader, in
    // two places: size of uniform array, limit in for-loop
    LIGHTS_LIMIT: 1,

    lights: null,

    init: function()
    {
        var gl = projectSandbox.gl;
        var shaderProgram = projectSandbox.shaderProgram;

        console.debug("engine/lights - initializing...");

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

        console.debug("engine/lights - setup complete - " + this.LIGHTS_LIMIT + " lights");
    },

    reset: function()
    {
        console.debug("engine/lights - resetting...");

        // Setup new lights
        var light;
        for (var i = 0; i < this.LIGHTS_LIMIT; i++)
        {
            light = this.lights[i];
            light.reset();
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
