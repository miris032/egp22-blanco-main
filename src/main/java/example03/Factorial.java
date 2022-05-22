package example03;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.UUID;

// #greeter
public class Factorial extends AbstractBehavior<Factorial.ContResult> {

    public static final class ContResult {
        public final int val;
        public final ActorRef<FactorialCont.Result> cust;

        public ContResult(int val, ActorRef<FactorialCont.Result> cust) {
            this.val = val;
            this.cust = cust;
        }
    }

    public static Behavior<ContResult> create() {
        return Behaviors.setup(Factorial::new);
    }

    private Factorial(ActorContext<ContResult> context) {
        super(context);
    }

    @Override
    public Receive<ContResult> createReceive() {
        return newReceiveBuilder().onMessage(ContResult.class, this::onContResult).build();
    }

    private Behavior<ContResult> onContResult(ContResult msg) {
        if (msg.val == 0) {
            msg.cust.tell(new FactorialCont.Result(1));
        } else {
            ActorRef<FactorialCont.Result> cont = this.getContext().spawn(FactorialCont.create(msg.val, msg.cust), UUID.randomUUID().toString());
            this.getContext().getSelf().tell(new ContResult(msg.val - 1, cont));
        }
        return this;
    }
}
// #greeter

