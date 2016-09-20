projectSandbox.interaction.keyboard =
{
    keys: new Map(),
    
    hook: function()
    {
        var self = this;

        // Fetch render container
        var renderContainer = $("#ps-render-container");

        // Hook key down...
        $(renderContainer).keydown(function (event) {
            self.handlerDown(event);
        });

        // Hook key up...
        $(renderContainer).keyup(function (event) {
            self.handlerUp(event);
        });
    },
    
    handlerDown: function(event)
    {
        if (projectSandbox.interaction.shared.isRenderArea(event))
        {
            var keyCode = event.keyCode;
            this.setKeyCode(keyCode, true);
        }
    },
    
    handlerUp: function(event)
    {
        if (projectSandbox.interaction.shared.isRenderArea(event))
        {
            var keyCode = event.keyCode;
            this.setKeyCode(keyCode, false);
        }
    },
    
    setKeyCode: function(keyCode, value)
    {
        var key;

        // Convert key code to easily readable value for print chars
        if (keyCode >= 32 && keyCode <= 126)
        {
            key = String.fromCharCode(keyCode);

            // Fallback...
            if (key == null || key.length == 0)
            {
                key = keyCode;
            }
        }
        else
        {
            key = keyCode;
        }

        // Update state of key in map
        this.keys.set(key, value);

        console.debug("projectSandbox.interaction.keyboard - key: " + key + " [key code: '" + keyCode + "'], value: " + value);
    },

    isKeyDown: function(keyCode)
    {
        return this.keys.get(keyCode);
    }
}
