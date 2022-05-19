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


    //Constructor
    private Kaffeetrinkende(ActorContext<Response> context, ActorRef<Kaffeekasse.Request> kaffeekasse, ActorRef<Loadbalancer.Response> loadbalancer, ActorRef<Kaffeemaschine.Request> kaffeemaschine) {
        super(context);
        this.kaffeekasse = kaffeekasse;
        this.loadbalancer = loadbalancer;
        this.kaffeemaschine = kaffeemaschine;
    }


    @Override
    public Receive<Response> createReceive() {
        return newReceiveBuilder()
                .onMessage(Success.class, this::onSuccess)
                .build();
    }


    private Behavior<Response> onSuccess(Success command) {
        getContext().getLog().info("Got a message. My attribute is {}!", kaffeekasse);
        return this;
    }
}
