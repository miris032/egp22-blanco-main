package com;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class SomeActor extends AbstractBehavior<SomeActor.SomeMessage> {

    private final int someAttribute;
    public static final class SomeMessage {}


    public static Behavior<SomeMessage> create(int someAttribute) {
        return Behaviors.setup(context -> new SomeActor(context, someAttribute));
    }


    private SomeActor(ActorContext<SomeMessage> context, int someAttribute) {
        super(context);
        this.someAttribute = someAttribute;
    }


    @Override
    public Receive<SomeMessage> createReceive() {
        return newReceiveBuilder().onMessage(SomeMessage.class, this::onSomeMessage).build();
    }


    private Behavior<SomeMessage> onSomeMessage(SomeMessage command) {
        getContext().getLog().info("Got a message. My attribute is {}!", someAttribute);
        return this;
    }
}
