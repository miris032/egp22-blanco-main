package com;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Kaffeetrinkende extends AbstractBehavior<Kaffeetrinkende.kt> {


    public interface kt {}

    private final ActorRef<Kaffeekasse.kk> kaffeekasse;
    private final ActorRef<Loadbalancer.lb> loadbalancer;
    private final ActorRef<Kaffeemaschine.km> kaffeemaschine;

    public static final class Success implements kt {}
    public static final class Fail implements kt {}
    public static final class ChoiceCoffeeMachine implements kt {
        public int coffeeMachineIndex;
        public ChoiceCoffeeMachine(int coffeeMachineIndex) {
            this.coffeeMachineIndex = coffeeMachineIndex;
        }
    }
    public static final class ChoiceCoffeeMachineFail implements kt {}




    public static Behavior<kt> create(ActorRef<Kaffeekasse.kk> kaffeekasse, ActorRef<Loadbalancer.lb> loadbalancer, ActorRef<Kaffeemaschine.km> kaffeemaschine) {
        return Behaviors.setup(context -> new Kaffeetrinkende(context, kaffeekasse, loadbalancer, kaffeemaschine));
    }


    // Constructor
    private Kaffeetrinkende(ActorContext<kt> context, ActorRef<Kaffeekasse.kk> kaffeekasse, ActorRef<Loadbalancer.lb> loadbalancer, ActorRef<Kaffeemaschine.km> kaffeemaschine) {
        super(context);
        this.kaffeekasse = kaffeekasse;
        this.loadbalancer = loadbalancer;
        this.kaffeemaschine = kaffeemaschine;

        // Die Kaffeetrinkenden entscheiden sich jeweils zufällig zwischen den beiden Optionen Guthaben aufladen oder Kaffee holen
        // Fall 1: Guthaben aufladen
        if (Math.random() < 0.5) {
            kaffeekasse.tell(new Kaffeekasse.Recharge(this.getContext().getSelf()));
        }
        // Fall 2 &3 &4: Kaffee holen
        else {
            loadbalancer.tell(new Loadbalancer.ZuKaffeeAbholung(this.getContext().getSelf()));
        }
    }


    @Override
    public Receive<kt> createReceive() {
        return newReceiveBuilder()
                .onMessage(Success.class, this::onSuccess)
                .onMessage(Fail.class, this::onFail)
                .onMessage(ChoiceCoffeeMachine.class, this::onChoiceCoffeeMachine)
                .onMessage(ChoiceCoffeeMachineFail.class, this::ChoiceCoffeeMachineFail)
                .build();
    }


    private Behavior<kt> onSuccess(Success command) {
        getContext().getLog().info("Successful!");

        // Die Kaffeetrinkenden entscheiden sich jeweils zufällig zwischen den beiden Optionen Guthaben aufladen oder Kaffee holen
        // Fall 1: Guthaben aufladen
        if (Math.random() < 0.5) {
            kaffeekasse.tell(new Kaffeekasse.Recharge(this.getContext().getSelf()));
        }
        // Fall 2 &3 &4: zu Kaffee abholen
        else {
            loadbalancer.tell(new Loadbalancer.ZuKaffeeAbholung(this.getContext().getSelf()));
        }
        return this;
    }


    // 咖啡不够了， 程序最终停止
    private Behavior<kt> onFail(Fail command) {
        getContext().getLog().info("Fail");
        return Behaviors.stopped();
    }


    private Behavior<kt> onChoiceCoffeeMachine(ChoiceCoffeeMachine response) {
        getContext().getLog().info("Choice coffee machine success");

        // 已选择确认的咖啡机， 接下来从这台咖啡机里取咖啡
        kaffeemaschine.tell(new Kaffeemaschine.GetOneCoffee(this.getContext().getSelf()));
        return this;
    }


    private Behavior<kt> ChoiceCoffeeMachineFail(ChoiceCoffeeMachineFail response) {
        getContext().getLog().info("Choice coffee machine fail");

        // TODO Fall 3？ 还是Fall 4 来着：补全

        return this;
    }

}
