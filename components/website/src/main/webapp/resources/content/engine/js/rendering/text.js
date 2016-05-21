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

    calculateCanvasSize: function(text, fontSize, blurSize)
    {
        var textSize = this.canvasTextContext.measureText(text);
        var textWidth = (textSize.width) + blurSize;
        var textHeight = (fontSize * 2.0) + blurSize;

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

        gl.bindTexture(gl.TEXTURE_2D, texture);

        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, this.canvasText);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_NEAREST);
        gl.generateMipmap(gl.TEXTURE_2D);

        gl.bindTexture(gl.TEXTURE_2D, null);

        return texture;
    },

    buildVertices: function(width, height, blurSize)
    {
        var vectorWidth = (width + blurSize) / this.canvasText.width;
        var vectorHeight = (height + blurSize) / this.canvasText.height;

        var frameData = new Float32Array(8);
        frameData[0] = 0.0;         frameData[1] = vectorHeight;
        frameData[2] = vectorWidth; frameData[3] = vectorHeight;
        frameData[4] = vectorWidth; frameData[5] = 0.0;
        frameData[6] = 0.0;         frameData[7] = 0.0;

        return frameData;
    },

    buildPrimitive: function(text, fontSize, colour, blurSize, blurColour, fontFace)
    {
        // Check blur size defined to avoid calculations resulting to NaN
        if (!blurSize)
        {
            blurSize = 0.0;
        }

        // Check font defined, or set default
        if (!fontFace)
        {
            fontFace = "arial";
        }

        // Setup font
        var font = fontSize + "px " + fontFace;
        this.canvasTextContext.font = font;

        // Re-setup canvas
        var canvasSize = this.calculateCanvasSize(text, fontSize, blurSize);

        this.canvasText.width = canvasSize[0];
        this.canvasText.height = canvasSize[1];

        // Set font and layout
        this.canvasTextContext.font = font;
        this.canvasTextContext.textBaseline = "top";

        // Set colour
        if (colour != null)
        {
            this.canvasTextContext.fillStyle = colour;
        }

        // Set blur
        if (blurSize != null && blurColour != null && blurSize > 0.0)
        {
            this.canvasTextContext.shadowColor = blurColour;
            this.canvasTextContext.shadowBlur = blurSize;
        }

        // Render text
        var x = blurSize;
        var y = 0.0;
        this.canvasTextContext.fillText(text, x, y);

        // Create texture / verts
        var textWidth = canvasSize[2];
        var textHeight = fontSize * 1.25;

        var texture = this.buildTexture();
        var textureVertices = this.buildVertices(textWidth, textHeight, blurSize);

        // Build texture src
        var textureSrc = new TextureSrc(null, null, textWidth, textHeight, texture);

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
        var primitive = new Primitive(textWidth, textHeight);
        primitive.setTextureRaw(primitiveTexture);

        // Set unique name for caching
        // TODO: build name here
        primitive.textName = "";

        return primitive;
    }
}
