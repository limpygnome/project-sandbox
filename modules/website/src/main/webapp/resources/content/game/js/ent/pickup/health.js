function HealthPickup()
{
    AbstractPickup.call(this, 16.0, 16.0, 32.0);

    this.setTexture("world/building");
    this.setColour(0.3, 1.0, 0.3, 1.0);
}

HealthPickup.inherits(AbstractPickup);
