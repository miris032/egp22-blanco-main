package example03;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class AlgoMain extends AbstractBehavior<FactorialCont.Result> {

    public static Behavior<FactorialCont.Result> create() {
        return Behaviors.setup(AlgoMain::new);
    }

    private AlgoMain(ActorContext<FactorialCont.Result> context) {
        super(context);
        ActorRef<Factorial.ContResult> start = context.spawn(Factorial.create(), "start");
        start.tell(new Factorial.ContResult(5, context.getSelf()));
    }

    @Override
    public Receive<FactorialCont.Result> createReceive() {
        return newReceiveBuilder().onMessage(FactorialCont.Result.class, this::onResult).build();
    }

    private Behavior<FactorialCont.Result> onResult(FactorialCont.Result val) {
        getContext().getLog().info("Result: {}", val.arg);
        return Behaviors.stopped();
    }
}
