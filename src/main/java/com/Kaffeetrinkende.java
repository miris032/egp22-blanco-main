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
    private final ActorRef<Loadbalancer.Response> loadbalancer;
    private final ActorRef<Kaffeemaschine.Request> kaffeemaschine;

    public static final class Success implements Response {}
    public static final class Fail implements Response {}




    public static Behavior<Response> create(ActorRef<Kaffeekasse.Request> kaffeekasse, ActorRef<Loadbalancer.Response> loadbalancer, ActorRef<Kaffeemaschine.Request> kaffeemaschine) {
        return Behaviors.setup(context -> new Kaffeetrinkende(context, kaffeekasse, loadbalancer, kaffeemaschine));
    }


    // Constructor
    private Kaffeetrinkende(ActorContext<Response> context, ActorRef<Kaffeekasse.Request> kaffeekasse, ActorRef<Loadbalancer.Response> loadbalancer, ActorRef<Kaffeemaschine.Request> kaffeemaschine) {
        super(context);
        this.kaffeekasse = kaffeekasse;
        this.loadbalancer = loadbalancer;
        this.kaffeemaschine = kaffeemaschine;

        // Die Kaffeetrinkenden entscheiden sich jeweils zufällig zwischen
        // den beiden Optionen Guthaben aufladen oder Kaffee holen.
        if (Math.random() < 0.5) {
            kaffeekasse.tell(new Kaffeekasse.Charge(this.getContext().getSelf()));
        } else {
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
        getContext().getLog().info("Successful recharge!");

        // Wieder zufällig zwischen den beiden Optionen Guthaben aufladen
        // oder Kaffee holen wählen.
        if (Math.random() < 0.5) {
            kaffeekasse.tell(new Kaffeekasse.Charge(this.getContext().getSelf()));
        } else {

        }
        return this;
    }


    private Behavior<Response> onFail(Fail command) {
        getContext().getLog().info("Fail");
        return Behaviors.stopped();
    }
}
