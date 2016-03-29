<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
    projectSandbox.sessionId = "<c:out value="${game_session_token}" />";
</script>

<div id="projectsandbox">

    <div class="mobile-warning">
        Displaying in mobile mode
    </div>

    <div id="ps-render-container" tabindex="1" class="clickable">

        <div id="ps-connecting" class="clickable">
            <div class="message">
                connecting...
            </div>
        </div>

        <div id="ps-death-screen" tabindex="1" class="clickable">
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

        <div id="ps-ui" class="clickable">

            <div id="ps-scoreboard" class="panel">
                <h1>
                    Scores
                </h1>
                <ol>
                    <li>
                        <span class="current-player">0</span>
                        Waiting for server...
                    </li>
                </ol>
            </div>

            <div id="ps-activity" class="panel top">
                <div id="ps-activity-items">
                    <p>
                        <span class="left">
                            <img src="" />
                        </span>
                        <span class="info">
                            Waiting for server...
                        </span>
                        <span class="right">
                            <img src="" />
                        </span>
                    </p>
                </div>
            </div>

            <div id="ps-chat" class="panel top">
                <div id="ps-chat-messages">
                    <p>
                        <img class="thumb" src="" />
                        Waiting for server...
                    </p>
                </div>
                <div id="ps-chat-box">
                    <input type="text" tabindex="2" placeholder="Enter message here..." id="ps-chat-box-field" />
                </div>
            </div>

            <div id="ps-healthbar">
                <div id="ps-healthbar-fill">&nbsp;</div>
                50%
            </div>

            <div id="ps-score">
                --
            </div>

            <div id="ps-fps">
                FPS: <span id="ps-fps-value">--</span>
            </div>

            <div id="ps-inventory-container">
                <div id="ps-inventory" class="clickable">
                </div>
            </div>

            <div id="ps-options" class="top">
                <div id="button-fullscreen" class="button fullscreen">
                    <span class="icon-enlarge"></span>
                </div>
                <a class="button" href="/">
                    <span class="icon-enter"></span>
                </a>
            </div>

        </div>

        <canvas id="ps_render" width="1024" height="768" class="clickable">
            <p>
                No support for HTML5 canvas.
            </p>
        </canvas>

        <canvas id="ps_render_text" width="1" height="1">
            <p>
                No support for HTML5 canvas.
            </p>
        </canvas>

    </div>

</div>

<script>
    projectSandbox.init();
</script>
