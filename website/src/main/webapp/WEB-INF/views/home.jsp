<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
</div>



<script>
    projectSandbox.init();
</script>
