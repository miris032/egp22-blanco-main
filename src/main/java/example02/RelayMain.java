package example02;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class RelayMain extends AbstractBehavior<RelayMain.Start> {

    public static class Start {
        public Start() {        }
    }

    public static Behavior<Start> create() {
        return Behaviors.setup(RelayMain::new);
    }

    private RelayMain(ActorContext<Start> context) {
        super(context);
        //#create-actors
        //greeter = context.spawn(Greeter.create(6), "greeter");
        //#create-actors
    }

    @Override
    public Receive<Start> createReceive() {
        return newReceiveBuilder().onMessage(Start.class, this::onStart).build();
    }

    private Behavior<Start> onStart(Start command) {
        //#create-actors
        ActorRef<Relayer.Message> alice =
                getContext().spawn(Relayer.create(15), "alice");
        ActorRef<Relayer.Message> bob =
                getContext().spawn(Relayer.create(15), "bob");
        ActorRef<Relayer.Message> charles =
                getContext().spawn(Relayer.create(15), "charles");

        //ActorRef<Relayer.Message>[] queue = {bob, charles, alice};

        alice.tell(new Relayer.Message(0, new ActorRef[]{bob, charles, alice}));
        //#create-actors
        return this;
    }
}
