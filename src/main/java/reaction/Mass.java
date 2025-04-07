package reaction;

import java.awt.Graphics;
import musics.I;

public abstract class Mass extends Reaction.List implements I.Show {
    public Layer layer;

    public Mass(String layerName){
        layer = Layer.byName.get(layerName);
        if(layer!=null){
            layer.add(this);
        }else{
            System.out.println("Bad Layer name" + layerName);
        }
    }

    public void deleteMass(){
        clearAll();
        layer.remove(this);
    }
    public void show(Graphics g){}

    //fix a bug that shows up, removing masses as musics.I.Shows from layers
    private static int M = 1; //first M musics.I create is one, every M created have a different number
    private int hash = M++;

    @Override
    public int hashCode(){return hash;}
    @Override
    public boolean equals(Object o){return this == o;}

}
