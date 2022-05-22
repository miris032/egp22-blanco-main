package example03;

import akka.actor.typed.ActorSystem;

import java.io.IOException;
public class Blatt3 {
  public static void main(String[] args) {
    //#actor-system
    final ActorSystem<FactorialCont.Result> greeterMain = ActorSystem.create(AlgoMain.create(), "factorial");
    //#actor-system

    try {
      System.out.println(">>> Press ENTER to exit <<<");
      System.in.read();
    } catch (IOException ignored) {
    } finally {
      greeterMain.terminate();
    }
  }
}
