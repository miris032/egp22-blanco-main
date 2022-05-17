package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class ActorMain extends AbstractBehavior<ActorMain.StartMessage> {

    public static class StartMessage {}
    // lskdjfkdjgsdkf

    ActorRef<SomeActor.SomeMessage> someActor;

    public static Behavior<StartMessage> create() {
        return Behaviors.setup(ActorMain::new);
    }

    private ActorMain(ActorContext<StartMessage> context) {
        super(context);
        someActor = context.spawn(SomeActor.create(37), "someActor");
    }

    @Override
    public Receive<StartMessage> createReceive() {
        return newReceiveBuilder().onMessage(StartMessage.class, this::onStartMessage).build();
    }

    private Behavior<StartMessage> onStartMessage(StartMessage command) {
        someActor.tell(new SomeActor.SomeMessage());
        return this;
    }
}
