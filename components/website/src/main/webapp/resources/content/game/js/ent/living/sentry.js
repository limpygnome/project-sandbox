function Sentry()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 32.0,
            height: 32.0
        }
    );

    this.setTexture("players/default");

    // Create sentry base
}

Sentry.inherits(Entity);
