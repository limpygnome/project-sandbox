projectSandbox.utils =
{
    PI:  3.14159254359,

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

    randPrecise: function(min, max, exponent)
    {
        var dExp = 10 ^ exponent;
        var dMin = min * dExp;
        var dMax = max * dExp;

        return this.rand(dMin, dMax) / 100.0;
    },

    randRotation: function()
    {
        return this.randPrecise(0.0, 6.28318531, 1000.0);
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

    clamp: function(value, min, max)
    {
        if (value < min)
        {
            return min;
        }
        else if (value > max)
        {
            return max;
        }
        else
        {
            return value;
        }
    },

    clampCircular: function(min, max, value)
    {
        var diff = max - min;
        var v = value - min;

        if (v > diff)
        {
            while (v > diff)
            {
                v -= diff;
            }
        }
        else if (v < 0.0)
        {
            while (v < 0.0)
            {
                v += diff;
            }
        }

        return min + v;
    },

    parseText: function(data, dataView, offset)
    {
        var length = dataView.getInt8(offset);
        var text = String.fromCharCode.apply(String, data.subarray(offset + 1, offset + 1 + length));

        return text;
    },

    parseText16: function(data, dataView, offset)
    {
        var length = dataView.getInt16(offset);
        var text = String.fromCharCode.apply(String, data.subarray(offset + 2, offset + 2 + length));

        return text;
    },

    formatNumberCommas: function(value)
    {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
    },

    str2bytes: function(value)
    {
        var bytes = [];
        var charCode;

        for (var i = 0; i < value.length; i++)
        {
            charCode = value.charCodeAt(i);
            bytes.push(charCode);
        }

        return bytes;
    },


    copy2array: function(dest, offset, data)
    {
        for (var i = 0; i < data.length; i++)
        {
            dest[offset + i] = data[i];
        }

        return dest;
    }
}
