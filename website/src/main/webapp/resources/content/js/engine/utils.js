projectSandbox.utils =
{
	ajax: function(url, callbackSuccess, callbackFailure)
	{
		this.ajaxInternal(
			url,
			function(ajax)
			{
				callbackSuccess(ajax.responseText);
			},
			callbackFailure
		);
	},
	
	ajaxJson: function(url, callbackSuccess, callbackFailure)
	{
		this.ajaxInternal(
			url,
			function(ajax)
			{
				var json;
				
				// Parse response as JSON
				try
				{
					json = JSON.parse(ajax.responseText);
				}
				catch (e)
				{
					console.error("Failed to parse JSON in file at '" + url + "' - " + e);
					callbackFailure(ajax, url);
					return;
				}
				
				// Invoke success callback
				callbackSuccess(json);
			},
			callbackFailure
		);
	},
	
	ajaxInternal: function(url, callbackSuccess, callbackFailure)
	{
		var ajax = new XMLHttpRequest();
		ajax.onreadystatechange = function()
		{
			if (ajax.readyState == 4)
			{
				if (ajax.status == 200)
				{
					callbackSuccess(ajax);
				}
				else if (callbackFailure != undefined && callbackFailure != null)
				{
					callbackFailure(ajax, url);
				}
				else
				{
					console.error("Request failed for '" + url + "' - no failure callback handler - HTTP code " + ajax.status);
				}
			}
		};
		
		ajax.open("GET", url, true);
		ajax.send();
	},
	
	rand: function(min, max)
	{
		return Math.floor(
			(Math.random() * (max-min)) + min
		);
	},
	
	vectorRotate: function(originX, originY, x, y, rotation)
	{
		// Avoid any computation if possible
		if (rotation == 0.0 || (x == originX && y == originY))
		{
			return [x, y];
		}
		
		// Build reusable trig values
		var sin = Math.sin(rotation);
		var cos = Math.cos(rotation);
		
		// Build new co-ords
		var newx = x - originX;
		var newy = y - originY;
		
		var rx = (cos * newx) - (sin * newy);
		var ry = (sin * newx) + (cos * newy)
		
		return [originX + rx, originY + ry];
	},
}