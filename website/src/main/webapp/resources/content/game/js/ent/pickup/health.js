function HealthPickup()
{
	AbstractPickup.call(this);

    this.setTexture("world/building");
}

HealthPickup.inherits(Primitive);
HealthPickup.inherits(Entity);
HealthPickup.inherits(AbstractPickup);
