/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.limpygnome.projectsandbox.website.players.enums;

public enum PlayerKeys
{
    MovementUp(1),
    MovementLeft(2),
    MovementDown(4),
    MovementRight(8),
    
    Action(16),
    
    Number1(32),
    Number2(64),
    Number3(128),
    Number4(256),
    Number5(512),
    Number6(1024),
    Number7(2048),
    Number8(4096),
    Number9(8192),
    Number0(16384),
    
    Spacebar(32768)
    ;

    public final int FLAG;

    private PlayerKeys(int flag)
    {
        this.FLAG = flag;
    }
}
