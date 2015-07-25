/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.limpygnome.projectsandbox.server.players.enums;

public enum PlayerKeys
{
    MovementUp(1),
    MovementLeft(2),
    MovementDown(4),
    MovementRight(8),
    
    Action(16),

    Spacebar(32768)
    ;

    public final int FLAG;

    private PlayerKeys(int flag)
    {
        this.FLAG = flag;
    }
}
