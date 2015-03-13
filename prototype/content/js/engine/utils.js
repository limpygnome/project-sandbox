projectSandbox.utils =
{
	ajaxJson: function (url, callbackSuccess, callbackFailure)
	{
		var ajax = new XMLHttpRequest();
		ajax.onreadystatechange = function()
		{
			if (ajax.readyState == 4)
			{
				if (ajax.status == 200)
				{
					var json;
					
					try
					{
						json = JSON.parse(ajax.responseText);
					}
					catch (e)
					{
						console.error("Failed to parse JSON in file at '" + url + "' - " + e);
						return;
					}
					callbackSuccess(json);
				}
				else if (callbackFailure != undefined && callbackFailure != null)
				{
					callbackFailure(ajax, url);
				}
			}
		};
		
		ajax.open("GET", url, true);
		ajax.send();
	}
}