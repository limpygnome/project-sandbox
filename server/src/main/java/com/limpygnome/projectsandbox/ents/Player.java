/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.limpygnome.projectsandbox.ents;

import com.limpygnome.projectsandbox.Controller;
import com.limpygnome.projectsandbox.ents.physics.Vector2;

/**
 * An entity which represents a player.
 * 
 * @author limpygnome
 */
public class Player extends Entity
{
    private static final int MOVEMENT_UP = 1;
    private static final int MOVEMENT_LEFT = 2;
    private static final int MOVEMENT_DOWN = 4;
    private static final int MOVEMENT_RIGHT = 8;
    
    public byte movement;
    
    public Player(Controller controller)
    {
        super(
                (short) 10,
                (short) 8,
                (short) 1
        );
        
        this.movement = 0;
    }

    @Override
    public void logic(Controller controller)
    {
        final float movementFactor = 2.0f;
        final float rotationFactor = 0.25f;
        
        float changeDist = 0.0f;
        float changeRotation = 0.0f;
        
        // Update player's position
        if((movement & MOVEMENT_UP) == MOVEMENT_UP)
        {
            changeDist += movementFactor;
        }
        if((movement & MOVEMENT_DOWN) == MOVEMENT_DOWN)
        {
            changeDist -= movementFactor;
        }
        
        // Check for rotation
        if((movement & MOVEMENT_RIGHT) == MOVEMENT_RIGHT)
        {
            //changeX += movementFactor;
            changeRotation = rotationFactor;
        }
        if((movement & MOVEMENT_LEFT) == MOVEMENT_LEFT)
        {
            changeRotation = -rotationFactor;
        }

        // Check if to update the position
        if(changeDist != 0.0f)
        {
            // Project dist for angle
            Vector2 rotatedDist = Vector2.vectorFromAngle(rotation, changeDist);
            positionOffset(rotatedDist);
        }
        
        // Check if to update the rotation
        if (changeRotation != 0.0f)
        {
            rotationOffset(changeRotation);
        }
        
        // Perform ent logic
        super.logic(controller);
    }
    
}
