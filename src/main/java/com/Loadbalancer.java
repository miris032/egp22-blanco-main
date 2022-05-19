package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Loadbalancer extends AbstractBehavior<Loadbalancer.Response> {

    public interface Response {}
    private final ActorRef<Kaffeekasse.Request> kaffeekasse2;
    public static final class Success implements Response {}
    public static final class Fail implements Response {}


    public static Behavior<Response> create(ActorRef<Kaffeekasse.Request> kaffeekasse2) {
        return Behaviors.setup(context -> new Loadbalancer(context, kaffeekasse2));
    }


    private Loadbalancer(ActorContext<Response> context, ActorRef<Kaffeekasse.Request> kaffeekasse2) {
        super(context);
        this.kaffeekasse2 = kaffeekasse2;
    }


    @Override
    public Receive<Response> createReceive() {
        return newReceiveBuilder()
                .onMessage(Success.class, this::onSuccess)
                .onMessage(Fail.class, this::onFail)
                .build();
    }


    private Behavior<Response> onSuccess(Response command) {
        getContext().getLog().info("Got a message. My attribute is {}!", kaffeekasse2);
        return this;
    }


    private Behavior<Response> onFail(Response command) {
        getContext().getLog().info("Got a message. My attribute is {}!", kaffeekasse2);
        return this;
    }
}
