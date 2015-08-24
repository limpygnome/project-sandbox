package com.limpygnome.projectsandbox.server.entity;

/**
* Used to indicate which params have been updated. Avoids sending lots of
* unchanged data.
*/
public enum UpdateMasks
{
   /**
    * Indicates the entity is alive.
    */
   ALIVE(1),

   /**
    * Indicates X of position has been updated.
    */
   X(2),

   /**
    * Indicates Y of position has been updated.
    */
   Y(4),

   /**
    * The rotation has been updated.
    */
   ROTATION(8),

   /**
    * The health of the player has been updated.
    */
   HEALTH(16),

   /**
    * All of the masks combined, excluding ALIVE.
    */
   ALL_MASKS(2+4+8+16)
   ;

   public final int MASK;

   UpdateMasks(int MASK)
   {
       this.MASK = MASK;
   }
}
