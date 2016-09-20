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
        // Fetch all entities from entity-factory

        // Populate entities box with element to select them
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
