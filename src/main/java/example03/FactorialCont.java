package example03;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class FactorialCont extends AbstractBehavior<FactorialCont.Result> {
    
    private final int val;
    private final ActorRef<Result> cust;

    public FactorialCont(ActorContext<Result> context, int val, ActorRef<Result> cust) {
        super(context);
        this.val = val;
        this.cust = cust;
    }

    public static final class Result {
        public final int arg;
        public Result (int arg) {
            this.arg = arg;
        }
    }

    public static Behavior<Result> create(int val, ActorRef<Result> cust) {
        return Behaviors.setup(context -> new FactorialCont(context, val, cust));
    }

    @Override
    public Receive<Result> createReceive() {
        return newReceiveBuilder().onMessage(Result.class, this::onResult).build();
    }

    public Behavior<Result> onResult(Result res) {
        this.cust.tell(new Result(this.val * res.arg));
        return this;
    }

}
