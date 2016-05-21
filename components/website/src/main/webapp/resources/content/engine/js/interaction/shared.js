projectSandbox.interaction.shared =
{
    hook: function()
    {
        // Hook mouse and keyboard
        projectSandbox.interaction.mouse.hook();
        projectSandbox.interaction.keyboard.hook();

        // Set initial focus to render area
        this.focusRenderArea();
    },

    isRenderArea: function(event)
    {
        var target = event.target;

        // Check if target has class 'clickable', which indicates the element can register interaction events
        // from mouse or keyboard
        return $(target).hasClass("clickable");
    },

    focusRenderArea: function()
    {
        $("#ps-render-container").focus();
    }

}
