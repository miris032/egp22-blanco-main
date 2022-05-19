package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Loadbalancer extends AbstractBehavior<Loadbalancer.Response> {


    public interface Response {}

    private final ActorRef<Kaffeekasse.Request> kaffeekasse;
    private final ActorRef<Kaffeemaschine.Request> kaffeemaschine;

    public static final class Success implements Response {}
    public static final class Fail implements Response {}




    public static Behavior<Response> create(ActorRef<Kaffeekasse.Request> kaffeekasse, ActorRef<Kaffeemaschine.Request> kaffeemaschine) {
        return Behaviors.setup(context -> new Loadbalancer(context, kaffeekasse, kaffeemaschine));
    }


    // Constructor
    private Loadbalancer(ActorContext<Response> context, ActorRef<Kaffeekasse.Request> kaffeekasse, ActorRef<Kaffeemaschine.Request> kaffeemaschine) {
        super(context);
        this.kaffeekasse = kaffeekasse;
        this.kaffeemaschine = kaffeemaschine;
    }


    @Override
    public Receive<Response> createReceive() {
        return newReceiveBuilder()
                .onMessage(Success.class, this::onSuccess)
                .onMessage(Fail.class, this::onFail)
                .build();
    }


    private Behavior<Response> onSuccess(Response command) {
        getContext().getLog().info("Got a message. My attribute is {}!", kaffeekasse);
        return this;
    }


    private Behavior<Response> onFail(Response command) {
        getContext().getLog().info("Got a message. My attribute is {}!", kaffeekasse);
        return this;
    }
}
