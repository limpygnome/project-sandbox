game.ui.mapEditor = (function(){


    var handleMapReload = function() {
    };

    var handleMapClear = function() {
    };

    var handleMapSave = function() {
    };

    var handleEntitySelect = function(typeId) {
    };


    var setupButtonHooks = function() {
        // Hook map editor buttons
        $("#ps-map-editor .map-reload").on("click", handleMapReload);
        $("#ps-map-editor .map-clear").on("click", handleMapClear);
        $("#ps-map-editor .map-save").on("click", handleMapSave);
    };

    var setupEntitiesBox = function() {
        // Clear content
        $("#ps-map-editor .entities").html("");

        // Fetch all entities from entity-factory
        var types = game.entityFactory.typeMap;

        // Populate entities box with types
        types.forEach(function(type) {
            // Add element to box
            $("#ps-map-editor .entities").append("<span data-type-id='" + type.typeId + "' class='button'>" + type.title + "</span>");
        });
    };

    var setup = function() {
        setupButtonHooks();
        setupEntitiesBox();
    };

    // Invoke setup when game is ready...
    $(document).on("gameSetup", setup);

    return {
    };

})();
