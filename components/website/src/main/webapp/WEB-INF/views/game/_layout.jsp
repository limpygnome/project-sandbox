<%@ taglib prefix="c"        uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles"    uri="http://tiles.apache.org/tags-tiles" %>

<!--
    Copyright &copy; limpygnome <c:out value="${copyright_year}"/>
-->
<!DOCTYPE html>
<html>
    <head>
        <title>
            Project Sandbox
        </title>

        <!--
            Prevent page zooming/scaling on mobile devices

            This prevents page from being zoomed when exiting full-screen mode.
        -->
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0" />

        <!--
            CSS
            ************************************************************************************************************
        -->

        <link rel="stylesheet" href="/content/game/css/main.css" />
        <link rel="stylesheet" href="/content/game/css/main-anims.css" />
        <link rel="stylesheet" href="/content/game/css/weapons.css" />

        <!-- Overlays -->
        <link rel="stylesheet" href="/content/game/css/overlay/shared.css" />
        <link rel="stylesheet" href="/content/game/css/overlay/connecting.css" />
        <link rel="stylesheet" href="/content/game/css/overlay/death-screen.css" />
        <link rel="stylesheet" href="/content/game/css/overlay/ui.css" />

        <!-- UI -->
        <link rel="stylesheet" href="/content/game/css/ui/map.css" />
        <link rel="stylesheet" href="/content/game/css/ui/activity.css" />
        <link rel="stylesheet" href="/content/game/css/ui/chat.css" />
        <link rel="stylesheet" href="/content/game/css/ui/fps.css" />
        <link rel="stylesheet" href="/content/game/css/ui/health-bar.css" />
        <link rel="stylesheet" href="/content/game/css/ui/inventory.css" />
        <link rel="stylesheet" href="/content/game/css/ui/options.css" />
        <link rel="stylesheet" href="/content/game/css/ui/score.css" />
        <link rel="stylesheet" href="/content/game/css/ui/scoreboard.css" />

        <!-- Font Icons -->
        <link rel="stylesheet" href="/content/game/fonts/projectsandbox.css" />

        <!--
            JavaScript
            ************************************************************************************************************
        -->

        <!--
            Third-party
            ------------------------------------------------------------------------------------------------------------
        -->
        <script src="/content/third-party/js/gl-matrix-min.js"></script>
        <script src="/content/third-party/js/jquery-1.11.3.min.js"></script>

        <!--
            Engine
            ------------------------------------------------------------------------------------------------------------
        -->

        <script src="/content/engine/js/project-sandbox.js"></script>

        <!-- Prototype -->
        <script src="/content/engine/js/_namespace.js"></script>
        <script src="/content/engine/js/_prototype_inheritence.js"></script>

        <!-- Rendering -->
        <script src="/content/engine/js/rendering/frustrum.js"></script>
        <script src="/content/engine/js/rendering/buffer-cache.js"></script>
        <script src="/content/engine/js/rendering/shaders.js"></script>
        <script src="/content/engine/js/rendering/primitive.js"></script>
        <script src="/content/engine/js/rendering/primitive-bar.js"></script>
        <script src="/content/engine/js/rendering/entity.js"></script>
        <script src="/content/engine/js/rendering/effect.js"></script>
        <script src="/content/engine/js/rendering/trail.js"></script>
        <script src="/content/engine/js/rendering/text.js"></script>

        <!-- World -->
        <script src="/content/engine/js/world/camera.js"></script>
        <script src="/content/engine/js/world/map.js"></script>
        <script src="/content/engine/js/world/map-open.js"></script>
        <script src="/content/engine/js/world/map-tiles.js"></script>

        <!-- Lighting -->
        <script src="/content/engine/js/lighting/lights.js"></script>
        <script src="/content/engine/js/lighting/light.js"></script>

        <!-- Textures -->
        <script src="/content/engine/js/textures/texturesrc.js"></script>
        <script src="/content/engine/js/textures/texture.js"></script>
        <script src="/content/engine/js/textures/textures.js"></script>

        <!-- Interaction -->
        <script src="/content/engine/js/interaction/shared.js"></script>
        <script src="/content/engine/js/interaction/keyboard.js"></script>
        <script src="/content/engine/js/interaction/mouse.js"></script>

        <!-- Inventory -->
        <script src="/content/engine/js/inventory/inventory.js"></script>
        <script src="/content/engine/js/inventory/inventory-item.js"></script>

        <!-- Network -->
        <script src="/content/engine/js/network/asset-loader.js"></script>
        <script src="/content/engine/js/network/network.js"></script>
        <script src="/content/engine/js/network/inbound-packet.js"></script>
        <script src="/content/engine/js/network/outbound-packet.js"></script>

        <!-- Network: Components -->
        <script src="/content/engine/js/network/components/entities.js"></script>
        <script src="/content/engine/js/network/components/entity-pool.js"></script>
        <script src="/content/engine/js/network/components/inventory.js"></script>
        <script src="/content/engine/js/network/components/world/map.js"></script>
        <script src="/content/engine/js/network/components/world/map-open.js"></script>
        <script src="/content/engine/js/network/components/world/map-tiles.js"></script>
        <script src="/content/engine/js/network/components/player.js"></script>
        <script src="/content/engine/js/network/components/session.js"></script>

        <!-- Types -->
        <script src="/content/engine/js/types/player.js"></script>

        <!-- Misc / unsorted -->
        <script src="/content/engine/js/utils.js"></script>
        <script src="/content/engine/js/players.js"></script>

        <!--
            Game
            ------------------------------------------------------------------------------------------------------------
        -->
        <script src="/content/game/js/_namespace.js"></script>
        <script src="/content/game/js/effects.js"></script>
        <script src="/content/game/js/entity-factory.js"></script>
        <script src="/content/game/js/ui/controller.js"></script>
        <script src="/content/game/js/ui/map.js"></script>

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

        <!-- Game: Ships -->
        <script src="/content/game/js/ent/ships/fighter.js"></script>
        <script src="/content/game/js/ent/ships/destroyer.js"></script>

        <!-- Game: Weapons -->
        <script src="/content/game/js/ent/weapon/rocket.js"></script>

        <!-- Game: World -->
        <script src="/content/game/js/ent/world/blackhole.js"></script>

        <!-- Game: Inventory -->
        <script src="/content/game/js/inventory/fist.js"></script>
        <script src="/content/game/js/inventory/gatling.js"></script>

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
