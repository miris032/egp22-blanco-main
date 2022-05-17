package uebung04;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class LagerMain extends AbstractBehavior<LagerMain.StartMessage> {

    public static class StartMessage {}

    ActorRef<SomeActor.SomeMessage> someActor;

    public static Behavior<StartMessage> create() {
        return Behaviors.setup(LagerMain::new);
    }

    private LagerMain(ActorContext<StartMessage> context) {
        super(context);
    }

    @Override
    public Receive<StartMessage> createReceive() {
        return newReceiveBuilder().onMessage(StartMessage.class, this::onStartMessage).build();
    }

    private Behavior<StartMessage> onStartMessage(StartMessage command) {
        ActorRef<Lagerverwaltung.Request> lagerverwaltung = getContext().spawn(Lagerverwaltung.create(3), "lagerverwaltung");
        ActorRef<Lagerist.Response> lagerist1 = getContext().spawn(Lagerist.create(lagerverwaltung), "lagerist1");
        ActorRef<Lagerist.Response> lagerist2 = getContext().spawn(Lagerist.create(lagerverwaltung), "lagerist2");
        ActorRef<Lagerist.Response> lagerist3 = getContext().spawn(Lagerist.create(lagerverwaltung), "lagerist3");
        return this;
    }
}
