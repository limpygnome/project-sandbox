function PrimitiveBar(width, height, horizontal)
{
	// Set initial values
	this.x = 0;
	this.y = 0;
	this.value = 0.0;
	
	// Save size for later
	this.width = width;
	this.height = height;
	this.horizontal = horizontal;
	
	// Compute initial bar sizes
	var initWidth = horizontal ? 0 : width;
	var initHeight = horizontal ? height : 0;
	
	// Build two primitives for the bar
	this.barValue = new Primitive(initWidth, initHeight);
	
	this.barEmpty = new Primitive(initWidth, initHeight);
}

// - Value must be 0.0 to 1.0
PrimitiveBar.prototype.setColour = function(barFullR, barFullG, barFullB, barFullA, barEmptyR, barEmptyG, barEmptyB, barEmptyA)
{
	this.barValue.setColour(barFullR, barFullG, barFullB, barFullA);
	this.barEmpty.setColour(barEmptyR, barEmptyG, barEmptyB, barEmptyA);
}

PrimitiveBar.prototype.setValue = function(value)
{
	// Check if value has changed - ignore if not
	if (this.value == value)
	{
		return;
	}
	
	// Compute new sizes
	var maxValue = this.horizontal ? this.width : this.height;
	var valueFull = maxValue * value;
	var valueEmpty = maxValue * (1.0 - value);
	
	// Set sizes
	if (this.horizontal)
	{
		// Set size
		this.barValue.width = valueFull;
		this.barEmpty.width = valueEmpty;
		
		// Set pos
		this.barValue.x = this.x - (valueEmpty / 2.0);
		this.barEmpty.x = this.x + (valueFull / 2.0);
		
		this.barValue.y = this.y;
		this.barEmpty.y = this.y;
	}
	else
	{
		// Set size
		this.barValue.height = valueFull;
		this.barEmpty.height = valueEmpty;
		
		// Set pos
		this.barValue.x = this.x;
		this.barEmpty.x = this.x;
		
		this.barValue.y = this.y - (valueEmpty / 2.0);
		this.barEmpty.y = this.y + (valueFull / 2.0);
	}
	
	// Rebuild primitives
	this.barValue.compile();
	this.barEmpty.compile();
	
	// Update internal value
	this.value = value;
}

PrimitiveBar.prototype.render = function(gl, shaderProgram, modelView, perspective)
{
	// Call render on the bars
	this.barValue.render(gl, shaderProgram, modelView, perspective);
	this.barEmpty.render(gl, shaderProgram, modelView, perspective);
}
