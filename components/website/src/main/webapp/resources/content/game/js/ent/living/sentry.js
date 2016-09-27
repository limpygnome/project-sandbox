function Sentry()
{
    Entity.call(this,
        {
            title: "Sentry",
            model: "2d-rect",
            width: 32.0,
            height: 32.0
        }
    );

    this.setTexture("players/default");

    // Create sentry base
}

Sentry.typeId = 500;
Sentry.title = "Sentry";
Sentry.mapEditorEnabled = true;

Sentry.inherits(Entity);
