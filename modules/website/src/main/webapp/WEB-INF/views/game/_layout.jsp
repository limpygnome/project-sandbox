<%@ taglib prefix="c"        uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles"    uri="http://tiles.apache.org/tags-tiles" %>

<!--
    Copyright &copy; limpygnome <c:out value="${copyright_year}"/>
-->
<!DOCTYPE html>
<html>
    <head>
        <title>
            Project Sandbox - Prototype
        </title>

        <link rel="stylesheet" href="/content/game/css/main.css" />
        <link rel="stylesheet" href="/content/game/css/main-anims.css" />
        <link rel="stylesheet" href="/content/game/css/weapons.css" />

        <!-- Third-Party -->
        <script src="/content/third-party/js/gl-matrix-min.js"></script>
        <script src="/content/third-party/js/jquery-1.11.3.min.js"></script>

        <!-- Main -->
        <script src="/content/engine/js/project-sandbox.js"></script>

        <!-- Engine -->
        <script src="/content/engine/js/_namespace.js"></script>
        <script src="/content/engine/js/_prototype_inheritence.js"></script>
        <script src="/content/engine/js/frustrum.js"></script>
        <script src="/content/engine/js/asset-loader.js"></script>
        <script src="/content/engine/js/buffer-cache.js"></script>
        <script src="/content/engine/js/camera.js"></script>
        <script src="/content/engine/js/shaders.js"></script>
        <script src="/content/engine/js/primitive.js"></script>
        <script src="/content/engine/js/primitive-bar.js"></script>
        <script src="/content/engine/js/entity.js"></script>
        <script src="/content/engine/js/effect.js"></script>
        <script src="/content/engine/js/trail.js"></script>
        <script src="/content/engine/js/texturesrc.js"></script>
        <script src="/content/engine/js/texture.js"></script>
        <script src="/content/engine/js/textures.js"></script>
        <script src="/content/engine/js/map.js"></script>
        <script src="/content/engine/js/keyboard.js"></script>
        <script src="/content/engine/js/mouse.js"></script>
        <script src="/content/engine/js/network.js"></script>
        <script src="/content/engine/js/utils.js"></script>
        <script src="/content/engine/js/inventory.js"></script>
        <script src="/content/engine/js/inventory-item.js"></script>
        <script src="/content/engine/js/text.js"></script>
        <script src="/content/engine/js/lighting/lights.js"></script>
        <script src="/content/engine/js/lighting/light.js"></script>

        <script src="/content/engine/js/players.js"></script>

        <script src="/content/engine/js/network/packet.js"></script>
        <script src="/content/engine/js/network/entities.js"></script>
        <script src="/content/engine/js/network/inventory.js"></script>
        <script src="/content/engine/js/network/map.js"></script>
        <script src="/content/engine/js/network/player.js"></script>
        <script src="/content/engine/js/network/session.js"></script>

        <script src="/content/engine/js/types/player.js"></script>

        <!-- Game -->
        <script src="/content/game/js/_namespace.js"></script>
        <script src="/content/game/js/effects.js"></script>
        <script src="/content/game/js/ui.js"></script>

        <!-- Game: Living -->
        <script src="/content/game/js/ent/living/player.js"></script>
        <script src="/content/game/js/ent/living/sentry.js"></script>
        <script src="/content/game/js/ent/living/pedestrian.js"></script>

        <!-- Game: Pickups -->
        <script src="/content/game/js/ent/pickup/abstract_pickup.js"></script>
        <script src="/content/game/js/ent/pickup/health.js"></script>

        <!-- Game: Vehicles -->
        <script src="/content/game/js/ent/vehicle/ice-cream-van.js"></script>
        <script src="/content/game/js/ent/vehicle/rocket-car.js"></script>
        <script src="/content/game/js/ent/vehicle/bus.js"></script>

        <!-- Game: Weapons -->
        <script src="/content/game/js/ent/weapon/rocket.js"></script>

        <!-- Game: Inventory -->
        <script src="/content/game/js/inventory/fist.js"></script>
        <script src="/content/game/js/inventory/smg.js"></script>

    </head>
    <body>

        <c:if test="${content_header}">
            <h2>
                <c:out value="${title}"/>
            </h2>
        </c:if>

        <tiles:insertAttribute name="content" />

    </body>
</html>
