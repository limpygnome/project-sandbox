function Light(rawIndex)
{
    // Setup default values
    this.on = false;
    this.distance = 200.0;
    this.colour = [1.0, 1.0, 1.0];
    this.x = 0.0;
    this.y = 0.0;
    this.z = 0.0;
    this.rotation = 0.0;
    this.coneAngle = 65.0;
    this.constantAttenuation = 0.2;
    this.linearAttenuation = 0.001;
    this.quadraticAttenuation = 0.000001;
}
