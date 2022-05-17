package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class KaffeeMain extends AbstractBehavior<KaffeeMain.StartMessage> {

    public static class StartMessage {}
    // lskdjfkdjgsdkfdfgdf

    ActorRef<SomeActor.SomeMessage> someActor;

    public static Behavior<StartMessage> create() {
        return Behaviors.setup(KaffeeMain::new);
    }

    private KaffeeMain(ActorContext<StartMessage> context) {
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
