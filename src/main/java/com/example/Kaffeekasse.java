package com.example;
import akka.actor.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import uebung04.Lagerist;
//123

public class Kaffeekasse extends AbstractBehavior<Kaffeekasse.Request> {

    private int Guthaben;
    public interface Request {}
    public static final class aufladen implements Request {
        public final ActorRef<Kaffeetrinkende.Response> sender;
        public aufladen(ActorRef<Kaffeetrinkende.Response> sender) {
            this.sender = sender;
        }
    }
    //public static final class Success {}
    //public static final class Fail {}

    //xixi
/////啦啦啦啦啦

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
              .onMessage(Request.class, this::onSomeMessage)
              .build();
    }


    private Behavior<Request> wennAufladen(aufladen request) {
        getContext().getLog().info("Got a put request from {} ({})!", request.sender.path(), lagerbestand);
        this.Guthaben += 1;
        request.sender.tell(new Kaffeetrinkende().Success());
        return this;
    }


    private Behavior<Request> onSomeMessage(Request command) {
      getContext().getLog().info("Got a message. My attribute is {}!", Guthaben);
      return this;
    }
}
