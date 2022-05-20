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
    private final ActorRef<Kaffeetrinkende.Response>

    public static final class MoneyEnough implements Response {}
    public static final class MoneyNotEnough implements Response {}
    public static final class CoffeeEnough implements Response {}
    public static final class CoffeeNotEnough implements Response {}


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
                .onMessage(MoneyNotEnough.class, this::onMoneyNotEnough)
                .onMessage(CoffeeEnough.class, this::onCoffeeEnough)
                .onMessage(CoffeeNotEnough.class, this::onCoffeeNotEnough)
                //.onMessage(ZuKaffeeAbholung.class, this::onZuKaffeeAbholung)
                .build();
    }

    /*private Loadbalancer(ActorContext<Loadbalancer.Response> context, ActorRef<Kaffeekasse.Request> kaffeekasse) {
        super(context);
        this.kaffeekasse = kaffeekasse;
        kaffeekasse.tell(new Kaffeekasse.Pay(this.getContext().getSelf()));
    }*/


    private Behavior<Request> onZuKaffeeAbholung(ZuKaffeeAbholung request) {
        kaffeekasse.tell(new Kaffeekasse.Pay(this.getContext().getSelf()));
        return this;
    }




    private Behavior<Response> onMoneyEnough(Response command) {

        // 此时已经检查完账户里有足够的钱了
        getContext().getLog().info("Has enough money!");

        // 再询问咖啡机里咖啡的数量
        kaffeemaschine.tell(new Kaffeemaschine.GetAmount(this.getContext().getSelf()));
        return this;
    }


    private Behavior<Response> onMoneyNotEnough(MoneyNotEnough response) {
        getContext().getLog().info("money is not enough!");
        //getContext().getLog().info("balance is insufficient, by {}!", loadbalancer);
        //kaffeetrinkende.sender.tell(new Kaffeetrinkende.Fail());
        return this;
    }


    private Behavior<Response> onCoffeeEnough(CoffeeEnough response) {

        // 此时检查完账户里有足够的钱后，也检查完咖啡机里有足够的咖啡了
        getContext().getLog().info("Has enough money!");

        // 反手把此消息告诉kaffeetrinkende
        kaffeetrinkende.tell(new Kaffeetrinkende.CoffeeEnoughForCustomer(this.getContext().getSelf()));
        return this;
    }


    private Behavior<Response> onCoffeeNotEnough(CoffeeNotEnough response) {

        return this;
    }

}
