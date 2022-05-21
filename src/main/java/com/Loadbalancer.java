package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Loadbalancer extends AbstractBehavior<Loadbalancer.lb> {

    public interface lb {}

    private final ActorRef<Kaffeekasse.kk> kaffeekasse;
    private final ActorRef<Kaffeemaschine.km> kaffeemaschine;
    private final ActorRef<Kaffeetrinkende.kt> kaffeetrinkende;

    public static final class MoneyEnough implements lb {}
    public static final class MoneyNotEnough implements lb {}
    public static final class CoffeeEnough implements lb {
        public int num;
        public CoffeeEnough(int num) {
            this.num = num;
        }
    }
    public static final class CoffeeNotEnough implements lb {}


    public static final class ZuKaffeeAbholung implements lb {
        public final ActorRef<Kaffeetrinkende.kt> sender;
        public ZuKaffeeAbholung(ActorRef<Kaffeetrinkende.kt> sender) {
            this.sender = sender;
        }
    }




    public static Behavior<lb> create(ActorRef<Kaffeekasse.kk> kaffeekasse, ActorRef<Kaffeemaschine.km> kaffeemaschine, ActorRef<Kaffeetrinkende.kt> kaffeetrinkende) {
        return Behaviors.setup(context -> new Loadbalancer(context, kaffeekasse, kaffeemaschine, kaffeetrinkende));
    }


    // Constructor
    private Loadbalancer(ActorContext<lb> context, ActorRef<Kaffeekasse.kk> kaffeekasse, ActorRef<Kaffeemaschine.km> kaffeemaschine, ActorRef<Kaffeetrinkende.kt> kaffeetrinkende) {
        super(context);
        this.kaffeekasse = kaffeekasse;
        this.kaffeemaschine = kaffeemaschine;
        this.kaffeetrinkende = kaffeetrinkende;
    }


    @Override
    public Receive<lb> createReceive() {
        return newReceiveBuilder()
                .onMessage(MoneyEnough.class, this::onMoneyEnough)
                .onMessage(MoneyNotEnough.class, this::onMoneyNotEnough)
                .onMessage(CoffeeEnough.class, this::onCoffeeEnough)
                .onMessage(CoffeeNotEnough.class, this::onCoffeeNotEnough)
                .onMessage(ZuKaffeeAbholung.class, this::onZuKaffeeAbholung)
                .build();
    }


    private Behavior<lb> onZuKaffeeAbholung(ZuKaffeeAbholung request) {
        kaffeekasse.tell(new Kaffeekasse.Pay(this.getContext().getSelf()));
        return this;
    }




    private Behavior<lb> onMoneyEnough(lb command) {

        // 此时已经检查完账户里有足够的钱了
        getContext().getLog().info("Has enough money!");

        // 再询问咖啡机里咖啡的数量
        kaffeemaschine.tell(new Kaffeemaschine.GetAmount(this.getContext().getSelf()));
        return this;
    }


    private Behavior<lb> onMoneyNotEnough(MoneyNotEnough response) {
        getContext().getLog().info("money is not enough!");
        //getContext().getLog().info("balance is insufficient, by {}!", loadbalancer);
        //kaffeetrinkende.sender.tell(new Kaffeetrinkende.Fail());
        return this;
    }


    private Behavior<lb> onCoffeeEnough(CoffeeEnough response) {

        // 此时检查完账户里有足够的钱后，也检查完咖啡机里有足够的咖啡了
        getContext().getLog().info("Has enough coffee!");

        // TODO 如何接收所有咖啡存量的数据，并选出一台适合的咖啡机？


        // TODO 这里需要进行一些操作，对咖啡机们进行横向比较，并得出结论要选择 index为 i的 kaffeemaschine
        int i = 0;

        // 反手把此消息告诉kaffeetrinkende
        kaffeetrinkende.tell(new Kaffeetrinkende.ChoiceCoffeeMachine(i));
        return this;
    }


    private Behavior<lb> onCoffeeNotEnough(CoffeeNotEnough response) {

        // TODO Fall 3？ 还是Fall 4 来着：补全

        return this;
    }

}
