package com.limpygnome.projectsandbox.ents.enums;

/**
* Used to indicate which params have been updated. Avoids sending lots of
* unchanged data.
*/
public enum UpdateMasks
{
   X(1),
   Y(2),
   ROTATION(4),
   HEALTH(8);

   public final int MASK;

   private UpdateMasks(int MASK)
   {
       this.MASK = MASK;
   }
}
