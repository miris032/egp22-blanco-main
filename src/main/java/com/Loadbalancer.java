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

    public static final class MoneyEnough implements Response {}
    public static final class MoneyNotEnouth implements Response {}


    public static final class ZuKaffeeAbholung implements Request {
        public final ActorRef<Kaffeetrinkende.Response> sender;
        public ZuKaffeeAbholung(ActorRef<Kaffeetrinkende.Response> sender) {
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
                .onMessage(MoneyEnough.class, this::onMoneyEnough)
                .onMessage(MoneyNotEnouth.class, this::onMoneyNotEnough)
                //.onMessage(ZuKaffeeAbholung.class, this::onZuKaffeeAbholung)
                .build();
    }


    private Behavior<Request> onZuKaffeeAbholung(ZuKaffeeAbholung request) {
        //getContext().getLog().info("Got a put request from {} ({})!", request.sender.path());

        request.sender.tell(new Kaffeekasse.Pay(this.getContext().getSelf()));
        return this;
    }


    private Behavior<Response> onMoneyEnough(Response command) {

        // 此时已经检查完账户里有足够的钱了
        getContext().getLog().info("Has enough money!");

        // 再询问咖啡机里咖啡的数量
        kaffeemaschine.tell(new Kaffeemaschine.GetAmount(this.getContext().getSelf()));
        return this;
    }


    private Behavior<Response> onMoneyNotEnough(MoneyNotEnouth response) {
        getContext().getLog().info("money is not enough!");
        //getContext().getLog().info("balance is insufficient, by {}!", loadbalancer);
        //kaffeetrinkende.sender.tell(new Kaffeetrinkende.Fail());
        return this;
    }

}
