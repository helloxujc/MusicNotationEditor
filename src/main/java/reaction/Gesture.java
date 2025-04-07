package reaction;

import graphics.G;
import java.util.ArrayList;
import musics.I;

public class Gesture {

    public static String recognized = "NULL";


    private static List UNDO = new List();

    //not legal to call a class that's either abstract or interface?
    //a single object, called anonymous class

    public static I.Area AREA = new I.Area() {
        public boolean hit(int x, int y) {return true;} //hit always return true
        public void dn(int x, int y) {Ink.BUFFER.dn(x,y);}
        public void drag(int x, int y) {Ink.BUFFER.drag(x,y);}
        public void up(int x, int y) {
            Ink.BUFFER.up(x,y);
            Ink ink = new Ink();
            Gesture gest = Gesture.getNew(ink);
            Ink.BUFFER.clear();
            recognized = gest == null? "NULL":gest.shape.name;
            if(gest!=null){
                if (gest.shape.name.equals("N-N")){undo();}else {gest.doGesture();}
            }
        }
    };

    public Shape shape;
    public G.VS vs;

    private Gesture(Shape shape, G.VS vs){
        this.shape = shape;
        this.vs = vs;
    }

    //difference between above and below, gesture cannot return null.
    //getNew does not have to succeed

    private void reDoGesture(){
        Reaction r = Reaction.best(this);
        if(r != null){r.act(this);}
    }

    private void doGesture(){
        Reaction r = Reaction.best(this);
        if(r != null){UNDO.add(this);r.act(this);}else{recognized+=" NO BIDS";}
    }

    public static void undo(){
        if(UNDO.size() > 0){
            UNDO.remove(UNDO.size()-1);
            Layer.nuke(); //eliminates all masses
            Reaction.nuke(); //clears out byshape and reloads initial reactions
            UNDO.redo();
        }
    }

    public static Gesture getNew(Ink ink){
        Shape s = Shape.recognize(ink);
        return s == null? null: new Gesture(s, ink.vs);
    }

    //-----------------List-----------------
    public static class List extends ArrayList<Gesture>{
        private void redo(){for(Gesture gest: this){gest.reDoGesture();}}

    }

}
