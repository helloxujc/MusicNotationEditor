package musics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Head extends Mass implements Comparable<Head>{
    public Staff staff;
    public int line;
    public Time time;
    public Glyph forcedGlyph = null;
    public Stem stem = null;
    public boolean wrongSide;
    public Accid accid = null;


    public Head(Staff staff, int x, int y){
        super("NOTE");
        this.staff = staff;
        time = staff.sys.getTime(x);
        line = staff.lineOfY(y);
        time.heads.add(this);
        addReaction(new Reaction("S-S") {
            public int bid(Gesture g) { //stem or unStem heads
                int x=g.vs.xM(), y1= g.vs.yL(), y2= g.vs.yH();
                int W= Head.this.w(), hY = Head.this.y();
                if(y1>y || y2<y){return UC.noBid;}
                int hL = Head.this.time.x, hR = hL+W;
                if (x<hL-W || x>hR+W){return UC.noBid;}
                if (x<hL+W/2){return hL-x;}
                if(x>hR-W/2){return x-hR;}
                return UC.noBid;
            }

            @Override
            public void act(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2= g.vs.yH();
                Staff staff = Head.this.staff;
                Time t = Head.this.time;
                int W = Head.this.w();
                boolean up = x>t.x+W/2;
                if(Head.this.stem == null) {
                    //t.stemHeads(staff, up, y1, y2);
                    Stem.getStem(staff,t,y1,y2,up);
                }else {
                    t.unStemHeads(y1, y2);
                }
            }
        });

        addReaction(new Reaction("DOT") {
            @Override
            public int bid(Gesture g) {
                if(Head.this.stem == null){return UC.noBid;}
                int xH = x(), yH = y(), H = staff.fmt.H, W=w();
                int x = g.vs.xM(), y = g.vs.yM();
                if(x<xH || x>xH+2*W || y<yH-H || y>yH+H){return UC.noBid;}
                return Math.abs(xH+W-x)+Math.abs(yH-y);
            }

            @Override
            public void act(Gesture g) {
                Head.this.stem.cycleDot();;
            }
        });

        addReaction(new Reaction("NE-SE") { // up arrow raises sharp
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL();
                int hX = Head.this.x() + Head.this.w()/2, hY = Head.this.y();
                int dX = Math.abs(x-hX), dY = Math.abs(y-hY), dist = dX + dY;
                return dist > 50? UC.noBid: dist;
            }

            public void act(Gesture g) {
                Head.this.accidUp();

            }
        });

        addReaction(new Reaction("SE-NE") { // down arrow lowers sharp
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL();
                int hX = Head.this.x() + Head.this.w()/2, hY = Head.this.y();
                int dX = Math.abs(x-hX), dY = Math.abs(y-hY), dist = dX + dY;
                return dist > 50? UC.noBid: dist;
            }

            public void act(Gesture g) {
                Head.this.accidDown();
            }
        });

        addReaction(new Reaction("S-N") { //delete
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL();
                int aX = Head.this.x() + Head.this.w()/2, aY = Head.this.y();
                int dX = Math.abs(x-aX), dY = Math.abs(y-aY), dist = dX +dY;
                return dist > 50? UC.noBid:dist;
            }

            public void act(Gesture g) {
                Head.this.deleteHead();
            }
        });
    }

    private void deleteHead() {
        if(accid != null){accid.deleteAccid();}
        unStem();
        time.heads.remove(this);
        deleteMass();
    }

    private void accidUp() {
        if (accid == null){accid = new Accid(this, Accid.SHARP);return;}
        if (accid.iGlyph < 4){accid.iGlyph++;}
    }

    private void accidDown() {
        if (accid == null){accid = new Accid(this, Accid.FLAT);return;}
        if (accid.iGlyph > 0){accid.iGlyph--;}
    }

    public int w(){return 24*staff.fmt.H/10;} // width of the note head
    public int y(){return staff.yOfLine(line);}
    public int x(){
        int res = time.x;
        if(wrongSide) {res += (stem != null && stem.isUp)? w(): -w();}
        return res;
    }
    public Glyph normalGlyph(){
        if (stem == null) {return Glyph.HEAD_Q;}
        if (stem.nFlag == -1) {return Glyph.HEAD_HALF;}
        if (stem.nFlag == -2) {return Glyph.HEAD_W;}
        return Glyph.HEAD_Q;
    }
    public void delete(){time.heads.remove(this);} //STUB

    public void show(Graphics g){
        g.setColor(stem == null?Color.BLUE:Color.BLACK);
        int H = staff.fmt.H;
        (forcedGlyph != null? forcedGlyph: normalGlyph()).showAt(g,H,x(),y());
        if(stem != null){
            int off = UC.AugDotOffset, sp = UC.AugDotSpacing;
            for (int i =0; i<stem.nDot; i++){
                g.fillOval(x()+off+i*sp, y()-3*H/2, H*2/3, H*2/3);
            }
        }
        g.setColor(Color.BLACK);
    }

    public void unStem(){
        if (stem!= null){
            stem.heads.remove(this);
            if(stem.heads.size()==0){stem.deleteStem();}
            stem = null;
            wrongSide =false;
        }
    }

    public void jointStem(musics.Stem s) {
        if (stem!= null){unStem();}
        s.heads.add(this);
        stem = s;
    }

    @Override
    public int compareTo(Head h) {
        return (staff.iStaff != h.staff.iStaff ? staff.iStaff - h.staff.iStaff : line - h.line);
    }

    //---------------------List--------------------
    public static class List extends ArrayList<Head>{}
}
