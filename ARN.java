import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;



/**
 * ARN<E> :
 * Implantation de l'interface Collection basée sur les arbres rouge noir .
 * Les éléments sont ordonnés soit en utilisant l'ordre naturel (Comparable) 
 * soit avec un Comparator fourni à la création.
 * @param <E> le type des clés stockées dans l'arbre
 * 
 */




public class ARN<E> extends AbstractCollection<E> {
    private Noeud racine;
    private int taille;
    private Comparator<? super E> cmp;

  private final Noeud sentinelle = new Noeud(null, Couleur.Noir);

    public enum Couleur {
        Rouge,
        Noir
    }

    private class Noeud {
        E cle;
        Noeud gauche;
        Noeud droit;
        Noeud pere;
        Couleur couleur;

        /**
         * un noeud est caractérisé de plus que sa clé et ses deux sous noeud gauche et droit d'une couleur (Rouge ou Noir) 
         * */
        Noeud(E cle, Couleur couleur) {
            this.cle = cle;
            this.gauche = sentinelle;
            this.droit = sentinelle;
            this.pere = sentinelle;
            this.couleur = couleur;
        }
        
    	/**
		 * Renvoie le successeur de ce noeud
		 * 
		 * @return le noeud contenant la clé qui suit la clé de ce noeud dans
		 *         l'ordre des clés, null si c'es le noeud contenant la plus
		 *         grande clé
		 */
        
        Noeud suivant() {
            if (droit != sentinelle) {
                return droit.minimum();
            }
            Noeud y = pere;
            Noeud x = this;
            while (y != sentinelle&& x == y.droit) {
                x = y;
                y = y.pere;
            }
            return y;
        }
        
        /**
		 * Renvoie le noeud contenant la clé minimale du sous-arbre enraciné
		 * dans ce noeud
		 * 
		 * @return le noeud contenant la clé minimale du sous-arbre enraciné
		 *         dans ce noeud
		 */
        
        Noeud minimum() {
            Noeud x = this;
            while (x.gauche != sentinelle) {
                x = x.gauche;
            }
            return x;
        }

        boolean isSentinelle() {
            return this == sentinelle;
        }

        
    }
    /**
	 * Crée un arbre vide. Les éléments sont ordonnés selon l'ordre naturel
	 */
    public ARN() {
        racine = sentinelle;
        taille = 0;
        cmp = cmp = (a, b) -> ((Comparable<E>) a).compareTo(b);
    }

	/**
	 * Crée un arbre vide. Les éléments sont comparés selon l'ordre imposé par
	 * le comparateur
	 * 
	 * @param cmp
	 *            le comparateur utilisé pour définir l'ordre des éléments
	 */
    public ARN(Comparator<? super E> cmp) {
        racine = sentinelle;
        taille = 0;
        this.cmp = cmp;
    }
    
    /**
	 * Constructeur par recopie. Crée un arbre qui contient les mêmes éléments
	 * que c. L'ordre des éléments est l'ordre naturel.
	 * 
	 * @param c la collection à copier
	 */
    public ARN(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    
    @Override
    public boolean add(E e) {
        if (e == sentinelle.cle) {
            throw new IllegalArgumentException("L'élément ne peut pas être null");
        }

        Noeud z = new Noeud(e, Couleur.Rouge); // On initialise le nouveau nœud en rouge

            inserer(z);
            racine.couleur = Couleur.Noir;
            return true;
      
    }

    private void inserer(Noeud z) {
        Noeud y = sentinelle;
        Noeud x = racine;
        while ( !x.isSentinelle()) {
            y = x;
            x = cmp.compare(z.cle, x.cle) < 0 ? x.gauche : x.droit;
        }
        z.pere = y;
        if (y == sentinelle) {
            racine = z;
        } else if (cmp.compare(z.cle, y.cle) < 0) {
            y.gauche = z;
        } else if (cmp.compare(z.cle, y.cle) > 0) {
            y.droit = z;
        }
        z.gauche = sentinelle;
        z.droit = sentinelle;
        taille++;
        Correction(z);
    }
/**
 * après une opération d'insertion quelques proprietés d'ARN sont violés 
 * On effectue des corrections de couleur et des rotations pour les réctifier.
 * 
 * */
    void Correction(Noeud z) {
        while (z.pere.couleur == Couleur.Rouge) {
            if (z.pere == z.pere.pere.gauche) {
                Noeud y = z.pere.pere.droit; // l'oncle de z
                if (y.couleur == Couleur.Rouge) {
                    
                    z.pere.couleur = Couleur.Noir;
                    y.couleur = Couleur.Noir;
                    z.pere.pere.couleur = Couleur.Rouge;
                    z = z.pere.pere;
                } else {
                    if (z == z.pere.droit) {
                       
                        z = z.pere;
                        rotationGauche(z);
                    }
                    
                    z.pere.couleur = Couleur.Noir;
                    z.pere.pere.couleur = Couleur.Rouge;
                    rotationDroite(z.pere.pere);
                }
            } else {
                Noeud y = z.pere.pere.gauche; // l'oncle de z
                if (y.couleur == Couleur.Rouge) {
                    
                    z.pere.couleur = Couleur.Noir;
                    y.couleur = Couleur.Noir;
                    z.pere.pere.couleur = Couleur.Rouge;
                    z = z.pere.pere;
                } else {
                    if (z == z.pere.gauche) {
                       
                        z = z.pere;
                        rotationDroite(z);
                    }
              
                    z.pere.couleur = Couleur.Noir;
                    z.pere.pere.couleur = Couleur.Rouge;
                    rotationGauche(z.pere.pere);
                }
            }
        }
        racine.couleur = Couleur.Noir;
    }

  
   private void rotationGauche(Noeud x) {
	    Noeud y = x.droit;
	    x.droit = y.gauche;

	    if ( !y.gauche.isSentinelle()) {
	        y.gauche.pere = x;
	    }

	    y.pere = x.pere;

	    if (x.pere.isSentinelle()) {
	        racine = y;
	    } else if (x == x.pere.gauche) {
	        x.pere.gauche = y;
	    } else {
	        x.pere.droit = y;
	    }

	    y.gauche = x;
	    x.pere = y;
	}


    private void rotationDroite(Noeud x) {
        Noeud y = x.gauche;
        x.gauche = y.droit;

        if ( !y.droit.isSentinelle()) {
            y.droit.pere = x;
        }

        y.pere = x.pere;

        if (x.pere.isSentinelle()) {
            racine = y;
        } else if (x == x.pere.droit) {
            x.pere.droit = y;
        } else {
            x.pere.gauche = y;
        }

        y.droit = x;
        x.pere = y;
    }

    /**
     * 
     * Ajout des élements d'une collection dans l'arbre
     * 
     * */

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modif = false;
        for (E e : c) {
            if (add(e)) {
                modif = true;
            }
        }
        return modif;
    }

    @Override
    public Iterator<E> iterator() {
        return new ARNIterator();
    }

    @Override
    public int size() {
        return this.taille;
    }

    private class ARNIterator implements Iterator<E> {
        private Noeud precedent;
        private Noeud suivant;

        public ARNIterator() {
            precedent = sentinelle;
            suivant = racine.minimum();
        }

        public boolean hasNext() {
            return suivant != sentinelle;
        }

        public E next() throws IllegalStateException {
            if (!hasNext()) {
                throw new IllegalStateException();
            }
            precedent = suivant;
            suivant = suivant.suivant();
            return precedent.cle;
        }

        public void remove() throws IllegalStateException {
            if (precedent == sentinelle) {
                throw new IllegalStateException();
            }
            supprimer(precedent);
            precedent = sentinelle;
        }
    }


    private Noeud supprimer(Noeud z) {
  	  Noeud y ,x;
  	  Noeud w = z.suivant();

  	  if (z.gauche == sentinelle || z.droit == sentinelle)
  		    y = z;
  	  else
  	    y = w;
  	  // y est le nœud à détacher
  	
  	  if (y.gauche != sentinelle)
  	    x = y.gauche;
  	  else
  	    x = y.droit;
  	  // x est le fils unique de y ou null si y n'a pas de fils
  	
  	  x.pere = y.pere;
  	
  	  if (y.pere == sentinelle) // suppression de la racine
  	  { 
  	    racine = x;
  	  } else 
  	  {
  	    if (y == y.pere.gauche)
  	      y.pere.gauche = x;
  	    else
  	      y.pere.droit = x;
  	  }
  	
  	  if (y != z) 
  	  {
  		  z.cle = y.cle;
  		  w = z;
  	  }
  	  if (y.couleur == Couleur.Noir) 
  	    supprimerCorrection(x);

  		taille--;
  		return w;
  	}
  	
  	private void supprimerCorrection(Noeud x) {
  	  Noeud w;
      while (x != racine && x.couleur == Couleur.Noir) 
      {
        if (x == x.pere.gauche) {
          w = x.pere.droit; // le frère de x
        if (w.couleur == Couleur.Rouge) 
        {
       
          w.couleur = Couleur.Noir;
          x.pere.couleur = Couleur.Rouge;
          rotationGauche(x.pere);
          w = x.pere.droit;
        }
        if (w.gauche.couleur == Couleur.Noir && w.droit.couleur == Couleur.Noir) 
        {
         
          w.couleur = Couleur.Rouge;
            x = x.pere;
        } else 
        {
          if (w.droit.couleur == Couleur.Noir) 
          {
           
            w.gauche.couleur =Couleur.Noir;
            w.couleur = Couleur.Rouge;
              rotationDroite(w);
              w = x.pere.droit;
          } // cas 4
          w.couleur = x.pere.couleur;
          x.pere.couleur = Couleur.Noir;
          w.droit.couleur = Couleur.Noir;
          rotationGauche(x.pere);
          x = racine;
        }
        } else 
        {
          if (x == x.pere.droit) 
          {
            w = x.pere.gauche; // le frère de x
            if (w.couleur == Couleur.Rouge) 
            {
             
              w.couleur = Couleur.Noir;
              x.pere.couleur = Couleur.Rouge;
              rotationGauche(x.pere);
              w = x.pere.gauche;
            }
            if (w.droit.couleur == Couleur.Noir && w.gauche.couleur == Couleur.Noir) 
            {
           
              w.couleur = Couleur.Rouge;
              x = x.pere;
            } else {
              if (w.gauche.couleur == Couleur.Noir) 
              {
                
                w.droit.couleur = Couleur.Noir;
                w.couleur = Couleur.Rouge;
                rotationGauche(w);
                w = x.pere.gauche;
              }
              
              w.couleur = x.pere.couleur;
              x.pere.couleur =Couleur.Noir;
              w.gauche.couleur = Couleur.Noir;
              rotationDroite(x.pere);
              x = racine;
            }
          }
        }
      }
  	  
  	  x.couleur = Couleur.Noir;
  	}
  	
    
	/**
	 * 
	 * Supprime tous les éléments de cette arbre
	 * L'arbre sera vide après l'execution de cette méthode.
	 * 
	 * */
    @Override
    public void clear() {
        Iterator<E> iterator = this.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

	/**    
    *cette méthode supprime tous les élement commun entre l'arbre et la collection passé en paramétre 
    *
    */

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modif = false;
        for (Object e : c) {
            if (remove(e)) {
                modif = true;
            }
        }
        return modif;
    }

/**
 * Recherche une clé. Cette méthode peut être utilisée par
 * {@link #contains(Object)} et {@link #remove(Object)}
 * 
 * @param o la clé à chercher
 * @return le noeud qui contient la clé ou null si la clé n'est pas trouvée.
 */
private Noeud rechercher(Object o) {
	// TODO
    Noeud x = racine;
    while (x != sentinelle && cmp.compare((E) o, x.cle) != 0) {
        x = cmp.compare((E) o, x.cle) < 0 ? x.gauche : x.droit;
    }
    return x;
}

@Override
public boolean contains(Object o) {
    return rechercher(o) != sentinelle;
}
@Override
public boolean containsAll(Collection<?> c) {
	boolean retour=true;
    for (Object e : c) {
        if (!this.contains(e)) {
          retour=false;  
        }
    }
    return retour;  
}
@Override
public boolean isEmpty() {
    return racine == sentinelle;
}

@Override
public String toString() {
    StringBuilder buf = new StringBuilder();
    toString(racine, buf, "", maxStrLen(racine));
    return buf.toString();
}

private void toString(Noeud x, StringBuilder buf, String path, int len) {
    final String RESET = "\u001B[0m";
    final String RED = "\u001B[31m";

    if (x == null) {
        return;  // Ajout de vérification pour éviter une NullPointerException
    }

  toString(x.droit, buf, path + "D", len);

    for (int i = 0; i < path.length(); i++) {
        for (int j = 0; j < len + 6; j++)
            buf.append(' ');

        char c = ' ';
        if (i == path.length() - 1)
            c = '+';
        else if (path.charAt(i) != path.charAt(i + 1))
            c = '|';

        buf.append(c);
    }

    if (x.isSentinelle()) {
        buf.append("-- " + "☒");
    } else {
        if (x.couleur == Couleur.Rouge) {
            buf.append("-- " + RED + x.cle.toString() + RESET);
        } else {
            buf.append("-- " + x.cle.toString());
        }
    }

    if (x.gauche != null || x.droit != null) {
        buf.append(" --");
        for (int j = x.isSentinelle() || (x.droit != null && x.droit.isSentinelle()) ? 1 : x.cle.toString().length(); j < len; j++)
            buf.append('-');
        buf.append('|');
    }

    buf.append("\n");
    toString(x.gauche, buf, path + "G", len);
}



private int maxStrLen(Noeud x) {
    if (x == null || x.cle == null) {
        return 0;
    }
    return Math.max(x.cle.toString().length(), Math.max(maxStrLen(x.gauche), maxStrLen(x.droit)));
}
}

