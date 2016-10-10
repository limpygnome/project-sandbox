game.ui.mapEditor.controls = (function(){

    var network = game.ui.mapEditor.network;

    var handleMapReload = function()
    {
        var payload = {
            action: "map-reload"
        };
        network.sendData(payload);
        return true;
    };

    var handleMapClear = function()
    {
        var payload = {
            action: "map-clear"
        };
        network.sendData(payload);
        return true;
    };

    var handleMapSave = function()
    {
        var payload = {
            action: "map-save"
        };
        network.sendData(payload);
        return true;
    };

    var handleEntitySelect = function(typeId)
    {
        var payload = {
            action: "entity-select",
            typeId: typeId
        };

        network.sendData(payload);
        return true;
    };

    var setupButtonHooks = function()
    {
        // Hook map editor buttons
        $("#ps-map-editor .map-reload").off("click.map-reload").on("click.map-reload", handleMapReload);
        $("#ps-map-editor .map-clear").off("click.map-clear").on("click.map-clear", handleMapClear);
        $("#ps-map-editor .map-save").off("click.map-save").on("click.map-save", handleMapSave);
    };

    var setupEntitiesBox = function()
    {
        // Clear content
        $("#ps-map-editor .entities").html("");

        // Fetch all entities from entity-factory
        var types = game.entityFactory.typeMap;

        // Populate entities box with types
        types.forEach(function(type)
        {
            if (type.mapEditorEnabled)
            {
                // Add button, to box, to change selected type
                $("#ps-map-editor .entities").append("<span data-type-id='" + type.typeId + "' class='button'>" + type.title + "</span>");

                // Hook new button
                // TODO: jquery bug whereby touchpad click is not working; proven by addeventlistener working with click
                $("#ps-map-editor .entities .button[data-type-id=" + type.typeId + "]").on("click", function(){
                    return handleEntitySelect(type.typeId);
                });
            }
        });
    };

    var setup = function()
    {
        setupButtonHooks();
        setupEntitiesBox();
    };

    // Invoke our setup when game is ready...
    $(document).on("gameSetup", setup);

    return {
    };

})();
