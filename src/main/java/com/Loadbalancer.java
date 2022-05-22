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
import akka.japi.Pair;


public class Loadbalancer extends AbstractBehavior<Loadbalancer.lb> {

    public interface lb {}

    private final ActorRef<Kaffeekasse.kk> kaffeekasse;
    private final ActorRef<Kaffeemaschine.km> kaffeemaschine;

    public static final class MoneyEnough implements lb {}
    public static final class MoneyNotEnough implements lb {}

    public static final class CoffeeEnough implements lb {
        public int Nr;
        public int Vorrat;
        public CoffeeEnough(int Nr, int Vorrat) {
            this.Nr = Nr;
            this.Vorrat = Vorrat;
        }
    }

    /*public static final class CoffeeNotEnough implements lb {
        public ActorRef<Kaffeemaschine.km> sender;
        public CoffeeNotEnough(ActorRef<Kaffeemaschine.km> sender) {
            this.sender = sender;
        }
    }*/


    public static final class ZuKaffeeAbholung implements lb {
        public final ActorRef<Kaffeetrinkende.kt> sender;
        /////////////////////////////////////////////////////////
        public int Nr;
        public int Vorrat;
        /////////////////////////////////////////////////////////
        public ZuKaffeeAbholung(ActorRef<Kaffeetrinkende.kt> sender) {
            this.sender = sender;
        }
    }




    public static Behavior<lb> create(ActorRef<Kaffeekasse.kk> kaffeekasse, ActorRef<Kaffeemaschine.km> kaffeemaschine) {
        return Behaviors.setup(context -> new Loadbalancer(context, kaffeekasse, kaffeemaschine));
    }


    // Constructor
    private Loadbalancer(ActorContext<lb> context, ActorRef<Kaffeekasse.kk> kaffeekasse,
                         ActorRef<Kaffeemaschine.km> kaffeemaschine) {
        super(context);
        this.kaffeekasse = kaffeekasse;
        this.kaffeemaschine = kaffeemaschine;
    }


    @Override
    public Receive<lb> createReceive() {
        return newReceiveBuilder()
                .onMessage(MoneyEnough.class, this::onMoneyEnough)
                .onMessage(ZuKaffeeAbholung.class, this::onMoneyNotEnough)
                .onMessage(ZuKaffeeAbholung.class, this::onCoffeeEnough)
                .onMessage(ZuKaffeeAbholung.class, this::onCoffeeNotEnough)
                .onMessage(ZuKaffeeAbholung.class, this::onZuKaffeeAbholung)
                .build();
    }


    private Behavior<lb> onZuKaffeeAbholung(ZuKaffeeAbholung request) {
        kaffeekasse.tell(new Kaffeekasse.Pay(this.getContext().getSelf()));
        return this;
    }


    private Behavior<lb> onMoneyEnough(lb command) {

        // Das Geld ist schon genug
        getContext().getLog().info("Has enough money!");

        // Dann fragen nach der Vorrat in der Kaffeemaschine
        kaffeemaschine.tell(new Kaffeemaschine.GetAmount(this.getContext().getSelf()));
        return this;
    }


    private Behavior<lb> onMoneyNotEnough(ZuKaffeeAbholung request) {
        getContext().getLog().info("money is not enough!");
        // Fall 3(weiter): Hier soll dann wieder zufällig zwischen aufladen und Kaffee holen entschieden werden, d.h. zurück zum ganz Beginn
        request.sender.tell(new Kaffeetrinkende.Success());
        return this;
    }


    private Behavior<lb> onCoffeeEnough(ZuKaffeeAbholung request) {

        // Bis jetzt sind wir schon sicher, dass genug Guthaben vorhanden ist, und es hat auch genug Kaffeevorrat in der Kaffeemaschine
        getContext().getLog().info("Has enough coffee!");

        // Und wählt dann die Kaffeemachine aus, die noch den höchsten Vorrat an Kaffee hat
        Pair<Integer, Integer> kaffeemaschine1 = new Pair<>(request.Nr, request.Vorrat);
        Pair<Integer, Integer> kaffeemaschine2 = new Pair<>(request.Nr, request.Vorrat);
        Pair<Integer, Integer> kaffeemaschine3 = new Pair<>(request.Nr, request.Vorrat);

        // Bekommt die Kaffeemaschine mit dem höchsten Vorrat
        Pair<Integer, Integer> maxKaffeemaschine = getMax(kaffeemaschine1, kaffeemaschine2, kaffeemaschine3);

        // Und gibt die höchsten Vorrat Nachricht an den*die Kaffeetrinkende*n zurück
        request.sender.tell(new Kaffeetrinkende.ChoiceCoffeeMachine(maxKaffeemaschine.first()));
        return this;
    }


    // Bekommt die Kaffeemaschine mit dem höchsten Vorrat und gibt die entsprechende Paar zurück
    private static Pair<Integer, Integer> getMax(Pair<Integer, Integer> a, Pair<Integer, Integer> b, Pair<Integer, Integer> c) {
        if (a.second() > b.second()) {
            if (a.second() > c.second()) {
                return a;
            } else {
                return c;

            }
        } else if (b.second() > c.second()) {
            return b;
        } else {
            return c;
        }
    }


    private Behavior<lb> onCoffeeNotEnough(ZuKaffeeAbholung request) {

        // Fall 4: Die Kaffeemaschine soll eine Kaffee liefern, aber die keinen Kaffee mehr hat, soll ebenfalls eine Fehlernachricht an den*die Kaffeetrinkende*n gesendet werden.
        request.sender.tell(new Kaffeetrinkende.Fail());
        return this;
    }

}
