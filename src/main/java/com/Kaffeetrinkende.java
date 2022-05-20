package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeetrinkende extends AbstractBehavior<Kaffeetrinkende.Response> {


    public interface Response {}

    private final ActorRef<Kaffeekasse.Request> kaffeekasse;
    private final ActorRef<Loadbalancer.Request> loadbalancer;

    public static final class Success implements Response {}
    public static final class Fail implements Response {}




    public static Behavior<Response> create(ActorRef<Kaffeekasse.Request> kaffeekasse, ActorRef<Loadbalancer.Request> loadbalancer, ActorRef<Kaffeemaschine.Request> kaffeemaschine) {
        return Behaviors.setup(context -> new Kaffeetrinkende(context, kaffeekasse, loadbalancer, kaffeemaschine));
    }


    // Constructor
    private Kaffeetrinkende(ActorContext<Response> context, ActorRef<Kaffeekasse.Request> kaffeekasse, ActorRef<Loadbalancer.Request> loadbalancer, ActorRef<Kaffeemaschine.Request> kaffeemaschine) {
        super(context);
        this.kaffeekasse = kaffeekasse;
        this.loadbalancer = loadbalancer;

        // Die Kaffeetrinkenden entscheiden sich jeweils zufällig zwischen den beiden Optionen Guthaben aufladen oder Kaffee holen
        // Fall 1: Guthaben aufladen
        if (Math.random() < 0.5) {
            kaffeekasse.tell(new Kaffeekasse.Charge(this.getContext().getSelf()));
        }
        // Fall 2 &3 &4: Kaffee holen
        else {
            loadbalancer.tell(new Loadbalancer.KaffeeAbholung(this.getContext().getSelf()));
        }
    }


    @Override
    public Receive<Response> createReceive() {
        return newReceiveBuilder()
                .onMessage(Success.class, this::onSuccess)
                .onMessage(Fail.class, this::onFail)
                .build();
    }


    private Behavior<Response> onSuccess(Success command) {
        getContext().getLog().info("Successful!");

        // Die Kaffeetrinkenden entscheiden sich jeweils zufällig zwischen den beiden Optionen Guthaben aufladen oder Kaffee holen
        // Fall 1: Guthaben aufladen
        if (Math.random() < 0.5) {
            kaffeekasse.tell(new Kaffeekasse.Charge(this.getContext().getSelf()));
        }
        // Fall 2 &3 &4: zu Kaffee abholen
        else {
            loadbalancer.tell(new Loadbalancer.KaffeeAbholung(this.getContext().getSelf()));
        }
        return this;
    }


    private Behavior<Response> onFail(Fail command) {
        getContext().getLog().info("Fail");
        return Behaviors.stopped();
    }

}
