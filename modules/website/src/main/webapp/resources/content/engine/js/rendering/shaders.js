projectSandbox.shaders =
{
    createDefaultTextureProgram: function(gl)
    {
        var fragmentShader = projectSandbox.assetLoader.get("/content/game/shaders/default-texture.frag");
        var vertexShaderSrc = projectSandbox.assetLoader.get("/content/game/shaders/default-texture.vert");
        
        return this.createProgram(gl, fragmentShader, vertexShaderSrc);
    },
    
    createProgram: function(gl, dataFragment, dataVertex)
    {
        // Compile shaders
        var shaderFragment = this.createFragment(gl, dataFragment);
        var shaderVertex = this.createVertex(gl, dataVertex);
        
        // Create program, attach and link
        var shaderProgram = gl.createProgram();
        gl.attachShader(shaderProgram, shaderVertex);
        gl.attachShader(shaderProgram, shaderFragment);
        gl.linkProgram(shaderProgram);
        
        if (!gl.getProgramParameter(shaderProgram, gl.LINK_STATUS)) {
            console.log("Failed to setup shader program.");
        }
        
        // Use the program
        gl.useProgram(shaderProgram);

        // Map fields
        this.createProgram_mapVariables(gl, shaderProgram);

        // Map uniform matrices
        this.createProgram_mapUniforms(gl, shaderProgram);

        return shaderProgram;
    },

    createProgram_mapVariables: function(gl, shaderProgram)
    {
        // Vertex position vector (array)
        shaderProgram.vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "aVertexPosition");
        gl.enableVertexAttribArray(shaderProgram.vertexPositionAttribute);

        // Colour vector (array)
        shaderProgram.vertexColourAttribute = gl.getAttribLocation(shaderProgram, "aColour");
        gl.disableVertexAttribArray(shaderProgram.vertexColourAttribute);

        // Texture co-ordinate vector (array)
        shaderProgram.textureCoordAttribute = gl.getAttribLocation(shaderProgram, "aTextureCoord");
        gl.enableVertexAttribArray(shaderProgram.textureCoordAttribute);

        // Normal vector (array)
        shaderProgram.normalsAttribute = gl.getAttribLocation(shaderProgram, "aNormals");
        gl.enableVertexAttribArray(shaderProgram.normalsAttribute);

        // Camera position (vec3)
        shaderProgram.cameraPosition = gl.getAttribLocation(shaderProgram, "aCameraPosition");
        gl.disableVertexAttribArray(shaderProgram.cameraPosition);

        // -- Set initial empty buffer
        var cameraPositionInitialBuffer = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, cameraPositionInitialBuffer);
        gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([ 0.0, 0.0, 0.0 ]), gl.STATIC_DRAW);
        gl.bindBuffer(gl.ARRAY_BUFFER, null);

        // -- Note: lights within shader are mapped by light.js in engine
    },

    createProgram_mapUniforms: function(gl, shaderProgram)
    {
        // Projection matrix
        shaderProgram.pMatrixUniform = gl.getUniformLocation(shaderProgram, "uPMatrix");

        // Model matrix
        shaderProgram.mvMatrixUniform = gl.getUniformLocation(shaderProgram, "uMVMatrix");

        // Normals matrix
        shaderProgram.nMatrixUniform = gl.getUniformLocation(shaderProgram, "uNMatrix");

        // Camera view matrix
        shaderProgram.uniformCameraViewMatrix = gl.getUniformLocation(shaderProgram, "uniformCameraViewMatrix");

        // Texture sampling
        shaderProgram.samplerUniform = gl.getUniformLocation(shaderProgram, "uSampler");
    },
        
    createFragment: function(gl, data)
    {
        return this.create(gl, gl.FRAGMENT_SHADER, data);
    },
    
    createVertex: function(gl, data)
    {
        return this.create(gl, gl.VERTEX_SHADER, data);
    },
    
    create: function(gl, type, data)
    {
        // Create and compile
        var shader = gl.createShader(type);
        gl.shaderSource(shader, data);
        gl.compileShader(shader);
        
        // Check for error
        if(!gl.getShaderParameter(shader, gl.COMPILE_STATUS))
        {
            console.log("Failed to compile shader - error: '" + gl.getShaderInfoLog(shader) + "', data: '" + data + "'");
        }
        
        return shader;
    }
}
