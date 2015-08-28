package com.limpygnome.projectsandbox.server.entity.physics.proximity;

/**
 * Created by limpygnome on 28/08/15.
 */
public class RotateResult
{
    private float angleOffset;
    private float angleOffsetPostMovement;

    public RotateResult()
    {
        this.angleOffset = 0.0f;
        this.angleOffsetPostMovement = 0.0f;
    }

    void setAngleOffset(float angleOffset)
    {
        this.angleOffset = angleOffset;
    }

    void setAngleOffsetPostMovement(float angleOffsetPostMovement)
    {
        this.angleOffsetPostMovement = angleOffsetPostMovement;
    }

    public float getAngleOffset()
    {
        return angleOffset;
    }

    public float getAngleOffsetPostMovement()
    {
        return angleOffsetPostMovement;
    }
}
