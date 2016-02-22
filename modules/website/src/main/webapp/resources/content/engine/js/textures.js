projectSandbox.textures =
{
    textures: new Map(),
    src: new Map(),
    emptyTexture: null,
    
    flagColourDisabled: false,
    
    setup: function(gl)
    {
        var gl = projectSandbox.gl;
        
        // Fetch empty texture
        this.emptyTexture = this.textures.get("white");
    },
    
    get: function(name)
    {
        return this.textures.get(name);
    },
    
    getSrc: function(name)
    {
        return this.src.get(name);
    },
    
    logic: function()
    {
        // Execute logic for each texture
        var texture;
        for(var kv of this.textures)
        {
            texture = kv[1];
            texture.logic();
        }
    },
    
    reset: function()
    {
        this.textures.clear();
        this.src.clear();
    },
    
    loadTextureJson: function(json)
    {
        var self = projectSandbox.textures;
    
        // Read src values
        var name = json["name"];
        var url = json["url"]
        var width = json["width"];
        var height = json["height"];
        
        // Create src entry
        var src = new TextureSrc(name, url, width, height);
        self.src.set(name, src);
        console.log("Textures - added texture src '" + name + "'");
        
        // Parse textures
        var textures = json["textures"];
        var textureData;
        var texture;
        
        for (var i = 0; i < textures.length; i++)
        {
            // Fetch JSON element
            textureData = textures[i];
            
            // Create texture
            texture = self.buildTexture(src, textureData);
            
            // Add to collection
            if (texture != null)
            {
                self.textures.set(texture.name, texture);
                console.log("Textures - added texture '" + texture.name + "' - " + texture.frames + " frames, " + texture.frameVertices + " vertices");
            }
        }
    },
    
    isLoaded : function()
    {
        var self = projectSandbox.textures;
        
        // Check for the first false
        for (var i = 0; i < self.srcLoaded.length; i++)
        {
            if (self.srcLoaded[i] == false)
            {
                return false;
            }
        }
        
        return true;
    },
    
    buildTexture : function(src, textureData)
    {
        var self = projectSandbox.textures;

        // Load properties from texture
        var name = textureData["name"];
        var speed = textureData["speed"];
        var type = textureData["type"];
        var frames = textureData["frames"];
        
        // Build framedata - convert each frame into array of vertices
        // -- Check we have at least one frame
        if (frames.length == 0)
        {
            console.error("Textures - cannot build texture - no frame data available - name: " + name);
            return;
        }

        // Parse based on type
        var framesResult;

        switch (type)
        {
            case "generic":
                framesResult = self.parseFramesTypeGeneric(frames, src);
                break;
            case "2d":
                framesResult = self.parseFrameType2D(frames, src);
                break;
            case "3d":
                framesResult = self.parseFrameType3D(frames, src);
                break;
            default:
                console.error("engine / textures - cannot build texture - no type specified");
                return;
        }
    
        // Create texture from element
        return new Texture(
            src,
            name,
            speed,
            frames.length,
            framesResult.vertices,
            framesResult.frameData
        );
    },

    parseFramesTypeGeneric: function(frames, src)
    {
        // -- Check number of vertices per frame from first item
        // -- -- Note: this is actually not verts, but total co-ordinates - divide by 2 for total verts
        var expectedVertsPerFrame = frames[0].length;

        if (expectedVertsPerFrame == 0)
        {
            console.error("Textures - cannot build texture - no vertex data - name: " + name);
            return;
        }
        // TODO: consider if this check is actually needed anymore, can probably drop it...
        else if (expectedVertsPerFrame % 8 != 0)
        {
            console.error("Textures - cannot build texture - must be multiples of 8 vertices - name: " + name);
            return;
        }

        // -- frames * 8 floats (4 verts)
        var frameData = new Float32Array(frames.length * expectedVertsPerFrame);
        var frameDataOffset = 0;
        var frame;
        var frameVertices;
        var vert;

        for (var f = 0; f < frames.length; f++)
        {
            frame = frames[f];
            frameVertices = frame.length;

            // Check frame has correct number of vertices
            if (frameVertices != expectedVertsPerFrame)
            {
                console.error("Textures - cannot build texture - differing number of vertices for frame " + f + " - name: " + name);
                return;
            }

            for (var v = 0; v < frameVertices; v++)
            {
                // Compute co-ordinate into unit vector
                if (v % 2 == 0)
                {
                    vert = frame[v] / src.width;
                }
                else
                {
                    // Texture system starts from 0,0 bottom-left, but we start 0,0 top-left, so subtract value
                    // from 1 to invert y axis
                    vert = frame[v] / src.height;
                }

                // Set vertex co-ordinate
                frameData[frameDataOffset++] = vert;
            }
        }

        return {
                    "vertices" : expectedVertsPerFrame / 2,
                    "frameData" : frameData
               };
    },

    parseFrameType2D : function(frames, src)
    {
        var self = projectSandbox.textures;

        var coordinatesPerFrame = 8; // 8 per face / 4 pairs of XY
        var frameData = new Float32Array(frames.length * coordinatesPerFrame);

        for (var f = 0; f < frames.length; f++)
        {
            frame = frames[f];

            // Compute frame data
            self.buildFrameDataFourVerts(
                frameData, f * coordinatesPerFrame,
                src,
                frame["x"], frame["y"], frame["width"], frame["height"]
            );
        }

        return {
                    "vertices" : coordinatesPerFrame / 2,
                    "frameData" : frameData
               };
    },

    parseFrameType3D : function(frames, src)
    {
        var self = projectSandbox.textures;

        var coordinatesPerFace = 8;
        var faces = 5;

        var frameData = new Float32Array(frames.length * coordinatesPerFace * faces);
        var frameDataOffset = 0;

        for (var f = 0; f < frames.length; f++)
        {
            frame = frames[f];

            // Compute frame data
            // -- Top
            self.buildFrameDataFourVerts(
                frameData,
                frameDataOffset,
                src,
                frame["top"]["x"], frame["top"]["y"], frame["top"]["width"], frame["top"]["height"]
            );
            frameDataOffset += coordinatesPerFace;

            // NOT IMPLEMENTED IN BUFFER CACHE AT PRESENT:
            // -- Bottom
//            self.buildFrameDataFourVerts(
//                frameData,
//                frameDataOffset,
//                src,
//                frame["bottom"]["x"], frame["bottom"]["y"], frame["bottom"]["width"], frame["bottom"]["height"]
//            );
//            frameDataOffset += coordinatesPerFace;

            // -- North
            self.buildFrameDataFourVerts(
                frameData,
                frameDataOffset,
                src,
                frame["north"]["x"], frame["north"]["y"], frame["north"]["width"], frame["north"]["height"]
            );
            frameDataOffset += coordinatesPerFace;

            // -- East
            self.buildFrameDataFourVerts(
                frameData,
                frameDataOffset,
                src,
                frame["east"]["x"], frame["east"]["y"], frame["east"]["width"], frame["east"]["height"]
            );
            frameDataOffset += coordinatesPerFace;

            // -- South
            self.buildFrameDataFourVerts(
                frameData,
                frameDataOffset,
                src,
                frame["south"]["x"], frame["south"]["y"], frame["south"]["width"], frame["south"]["height"]
            );
            frameDataOffset += coordinatesPerFace;

            // -- West
            self.buildFrameDataFourVerts(
                frameData,
                frameDataOffset,
                src,
                frame["west"]["x"], frame["west"]["y"], frame["west"]["width"], frame["west"]["height"]
            );
            frameDataOffset += coordinatesPerFace;
        }

        return {
                    "vertices" : (coordinatesPerFace * faces) / 2,
                    "frameData" : frameData
               };
    },

    buildFrameDataFourVerts: function(frameDataArray, frameDataOffset, src, x, y, width, height)
    {
        // -- Bottom,left
        frameDataArray[frameDataOffset + 0] = x;
        frameDataArray[frameDataOffset + 1] = y;

        // -- Bottom,right
        frameDataArray[frameDataOffset + 2] = x + (width - 1);
        frameDataArray[frameDataOffset + 3] = y;

        // -- Top,right
        frameDataArray[frameDataOffset + 4] = x + (width - 1);
        frameDataArray[frameDataOffset + 5] = y + (height - 1);

        // -- Top,left
        frameDataArray[frameDataOffset + 6] = x;
        frameDataArray[frameDataOffset + 7] = y + (height - 1);

        // Now convert co-ordinates into unit vector
        for (var i = 0; i < 8; i++)
        {
            if (i % 2 == 0)
            {
                frameDataArray[frameDataOffset + i] /= src.width;
            }
            else
            {
                frameDataArray[frameDataOffset + i] /= src.height;
            }
        }
    },
    
    switchFrameY: function(frameData, index1, index2)
    {
        var v = frameData[index1];
        frameData[index1] = frameData[index2];
        frameData[index2] = v;
    },
    
    bindNoTexture: function(gl, shaderProgram)
    {
        this.emptyTexture.bind(gl, shaderProgram);
    },
    
    unbindNoTexture: function(gl, shaderProgram)
    {
        this.emptyTexture.unbind(gl, shaderProgram);
    }
}
