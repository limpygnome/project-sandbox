projectSandbox.interaction.mouse =
{
    // Indicates if a button is down
    left: false,
    right: false,
    
    // Indicates cursor position in canvas
    x: 0,
    y: 0,
    
    // Indicates scroll-wheel delta
    scrollDelta: 0,
    
    hook: function()
    {
        var self = this;

        // Fetch render container
        var renderContainer = $("#ps-render-container");

        // Disable context menu for main canvas, just so we can use right click
        $(renderContainer).bind("contextmenu", function(e) {
            return self.handleContextMenu(e.originalEvent);
        });

        // Hook mouse-down
        $(renderContainer).bind("mousedown", function(e) {
            return self.handleMouseDown(e.originalEvent);
        });

        // Hook mouse-up
        $(renderContainer).bind("mouseup", function(e) {
            return self.handleMouseUp(e.originalEvent);
        });

        // Hook mouse-up
        $(renderContainer).bind("mousemove", function(e) {
            return self.handleMouseMove(e.originalEvent);
        });

        // Hook mouse scroll wheel
        $(renderContainer).bind("mousewheel", function(e) {
            return self.handleMouseWheelEvent(e.originalEvent);
        });
    },

    handleContextMenu: function(event)
    {
        if (projectSandbox.interaction.shared.isRenderArea(event))
        {
            console.debug("engine / mouse - disabled context menu, clicked on render element");
            return false;
        }
    },
    
    handleMouseDown: function(event)
    {
        if (projectSandbox.interaction.shared.isRenderArea(event))
        {
            // Focus on render container
            projectSandbox.interaction.shared.focusRenderArea();

            // Handle event
            switch (event.button)
            {
                case 0:
                    this.left = true;
                    console.debug("engine / mouse - mouse down - left")
                    break;
                case 2:
                    this.right = true;
                    console.debug("engine / mouse - mouse down - right")
                    break;
            }

            return false;
        }
    },

    handleMouseUp: function(event)
    {
        if (projectSandbox.interaction.shared.isRenderArea(event))
        {
            switch (event.button)
            {
                case 0:
                    this.left = false;
                    console.debug("engine / mouse - mouse up - left")
                    break;
                case 2:
                    this.right = false;
                    console.debug("engine / mouse - mouse up - right")
                    break;
            }

            return false;
        }
    },
    
    handleMouseMove: function(event)
    {
        // Don't log anything here, could impact performance...
        this.x = event.x;
        this.y = event.y;
    },
    
    handleMouseWheelEvent: function(event)
    {
        this.scrollDelta = event.wheelDelta;
        return false;
    }
}