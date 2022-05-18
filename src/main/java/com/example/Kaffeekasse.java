package com.example;
import akka.actor.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeekasse extends AbstractBehavior<Kaffeekasse.Request> {

    private int Guthaben;
    public interface Request {}
    public interface Response {}
    public static final class aufladen implements Request {
        public final ActorRef<Kaffeetrinkende.Response> sender;
        public aufladen(ActorRef<Kaffeetrinkende.Response> sender) {
            this.sender = sender;
        }
    }


    public static Behavior<Request> create(int Guthaben) {
      return Behaviors.setup(context -> new Kaffeekasse(context, Guthaben));
    }


    //Constructor
    private Kaffeekasse(ActorContext<Request> context, int Guthaben) {
      super(context);
      this.Guthaben = Guthaben;
    }


    @Override
    public Receive<Request> createReceive() {
      return newReceiveBuilder()
              .onMessage(aufladen.class, this::wennAufladen)
              .build();
    }


    private Behavior<Request> wennAufladen(aufladen request) {
        getContext().getLog().info("Laden 1 Euro für {} ({})!", request.sender.path(), Guthaben);
        this.Guthaben += 1;
        request.sender.tell(new Kaffeetrinkende().Success());
        return this;
    }


}
