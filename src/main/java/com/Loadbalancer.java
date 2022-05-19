package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Loadbalancer extends AbstractBehavior<Loadbalancer.Response> {


    public interface Response {}
    public interface Request {}

    private final ActorRef<Kaffeekasse.Request> kaffeekasse;
    private final ActorRef<Kaffeemaschine.Request> kaffeemaschine;

    public static final class Success implements Response {}
    public static final class Fail implements Response {}

    public static final class KaffeeAbholung implements Request {
        public final ActorRef<Kaffeetrinkende.Response> sender;
        public KaffeeAbholung(ActorRef<Kaffeetrinkende.Response> sender) {
            this.sender = sender;
        }
    }




    public static Behavior<Response> create(ActorRef<Kaffeekasse.Request> kaffeekasse, ActorRef<Kaffeemaschine.Request> kaffeemaschine) {
        return Behaviors.setup(context -> new Loadbalancer(context, kaffeekasse, kaffeemaschine));
    }


    // Constructor
    private Loadbalancer(ActorContext<Response> context, ActorRef<Kaffeekasse.Request> kaffeekasse, ActorRef<Kaffeemaschine.Request> kaffeemaschine) {
        super(context);
        this.kaffeekasse = kaffeekasse;
        this.kaffeemaschine = kaffeemaschine;
    }


    @Override
    public Receive<Response> createReceive() {
        return newReceiveBuilder()
                .onMessage(Success.class, this::onSuccess)
                .onMessage(Fail.class, this::onFail)
                .build();
    }


    private Behavior<Response> onSuccess(Response command) {
        // 此时已经检查完账户里有足够的钱了
        getContext().getLog().info("Has enough money!");


        //TODO
        // 接下来询问咖啡机里咖啡的数量
        kaffeemaschine.tell(new Kaffeemaschine.GetAmount(this.getContext().getSelf()));
        if (/*咖啡机里还有咖啡*/) {
            kaffeemaschine.tell(new Kaffeemaschine.GetOneCoffee(this.getContext().getSelf()));
        }
        return this;
    }


    /*private Behavior<Response> onFail(Response command) {
        getContext().getLog().info("balance is insufficient, by {}!", kaffeekasse);

        return this;
    }*/


    private Behavior<Response> onFail(Fail response) {
        getContext().getLog().info("money is not enough!");
        //getContext().getLog().info("balance is insufficient, by {}!", loadbalancer);
        //response.sender.tell(new Kaffeetrinkende.Fail());
        return this;
    }
}
