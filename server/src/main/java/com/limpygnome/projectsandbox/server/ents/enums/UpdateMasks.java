package com.limpygnome.projectsandbox.server.ents.enums;

/**
* Used to indicate which params have been updated. Avoids sending lots of
* unchanged data.
*/
public enum UpdateMasks
{
   SPAWNED(1),
   X(2),
   Y(4),
   ROTATION(8),
   HEALTH(16),
   
   ALL_MASKS(1+2+4+8+16)
   ;

   public final int MASK;

   private UpdateMasks(int MASK)
   {
       this.MASK = MASK;
   }
}
