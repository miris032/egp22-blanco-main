package com;
import akka.actor.Actor;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;


public class KaffeeMain extends AbstractBehavior<KaffeeMain.StartMessage> {


    public static class StartMessage {}
    ActorRef<SomeActor.SomeMessage> someActor;
    public static Behavior<StartMessage> create() {
        return Behaviors.setup(KaffeeMain::new);
    }


    private KaffeeMain(ActorContext<StartMessage> context) {
        super(context);
        someActor = context.spawn(SomeActor.create(37), "someActor");
    }


    @Override
    public Receive<StartMessage> createReceive() {
        return newReceiveBuilder().onMessage(StartMessage.class, this::onStartMessage).build();
    }


    private Behavior<StartMessage> onStartMessage(StartMessage command) {

        ActorRef<Kaffeemaschine.km> kaffeemaschine1 = getContext().spawn(Kaffeemaschine.create(10), "kaffeemaschine1");
        ActorRef<Kaffeemaschine.km> kaffeemaschine2 = getContext().spawn(Kaffeemaschine.create(10), "kaffeemaschine2");
        ActorRef<Kaffeemaschine.km> kaffeemaschine3 = getContext().spawn(Kaffeemaschine.create(10), "kaffeemaschine3");

        ActorRef<Kaffeekasse.kk> kaffeekasse1 = getContext().spawn(Kaffeekasse.create(0), "kaffeekasse1");
        ActorRef<Kaffeekasse.kk> kaffeekasse2 = getContext().spawn(Kaffeekasse.create(0), "kaffeekasse2");
        ActorRef<Kaffeekasse.kk> kaffeekasse3 = getContext().spawn(Kaffeekasse.create(0), "kaffeekasse3");
        ActorRef<Kaffeekasse.kk> kaffeekasse4 = getContext().spawn(Kaffeekasse.create(0), "kaffeekasse4");

        // TODO
        ActorRef<Kaffeetrinkende.kt> kaffeetrinkender1 = getContext().spawn(Kaffeetrinkende.create( ), "kaffeetrinkender1");
        ActorRef<Kaffeetrinkende.kt> kaffeetrinkender2 = getContext().spawn(Kaffeetrinkende.create( ), "kaffeetrinkender2");
        ActorRef<Kaffeetrinkende.kt> kaffeetrinkender3 = getContext().spawn(Kaffeetrinkende.create( ), "kaffeetrinkender3");
        ActorRef<Kaffeetrinkende.kt> kaffeetrinkender4 = getContext().spawn(Kaffeetrinkende.create( ), "kaffeetrinkender4");

        ActorRef<Loadbalancer.lb> loadbalancer1 = getContext().spawn(Loadbalancer.create(kaffeekasse1, kaffeemaschine1, kaffeetrinkender1), "loadbalancer1");
        ActorRef<Loadbalancer.lb> loadbalancer2 = getContext().spawn(Loadbalancer.create(kaffeekasse2, kaffeemaschine2, kaffeetrinkender2), "loadbalancer2");
        ActorRef<Loadbalancer.lb> loadbalancer3 = getContext().spawn(Loadbalancer.create(kaffeekasse3, kaffeemaschine3, kaffeetrinkender3), "loadbalancer3");



        return this;
    }
}
