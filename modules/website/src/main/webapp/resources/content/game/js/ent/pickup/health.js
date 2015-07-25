function HealthPickup()
{
	AbstractPickup.call(this);

    this.setTexture("world/building");
}

HealthPickup.inherits(AbstractPickup);
