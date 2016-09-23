function HealthPickup()
{
    AbstractPickup.call(this, "Health Pickup", 16.0, 16.0, 16.0);

    this.setTexture("pickups/health");
}

HealthPickup.typeId = 1201;
HealthPickup.title = "Health Pickup";
HealthPickup.inherits(AbstractPickup);
