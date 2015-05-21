<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<p>
    test
</p>
<div id="projectsandbox">
    <canvas id="ps_render" width="800" height="600">
        <p>
            No support for HTML5 canvas.
        </p>
    </canvas>
    <p>
        FPS: <span id="ps_fps">--</span>
    </p>
    <canvas id="ps_render_text" width="1" height="1">
        <p>
            No support for HTML5 canvas.
        </p>
    </canvas>
    <div id="ps-death-screen" tabindex="1">
        <div class="message">
            <div class="wrekt">
                #wrekt
            </div>
            <div id="ps-death-screen-cause" class="cause">
                killed by unknown causes
            </div>
        </div>
        <div class="continue">
            ~ press any key to close ~
        </div>
    </div>
</div>



<script>
    projectSandbox.init();
</script>
