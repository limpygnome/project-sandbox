game.effects =
{
    // Constants
    BULLET_WIDTH: 8,
    BULLET_HEIGHT: 8,
    BULLET_Z: -0.5,
    BULLET_LIFESPAN: 8000,
    BULLET_FADE: true,

    TRACER_WIDTH: 1,
    TRACER_Z: -0.5,
    TRACER_LIFESPAN: 1000,
    TRACER_FADE: true,

    handlePacket: function(packet)
    {
        var subType;

        while (packet.hasMoreData())
        {
            subType = packet.readChar();

            // Create effect
            switch (subType)
            {
                case "B":
                    this.packetCreateBullet(packet);
                    break;

                case "T":
                    this.packetCreateTracer(packet);
                    break;

                case "E":
                    this.packetCreateExplosion(packet);
                    break;

                default:
                    console.error("game/effects - unknown effect sub-type - " + subType);
                    break;
            }
        }
    },

    packetCreateBullet: function(packet)
    {
        // Read position of effect
        var effectX = packet.readFloat();
        var effectY = packet.readFloat();

        // Create bullet effect
        var effect = new Effect("error", this.BULLET_WIDTH, this.BULLET_HEIGHT, effectX, effectY, this.BULLET_Z, this.BULLET_LIFESPAN, this.BULLET_FADE);
        effect.rotation = projectSandbox.utils.randRotation();
        projectSandbox.addEffect(effect);
    },

    packetCreateTracer: function(packet)
    {
        // Create tracer effect
        var x1 = packet.readFloat();
        var y1 = packet.readFloat();
        var x2 = packet.readFloat();
        var y2 = packet.readFloat();

        // - Compute size
        var w = Math.abs(x2 - x1);
        var h = Math.abs(y2 - y1);
        var size = Math.sqrt( (w*w) + (h*h) );

        // - Compute midpoint for x,y
        var midx = x1 + ((x2 - x1) / 2.0);
        var midy = y1 + ((y2 - y1) / 2.0);

        // - Compute rotation
        var rotation = Math.atan2(x2 - x1, y2 - y1);

        // - Finally create the effect
        var effect = new Effect("error", this.TRACER_WIDTH, size, midx, midy, this.TRACER_Z, this.TRACER_LIFESPAN, this.TRACER_FADE);
        effect.rotation = rotation;
        projectSandbox.addEffect(effect);
    },

    packetCreateExplosion: function (packet)
    {
        // Parse data
        var subType = packet.readByte();
        var x = packet.readFloat();
        var y = packet.readFloat();

        // Create explosion based on subtype
        switch (subType)
        {
            // Suicide vest explosion
            case 100:
                this.createExplosion(
                    x, y, 512, 8000, -2.0, 2.0
                );
                this.createExplosion(
                    x, y, 128, 5000, -4.5, 4.5
                );
                this.createExplosion(
                    x, y, 128, 2500, -8.0, 8.0
                );
                break;

            // Jump drive
            case 101:
                this.createExplosion(
                    x, y, 256, 5000, -12.0, 12.0
                );
                break;

            // Force field
            case 102:
                this.createExplosion(
                    x, y, 4, 5000, -2.0, 2.0
                );
                break;

            default:
                console.error("game/effects - unknown explosion effect sub-type - " + subType);
                break;
        }

        return 9;
    },

    EXPLOSION_Z: -0.5,
    EXPLOSION_FADE: true,
    EXPLOSION_WIDTH: 16,
    EXPLOSION_HEIGHT: 16,

    createExplosion: function(x, y, particles, lifeSpan, velocityMin, velocityMax)
    {
        var effect;

        for (var i = 0; i < particles; i++)
        {
            effect = new Effect("flames/blue", this.EXPLOSION_WIDTH, this.EXPLOSION_HEIGHT, x, y, this.EXPLOSION_Z, lifeSpan, this.EXPLOSION_FADE);

            effect.rotation = projectSandbox.utils.randRotation();
            effect.vx = projectSandbox.utils.randPrecise(velocityMin, velocityMax, 100.0);
            effect.vy = projectSandbox.utils.randPrecise(velocityMin, velocityMax, 100.0);
            effect.vz = projectSandbox.utils.randPrecise(velocityMin, velocityMax, 100.0);

            projectSandbox.addEffect(effect);
        }
    }

}
