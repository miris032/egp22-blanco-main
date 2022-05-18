package com.example;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeetrinkende extends AbstractBehavior<Kaffeetrinkende.SomeMessage> {

    public final AbstractBehavior<Kaffeekasse.Request> kaffeekasse;
    public static final class SomeMessage {}
    public static final class Success implements Loadbalancer.Response {}
    public static final class Fail implements Loadbalancer.Response {}


    public static Behavior<SomeMessage> create(AbstractBehavior<Kaffeekasse.Request> kaffeekasse) {
        return Behaviors.setup(context -> new Kaffeetrinkende(context, kaffeekasse));
    }


    private Kaffeetrinkende(ActorContext<SomeMessage> context, AbstractBehavior<Kaffeekasse.Request> kaffeekasse) {
        super(context);
        this.kaffeekasse = kaffeekasse;
    }


    @Override
    public Receive<SomeMessage> createReceive() {
        return newReceiveBuilder().onMessage(SomeMessage.class, this::onSomeMessage).build();
    }


    private Behavior<SomeMessage> onSomeMessage(SomeMessage command) {
        getContext().getLog().info("Got a message. My attribute is {}!", kaffeekasse);
        return this;
    }
}
