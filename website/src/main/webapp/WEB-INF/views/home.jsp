<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="projectsandbox">

    <div id="ps-chat">
        <h3>
            Chat
        </h3>
        <p>

            message
        </p>
    </div>

    <div id="ps-activity">
        <h3>
            Activity
        </h3>
    </div>

    <div id="ps-render-container">
        <canvas id="ps_render" width="800" height="600">
            <p>
                No support for HTML5 canvas.
            </p>
        </canvas>

        <canvas id="ps_render_text" width="1" height="1">
            <p>
                No support for HTML5 canvas.
            </p>
        </canvas>

        <div id="ps-death-screen" tabindex="1">
            <div class="container">
                <div class="message">
                    <div class="wrekt">
                        #wrekt
                    </div>
                    <div id="ps-death-screen-cause" class="cause">
                        killed by unknown causes
                    </div>
                </div>
                <div class="continue">
                    ~ press space to close ~
                </div>
            </div>
        </div>
    </div>

    <p>
        FPS: <span id="ps_fps">--</span>
    </p>

    <div class="clear"></div>
</div>



<script>
    projectSandbox.init();
</script>
