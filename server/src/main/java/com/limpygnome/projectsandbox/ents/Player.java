/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.limpygnome.projectsandbox.ents;

import com.limpygnome.projectsandbox.Controller;

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
                controller.textureManager.textureFiles.get("characters/player"),
                (short) 32,
                (short) 32
        );
        
        this.movement = 0;
    }

    @Override
    public void logic(Controller controller)
    {
        final float movementFactor = 1.5f;
        
        float changeX = 0.0f;
        float changeY = 0.0f;
        
        // Update player's position
        if((movement & MOVEMENT_UP) == MOVEMENT_UP)
        {
            changeY += movementFactor;
        }
        if((movement & MOVEMENT_RIGHT) == MOVEMENT_RIGHT)
        {
            changeX += movementFactor;
        }
        if((movement & MOVEMENT_DOWN) == MOVEMENT_DOWN)
        {
            changeY -= movementFactor;
        }
        if((movement & MOVEMENT_LEFT) == MOVEMENT_LEFT)
        {
            changeX -= movementFactor;
        }

        // Check if to update the position
        if(changeX != 0.0f || changeY != 0.0f)
        {
            positionOffset(changeX, changeY);
        }
        
        // Perform ent logic
        super.logic(controller);
    }
    
}
