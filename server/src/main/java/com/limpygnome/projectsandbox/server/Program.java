
package com.limpygnome.projectsandbox.server;

/**
 *
 * @author limpygnome
 */
public class Program
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("Running project sandbox server...");
        
        Controller controller = new Controller();
        controller.start();
    }
}
