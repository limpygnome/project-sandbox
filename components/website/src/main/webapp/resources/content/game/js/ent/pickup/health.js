function HealthPickup()
{
    AbstractPickup.call(this, 16.0, 16.0, 16.0);

    this.setTexture("pickups/health");
}

HealthPickup.inherits(AbstractPickup);
