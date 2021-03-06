// Yuyang Peng, 216417
// Zefei Gao, 216783
// Fangshu YU, 208929
// Zihao Li, 214271

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
        public int coffeeMachineNr;
        public ChoiceCoffeeMachine(int coffeeMachineNr) {
            this.coffeeMachineNr = coffeeMachineNr;
        }
    }




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
        // Fall 2 &3 &4: Zu Kaffee abholen
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
                .build();
    }


    private Behavior<kt> onSuccess(Success command) {
        getContext().getLog().info("Successful!");

        // Die Kaffeetrinkenden entscheiden sich jeweils zufällig zwischen den beiden Optionen Guthaben aufladen oder Kaffee holen
        // Fall 1: Guthaben aufladen
        if (Math.random() < 0.5) {
            kaffeekasse.tell(new Kaffeekasse.Recharge(this.getContext().getSelf()));
        }
        // Fall 2 &3 &4: Zu Kaffee abholen
        else {
            loadbalancer.tell(new Loadbalancer.ZuKaffeeAbholung(this.getContext().getSelf()));
        }
        return this;
    }


    // Fall 4(weiter): Wenn Fail, soll der*die Kaffeetrinkende aber keine weiteren Nachrichten senden, und stoppt der Programm
    private Behavior<kt> onFail(Fail command) {
        getContext().getLog().info("Fail, Program stop!");
        return Behaviors.stopped();
    }


    private Behavior<kt> onChoiceCoffeeMachine(ChoiceCoffeeMachine request) {
        getContext().getLog().info("Choice coffee machine success");

        // Hat die Kaffeemaschine bestimmen, holt dann eine Kaffee an diese Kaffeemaschine ab
        kaffeemaschine.tell(new Kaffeemaschine.GetOneCoffee(this.getContext().getSelf()));
        return this;
    }

}
