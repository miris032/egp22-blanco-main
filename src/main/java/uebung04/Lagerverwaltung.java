package uebung04;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Lagerverwaltung extends AbstractBehavior<Lagerverwaltung.Request> {

    private int lagerbestand;
    public interface Request {}
    public static final class Put implements Request{
        public ActorRef<Lagerist.Response> sender;
        public Put(ActorRef<Lagerist.Response> sender) {
            this.sender = sender;
        }
    }
    public static final class Get implements Request{
        public final ActorRef<Lagerist.Response> sender;
        public Get(ActorRef<Lagerist.Response> sender) {
          this.sender = sender;
        }
    }

    public static Behavior<Request> create(int lagerbestand) {
        return Behaviors.setup(context -> new Lagerverwaltung(context, lagerbestand));
    }


    //Constructor
    private Lagerverwaltung(ActorContext<Request> context, int lagerbestand) {
        super(context);
        this.lagerbestand = lagerbestand;
    }


    @Override
    public Receive<Request> createReceive() {
        return newReceiveBuilder()
                .onMessage(Put.class, this::onPut)
                .onMessage(Get.class, this::onGet)
                .build();
    }


    private Behavior<Request> onPut(Put request) {
        getContext().getLog().info("Got a put request from {} ({})!", request.sender.path(), lagerbestand);
        this.lagerbestand += 1;
        request.sender.tell(new Lagerist.Success());
        return this;
    }


    private Behavior<Request> onGet(Get request) {
        getContext().getLog().info("Got a get request from {} ({})!", request.sender.path(), lagerbestand);
        if (this.lagerbestand > 0) {
            this.lagerbestand -= 1;
            request.sender.tell(new Lagerist.Success());
        } else {
            request.sender.tell(new Lagerist.Fail());
        }
        return this;
    }
}
