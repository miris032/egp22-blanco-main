package example02;

import akka.actor.typed.ActorSystem;

import java.io.IOException;
public class AkkaQuickstart {
  public static void main(String[] args) {
    //#actor-system
    final ActorSystem<RelayMain.Start> greeterMain = ActorSystem.create(RelayMain.create(), "roundaround");
    //#actor-system

    //#main-send-messages
    greeterMain.tell(new RelayMain.Start());
    //#main-send-messages

    try {
      System.out.println(">>> Press ENTER to exit <<<");
      System.in.read();
    } catch (IOException ignored) {
    } finally {
      greeterMain.terminate();
    }
  }
}
