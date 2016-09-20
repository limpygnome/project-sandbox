/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projectsandbox.components.server.player;

public enum PlayerKeys
{
    /**
     * Currently the W key.
     */
    MovementUp(1),

    /**
     * Currently the A key.
     */
    MovementLeft(2),

    /**
     * Currently the S key.
     */
    MovementDown(4),

    /**
     * Currently the D key.
     */
    MovementRight(8),

    /**
     * Currently the F key.
     */
    Action(16),

    /**
     * Currently the shift key.
     */
    Shift(32),

    /**
     * Currently the space bar.
     */
    Spacebar(32768)
    ;

    public final int FLAG;

    PlayerKeys(int flag)
    {
        this.FLAG = flag;
    }
}
