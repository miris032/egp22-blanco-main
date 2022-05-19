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

    public static final class MoneySuccess implements Response {}
    public static final class MoneyFail implements Response {}
    public static final class CoffeeSuccess implements Response {}
    public static final class CoffeeFail implements Response {}


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
                .onMessage(MoneySuccess.class, this::onMoneySuccess)
                .onMessage(MoneyFail.class, this::onMoneyFail)
                .onMessage(CoffeeSuccess.class, this::onCoffeeSuccess)
                .onMessage(CoffeeFail.class, this::onCoffeeFail)
                .build();
    }


    private Behavior<Request> onKaffeeAbholung(KaffeeAbholung request) {
        getContext().getLog().info("Got a put request from {} ({})!", request.sender.path());

        return this;
    }


    private Behavior<Response> onMoneySuccess(Response command) {
        // 此时已经检查完账户里有足够的钱了
        getContext().getLog().info("Has enough money!");


        //TODO
        // 接下来直接取咖啡，不用单独写返回咖啡数量的方法？
        kaffeemaschine.tell(new Kaffeemaschine.GetOneCoffee(this.getContext().getSelf()));

        return this;
    }


    private Behavior<Response> onMoneyFail(MoneyFail response) {
        getContext().getLog().info("money is not enough!");
        //getContext().getLog().info("balance is insufficient, by {}!", loadbalancer);
        //response.sender.tell(new Kaffeetrinkende.Fail());
        return this;
    }


    private Behavior<Response> onCoffeeSuccess(CoffeeSuccess response) {

        return this;
    }


    private Behavior<Response> onCoffeeFail(CoffeeFail response) {

        return this;
    }

}
