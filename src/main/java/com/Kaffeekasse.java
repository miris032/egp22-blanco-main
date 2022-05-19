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

    public static final class Pay implements Request {
        public ActorRef<Loadbalancer.Response> sender;
        public Pay(ActorRef<Loadbalancer.Response> sender) {
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

        loadbalancer.tell(new Kaffeekasse.Pay(this.getContext().getSelf()));

    }


    @Override
    public Receive<Request> createReceive() {
        return newReceiveBuilder()
                .onMessage(Charge.class, this::onCharge)
                .onMessage(Pay.class, this::onPay)
                .build();
    }


    // Guthaben aufladen
    private Behavior<Request> onCharge(Charge request) {
        getContext().getLog().info("charge 1 Euro for {} ({})!", request.sender.path(), Guthaben);
        this.Guthaben += 1;
        request.sender.tell(new Kaffeetrinkende.Success());
        return this;
    }


    // Fall 2 & 3
    // bezahlen für eine Kaffee
    private Behavior<Request> onPay(Pay request) {
        getContext().getLog().info("Got a pay request from {} ({})!", request.sender.path(), Guthaben);
        // Fall 2
        if (this.Guthaben > 0) {
            this.Guthaben -= 1;
            request.sender.tell(new Loadbalancer.Success());
        }
        // Fall 3
        else {

            request.sender.tell(new Loadbalancer.Fail());
        }
        return this;
    }

}
