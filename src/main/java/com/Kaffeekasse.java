package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeekasse extends AbstractBehavior<Kaffeekasse.Charge> {

    public interface Request {}
    private int Guthaben;
    public static final class Charge {
        public ActorRef<Kaffeetrinkende.Response> sender;
        public Charge(ActorRef<Kaffeetrinkende.Response> sender) {
            this.sender = sender;
        }
    }


    public static Behavior<Charge> create(int Guthaben) {
        return Behaviors.setup(context -> new Kaffeekasse(context, Guthaben));
    }


    //Constructor
    private Kaffeekasse(ActorContext<Charge> context, int Guthaben) {
        super(context);
        this.Guthaben = Guthaben;
    }


    @Override
    public Receive<Charge> createReceive() {
        return newReceiveBuilder()
                .onMessage(Charge.class, this::onCharge)
                .build();
    }


    private Behavior<Charge> onCharge(Charge request) {
        getContext().getLog().info("charge 1 Euro for {}!", Guthaben);
        this.Guthaben += 1;
        request.sender.tell(new Kaffeetrinkende.Success());
        return this;
    }
}
