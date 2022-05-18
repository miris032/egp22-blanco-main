package uebung04;
import akka.actor.typed.ActorSystem;

import java.io.IOException;


public class Blatt4 {
    public static void main(String[] args) {
        final ActorSystem<LagerMain.StartMessage> lagerMain = ActorSystem.create(LagerMain.create(), "lagerMain");

        lagerMain.tell(new LagerMain.StartMessage());

        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            lagerMain.terminate();
        }
    }
}
