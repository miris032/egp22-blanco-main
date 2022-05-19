package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeekasse extends AbstractBehavior<Kaffeekasse.Aufladen> {

    public interface Request {}
    private final int Guthaben;
    public static final class Aufladen {
        public ActorRef<Kaffeetrinkende.Response> sender;
        public Aufladen(ActorRef<Kaffeetrinkende.Response> sender) {
            this.sender = sender;
        }
    }


    public static Behavior<Aufladen> create(int Guthaben) {
        return Behaviors.setup(context -> new Kaffeekasse(context, Guthaben));
    }


    private Kaffeekasse(ActorContext<Aufladen> context, int Guthaben) {
        super(context);
        this.Guthaben = Guthaben;
    }


    @Override
    public Receive<Aufladen> createReceive() {
        return newReceiveBuilder().onMessage(Aufladen.class, this::onAufladen).build();
    }


    private Behavior<Aufladen> onAufladen(Aufladen command) {
        getContext().getLog().info("Got a message. My attribute is {}!", Guthaben);
        return this;
    }
}
