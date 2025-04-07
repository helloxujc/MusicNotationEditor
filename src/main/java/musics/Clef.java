package musics;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Clef extends Mass implements Comparable<Clef> {
    public Glyph glyph;
    public int x;
    public Staff staff;
    public Clef (Staff staff, int x, Glyph glyph){
        super("NOTE");
        this.staff = staff;
        this.x = x;
        this.glyph = glyph;

        addReaction(new Reaction("S-N") { //delete
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL();
                int aX = Clef.this.x + staff.fmt.H*2, aY = staff.yOfLine(4);
                int dX = Math.abs(x-aX), dY = Math.abs(y-aY), dist = dX + dY;
                return dist > 50? UC.noBid:dist;
            }
            public void act(Gesture g) {
                deleteClef();
            }
        });
    }

    private void deleteClef() {
        staff.clefs.remove(this);
        if (staff.clefs.size() == 0){
            staff.clefs  = null;
        }else {
            Collections.sort(staff.clefs);
        }
        deleteMass();
    }


    public void show(Graphics g){glyph.showAt(g,staff.fmt.H,x,staff.yOfLine(4));}

    @Override
    public int compareTo(Clef c) {
        return x-c.x;
    }


    //----------------List----------------
    public static class List extends ArrayList<Clef>{

    }
}
