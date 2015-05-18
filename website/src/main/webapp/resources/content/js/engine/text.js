projectSandbox.text =
{
    canvasText: null,
    canvasTextContext: null,

    setup: function()
    {
        // Setup canvas rendering
        this.canvasText = document.getElementById("ps_render_text");
        this.canvasTextContext = this.canvasText.getContext("2d");
    },

    calculateCanvasSize: function(text, fontSize)
    {
        var textSize = this.canvasTextContext.measureText(text);
        var textWidth = textSize.width;
        var textHeight = fontSize * 2.0;

        // Build result
        var canvasSize = new Array();

        canvasSize[0] = this.calculateClosestPowerOfTwo(textWidth);
        canvasSize[1] = this.calculateClosestPowerOfTwo(textHeight);
        canvasSize[2] = textWidth;
        canvasSize[3] = textHeight;

        return canvasSize;
    },

    calculateClosestPowerOfTwo: function(value)
    {
        var exp = Math.ceil(
            Math.log(value) / Math.log(2)
        );

        return Math.pow(2, exp);
    },

    buildTexture: function()
    {
        var gl = projectSandbox.gl;

        var texture = gl.createTexture();

        gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
        gl.bindTexture(gl.TEXTURE_2D, texture);
        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, this.canvasText);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_NEAREST);
        gl.generateMipmap(gl.TEXTURE_2D);

        gl.bindTexture(gl.TEXTURE_2D, null);
        gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, false);

        return texture;
    },

    buildVertices: function(width, height)
    {
        var vectorWidth = width / this.canvasText.width;
        var vectorHeight = height / this.canvasText.height;

        // TODO: is 0.0 the right away? isnt 0.0 at the bottom left?

        var frameData = new Float32Array(8);
        frameData[0] = 0.0;         frameData[1] = 0.0;
        frameData[2] = vectorWidth; frameData[3] = 0.0;
        frameData[4] = vectorWidth; frameData[5] = vectorHeight;
        frameData[6] = 0.0;         frameData[7] = vectorHeight;

        return frameData;
    },

    buildPrimitive: function(text, fontSize)
    {
        // Setup font
        var font = fontSize + "px serif";
        this.canvasTextContext.font = font;

        // Re-setup canvas
        var canvasSize = this.calculateCanvasSize(text, fontSize);

        this.canvasText.width = canvasSize[0];
        this.canvasText.height = canvasSize[1];
        this.canvasTextContext.font = font;

        // Render text
        this.canvasTextContext.fillText(text, 0.0, fontSize);

        // Create texture / verts
        var textureWidth = canvasSize[2];
        var textureHeight = canvasSize[3];

        var texture = this.buildTexture();
        var textureVertices = this.buildVertices(textureWidth, textureHeight);

        // Build texture src
        var textureSrc = new TextureSrc(null, null, textureWidth, textureHeight, texture);

        // Build texture (obj)
        var primitiveTexture = new Texture(
            textureSrc,
            null,
            -1,
            1,
            4,
            textureVertices
        );

        // Create primitive
        var primitive = new Primitive(textureWidth, textureHeight);
        primitive.setTextureRaw(primitiveTexture);
        //primitive.setTexture("error");


        return primitive;
    }
}
