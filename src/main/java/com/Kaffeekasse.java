package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeekasse extends AbstractBehavior<Kaffeekasse.Request> {


    public interface Request {}
    private int Guthaben;
    public static final class Charge implements Request {
        public ActorRef<Kaffeetrinkende.Response> sender;
        public Charge(ActorRef<Kaffeetrinkende.Response> sender) {
            this.sender = sender;
        }
    }
    public static final class Check implements Request {
        public ActorRef<Loadbalancer.Response> sender;
        public Check(ActorRef<Loadbalancer.Response> sender) {
            this.sender = sender;
        }
    }




    public static Behavior<Request> create(int Guthaben) {
        return Behaviors.setup(context -> new Kaffeekasse(context, Guthaben));
    }


    // Constructor
    private Kaffeekasse(ActorContext<Request> context, int Guthaben) {
        super(context);
        this.Guthaben = Guthaben;
    }


    @Override
    public Receive<Request> createReceive() {
        return newReceiveBuilder()
                .onMessage(Charge.class, this::onCharge)
                .build();
    }


    // aufladen
    private Behavior<Request> onCharge(Charge request) {
        getContext().getLog().info("charge 1 Euro for {} ({})!", request.sender.path(), Guthaben);
        this.Guthaben += 1;
        request.sender.tell(new Kaffeetrinkende.Success());
        return this;
    }


    // ueberpruefung, ob genug Guthaben vorhanden ist
    private Behavior<Request> onCheck(Check request) {
        getContext().getLog().info("Got a check request from {} ({})!", request.sender.path(), Guthaben);
        if (this.Guthaben > 0) {

            request.sender.tell(new Loadbalancer.Success());
        }
        return this;
    }

}
