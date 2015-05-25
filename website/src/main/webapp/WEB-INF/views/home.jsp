<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="projectsandbox">

    <div class="ps-sidebar left">

        <div class="container">
            <h3>
                Activity
            </h3>
            <div id="ps-activity">
                <p>
                    activity
                </p>
            </div>
        </div>

        <div class="container">
            <h3>
                Chat
            </h3>
            <div id="ps-chat">

                <div id="ps-chat-box">
                    <input type="text" placeholder="Enter message here..." />
                </div>

                <div id="ps-chat-messages">
                    <p>
                        <img class="thumb" src="" />
                        whatever: message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                    <p>
                        <img class="thumb" src="" />
                        whatever: message mesage mssage message message message
                    </p>
                </div>

            </div>
        </div>

    </div>

    <div class="ps-sidebar right">

        <div class="container">
            <h3>
                Scoreboard
            </h3>
            <div id="ps-scoreboard">
                <ol>
                    <li>
                        <span>
                            1000
                        </span>

                        test #1
                    </li>
                    <li>
                        <span>
                            448
                        </span>

                        test #2
                    </li>
                </ol>
            </div>
        </div>

        <div class="container">
            <h3>
                Options
            </h3>
            <div id="ps-options">
                <p>
                    none
                </p>
            </div>
        </div>

    </div>

    <div id="ps-render-container">

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

        <p>
            FPS: <span id="ps_fps">--</span>
        </p>

    </div>

    <div class="clear"></div>
</div>



<script>
    projectSandbox.init();
</script>
