projectSandbox.camera =
{
	// Zoom settings
	zoomFactor: 10,
	zoomMin: 1,
	zoomMax: 1000,
	
	// Co-ordinates of the camera
	x: 0,
	y: 0,
	z: 0,
	
	// Rotation of the camera
	rotationX: 0,
	rotationY: 0,
	rotationZ: 0,
	
	// Zoom of the camera from the co-ordinates
	zoom: 250,
	
	// The entity id to chase; if not null, the xyz of the camera is the entity
	chaseEntityId: null,
	
	// Last co-ordinates of mouse
	mouseX: 0,
	mouseY: 0,
	mouseMove: false,
	mouseRotationFactor: 0.1,
	
	applyToModelView: function()
	{
		// Translate to center of camera
		mat4.translate(projectSandbox.modelView, projectSandbox.modelView, [-this.x, -this.y, -this.z]);
		
		// Apply rotations
		mat4.rotateX(projectSandbox.modelView, projectSandbox.modelView, this.rotationX);
		mat4.rotateY(projectSandbox.modelView, projectSandbox.modelView, this.rotationY);
		mat4.rotateZ(projectSandbox.modelView, projectSandbox.modelView, this.rotationZ);
		
		// Translate by zoom
		mat4.translate(projectSandbox.modelView, projectSandbox.modelView, [0, 0, -this.zoom]);
	},
	
	renderLogic: function()
	{
		// Update chase co-ordinates
		if(this.chaseEntityId != null)
		{
			var ent = projectSandbox.entities.get(this.chaseEntityId);
			
			if(ent != null)
			{
			    if (this.x != ent.renderX || this.y != ent.renderY || this.z != ent.renderZ)
			    {
                    // Update camera
                    this.x = ent.x;
                    this.y = ent.y;
                    this.z = ent.z;

                    // Update frustrum
                    projectSandbox.frustrum.update();
				}
			}
			else
			{
				console.warn("Camera - chase entity " + this.chaseEntityId + " not found");
			}
		}
		else if (!projectSandbox.comms.closed)
		{
			console.warn("Camera - no chase entity");
		}
	},
	
	logic: function()
	{
		// Update zoom level
		var delta = projectSandbox.mouse.scrollDelta;
		
		if(delta != 0)
		{
			// Adjust zoom
			if(delta < 0)
			{
				this.zoom += this.zoomFactor;
			}
			else
			{
				this.zoom -= this.zoomFactor;
			}
			
			// Bound zoom
			if(this.zoom < this.zoomMin)
			{
				this.zoom = this.zoomMin;
			}
			else if(this.zoom > this.zoomMax)
			{
				this.zoom = this.zoomMax;
			}
			
			// Reset delta
			projectSandbox.mouse.scrollDelta = 0;
		}
		
		// Update rotation
		if(projectSandbox.mouse.left)
		{
			// Check if this is the first loop with mouse down
			if(!this.mouseMove)
			{
				this.mouseMove = true;
				this.mouseX = projectSandbox.mouse.x;
				this.mouseY = projectSandbox.mouse.y;
			}
			else
			{
				// Move rotation based on amount of movement
				var diffX = (projectSandbox.mouse.x - this.mouseX)/projectSandbox.canvas.width;
				var diffY = (projectSandbox.mouse.y - this.mouseY)/projectSandbox.canvas.height;
				this.rotationY += diffX * this.mouseRotationFactor;
				this.rotationX += diffY * this.mouseRotationFactor;
			}
		}
		else if(this.mouseMove)
		{
			// Reset ready for next loop
			this.mouseMove = false;
		}
		
	}
}