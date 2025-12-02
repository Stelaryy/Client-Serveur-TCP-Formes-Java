// Auteur : Ahmed Boukra Bettayeb
// class Cercle
// date : 2025-11-25

public class cercle extends ellipse {
    private static int compteurCercle = 0;
    private double rayon;

    public cercle(double rayon) {
        super(rayon, rayon);
        this.rayon = rayon;
        compteurCercle++;
        Forme.ajouterAListe(this);
        System.out.println("Creation d'un Cercle. Compteur Cercle : " + compteurCercle + ", total formes : " + Forme.getCompteurFormes());
    }

    public cercle() { this(1.0); }

    public static void decrementerCompteur() { if (compteurCercle > 0) compteurCercle--; }

    @Override
    public double getSurface() {
        return Math.PI * rayon * rayon;
    }

    @Override
    public double getPerimetre() {
        return 2 * Math.PI * rayon;
    }

    @Override
    public void detruire() {
        decrementerCompteur();
        detruireFormeGlobale();
        System.out.println("Suppression d'un Cercle. Restants Cercle : " + compteurCercle + ", total formes : " + Forme.getCompteurFormes());
    }
}
